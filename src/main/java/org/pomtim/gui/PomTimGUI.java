package org.pomtim.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PomTimGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PomTim");
        primaryStage.getIcons().add(new Image("icons/logo_tomato_removebg.png"));

        BorderPane root = new BorderPane();
        Timer timerPane = new Timer();

        root.setLeft(timerPane);
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.getScene().getStylesheets().add("css/style.css");
        primaryStage.show();
    }
}
