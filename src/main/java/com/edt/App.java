package com.edt;

import com.edt.export.PdfExporter;
import com.edt.planning.entities.*;
import com.edt.planning.solver.constraints.EdtConstraintProvider;
import com.edt.planning.solver.entities.Cours;
import com.edt.planning.solver.entities.EdtPlanificateur;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
        SolverFactory<EdtPlanificateur> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(EdtPlanificateur.class)
                .withEntityClasses(Cours.class)
                .withConstraintProviderClass(EdtConstraintProvider.class)
                .withTerminationConfig(new TerminationConfig().withSecondsSpentLimit(60L))
        );

        EdtPlanificateur problem = createDemoData();
        Solver<EdtPlanificateur> solver = solverFactory.buildSolver();
        EdtPlanificateur solution = solver.solve(problem);

        PdfExporter.export(solution, "edt.pdf");
        printSchedule(solution);
        printScheduleParProf(solution);
    }

    private static EdtPlanificateur createDemoData() {
        // 1. Jours de la semaine
        JourPlan lundi = new JourPlan(1, "Lundi");
        JourPlan mardi = new JourPlan(2, "Mardi");
        JourPlan mercredi = new JourPlan(3, "Mercredi");
        JourPlan jeudi = new JourPlan(4, "Jeudi");
        JourPlan vendredi = new JourPlan(5, "Vendredi");
        List<JourPlan> jours = Arrays.asList(lundi, mardi, mercredi, jeudi, vendredi);

        // 2. Heures de cours
        HeurePlan h8 = new HeurePlan(1, "08:00", LocalTime.of(8, 0));
        HeurePlan h9 = new HeurePlan(2, "09:00", LocalTime.of(9, 0));
        HeurePlan h10 = new HeurePlan(3, "10:00", LocalTime.of(10, 0));
        HeurePlan h11 = new HeurePlan(4, "11:00", LocalTime.of(11, 0));
        HeurePlan h13 = new HeurePlan(5, "13:00", LocalTime.of(13, 0));
        HeurePlan h14 = new HeurePlan(6, "14:00", LocalTime.of(14, 0));
        HeurePlan h15 = new HeurePlan(7, "15:00", LocalTime.of(15, 0));
        HeurePlan h16 = new HeurePlan(8, "16:00", LocalTime.of(16, 0));
        List<HeurePlan> heures = Arrays.asList(h8, h9, h10, h11, h13, h14, h15, h16);

        // 3. Créneaux (combinaison de jours et heures)
        List<Creneau> creneaux = new ArrayList<>();
        for (JourPlan jour : jours) {
            for (HeurePlan heure : heures) {
                creneaux.add(new Creneau(jour, heure));
            }
        }

        // 4. Niveaux et Spécialisations
        LevelPlan seconde = new LevelPlan(1, "Seconde");
        LevelPlan premiere = new LevelPlan(2, "Première");
        LevelPlan terminale = new LevelPlan(3, "Terminale");
        List<LevelPlan> levels = Arrays.asList(seconde, premiere, terminale);

        SpePlan scientifique = new SpePlan(1, "Scientifique");
        SpePlan litteraire = new SpePlan(2, "Littéraire");
        List<SpePlan> spes = Arrays.asList(scientifique, litteraire);

        // 5. Classes
        ClassePlan secondeA = new ClassePlan(1, 101, seconde, scientifique);
        ClassePlan secondeB = new ClassePlan(2, 102, seconde, scientifique);
        ClassePlan premiereL = new ClassePlan(3, 201, premiere, litteraire);
        ClassePlan terminaleS = new ClassePlan(4, 301, terminale, scientifique);
        List<ClassePlan> classes = Arrays.asList(secondeA, secondeB, premiereL, terminaleS);

        // 6. Matières
        // Matières avec durées spécifiées
        MatierePlan maths = new MatierePlan(1, "Maths", 0, 3);  // 2h consécutives
        MatierePlan physique = new MatierePlan(2, "Physique", 0, 2); // 2h consécutives
        MatierePlan francais = new MatierePlan(3, "Français", 0, 2); // 2h consécutives
        MatierePlan histoire = new MatierePlan(4, "Histoire", 0, 1); // 1h
        MatierePlan info = new MatierePlan(5, "Informatique", 1, 1); // 1h
        MatierePlan eps = new MatierePlan(6, "EPS", 2, 2); // 2h consécutives
        List<MatierePlan> matieres = Arrays.asList(maths, physique, francais, histoire, info, eps);

        // 7. Matières de base par spécialisation
        List<MatiereBaseSpePlan> matieresDeBase = Arrays.asList(
            new MatiereBaseSpePlan(maths, scientifique),
            new MatiereBaseSpePlan(physique, scientifique),
            new MatiereBaseSpePlan(francais, litteraire),
            new MatiereBaseSpePlan(histoire, litteraire)
        );

        // 8. Professeurs
        ProfPlan profA = new ProfPlan(1, "Prof A (Maths/Physique)");
        ProfPlan profB = new ProfPlan(2, "Prof B (Français/Histoire)");
        ProfPlan profC = new ProfPlan(3, "Prof C (Informatique)");
        ProfPlan profD = new ProfPlan(4, "Prof D (EPS)");
        List<ProfPlan> profs = Arrays.asList(profA, profB, profC, profD);

        // 9. Association Profs -> Matières
        List<MatiereProfPlan> profMatieres = Arrays.asList(
            new MatiereProfPlan(maths, profA),
            new MatiereProfPlan(physique, profA),
            new MatiereProfPlan(francais, profB),
            new MatiereProfPlan(histoire, profB),
            new MatiereProfPlan(info, profC),
            new MatiereProfPlan(eps, profD)
        );


        // 10. Association Profs -> Classes (laisser vide pour laisser le solveur décider)
        List<ClasseProfPlan> classeProfs = new ArrayList<>();

        // 11. Disponibilités des Profs - BEAUCOUP PLUS RÉALISTES
        List<DisponibiliteProf> disponibiliteProfs = new ArrayList<>();

        // Prof A (Maths/Physique) - disponible tous les jours sauf mercredi après-midi
        for (JourPlan jour : jours) {
            for (HeurePlan heure : heures) {
                // Pas de cours mercredi après-midi
                if (jour.equals(mercredi) && heure.getHeure().getHour() >= 13) {
                    continue;
                }
                disponibiliteProfs.add(new DisponibiliteProf(new Creneau(jour, heure), profA));
            }
        }

        // Prof B (Français/Histoire) - disponible tous les jours sauf mercredi après-midi
        for (JourPlan jour : jours) {
            for (HeurePlan heure : heures) {
                // Pas de cours mercredi après-midi
                if (jour.equals(mercredi) && heure.getHeure().getHour() >= 13) {
                    continue;
                }
                disponibiliteProfs.add(new DisponibiliteProf(new Creneau(jour, heure), profB));
            }
        }

        // Prof C (Informatique) - disponible le matin principalement + quelques créneaux aprem
        for (JourPlan jour : jours) {
            // Matins (toujours dispo)
            for (HeurePlan heure : Arrays.asList(h8, h9, h10, h11)) {
                disponibiliteProfs.add(new DisponibiliteProf(new Creneau(jour, heure), profC));
            }
            // Quelques créneaux après-midi (pas mercredi)
            if (!jour.equals(mercredi)) {
                for (HeurePlan heure : Arrays.asList(h14, h15)) {
                    disponibiliteProfs.add(new DisponibiliteProf(new Creneau(jour, heure), profC));
                }
            }
        }

        // Prof D (EPS) - disponible partout sauf mercredi après-midi
        for (JourPlan jour : jours) {
            for (HeurePlan heure : heures) {
                // Pas de cours mercredi après-midi
                if (jour.equals(mercredi) && heure.getHeure().getHour() >= 13) {
                    continue;
                }
                disponibiliteProfs.add(new DisponibiliteProf(new Creneau(jour, heure), profD));
            }
        }


// 12. Volume Horaire - COURS INDIVIDUELS (regroupement automatique)
        List<Cours> coursList = new ArrayList<>();
        long coursIdCounter = 0;

        // Seconde A (Scientifique) - Créer chaque cours individuellement
        // 4h de maths par semaine = 4 cours d'1h (le système les regroupera en 2 blocs de 2h)
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, maths, secondeA));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, physique, secondeA));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, francais, secondeA));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, histoire, secondeA)); // dureeSeance=1
        for (int i = 0; i < 1; i++) coursList.add(new Cours(coursIdCounter++, info, secondeA));     // dureeSeance=1
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, secondeA));      // dureeSeance=1

        // Seconde B (Scientifique)
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, maths, secondeB));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, physique, secondeB));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, francais, secondeB));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, histoire, secondeB));
        for (int i = 0; i < 1; i++) coursList.add(new Cours(coursIdCounter++, info, secondeB));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, secondeB));

        // Premiere L (Littéraire)
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, maths, premiereL)); // dureeSeance=2
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, francais, premiereL)); // dureeSeance=2
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, histoire, premiereL)); // dureeSeance=1
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, premiereL));

        // Terminale S (Scientifique)
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, maths, terminaleS));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, physique, terminaleS));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, francais, terminaleS)); // dureeSeance=2
        for (int i = 0; i < 1; i++) coursList.add(new Cours(coursIdCounter++, info, terminaleS));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, terminaleS));

        // 13. Assemblage de la solution
        EdtPlanificateur problem = new EdtPlanificateur();
        problem.setJours(jours);
        problem.setHeures(heures);
        problem.setCreneaux(creneaux);
        problem.setClasses(classes);
        problem.setProfs(profs);
        problem.setMatieres(matieres);
        problem.setMatiereBaseSpePlans(matieresDeBase); // IMPORTANT: Cette ligne doit être présente
        problem.setCoursList(coursList);
        problem.setDisponibiliteProfs(disponibiliteProfs);
        problem.setLevels(levels);
        problem.setSpes(spes);
        problem.setMatiereProfs(profMatieres);
        problem.setClasseProfs(classeProfs);
        problem.setVolumeHoraires(new ArrayList<>());

        return problem;
    }

    private static void printSchedule(EdtPlanificateur solution) {
        System.out.println("========================= EMPLOI DU TEMPS FINAL ========================");
        List<ClassePlan> classes = solution.getClasses();
        List<Cours> coursList = solution.getCoursList();

        // Group courses by class for easier lookup
        Map<ClassePlan, List<Cours>> coursParClasse = coursList.stream()
                .filter(c -> c.getCreneau() != null && c.getProf() != null)
                .collect(Collectors.groupingBy(Cours::getClasse));

        for (ClassePlan classe : classes) {
            System.out.printf("\n--- Emploi du temps pour la classe: %s (%s - %s) ---\n",
                    classe.getNumeroClasse(), classe.getLevelPlan().getNomLevel(), classe.getSpePlan().getNomSpe());

            List<Cours> coursDeLaClasse = coursParClasse.getOrDefault(classe, new ArrayList<>());

            for (JourPlan jour : solution.getJours()) {
                List<Cours> coursJour = coursDeLaClasse.stream()
                        .filter(c -> c.getCreneau().getJour().equals(jour))
                        .sorted((c1, c2) -> c1.getCreneau().getHeure().getHeure().compareTo(c2.getCreneau().getHeure().getHeure()))
                        .collect(Collectors.toList());

                if (!coursJour.isEmpty()) {
                    System.out.println("  * " + jour.getNomJour() + ":");
                    coursJour.forEach(c -> {
                        System.out.printf("    - %s: %s avec %s\n",
                                c.getCreneau().getHeure().getNomHeure(),
                                c.getMatiere().getNomMatiere(),
                                c.getProf().getNomProf());
                    });
                }
            }
        }

        System.out.println("\n========================= SCORE ========================");
        System.out.println("Score final: " + solution.getScore());

        // Compter les cours non assignés
        long coursNonAssignes = coursList.stream()
                .filter(c -> c.getCreneau() == null || c.getProf() == null)
                .count();

        if (coursNonAssignes > 0) {
            System.out.println("Cours non assignés: " + coursNonAssignes);
        }

        System.out.println("========================================================");
    }

    private static void printScheduleParProf(EdtPlanificateur solution) {
        System.out.println("========================= EMPLOI DU TEMPS PAR PROF ========================");

        List<ProfPlan> profs = solution.getProfs(); // ProfPlan au lieu de Prof
        List<Cours> coursList = solution.getCoursList();

        Map<ProfPlan, List<Cours>> coursParProf = coursList.stream()
                .filter(c -> c.getCreneau() != null && c.getProf() != null)
                .collect(Collectors.groupingBy(Cours::getProf)); // getProf doit retourner ProfPlan

        for (ProfPlan prof : profs) {
            System.out.printf("\n--- Emploi du temps pour Professeur: %s ---\n", prof.getNomProf());

            List<Cours> coursDuProf = coursParProf.getOrDefault(prof, new ArrayList<>());

            for (JourPlan jour : solution.getJours()) {
                List<Cours> coursJour = coursDuProf.stream()
                        .filter(c -> c.getCreneau().getJour().equals(jour))
                        .sorted(Comparator.comparing(c -> c.getCreneau().getHeure().getHeure()))
                        .collect(Collectors.toList());

                if (!coursJour.isEmpty()) {
                    System.out.println("  * " + jour.getNomJour() + ":");
                    for (Cours c : coursJour) {
                        System.out.printf("    - %s: %s en classe %s\n",
                                c.getCreneau().getHeure().getNomHeure(),
                                c.getMatiere().getNomMatiere(),
                                c.getClasse().getNumeroClasse());
                    }
                }
            }
        }

        System.out.println("\n========================================================");
    }


}
