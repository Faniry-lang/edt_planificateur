package com.edt.planning.entities;

public class MatierePlan {
    Integer id;
    String nomMatiere;
    Integer limiteClasseEnParallele;
    Integer dureeSeance;

    public MatierePlan(Integer id, String nomMatiere) {
        this.id = id;
        this.nomMatiere = nomMatiere;
        this.limiteClasseEnParallele = 0;
        this.dureeSeance=1;
    }

    public MatierePlan(Integer id, String nomMatiere, Integer limiteClasseEnParallele) {
        this.id = id;
        this.nomMatiere = nomMatiere;
        this.limiteClasseEnParallele = limiteClasseEnParallele;
    }

    public MatierePlan(Integer id, String nomMatiere, Integer limiteClasseEnParallele , Integer dureeSeance) {
        this.id = id;
        this.nomMatiere = nomMatiere;
        this.limiteClasseEnParallele = limiteClasseEnParallele;
        this.dureeSeance = dureeSeance;
    }
    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getNomMatiere() 
    {
        return this.nomMatiere;
    }

    public void setNomMatiere(String nomMatiere) 
    {
        this.nomMatiere = nomMatiere;
    }

    public Integer getLimiteClasseEnParallele()
    {
        return this.limiteClasseEnParallele;
    }

    public Integer getDureeSeance()
    {
        return this.dureeSeance;
    }

    public void setLimiteClasseEnParallele(Integer limiteClasseEnParallele)
    {
        this.limiteClasseEnParallele = limiteClasseEnParallele;
    }
}
