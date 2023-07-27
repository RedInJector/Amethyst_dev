package com.rij.amethyst_dev.Dev.Events;

import com.rij.amethyst_dev.jsons.Donation;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentEvent extends ApplicationEvent {

    private final Donation donation;
    public PaymentEvent(Object source, Donation donation) {
        super(source);
        this.donation = donation;
    }
}
