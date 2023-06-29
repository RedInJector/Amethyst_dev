package com.rij.amethyst_dev.webSockets.PaymentSocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@Service
public class PaymentSocketService {
    private final Map<String, WebSocketSession> Sessions = new Hashtable<>();

    public void sendMessage(String userId){
        if(!Sessions.containsKey(userId))
            return;

        WebSocketSession session = Sessions.get(userId);
        if(!session.isOpen())
            return;


        WebSocketMessage<String> message = new TextMessage("Ok");
        try { session.sendMessage(message); } catch (IOException ignored) {}

    }
    public void addSession(String userId, WebSocketSession session){
        Sessions.put(userId, session);
    }
    public void removeSession(WebSocketSession session){
        if(!Sessions.containsValue(session))
            return;

        Sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }
}
