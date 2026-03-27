package org.example.boxlybackend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.entites.Enums.ReservationStatus;
import org.example.boxlybackend.entites.LunchReservation;
import org.example.boxlybackend.repository.LunchReservationRepository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final LunchReservationRepository reservationRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateDailyReservationReport(LocalDate date) {
        List<LunchReservation> reservations = reservationRepository
                .findByMenuWeekDay_DateAndStatus(date, ReservationStatus.CONFIRMED);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Réservations du déjeuner — " + date.format(DATE_FORMAT), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Summary line
            Font normalFont = new Font(Font.HELVETICA, 11);
            Paragraph summary = new Paragraph("Total confirmé : " + reservations.size() + " employé(s)", normalFont);
            summary.setSpacingAfter(14);
            document.add(summary);

            // Table: N° | Matricule | Nom | Option
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.5f, 1.5f, 3f, 3f});

            addHeaderCell(table, "N°");
            addHeaderCell(table, "Matricule");
            addHeaderCell(table, "Nom");
            addHeaderCell(table, "Option");

            for (int i = 0; i < reservations.size(); i++) {
                LunchReservation r = reservations.get(i);
                boolean shaded = i % 2 == 1;

                addCell(table, String.valueOf(i + 1), shaded);
                addCell(table, r.getEmploye().getMatricule() != null ? String.valueOf(r.getEmploye().getMatricule()) : "", shaded);
                addCell(table, r.getEmploye().getName() != null ? r.getEmploye().getName() : "", shaded);
                String option = r.getMenuOption() != null ? r.getMenuOption().getTitle() : "—";
                addCell(table, option, shaded);
            }

            document.add(table);

        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private void addHeaderCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(52, 73, 94));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, boolean shaded) {
        Font font = new Font(Font.HELVETICA, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        if (shaded) {
            cell.setBackgroundColor(new Color(236, 240, 241));
        }
        table.addCell(cell);
    }
}
