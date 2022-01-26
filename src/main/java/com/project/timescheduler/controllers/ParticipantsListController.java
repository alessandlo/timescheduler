package com.project.timescheduler.controllers;

import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class ParticipantsListController {
    @FXML
    private ListView<String> allParticipantsList;
    @FXML
    private ListView<String> selectedParticipantsList;


    public interface OnActionListener{
        void onAction(ObservableList<String> list);
    }

    private OnActionListener listener;

    @FXML
    public void initialize(OnActionListener listener){
        this.listener = listener;

        loadUsers();
    }

    public void loadUsers(){
        ObservableList<String> users = FXCollections.observableArrayList();

        String sql = "SELECT username FROM sched_user";

        DBResults rs = new DatabaseConnection().query(sql);
        while (rs.next()){
            users.add(rs.get("username"));
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
