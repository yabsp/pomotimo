package org.pomotimo.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pomotimo.logic.PomoState;
import org.pomotimo.logic.PomoTimer;
import org.pomotimo.logic.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pomotimo.logic.PomoState.FOCUS;
import static org.pomotimo.logic.PomoState.LONGBR;
import static org.pomotimo.logic.PomoState.SHORTBR;

public class TimerPane extends BorderPane {

    private Logger logger = LoggerFactory.getLogger(TimerPane.class);
    @FXML private Label timerLabel;
    @FXML private Button startBtn;
    @FXML private Button resetBtn;
    @FXML private Button skipBtn;
    @FXML private Label stateLabel;
    @FXML private Label cycleLabel;
    @FXML private VBox timerContainer;
    private final PomoTimer timer = new PomoTimer();
    private PresetManager presetManager;
    private int cycleCounter;
    private PomoState state;
    private int focusSec;
    private int shortBrSec;
    private int longBrSec;

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
            plusIcon.setIconSize(18);
            createPresetButton.setGraphic(plusIcon);
            createPresetButton.getStyleClass().add("create-preset-button");
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
        state = PomoState.FOCUS;
        cycleCounter = 1;
        presetManager.getFirst().ifPresent(pr -> {
            focusSec = pr.getDurationFocus();
            shortBrSec = pr.getDurationShortBreak();
            longBrSec = pr.getDurationLongBreak();
            timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));

            timer.setRemainingSeconds(focusSec);
            startBtn.setOnAction(e -> {
                if(timer.isRunning()) {
                    timer.pause();
                    startBtn.setText("Start");
                } else {
                    timer.start( seconds -> {
                        int min = seconds / 60;
                        int sec = seconds % 60;
                        String time = String.format("%02d:%02d", min, sec);
                        Platform.runLater(() -> timerLabel.setText(time));
                        if (seconds <= 0) {
                            Platform.runLater(this::setNewPomoState);
                        }
                    });
                    startBtn.setText("Stop");
                }

            });
            resetBtn.setOnAction(e -> {
                if(timer.isRunning()){
                    startBtn.setText("Start");
                }
                timer.reset(focusSec);
                timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));
            });
            skipBtn.setOnAction(e -> setNewPomoState());
        });
    }

    private void setNewPomoState() {
        switch (state) {
            case FOCUS:
                if(cycleCounter >= 4) {
                    state = LONGBR;
                    cycleCounter = 1;
                    timer.setRemainingSeconds(longBrSec);
                    stateLabel.setText("Long Break");
                    timerLabel.setText(String.format("%02d:%02d", longBrSec / 60, longBrSec % 60));
                } else {
                    state = SHORTBR;
                    cycleCounter += 1;
                    timer.setRemainingSeconds(shortBrSec);
                    stateLabel.setText("Short Break");
                    timerLabel.setText(String.format("%02d:%02d", shortBrSec / 60, shortBrSec % 60));
                }
                break;
            case SHORTBR:
                state = FOCUS;
                stateLabel.setText("Focus");
                cycleLabel.setText("Cycle: " + cycleCounter + " / 4");
                timer.setRemainingSeconds(focusSec);
                timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));
                break;
            case LONGBR:
                state = PomoState.FOCUS;
                stateLabel.setText("Focus");
                cycleLabel.setText("Cycle: " + cycleCounter + " / 4");
                timer.setRemainingSeconds(focusSec);
                timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));
                break;
        }
    }

}
