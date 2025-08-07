package com.edt.planning.entities;

public class ClassePlan {
    private Integer id;
    private Integer numeroClasse;
    private LevelPlan levelPlan;
    private SpePlan spePlan;

    public ClassePlan(Integer id, Integer numeroClasse, LevelPlan levelPlan, SpePlan spePlan) {
        this.id = id;
        this.numeroClasse = numeroClasse;
        this.levelPlan = levelPlan;
        this.spePlan = spePlan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroClasse() {
        return numeroClasse;
    }

    public void setNumeroClasse(Integer numeroClasse) {
        this.numeroClasse = numeroClasse;
    }

    public LevelPlan getLevelPlan() {
        return levelPlan;
    }

    public void setLevelPlan(LevelPlan levelPlan) {
        this.levelPlan = levelPlan;
    }

    public SpePlan getSpePlan() {
        return spePlan;
    }

    public void setSpePlan(SpePlan spePlan) {
        this.spePlan = spePlan;
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
