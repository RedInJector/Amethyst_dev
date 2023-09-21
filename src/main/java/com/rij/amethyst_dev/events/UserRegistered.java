package com.rij.amethyst_dev.events;

import com.rij.amethyst_dev.models.Userdb.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegistered extends ApplicationEvent {

    private final User user;
    public UserRegistered(Object source, User user) {
        super(source);
        this.user = user;
    }
}
