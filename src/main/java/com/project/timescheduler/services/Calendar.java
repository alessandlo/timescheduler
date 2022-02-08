package com.project.timescheduler.services;

import com.project.timescheduler.Main;
import com.project.timescheduler.controllers.TimeSchedulerController;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

public class Calendar {

    private final GridPane calendarGridPane;
    private final Label currentYearLabel;
    private final OnMouseClickedListener listener;
    private final LocalDate todaysDate;
    private LocalDate currentDate;
    private String calendarItemStyle;
    private final String currentUser;

    private enum WEEK_DAY{
        MONDAY      (0),
        TUESDAY     (1),
        WEDNESDAY   (2),
        THURSDAY    (3),
        FRIDAY      (4),
        SATURDAY    (5),
        SUNDAY      (6);

        private final int code;

        WEEK_DAY(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public interface OnMouseClickedListener{
        void onDayClicked(MouseEvent mouseEvent);
        void onWeekClicked(MouseEvent mouseEvent);
    }

    public Calendar(ArrayList<Node> nodeArrayList, OnMouseClickedListener listener, LocalDate currentDate, String currentUser){
        this.calendarGridPane = (GridPane) nodeArrayList.get(0);
        this.currentYearLabel = (Label) nodeArrayList.get(1);

        calendarItemStyle = "calendarItemView1";
        this.currentUser = currentUser;
        this.currentDate = currentDate;
        this.todaysDate = currentDate;
        this.listener = listener;
    }

    private void addEventsToCalendar(LocalDate iteratedDate, VBox parentNode, ArrayList<Event> events, HashMap<Integer, Event> tempEvents){
        DoubleBinding binding = Bindings.createDoubleBinding(() -> {
            double value = (Main.mainStage.getHeight() - 571.43) / 14.29; //Lineare Equation (f(x) = 14.29 * x + 571.43)
            if (Main.mainStage.getHeight() < 800.0){
                return value;
            }else {
                return -1.0;
            }

        }, Main.mainStage.heightProperty());

        for (int i = 1; i < 4; i++){
            boolean in_between = false;
            Event e = null;
            Label eventLabel = (Label) parentNode.getChildren().get(i);

            eventLabel.minHeightProperty().bind(binding);
            eventLabel.prefHeightProperty().bind(binding);



            if (tempEvents.get(i) == null) {
                for (Event event : events) {
                    //https://stackoverflow.com/questions/883060/how-can-i-determine-if-a-date-is-between-two-dates-in-java
                    in_between = event.getStartDate().compareTo(iteratedDate) * iteratedDate.compareTo(event.getEndDate()) >= 0;

                    if (in_between) {
                        boolean startDateCmp = event.getStartDate().equals(iteratedDate);
                        boolean endDateCmp = event.getEndDate().equals(iteratedDate);

                        if (startDateCmp && endDateCmp) {
                            e = event;
                            break;
                        } else if (startDateCmp) {
                            e = event;
                            break;
                        }
                    }
                }
            }else {
                e = tempEvents.get(i);
                in_between = true;
            }

            if (in_between) {
                boolean startDateCmp = e.getStartDate().equals(iteratedDate);
                boolean endDateCmp = e.getEndDate().equals(iteratedDate);

                if (startDateCmp && endDateCmp) {
                    VBox.setMargin(eventLabel, new Insets(0, 4, 0, 4));
                    events.remove(e);
                } else if (startDateCmp) {
                    VBox.setMargin(eventLabel, new Insets(0, 0, 0, 4));
                    tempEvents.put(i, e);//add to tempEvent
                    events.remove(e);
                } else if (endDateCmp) {
                    VBox.setMargin(eventLabel, new Insets(0, 4, 0, 0));
                    tempEvents.remove(i);
                } else {
                    VBox.setMargin(eventLabel, new Insets(0, 0, 0, 0));
                }

                eventLabel.setText(e.getName());
                switch (e.getPriority()){
                    case high -> eventLabel.setStyle("-fx-background-color: #ff0000;");
                    case medium -> eventLabel.setStyle("-fx-background-color: #ffdd00;");
                    case low -> eventLabel.setStyle("-fx-background-color: #00ff0d;");
                }
            }else {
                eventLabel.setText("");
                eventLabel.setStyle("-fx-background-color: #00000000;");
            }

        }
    }

    public void initializeCalendar() throws IOException {
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        User temp_user = TimeSchedulerController.getCurrentUser().copy();

        ArrayList<Event> events = temp_user.getAllEvents(currentDate.withDayOfMonth(1),
                currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        HashMap<Integer, Event> tempEvent = new HashMap<>();

        for (int row = 0; row < rowCount; row++){
            for (int column = 0; column < columnCount; column++) {
                AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("calendarItem.fxml")));
                VBox vBox = (VBox) anchorPane.getChildren().get(0);
                Label label = (Label) vBox.getChildren().get(0);

                anchorPane.getStyleClass().add(calendarItemStyle);

                if (column == 0 && row > 0){
                    while (vBox.getChildren().size() != 1) {
                        vBox.getChildren().remove(1);
                    }
                    label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
                    vBox.setOnMouseClicked(listener::onWeekClicked);
                    calendarGridPane.add(anchorPane, column, row);
                }else if (column > 0 && row > 0){
                    anchorPane.setDisable(startDate.getMonth() != currentDate.getMonth());

                    label.setText(String.format("%d", startDate.getDayOfMonth()));

                    if (todaysDate.equals(startDate)){
                        label.setTextFill(Color.RED);
                    }

                    System.out.printf("Before:\nDate: %s\ntempEvents: %s\n", startDate, tempEvent);
                    for (int k = 1; k < 4; k++){
                        if (tempEvent.containsKey(k)) {
                            System.out.printf("tempEvents: %s\n", tempEvent.get(k).getName());
                        }
                    }
                    System.out.println("");

                    addEventsToCalendar(startDate, vBox, events, tempEvent);

                    System.out.printf("After:\nDate: %s\ntempEvents: %s\n", startDate, tempEvent);
                    for (int k = 1; k < 4; k++){
                        if (tempEvent.containsKey(k)) {
                            System.out.printf("tempEvents: %s\n", tempEvent.get(k).getName());
                        }
                    }
                    System.out.println("");

                    vBox.setOnMouseClicked(listener::onDayClicked);
                    calendarGridPane.add(anchorPane, column, row);
                    startDate = startDate.plusDays(1);
                }

            }
        }

    }

