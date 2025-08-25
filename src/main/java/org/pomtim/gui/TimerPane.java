package org.pomtim.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pomtim.logic.PomodoroTimer;
import org.pomtim.logic.PresetManager;

public class TimerPane extends BorderPane {

    @FXML private Label timerLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    @FXML private VBox timerContainer;
    private final PomodoroTimer timer = new PomodoroTimer();
    private PresetManager presetManager;
    private Node timerUI;

    public TimerPane(PresetManager presetManager) {
        this.presetManager = presetManager;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TimerPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TimerPane.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        refreshUI();
    }

    private void showPresetEditor(){
        PresetEditor presetEditor = new PresetEditor(presetManager, this);
        this.setCenter(presetEditor);
    }

    /**
     * Sets up the UI of the timer if the {@link PresetManager} contains a Preset.
     * Otherwise, show a "Create Preset" Button.
     */
    public void refreshUI () {
        if(!presetManager.hasPresets()) {
            timerContainer.setVisible(false);
            timerContainer.setManaged(false);

            Button createPresetButton = new Button("Create Preset");
            FontIcon plusIcon = new FontIcon(FontAwesomeSolid.PLUS_SQUARE);
            plusIcon.setIconColor(Color.WHITE);
            createPresetButton.setGraphic(plusIcon);
            createPresetButton.setOnAction(e -> showPresetEditor());
            this.setCenter(createPresetButton);
        } else {

            timerContainer.setVisible(true);
            timerContainer.setManaged(true);
            /* Init with buttons and clock directly */
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
        }
    }

}
