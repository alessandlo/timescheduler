package com.project.timescheduler.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

public class PdfExport {
    //ONLY TEST
    public void createFile(String path) throws IOException {
        //Creating PDF document object
        PDDocument document = new PDDocument();

        //Creating a blank page
        PDPage blankPage = new PDPage();

        //Adding the blank page to the document
        document.addPage( blankPage );

        //Saving the document
        document.save(path);
        System.out.println("PDF created");

        //Closing the document
        document.close();
    }
}
