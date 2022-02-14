package com.project.timescheduler.controllers;

import com.project.timescheduler.services.Calendar;
import com.project.timescheduler.Main;
import com.project.timescheduler.services.PdfExport;
import com.project.timescheduler.services.User;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;
/** This controller manages the entire home page of our Time Scheduler including the design of our calendar. **/
public class TimeSchedulerController{

    private Stage menuStage;
    private Stage userSettingStage;
    private Scene scene;
    private static User currentUser;

    @FXML
    GridPane calendarGridPane;
    @FXML
    Label currentDateLabel;
    @FXML
    public AnchorPane anchorPaneTimeScheduler;
    @FXML
    private Label currentUserLabel;

    LocalDate currentDate;
    Calendar calendar;
    /** Initializing several parts of home page structure. **/
    @FXML
    public void initialize(User currentUser) throws IOException {
        currentDate = LocalDate.now();
        currentUserLabel.setText(currentUser.getUsername());
        TimeSchedulerController.currentUser = currentUser;

        calendar = new Calendar(calendarGridPane, new Calendar.OnMouseClickedListener() {
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

    /** Initializing calendar. **/
    private void initializeCalendar() throws IOException {
        calendar.initializeCalendar();
        updateDateLabel();
    }

    /** Refreshing currentDate label. **/
    private void updateDateLabel(){
        currentDate = calendar.getCurrentDate();
        currentDateLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
    }

    /**
     * Queries save path and calls PDF export
     * @param firstDay First day of the calendar week
     * @param lastDay Last day of the calendar week
     */
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

    /**
     * Triggers event creation or event view depending on whether the day has events
     * @param mouseEvent
     */
    @FXML
    private void mouseDayClicked(MouseEvent mouseEvent){
        EventTarget target = mouseEvent.getTarget();

        VBox vBox = (VBox) target;
        Label label = (Label) vBox.getChildren().get(0);

        LocalDate clickedDate = currentDate.withDayOfMonth(Integer.parseInt(label.getText()));

        try {
            anchorPaneTimeScheduler.setDisable(true);

            Parent root = null;

            if (mouseEvent.getButton() == MouseButton.PRIMARY){
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("eventMenu.fxml"));
                root = loader.load();
                EventMenuController eventMenuController = loader.getController();

                eventMenuController.initialize(() -> {
                    anchorPaneTimeScheduler.setDisable(false);
                    menuStage.close();
                    calendar.updateCalendar();
                }, currentUser.getUsername(), clickedDate);
            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("eventViewerVertical.fxml"));
                root = loader.load();
                EventViewerController eventViewerController = loader.getController();

                eventViewerController.initialize(calendar::updateCalendar, clickedDate, clickedDate);
            }

            menuStage = new Stage();
            scene = new Scene(root);
            menuStage.getIcons().add(Main.mainStage.getIcons().get(0));
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

    /**
     * Triggers PDF export. Calculates corresponding calendar week
     * @param mouseEvent Mouse target
     */
    @FXML
    private void mouseWeekClicked(MouseEvent mouseEvent){
        int year = currentDate.getYear();
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

    /** Display previous month. **/
    @FXML
    private void loadPreviousMonth() {
        calendar.previousMonth();
        updateDateLabel();
    }

    /** Display next month. **/
    @FXML
    private void loadNextMonth() {
        calendar.nextMonth();
        updateDateLabel();
    }

    /** Display Event View. **/
    @FXML
    private void switchToEventView()throws IOException{

        anchorPaneTimeScheduler.setDisable(true);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("eventViewerHorizontal.fxml"));
        Parent root = loader.load();

        EventViewerController eventViewerController = loader.getController();
        eventViewerController.initialize(calendar::updateCalendar);

        Stage listStage = new Stage();
        scene = new Scene(root);
        listStage.getIcons().add(Main.mainStage.getIcons().get(0));
        listStage.setScene(scene);
        listStage.setOnCloseRequest(windowEvent -> anchorPaneTimeScheduler.setDisable(false));
        listStage.initModality(Modality.APPLICATION_MODAL);
        listStage.setMinWidth(600);
        listStage.setMinHeight(480);
        listStage.showAndWait();
    }

    /**
     * Switch to AccountDetails
     */
    @FXML
    private void switchToUserSettings(){
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
            userSettingStage.getIcons().add(Main.mainStage.getIcons().get(0));
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

    /**
     * Logout, back to the login screen
     * @throws IOException Exception if error occurs when loading FXML
     */
    @FXML
    private void logout() throws IOException{
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("login.fxml"))));
        Main.mainStage.setScene(scene);

        Main.mainStage.setMinWidth(300);
        Main.mainStage.setMinHeight(300);
        Main.mainStage.setWidth(300);
        Main.mainStage.setHeight(300);

        System.out.println(Main.mainStage.getMinWidth());
    }

    /**
     * Changes calendar view
     */
    @FXML
    private void switchView(){
        if (calendar.getView() == Calendar.CALENDARVIEW.SIMPLE){
            calendar.setViewTo(Calendar.CALENDARVIEW.NORMAL);
        }else {
            calendar.setViewTo(Calendar.CALENDARVIEW.SIMPLE);
        }
    }

    /** Global method to spread/transfer the current User to all other functionalities.
     * @return TimeSchedulerController.currenUser **/
    public static User getCurrentUser(){
        return TimeSchedulerController.currentUser;
    }
}

