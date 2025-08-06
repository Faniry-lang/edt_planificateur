package com.edt.planning.entities;

public class ClasseProfPlan {
    ClassePlan classe;
    ProfPlan prof;

    public ClasseProfPlan() {}

    public ClassePlan getClasse()
    {
        return this.classe;
    }

    public void setClasse(ClassePlan classe) 
    {
        this.classe = classe;
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
