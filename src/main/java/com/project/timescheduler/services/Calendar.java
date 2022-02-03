package com.project.timescheduler.services;

import com.project.timescheduler.Main;
import com.project.timescheduler.controllers.TimeSchedulerController;
import com.project.timescheduler.helpers.DBResults;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Objects;

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

                anchorPane.getStyleClass().add(calendarItemStyle);

                if (column == 0 && row > 0){
                    label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
                    vBox.setOnMouseClicked(listener::onWeekClicked);
                    calendarGridPane.add(anchorPane, column, row);

                }else if (column > 0 && row > 0){
                    anchorPane.setDisable(startDate.getMonth() != currentDate.getMonth());

                    label.setText(String.format("%d", startDate.getDayOfMonth()));

                    if (todaysDate.equals(startDate)){
                        label.setTextFill(Color.RED);
                    }

                    for (Event e: events) {
                        if (e.getStartDate().equals(startDate)){
                            AnchorPane eventPane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("calendarItemEvent.fxml")));
                            eventPane.setPadding(new Insets(0, 4, 0, 4));
                            vBox.getChildren().add(eventPane);
                            break;
                        }
                    }

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

        int calenderLength = (columnCount * rowCount) - 7 - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        User temp_user = TimeSchedulerController.getCurrentUser().copy();
        ArrayList<Event> events = temp_user.getAllEvents(currentDate.withDayOfMonth(1),
                currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        for (int i = 0; i < calenderLength; i++) {
            AnchorPane anchorPane = (AnchorPane) calendarGridPane.getChildren().get(i + 7); // +7 wegen der ersten Zeile
            VBox vBox = (VBox) anchorPane.getChildren().get(0);
            Label label = (Label) vBox.getChildren().get(0);

            anchorPane.getStyleClass().clear();
            anchorPane.getStyleClass().add(calendarItemStyle);

            if (i % 8 == 0){
                label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
            }else {
                anchorPane.setDisable(startDate.getMonth() != currentDate.getMonth());
                label.setText(String.format("%d", startDate.getDayOfMonth()));

                if (todaysDate.equals(startDate)){
                    label.setTextFill(Color.RED);
                }else {
                    label.setTextFill(Color.BLACK);
                }

                for (Event e: events) {
                    if (e.getStartDate().equals(startDate)){
                        try {
                            if (vBox.getChildren().size() > 1) {
                                vBox.getChildren().remove(1);
                            }
                            AnchorPane eventPane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("calendarItemEvent.fxml")));
                            eventPane.setPadding(new Insets(0, 4, 0, 4));
                            vBox.getChildren().add(eventPane);
                        }catch (IOException f){
                            f.printStackTrace();
                        }
                        break;
                    }
                }

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
