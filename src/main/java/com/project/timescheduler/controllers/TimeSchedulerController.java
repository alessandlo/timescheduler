package com.project.timescheduler.controllers;

import com.project.timescheduler.services.Calendar;
import com.project.timescheduler.Main;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class TimeSchedulerController{

    private Scene scene;

    @FXML
    GridPane calenderGridPane;

    @FXML
    Label currentYearLabel;

    @FXML
    AnchorPane anchorPaneTimeScheduler;

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
        System.out.println(target.getClass());
        if (target.getClass() == VBox.class){
            VBox vBox = (VBox) target;
            Label label = (Label) vBox.getChildren().get(0);
            System.out.println(label.getText());
        }
        try {
            anchorPaneTimeScheduler.setDisable(true);
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("eventMenu.fxml")));
            Stage menuStage = new Stage();
            scene = new Scene(root);
            menuStage.setScene(scene);
            menuStage.setOnCloseRequest(windowEvent -> {
                anchorPaneTimeScheduler.setDisable(false);
            });
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
}

