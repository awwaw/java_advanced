package info.kgeorgiy.ja.podkorytov.iterative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynchronizedList<T> {
    private final List<T> values;
    private int processed = 0;

    public SynchronizedList(int blocks) {
        this.values = new ArrayList<>(Collections.nCopies(blocks, null));
    }

    public synchronized void set(int index, T value) {
        values.set(index, value);
        processed++;
        notifyAll();
    }

    public synchronized List<T> getValues() throws InterruptedException {
        while (processed < values.size()) {
            wait();
        }
        List<T> res = new ArrayList<>(values);
        notifyAll();
        return res;
    }
}
