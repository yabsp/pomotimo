package org.pomotimo.gui;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import org.pomotimo.gui.state.AppState;
import org.pomotimo.gui.state.TimerViewState;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JavaFX view component that provides a user interface for selecting and deleting
 * one or more {@link Preset} objects. This view is typically displayed in a
 * secondary window or dialog.
 */
public class DeletePresetView extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(DeletePresetView.class);
    private final PresetManager presetManager;
    private final AppState appState;
    @FXML private ListView<Preset> checkList;
    @FXML private Button deleteBtn;

    /**
     * Constructs the DeletePresetView.
     *
     * @param presetManager The manager responsible for preset data logic, used to delete presets.
     * @param appState     The state of the application.
     */
    public DeletePresetView(PresetManager presetManager, AppState appState) {
        this.presetManager = presetManager;
        this.appState = appState;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeletePresetView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            getStylesheets().add(getClass().getResource("/css/generalstyle.css").toExternalForm());
        } catch (IOException e) {
            logger.error("Failed to load DeletePresetView.fxml", e);
        } catch (NullPointerException e) {
            logger.error("Stylesheet not found", e);
        }
    }

    /**
     * Initializes the view after its root element has been processed.
     * This method is automatically called by the FXML loader. It sets up the selection mode
     * for the list view and wires up the event handlers for the delete button and keyboard shortcuts.
     */
    @FXML
    public void initialize() {
        checkList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshPresetList();
        checkList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deletePreset(List.copyOf(checkList.getSelectionModel().getSelectedItems()));
            }
        });
        deleteBtn.setOnAction(e -> {
            deletePreset(List.copyOf(checkList.getSelectionModel().getSelectedItems()));
        });
    }

    /**
     * Deletes the given list of presets from the application.
     * It removes them from the PresetManager and the UI, then triggers a refresh
     * of other UI components and schedules a save of the preset data.
     *
     * @param pList The list of {@link Preset} objects to be deleted.
     */
    private void deletePreset(List<Preset> pList) {
        pList.forEach(pr -> {
            logger.debug("Preset to delete: {}", pr);
            presetManager.removePreset(pr);
            if (presetManager.getCurrentPreset().isPresent()) {
                appState.setCurrentPreset(presetManager.getCurrentPreset().get());
                appState.setTimerViewState(TimerViewState.TIMER);
            } else {
                appState.setTimerViewState(TimerViewState.EMPTY);
            }
            checkList.getItems().remove(pr);
        });
        presetManager.scheduleSave();
    }

    /**
     * Populates the list view with the current presets from the PresetManager.
     * This can be used to initialize or refresh the list.
     */
    public void refreshPresetList() {
        checkList.getItems().clear();
        checkList.getItems().addAll(presetManager.getPresets());
    }
}
