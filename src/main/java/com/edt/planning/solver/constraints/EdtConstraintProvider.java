package com.edt.planning.solver.constraints;

import com.edt.planning.entities.ClasseProfPlan;
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

public class EdtConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
                return new Constraint[]{
                        profTientCetteClasse(constraintFactory),
                        profEnseignantMatiere(constraintFactory),
                        chevauchementEdtProf(constraintFactory),
                        chevauchementEdtClasse(constraintFactory),
                        pasDeCoursMercrediAprem(constraintFactory),
                        // pasDeClasseEnParallèlePourLaMatièreInfo(constraintFactory),
                        disponibiliteProf(constraintFactory),
                        matieresDeBaseConsecutives(constraintFactory),
                        depassementDeLimiteDeClasseParalleleParMatiere(constraintFactory)
                };
        }

        private Constraint profTientCetteClasse(ConstraintFactory constraintFactory)
        {
                return constraintFactory
                        .forEach(Cours.class)
                        .ifExists(ClasseProfPlan.class,
                                Joiners.equal(Cours::getProf, ClasseProfPlan::getProf),
                                Joiners.equal(Cours::getClasse, ClasseProfPlan::getClasse)
                        ).penalize("Prof ne tient pas cette classe", ONE_HARD);
        }

        private Constraint profEnseignantMatiere(ConstraintFactory constraintFactory)
        {
                return constraintFactory
                        .forEach(Cours.class)
                        .ifExists(MatiereProfPlan.class, 
                                Joiners.equal(Cours::getProf, MatiereProfPlan::getProf),
                                Joiners.equal(Cours::getMatiere, MatiereProfPlan::getMatiere)
                        ).penalize("Prof n'enseigne pas cette matière", ONE_HARD);
        }

        private Constraint chevauchementEdtProf(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEachUniquePair(Cours.class,
                                Joiners.equal(Cours::getProf),
                                Joiners.equal(Cours::getCreneau))
                        .penalize("Conflit d'horaire pour un professeur", ONE_HARD);
        }

        private Constraint chevauchementEdtClasse(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEachUniquePair(Cours.class,
                                Joiners.equal(Cours::getClasse),
                                Joiners.equal(Cours::getCreneau))
                        .penalize("Conflit d'horaire pour une classe", ONE_HARD);
        }

        private Constraint pasDeCoursMercrediAprem(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Cours.class)
                        .filter(cours ->
                                "Mercredi".equalsIgnoreCase(cours.getCreneau().getJour().getNomJour()) &&
                                cours.getCreneau().getHeure().getHeure().getHour() >= 12)
                        .penalize("Cours le mercredi après-midi", ONE_HARD);
        }

        /* 
        private Constraint pasDeClasseEnParallèlePourLaMatièreInfo(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEachUniquePair(Cours.class,
                                Joiners.equal(Cours::getMatiere),
                                Joiners.equal(Cours::getCreneau))
                        .filter((c1, c2) ->
                                "Informatique".equalsIgnoreCase(c1.getMatiere().getNomMatiere()))
                        .penalize("Cours d'informatique en parallèle", ONE_HARD);
        }
        */

        private Constraint disponibiliteProf(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Cours.class)
                        .ifNotExists(DisponibiliteProf.class,
                                Joiners.equal(Cours::getProf, DisponibiliteProf::getProf),
                                Joiners.equal(Cours::getCreneau, DisponibiliteProf::getCreneau))
                        .penalize("Professeur non disponible", ONE_HARD);
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
                                LocalTime t1 = c1.getCreneau().getHeure().getHeure();
                                LocalTime t2 = c2.getCreneau().getHeure().getHeure();
                                return t1.plusHours(1).equals(t2) || t2.plusHours(1).equals(t1);
                        })

                        .ifExists(MatiereBaseSpePlan.class,
                                Joiners.equal((c1, c2) -> c2.getMatiere(), MatiereBaseSpePlan::getMatierePlan),
                                Joiners.equal((c1, c2) -> c2.getClasse().getSpePlan(), MatiereBaseSpePlan::getSpePlan))

                        .penalize("Deux matières de base se suivent pour une classe", ONE_HARD);

        }

        private Constraint depassementDeLimiteDeClasseParalleleParMatiere(ConstraintFactory constraintFactory) {
                return constraintFactory
                        .forEach(Cours.class)
                        .groupBy(
                                c -> c.getMatiere(),
                                c -> c.getCreneau(),
                                ConstraintCollectors.count()
                        ).filter((matiere, creneau, count) -> 
                                matiere.getLimiteClasseEnParallele() != null &&
                                matiere.getLimiteClasseEnParallele() > 0 &&
                                count > matiere.getLimiteClasseEnParallele()
                        )
                        .penalize("Trop de classes en parallèles pour cette matière à ce créneau", ONE_HARD);
        }

}
