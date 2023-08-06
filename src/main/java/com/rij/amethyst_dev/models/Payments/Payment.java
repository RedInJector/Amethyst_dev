package com.rij.amethyst_dev.models.Payments;

import com.rij.amethyst_dev.jsons.Donation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payment_history")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time", nullable = false, updatable = false)
    private LocalDateTime dateTime;
    @Column
    private BigDecimal amount;
    @Column
    private String message;
    @Column
    private String clientName;
    @Column
    private String goal;

    public Payment(){}

    public Payment fromDonate(Donation donation){
        this.goal = donation.getGoal();
        this.amount = donation.getAmount();
        this.clientName = donation.getClientName();
        this.message = donation.getMessage();
        this.dateTime = LocalDateTime.now();
        return this;
    }
}
