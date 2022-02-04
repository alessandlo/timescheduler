package com.project.timescheduler.services;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import com.project.timescheduler.controllers.TimeSchedulerController;
import javafx.fxml.FXML;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfExport {
    private String filePath;

    @FXML
    public void initialize(String path, LocalDate firstDay, LocalDate lastDay) throws IOException {
        filePath = path;
        loadAllEvents(firstDay, lastDay);
        //loadData(firstDay, lastDay, currentUser);
    }

    public void loadAllEvents(LocalDate firstDay, LocalDate lastDay) throws IOException {
        ArrayList<Event> dataList = new ArrayList<>(TimeSchedulerController.getCurrentUser().getAllEvents(firstDay, lastDay));
        createFile(dataList, firstDay, lastDay);
    }

    public void createFile(ArrayList<Event> dataList, LocalDate firstDay, LocalDate lastDay) throws IOException {
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        PDDocument document = new PDDocument();

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
        contentStream.newLineAtOffset(50, 530);
        contentStream.showText("Weekly Schedule: " + firstDay + " to " + lastDay);
        contentStream.endText();
        contentStream.close();
        document.addPage(page);

        // margin
        float margin = 50;
        // starting y position is whole page height (subtracted by top and bottom margin)
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // table across whole page width (subtracted by left and right margin)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70;

        ArrayList<List> data = new ArrayList();
        data.add(new ArrayList<>(
                Arrays.asList("Event Name", "Start Date - Time", "End Date - Time", "Location ", "Priority ", "Participants ", "Created by")));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm");
        for (Event e : dataList) {
            LocalDateTime start_dt = LocalDateTime.of(e.getStartDate(), e.getStartTime());
            LocalDateTime end_dt = LocalDateTime.of(e.getEndDate(), e.getEndTime());

            String start_format = start_dt.format(dtf);
            String end_format = end_dt.format(dtf);

            String participantsList = Arrays.toString(e.getParticipants().toArray()).replace("[", "").replace("]", "");

            data.add(new ArrayList<>(
                    Arrays.asList(e.getName(), start_format, end_format, e.getLocation(), e.getPriority(), participantsList, e.getCreatorName())));
        }

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, true);
        DataTable t = new DataTable(dataTable, page);
        t.addListToTable(data, DataTable.HASHEADER);
        dataTable.draw();

        //Saving the document
        document.save(filePath);
        System.out.println("PDF created");

        //Closing the document
        document.close();
    }
}
