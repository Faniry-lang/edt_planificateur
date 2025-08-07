package com.edt.planning.solver.constraints;

import com.edt.planning.entities.ClasseProfPlan;
import com.edt.planning.entities.DisponibiliteProf;
import com.edt.planning.entities.MatiereBaseSpePlan;
import com.edt.planning.entities.MatiereProfPlan;
import com.edt.planning.solver.entities.Cours;

import org.drools.tms.beliefsystem.defeasible.Join;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ONE_HARD;
import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ONE_SOFT;

public class EdtConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            // Contraintes HARD essentielles
            profTientCetteClasse(constraintFactory),
            profEnseignantMatiere(constraintFactory),
            chevauchementEdtProf(constraintFactory),
            chevauchementEdtClasse(constraintFactory),
            pasDeCoursMercrediAprem(constraintFactory),
            disponibiliteProf(constraintFactory),
            matieresDeBaseConsecutives(constraintFactory),
            depassementDeLimiteDeClasseParalleleParMatiere(constraintFactory),
            
            // Contraintes HARD pour la gestion des blocs
            coursConsecutifsMemeMatiere(constraintFactory),
            
            // Contraintes SOFT pour optimiser les regroupements
            favoriserTailleBlocPreferee(constraintFactory),
            penaliserCoursIsoles(constraintFactory),
            favoriserBlocContinuParfait(constraintFactory)

        //     encouragerRegroupementGeneral(constraintFactory)
    
                };
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
                        ).penalize("Prof n'enseigne pas cette matière",
                                HardSoftScore.ofHard(10000)); // PÉNALITÉ MAXIMALE - INVIOLABLE
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
            // Vérifier que le premier cours est une matière de base
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

            // Vérifier que le second cours est aussi une matière de base
            .ifExists(MatiereBaseSpePlan.class,
                    Joiners.equal((c1, c2) -> c2.getMatiere(), MatiereBaseSpePlan::getMatierePlan),
                    Joiners.equal((c1, c2) -> c2.getClasse().getSpePlan(), MatiereBaseSpePlan::getSpePlan))

            // NOUVELLE CONDITION : pénaliser seulement si les matières sont DIFFÉRENTES
            .filter((c1, c2) -> !c1.getMatiere().equals(c2.getMatiere()))

            .penalize("Deux matières de base différentes se suivent pour une classe", ONE_HARD);
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

        /**
 * Contrainte HARD : tous les cours d'une même matière dans une journée doivent être consécutifs
 */
private Constraint coursConsecutifsMemeMatiere(ConstraintFactory constraintFactory) {
    return constraintFactory
            .forEach(Cours.class)
            .groupBy(
                    c -> c.getClasse(),
                    c -> c.getMatiere(),
                    c -> c.getCreneau().getJour(),
                    ConstraintCollectors.toList()
            )
            .filter((classe, matiere, jour, coursDuJour) -> {
                if (coursDuJour.size() <= 1) return false; // Un seul cours, OK

                // Trier les cours par heure
                coursDuJour.sort((c1, c2) ->
                        c1.getCreneau().getHeure().getHeure().compareTo(
                                c2.getCreneau().getHeure().getHeure()));

                // Vérifier que tous les cours sont consécutifs
                for (int i = 0; i < coursDuJour.size() - 1; i++) {
                    LocalTime h1 = coursDuJour.get(i).getCreneau().getHeure().getHeure();
                    LocalTime h2 = coursDuJour.get(i + 1).getCreneau().getHeure().getHeure();
                    if (!h1.plusHours(1).equals(h2)) {
                        return true; // Cours non consécutifs = pénalité
                    }
                }
                return false; // Tous consécutifs = OK
            })
            .penalize("Cours de même matière non consécutifs dans la journée",
                    HardSoftScore.ofHard(1000));
}

/**
 * Contrainte SOFT : favorise les blocs de la taille dureeSeance préférée
 */
