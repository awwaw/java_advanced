package info.kgeorgiy.ja.podkorytov.i18n;

import java.text.Collator;

public class TextStat extends Statistic<String> {
    private String minLength = null;
    private String maxLength = null;
    private int minLengthValue = 0;
    private int maxLengthValue = 0;

    private void updateMin(final String candidate, final Collator collator) {
        if (min == null || collator.compare(candidate, min) < 0) {
            min = candidate;
        }
    }

    private void updateMax(final String candidate, final Collator collator) {
        if (max == null || collator.compare(candidate, min) > 0) {
            max = candidate;
        }
    }

    private void updateMinLength(final String candidate) {
        if (minLength == null || candidate.length() < minLengthValue) {
            minLength = candidate;
            minLengthValue = candidate.length();
        }
    }

    private void updateMaxLength(final String candidate) {
        if (maxLength == null || candidate.length() > maxLengthValue) {
            maxLength = candidate;
            maxLengthValue = candidate.length();
        }
    }

    @Override
    public void update(final String candidate, final Collator collator) {
        updateMax(candidate, collator);
        updateMin(candidate, collator);
        updateMaxLength(candidate);
        updateMinLength(candidate);
    }

    public String getMinLength() {
        return minLength;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public int getMinLengthValue() {
        return minLengthValue;
    }

    public int getMaxLengthValue() {
        return maxLengthValue;
    }

}
