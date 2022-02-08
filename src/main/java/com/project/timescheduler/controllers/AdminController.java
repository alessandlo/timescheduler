package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {

    private Stage warningStage;

    @FXML
    public AnchorPane anchorPaneAdminPanel;
    @FXML
    private TableView<User> tableview;
    @FXML
    private TableColumn<User, String> col_username;
    @FXML
    private TableColumn<User, String> col_firstname;
    @FXML
    private TableColumn<User, String> col_lastname;
    @FXML
    private TableColumn<User, String> col_email;
    @FXML
    private TableColumn<User, String> col_password;
    @FXML
    private TextField emailEdit;
    @FXML
    private PasswordField passwordEdit;
    @FXML
    private Label feedback, selectedUserLabel;
    @FXML
    private Button editButton, deleteButton;

    @FXML
    private void initialize() {
        editButton.disableProperty().bind(Bindings.isEmpty(tableview.getSelectionModel().getSelectedItems()).or(
                emailEdit.textProperty().isEmpty().and(passwordEdit.textProperty().isEmpty())));
        deleteButton.disableProperty().bind(Bindings.isEmpty(tableview.getSelectionModel().getSelectedItems()));

        col_username.setCellValueFactory(new PropertyValueFactory<>("Username"));
        col_firstname.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
        col_lastname.setCellValueFactory(new PropertyValueFactory<>("Lastname"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("Password"));

        loadData();
    }

    private void loadData(){
        ObservableList<User> userData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM sched_user";
        DBResults rs = Main.connection.query(sql);

        while (rs.next()){
            User userDetails = new User(
                    rs.get("username"),
                    rs.get("firstname"),
                    rs.get("lastname"),
                    rs.get("email"),
                    rs.get("password")
            );
            userData.add(userDetails);
        }
        tableview.setItems(userData);
    }

    @FXML
    private void showSelectedUser(MouseEvent mouseEvent){
        try {
            selectedUserLabel.setText(tableview.getSelectionModel().getSelectedItem().getUsername());
        }
        catch (Exception e){
            selectedUserLabel.setText("No user selected");
        }
    }

    @FXML
    private void deleteUser() throws IOException {
        String selectedUser = tableview.getSelectionModel().getSelectedItem().getUsername();

        if (new User(selectedUser).getHostedEvents().isEmpty()) {
            String delete_user = String.format("DELETE FROM sched_user WHERE username='%s'", selectedUser);
            Main.connection.update(delete_user);
            feedback.setText("User deleted");
        }
        else {
            anchorPaneAdminPanel.setDisable(true);

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("eventListWarning.fxml"));
            Parent root = loader.load();
            WarningListController warningListController = loader.getController();

            warningListController.initialize(() -> {
                anchorPaneAdminPanel.setDisable(false);
                warningStage.close();
            }, selectedUser);

            warningStage = new Stage();
            Scene scene = new Scene(root);
            warningStage.setScene(scene);
            warningStage.setOnCloseRequest(windowEvent -> anchorPaneAdminPanel.setDisable(false));
            warningStage.initModality(Modality.APPLICATION_MODAL);
            warningStage.setMinWidth(300);
            warningStage.setMinHeight(370);
            warningStage.showAndWait();
        }

        loadData();
    }

    @FXML
    private void editUser(){
        Validation validation = new Validation();
        Encryption encryption = new Encryption();

        String selectedUser = tableview.getSelectionModel().getSelectedItem().getUsername();
        String sql;

        if (passwordEdit.getText().isEmpty()) {
            if (validation.emailValidation(emailEdit.getText(), true)){
                sql = String.format("UPDATE sched_user SET email='%s' WHERE username='%s'",
                        emailEdit.getText(), selectedUser);
                Main.connection.update(sql);
                feedback.setText("Email changed");
            }
        }
        else if (emailEdit.getText().isEmpty()){
            if (validation.passwordValidation(passwordEdit.getText(), true)){
                sql = String.format("UPDATE sched_user SET password='%s' WHERE username='%s'",
                        encryption.createHash(passwordEdit.getText()), selectedUser);
                Main.connection.update(sql);
                feedback.setText("Password changed");
            }
        }
        else {
            if (validation.emailValidation(emailEdit.getText(), true) &&
                    validation.passwordValidation(passwordEdit.getText(), true)) {
                sql = String.format("UPDATE sched_user SET email='%s', password='%s' WHERE username='%s'",
                        emailEdit.getText(), encryption.createHash(passwordEdit.getText()), selectedUser);
                Main.connection.update(sql);
                feedback.setText("Email and Password changed");
            }
        }
        emailEdit.clear();
        passwordEdit.clear();

        loadData();
    }

    @FXML
    private void logout(ActionEvent event) throws IOException{
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("login.fxml"))));
        Main.mainStage.setScene(scene);

        Main.mainStage.setMinWidth(300);
        Main.mainStage.setMinHeight(300);
        Main.mainStage.setWidth(300);
        Main.mainStage.setHeight(300);

        System.out.println(Main.mainStage.getMinWidth());
    }
}
