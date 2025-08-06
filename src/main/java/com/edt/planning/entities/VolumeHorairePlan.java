package com.edt.planning.entities;

public class VolumeHorairePlan {
    private Double heureMax;
    private MatierePlan matierePlan;
    private LevelPlan levelPlan;
    private SpePlan spePlan;

    public VolumeHorairePlan() {}

    public Double getHeureMax() {
        return heureMax;
    }

    public void setHeureMax(Double heureMax) {
        this.heureMax = heureMax;
    }

    public MatierePlan getMatierePlan() {
        return matierePlan;
    }

    public void setMatierePlan(MatierePlan matierePlan) {
        this.matierePlan = matierePlan;
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
}
