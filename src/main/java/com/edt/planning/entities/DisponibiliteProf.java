package com.edt.planning.entities;

public class DisponibiliteProf {
    Creneau creneau;
    ProfPlan prof;

    public DisponibiliteProf(Creneau creneau, ProfPlan prof) {
        this.creneau = creneau;
        this.prof = prof;
    }

    public Creneau getCreneau()
    {
        return this.creneau;
    }

    public void setCreneau(Creneau creneau)
    {
        this.creneau = creneau;
    }

    public ProfPlan getProf()
    {
        return this.prof;
    }

    public void setProf(ProfPlan prof)
    {
        this.prof = prof;
    }
}
