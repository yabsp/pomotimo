package org.pomtim.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class TimerPane extends BorderPane {

    @FXML private Label timerLabel;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button resetButton;

    public TimerPane() {
        System.out.println("This is the path: "+ getClass().getResource("/fxml/TimerPane.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TimerPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        this.getStylesheets().add("css/timer.css");

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TimerPane.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        timerLabel.setText("00:00");

        startButton.setOnAction(e -> System.out.println("Start clicked"));
        pauseButton.setOnAction(e -> System.out.println("Pause clicked"));
        resetButton.setOnAction(e -> System.out.println("Reset clicked"));
    }
}
