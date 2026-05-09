package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historique")
public class Historique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long tel;
    @Column
    @CreationTimestamp
    private LocalDateTime date;
    @Column
    private long offerId;

    public Historique(Long tel, long offerId) {
        this.tel = tel;
        this.offerId = offerId;
    }

    public Historique() {

    }
}


