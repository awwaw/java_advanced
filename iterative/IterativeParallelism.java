package info.kgeorgiy.ja.podkorytov.iterative;

import info.kgeorgiy.java.advanced.iterative.AdvancedIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements AdvancedIP {

    private final ParallelMapper mapper;

    /**
     * Creates instance of this class with no given mapper.
     * In this case, all operations will be processed using iterative parallelism
     */
    public IterativeParallelism() {
        this.mapper = null;
    }

    /**
     * Creates instance of this class with given mapper.
     * In this case, all operations will be processed using specified parallel mapper
     * @param mapper which will apply given functions using synchronized queue
     */
    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    private <T> List<T> makeStep(List<T> values, int step) {
        int limit = values.size() / step + Math.min(values.size() % step, 1);
        if (step == 1) {
            return values;
        }
        return Stream.iterate(0, idx -> idx + step)
                .limit(limit)
                .map(values::get)
                .collect(Collectors.toList());
    }

    private <T> List<List<T>> split(int threads, List<T> values) {
        List<List<T>> chunks = new ArrayList<>();
        int i = 0;
        int batchSize = values.size() / threads;
        int remaining = values.size() - values.size() % threads;
        while (i < values.size()) {
            int inc = Math.min(values.size() - i, batchSize + (remaining-- > 0 ? 1 : 0));
            chunks.add(values.subList(i, i + inc));
            i += inc;
        }
        return chunks;
    }

    private <T, R> List<R> getThreadsResults(int threads,
                                             List<T> values,
                                             Function<Stream<T>, R> f) {
        if (threads < 1) {
            throw new IllegalArgumentException("There must be at least 1 thread");
        }
        Objects.requireNonNull(values);

        threads = Math.max(1, Math.min(values.size(), threads));
        List<List<T>> batches = split(threads, values);

        List<R> threadResults = new ArrayList<>(Collections.nCopies(batches.size(), null));
        List<Thread> startedThreads = getThreads(f, batches, threadResults);

        startedThreads.forEach(Thread::start);
        closeThreads(startedThreads);

        return threadResults;
    }

    public static void closeThreads(List<Thread> startedThreads) {
        try {
            for (final Thread thread : startedThreads) {
                thread.join();
            }
        } catch (final InterruptedException e) {
            startedThreads.forEach(Thread::interrupt);
            startedThreads.forEach(
                    thread -> {
                        while (true) {
                            try {
                                thread.join();
                                break;
                            } catch (InterruptedException ignored) {
                                // ignored
                            }
                        }
                    }
            );
        }
    }

    private static <T, R> List<Thread> getThreads(Function<Stream<T>, R> f, List<List<T>> batches,
                                                  List<R> threadResults) {
        List<Thread> startedThreads = new ArrayList<>(Collections.nCopies(batches.size(), null));

        for (int i = 0; i < batches.size(); i++) {
            List<T> batch = batches.get(i);
            int finalI = i;
            Thread thread = new Thread(
                    () -> threadResults.set(
                            finalI,
                            f.apply(batch.stream())
                    )
            );
//            thread.start();
            startedThreads.set(i, thread);
        }
        return startedThreads;
    }

    private <T, R> R process(int threads,
                             List<T> values,
                             Function<Stream<T>, R> f,
                             Function<Stream<R>, R> collector) throws InterruptedException {
        if (threads > values.size()) {
            threads = values.size();
        }
        List<R> result;
        if (mapper == null) {
            result = getThreadsResults(threads, values, f);
        } else {
            List<Stream<T>> batches = split(threads, values).stream().map(List::stream).toList();
            result = mapper.map(f, batches);
        }
        return collector.apply(result.stream());
    }

    @Override
    public String join(int threads, List<?> values, int step) throws InterruptedException {
        return join(threads, makeStep(values, step));
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate,
                              int step) throws InterruptedException {
        return filter(threads, makeStep(values, step), predicate);
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f,
                              int step) throws InterruptedException {
        return map(threads, makeStep(values, step), f);
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.map(Object::toString).collect(Collectors.joining()),
                stream -> stream.map(Object::toString).collect(Collectors.joining())
        );
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values,
                              Predicate<? super T> predicate) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.filter(predicate).collect(Collectors.toList()),
                stream -> stream.flatMap(Collection::stream).collect(Collectors.toList())
        );
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values,
                              Function<? super T, ? extends U> f) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.map(f).collect(Collectors.toList()),
                stream -> stream.flatMap(Collection::stream).collect(Collectors.toList())
        );
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator,
                         int step) throws InterruptedException {
        return maximum(threads, makeStep(values, step), comparator);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator,
                         int step) throws InterruptedException {
        return minimum(threads, makeStep(values, step), comparator);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate,
                           int step) throws InterruptedException {
        // :NOTE: !any(predicate.negate())
        return all(threads, makeStep(values, step), predicate);
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate,
                           int step) throws InterruptedException {
        return any(threads, makeStep(values, step), predicate);
    }

    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate,
                         int step) throws InterruptedException {
        return count(threads, makeStep(values, step), predicate);
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.max(comparator).orElseThrow(),
                stream -> stream.max(comparator).orElseThrow()
        );
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values,
                         Comparator<? super T> comparator) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.min(comparator).orElseThrow(),
                stream -> stream.min(comparator).orElseThrow()
        );
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.allMatch(predicate),
                stream -> stream.reduce((a, b) -> a && b).orElse(true)
        );
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values,
                           Predicate<? super T> predicate) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.reduce((a, b) -> a || b).orElse(true)
        );
    }

    @Override
    public <T> int count(int threads, List<? extends T> values,
                         Predicate<? super T> predicate) throws InterruptedException {
        return process(
                threads,
                values,
                stream -> stream.filter(predicate).toList().size(),
                stream -> stream.reduce(0, Integer::sum)
        );
    }

    @Override
    public <T> T reduce(int threads, List<T> values, T identity, BinaryOperator<T> operator,
                        int step) throws InterruptedException {
        return process(
                threads,
                makeStep(values, step),
                stream -> stream.reduce(identity, operator),
                stream -> stream.reduce(identity, operator)
        );
    }

    @Override
    public <T, R> R mapReduce(int threads, List<T> values, Function<T, R> lift, R identity, BinaryOperator<R> operator,
                              int step) throws InterruptedException {
        return process(
                threads,
                makeStep(values, step),
                stream -> stream.map(lift)
                        .reduce(identity, operator),
                stream -> stream.reduce(identity, operator)
        );
    }
}
