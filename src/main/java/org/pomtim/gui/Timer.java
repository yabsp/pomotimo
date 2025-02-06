package org.pomtim.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Timer  extends BorderPane {

    private Label timerLabel;
    private Button startButton, pauseButton, resetButton;

    public Timer () {
        timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        startButton = new Button("Start");
        pauseButton = new Button("Pause");
        resetButton = new Button("Reset");

        HBox controls = new HBox(10, startButton, pauseButton, resetButton);
        controls.setAlignment(Pos.CENTER);

        VBox timerBox = new VBox(10, timerLabel, controls);
        timerBox.setAlignment(Pos.CENTER);

        this.setCenter(timerBox);
    }
}
