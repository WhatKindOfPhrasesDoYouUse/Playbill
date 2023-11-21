package com.vyatsu.playbill.models;

import lombok.Data;
import org.springframework.data.domain.Page;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "time", nullable = false)
    private LocalTime time;
    @Column(name = "duration", nullable = false)
    private int duration;
    @Column(name = "location", nullable = false)
    private String location;
    @Column(name = "price", nullable = false)
    private int price;
    @Column(name = "age_limit", nullable = false)
    private int ageLimit;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts;
}
