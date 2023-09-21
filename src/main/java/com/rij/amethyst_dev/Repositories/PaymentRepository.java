package com.rij.amethyst_dev.Repositories;

import com.rij.amethyst_dev.models.Payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
