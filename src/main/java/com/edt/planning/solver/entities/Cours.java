package com.edt.planning.solver.entities;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.edt.planning.entities.*;

@PlanningEntity
public class Cours {
    @PlanningId
    Long id;

    @PlanningVariable(valueRangeProviderRefs = "creneauRange")
    Creneau creneau;

    @PlanningVariable(valueRangeProviderRefs = "profRange")
    ProfPlan prof;

    private MatierePlan matiere;

    private ClassePlan classe;

    public Cours(Long id, MatierePlan matiere, ClassePlan classe) {
        this.id = id;
        this.matiere = matiere;
        this.classe = classe;
    }

    public Cours() {}

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public MatierePlan getMatiere()
    {
        return this.matiere;
    }

    public void setMatiere(MatierePlan matiere)
    {
        this.matiere = matiere;
    }

    public ClassePlan getClasse()
    {
        return this.classe;
    }

    public void setClasse(ClassePlan classe)
    {
        this.classe = classe;
    }
}
