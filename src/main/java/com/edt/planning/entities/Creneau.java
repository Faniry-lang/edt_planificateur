package com.edt.planning.entities;

public class Creneau {
    JourPlan jour;
    HeurePlan heure;

    public Creneau(JourPlan jour, HeurePlan heure) {
        this.jour = jour;
        this.heure = heure;
    }

    public JourPlan getJour()
    {
        return this.jour;
    }

    public void setJour(JourPlan jour)
    {
        this.jour = jour;
    }

    public HeurePlan getHeure()
    {
        return this.heure;
    }

    public void setHeure(HeurePlan heure)
    {
        this.heure = heure;
    }
}
