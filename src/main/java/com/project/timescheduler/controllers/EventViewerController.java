package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Displays all events the logged-in user host or participates in
 */
public class EventViewerController {

    @FXML
    private TilePane tilePaneHosted;

    @FXML
    private TilePane tilePaneAttended;

    private String currentUser;

    interface OnActionListener{
        void onExit();
    }

    private OnActionListener listener;

    /**
     * called when opening the scene
     * calls loadHostedEvents and loadAttendingEvents
     */
    @FXML
    public void initialize(OnActionListener listener, LocalDate... localDate){
        this.currentUser = TimeSchedulerController.getCurrentUser().getUsername();
        System.out.println(currentUser);
        loadHostedEvents(localDate);
        loadAttendingEvents(localDate);

        this.listener = listener;
    }

    /**
     * loads all events the logged-in user attends
     * calls loadEventViewerItem
     */
    public void loadAttendingEvents(LocalDate... localDate){
        tilePaneAttended.getChildren().clear();
        for (Event e : TimeSchedulerController.getCurrentUser().getAttendedEvents(localDate)) {
                loadEventViewerItem(tilePaneAttended, e.getName(), e);
        }
    }

    /**
     * loads all events the logged-in hosts
     * calls loadEventViewerItem
     */
    public void loadHostedEvents(LocalDate... localDate){
        tilePaneHosted.getChildren().clear();
        for (Event e : TimeSchedulerController.getCurrentUser().getHostedEvents(localDate)) {
                loadEventViewerItem(tilePaneHosted, e.getName(), e);
        }
    }

    /**
     * Displays event in TilePane
     * @param rootPane rootPane
     * @param eventName String of the event name
     * @param ev Event that is linked to this pane
     */
    private void loadEventViewerItem(Pane rootPane, String eventName, Event ev){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("eventViewerItem.fxml")));
            AnchorPane anchorPane = loader.load();
            rootPane.getChildren().add(anchorPane);

            VBox vBox = (VBox) anchorPane.getChildren().get(0);
            vBox.setUserData(ev);
            Label label = (Label) vBox.getChildren().get(0);
            label.setText(eventName);
            vBox.setOnMouseClicked(this::onEventClicked);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * when clicked on TilePane opens new Stage with EventInformation or HostInformation
     * @param mouseEvent MouseEvent
     */
    @FXML
    private void onEventClicked(MouseEvent mouseEvent){
        VBox vBox = (VBox) mouseEvent.getTarget();
        Event activeEvent = (Event) vBox.getUserData();
        Parent root = null;

        try {
            if (activeEvent.getCreatorName().equals(currentUser)){
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("HostEventInformation.fxml")));
                root = loader.load();

                HostEventInformationController controller = loader.getController();
                controller.initialize(activeEvent, currentUser, () -> {
                    loadHostedEvents();
                    listener.onExit();
                });
            }else {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("EventInformation.fxml")));
                root = loader.load();

                EventInformationController controller = loader.getController();
                controller.initialize(activeEvent, currentUser);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        assert root != null;

        Stage eventStage = new Stage();
        eventStage.getIcons().add(Main.mainStage.getIcons().get(0));
        eventStage.setScene(new Scene(root));
        eventStage.initModality(Modality.APPLICATION_MODAL);
        eventStage.showAndWait();
    }
}
