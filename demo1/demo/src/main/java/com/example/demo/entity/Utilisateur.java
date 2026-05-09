package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long segment;
    private long tel;

    public Utilisateur(){

    }
    public Utilisateur(long tel, long segment){
        this.tel = tel;
        this.segment = segment;
    }
}
