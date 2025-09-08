package org.pomotimo.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.sun.tools.javac.Main;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.pomotimo.gui.frame.MainFrame;
import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.gui.frame.PomoFrame;
import org.pomotimo.logic.PresetManager;
import org.pomotimo.logic.utils.EditorMode;
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
