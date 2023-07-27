package com.rij.amethyst_dev.Dev.DTO.User;

import java.util.List;

public record UserStatisticsDTO(

        String time_all,
        String time_month,
        String time_week,
        String time_day,
        Long last_online,
        List<PlayTimeDateDTO> heatmap_data
) {
}
