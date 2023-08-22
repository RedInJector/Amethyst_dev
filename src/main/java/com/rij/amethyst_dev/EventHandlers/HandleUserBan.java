package com.rij.amethyst_dev.EventHandlers;

import com.rij.amethyst_dev.events.UserBanned;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class HandleUserBan implements ApplicationListener<UserBanned> {



    @Override
    public void onApplicationEvent(UserBanned event) {

    }
}
