package org.pomotimo.gui.utils;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * A factory utility class for creating standardized JavaFX Alert dialogs.
 * This class provides methods to generate pre-configured alerts with consistent
 * styling, titles, and icons for the application.
 */
public class AlertFactory {

    private static final Image LOGO = new Image(Objects.requireNonNull(AlertFactory.class.
            getResourceAsStream("/icons/logo_tomato_removebg.png")));
    private static final Image ICON = new Image(Objects.requireNonNull(AlertFactory.class.
            getResourceAsStream("/icons/logo_24x24.png")));

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AlertFactory() {}

    /**
     * Creates and configures a generic Alert dialog with the application's branding.
     *
     * @param type    The type of the alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title   The text to be displayed in the title bar of the dialog window.
     * @param header  The header text displayed within the dialog. Can be null.
     * @param content The main content message of the alert.
     * @return A configured {@link Alert} instance, ready to be shown.
     */
    public static Alert alert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ImageView logoView = new ImageView(LOGO);
        logoView.setFitWidth(48);
        logoView.setFitHeight(48);
        alert.setGraphic(logoView);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(ICON);

        return alert;
    }

    /**
     * Creates a specific warning alert for when a time input field is left empty.
     *
     * @param field The name of the field that requires a time input (e.g., "Focus Time").
     * @return A pre-configured warning {@link Alert}.
     */
    public static Alert emptyTimeFieldAlert(String field) {
        return alert(
                Alert.AlertType.WARNING,
                "Input required",
                field,
                "Please enter a time into your " + field + " before saving!"
        );
    }

    /**
     * Creates a specific warning alert for when a name input field is left empty.
     *
     * @param field The name of the field that requires a name input (e.g., "Preset Name").
     * @return A pre-configured warning {@link Alert}.
     */
    public static Alert emptyNameFieldAlert(String field) {
        return alert(
                Alert.AlertType.WARNING,
                "Input required",
                "Missing Name",
                "Please enter a name into your " + field + " before saving!"
        );
    }
}
