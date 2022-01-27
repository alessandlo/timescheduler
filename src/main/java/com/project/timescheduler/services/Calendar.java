package com.project.timescheduler.services;

import com.project.timescheduler.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
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
        void onMouseClicked(MouseEvent mouseEvent);
    }

    public Calendar(ArrayList<Node> nodeArrayList, OnMouseClickedListener listener, LocalDate currentDate){
        this.calendarGridPane = (GridPane) nodeArrayList.get(0);
        this.currentYearLabel = (Label) nodeArrayList.get(1);

        this.currentDate = currentDate;
        this.todaysDate = currentDate;
        this.listener = listener;
    }

    public void initializeCalendar() throws IOException {
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        for (int row = 0; row < rowCount; row++){
            for (int column = 0; column < columnCount; column++) {
                AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("calendarItem.fxml")));
                VBox vBox = (VBox) pane.getChildren().get(0);
                Label label = (Label) vBox.getChildren().get(0);

                if (column == 0 && row > 0){
                    label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
                    vBox.setCursor(Cursor.DEFAULT);
                    calendarGridPane.add(pane, column, row);

                }else if (column > 0 && row > 0){
                    pane.setDisable(startDate.getMonth() != currentDate.getMonth());

                    label.setText(String.format("%d", startDate.getDayOfMonth()));

                    if (todaysDate.equals(startDate)){
                        label.setTextFill(Color.RED);
                    }

                    vBox.setOnMouseClicked(listener::onMouseClicked);
                    calendarGridPane.add(pane, column, row);
                    startDate = startDate.plusDays(1);
                }

            }
        }
    }

    public void updateCalendar(){
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        int calenderLength = (columnCount * rowCount) - 7 - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        //System.out.println("Start Date: " + startDate);
        //System.out.println("Current Date: " + currentDate);
        for (int i = 0; i < calenderLength; i++) {
            Pane pane = (Pane) calendarGridPane.getChildren().get(i + 7); // +7 wegen der ersten Zeile
            VBox vBox = (VBox) pane.getChildren().get(0);
            Label label = (Label) vBox.getChildren().get(0);

            if (i % 8 == 0){
                label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
            }else {
                pane.setDisable(startDate.getMonth() != currentDate.getMonth());
                label.setText(String.format("%d", startDate.getDayOfMonth()));

                if (todaysDate.equals(startDate)){
                    label.setTextFill(Color.RED);
                }else {
                    label.setTextFill(Color.BLACK);
                }

                startDate = startDate.plusDays(1);
            }
        }

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
