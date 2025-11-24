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
import org.pomotimo.gui.config.GUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PomoFrame extends BorderPane implements UIRefreshable {
    protected static final Logger logger = LoggerFactory.getLogger(PomoFrame.class);
    double xOffset = 0;
    double yOffset = 0;
    double startX, startY, startWidth, startHeight;
    double startScreenX, startScreenY;
    boolean resizing = false;
    Cursor resizeCursor = Cursor.DEFAULT;
    Stage mainStage;
    TimerPane timerPane;
    TaskPane taskPane;
    HBox topBar;
    PresetManager presetManager;
    PresetImporterExporter importerExporter;

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

        this.setOnMousePressed(event -> {
            if (resizeCursor != Cursor.DEFAULT) {
                resizing = true;
                startX = mainStage.getX();
                startY = mainStage.getY();
                startWidth = mainStage.getWidth();
                startHeight = mainStage.getHeight();
                startScreenX = event.getScreenX();
                startScreenY = event.getScreenY();
            }
        });

        this.setOnMouseDragged(event -> {
            if (!resizing || resizeCursor == Cursor.DEFAULT) return;

            double rawDeltaX = event.getScreenX() - startScreenX;
            double rawDeltaY = event.getScreenY() - startScreenY;

            double newWidth = startWidth;
            double newHeight = startHeight;
            double newX = startX;
            double newY = startY;

            switch (resizeCursor.toString()) {
                case "W_RESIZE": // Left Resize
                    double effectiveDeltaW = Math.min(rawDeltaX, startWidth - GUIConstants.MIN_WIN_WIDTH);
                    newX = startX + effectiveDeltaW;
                    newWidth = startWidth - effectiveDeltaW;
                    break;

                case "E_RESIZE": // Right Resize
                    newWidth = Math.max(startWidth + rawDeltaX, GUIConstants.MIN_WIN_WIDTH);
                    break;

                case "N_RESIZE": // Top Resize
                    double effectiveDeltaN = Math.min(rawDeltaY, startHeight - GUIConstants.MIN_WIN_HEIGHT);
                    newY = startY + effectiveDeltaN;
                    newHeight = startHeight - effectiveDeltaN;
                    break;

                case "S_RESIZE": // Bottom Resize
                    newHeight = Math.max(startHeight + rawDeltaY, GUIConstants.MIN_WIN_WIDTH);
                    break;

                case "NW_RESIZE": // Top-Left
                    double effDX_NW = Math.min(rawDeltaX, startWidth - GUIConstants.MIN_WIN_WIDTH);
                    double effDY_NW = Math.min(rawDeltaY, startHeight - GUIConstants.MIN_WIN_HEIGHT);
                    newX = startX + effDX_NW;
                    newWidth = startWidth - effDX_NW;
                    newY = startY + effDY_NW;
                    newHeight = startHeight - effDY_NW;
                    break;

                case "NE_RESIZE": // Top-Right
                    double effDY_NE = Math.min(rawDeltaY, startHeight - GUIConstants.MIN_WIN_HEIGHT);
                    newWidth = Math.max(startWidth + rawDeltaX, GUIConstants.MIN_WIN_WIDTH);
                    newY = startY + effDY_NE;
                    newHeight = startHeight - effDY_NE;
                    break;

                case "SW_RESIZE": // Bottom-Left
                    double effDX_SW = Math.min(rawDeltaX, startWidth - GUIConstants.MIN_WIN_WIDTH);
                    newX = startX + effDX_SW;
                    newWidth = startWidth - effDX_SW;
                    newHeight = Math.max(startHeight + rawDeltaY, GUIConstants.MIN_WIN_HEIGHT);
                    break;

                case "SE_RESIZE": // Bottom-Right
                    newWidth = Math.max(startWidth + rawDeltaX, GUIConstants.MIN_WIN_WIDTH);
                    newHeight = Math.max(startHeight + rawDeltaY, GUIConstants.MIN_WIN_HEIGHT);
                    break;
            }

            if (mainStage.getWidth() != newWidth) mainStage.setWidth(newWidth);
            if (mainStage.getHeight() != newHeight) mainStage.setHeight(newHeight);
            if (mainStage.getX() != newX) mainStage.setX(newX);
            if (mainStage.getY() != newY) mainStage.setY(newY);
        });

        this.setOnMouseReleased(event -> {
            resizing = false;
            if (event.getPickResult().getIntersectedNode() == this) {
                this.setCursor(Cursor.DEFAULT);
            }
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
