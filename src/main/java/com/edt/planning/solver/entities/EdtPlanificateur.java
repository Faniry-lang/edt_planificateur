package com.edt.planning.solver.entities;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.edt.planning.entities.*;

@PlanningSolution
public class EdtPlanificateur {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "creneauRange")
    private List<Creneau> creneaux;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "profRange")
    private List<ProfPlan> profs;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "matiereRange")
    private List<MatierePlan> matieres;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "classeRange")
    private List<ClassePlan> classes;

    @ProblemFactCollectionProperty
    private List<DisponibiliteProf> disponibiliteProfs;

    @ProblemFactCollectionProperty
    private List<HeurePlan> heures;

    @ProblemFactCollectionProperty
    private List<JourPlan> jours;

    @ProblemFactCollectionProperty
    private List<LevelPlan> levels;

    @ProblemFactCollectionProperty
    private List<MatiereBaseSpePlan> matiereBaseSpePlans;

    @ProblemFactCollectionProperty
    private List<SpePlan> spes;

    @ProblemFactCollectionProperty
    private List<VolumeHorairePlan> volumeHoraires;

    @ProblemFactCollectionProperty
    private List<MatiereProfPlan> matiereProfs;

    @ProblemFactCollectionProperty
    private List<ClasseProfPlan> classeProfs;

    @PlanningEntityCollectionProperty
    private List<Cours> coursList;

    @PlanningScore
    private HardSoftScore score;

    public EdtPlanificateur() {}


    public List<Creneau> getCreneaux() {
        return creneaux;
    }

    public void setCreneaux(List<Creneau> creneaux) {
        this.creneaux = creneaux;
    }

    public List<ProfPlan> getProfs() {
        return profs;
    }

    public void setProfs(List<ProfPlan> profs) {
        this.profs = profs;
    }

    public List<MatierePlan> getMatieres() {
        return matieres;
    }

    public void setMatieres(List<MatierePlan> matieres) {
        this.matieres = matieres;
    }

    public List<ClassePlan> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassePlan> classes) {
        this.classes = classes;
    }

    public List<DisponibiliteProf> getDisponibiliteProfs() {
        return disponibiliteProfs;
    }

    public void setDisponibiliteProfs(List<DisponibiliteProf> disponibiliteProfs) {
        this.disponibiliteProfs = disponibiliteProfs;
    }

    public List<HeurePlan> getHeures() {
        return heures;
    }

    public void setHeures(List<HeurePlan> heures) {
        this.heures = heures;
    }

    public List<JourPlan> getJours() {
        return jours;
    }

    public void setJours(List<JourPlan> jours) {
        this.jours = jours;
    }

    public List<LevelPlan> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelPlan> levels) {
        this.levels = levels;
    }

    public List<MatiereBaseSpePlan> getMatiereBaseSpePlans() {
        return matiereBaseSpePlans;
    }

    public void setMatiereBaseSpePlans(List<MatiereBaseSpePlan> matiereBaseSpePlans) {
        this.matiereBaseSpePlans = matiereBaseSpePlans;
    }

    public List<SpePlan> getSpes() {
        return spes;
    }

    public void setSpes(List<SpePlan> spes) {
        this.spes = spes;
    }

    public List<VolumeHorairePlan> getVolumeHoraires() {
        return volumeHoraires;
    }

    public void setVolumeHoraires(List<VolumeHorairePlan> volumeHoraires) {
        this.volumeHoraires = volumeHoraires;
    }

    public List<Cours> getCoursList() {
        return coursList;
    }

    public void setCoursList(List<Cours> coursList) {
        this.coursList = coursList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    public List<MatiereProfPlan> getMatiereProfs() {
        return matiereProfs;
    }

    public void setMatiereProfs(List<MatiereProfPlan> matiereProfs) {
        this.matiereProfs = matiereProfs;
    }

    public List<ClasseProfPlan> getClasseProfs() {
        return classeProfs;
    }

    public void setClasseProfs(List<ClasseProfPlan> classeProfs) {
        this.classeProfs = classeProfs;
    }
}