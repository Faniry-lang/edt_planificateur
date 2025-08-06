package com.edt.planning.entities;

public class JourPlan {
    Integer id;
    String nomJour;

    public JourPlan(Integer id, String nomJour) {
        this.id = id;
        this.nomJour = nomJour;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomJour() {
        return nomJour;
    }

    public void setNomJour(String nomJour) {
        this.nomJour = nomJour;
    }   
}
