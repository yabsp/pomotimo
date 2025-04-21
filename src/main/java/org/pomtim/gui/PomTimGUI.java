package org.pomtim.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setTitle("PomTim");
        primaryStage.getIcons().add(new Image("icons/logo_tomato_removebg.png"));

        BorderPane root = new BorderPane();
        Rectangle clip = new Rectangle(800, 400);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);
        root.setStyle("-fx-background-color: #ffffff;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        root.setEffect(shadow);


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
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });

        ImageView appIcon = new ImageView(new Image("icons/logo_24x24.png"));
        appIcon.setFitHeight(24);
        appIcon.setFitWidth(24);

        topBar.getChildren().addAll(appIcon, settingsButton, profileButton, spacer, minimizeBtn, maximizeBtn, closeBtn);
        root.setTop(topBar);

        Timer timerPane = new Timer();
        root.setLeft(timerPane);

        Scene scene = new Scene(root, 800, 400);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("css/style.css");
        primaryStage.show();
    }
}
