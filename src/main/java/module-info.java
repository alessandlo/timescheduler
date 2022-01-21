module com.project.timescheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ojdbc10;

    opens com.project.timescheduler to javafx.fxml;
    opens com.project.timescheduler.controllers to javafx.fxml;
    exports com.project.timescheduler;
    exports com.project.timescheduler.controllers;
    exports com.project.timescheduler.services;
    opens com.project.timescheduler.services to javafx.fxml;
}