private Constraint favoriserTailleBlocPreferee(ConstraintFactory constraintFactory) {
    return constraintFactory
            .forEach(Cours.class)
            .filter(cours -> cours.getMatiere().getDureeSeance() != null &&
                    cours.getMatiere().getDureeSeance() > 1)
            .groupBy(
                    c -> c.getClasse(),
                    c -> c.getMatiere(),
                    c -> c.getCreneau().getJour(),
                    ConstraintCollectors.toList()
            )
            .filter((classe, matiere, jour, coursDuJour) -> coursDuJour.size() > 1)
            .reward("Bonus pour respect de la durée de séance préférée", HardSoftScore.ONE_SOFT,
                    (classe, matiere, jour, coursDuJour) -> {
                        int dureeSeancePreferee = matiere.getDureeSeance();
                        int totalCours = coursDuJour.size();
                        int bonus = 0;

                        int blocsParfaits = totalCours / dureeSeancePreferee;
                        int coursRestants = totalCours % dureeSeancePreferee;

                        bonus += blocsParfaits * dureeSeancePreferee * 100;

                        if (coursRestants > 1) {
                            bonus += coursRestants * 50;
                        } else if (coursRestants == 1) {
                            bonus += 10;
                        }

                        return bonus;
                    });
}


/**
 * Contrainte SOFT : pénalise les cours isolés (encourage les regroupements)
 */
private Constraint penaliserCoursIsoles(ConstraintFactory constraintFactory) {
    return constraintFactory
            .forEach(Cours.class)
            .groupBy(
                    c -> c.getClasse(),
                    c -> c.getMatiere(),
                    c -> c.getCreneau().getJour(),
                    ConstraintCollectors.count()
            )
            .filter((classe, matiere, jour, count) -> count == 1)
            .penalize("Cours isolé (non regroupé)", HardSoftScore.ofSoft(20));
}

/**
 * Contrainte SOFT : bonus supplémentaire pour regroupement même sans dureeSeance spécifiée
 */
private Constraint encouragerRegroupementGeneral(ConstraintFactory constraintFactory) {
    return constraintFactory
            .forEach(Cours.class)
            .join(Cours.class,
                    Joiners.equal(Cours::getClasse),
                    Joiners.equal(Cours::getMatiere),
                    Joiners.equal(c -> c.getCreneau().getJour()))
            .filter((c1, c2) -> {
                if (c1.equals(c2)) return false;
                LocalTime h1 = c1.getCreneau().getHeure().getHeure();
                LocalTime h2 = c2.getCreneau().getHeure().getHeure();
                return h1.plusHours(1).equals(h2) || h2.plusHours(1).equals(h1);
            })
            .reward("Cours consécutifs de même matière", HardSoftScore.ofSoft(30));
}

private Constraint favoriserBlocContinuParfait(ConstraintFactory constraintFactory) {
    return constraintFactory
            .forEach(Cours.class)
            .filter(c -> c.getCreneau() != null) // on évite les cours non placés
            .groupBy(
                    c -> c.getClasse(),
                    c -> c.getMatiere(),
                    c -> c.getCreneau().getJour(),
                    ConstraintCollectors.toList()
            )
            .reward("Bloc parfait respecté", HardSoftScore.ofHard(5000),
                    (classe, matiere, jour, coursList) -> {
                        Integer dureePref = matiere.getDureeSeance();
                        if (dureePref == null || dureePref < 2) return 0;

                        // Trier les cours du jour par heure
                        List<Integer> heures = coursList.stream()
                                .map(c -> c.getCreneau().getHeure().getId()) // suppose que tu as un index pour chaque heure
                                .sorted()
                                .collect(Collectors.toList());

                        int maxBloc = 1;
                        int currentBloc = 1;

                        for (int i = 1; i < heures.size(); i++) {
                            if (heures.get(i) == heures.get(i - 1) + 1) {
                                currentBloc++;
                                maxBloc = Math.max(maxBloc, currentBloc);
                            } else {
                                currentBloc = 1;
                            }
                        }

                        // Si un bloc consécutif a exactement la bonne taille
                        return (maxBloc == dureePref) ? 1 : 0;
                    });
}



}