package com.rij.amethyst_dev.EventHandlers;

import com.rij.amethyst_dev.events.PaymentEvent;
import com.rij.amethyst_dev.events.UserPardoned;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentEventHandler implements ApplicationListener<PaymentEvent> {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final DiscordBotService discordBotService;

    public PaymentEventHandler(UserService userService, ApplicationEventPublisher eventPublisher, DiscordBotService discordBotService) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.discordBotService = discordBotService;
    }

    @Override
    public void onApplicationEvent(PaymentEvent event) {
        String goal = event.getDonation().getGoal();
        BigDecimal amount = event.getDonation().getAmount();

        switch (goal.toLowerCase()){
            case "перепустка на сервер":
                    if(amount.compareTo(new BigDecimal("35.99")) < 0 ) break;

                    giveServerPass(event.getDonation().getClientName());
                break;
            case "підтримка сервера":

                break;
            case "розбан на сервері":
                if(amount.compareTo(new BigDecimal("100")) < 0 ) break;
                Unban(event.getDonation().getClientName());
                break;
        }
    }



    private void giveServerPass(String playerName){
        User user = userService.getUserWithMinecraftname(playerName);

        if(user.isHasPayed())
            return;

        user.setHasPayed(true);
        userService.saveUser(user);

        String discordid = user.getDiscordUser().getDiscordId();

        discordBotService.GreetFirstTime(discordid);
        discordBotService.givePlayerRole(discordid);
    }

    private void Unban(String playerName) {
        User user = userService.getUserWithMinecraftname(playerName);

        if (!user.isUnbannable())
            return;

        user.setHasPayed(true);
        user.setBanned(false);

        UserPardoned event = new UserPardoned(this, user);
        eventPublisher.publishEvent(event);

        userService.saveUser(user);
    }

}
