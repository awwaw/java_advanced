package info.kgeorgiy.ja.podkorytov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WebCrawler implements AdvancedCrawler {

    private final Downloader downloader;
    private final int perHost;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;
    private final ConcurrentMap<String, HostsQueue> hosts;

    private final static String USAGE = "USAGE: WebCrawler url [depth [downloads [extractors [perHost]]]]";

    /**
     * Creates an instance of WebCrawler
     *
     * @param downloader - specific type of downloader used to download pages
     * @param downloaders - maximum amount of downloaders to use
     * @param extractors - maximum amount of extractors to use
     * @param perHost - maximum amount of pages to be downloaded on single host
     */
    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
        this.hosts = new ConcurrentHashMap<>();
    }

    private static class DownloadSetup {
        private final String url;
        private final int depth;
        private final Set<String> result;
        private final Set<String> was;
        private final ConcurrentMap<String, IOException> exceptions;
        private final Phaser phaser;
        private final Set<String> excludes;

        private final Predicate<String> hostFilter;

        public Set<String> getResult() {
            return result;
        }

        public ConcurrentMap<String, IOException> getExceptions() {
            return exceptions;
        }

        // :NOTE: Unused code
        public DownloadSetup(String url, int depth, final Set<String> excludes, Predicate<String> hostFilter) {
            this.url = url;
            this.depth = depth;
            result = ConcurrentHashMap.newKeySet();
            was = ConcurrentHashMap.newKeySet();
            exceptions = new ConcurrentHashMap<>();
            phaser = new Phaser(1);
            this.excludes = excludes;
            this.hostFilter = hostFilter;
        }
    }

    @Override
    public Result download(final String url, final int depth, final Set<String> excludes) {
        final DownloadSetup setup = new DownloadSetup(url, depth, excludes, host -> true);
        bfsDownload(setup);
        return new Result(new ArrayList<>(setup.getResult()), setup.getExceptions());
    }

    @Override
    public Result download(final String url, final int depth) {
        return download(url, depth, new HashSet<>());
    }

    @Override
    public Result advancedDownload(final String url, final int depth, final List<String> hosts) {
        final Set<String> hostSet = hosts.stream().collect(Collectors.toSet());
        final DownloadSetup setup = new DownloadSetup(url, depth, new HashSet<>(), hostSet::contains);
        bfsDownload(setup);
        return new Result(new ArrayList<>(setup.result), setup.exceptions);
    }

    private class HostsQueue {
        private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        // :NOTE: AtomicInteger?
        private final Semaphore semaphore = new Semaphore(perHost);
        private void addNewTask(final Runnable task) {
            if (semaphore.tryAcquire()) {
                downloaders.submit(task);
            } else {
                queue.add(task);
            }
        }

        private void submitQueuedTask() {
            if (queue.isEmpty()) {
                semaphore.release();
            } else {
                downloaders.submit(queue.remove());
            }
        }
    }

    private boolean checkUrl(final String url, final Set<String> excludes) {
        for (final String ex : excludes) {
            if (url.contains(ex)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkForAvailableHost(final String url, final Predicate<String> hostFilter) {
        try {
            return hostFilter.test(URLUtils.getHost(url));
        } catch (final MalformedURLException e) {
            return false;
        }
    }

    private Runnable getExtractorTask(
            final String link, final Document page, final Set<String> layer,
            final Map<String, IOException> exceptions, final Phaser sync) {
        return () -> {
            try {
                layer.addAll(page.extractLinks());
            } catch (final IOException e) {
                exceptions.put(link, e);
            } finally {
                sync.arriveAndDeregister();
            }
        };
    }

    private Runnable getDownloaderTask(final String link, final int finalDepth, final Phaser sync, final Set<String> layer,
                                       final Map<String, IOException> exceptions, final Set<String> was, final HostsQueue tasks) {
        return () -> {
            try {
                final Document page = downloader.download(link);
                if (finalDepth != 0) {
                    sync.register();
                    final Runnable extractorsTask = getExtractorTask(link, page, layer, exceptions, sync);
                    extractors.submit(extractorsTask);
                }
            } catch (final IOException e) {
                exceptions.put(link, e);
                was.remove(link);
            } finally {
                sync.arriveAndDeregister();
                tasks.submitQueuedTask();
            }
        };
    }

    private boolean checkValidLink(
            final String link,
            final Set<String> excludes,
            final Predicate<String> hostFilter,
            final Set<String> was
    ) {
        return checkUrl(link, excludes) && checkForAvailableHost(link, hostFilter) && was.add(link);
    }

    private void bfsDownload(DownloadSetup setup) {
        final Set<String> layer = new ConcurrentSkipListSet<>();
        layer.add(setup.url);
        int depth = setup.depth;
        while (depth-- > 0) {
            if (layer.isEmpty()) {
                break;
            }
            final Set<String> tmp = new ConcurrentSkipListSet<>(layer);
            layer.clear();

            final int finalDepth = depth;
            tmp.stream()
                    .filter(
                            link -> !setup.getExceptions().containsKey(link)
                                    && checkValidLink(link, setup.excludes, setup.hostFilter, setup.was)
                    ).forEach(
                            link -> {
                                try {
                                    final String host = URLUtils.getHost(link);
                                    final HostsQueue tasks = hosts.computeIfAbsent(host, h -> new HostsQueue());
                                    setup.phaser.register();

                                    tasks.addNewTask(getDownloaderTask(
                                            link,
                                            finalDepth,
                                            setup.phaser,
                                            layer,
                                            setup.getExceptions(),
                                            setup.was,
                                            tasks
                                    ));
                                } catch (final MalformedURLException e) {
                                    setup.phaser.arriveAndDeregister();
                                    setup.exceptions.put(link, e);
                                    setup.was.remove(link);
                                }
                            }
                    );
            setup.phaser.arriveAndAwaitAdvance();
        }
        setup.result.addAll(setup.was);
    }

    private static class CrawlerArguments {
        private final String url;
        private final int depth;
        private final int downloaders;
        private final int extractors;
        private final int perHost;

        private int parseArgument(final String[] args, final int idx, final int defaultValue) {
            try {
                final String d = args[idx];
                return Integer.parseInt(d);
            } catch (final IllegalArgumentException | IndexOutOfBoundsException e) {
                return defaultValue;
            }
        }

        public CrawlerArguments(final String[] args) {
            url = args[0];
            depth = parseArgument(args, 1, 2);
            downloaders = parseArgument(args, 2, 16);
            extractors = parseArgument(args, 3, 16);
            perHost = parseArgument(args, 4, 128);
        }

        public String getUrl() {
            return url;
        }

        public int getDepth() {
            return depth;
        }

        public int getDownloaders() {
            return downloaders;
        }

        public int getExtractors() {
            return extractors;
        }

        public int getPerHost() {
            return perHost;
        }
    }

    /**
     * Creates Crawler and starts a download with given params.
     * Usage - WebCrawler url [depth [downloads [extractors [perHost]]]]
     *
     * @param args params for download: {@code args[0]} - url to start download with
     *             [OPTIONAL] {@code args[1]} - max depth of pages to download
     *             [OPTIONAL] {@code args[2]} - amount of downloaders to use
     *             [OPTIONAL] {@code args[3]} - amount of extractors to use
     *             [OPTIONAL] {@code args[4]} - max amount of pages to download using one host
     */
    public static void main(final String[] args) {
        if (args.length > 5) {
            System.out.println("Illegal amount of arguments: must be 5, but actually is " + args.length);
            System.out.println(USAGE);
            return;
        }
        final CrawlerArguments arguments = new CrawlerArguments(args);
        // :NOTE: magic number
        try (final Crawler crawler = new WebCrawler(new CachingDownloader(60),
                                                    arguments.getDownloaders(), arguments.getExtractors(), arguments.getPerHost())) {
            System.out.println(crawler.download(arguments.getUrl(), arguments.getDepth()).getDownloaded());
        } catch (final IOException e) {
            System.out.println("An error occurred while trying to crawl - " + e.getMessage());
        }
    }

    @Override
    public void close() {
        downloaders.shutdownNow();
        extractors.shutdownNow();
    }
}
