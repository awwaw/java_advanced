package info.kgeorgiy.ja.podkorytov.i18n;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

public class UsageResourceBundle_en extends ListResourceBundle {

    private final static Object[][] CONTENTS = {
            {"analyzedFile", "Analyzed file"},
            {"commonStat", "Common statistic"},
            {"wordsStat", "Words statistic"},
            {"sentencesStat", "Sentences statistic"},
            {"datesStat", "Dates statistic"},
            {"currencyStat", "Currency statistic"},
            {"numbersStat", "Numbers statistic"},
            {"Number", "Number"},
            {"number", "number"},
            {"numbers", "numbers"},
            {"number_el", "numbers"},
            {"words", "words"},
            {"word","of word"},
            {"word_o","words"},
            {"sentence", "sentence"},
            {"sentences", "sentences"},
            {"sentences_ya", "of sentences"},
            {"dates", "dates"},
            {"dates_", "dates"},
            {"date_a", "date"},
            {"currencies_t","currencies"},
            {"currencies", "currencies"},
            {"sum", "sum"},
            {"sum_t", "sum"},
            {"min_oe", "Min"},
            {"min_ya", "Min"},
            {"max_oe", "Max"},
            {"max_ya", "Max"},
            {"average", "Average"},
            {"average_ya", "Average"},
            {"length", "length"},
            {"unique", "unique"}
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
