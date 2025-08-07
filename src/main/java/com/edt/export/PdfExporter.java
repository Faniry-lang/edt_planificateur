package com.edt.export;

import com.edt.planning.entities.*;
import com.edt.planning.solver.entities.Cours;
import com.edt.planning.solver.entities.EdtPlanificateur;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PdfExporter {

    public static void export(EdtPlanificateur solution, String filepath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filepath));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 11);

            document.add(new Paragraph("Emploi du temps généré", titleFont));
            document.add(new Paragraph(" "));

            Map<ClassePlan, List<Cours>> coursParClasse = solution.getCoursList().stream()
                    .filter(c -> c.getCreneau() != null && c.getProf() != null)
                    .collect(Collectors.groupingBy(Cours::getClasse));

            for (ClassePlan classe : solution.getClasses()) {
                document.add(new Paragraph(
                        String.format("Classe: %s (%s - %s)", classe.getNumeroClasse(),
                                classe.getLevelPlan().getNomLevel(), classe.getSpePlan().getNomSpe()), headerFont));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(solution.getHeures().size() + 1); // 1 col pour jour + heures
                table.setWidthPercentage(100);

                // Ligne d'en-tête
                table.addCell(new PdfPCell(new Phrase("Jour / Heure", headerFont)));
                for (HeurePlan h : solution.getHeures()) {
                    table.addCell(new PdfPCell(new Phrase(h.getNomHeure(), headerFont)));
                }

                List<Cours> coursClasse = coursParClasse.getOrDefault(classe, Collections.emptyList());

                for (JourPlan jour : solution.getJours()) {
                    table.addCell(new PdfPCell(new Phrase(jour.getNomJour(), bodyFont)));

                    for (HeurePlan heure : solution.getHeures()) {
                        Optional<Cours> coursOpt = coursClasse.stream()
                                .filter(c -> c.getCreneau().getJour().equals(jour) &&
                                        c.getCreneau().getHeure().equals(heure))
                                .findFirst();

                        if (coursOpt.isPresent()) {
                            Cours c = coursOpt.get();
                            String contenu = String.format("%s\n%s", c.getMatiere().getNomMatiere(), c.getProf().getNomProf());
                            table.addCell(new PdfPCell(new Phrase(contenu, bodyFont)));
                        } else {
                            table.addCell(new PdfPCell(new Phrase("-", bodyFont)));
                        }
                    }
                }

                document.add(table);
                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph("Score final : " + solution.getScore(), headerFont));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
