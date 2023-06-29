package com.rij.amethyst_dev.bot.DiscordEventHandlers;

import com.rij.amethyst_dev.MinecraftAuth.MCserverAuthService;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReaction extends ListenerAdapter {
    private final MCserverAuthService mCserverAuthService;

    public MessageReaction(MCserverAuthService mCserverAuthService) {
        this.mCserverAuthService = mCserverAuthService;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonID = event.getButton().getId();
        mCserverAuthService.ReactToButton(buttonID);
    }
}
