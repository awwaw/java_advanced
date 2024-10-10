package info.kgeorgiy.ja.podkorytov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloUDPServer implements NewHelloServer {
    private static final String USAGE = "USAGE: <port> <number of threads>";
    private final Map<Integer, DatagramSocket> sockets = new HashMap<>();
    private ExecutorService workers;
    private ExecutorService listeners;

    private static final int MAX_LOAD = 1_000;
    private static final int TIMEOUT = 300;


    @Override
    public void start(final int threads, final Map<Integer, String> ports) {
        final int portsAmount = ports.size();
        // :NOTE: check threads > 0?
        if (portsAmount == 0) {
            return;
        }
        listeners = Executors.newFixedThreadPool(portsAmount);
        workers = Executors.newFixedThreadPool(threads);
        for (final Map.Entry<Integer, String> entry : ports.entrySet()) {
            final int port = entry.getKey();
            final String prefix = entry.getValue();
            final DatagramSocket datagramSocket;
            try {
                sockets.putIfAbsent(port, new DatagramSocket(port));
                datagramSocket = sockets.get(port);
                datagramSocket.setSoTimeout(TIMEOUT);
            } catch (final SocketException e) {
                System.err.println("Couldn't open socket");
                System.err.println(e.getMessage());
                continue;
            }
            final DatagramSocket finalDatagramSocket = datagramSocket;
            final AtomicInteger loaded = new AtomicInteger(0);
            listeners.submit(
                    () -> {
                        while (!finalDatagramSocket.isClosed()) {
                            try {
                                final int bufferSize = finalDatagramSocket.getSendBufferSize();
                                final DatagramPacket request = new DatagramPacket(new byte[bufferSize], bufferSize);
                                try {
                                    finalDatagramSocket.receive(request);
                                    final String requestBody = new String(request.getData(),
                                            request.getOffset(),
                                            request.getLength(),
                                            StandardCharsets.UTF_8);
                                    if (loaded.get() == MAX_LOAD) { // :NOTE: to const
                                        continue;
                                    }
                                    workers.submit(
                                            () -> {
                                                loaded.getAndIncrement();
                                                request.setData(
                                                        prefix.replace("$", requestBody)
                                                                .getBytes(StandardCharsets.UTF_8)
                                                );
                                                try {
                                                    finalDatagramSocket.send(request);
                                                } catch (final IOException e) {
                                                    System.err.println("Unable to send packet");
                                                    System.err.println(e.getMessage());
                                                }
                                                loaded.getAndDecrement();
                                            }
                                    );
                                } catch (final IOException e) {
                                    System.err.println("An I/O error occurred while exchanging data, port = " + port);
                                    System.err.println(e.getMessage());
                                }
                            } catch (final SocketException e) {
                                System.err.println("Couldn't open socket");
                                System.err.println(e.getMessage());
                            }
                        }
                    }
            );
        }
    }

    private void shutdownService(final ExecutorService service) {
        if (service == null) {
            return;
        }

        // :NOTE: service.close() since jdk19
        // fixed
        service.close();
        try {
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            System.err.println("An error occurred while trying to shutdown ExecutorService");
            System.err.println(e.getMessage());
            service.shutdownNow();
        }
    }

    @Override
    public void close() {
        for (final Map.Entry<Integer, DatagramSocket> entry : sockets.entrySet()) {
            entry.getValue().close(); // :NOTE: close() may throw
        }
        shutdownService(workers);
        shutdownService(listeners);
    }

    public static void main(final String[] args) {
        // :NOTE: args same as Client
        try (final HelloUDPServer server = new HelloUDPServer()) {
            final int port = Integer.parseInt(Objects.requireNonNull(args[0]));
            final int threads = Integer.parseInt(Objects.requireNonNull(args[1]));
            server.start(port, threads);
        } catch (final IllegalArgumentException e) {
            System.out.println(USAGE);
            System.out.println(e.getMessage());
        }
    }
}
