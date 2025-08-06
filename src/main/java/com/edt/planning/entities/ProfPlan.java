package com.edt.planning.entities;

public class ProfPlan {
    Integer id;
    String nomProf;

    public ProfPlan(Integer id, String nomProf) {
        this.id = id;
        this.nomProf = nomProf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomProf() {
        return nomProf;
    }

    public void setNomProf(String nomProf) {
        this.nomProf = nomProf;
    }
}
