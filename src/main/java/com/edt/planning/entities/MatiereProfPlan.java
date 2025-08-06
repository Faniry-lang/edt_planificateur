package com.edt.planning.entities;

public class MatiereProfPlan {
    MatierePlan matiere;
    ProfPlan prof;

    public MatiereProfPlan(MatierePlan matiere, ProfPlan prof) {
        this.matiere = matiere;
        this.prof = prof;
    }

    public MatierePlan getMatiere()
    {
        return this.matiere;
    }

    public void setMatiere(MatierePlan matiere)
    {
        this.matiere = matiere;
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
