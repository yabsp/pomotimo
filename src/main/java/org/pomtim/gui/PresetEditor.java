package org.pomtim.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

import org.pomtim.logic.PomodoroTimer;
import org.pomtim.logic.PresetManager;

public class PresetEditor extends BorderPane {

    private PresetManager presetManager;

    public PresetEditor(PresetManager presetManager) {
        this.presetManager = presetManager;
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
}
