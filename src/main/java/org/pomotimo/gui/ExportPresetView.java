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
import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.utils.PresetImporterExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportPresetView extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(ExportPresetView.class);
    private final PresetManager presetManager;
    private final PresetImporterExporter importerExporter;
    private final UIRefreshable refresher;
    @FXML private ListView<Preset> checkList;
    @FXML private Button exportBtn;

    public ExportPresetView(PresetManager presetManager,
                            PresetImporterExporter importerExporter, UIRefreshable refresher) {
        this.presetManager = presetManager;
        this.refresher = refresher;
        this.importerExporter = importerExporter;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExportPresetView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (
                IOException e) {
            logger.error("Failed to load DeletePresetView.fxml", e);
        }
    }

    @FXML
    public void initialize() {
        refreshPresetList();
        checkList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                exportPreset(checkList.getSelectionModel().getSelectedItem());
            }
        });
        exportBtn.setOnAction(e -> {
            exportPreset(checkList.getSelectionModel().getSelectedItem());
        });
    }

    private void exportPreset(Preset p) {
        Preset pr = checkList.getSelectionModel().getSelectedItem();
        if (pr == null) {
            AlertFactory.alert(Alert.AlertType.WARNING,
                    "No Selection", "",
                    "Please select a preset to export.").showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Profile");
        fileChooser.setInitialFileName(pr.getName().replaceAll("\\s+", "_") + ".pomo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pomotimo Profile Files", "*.pomo")
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

    public void refreshPresetList() {
        presetManager.getPresets().forEach(pr -> {
            checkList.getItems().add(pr);
        });
    }
}
