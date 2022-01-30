module com.project.timescheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;
    requires org.apache.pdfbox;

    opens com.project.timescheduler to javafx.fxml;
    opens com.project.timescheduler.controllers to javafx.fxml;
    exports com.project.timescheduler;
    exports com.project.timescheduler.controllers;
    exports com.project.timescheduler.services;
    exports com.project.timescheduler.helpers;
    opens com.project.timescheduler.services to javafx.fxml;
}