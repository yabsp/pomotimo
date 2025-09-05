package org.pomotimo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.logic.PresetManager;
import org.pomotimo.logic.utils.EditorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomotimoGUI extends Application implements UIRefreshable {

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean resizing = false;
    private Cursor resizeCursor = Cursor.DEFAULT;
    private Stage primaryStage;
    private BorderPane root;
    private TimerPane timerPane;
    private TaskPane taskPane;
    private HBox topBar;
    private final PresetManager presetManager = new PresetManager();
    private static final Logger logger = LoggerFactory.getLogger(PomotimoGUI.class);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setTitle("Pomotimo");
        primaryStage.getIcons().add(new Image("icons/logo_tomato_removebg.png"));

        /* Smooth window corners */
        this.root = new BorderPane();
        Rectangle clip = new Rectangle(800, 400);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);
        root.setStyle("-fx-background-color: #383736;");

        /* Window snap and resizable window */
        makeWindowResizable();


        /* Shadow effect for better window appearance */
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        root.setEffect(shadow);

        /* Add the timer pane and the task pane */
        this.timerPane = new TimerPane(presetManager, this);
        this.taskPane = new TaskPane(presetManager);

        initTopBar();

        HBox content = new HBox();
        content.setSpacing(10);

        content.getChildren().addAll(timerPane, taskPane);

        HBox.setHgrow(timerPane, Priority.ALWAYS);
        HBox.setHgrow(taskPane, Priority.ALWAYS);

        root.setCenter(content);

        /* Show the window */
        Scene scene = new Scene(root, 900, 500);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalstyle.css");
        primaryStage.show();
    }

    private void makeWindowResizable() {
        final int RESIZE_MARGIN = 6;

        root.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            double width = root.getWidth();
            double height = root.getHeight();

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

            root.setCursor(resizeCursor);
        });

        root.setOnMouseDragged(event -> {
            if (resizeCursor == Cursor.DEFAULT) return;
            resizing = true;

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            double stageX = primaryStage.getX();
            double stageY = primaryStage.getY();
            double stageW = primaryStage.getWidth();
            double stageH = primaryStage.getHeight();

            switch (resizeCursor.toString()) {
                case "NW_RESIZE":
                    primaryStage.setX(mouseX);
                    primaryStage.setY(mouseY);
                    primaryStage.setWidth(stageW - (mouseX - stageX));
                    primaryStage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "NE_RESIZE":
                    primaryStage.setY(mouseY);
                    primaryStage.setWidth(mouseX - stageX);
                    primaryStage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "SW_RESIZE":
                    primaryStage.setX(mouseX);
                    primaryStage.setWidth(stageW - (mouseX - stageX));
                    primaryStage.setHeight(mouseY - stageY);
                    break;
                case "SE_RESIZE":
                    primaryStage.setWidth(mouseX - stageX);
                    primaryStage.setHeight(mouseY - stageY);
                    break;
                case "W_RESIZE":
                    primaryStage.setX(mouseX);
                    primaryStage.setWidth(stageW - (mouseX - stageX));
                    break;
                case "E_RESIZE":
                    primaryStage.setWidth(mouseX - stageX);
                    break;
                case "N_RESIZE":
                    primaryStage.setY(mouseY);
                    primaryStage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "S_RESIZE":
                    primaryStage.setHeight(mouseY - stageY);
                    break;
            }
        });

        root.setOnMouseReleased(event -> {
            resizing = false;
        });
    }

    private void initTopBar() {
        /* Custom titlebar */
        this.topBar = new HBox();
        topBar.setId("custom-title-bar");

        MenuButton settingsButton = new MenuButton("Settings");
        settingsButton.getItems().addAll(
                new MenuItem("General Settings")
        );
        settingsButton.getStyleClass().add("topbar-button");
        MenuItem importItem = new MenuItem("Import");
        MenuItem exportItem = new MenuItem("Export");


        Menu manageMenu = new Menu("Manage");
        MenuItem createItem = new MenuItem("Create New");
        MenuItem editItem = new MenuItem("Edit Current Profile");
        MenuItem deleteItem = new MenuItem("Delete");
        createItem.setOnAction(e -> {
            timerPane.showPresetEditor(EditorMode.ADD_NEW);
        });

        editItem.setOnAction(e -> {
            timerPane.showPresetEditor(EditorMode.EDIT_OLD);
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
                logger.info("Switched to preset: {}", pr);
            });
            switchMenu.getItems().add(prItem);
        });

        MenuButton profileButton = new MenuButton("Profile");
        profileButton.getItems().addAll(importItem, exportItem, switchMenu, manageMenu);
        profileButton.getStyleClass().add("topbar-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimizeBtn = new Button();
        FontIcon minimizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MINIMIZE);
        minimizeIcon.setIconColor(Color.WHITE);
        minimizeBtn.setGraphic(minimizeIcon);

        Button maximizeBtn = new Button();
        FontIcon maximizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MAXIMIZE);
        maximizeIcon.setIconColor(Color.WHITE);
        maximizeBtn.setGraphic(maximizeIcon);

        Button closeBtn = new Button();
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);
        closeIcon.setIconColor(Color.WHITE);
        closeBtn.setGraphic(closeIcon);

        minimizeBtn.getStyleClass().add("topbar-button");
        maximizeBtn.getStyleClass().add("topbar-button");
        closeBtn.getStyleClass().add("topbar-button");

        minimizeBtn.setOnAction(e -> primaryStage.setIconified(true));
        maximizeBtn.setOnAction(e -> primaryStage.setMaximized(!primaryStage.isMaximized()));
        closeBtn.setOnAction(e -> {
            presetManager.shutDownScheduler();
            Platform.exit();
        });

        topBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        topBar.setOnMouseDragged(e -> {
            if(resizing) return;
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });

        ImageView appIcon = new ImageView(new Image("icons/logo_24x24.png"));
        appIcon.setFitHeight(18);
        appIcon.setFitWidth(18);
        HBox.setMargin(appIcon, new Insets(0, 5, 0, 10));

        topBar.getChildren().addAll(appIcon, settingsButton, profileButton, spacer, minimizeBtn, maximizeBtn, closeBtn);
        root.setTop(topBar);

        topBar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                primaryStage.setMaximized(!primaryStage.isMaximized());
            }
        });
    }

    @Override
    public void refreshTopBar() {
        initTopBar();
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
