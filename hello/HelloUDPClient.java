package info.kgeorgiy.ja.podkorytov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class HelloUDPClient implements HelloClient {
    private static final String USAGE = "USAGE: <host> <prefix> <port> <number of threads> <number of requests>";

    @Override
    public void run(final String host, final int port, final String prefix, final int threads, final int requests) {
        final InetSocketAddress address = new InetSocketAddress(host, port);
        try (final ExecutorService workers = Executors.newFixedThreadPool(threads)) {
            for (int i = 1; i <= threads; i++) {
                final int num = i;
                final int finalI = i;
                final Runnable task = () -> {
                    try (final DatagramSocket socket = new DatagramSocket()) {
                        final int bufferSize = socket.getReceiveBufferSize();
                        final DatagramPacket response = new DatagramPacket(
                                new byte[bufferSize],
                                bufferSize
                        );

                        for (int requestId = 1; requestId <= requests; requestId++) {
                            final String requestBody = getRequest(prefix, num, requestId);
                            final DatagramPacket request = new DatagramPacket(
                                    requestBody.getBytes(StandardCharsets.UTF_8),
                                    requestBody.length(),
                                    address
                            );
                            socket.setSoTimeout(300); // :NOTE: to const

                            while (true) {
                                try {
                                    socket.send(request);
                                    socket.receive(response);
                                } catch (final SecurityException e) {
                                    // :NOTE: Лучше в одну строку, а то потом непонятно, сообщение относится
                                    // :NOTE: К строке выше или ниже
                                    System.err.println("There are no permission to send requests.");
                                    System.err.println(e.getMessage());
                                } catch (final PortUnreachableException e) {
                                    System.err.println("Socket is in unreachable state");
                                    System.err.println(e.getMessage());
                                } catch (final SocketTimeoutException e) {
                                    System.err.println("Socket was timeout:(");
                                    System.err.println(e.getMessage());
                                } catch (final IOException e) {
                                    System.err.println("An I/O error occurred while trying to send/receive data");
                                    System.err.println(e.getMessage());
                                }

                                final String responseData = new String(response.getData(),
                                        response.getOffset(),
                                        response.getLength(),
                                        StandardCharsets.UTF_8);

                                if (validateResponse(responseData, finalI, requestId)) {
                                    System.out.println(responseData);
                                    break;
                                }
                            }
                        }
                    } catch (final SocketException e) {
                        System.err.println("Couldn't open socket");
                        System.err.println(e.getMessage());
                    }
                };

                workers.submit(task);
            }
        }

    }

    private String getRequest(final String prefix, final int thread, final int request) {
        return String.format("%s%d_%d", prefix, thread, request);
    }

    private boolean validateResponse(final String responseData, final int threadId, final int requestId) {
        final StringBuilder responseString = new StringBuilder(responseData);
        for (int i = 0; i < responseString.length(); i++) {
            if (Character.isDigit(responseString.charAt(i))) {
                responseString.setCharAt(i, (char) (Character.getNumericValue(responseString.charAt(i)) + '0'));
            }
        }
        // :NOTE: Почти, Pattern.compile + find, тогда не придётся делать обработку сверху
        // :NOTE: (не забыть переиспользовать паттерн)
        return responseString.toString().matches("\\D*" + threadId + "\\D+" + requestId + "\\D*");
    }

    /**
     * Starts an instance of {@link HelloUDPClient} with given args
     * Usage - {@code <host> <prefix> <port> <number of threads> <number of requests>}
     *
     * @param args
     */
    public static void main(final String[] args) {
        // :NOTE: args == null, args[i]==null (i.g. host)
        try {
            final String host = args[0];
            final int port = Integer.parseInt(Objects.requireNonNull(args[1]));
            final String prefix = args[2];
            final int threads = Integer.parseInt(Objects.requireNonNull(args[3]));
            final int requests = Integer.parseInt(Objects.requireNonNull(args[4]));
            new HelloUDPClient().run(host, port, prefix, threads, requests);
        } catch (final IllegalArgumentException e) {
            System.out.println(USAGE);
        }
    }
}