    public void updateCalendar() {
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        int calenderLength = (columnCount * rowCount); // - 7 damit man die Wochenbeschriftung nicht drinne hat und - 1 weil man von 0 anfängt

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        User temp_user = TimeSchedulerController.getCurrentUser().copy();
        ArrayList<Event> events = temp_user.getAllEvents(currentDate.withDayOfMonth(1),
                currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        HashMap<Integer, Event> tempEvent = new HashMap<>();

        for (int i = 8; i < calenderLength; i++) {
            AnchorPane anchorPane = (AnchorPane) calendarGridPane.getChildren().get(i);
            VBox vBox = (VBox) anchorPane.getChildren().get(0);
            Label label = (Label) vBox.getChildren().get(0);
            /**
            while (vBox.getChildren().size() != 1){
                vBox.getChildren().remove(1);
            }**/

            anchorPane.getStyleClass().clear();
            anchorPane.getStyleClass().add(calendarItemStyle);

            if (i % 8 == 0){
                label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
            }else {
                anchorPane.setDisable(startDate.getMonth() != currentDate.getMonth());
                label.setText(String.format("%d", startDate.getDayOfMonth()));

                //Current Day is Red
                if (todaysDate.equals(startDate)){
                    label.setTextFill(Color.RED);
                }else {
                    label.setTextFill(Color.BLACK);
                }

                addEventsToCalendar(startDate, vBox, events, tempEvent);

                startDate = startDate.plusDays(1);
            }
        }

    }

    public void switchView(){
        if (calendarGridPane.getHgap() == 10){
            calendarGridPane.setHgap(0);
            calendarGridPane.setVgap(0);
            calendarItemStyle = "calendarItemView2";
        }else {
            calendarGridPane.setHgap(10);
            calendarGridPane.setVgap(10);
            calendarItemStyle = "calendarItemView1";
        }
        updateCalendar();

    }

    public void nextMonth() {
        currentDate = currentDate.plusMonths(1);
        currentYearLabel.setText(currentDate.toString());
        updateCalendar();
    }

    public void previousMonth() {
        currentDate = currentDate.minusMonths(1);
        currentYearLabel.setText(currentDate.toString());
        updateCalendar();
    }
}
