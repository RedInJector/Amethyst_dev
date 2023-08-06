package com.rij.amethyst_dev.webSockets;

import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.Services.UserService;
import com.rij.amethyst_dev.webSockets.GuildSocket.GuildSocketHandler;
import com.rij.amethyst_dev.webSockets.GuildSocket.GuildSocketService;
import com.rij.amethyst_dev.webSockets.PaymentSocket.PaymentSocketHandler;
import com.rij.amethyst_dev.webSockets.PaymentSocket.PaymentSocketService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketsConfig implements WebSocketConfigurer {
    private final GuildSocketService guildSocketService;
    private final DiscordBotService discordBotService;
    private final PaymentSocketService paymentSocketService;
    private final UserService userService;

    public WebSocketsConfig(GuildSocketService guildSocketService, DiscordBotService discordBotService, PaymentSocketService paymentSocketService, UserService userService) {
        this.guildSocketService = guildSocketService;
        this.discordBotService = discordBotService;
        this.paymentSocketService = paymentSocketService;
        this.userService = userService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        GuildSocketHandler handler = new GuildSocketHandler(guildSocketService, discordBotService);
        PaymentSocketHandler paymentSocketHandler = new PaymentSocketHandler(paymentSocketService, userService);

        registry.addHandler(handler, "/api/v1/guild-socket")
                .setAllowedOrigins("*");

        registry.addHandler(paymentSocketHandler, "/api/v1/payment-socket")
                .setAllowedOrigins("*");
    }
}
