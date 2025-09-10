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

/**
 * A factory utility class for creating common and reusable JavaFX UI elements.
 * This class provides static methods to generate standardized components like
 * window control buttons and spacers, ensuring a consistent appearance.
 */
public class ElementsFactory {
    static final String ICON_SM_PATH = "icons/logo_24x24.png";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ElementsFactory() {}

    /**
     * Creates a styled "minimize" button for a custom window title bar.
     *
     * @return A {@link Button} configured with a minimize icon.
     */
    public static Button minimizeBtn() {
        Button minimizeBtn = new Button();
        FontIcon minimizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MINIMIZE);
        minimizeIcon.setIconColor(Color.WHITE);
        minimizeBtn.setGraphic(minimizeIcon);
        minimizeBtn.getStyleClass().add("topbar-button");

        return minimizeBtn;
    }

    /**
     * Creates a styled "maximize" button for a custom window title bar.
     *
     * @return A {@link Button} configured with a maximize icon.
     */
    public static Button maximizeBtn() {
        Button maximizeBtn = new Button();
        FontIcon maximizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MAXIMIZE);
        maximizeIcon.setIconColor(Color.WHITE);
        maximizeBtn.setGraphic(maximizeIcon);
        maximizeBtn.getStyleClass().add("topbar-button");

        return maximizeBtn;
    }

    /**
     * Creates a styled "close" button for a custom window title bar.
     *
     * @return A {@link Button} configured with a close icon.
     */
    public static Button closeBtn() {
        Button closeBtn = new Button();
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.WINDOW_CLOSE);
        closeIcon.setIconColor(Color.WHITE);
        closeBtn.setGraphic(closeIcon);
        closeBtn.getStyleClass().add("topbar-button");

        return closeBtn;
    }

    /**
     * Creates an {@link ImageView} for the application icon to be displayed in the title bar.
     *
     * @return A configured {@link ImageView} containing the application icon.
     */
    public static ImageView appIcon() {
        ImageView appIcon = new ImageView(new Image(ICON_SM_PATH));
        appIcon.setFitHeight(18);
        appIcon.setFitWidth(18);
        HBox.setMargin(appIcon, new Insets(0, 5, 0, 10));

        return appIcon;
    }

    /**
     * Creates a flexible spacer {@link Region} that grows to fill available horizontal space.
     * This is typically used in an {@link HBox} to push child nodes to the left and right edges.
     *
     * @return An expanding {@link Region} instance.
     */
    public static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

}
