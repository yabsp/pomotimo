package org.pomotimo.gui;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.logic.Preset;
import org.pomotimo.logic.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteMenu extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(DeleteMenu.class);
    private final PresetManager presetManager;
    private final UIRefreshable refresher;
    @FXML private ListView<Preset> checkList;
    @FXML private Button deleteBtn;


    public DeleteMenu(PresetManager presetManager, UIRefreshable refresher) {
        this.presetManager = presetManager;
        this.refresher = refresher;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeleteMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Failed to load DeleteMenu.fxml", e);
        }
    }

    @FXML
    public void initialize() {
        refreshPresetList();
        deleteBtn.setOnAction(e -> {
            Preset pr = checkList.getSelectionModel().getSelectedItem();
            presetManager.removePreset(pr);
            presetManager.scheduleSave();
            checkList.getItems().remove(pr);
            refresher.refreshTimerPane();
            refresher.refreshTaskListView();
            refresher.refreshTopBar();
        });
    }

    public void refreshPresetList() {
        presetManager.getPresets().forEach(pr -> {
            checkList.getItems().add(pr);
        });
    }
}
