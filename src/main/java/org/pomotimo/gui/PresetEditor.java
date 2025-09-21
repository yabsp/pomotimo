package org.pomotimo.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.utils.EditorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.pomotimo.logic.preset.PresetManager;

public class PresetEditor extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(PresetEditor.class);
    @FXML private Button closeBtn;
    @FXML private TextField focusTimeField;
    @FXML private TextField shortBreakField;
    @FXML private TextField longBreakField;
    @FXML private TextField nameField;
    @FXML private Button saveBtn;
    @FXML private Button setDefaultsBtn;
    private final PresetManager presetManager;
    private final TimerPane parentPane;
    private final Preset currentPreset;

    public PresetEditor(PresetManager presetManager, TimerPane parentPane, Preset currentPreset) {
        this.presetManager = presetManager;
        this.parentPane = parentPane;
        this.currentPreset = currentPreset;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PresetEditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            getStylesheets().add(getClass().getResource("/css/generalstyle.css").toExternalForm());
        } catch (IOException e) {
            logger.error("Failed to load PresetEditor.fxml", e);
        } catch (NullPointerException e) {
            logger.error("Stylesheet not found", e);
        }
    }

    @FXML
    private void initialize() {
        closeBtn.setOnAction(e -> parentPane.refreshUI());
        saveBtn.setOnAction(e -> savePresetConfiguration());
        setDefaultsBtn.setOnAction(e -> setTextFields(EditorMode.RESET));
        setTextFields(EditorMode.EDIT);
    }

    private void configureTimeField(TextField field, String initialValue) {

        field.setText(initialValue);
        field.setTextFormatter(new javafx.scene.control.TextFormatter<String>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) return change;
            if (!newText.matches("\\d{0,2}(:\\d{0,2})?") || newText.length() > 5) return null;

            String[] parts = newText.split(":");
            try {
                if (parts.length > 0 && !parts[0].isEmpty() && Integer.parseInt(parts[0]) > 59) return null;
                if (parts.length > 1 && !parts[1].isEmpty() && Integer.parseInt(parts[1]) > 59) return null;
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        }));
    }

    public void savePresetConfiguration() {
        logger.debug("Saving preset");
        if (fieldEmpty(focusTimeField)) {
            AlertFactory.emptyTimeFieldAlert("Focus Time Field").showAndWait();
            return;
        } else if (fieldEmpty(shortBreakField)) {
            AlertFactory.emptyTimeFieldAlert("Short Break Field").showAndWait();
            return;
        } else if (fieldEmpty(longBreakField)) {
            AlertFactory.emptyTimeFieldAlert("Long Break Field").showAndWait();
            return;
        }
        int focusSecs = extractTimeInSeconds(focusTimeField);
        int shortBrSecs = extractTimeInSeconds(shortBreakField);
        int longBrSecs = extractTimeInSeconds(longBreakField);
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            AlertFactory.emptyNameFieldAlert("Preset Name Field").showAndWait();
            return;
        }
        currentPreset.setName(nameField.getText())
                     .setDurationFocus(focusSecs)
                     .setDurationShortBreak(shortBrSecs)
                     .setDurationLongBreak(longBrSecs);

        if (!presetManager.contains(currentPreset)) {
            presetManager.addPreset(currentPreset);
            presetManager.setCurrentPreset(currentPreset);
        }
        presetManager.scheduleSave();
        parentPane.refreshTopBar();
        parentPane.refreshTaskListView();
        parentPane.refreshUI();
    }

    private void configureTextField(TextField field, String initialValue) {
        field.setText(initialValue);
    }

    private boolean fieldEmpty(TextField field) {
        return field.getText() == null | field.getText().trim().isEmpty();
    }

    private int extractTimeInSeconds(TextField field) {
        String[] temp = field.getText().split(":");
        return Integer.parseInt(temp[0])*60 + Integer.parseInt(temp[1]);
    }

    private void setTextFields(EditorMode mode) {
        switch (mode){
            case EDIT -> {
                configureTimeField(focusTimeField, String.format("%02d:%02d", currentPreset.getDurationFocus() / 60, currentPreset.getDurationFocus() % 60));
                configureTimeField(shortBreakField, String.format("%02d:%02d", currentPreset.getDurationShortBreak() / 60, currentPreset.getDurationShortBreak() % 60));
                configureTimeField(longBreakField, String.format("%02d:%02d", currentPreset.getDurationLongBreak() / 60, currentPreset.getDurationLongBreak() % 60));
                configureTextField(nameField, currentPreset.getName());
            }

            case RESET -> {
                configureTimeField(focusTimeField, "25:00");
                configureTimeField(shortBreakField, "05:00");
                configureTimeField(longBreakField, "15:00");
                configureTextField(nameField, currentPreset.getName());

            }
        }
    }

}
