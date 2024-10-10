package info.kgeorgiy.ja.podkorytov.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threads;
    private final TaskQueue tasksQueue;

    private boolean closed;

    public ParallelMapperImpl(int threadsCount) throws InterruptedException {
        if (threadsCount < 1) {
            throw new InterruptedException("There must be at least 1 thread to be used");
        }
        threads = new ArrayList<>();
        tasksQueue = new TaskQueue(new ArrayDeque<>());
        Runnable task1 = () -> {
            try {
                while (!Thread.interrupted()) {
                    Runnable task = tasksQueue.poll();
                    task.run();
                }
            } catch (InterruptedException ignored) {
            } finally {
                Thread.currentThread().interrupt();
            }
        };
        for (int i = 0; i < threadsCount; i++) {
            threads.add(new Thread(task1));
            threads.getLast().start();
        }
        closed = false;
    }

    private static class ExceptionChecker {
        private volatile RuntimeException exception;

        public ExceptionChecker() {
            exception = null;
        }

        public RuntimeException getFailedStatus() {
            return exception;
        }

        public void setFailedStatus(RuntimeException exception) {
            if (this.exception == null) {
                this.exception = exception;
            } else {
                exception.addSuppressed(exception);
            }
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> items) throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("This mapper is already closed");
        }
        SynchronizedList<R> result = new SynchronizedList<>(items.size());
        ExceptionChecker failed = new ExceptionChecker();
        for (int i = 0; i < items.size(); i++) {
            final int idx = i;
            tasksQueue.add(
                    () -> {
                        try {
                            result.set(idx, f.apply(items.get(idx)));
                        } catch (RuntimeException e) {
                            failed.setFailedStatus(e);
                        }
                    }
            );
        }

        List<R> res = result.getValues();

        RuntimeException exc = failed.getFailedStatus();
        if (exc != null) {
            throw exc;
        }
        return res;
    }

    @Override
    public void close() {
        // :NOTE: no synchronization
        if (closed) {
            throw new IllegalStateException("This mapper is already closed");
        }
        threads.forEach(Thread::interrupt);
        boolean mustInterrupt = false;
        for (int i = 0; i < threads.size(); ) {
            Thread thread = threads.get(i);
            try {
                thread.join();
                i++;
            } catch (InterruptedException ie) {
                mustInterrupt = true;
            }
        }
        if (mustInterrupt) {
            Thread.currentThread().interrupt();
        }
    }
}
