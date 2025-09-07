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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.pomotimo.gui.utils.UIRefreshable;
import org.pomotimo.gui.utils.WindowFactory;
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
        this.primaryStage.initStyle(StageStyle.TRANSPARENT);

        this.primaryStage.setTitle("Pomotimo");
        this.primaryStage.getIcons().add(new Image("icons/logo_tomato_removebg.png"));

        this.root = new BorderPane();
        initPane(root);

        /* Window snap and resizable window */
        makeWindowResizable(this.primaryStage, root);

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
        this.primaryStage.setScene(scene);
        this.primaryStage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalstyle.css");
        this.primaryStage.show();
    }

    private void makeWindowResizable(Stage stage, Parent parent) {
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
            double stageX = stage.getX();
            double stageY = stage.getY();
            double stageW = stage.getWidth();
            double stageH = stage.getHeight();

            switch (resizeCursor.toString()) {
                case "NW_RESIZE":
                    stage.setX(mouseX);
                    stage.setY(mouseY);
                    stage.setWidth(stageW - (mouseX - stageX));
                    stage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "NE_RESIZE":
                    stage.setY(mouseY);
                    stage.setWidth(mouseX - stageX);
                    stage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "SW_RESIZE":
                    stage.setX(mouseX);
                    stage.setWidth(stageW - (mouseX - stageX));
                    stage.setHeight(mouseY - stageY);
                    break;
                case "SE_RESIZE":
                    stage.setWidth(mouseX - stageX);
                    stage.setHeight(mouseY - stageY);
                    break;
                case "W_RESIZE":
                    stage.setX(mouseX);
                    stage.setWidth(stageW - (mouseX - stageX));
                    break;
                case "E_RESIZE":
                    stage.setWidth(mouseX - stageX);
                    break;
                case "N_RESIZE":
                    stage.setY(mouseY);
                    stage.setHeight(stageH - (mouseY - stageY));
                    break;
                case "S_RESIZE":
                    stage.setHeight(mouseY - stageY);
                    break;
            }
        });

        parent.setOnMouseReleased(event -> {
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

        deleteItem.setOnAction(ev -> {
            DeleteMenu deleteMenu = new DeleteMenu(presetManager, this);
            Stage stage = new Stage();
            stage.setTitle("Delete Profiles");
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.getIcons().add(new Image("icons/logo_tomato_removebg.png"));

            BorderPane parent = new BorderPane();
            HBox bar = new HBox();
            bar.setMinHeight(20);
            initWindowControl(stage, bar, parent, WindowFactory.WindowType.MENU_WINDOW, new ArrayList<>());
            parent.setCenter(deleteMenu);
            initPane(parent);
            Scene scene = new Scene(parent, 600, 300);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalstyle.css");
            stage.show();
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

        initWindowControl(primaryStage, topBar, root, WindowFactory.WindowType.MAIN_WINDOW,
                List.of(settingsButton, profileButton));
    }

    private void initPane(Pane pane) {
        /* Smooth Window Corners*/
        Rectangle clip = new Rectangle(800, 400);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        clip.widthProperty().bind(pane.widthProperty());
        clip.heightProperty().bind(pane.heightProperty());
        pane.setClip(clip);
        pane.setStyle("-fx-background-color: #383736;");

        /* Shadow effect for better window appearance */
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        pane.setEffect(shadow);
    }

    private void initWindowControl(Stage stage, HBox bar, BorderPane pane, WindowFactory.WindowType windowType, List<Node> topBarElements) {
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

        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        closeBtn.setOnAction(e -> {
            switch (windowType) {
                case MAIN_WINDOW -> {
                    presetManager.shutDownScheduler();
                    Platform.exit();
                }
                case MENU_WINDOW -> stage.close();
            }

        });
        if (windowType == WindowFactory.WindowType.MAIN_WINDOW) {
            bar.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });

            bar.setOnMouseDragged(e -> {
                if(resizing) return;
                primaryStage.setX(e.getScreenX() - xOffset);
                primaryStage.setY(e.getScreenY() - yOffset);
            });
        }

        ImageView appIcon = new ImageView(new Image("icons/logo_24x24.png"));
        appIcon.setFitHeight(18);
        appIcon.setFitWidth(18);
        HBox.setMargin(appIcon, new Insets(0, 5, 0, 10));

        bar.getChildren().addAll(appIcon);

        topBarElements.forEach(node -> bar.getChildren().add(node));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().add(spacer);

        bar.getChildren().addAll(minimizeBtn, maximizeBtn, closeBtn);
        pane.setTop(bar);

        bar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
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
