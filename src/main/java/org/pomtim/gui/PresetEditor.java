package org.pomtim.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.pomtim.logic.PresetManager;
import org.w3c.dom.Text;

public class PresetEditor extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(PresetEditor.class);
    private PresetManager presetManager;
    private TimerPane parentPane;
    @FXML private Button closeButton;
    @FXML private TextField focusTimeField;
    @FXML private TextField shortBreakField;
    @FXML private TextField longBreakField;

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
        closeButton.setOnAction(e -> parentPane.refreshUI());

        configureTimeField(focusTimeField, "25:00");
        configureTimeField(shortBreakField, "05:00");
        configureTimeField(longBreakField, "15:00");

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

        // Autosave
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            saveFieldValue(field, newVal);
        });
    }

    //TODO implement save logic
    private void saveFieldValue(TextField field, String value) {
        logger.info("Autosaving " + field.getId() + ": " + value);
    }

}
