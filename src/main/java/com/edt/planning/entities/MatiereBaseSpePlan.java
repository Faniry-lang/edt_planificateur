package com.edt.planning.entities;

public class MatiereBaseSpePlan {
    MatierePlan matierePlan;
    SpePlan spePlan;

    public MatiereBaseSpePlan(MatierePlan matierePlan, SpePlan spePlan) {
        this.matierePlan = matierePlan;
        this.spePlan = spePlan;
    }

    public MatierePlan getMatierePlan()
    {
        return this.matierePlan;
    }

    public void setMatierePlan(MatierePlan matierePlan)
    {
        this.matierePlan = matierePlan;
    }

    public SpePlan getSpePlan()
    {
        return this.spePlan;
    }

    public void setSpePlan(SpePlan spePlan)
    {
        this.spePlan = spePlan;
    }
}
