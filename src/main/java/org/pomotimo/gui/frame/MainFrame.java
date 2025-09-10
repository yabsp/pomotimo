package org.pomotimo.gui.frame;

import java.io.File;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.pomotimo.gui.ExportPresetView;
import org.pomotimo.gui.TaskPane;
import org.pomotimo.gui.TimerPane;
import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.gui.utils.ElementsFactory;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.utils.EditorMode;
import org.pomotimo.logic.utils.PresetImporterExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainFrame extends PomoFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

    public MainFrame(PresetManager presetManager,
                     PresetImporterExporter importerExporter,
                     Stage mainStage) {
        super(presetManager, importerExporter, mainStage);
        initialize();
    }

    @Override
    protected void createTopBar() {
        this.topBar = new HBox();
        topBar.getStyleClass().add("custom-title-bar");

        MenuButton settingsButton = new MenuButton("Settings");
        settingsButton.getItems().addAll(
                new MenuItem("General Settings")
        );
        settingsButton.getStyleClass().add("topbar-button");
        MenuItem importItem = new MenuItem("Import");
        MenuItem exportItem = new MenuItem("Export");


        Menu manageMenu = new Menu("Manage");
        MenuItem createItem = new MenuItem("Create New");
        MenuItem editItem = new MenuItem("Edit Current");
        MenuItem deleteItem = new MenuItem("Delete");
        createItem.setOnAction(e -> {
            timerPane.showPresetEditor(EditorMode.ADD_NEW);
        });

        importItem.setOnAction(e -> handleImportPreset());
        exportItem.setOnAction(e -> handleExportPreset());

        editItem.setOnAction(e -> {
            if(presetManager.getCurrentPreset().isPresent()){
                timerPane.showPresetEditor(EditorMode.EDIT_OLD);
            } else {
                AlertFactory.alert(Alert.AlertType.WARNING, "Edit Not Possible",
                        "No Current Preset",
                        "Please select or create a preset in order to edit it!").showAndWait();
            }

        });

        deleteItem.setOnAction(ev -> {
            MenuFrame menuFrame = new MenuFrame(presetManager, importerExporter,
                    timerPane, taskPane, mainStage, ViewType.DELETE_VIEW);
            menuFrame.show();
        });

        manageMenu.getItems().addAll(createItem, editItem, deleteItem);
        Menu switchMenu = new Menu("Select");
        ToggleGroup presetsGroup = new ToggleGroup();

        presetManager.getPresets().forEach(pr -> {
            RadioMenuItem prItem = new RadioMenuItem(pr.getName());
            prItem.setToggleGroup(presetsGroup);
            if (presetManager.getCurrentPreset().map(p -> p.equals(pr)).orElse(false)) {
                prItem.setSelected(true);
            }
            prItem.setOnAction(e -> {
                presetManager.setCurrentPreset(pr);
                prItem.setSelected(true);
                timerPane.refreshUI();
                taskPane.refreshTaskListView();
                logger.debug("Switched to preset: {}", pr);
            });
            switchMenu.getItems().add(prItem);
        });

        MenuButton presetButton = new MenuButton("Preset");
        presetButton.getItems().addAll(importItem, exportItem, switchMenu, manageMenu);
        presetButton.getStyleClass().add("topbar-button");

        Button minimizeBtn = ElementsFactory.minimizeBtn();
        Button maximizeBtn = ElementsFactory.maximizeBtn();
        Button closeBtn = ElementsFactory.closeBtn();

        minimizeBtn.setOnAction(e -> mainStage.setIconified(true));
        maximizeBtn.setOnAction(e -> mainStage.setMaximized(!mainStage.isMaximized()));
        closeBtn.setOnAction(e -> {
            presetManager.shutDownScheduler();
            Platform.exit();
        });

        makeWindowDraggable();

        topBar.getChildren().addAll(ElementsFactory.appIcon(),
                settingsButton, presetButton,
                ElementsFactory.spacer(),
                minimizeBtn, maximizeBtn, closeBtn);
        this.setTop(topBar);

        topBar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                mainStage.setMaximized(!mainStage.isMaximized());
            }
        });
    }

    private void handleExportPreset() {
        MenuFrame exportFrame = new MenuFrame(presetManager, importerExporter,
                timerPane, taskPane, mainStage, ViewType.EXPORT_VIEW);
        exportFrame.show();
    }

    private void handleImportPreset() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Preset");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PomoTimo Preset Files", "*.pomo")
        );

        File file = fileChooser.showOpenDialog(mainStage);

        if (file != null) {
            Optional<Preset> importedPreset = importerExporter.importPreset(file);
            if (importedPreset.isPresent()) {
                refreshTimerPane();
                refreshTaskPane();
                refreshTopBar();
                AlertFactory.alert(Alert.AlertType.INFORMATION, "Import Successful", "",
                        "Preset '" + importedPreset.get().getName() + "' was imported.").showAndWait();
            } else {
                AlertFactory.alert(Alert.AlertType.ERROR, "Import Failed", "",
                        "Could not import the preset from the selected file.").showAndWait();
            }
        }
    }

    @Override
    protected void initialize() {
        this.mainStage.initStyle(StageStyle.TRANSPARENT);
        this.mainStage.setTitle("Pomotimo");
        this.mainStage.getIcons().add(new Image(ICON_PATH));

        createFrameEffects();
        makeWindowResizable();
        createTopBar();

        HBox content = new HBox();
        content.setSpacing(10);

        this.timerPane = new TimerPane(presetManager, this);
        this.taskPane = new TaskPane(presetManager);

        content.getChildren().addAll(timerPane, taskPane);
        HBox.setHgrow(timerPane, Priority.ALWAYS);
        HBox.setHgrow(taskPane, Priority.ALWAYS);

        this.setCenter(content);

        Scene scene = new Scene(this, 900, 500);
        scene.setFill(Color.TRANSPARENT);
        this.mainStage.setScene(scene);
        this.mainStage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalstyle.css");
    }
}
