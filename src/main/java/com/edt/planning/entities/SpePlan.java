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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatierePlan that = (MatierePlan) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
