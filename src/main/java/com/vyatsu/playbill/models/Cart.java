package com.vyatsu.playbill.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "purchased", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean purchased;
    @Column(name = "payment_type", columnDefinition = "VARCHAR(255) DEFAULT 'common'")
    private String paymentType;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
