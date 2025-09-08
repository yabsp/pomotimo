package org.pomotimo.gui;


import javafx.application.Application;
import javafx.stage.Stage;

import org.pomotimo.gui.frame.MainFrame;
import org.pomotimo.logic.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomotimoGUI extends Application {

    private static Stage mainStage;
    private static PresetManager presetManager;
    private static final Logger logger = LoggerFactory.getLogger(PomotimoGUI.class);

    @Override
    public void start(Stage stage) {
        PomotimoGUI.presetManager = new PresetManager();
        PomotimoGUI.mainStage = stage;
        logger.debug("Starting main frame.");
        MainFrame mainFrame = new MainFrame(presetManager, mainStage);
        mainFrame.show();
    }
}
