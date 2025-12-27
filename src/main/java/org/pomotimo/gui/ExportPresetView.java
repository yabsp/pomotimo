package org.pomotimo.gui;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.logic.config.AppConstants;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.utils.PresetImporterExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JavaFX view component that provides a user view for selecting a single
 * {@link Preset} and exporting it to a file. This view is typically displayed
 * in a secondary window or dialog.
 */
public class ExportPresetView extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(ExportPresetView.class);
    private final PresetManager presetManager;
    private final PresetImporterExporter importerExporter;
    @FXML private ListView<Preset> checkList;
    @FXML private Button exportBtn;

    /**
     * Constructs the ExportPresetView.
     *
     * @param presetManager The manager responsible for preset data logic, used to retrieve presets.
     * @param importerExporter The utility for handling the preset export process.
     */
    public ExportPresetView(PresetManager presetManager,
                            PresetImporterExporter importerExporter) {
        this.presetManager = presetManager;
        this.importerExporter = importerExporter;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExportPresetView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
        } catch (IOException e) {
            logger.error("Failed to load ExportPresetView.fxml", e);
        } catch (NullPointerException e) {
            logger.error("Stylesheet not found", e);
        }
    }

    /**
     * Initializes the view after its root element has been processed.
     * This method is automatically called by the FXML loader. It populates the preset list
     * and sets up event handlers for the export button and keyboard shortcuts.
     */
    @FXML
    public void initialize() {
        refreshPresetList();
        checkList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                exportPreset(checkList.getSelectionModel().getSelectedItem());
            }
        });
        exportBtn.setOnAction(e -> {
            exportPreset(checkList.getSelectionModel().getSelectedItem());
        });
    }

    /**
     * Handles the logic for exporting the selected preset.
     * It opens a file chooser dialog for the user to select a save location and
     * then initiates the export process, showing a confirmation or error alert.
     *
     * @param p The preset selected in the list view.
     */
    private void exportPreset(Preset p) {
        Preset pr = checkList.getSelectionModel().getSelectedItem();
        if (pr == null) {
            AlertFactory.alert(Alert.AlertType.WARNING,
                    "No Selection", "",
                    "Please select a preset to export.").showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Preset");
        fileChooser.setInitialFileName(pr.getName().replaceAll("\\s+", "_") + AppConstants.FILE_TYPE);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pomotimo Preset Files", "*" + AppConstants.FILE_TYPE)
        );

        Stage stage = (Stage) exportBtn.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            boolean success = importerExporter.exportPreset(pr, file);
            if (success) {
                AlertFactory.alert(Alert.AlertType.INFORMATION,
                        "Export Successful", "",
                        "The preset was exported successfully.").showAndWait();
            } else {
                AlertFactory.alert(Alert.AlertType.ERROR, "Export Failed", "",
                        "There was an error exporting the preset.").showAndWait();
            }
        }

    }

    /**
     * Clears and repopulates the list view with the current presets from the PresetManager.
     */
    public void refreshPresetList() {
        checkList.getItems().clear();
        checkList.getItems().addAll(presetManager.getPresets());
    }
}
