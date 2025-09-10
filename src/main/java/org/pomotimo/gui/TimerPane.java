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
import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.logic.utils.EditorMode;
import org.pomotimo.logic.PomoTimer;
import org.pomotimo.logic.preset.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JavaFX view component that serves as the main timer display and control panel.
 * It manages the visual representation of the countdown timer, provides user controls
 * (start, pause, reset, skip), and handles the Pomodoro state logic (Focus, Short Break, Long Break).
 */
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
    private final PresetManager presetManager;
    private final UIRefreshable refresher;
    private int cycleCounter;
    private PomoState state;
    private int focusSec;
    private int shortBrSec;
    private int longBrSec;

    private enum PomoState {
        FOCUS,
        SHORTBR,
        LONGBR
    }

    /**
     * Constructs the TimerPane.
     *
     * @param presetManager The manager for accessing preset data, such as timer durations.
     * @param refresher     An interface implementation used to refresh other UI components like the top bar.
     */
    public TimerPane(PresetManager presetManager, UIRefreshable refresher) {
        this.presetManager = presetManager;
        this.refresher = refresher;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TimerPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Failed to load TimerPane.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        refreshUI();
    }

    /**
     * Displays the {@link PresetEditor} within this pane, allowing the user to create or edit a preset.
     *
     * @param mode The mode for the editor, either {@link EditorMode#ADD_NEW} or {@link EditorMode#EDIT_OLD}.
     */
    public void showPresetEditor(EditorMode mode){
        PresetEditor presetEditor = new PresetEditor(presetManager, this, mode);
        this.setCenter(presetEditor);
    }

    /**
     * Refreshes the user interface of the timer pane.
     * If no presets exist, it displays a button to create a new one. Otherwise, it
     * configures the timer display based on the currently selected preset.
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
            createPresetButton.setOnAction(e -> showPresetEditor(EditorMode.ADD_NEW));
            this.setCenter(createPresetButton);
        } else {
            logger.debug("Refreshing UI with a Preset");
            timerContainer.setVisible(true);
            timerContainer.setManaged(true);
            this.setCenter(timerContainer);
            setUIFromPreset();
        }
    }

    private void setUIFromPreset(){
        state = PomoState.FOCUS;
        cycleCounter = 1;
        presetManager.getCurrentPreset().ifPresent(pr -> {
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
                    if(presetManager.player.isPlaying()) {
                        presetManager.player.stop();
                    }
                    timer.start( seconds -> {
                        int min = seconds / 60;
                        int sec = seconds % 60;
                        String time = String.format("%02d:%02d", min, sec);
                        Platform.runLater(() -> timerLabel.setText(time));
                        if (seconds <= 0) {
                            timer.pause();
                            Platform.runLater(() -> {
                                presetManager.player.play();
                                setNewPomoState();
                                startBtn.setText("Start");
                            });
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
                    state = PomoState.LONGBR;
                    cycleCounter = 1;
                    timer.setRemainingSeconds(longBrSec);
                    stateLabel.setText("Long Break");
                    timerLabel.setText(String.format("%02d:%02d", longBrSec / 60, longBrSec % 60));
                } else {
                    state = PomoState.SHORTBR;
                    cycleCounter += 1;
                    timer.setRemainingSeconds(shortBrSec);
                    stateLabel.setText("Short Break");
                    timerLabel.setText(String.format("%02d:%02d", shortBrSec / 60, shortBrSec % 60));
                }
                break;
            case SHORTBR, LONGBR:
                state = PomoState.FOCUS;
                stateLabel.setText("Focus");
                cycleLabel.setText("Cycle: " + cycleCounter + " / 4");
                timer.setRemainingSeconds(focusSec);
                timerLabel.setText(String.format("%02d:%02d", focusSec / 60, focusSec % 60));
                break;
        }
    }

    /**
     * Triggers a refresh of the main application's top bar via the refresher interface.
     */
    public void refreshTopBar() {
        refresher.refreshTopBar();
    }

    /**
     * Triggers a refresh of the task list view via the refresher interface.
     */
    public void refreshTaskListView() {
        refresher.refreshTaskListView();
    }

}
