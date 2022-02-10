package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

/** This controller helps us to manage a separate window for the participant selection when creating a new event. **/
public class ParticipantsListController {
    @FXML
    private ListView<String> allParticipantsList;
    @FXML
    private ListView<String> selectedParticipantsList;
    @FXML
    private Button searchButton;
    @FXML
    private TextField usernameInput;
    /** Interface to connect/link and refresh the overview list properly. **/
    public interface OnActionListener{
        void onAction(ObservableList<String> list);
    }

    private OnActionListener listener;

    private ArrayList<String> participants;

    /**
     *
     * @param listener The listener, which is used when submitting the participants selection.
     * @param participants The participants which are already selected.
     **/
    @FXML
    public void initialize(OnActionListener listener, ArrayList<String> participants){
        this.listener = listener;
        this.participants = participants;
        loadUsers();
    }

    /** Load all Users into all participants list, except the ones, which are loaded inside the selected participants list **/
    public void loadUsers(){
        ObservableList<String> users = FXCollections.observableArrayList();
        selectedParticipantsList.getItems().addAll(participants);
        participants.clear();
        String sql = "SELECT username FROM sched_user";

        DBResults rs = Main.connection.query(sql);
        while (rs.next()){
            if (!selectedParticipantsList.getItems().contains(rs.get("username")) && !TimeSchedulerController.getCurrentUser().getUsername().equals(rs.get("username"))) {
                users.add(rs.get("username"));
            }
        }
        allParticipantsList.setItems(users);
    }

    /** Handling the display of the item/participant when selecting to add. **/
    @FXML
    private void addNameSelection(MouseEvent mouseEvent){
        String selectedItem = allParticipantsList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            selectedParticipantsList.getItems().add(selectedItem);
            allParticipantsList.getItems().remove(selectedItem);
            allParticipantsList.getSelectionModel().clearSelection();
        }
    }
    /** Handling the display of the item/participant when selecting to remove. **/
    @FXML
    private void removeNameSelection(MouseEvent mouseEvent){
        String selectedItem = selectedParticipantsList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            allParticipantsList.getItems().add(selectedItem);
            selectedParticipantsList.getItems().remove(selectedItem);
            selectedParticipantsList.getSelectionModel().clearSelection();
        }
        searchUsername();
    }
    /** Submitting the list with selected participants. **/
    @FXML
    private void submitParticipants(){
        listener.onAction(selectedParticipantsList.getItems());
    }

    /** Filter all participants list according Username Input **/
    @FXML
    private void searchUsername(){
        loadUsers();
        allParticipantsList.getItems().removeAll(selectedParticipantsList.getItems());
        allParticipantsList.getItems().removeIf(username -> !username.toLowerCase().contains(usernameInput.getText().toLowerCase()));
    }
}
