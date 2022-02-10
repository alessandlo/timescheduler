package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.Event;
import com.project.timescheduler.services.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;
/** Before deleting a user, an Alert will show up stating all of their events that will be deleted as well. **/
public class WarningListController {

    @FXML
    private ListView<String> listview;

    String selectedUser;

    private OnActionListener listener;
    /**
     * Interface for closing the current stage
     */
    public interface OnActionListener {
        void onAction();
    }
    /**
     * Initializes OnActionListener and listview
     * @param listener OnActionListener
     * @param selectedUser Selected user to delete
     */
    public void initialize(OnActionListener listener, String selectedUser) {
        this.listener = listener;
        this.selectedUser = selectedUser;
        ArrayList<Event> list = new User(selectedUser).getHostedEvents();
        ObservableList<String> item = FXCollections.observableArrayList();

        for (Event e : list) {
            item.add(e.getName());
        }

        listview.setItems(item);
    }
    /**
     * Deletes all created events and user
     */
    @FXML
    private void confirmDelete(){
        String delete_events = String.format("DELETE FROM sched_event WHERE creator_name='%s'", selectedUser);
        Main.connection.update(delete_events);
        String delete_user = String.format("DELETE FROM sched_user WHERE username='%s'", selectedUser);
        Main.connection.update(delete_user);

        listener.onAction();
    }
    /**
     * exit Screen
     */
    @FXML
    private void cancelDelete(){
        listener.onAction();
    }
}
