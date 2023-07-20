package com.rij.amethyst_dev.DTO;

import java.time.LocalDate;
import java.util.Date;

public record DatePlaytime(
        Integer playtime,
        int day,
        String date
) {
}
