package com.project.timescheduler.controllers;

import com.project.timescheduler.services.Calendar;
import com.project.timescheduler.Main;
import com.project.timescheduler.services.PdfExport;
import com.project.timescheduler.services.User;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;

public class TimeSchedulerController{

    private Stage menuStage;
    private Stage userSettingStage;
    private Scene scene;
    private static User currentUser;

    @FXML
    GridPane calendarGridPane;
    @FXML
    Label currentYearLabel;
    @FXML
    public AnchorPane anchorPaneTimeScheduler;
    @FXML
    private Label currentUserLabel;

    LocalDate currentDate;
    Calendar calendar;

    @FXML
    public void initialize(User currentUser) throws IOException {
        currentDate = LocalDate.now();
        currentUserLabel.setText(currentUser.getUsername());
        TimeSchedulerController.currentUser = currentUser;

        ArrayList<Node> list = new ArrayList<>();
        list.add(calendarGridPane);
        list.add(currentYearLabel);

        calendar = new Calendar(list, new Calendar.OnMouseClickedListener() {
            @Override
            public void onDayClicked(MouseEvent mouseEvent) {
                mouseDayClicked(mouseEvent);
            }

            @Override
            public void onWeekClicked(MouseEvent mouseEvent) {
                mouseWeekClicked(mouseEvent);
            }
        }, currentDate, currentUser.getUsername());
        initializeCalendar();
    }

    private void initializeCalendar() throws IOException {
        calendar.initializeCalendar();
        currentYearLabel.setText(currentDate.toString());
    }

    @FXML
    private void mouseDayClicked(MouseEvent mouseEvent){
        EventTarget target = mouseEvent.getTarget();

        if (target.getClass() == VBox.class){
            VBox vBox = (VBox) target;
            Label label = (Label) vBox.getChildren().get(0);
            System.out.println(label.getText());
        }

        try {
            anchorPaneTimeScheduler.setDisable(true);

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("eventMenu.fxml"));
            Parent root = loader.load();
            EventMenuController eventMenuController = loader.getController();

            eventMenuController.initialize(() -> {
                anchorPaneTimeScheduler.setDisable(false);
                menuStage.close();
            }, currentUser.getUsername());

            menuStage = new Stage();
            scene = new Scene(root);
            menuStage.setScene(scene);
            menuStage.setOnCloseRequest(windowEvent -> anchorPaneTimeScheduler.setDisable(false));
            menuStage.initModality(Modality.APPLICATION_MODAL);
            menuStage.setMinWidth(560);
            menuStage.setMinHeight(300);
            menuStage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void mouseWeekClicked(MouseEvent mouseEvent){
        int year = Integer.parseInt(String.format("%.4s", currentYearLabel.getText()));
        EventTarget target = mouseEvent.getTarget();
        VBox vBox = (VBox) target;
        Label label = (Label) vBox.getChildren().get(0);
        int calendarWeek = Integer.parseInt(label.getText());

        LocalDate firstDay = LocalDate.now()
                .with(WeekFields.ISO.weekBasedYear(), year) // year
                .with(WeekFields.ISO.weekOfWeekBasedYear(), calendarWeek) // week of year
                .with(WeekFields.ISO.dayOfWeek(), DayOfWeek.MONDAY.getValue()); // day of week

        LocalDate lastDay = firstDay.plusDays(6);
        if (TimeSchedulerController.getCurrentUser().getAllEvents(firstDay, lastDay).isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("No events in this time period");
            alert.setContentText("You can't export events because there are no events in the selected week.");
            alert.showAndWait();
        }
        else {
            exportPDF(firstDay, lastDay);
        }
    }

    @FXML
    private void loadPreviousMonth() {
        calendar.previousMonth();
    }

    @FXML
    private void loadNextMonth() {
        calendar.nextMonth();
    }

    @FXML
    private void switchToEventView(ActionEvent event)throws IOException{

        anchorPaneTimeScheduler.setDisable(true);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("eventViewer.fxml"));
        Parent root = loader.load();

        EventViewerContoller controller = loader.getController();
        controller.initialize(currentUser.getUsername());

        Stage listStage = new Stage();
        scene = new Scene(root);
        listStage.setScene(scene);
        listStage.setOnCloseRequest(windowEvent -> anchorPaneTimeScheduler.setDisable(false));
        listStage.initModality(Modality.APPLICATION_MODAL);
        listStage.setMinWidth(600);
        listStage.setMinHeight(480);
        listStage.showAndWait();
    }

    @FXML
    private void switchToUserSettings(ActionEvent event){
        try {
            anchorPaneTimeScheduler.setDisable(true);

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("userSettings.fxml"));
            Parent root = loader.load();
            UserSettingsController userSettingsController = loader.getController();

            userSettingsController.initialize(() -> {
                anchorPaneTimeScheduler.setDisable(false);
                userSettingStage.close();
            }, currentUser.getUsername());

            userSettingStage = new Stage();
            scene = new Scene(root);
            userSettingStage.setScene(scene);
            userSettingStage.setOnCloseRequest(windowEvent -> anchorPaneTimeScheduler.setDisable(false));
            userSettingStage.initModality(Modality.APPLICATION_MODAL);
            userSettingStage.setMinWidth(450);
            userSettingStage.setMinHeight(500);
            userSettingStage.showAndWait();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event){
        System.out.println("Logout");
    }

    @FXML
    private void switchView(ActionEvent actionEvent){
        calendar.switchView();
    }

    private void exportPDF(LocalDate firstDay, LocalDate lastDay)  {
        FileChooser fileChooser = new FileChooser();
        PdfExport pdfExport = new PdfExport();

        fileChooser.setInitialFileName("Weekly_" + firstDay + "_to_" + lastDay);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF file (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(Main.mainStage);
        try {
            String path = file.getAbsolutePath();
            pdfExport.initialize(path, firstDay, lastDay);
        }
        catch (Exception e){
            System.out.println("canceled");
        }
    }

    public static User getCurrentUser(){
        return TimeSchedulerController.currentUser;
    }
}

