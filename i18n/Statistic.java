package info.kgeorgiy.ja.podkorytov.i18n;

import java.text.Collator;
import java.util.Comparator;

public abstract class Statistic<T> {
    protected int number;
    protected int uniqueNumber;
    protected T min;
    protected T max;
    protected double average;

    public int getNumber() {
        return number;
    }

    public int getUnique() {
        return uniqueNumber;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public double getAverage() {
        return average;
    }

    public abstract void update(final T candidate, final Collator collator);

    public void setNumericStatistic(final int number, final int uniqueNumber, final double averageLength) {
        this.number = number;
        this.uniqueNumber = uniqueNumber;
        this.average = averageLength;
    }
}
