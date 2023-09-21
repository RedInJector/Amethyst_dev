package com.rij.amethyst_dev.DTO;

public record AllPlaytime(
        String lastDaySeconds,
        String lastWeekSeconds,
        String lastMonthSeconds,
        String allTimeSeconds
) {
}

