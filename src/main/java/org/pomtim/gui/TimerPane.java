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
import java.util.Optional;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pomtim.logic.PomodoroTimer;
import org.pomtim.logic.Preset;
import org.pomtim.logic.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerPane extends BorderPane {

    private Logger logger = LoggerFactory.getLogger(TimerPane.class);
    @FXML private Label timerLabel;
    @FXML private Button startButton;
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
            logger.info("Refreshing UI with a Preset");
            timerContainer.setVisible(true);
            timerContainer.setManaged(true);
            this.setCenter(timerContainer);
            setUIFromPreset();
        }
    }

    private void setUIFromPreset(){
        presetManager.getFirst().ifPresent(pr -> {
            int focusSec = pr.getDurationFocus();
            int shortBrSec = pr.getDurationShortBreak();
            int longBrSec = pr.getDurationLongBreak();
            timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));

            timer.setRemainingSeconds(focusSec);
            startButton.setOnAction(e -> {
                if(timer.isRunning()) {
                    timer.pause();
                    startButton.setText("Start");
                } else {
                    timer.start( seconds -> {
                        int min = seconds / 60;
                        int sec = seconds % 60;
                        String time = String.format("%02d:%02d", min, sec);
                        Platform.runLater(() -> timerLabel.setText(time));
                    });
                    startButton.setText("Stop");
                }

            });
            resetButton.setOnAction(e -> {
                if(timer.isRunning()){
                    startButton.setText("Start");
                }
                timer.reset(focusSec);
                timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));
            });
        });
    }

}
