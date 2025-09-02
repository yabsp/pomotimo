package org.pomtim.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import org.pomtim.logic.Preset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.pomtim.logic.PresetManager;
import org.w3c.dom.Text;

public class PresetEditor extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(PresetEditor.class);
    private PresetManager presetManager;
    private TimerPane parentPane;
    @FXML private Button closeBtn;
    @FXML private TextField focusTimeField;
    @FXML private TextField shortBreakField;
    @FXML private TextField longBreakField;
    @FXML private TextField nameField;
    @FXML private Button saveBtn;
    @FXML private Button setDefaultsBtn;

    public PresetEditor(PresetManager presetManager, TimerPane parentPane) {
        this.presetManager = presetManager;
        this.parentPane = parentPane;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PresetEditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (
                IOException e) {
            throw new RuntimeException("Failed to load PresetEditor.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        closeBtn.setOnAction(e -> parentPane.refreshUI());
        saveBtn.setOnAction(e -> this.savePresetConfiguration());

        configureTimeField(focusTimeField, "25:00");
        configureTimeField(shortBreakField, "05:00");
        configureTimeField(longBreakField, "15:00");
        configureTextField(nameField, "preset" + (presetManager.getPresetCount()+1));

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
        logger.info("Saving preset");
        if(fieldEmpty(focusTimeField)){
            showMissingTimeAlert("Focus Time Field");
            return;
        } else if(fieldEmpty(shortBreakField)) {
            showMissingTimeAlert("Short Break Field");
            return;
        } else if(fieldEmpty(longBreakField)) {
            showMissingTimeAlert("longBreakField");
            return;
        }
        int focusSecs = extractTimeInSeconds(focusTimeField);
        int shortBrSecs = extractTimeInSeconds(shortBreakField);
        int longBrSecs = extractTimeInSeconds(longBreakField);
        if(nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Input required",
                    "Missing Name",
                    "Please enter a name for your preset before saving!");
            return;
        }
        Preset p = new Preset(nameField.getText(), focusSecs, shortBrSecs, longBrSecs);
        presetManager.addPreset(p);
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

    private void showMissingTimeAlert(String field){
        showAlert(Alert.AlertType.WARNING,
                "Input required",
                field,
                "Please enter a time into your " + field + " before saving!");
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        /* Add logos */
        Image logo = new Image(getClass().getResourceAsStream("/icons/logo_tomato_removebg.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(48);
        logoView.setFitHeight(48);
        alert.setGraphic(logoView);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/logo_24x24.png")));

        alert.showAndWait();
    }

}
