package org.pomotimo.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletePresetView extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(DeletePresetView.class);
    private final PresetManager presetManager;
    private final UIRefreshable refresher;
    @FXML private ListView<Preset> checkList;
    @FXML private Button deleteBtn;


    public DeletePresetView(PresetManager presetManager, UIRefreshable refresher) {
        this.presetManager = presetManager;
        this.refresher = refresher;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeletePresetView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Failed to load DeletePresetView.fxml", e);
        }
    }

    @FXML
    public void initialize() {
        refreshPresetList();
        checkList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deletePreset(checkList.getSelectionModel().getSelectedItem());
            }
        });
        deleteBtn.setOnAction(e -> {
            deletePreset(checkList.getSelectionModel().getSelectedItem());
        });
    }

    private void deletePreset(Preset p) {
        Preset pr = checkList.getSelectionModel().getSelectedItem();
        presetManager.removePreset(pr);
        checkList.getItems().remove(pr);
        refresher.refreshTimerPane();
        refresher.refreshTaskPane();
        refresher.refreshTopBar();
        presetManager.scheduleSave();
    }

    public void refreshPresetList() {
        presetManager.getPresets().forEach(pr -> {
            checkList.getItems().add(pr);
        });
    }
}
