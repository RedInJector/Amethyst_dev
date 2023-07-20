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

    public static EmbedBuilder PaymentGreetengsToGuild(String id){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Вітаємо з приєднанням до нашої спільноти!");
        eb.setDescription("<@"+id+">"+" Через те, що нам не вдалося надіслати повідомлення вам особисто, бот буде надсилати повідомлення сюди. \n\nКоли ви будете заходити на сервер у майнкрафт в цей чат будуть приходити сповіщення про підтвердження авторизації.");

        eb.setColor(new Color(0x8253ef));

        return eb;
    }

    public static EmbedBuilder PaymentGreetengs(String id){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Вітаємо з приєднанням до нашої спільноти!");
        eb.setDescription("<@"+id+">"+" Коли ви будете заходити на сервер у майнкрафт в цей чат будуть приходити сповіщення про підтвердження авторизації.");

        eb.setColor(new Color(0x8253ef));

        return eb;
    }
}
