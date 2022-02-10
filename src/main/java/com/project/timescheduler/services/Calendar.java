package com.project.timescheduler.services;

import com.project.timescheduler.Main;
import com.project.timescheduler.controllers.TimeSchedulerController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * This class is responsible for constructing the Calendar View
 */
public class Calendar {

    private final GridPane calendarGridPane;
    private final OnMouseClickedListener listener;
    private final LocalDate todaysDate;
    private final String currentUser;
    private LocalDate currentDate;
    private CALENDARVIEW calendarView;
    private HashMap<Integer, Event> tempEvents;

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

    public enum CALENDARVIEW{
        SIMPLE   ("calendarItemView1"),
        NORMAL   ("calendarItemView2");

        private final String view;

        CALENDARVIEW(String calendarItemView) {
            this.view = calendarItemView;
        }

        public String getView(){
            return view;
        }
    }

    /**
     * This interface handles the clicks on the Days and Weeks in the Calendar
     */
    public interface OnMouseClickedListener{
        void onDayClicked(MouseEvent mouseEvent);
        void onWeekClicked(MouseEvent mouseEvent);
    }

    /**
     * Creates a class which is responsible for constructing the Calendar View
     * @param gridPane The GridPane which should be used, for the Calendar
     * @param listener The listener for handling clicks on the Days and Weeks in the Calendar
     * @param currentDate The Date, which the Calendar should be starts at
     * @param currentUser The User currently logged in
     */
    public Calendar(GridPane gridPane, OnMouseClickedListener listener, LocalDate currentDate, String currentUser){
        this.calendarGridPane = gridPane;

        this.currentUser = currentUser;
        this.currentDate = currentDate;
        this.todaysDate = currentDate;
        this.listener = listener;

        calendarView = CALENDARVIEW.NORMAL;
        tempEvents = new HashMap<>();
    }

    /**
     * Add Events to the Calendar View of the respective Node (VBox), which then is shown in the Calendar
     * @param iteratedDate The Date for which the Events should be added to the respective Node (VBox)
     * @param parentNode The Node in which the Events should be added to
     * @param events A List of events, which should be displayed
     */
    private void addEventsToCalendar(LocalDate iteratedDate, VBox parentNode, ArrayList<Event> events){
        if (calendarView == CALENDARVIEW.NORMAL) {

            DoubleBinding binding = Bindings.createDoubleBinding(() -> {
                double value = (Main.mainStage.getHeight() - 571.43) / 14.29; //Lineare Equation (f(x) = 14.29 * x + 571.43)
                if (Main.mainStage.getHeight() < 800.0) {
                    return value;
                } else {
                    return -1.0;
                }

            }, Main.mainStage.heightProperty());


            for (int i = 1; i < 4; i++) {
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
                            in_between = false;
                        }
                    }
                } else {
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
                    switch (e.getPriority()) {
                        case high -> eventLabel.setStyle("-fx-background-color: #F25C5C;");
                        case medium -> eventLabel.setStyle("-fx-background-color: #F2CA52;");
                        case low -> eventLabel.setStyle("-fx-background-color: #5BD992;");
                    }
                } else {
                    eventLabel.setText("");
                    eventLabel.setStyle("-fx-background-color: #00000000;");
                }

            }
        }else {
            for (int j = 1; j < 4; j++){
                Label eventLabel = (Label) parentNode.getChildren().get(j);
                eventLabel.setText("");
                eventLabel.setStyle("-fx-background-color: #00000000;");
            }
        }
    }

    /**
     * This function builds up the Calendar View
     * @throws IOException
     */
    public void initializeCalendar() throws IOException {
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        User temp_user = TimeSchedulerController.getCurrentUser().copy();

        ArrayList<Event> events = temp_user.getAllEvents(currentDate.withDayOfMonth(1),
                currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        for (int row = 0; row < rowCount; row++){
            for (int column = 0; column < columnCount; column++) {
                AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("calendarItem.fxml")));
                VBox vBox = (VBox) anchorPane.getChildren().get(0);
                Label label = (Label) vBox.getChildren().get(0);

                anchorPane.getStyleClass().add(calendarView.getView());

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

                    addEventsToCalendar(startDate, vBox, events);

                    vBox.setOnMouseClicked(listener::onDayClicked);
                    calendarGridPane.add(anchorPane, column, row);
                    startDate = startDate.plusDays(1);
                }

            }
        }
        setViewTo(calendarView);
    }

    /**
     * This function updates the Calendar View
     */
    public void updateCalendar() {
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        int calenderLength = (columnCount * rowCount);

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        User temp_user = TimeSchedulerController.getCurrentUser().copy();
        ArrayList<Event> events = temp_user.getAllEvents(currentDate.withDayOfMonth(1),
                currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        for (int i = 8; i < calenderLength; i++) {
            AnchorPane anchorPane = (AnchorPane) calendarGridPane.getChildren().get(i);
            VBox vBox = (VBox) anchorPane.getChildren().get(0);
            Label label = (Label) vBox.getChildren().get(0);
            anchorPane.getStyleClass().clear();
            anchorPane.getStyleClass().add(calendarView.getView());

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


                addEventsToCalendar(startDate, vBox, events);


                startDate = startDate.plusDays(1);
            }
        }
    }

    /**
     * Sets the calendar view to the preferred View
     * @param calendarview The Calendar View, that should be set to
     */
    public void setViewTo(CALENDARVIEW calendarview){
        this.calendarView = calendarview;
        switch (calendarview) {
            case SIMPLE -> {
                calendarGridPane.setHgap(10);
                calendarGridPane.setVgap(10);
            }
            case NORMAL -> {
                calendarGridPane.setHgap(0);
                calendarGridPane.setVgap(0);
            }
        }
        updateCalendar();
    }

    /**
     * Sets the current date to the next month and updates the calendar view
     */
    public void nextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateCalendar();
    }

    /**
     * Sets the current date to the previous month and updates the calendar view
     */
    public void previousMonth() {
        currentDate = currentDate.minusMonths(1);
        updateCalendar();
    }

    /**
     * Return the current date of the Calendar
     * @return Current date of the Calendar
     */
    public LocalDate getCurrentDate(){
        return currentDate;
    }

    /**
     * Return the current view of the Calendar
     * @return The current view of the Calendar
     */
    public CALENDARVIEW getView(){
        return calendarView;
    }
}
