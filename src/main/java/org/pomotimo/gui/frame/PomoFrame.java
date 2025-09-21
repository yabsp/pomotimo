package org.pomotimo.gui.frame;

import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.pomotimo.gui.TaskPane;
import org.pomotimo.gui.TimerPane;
import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.utils.PresetImporterExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PomoFrame extends BorderPane implements UIRefreshable {
    protected static final Logger logger = LoggerFactory.getLogger(PomoFrame.class);
    double xOffset = 0;
    double yOffset = 0;
    boolean resizing = false;
    Cursor resizeCursor = Cursor.DEFAULT;
    Stage mainStage;
    TimerPane timerPane;
    TaskPane taskPane;
    HBox topBar;
    PresetManager presetManager;
    PresetImporterExporter importerExporter;
    final String ICON_PATH = "/icons/app_icon.png";

    public enum ViewType {
        DELETE_VIEW,
        EXPORT_VIEW
    }

    public PomoFrame (PresetManager presetManager,
                      PresetImporterExporter importerExporter,
                      TimerPane timerPane,
                      TaskPane taskPane,
                      Stage mainStage) {
        this.presetManager = presetManager;
        this.importerExporter = importerExporter;
        this.taskPane = taskPane;
        this.timerPane = timerPane;
        this.mainStage = mainStage;
    }

    public PomoFrame(PresetManager presetManager,
                     PresetImporterExporter importerExporter,
                     Stage mainStage) {
        this.presetManager = presetManager;
        this.importerExporter = importerExporter;
        this.mainStage = mainStage;
    }

    protected abstract void createTopBar();
    protected abstract void initialize();

    protected void makeWindowResizable() {
        final int RESIZE_MARGIN = 6;

        this.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            double width = this.getWidth();
            double height = this.getHeight();

            if (x < RESIZE_MARGIN && y < RESIZE_MARGIN) {
                resizeCursor = Cursor.NW_RESIZE;
            } else if (x > width - RESIZE_MARGIN && y < RESIZE_MARGIN) {
                resizeCursor = Cursor.NE_RESIZE;
            } else if (x < RESIZE_MARGIN && y > height - RESIZE_MARGIN) {
                resizeCursor = Cursor.SW_RESIZE;
            } else if (x > width - RESIZE_MARGIN && y > height - RESIZE_MARGIN) {
                resizeCursor = Cursor.SE_RESIZE;
            } else if (x < RESIZE_MARGIN) {
                resizeCursor = Cursor.W_RESIZE;
            } else if (x > width - RESIZE_MARGIN) {
                resizeCursor = Cursor.E_RESIZE;
            } else if (y < RESIZE_MARGIN) {
                resizeCursor = Cursor.N_RESIZE;
            } else if (y > height - RESIZE_MARGIN) {
                resizeCursor = Cursor.S_RESIZE;
            } else {
                resizeCursor = Cursor.DEFAULT;
            }

            this.setCursor(resizeCursor);
        });

        this.setOnMouseDragged(event -> {
            if (resizeCursor == Cursor.DEFAULT) return;
            resizing = true;

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            double stageX = mainStage.getX();
            double stageY = mainStage.getY();
            double stageW = mainStage.getWidth();
            double stageH = mainStage.getHeight();

            switch (resizeCursor.toString()) {
                case "NW_RESIZE":
                    mainStage.setX(mouseX);
                    mainStage.setY(mouseY);
                    mainStage.setWidth(stageW - (mouseX - stageX));
                    mainStage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "NE_RESIZE":
                    mainStage.setY(mouseY);
                    mainStage.setWidth(mouseX - stageX);
                    mainStage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "SW_RESIZE":
                    mainStage.setX(mouseX);
                    mainStage.setWidth(stageW - (mouseX - stageX));
                    mainStage.setHeight(mouseY - stageY);
                    break;
                case "SE_RESIZE":
                    mainStage.setWidth(mouseX - stageX);
                    mainStage.setHeight(mouseY - stageY);
                    break;
                case "W_RESIZE":
                    mainStage.setX(mouseX);
                    mainStage.setWidth(stageW - (mouseX - stageX));
                    break;
                case "E_RESIZE":
                    mainStage.setWidth(mouseX - stageX);
                    break;
                case "N_RESIZE":
                    mainStage.setY(mouseY);
                    mainStage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "S_RESIZE":
                    mainStage.setHeight(mouseY - stageY);
                    break;
            }
        });

        this.setOnMouseReleased(event -> {
            resizing = false;
        });

    }

    protected void makeWindowDraggable() {
        topBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        topBar.setOnMouseDragged(e -> {
            if(resizing) return;
            mainStage.setX(e.getScreenX() - xOffset);
            mainStage.setY(e.getScreenY() - yOffset);
        });
    }

    protected void createFrameEffects() {
        /* Smooth Window Corners*/
        Rectangle clip = new Rectangle(800, 400);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());
        this.setClip(clip);
        this.setStyle("-fx-background-color: #383736;");

        /* Shadow effect for better window appearance */
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        this.setEffect(shadow);
    }

    public void show() {
        mainStage.show();
    }

    @Override
    public void refreshTopBar() {
        createTopBar();
    }

    @Override
    public void refreshTimerPane() {
        timerPane.refreshUI();
    }

    @Override
    public void refreshTaskPane() {
        taskPane.refreshUI();
    }

    @Override
    public void refreshTaskListView() {
        taskPane.refreshTaskListView();
    }
}
