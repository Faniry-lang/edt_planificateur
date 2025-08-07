package com.edt.planning.solver.constraints;

import com.edt.planning.entities.ClasseProfPlan;
import com.edt.planning.entities.Creneau;
import com.edt.planning.entities.DisponibiliteProf;
import com.edt.planning.entities.MatiereBaseSpePlan;
import com.edt.planning.entities.MatiereProfPlan;
import com.edt.planning.solver.entities.Cours;

import org.drools.tms.beliefsystem.defeasible.Join;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.time.LocalTime;

import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ONE_HARD;
import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ONE_SOFT;;

public class EdtConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
                return new Constraint[]{
                        profTientCetteClasse(constraintFactory),
                        profEnseignantMatiere(constraintFactory),
                        chevauchementEdtProf(constraintFactory),
                        chevauchementEdtClasse(constraintFactory),
                        disponibiliteProf(constraintFactory),
                        matieresDeBaseConsecutives(constraintFactory),
                        depassementDeLimiteDeClasseParalleleParMatiere(constraintFactory),
                        creneauDepasseHeureDeSortie(constraintFactory),
                        //pasDeTrouDansEmploiDuTemps(constraintFactory),
                };
        }

        /* 
        private Constraint pasDeTrouDansEmploiDuTemps(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Cours.class)
                        .groupBy(cours -> cours.getClasse(), cours -> cours.getCreneau().getJour(), ConstraintCollectors.toList())
                        .filter((classe, jour, coursList) -> {
                                if (coursList.size() < 2) {
                                        return false; 
                                }
                                coursList.sort((c1, c2) -> c1.getCreneau().getHeure().getHeure().compareTo(c2.getCreneau().getHeure().getHeure()));
                                for (int i = 0; i < coursList.size() - 1; i++) {
                                        Cours currentCours = coursList.get(i);
                                        Cours nextCours = coursList.get(i + 1);
                                        if (currentCours.getFinHeure() != null && nextCours.getCreneau() != null && nextCours.getCreneau().getHeure() != null && nextCours.getCreneau().getHeure().getHeure() != null) {
                                                if (currentCours.getFinHeure().isBefore(nextCours.getCreneau().getHeure().getHeure())) {
                                                        return true; 
                                                }
                                        }
                                }
                                return false;
                        })
                        .penalize("Trou dans l'emploi du temps", ONE_SOFT);
        }*/

        private Constraint creneauDepasseHeureDeSortie(ConstraintFactory constraintFactory) {
                LocalTime heureDeSortieMatin = LocalTime.of(11, 0); 
                LocalTime heureDeSortieAprem = LocalTime.of(16, 0); 
                return constraintFactory
                        .forEach(Cours.class)
                        .filter(c -> {
                                if(
                                        (
                                                c.getCreneau().getHeure().getHeure().isBefore(heureDeSortieMatin) && 
                                                c.getFinHeure().isAfter(heureDeSortieMatin)
                                        ) || 
                                        (
                                                c.getCreneau().getHeure().getHeure().isBefore(heureDeSortieAprem) && 
                                                c.getFinHeure().isAfter(heureDeSortieAprem)
      
                                        )
                                ) {
                                        return true;
                                }
                                return false;
                        })
                        .penalize("Le cours dépasse l'heure de sortie", ONE_HARD);
        }

        private Constraint profTientCetteClasse(ConstraintFactory constraintFactory)
        {
                return constraintFactory
                        .forEach(Cours.class)
                        .ifNotExists(ClasseProfPlan.class,
                                Joiners.equal(Cours::getProf, ClasseProfPlan::getProf),
                                Joiners.equal(Cours::getClasse, ClasseProfPlan::getClasse)
                        ).penalize("Prof ne tient pas cette classe", ONE_HARD);
        }

        private Constraint profEnseignantMatiere(ConstraintFactory constraintFactory)
        {
                return constraintFactory
                        .forEach(Cours.class)
                        .ifNotExists(MatiereProfPlan.class, 
                                Joiners.equal(Cours::getProf, MatiereProfPlan::getProf),
                                Joiners.equal(Cours::getMatiere, MatiereProfPlan::getMatiere)
                        ).penalize("Prof n'enseigne pas cette matière", ONE_HARD);
        }

        private Constraint chevauchementEdtProf(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEachUniquePair(Cours.class,
                                Joiners.equal(Cours::getProf),
                                Joiners.equal(cours -> cours.getCreneau().getJour()))
                        .filter((cours1, cours2) -> doCoursesOverlap(cours1, cours2))
                        .penalize("Conflit d'horaire pour un professeur", ONE_HARD);
        }

        private Constraint chevauchementEdtClasse(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEachUniquePair(Cours.class,
                                Joiners.equal(Cours::getClasse),
                                Joiners.equal(cours -> cours.getCreneau().getJour()))
                        .filter((cours1, cours2) -> doCoursesOverlap(cours1, cours2))
                        .penalize("Conflit d'horaire pour une classe", ONE_HARD);
        }

        private boolean doCoursesOverlap(Cours cours1, Cours cours2) {
                if (!cours1.getCreneau().getJour().equals(cours2.getCreneau().getJour())) {
                        return false;
                }

                LocalTime start1 = cours1.getCreneau().getHeure().getHeure();
                LocalTime end1 = cours1.getFinHeure();
                LocalTime start2 = cours2.getCreneau().getHeure().getHeure();
                LocalTime end2 = cours2.getFinHeure();

                return start1.isBefore(end2) && start2.isBefore(end1);
        }

        private Constraint disponibiliteProf(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Cours.class)
                        .join(DisponibiliteProf.class,
                                Joiners.equal(Cours::getProf, DisponibiliteProf::getProf),
                                Joiners.equal(cours -> cours.getCreneau().getJour(), dispo -> dispo.getCreneau().getJour()))
                        .filter((cours, dispo) -> {
                                LocalTime courseStart = cours.getCreneau().getHeure().getHeure();
                                LocalTime courseEnd = cours.getFinHeure();
                                LocalTime dispoHour = dispo.getCreneau().getHeure().getHeure();
                                return dispoHour.isAfter(courseStart.minusNanos(1)) && dispoHour.isBefore(courseEnd);
                        })
                        .groupBy(
                                (cours, dispo) -> cours,          
                                ConstraintCollectors.countBi()    
                        )
                        .filter((cours, count) -> count < cours.getDuree())
                        .penalize("Professeur non disponible pour toute la durée du cours", ONE_HARD);
        }

        private Constraint matieresDeBaseConsecutives(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Cours.class)
                        
                        .ifExists(MatiereBaseSpePlan.class,
                                Joiners.equal(Cours::getMatiere, MatiereBaseSpePlan::getMatierePlan),
                                Joiners.equal(c -> c.getClasse().getSpePlan(), MatiereBaseSpePlan::getSpePlan))

                        .join(Cours.class,
                                Joiners.equal(Cours::getClasse),
                                Joiners.equal(c -> c.getCreneau().getJour(), c -> c.getCreneau().getJour()))

                        .filter((c1, c2) -> {
                                LocalTime start1 = c1.getCreneau().getHeure().getHeure();
                                LocalTime end1 = c1.getFinHeure();
                                LocalTime start2 = c2.getCreneau().getHeure().getHeure();
                                LocalTime end2 = c2.getFinHeure();
                                return end1.equals(start2) || end2.equals(start1);
                        })

                        .ifExists(MatiereBaseSpePlan.class,
                                Joiners.equal((c1, c2) -> c2.getMatiere(), MatiereBaseSpePlan::getMatierePlan),
                                Joiners.equal((c1, c2) -> c2.getClasse().getSpePlan(), MatiereBaseSpePlan::getSpePlan))

                        .penalize("Deux matières de base se suivent pour une classe", ONE_HARD);

        }

        private Constraint depassementDeLimiteDeClasseParalleleParMatiere(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Creneau.class) 
                        .join(Cours.class,
                                Joiners.equal(Creneau::getJour, cours -> cours.getCreneau().getJour())) 
                        .filter((creneau, cours) -> {
                                LocalTime slotStart = creneau.getHeure().getHeure();
                                LocalTime courseStart = cours.getCreneau().getHeure().getHeure();
                                LocalTime courseEnd = cours.getFinHeure();
                                return !slotStart.isBefore(courseStart) && slotStart.isBefore(courseEnd);
                        })
                        .groupBy(
                                (creneau, cours) -> cours.getMatiere(), 
                                (creneau, cours) -> creneau,             
                                ConstraintCollectors.countBi()           
                        )
                        .filter((matiere, creneau, count) ->
                                matiere.getLimiteClasseEnParallele() != null &&
                                matiere.getLimiteClasseEnParallele() > 0 &&
                                count > matiere.getLimiteClasseEnParallele()
                        )
                        .penalize("Trop de classes en parallèles pour cette matière à ce créneau", ONE_HARD);
        }

}
