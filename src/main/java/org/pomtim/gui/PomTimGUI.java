package org.pomtim.gui;

import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

public class PomTimGUI extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean resizing = false;
    private Cursor resizeCursor = Cursor.DEFAULT;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setTitle("PomTim");
        primaryStage.getIcons().add(new Image("icons/logo_tomato_removebg.png"));

        /* Smooth window corners */
        BorderPane root = new BorderPane();
        Rectangle clip = new Rectangle(800, 400);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);
        root.setStyle("-fx-background-color: #383736;");

        /* Window snap and resizable window */
        makeWindowResizable(primaryStage, root);


        /* Shadow effect for better window appearance */
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        root.setEffect(shadow);

        /* Custom titlebar */
        HBox topBar = new HBox();
        topBar.setId("custom-title-bar");

        MenuButton settingsButton = new MenuButton("Settings");
        settingsButton.getItems().addAll(
                new MenuItem("General Settings")
        );
        settingsButton.getStyleClass().add("topbar-button");

        MenuButton profileButton = new MenuButton("Profile");
        profileButton.getItems().addAll(
                new MenuItem("Import"),
                new MenuItem("Export")
        );
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
        closeBtn.setOnAction(e -> Platform.exit());

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

        /* Add the timer pane and the task pane */
        TimerPane timerPane = new TimerPane();
        TaskPane taskPane = new TaskPane();

        HBox content = new HBox();
        content.setSpacing(10);

        content.getChildren().addAll(timerPane, taskPane);

        HBox.setHgrow(timerPane, Priority.ALWAYS);
        HBox.setHgrow(taskPane, Priority.ALWAYS);

        root.setCenter(content);

        /* Show the window */
        Scene scene = new Scene(root, 800, 400);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalStyle.css");
        primaryStage.show();
    }

    private void makeWindowResizable(Stage stage, Region root) {
        final int RESIZE_MARGIN = 4;

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

        root.setOnMouseReleased(event -> {
            resizing = false;
        });
    }


}
