package org.pomotimo.gui;


import javafx.application.Application;
import javafx.stage.Stage;

import org.pomotimo.gui.frame.MainFrame;
import org.pomotimo.gui.state.AppState;
import org.pomotimo.logic.utils.PresetImporterExporter;
import org.pomotimo.logic.preset.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomotimoGUI extends Application {

    private static Stage mainStage;
    private static PresetManager presetManager;
    private static PresetImporterExporter importerExporter;
    private static MainFrame mainFrame;
    private static final Logger logger = LoggerFactory.getLogger(PomotimoGUI.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        AppState appState = new AppState();
        PomotimoGUI.presetManager = new PresetManager(appState);
        PomotimoGUI.importerExporter = new PresetImporterExporter(presetManager);
        PomotimoGUI.mainStage = stage;
        logger.debug("Starting GUI...");
        PomotimoGUI.mainFrame = new MainFrame(presetManager, importerExporter, mainStage, appState);
        mainFrame.show();
    }
}
