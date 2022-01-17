module com.project.timescheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.project.timescheduler to javafx.fxml;
    exports com.project.timescheduler;
}