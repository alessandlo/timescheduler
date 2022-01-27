package com.project.timescheduler.controllers;

import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class ParticipantsListController {
    @FXML
    private ListView<String> allParticipantsList;
    @FXML
    private ListView<String> selectedParticipantsList;


    public interface OnActionListener{
        void onAction(ObservableList<String> list);
    }

    private OnActionListener listener;

    private ArrayList<String> participants;

    @FXML
    public void initialize(OnActionListener listener, ArrayList<String> participants){
        this.listener = listener;
        this.participants = participants;
        loadUsers();
    }

    public void loadUsers(){
        ObservableList<String> users = FXCollections.observableArrayList();
        selectedParticipantsList.getItems().addAll(participants);
        String sql = "SELECT username FROM sched_user";

        DBResults rs = new DatabaseConnection().query(sql);
        while (rs.next()){
            if (!participants.contains(rs.get("username"))) {
                users.add(rs.get("username"));
            }
        }
        allParticipantsList.setItems(users);

    }

    @FXML
    private void addNameSelection(MouseEvent mouseEvent){
        String selectedItem = allParticipantsList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            selectedParticipantsList.getItems().add(selectedItem);
            allParticipantsList.getItems().remove(selectedItem);
            allParticipantsList.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void removeNameSelection(MouseEvent mouseEvent){
        String selectedItem = selectedParticipantsList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            allParticipantsList.getItems().add(selectedItem);
            selectedParticipantsList.getItems().remove(selectedItem);
            selectedParticipantsList.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void submitParticipants(){
        listener.onAction(selectedParticipantsList.getItems());
    }
}
