package com.rij.amethyst_dev.webSockets.GuildSocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@Service
public class GuildSocketService {
    private final Map<String, WebSocketSession> Sessions = new Hashtable<>();

    public void sendMessage(String discordID){
        if(!Sessions.containsKey(discordID))
            return;

        WebSocketSession session = Sessions.get(discordID);
        if(!session.isOpen())
            return;


        WebSocketMessage<String> message = new TextMessage("Ok");
        try { session.sendMessage(message); } catch (IOException ignored) {}

    }
    public void addSession(String discordid, WebSocketSession session){
        Sessions.put(discordid, session);
    }
    public void removeSession(WebSocketSession session){
        if(!Sessions.containsValue(session))
            return;

        Sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }
}
