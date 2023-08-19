package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.models.Payments.Payment;
import com.rij.amethyst_dev.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void savePayment(Payment payment){
        paymentRepository.save(payment);
    }


}
