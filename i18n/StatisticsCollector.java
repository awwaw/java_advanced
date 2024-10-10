package info.kgeorgiy.ja.podkorytov.i18n;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StatisticsCollector {

    private static Locale getLocale(final String locale) throws NoSuchElementException {
        String lang = locale.split("-")[0];
        String country = locale.split("-")[1];
        return Arrays.stream(Locale.getAvailableLocales())
                .filter(loc -> loc.getCountry().equals(country) && loc.getLanguage().equals(lang))
                .findFirst()
                .get();
    }

    public static List<Statistic<?>> read(final String textLocale,
                                          final String inputFile) throws NoSuchElementException, IOException {
        Locale locale; // :NOTE: получить один раз в конструктое
        try {
            System.out.println("TextLocale - " + textLocale);
            locale = getLocale(textLocale);
        } catch (NoSuchElementException e) {
            System.err.println("YUUUUUUUUUU"); // :NOTE:
            return null;
        }
        String text = String.join("\n", Files.readAllLines(Path.of(inputFile)));
        TextStatistics statistics = new TextStatistics(locale, text);
        return statistics.getAllStatistics();
    }

    public static void print(List<Statistic<?>> statistics,
                             final String loc,
                             final String inputFile,
                             final String outputFile) throws IOException, NoSuchElementException {
        TextStat sentences = (TextStat) statistics.get(0);
        TextStat words = (TextStat) statistics.get(1);
        NumberStat numbers = (NumberStat) statistics.get(2);
        NumberStat money = (NumberStat) statistics.get(3);
        DateStat dates = (DateStat) statistics.get(4);

        Locale locale = getLocale(loc);
        ResourceBundle bundle = ResourceBundle.getBundle("info.kgeorgiy.ja.podkorytov.i18n.UsageResourceBundle", locale);

        String out = String.format(
                "  %s: %s\n" +

                        "%s:\n" +
                        "%s %s: %d\n" +
                        "%s %s: %d\n" +
                        "%s %s: %d\n" +
                        "%s %s: %d\n" +
                        "%s %s: %d\n" +

                        "%s:\n" +
                        "%s %s: %d (%d %s)\n" +
                        "%s %s: %s\n" +
                        "%s %s: %s\n" +
                        "%s %s %s: %d (%s)\n" +
                        "%s %s %s: %d (%s)\n" +
                        "%s %s %s: %d\n" +
                        "\n" +

                        "%s:\n" +
                        "%s %s: %d (%d %s)\n" +
                        "%s %s: %s\n" +
                        "%s %s: %s\n" +
                        "%s %s %s: %d (%s)\n" +
                        "%s %s %s: %d (%s)\n" +
                        "%s %s %s: %d\n" +
                        "\n" +

                        "%s:\n" +
                        "%s %s: %d (%d %s)\n" +
                        "%s %s: %s\n" +
                        "%s %s: %s\n" +
                        "%s %s: %f\n" +
                        "\n" +

                        "%s:\n" +
                        "%s %s: %d (%d %s)\n" +
                        "%s %s: %s\n" +
                        "%s %s: %s\n" +
                        "%s %s: %f\n" +
                        "\n" +

                        "%s:\n" +
                        "%s %s: %d (%d %s)\n" +
                        "%s %s: %s\n" +
                        "%s %s: %s\n" +
                        "%s %s: %f\n" +
                        "\n",
                bundle.getString("analyzedFile"),
                inputFile,

                bundle.getString("commonStat"),
                bundle.getString("Number"),
                bundle.getString("sentences"),
                sentences.getNumber(),

                bundle.getString("Number"),
                bundle.getString("words"),
                words.getNumber(),

                bundle.getString("Number"),
                bundle.getString("number_el"),
                numbers.getNumber(),

                bundle.getString("Number"),
                bundle.getString("currencies_t"),
                money.getNumber(),

                bundle.getString("Number"),
                bundle.getString("dates_"),
                dates.getNumber(),
////////////////////////////////////////////////////////////////////////
                bundle.getString("sentencesStat"),
                bundle.getString("Number"),
                bundle.getString("sentences"),
                sentences.getNumber(),
                sentences.getUnique(),
                bundle.getString("unique"),

                bundle.getString("min_oe"),
                bundle.getString("sentence"),
                sentences.getMin(),

                bundle.getString("max_oe"),
                bundle.getString("sentence"),
                sentences.getMax(),

                bundle.getString("max_ya"),
                bundle.getString("length"),
                bundle.getString("sentences_ya"),
                sentences.getMaxLengthValue(),
                sentences.getMaxLength(),

                bundle.getString("min_ya"),
                bundle.getString("length"),
                bundle.getString("sentences_ya"),
                sentences.getMinLengthValue(),
                sentences.getMinLength(),

                bundle.getString("average_ya"),
                bundle.getString("length"),
                bundle.getString("sentences_ya"),
                ((int) sentences.getAverage()),
///////////////////////////////////////////////////////////////////////////
                bundle.getString("wordsStat"),
                bundle.getString("Number"),
                bundle.getString("words"),
                words.getNumber(),
                words.getUnique(),

                bundle.getString("unique"),
                bundle.getString("min_oe"),
                bundle.getString("word_o"),
                words.getMin(),

                bundle.getString("max_oe"),
                bundle.getString("word_o"),
                words.getMax(),

                bundle.getString("max_ya"),
                bundle.getString("length"),
                bundle.getString("word"),
                words.getMaxLengthValue(),
                words.getMaxLength(),

                bundle.getString("min_ya"),
                bundle.getString("length"),
                bundle.getString("word"),
                words.getMinLengthValue(),
                words.getMinLength(),

                bundle.getString("average_ya"),
                bundle.getString("length"),
                bundle.getString("word"),
                ((int) words.getAverage()),

                bundle.getString("numbersStat"),
                bundle.getString("Number"),
                bundle.getString("number_el"),
                numbers.getNumber(),
                numbers.getUnique(),
                bundle.getString("unique"),

                bundle.getString("min_oe"),
                bundle.getString("number"),
                numbers.getMin(),

                bundle.getString("max_oe"),
                bundle.getString("number"),
                numbers.getMax(),

                bundle.getString("average"),
                bundle.getString("number"),
                numbers.getAverage(),
////////////////////////////////////////////////////////////////////////
                bundle.getString("currencyStat"),
                bundle.getString("Number"),
                bundle.getString("sum_t"),
                money.getNumber(),
                money.getUnique(),
                bundle.getString("unique"),

                bundle.getString("min_ya"),
                bundle.getString("sum"),
                money.getMin(),

                bundle.getString("max_ya"),
                bundle.getString("sum"),
                money.getMax(),

                bundle.getString("average_ya"),
                bundle.getString("sum"),
                money.getAverage(),
/////////////////////////////////////////////////////////////////////
                bundle.getString("datesStat"),
                bundle.getString("Number"),
                bundle.getString("dates_"),
                dates.getNumber(),
                dates.getUnique(),
                bundle.getString("unique"),

                bundle.getString("min_ya"),
                bundle.getString("date_a"),
                dates.getMin(),

                bundle.getString("max_ya"),
                bundle.getString("date_a"),
                dates.getMax(),

                bundle.getString("average_ya"),
                bundle.getString("date_a"),
                dates.getAverage());

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(out);
        writer.close();
    }
}
