package com.rij.amethyst_dev.bot;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class EmbedGenerator {
    public static EmbedBuilder AuthEmbed(String username){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Вхід на Ametis");
        eb.setDescription(username +  " запит на підтвердження входу на сервер."
                + "\n\n" + "Натисніть :white_check_mark:, щоб підтвердити вхід.");

        eb.setColor(new Color(0x8253ef));


        return eb;
    }
}
