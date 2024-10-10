package info.kgeorgiy.ja.podkorytov.i18n;

import java.util.ListResourceBundle;

public class UsageResourceBundle_ru extends ListResourceBundle {

    private static final Object[][] CONTENTS = {
            {"analyzedFile", "Анализируемый файл"},
            {"commonStat", "Сводная статистика"},
            {"wordsStat", "Статистика по словам"},
            {"sentencesStat", "Статистика по предложениям"},
            {"datesStat", "Статистика по датам"},
            {"currencyStat", "Статистика по валюте"},
            {"numbersStat", "Статистика по числам"},
            {"Number", "Число"},
            {"number", "число"},
            {"numbers", "числа"},
            {"number_el", "чисел"},
            {"words", "слов"},
            {"word","слова"},
            {"word_o","слово"},
            {"sentence", "предложение"},
            {"sentences", "предложений"},
            {"sentences_ya", "предложения"},
            {"dates", "даты"},
            {"dates_", "дат"},
            {"date_a", "дата"},
            {"currencies_t","валют"},
            {"currencies", "валюты"},
            {"sum", "сумма"},
            {"sum_t", "сумм"},
            {"min_oe", "Минимальное"},
            {"min_ya", "Минимальная"},
            {"max_oe", "Максимальное"},
            {"max_ya", "Максимальная"},
            {"average", "Среднее"},
            {"average_ya", "Средняя"},
            {"length", "длина"},
            {"unique", "уникальных"}
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
