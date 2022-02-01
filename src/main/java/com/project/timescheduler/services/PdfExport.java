package com.project.timescheduler.services;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import com.project.timescheduler.helpers.DBResults;
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
    private DatabaseConnection connection;
    private String filePath;


    public void initialize(String path, LocalDate firstDay, LocalDate lastDay, String currentUser) throws IOException {
        connection = new DatabaseConnection();
        filePath = path;
        System.out.println(firstDay);
        loadData(firstDay, lastDay, currentUser);
    }

    private void loadData(LocalDate firstDay, LocalDate lastDay, String currentUser) throws IOException {
        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";
        connection.update(alter_date_format);
        String sql = String.format("SELECT PI.EVENT_ID, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY FROM SCHED_EVENT JOIN SCHED_PARTICIPATES_IN PI on SCHED_EVENT.EVENT_ID = PI.EVENT_ID WHERE START_DATE BETWEEN '%s 00:00' and '%s 23:59' AND PI.USERNAME='%s'", firstDay, lastDay, currentUser);
        DBResults rs = connection.query(sql);

        ArrayList<Event> dataList = new ArrayList<>();

        while (rs.next()){
            Event event = new Event(Integer.parseInt(rs.get("EVENT_ID")),
                    rs.get("CREATOR_NAME"),
                    rs.get("EVENT_NAME"),
                    rs.get("LOCATION"),
                    getParticipants(Integer.parseInt(rs.get("EVENT_ID"))),
                    rs.getDate("START_DATE").toLocalDate(),
                    rs.getDate("END_DATE").toLocalDate(),
                    rs.getTime("START_DATE").toLocalTime(),
                    rs.getTime("END_DATE").toLocalTime(),
                    Event.Priority.valueOf(rs.get("PRIORITY")));
            dataList.add(event);
        }
        createFile(dataList);
    }

    public ArrayList<String> getParticipants(int eventID){
        ArrayList<String> participants = new ArrayList<>();
        String sql = String.format("SELECT Username FROM SCHED_EVENT JOIN SCHED_PARTICIPATES_IN PI on SCHED_EVENT.EVENT_ID = PI.EVENT_ID WHERE PI.EVENT_ID=%s", eventID);
        DBResults rs = connection.query(sql);
        while (rs.next()){
            participants.add(rs.get("USERNAME"));
        }
        return participants;
    }

    public void createFile(ArrayList<Event> dataList) throws IOException {
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        PDDocument document = new PDDocument();

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
        contentStream.newLineAtOffset(50, 530);
        contentStream.showText("Weekly Schedule");
        contentStream.endText();
        contentStream.close();
        document.addPage(page);

        // margin
        float margin = 50;
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin ofcourse)
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
