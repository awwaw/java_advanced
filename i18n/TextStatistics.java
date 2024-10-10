package info.kgeorgiy.ja.podkorytov.i18n;

import java.io.IOException;
import java.text.*;
import java.util.*;

public class TextStatistics {
    private Locale locale;
    private String text;

    public TextStatistics(final Locale locale, final String text) {
        this.locale = locale;
        this.text = text;
    }

    public Locale getLocale() {
        return locale;
    }

    private boolean checkWord(final String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    public TextStat getStatistic(final BreakIterator iterator) {
        TextStat stats = new TextStat();

        int number = 0;
        long summaryLength = 0;
        Set<String> strings = new HashSet<>();
        Collator collator = Collator.getInstance(locale);

        iterator.setText(text);
        int start = iterator.first();
        int currentPosition = iterator.next();


        while (currentPosition != BreakIterator.DONE) {
            String currentString = text.substring(start, currentPosition).strip();

            if (!currentString.isEmpty() && checkWord(currentString)) {
                stats.update(currentString, collator);
                summaryLength += currentString.length();
                number++;
                strings.add(currentString);
            }

            start = currentPosition;
            currentPosition = iterator.next();
        }
        stats.setNumericStatistic(number, strings.size(), (double) summaryLength / (double) number);

        return stats;
    }

    private NumberStat getNumberStatistic(final NumberFormat format) {
        NumberStat numbers = new NumberStat();
        int number = 0;
        double sum = 0;
        Set<Number> unique = new HashSet<>();

        int index = 0;
        while (index < text.length()) {
            ParsePosition position = new ParsePosition(index);
            int oldIndex = position.getIndex();
            Number value = format.parse(text, position);
            if (position.getIndex() == oldIndex) {
                index++;
            } else {
                number++;
                sum += value.doubleValue();
                numbers.update(value, null);
                unique.add(value);
                index = position.getIndex();
            }
        }
        numbers.setNumericStatistic(number, unique.size(), sum / number);

        return numbers;
    }

    private Long getMilliseconds(final Date date) {
        return date.toInstant().toEpochMilli();
    }

    public List<Statistic<?>> getAllStatistics() {
        TextStat sentences = new TextStat();
        TextStat words = new TextStat();
        NumberStat numbers = new NumberStat();
        NumberStat money = new NumberStat();
        DateStat dates = new DateStat();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

        List<DateFormat> dateFormats = List.of(
                DateFormat.getDateInstance(DateFormat.SHORT, locale),
                DateFormat.getDateInstance(DateFormat.MEDIUM, locale),
                DateFormat.getDateInstance(DateFormat.LONG, locale),
                DateFormat.getDateInstance(DateFormat.FULL, locale),
                DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
        );

        BreakIterator wordIterator = BreakIterator.getWordInstance(locale);
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(locale);
        wordIterator.setText(text);
        sentenceIterator.setText(text);
        words = getStatistic(wordIterator);
        sentences = getStatistic(sentenceIterator);

        numbers = getNumberStatistic(numberFormat);
        money = getNumberStatistic(currencyFormat);

        int number = 0;
        Set<Date> unique = new HashSet<>();
        long sum = 0;

        int index = 0;
        while (index < text.length()) {
            ParsePosition position = new ParsePosition(index);
            int oldIndex = position.getIndex();
            boolean parsed = false;
            for (final DateFormat format : dateFormats) {
                Date value = format.parse(text, position);
                if (position.getIndex() != oldIndex) {
                    parsed = true;
                    number++;
                    sum += getMilliseconds(value);
                    dates.update(value, null);
                    unique.add(value);
                    index = position.getIndex();
                    break;
                }
            }
            if (!parsed) {
                index++;
            }
        }
        dates.setNumericStatistic(number, unique.size(), (double) sum / number);

        return List.of(sentences, words, numbers, money, dates);
    }

    public static void main(String[] args) {
        if (args == null || args.length < 4) {
            System.err.println("Usage: <Input Locale> <Output Locale> <Input file> <Output file>.");
            Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::getDisplayName)
                    .sorted()
                    .forEachOrdered(System.err::println);
            return;
        }

        String inputLocale = Objects.requireNonNull(args[0]);
        String outputLocale = Objects.requireNonNull(args[1]);
        String inputFile = Objects.requireNonNull(args[2]);
        String outputFile = Objects.requireNonNull(args[3]);

        List<Statistic<?>> stats;
        try {
            stats = StatisticsCollector.read(inputLocale, inputFile);
        } catch (IOException e) {
            System.err.println("Can't read from input file: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println("An error while trying to read from file: " + e.getMessage());
            return;
        }

        try {
            StatisticsCollector.print(stats, outputLocale, inputFile, outputFile);
        } catch (IOException e) {
            System.err.println("Can't write to output file: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("An error while trying to write to file: " + e.getMessage());
        }
    }
}
