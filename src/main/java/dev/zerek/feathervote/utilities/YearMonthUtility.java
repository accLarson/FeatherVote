package dev.zerek.feathervote.utilities;

import dev.zerek.feathervote.FeatherVote;

import java.time.LocalDate;
import java.time.ZoneId;

public class YearMonthUtility {

    private final FeatherVote plugin;

    public YearMonthUtility(FeatherVote plugin) {
        this.plugin = plugin;
    }

    private LocalDate nowEastern() {
        return LocalDate.now(ZoneId.of("Canada/Eastern"));
    }

    public String getPreviousYearMonth(int reverseMonths) {
        if (reverseMonths == 0) return getCurrentYearMonth();

        LocalDate date = nowEastern().minusMonths(reverseMonths);
        return date.getYear() + "/" + date.getMonthValue();
    }

    public String getCurrentYearMonth() {
        LocalDate date = nowEastern();
        return date.getYear() + "/" + date.getMonthValue();
    }
}
