package org.pomtim.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

import org.pomtim.logic.PomodoroTimer;

public class TimerPane extends BorderPane {

    @FXML private Label timerLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    private final PomodoroTimer timer = new PomodoroTimer();

    public TimerPane() {
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


        /* Init with buttons and clock directly
        timerLabel.setText("00:00");

        startButton.setOnAction(e -> {
            timer.start(25 * 60, seconds -> {
                int min = seconds / 60;
                int sec = seconds % 60;
                String time = String.format("%02d:%02d", min, sec);
                Platform.runLater(() -> timerLabel.setText(time));
            });
        });

        stopButton.setOnAction(e -> timer.pause());
        resetButton.setOnAction(e -> {
            timer.reset(25 * 60);
            timerLabel.setText("25:00");
        });
         */
    }
}
