package com.rij.amethyst_dev.webSockets.GuildSocket;

import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.webSockets.Helper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.net.URI;

public class GuildSocketHandler extends TextWebSocketHandler {
    //ws://localhost:8080/api/v1/guild-socket?dsid=654684984

    private final GuildSocketService guildSocketService;
    private final DiscordBotService discordBotService;

    public GuildSocketHandler(GuildSocketService guildSocketService, DiscordBotService discordBotService) {
        this.guildSocketService = guildSocketService;
        this.discordBotService = discordBotService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Code to handle connection establishment
        URI uri = session.getUri();
        String discordid;

        discordid = Helper.splitQuery(uri).get("dsid").get(0);
        if(discordid == null)
            return;

        guildSocketService.addSession(discordid, session);

        if(discordBotService.isUserOnServer(discordid)){
            guildSocketService.sendMessage(discordid);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        guildSocketService.removeSession(session);
    }


}
