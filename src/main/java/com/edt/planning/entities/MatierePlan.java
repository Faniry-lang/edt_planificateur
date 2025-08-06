package com.edt.planning.entities;

public class MatierePlan {
    Integer id;
    String nomMatiere;
    Integer limiteClasseEnParallele;

    public MatierePlan(Integer id, String nomMatiere) {
        this.id = id;
        this.nomMatiere = nomMatiere;
        this.limiteClasseEnParallele = 0;
    }

    public MatierePlan(Integer id, String nomMatiere, Integer limiteClasseEnParallele) {
        this.id = id;
        this.nomMatiere = nomMatiere;
        this.limiteClasseEnParallele = limiteClasseEnParallele;
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

    public void setLimiteClasseEnParallele(Integer limiteClasseEnParallele)
    {
        this.limiteClasseEnParallele = limiteClasseEnParallele;
    }
}
