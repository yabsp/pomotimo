package org.pomtim.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.BatchUpdateException;

import org.pomtim.logic.PresetManager;

public class PresetEditor extends BorderPane {

    private PresetManager presetManager;
    private TimerPane parentPane;
    @FXML private Button closeButton;

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
    }
}
