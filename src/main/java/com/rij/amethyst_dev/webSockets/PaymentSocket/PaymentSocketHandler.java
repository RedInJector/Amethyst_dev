package com.rij.amethyst_dev.webSockets.PaymentSocket;

import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import com.rij.amethyst_dev.webSockets.Helper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Optional;

public class PaymentSocketHandler extends TextWebSocketHandler {
    //ws://localhost:8080/api/v1/payment-socket?userid=1
    private final PaymentSocketService paymentSocketService;
    private final UserService userService;

    public PaymentSocketHandler(PaymentSocketService paymentSocketService, UserService userService) {
        this.paymentSocketService = paymentSocketService;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Code to handle connection establishment
        URI uri = session.getUri();
        String userid;

        userid = Helper.splitQuery(uri).get("userid").get(0);
        if(userid == null)
            return;

        paymentSocketService.addSession(userid, session);

        Optional<User> user = userService.getById(Integer.parseInt(userid));


        if(user.isPresent() && user.get().isHasPayed())
            paymentSocketService.sendMessage(userid);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        paymentSocketService.removeSession(session);
    }
}
