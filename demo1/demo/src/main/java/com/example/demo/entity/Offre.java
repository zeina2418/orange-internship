package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "offre")
public class Offre {
    @Id
    private long id;
    private long segment;
    private String nom;
    private Integer times;
}
