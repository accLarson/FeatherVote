package com.zerek.feathervote.utilities;

import com.zerek.feathervote.FeatherVote;

import java.time.LocalDate;

public class YearMonthUtility {

    private final FeatherVote plugin;

    private String currentYearMonth;


    public YearMonthUtility(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        this. currentYearMonth = LocalDate.now().getYear() + "/" + LocalDate.now().getMonthValue();
    }

    public String getPreviousYearMonth(int reverseMonths) {

        if (reverseMonths == 0) return currentYearMonth;

        int year = LocalDate.now().getYear() - reverseMonths/12;

        return year + "/" + LocalDate.now().getMonth().minus(reverseMonths).getValue();
    }

    public String getCurrentYearMonth() {
        return currentYearMonth;
    }
}
