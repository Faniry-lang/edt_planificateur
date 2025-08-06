package com.edt.planning.entities;

// scientifique, litéraire, science economique et sociale, etc.
public class SpePlan {
    Integer id;
    String nomSpe;

    public SpePlan(Integer id, String nomSpe) {
        this.id = id;
        this.nomSpe = nomSpe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomSpe() {
        return nomSpe;
    }

    public void setNomSpe(String nomSpe) {
        this.nomSpe = nomSpe;
    }
}
