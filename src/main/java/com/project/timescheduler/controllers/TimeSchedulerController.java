package com.project.timescheduler.controllers;

import com.project.timescheduler.services.Calendar;
import com.project.timescheduler.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class TimeSchedulerController{

    public static Stage menuStage = null;

    private Scene scene;

    private String currentUser;

    @FXML
    GridPane calenderGridPane;

    @FXML
    Label currentYearLabel;

    @FXML
    public AnchorPane anchorPaneTimeScheduler;

    @FXML
    public Button showListButton;

    @FXML
    private Label currentUserLabel;

    LocalDate currentDate;
    Calendar calendar;

    @FXML
    public void initialize() throws IOException {
        currentDate = LocalDate.now();

        ArrayList<Node> list = new ArrayList<>();
        list.add(calenderGridPane);
        list.add(currentYearLabel);

        calendar = new Calendar(list, this::mouseClicked, currentDate);
        initializeCalendar();
    }

    private void initializeCalendar() throws IOException {
        calendar.initializeCalendar();
        currentYearLabel.setText(currentDate.toString());
    }


    @FXML
    private void mouseClicked(MouseEvent mouseEvent){
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
            }, currentUser);

            menuStage = new Stage();
            scene = new Scene(root);
            menuStage.setScene(scene);
            menuStage.setOnCloseRequest(windowEvent -> {
                anchorPaneTimeScheduler.setDisable(false);
            });
            menuStage.initModality(Modality.APPLICATION_MODAL);
            menuStage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
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
    private void switchToList(ActionEvent event)throws IOException{

        anchorPaneTimeScheduler.setDisable(true);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("EventList.fxml"));
        Parent root = loader.load();

        EventListController controller = loader.getController();
        controller.initialize(currentUser);

        Stage listStage = new Stage();
        scene = new Scene(root);
        listStage.setScene(scene);
        listStage.show();
        listStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                anchorPaneTimeScheduler.setDisable(false);
            }
        });
    }

    public void getCurrentUser(String text){
        currentUserLabel.setText(text);
        currentUser = text;
    }

    @FXML
    private void switchToUserSettings(ActionEvent event2)throws IOException{

        anchorPaneTimeScheduler.setDisable(true);
        FXMLLoader loader2 = new FXMLLoader(Main.class.getResource("userSettings.fxml"));
        Parent root2 = loader2.load();

        UserSettingsController controller2 = loader2.getController();
        controller2.initialize(currentUser);

        Stage userSettingStage = new Stage();
        Scene scene2 = new Scene(root2);
        userSettingStage.setScene(scene2);
        userSettingStage.show();
        userSettingStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent2) {
                anchorPaneTimeScheduler.setDisable(false);
            }
        });
}}

