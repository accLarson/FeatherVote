package dev.zerek.feathervote.utilities;

import dev.zerek.feathervote.FeatherVote;

import java.time.LocalDate;
import java.time.ZoneId;

public class YearMonthUtility {

    private final FeatherVote plugin;

    private String currentYearMonth;


    public YearMonthUtility(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        this. currentYearMonth = LocalDate.now(ZoneId.of("Canada/Eastern")).getYear() + "/" + LocalDate.now(ZoneId.of("Canada/Eastern")).getMonthValue();
    }

    public String getPreviousYearMonth(int reverseMonths) {

        if (reverseMonths == 0) return currentYearMonth;

        int year = LocalDate.now(ZoneId.of("Canada/Eastern")).getYear() - reverseMonths/12;

        return year + "/" + LocalDate.now(ZoneId.of("Canada/Eastern")).getMonth().minus(reverseMonths).getValue();
    }

    public String getCurrentYearMonth() {
        return currentYearMonth;
    }
}
