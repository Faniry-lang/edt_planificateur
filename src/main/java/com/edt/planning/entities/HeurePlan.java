package com.edt.planning.entities;

import java.time.LocalTime;

public class HeurePlan {
    Integer id;
    String nomHeure;
    LocalTime heure; 

    public HeurePlan(Integer id, String nomHeure, LocalTime heure) {
        this.id = id;
        this.nomHeure = nomHeure;
        this.heure = heure;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomHeure() {
        return nomHeure;
    }

    public void setNomHeure(String nomHeure) {
        this.nomHeure = nomHeure;
    }

    public LocalTime getHeure()
    {
        return this.heure;
    }

    public void setHeure(LocalTime heure)
    {
        this.heure = heure;
    }
}
