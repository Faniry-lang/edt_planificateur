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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
        SolverFactory<EdtPlanificateur> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(EdtPlanificateur.class)
                .withEntityClasses(Cours.class)
                .withConstraintProviderClass(EdtConstraintProvider.class)
                .withTerminationConfig(new TerminationConfig().withSecondsSpentLimit(30L))
        );

        EdtPlanificateur problem = createDemoData();
        Solver<EdtPlanificateur> solver = solverFactory.buildSolver();
        EdtPlanificateur solution = solver.solve(problem);

        printSchedule(solution);
        PdfExporter.export(solution, "emploi_du_temps.pdf");
        System.out.println("PDF généré : emploi_du_temps.pdf");

    }

    public static EdtPlanificateur createDemoData() {
        // 1. Jours de la semaine
        JourPlan lundi = new JourPlan(1, "Lundi");
        JourPlan mardi = new JourPlan(2, "Mardi");
        JourPlan mercredi = new JourPlan(3, "Mercredi");
        JourPlan jeudi = new JourPlan(4, "Jeudi");
        JourPlan vendredi = new JourPlan(5, "Vendredi");
        List<JourPlan> jours = Arrays.asList(lundi, mardi, mercredi, jeudi, vendredi);
        
        List<Integer> durees = new ArrayList<>();
        for(int i = 1; i <= 5; i++) 
        {
            durees.add(i);
        }

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
        MatierePlan maths = new MatierePlan(1, "Maths");
        MatierePlan physique = new MatierePlan(2, "Physique");
        MatierePlan francais = new MatierePlan(3, "Français");
        MatierePlan histoire = new MatierePlan(4, "Histoire");

        // Limite de 1 pour Info, 2 pour EPS
        MatierePlan info = new MatierePlan(5, "Informatique", 1);
        MatierePlan eps = new MatierePlan(6, "EPS", 2);
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

        // 10. Association Profs -> Classes (Optionnel, si un prof est titulaire)
        // Pour cet exemple, on ne le remplit pas, laissant le solveur décider.
        List<ClasseProfPlan> classeProfs = new ArrayList<>();

        // 11. Disponibilités des Profs
        List<DisponibiliteProf> disponibiliteProfs = new ArrayList<>();
        // Prof A est dispo tout le temps sauf le mercredi
        for (Creneau c : creneaux) {
            if (!c.getJour().equals(mercredi)) {
                disponibiliteProfs.add(new DisponibiliteProf(c, profA));
            }
        }
        // Prof B est dispo Lundi/Mardi matin et Jeudi/Vendredi aprem
        for (HeurePlan h : Arrays.asList(h8, h9, h10, h11)) {
            disponibiliteProfs.add(new DisponibiliteProf(new Creneau(lundi, h), profB));
            disponibiliteProfs.add(new DisponibiliteProf(new Creneau(mardi, h), profB));
        }
        for (HeurePlan h : Arrays.asList(h13, h14, h15, h16)) {
            disponibiliteProfs.add(new DisponibiliteProf(new Creneau(jeudi, h), profB));
            disponibiliteProfs.add(new DisponibiliteProf(new Creneau(vendredi, h), profB));
        }
        // Prof C (Info) n'est dispo que le matin
         for (JourPlan j : jours) {
            for (HeurePlan h : Arrays.asList(h8, h9, h10, h11)) {
                 disponibiliteProfs.add(new DisponibiliteProf(new Creneau(j, h), profC));
            }
        }
        // Prof D (EPS) est toujours dispo
        for (Creneau c : creneaux) {
            disponibiliteProfs.add(new DisponibiliteProf(c, profD));
        }


        // 12. Volume Horaire par matière/classe (simplifié)
        // On va créer les cours directement, ce qui représente le volume horaire.
        List<Cours> coursList = new ArrayList<>();
        long coursIdCounter = 0;

        // Seconde A (Scientifique)
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, maths, secondeA));
        for (int i = 0; i < 3; i++) coursList.add(new Cours(coursIdCounter++, physique, secondeA));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, francais, secondeA));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, histoire, secondeA));
        for (int i = 0; i < 1; i++) coursList.add(new Cours(coursIdCounter++, info, secondeA));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, secondeA));

        // Seconde B (Scientifique)
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, maths, secondeB));
        for (int i = 0; i < 3; i++) coursList.add(new Cours(coursIdCounter++, physique, secondeB));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, francais, secondeB));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, histoire, secondeB));
        for (int i = 0; i < 1; i++) coursList.add(new Cours(coursIdCounter++, info, secondeB));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, secondeB));

        // Premiere L (Littéraire)
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, maths, premiereL));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, francais, premiereL));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, histoire, premiereL));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, eps, premiereL));

        // Terminale S (Scientifique)
        for (int i = 0; i < 5; i++) coursList.add(new Cours(coursIdCounter++, maths, terminaleS));
        for (int i = 0; i < 4; i++) coursList.add(new Cours(coursIdCounter++, physique, terminaleS));
        for (int i = 0; i < 2; i++) coursList.add(new Cours(coursIdCounter++, francais, terminaleS));
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
        problem.setMatiereBaseSpePlans(matieresDeBase);
        problem.setCoursList(coursList);
        problem.setDisponibiliteProfs(disponibiliteProfs);
        problem.setLevels(levels);
        problem.setSpes(spes);
        problem.setMatiereProfs(profMatieres);
        problem.setClasseProfs(classeProfs);
        problem.setDurees(durees);
        // VolumeHorairePlan n'est plus nécessaire si on crée les cours directement
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
            System.out.printf("--- Emploi du temps pour la classe: %s (%s - %s) ---",
                    classe.getNumeroClasse(), classe.getLevelPlan().getNomLevel(), classe.getSpePlan().getNomSpe());

            List<Cours> coursDeLaClasse = coursParClasse.getOrDefault(classe, new ArrayList<>());

            for (JourPlan jour : solution.getJours()) {
                System.out.println("  * " + jour.getNomJour() + ":");
                coursDeLaClasse.stream()
                        .filter(c -> c.getCreneau().getJour().equals(jour))
                        .sorted((c1, c2) -> c1.getCreneau().getHeure().getHeure().compareTo(c2.getCreneau().getHeure().getHeure()))
                        .forEach(c -> {
                            System.out.printf("    - %s: %s avec %s",
                                    c.getCreneau().getHeure().getNomHeure(),
                                    c.getMatiere().getNomMatiere(),
                                    c.getProf().getNomProf());
                        });
            }
        }

        System.out.println("========================= SCORE ========================");
        System.out.println("Score final: " + solution.getScore());
        System.out.println("========================================================");
    }
}
