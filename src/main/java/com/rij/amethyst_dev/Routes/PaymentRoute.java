package com.rij.amethyst_dev.Routes;

import com.rij.amethyst_dev.events.PaymentEvent;
import com.rij.amethyst_dev.Helpers.StringComparator;
import com.rij.amethyst_dev.jsons.Donation;
import com.rij.amethyst_dev.models.Payments.Payment;
import com.rij.amethyst_dev.Services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/")
public class PaymentRoute {


    private final ApplicationEventPublisher eventPublisher;
    private final PaymentService paymentService;
    Logger logger = LoggerFactory.getLogger(PaymentRoute.class);
    @Value("${donatello.api.key}")
    public String DONATELLO_API_KEY;

    public PaymentRoute(ApplicationEventPublisher eventPublisher, PaymentService paymentService) {
        this.eventPublisher = eventPublisher;
        this.paymentService = paymentService;
    }


    @PostMapping("/donatellopayment")
    public ResponseEntity<String> test2(@RequestBody Donation donate, @RequestHeader("X-Key") String header) {
        if (!StringComparator.compareAPIKeys(DONATELLO_API_KEY, header))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        logger.info("Incoming payment: " + donate.toString());
        Payment payment = new Payment().fromDonate(donate);
        paymentService.savePayment(payment);

        PaymentEvent event = new PaymentEvent(this, donate);
        eventPublisher.publishEvent(event);

        return ResponseEntity.ok("Ok");
    }
}