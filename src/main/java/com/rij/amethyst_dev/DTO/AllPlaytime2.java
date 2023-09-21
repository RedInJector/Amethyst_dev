package com.rij.amethyst_dev.DTO;

public record AllPlaytime2(
        Integer planuserid,
        String lastDaySeconds,
        String lastWeekSeconds,
        String lastMonthSeconds,
        String allTimeSeconds
) {
}
