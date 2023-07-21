package com.rij.amethyst_dev.Dev.Events;

import com.rij.amethyst_dev.models.Userdb.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserPardoned extends ApplicationEvent {
    private final User user;

    public UserPardoned(Object source, User user) {
        super(source);
        this.user = user;
    }
}
