package org.pomotimo.gui.utils;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ElementsFactory {
    static final String ICON_SM_PATH = "icons/logo_24x24.png";

    public static Button minimizeBtn() {
        Button minimizeBtn = new Button();
        FontIcon minimizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MINIMIZE);
        minimizeIcon.setIconColor(Color.WHITE);
        minimizeBtn.setGraphic(minimizeIcon);
        minimizeBtn.getStyleClass().add("topbar-button");

        return minimizeBtn;
    }

    public static Button maximizeBtn() {
        Button maximizeBtn = new Button();
        FontIcon maximizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MAXIMIZE);
        maximizeIcon.setIconColor(Color.WHITE);
        maximizeBtn.setGraphic(maximizeIcon);
        maximizeBtn.getStyleClass().add("topbar-button");

        return maximizeBtn;
    }

    public static Button closeBtn() {
        Button closeBtn = new Button();
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);
        closeIcon.setIconColor(Color.WHITE);
        closeBtn.setGraphic(closeIcon);
        closeBtn.getStyleClass().add("topbar-button");

        return closeBtn;
    }

    public static ImageView appIcon() {
        ImageView appIcon = new ImageView(new Image(ICON_SM_PATH));
        appIcon.setFitHeight(18);
        appIcon.setFitWidth(18);
        HBox.setMargin(appIcon, new Insets(0, 5, 0, 10));

        return appIcon;
    }

    public static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

}
