package com.rij.amethyst_dev.Dev.Events;

import com.rij.amethyst_dev.jsons.Donation;
import com.rij.amethyst_dev.models.Userdb.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DonationEvent extends ApplicationEvent {

    private final Donation donation;
    public DonationEvent(Object source, Donation donation) {
        super(source);
        this.donation = donation;
    }
}
