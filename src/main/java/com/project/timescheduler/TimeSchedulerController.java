package com.project.timescheduler;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TimeSchedulerController {

    private Scene scene;
    private Parent root;

    @FXML
    GridPane calenderGridPane;

    @FXML
    Label currentYearLabel;

    @FXML
    AnchorPane anchorPaneTimeScheduler;

    LocalDate currentDate;
    Calendar calendar;

    @FXML
    public void initialize() throws IOException, InterruptedException {
        currentDate = LocalDate.now();

        ArrayList<Node> list = new ArrayList<>();
        list.add(calenderGridPane);
        list.add(currentYearLabel);

        calendar = new Calendar(list, this::mouseClicked, currentDate);
        initializeCalendar();
    }

    private void initializeCalendar() throws IOException, InterruptedException {
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
            Parent root = FXMLLoader.load(getClass().getResource("EventMenu.fxml"));
            Stage menuStage = new Stage();
            scene = new Scene(root);
            menuStage.setScene(scene);
            menuStage.show();
            menuStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    anchorPaneTimeScheduler.setDisable(false);
                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @FXML
    private void loadPreviousMonth() throws IOException {
        calendar.previousMonth();
    }

    @FXML
    private void loadNextMonth() throws IOException {
        calendar.nextMonth();
    }
}

