package info.kgeorgiy.ja.podkorytov.iterative;

import java.util.Queue;

public class TaskQueue {
    private final Queue<Runnable> queue;

    public TaskQueue(Queue<Runnable> queue) {
        this.queue = queue;
    }

    synchronized void add(Runnable task) {
        queue.add(task);
        notifyAll();
    }

    synchronized Runnable poll() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }
}
