package org.pomotimo.gui.utils;

import javax.swing.Icon;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AlertFactory {

    private static final Image LOGO = new Image(AlertFactory.class.getResourceAsStream("/icons/logo_tomato_removebg.png"));
    private static final Image ICON = new Image(AlertFactory.class.getResourceAsStream("/icons/logo_24x24.png"));

    /** Generic alert creation */
    public static Alert createAlert(Alert.AlertType type, String title, String header, String content) {
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

    /** Create alert for informing about an empty time field*/
    public static Alert createEmptyTimeFieldAlert(String field) {
        return createAlert(
                Alert.AlertType.WARNING,
                "Input required",
                field,
                "Please enter a time into your " + field + " before saving!"
        );
    }

    public static Alert createEmptyNameFieldAlert(String field) {
        return createAlert(
                Alert.AlertType.WARNING,
                "Input required",
                "Missing Name",
                "Please enter a name into your " + field + " before saving!"
        );
    }
}
