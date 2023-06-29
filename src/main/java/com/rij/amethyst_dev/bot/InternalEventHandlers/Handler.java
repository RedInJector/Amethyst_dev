package com.rij.amethyst_dev.bot.InternalEventHandlers;

import com.rij.amethyst_dev.bot.DiscordBot;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;


public abstract class Handler<T extends ApplicationEvent> implements ApplicationListener<T> {
    private final DiscordBot botConfig;
    protected JDA jda;
    protected MessageSource messageSource;
    public Handler(DiscordBot botConfig) {
        System.out.println("set jda2");
        this.botConfig = botConfig;
    }

    protected String getMessage(String key) {
        return messageSource.getMessage(key, null, null);
    }
    @Override
    public void onApplicationEvent(T event){
        if(botConfig.getJda() == null)
            return;
        this.jda = botConfig.getJda();
        this.messageSource = botConfig.getMessageSource();

        onEvent(event);
    }

    public abstract void onEvent(T event);


}
