package com.rij.amethyst_dev.events;

import com.rij.amethyst_dev.models.Userdb.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserPayedEvent extends ApplicationEvent {
    private final User user;
    public UserPayedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
