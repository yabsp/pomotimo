package org.pomotimo.gui.frame;

import java.util.Objects;

import javax.swing.text.View;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.pomotimo.gui.DeletePresetView;
import org.pomotimo.gui.ExportPresetView;
import org.pomotimo.gui.TaskPane;
import org.pomotimo.gui.TimerPane;
import org.pomotimo.gui.utils.ElementsFactory;
import org.pomotimo.logic.config.AppConstants;
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.utils.PresetImporterExporter;

/**
 * Represents a secondary, modal frame used for various menu dialogs like
 * deleting or exporting presets. The specific content displayed is determined
 * by a {@link ViewType} passed during construction.
 */
public class MenuFrame extends PomoFrame {
    private final Stage parentStage;
    private final ViewType viewType;

    /**
     * Constructs a new menu frame, which acts as a modal dialog window.
     * The content of this frame is determined by the provided ViewType.
     *
     * @param presetManager The manager for handling application presets.
     * @param importerExporter The utility for importing and exporting presets.
     * @param timerPane A reference to the main application's TimerPane.
     * @param taskPane A reference to the main application's TaskPane.
     * @param parentStage The primary stage that owns this menu frame.
     * @param viewType The type of view to display within this frame (e.g., {@link PomoFrame.ViewType#DELETE_VIEW}).
     */
    public MenuFrame(PresetManager presetManager,
                     PresetImporterExporter importerExporter,
                     TimerPane timerPane,
                     TaskPane taskPane,
                     Stage parentStage,
                     ViewType viewType) {
        super(presetManager, importerExporter, timerPane, taskPane, new Stage());
        this.parentStage = parentStage;
        this.viewType = viewType;
        initialize();
    }

    /**
     * Creates and configures the custom top bar for the menu window.
     * This bar includes standard window controls and enables dragging.
     * Overrides the method from {@link PomoFrame}.
     */
    @Override
    protected void createTopBar() {
        this.topBar = new HBox();
        topBar.getStyleClass().add("custom-title-bar");

        Button minimizeBtn = ElementsFactory.minimizeBtn();
        Button maximizeBtn = ElementsFactory.maximizeBtn();
        Button closeBtn = ElementsFactory.closeBtn();

        minimizeBtn.setOnAction(e -> mainStage.setIconified(true));
        maximizeBtn.setOnAction(e -> mainStage.setMaximized(!mainStage.isMaximized()));
        closeBtn.setOnAction(e -> {
            mainStage.close();
        });

        makeWindowDraggable();

        topBar.getChildren().addAll(ElementsFactory.appIcon(),
                ElementsFactory.spacer(), minimizeBtn, maximizeBtn, closeBtn);
        this.setTop(topBar);

        topBar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                mainStage.setMaximized(!mainStage.isMaximized());
            }
        });

    }

    /**
     * Initializes the menu frame based on the specified {@link ViewType}.
     * This method sets up the window as a transparent, modal dialog, creates the
     * appropriate content view (e.g., for deleting or exporting presets),
     * and applies the necessary styles. Overrides the method from {@link PomoFrame}.
     */
    @Override
    protected void initialize() {
        BorderPane view;
        switch(viewType) {
            case DELETE_VIEW -> {
                view = new DeletePresetView(presetManager, (MainFrame) parentStage.getScene().getRoot());
                mainStage.setTitle("Delete Presets");
            }
            case EXPORT_VIEW -> {
                view = new ExportPresetView(presetManager, importerExporter, this);
                mainStage.setTitle("Export Presets");
            }
            default -> view = new BorderPane();
        }
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.initModality(Modality.WINDOW_MODAL);
        mainStage.initOwner(parentStage);
        try {
            this.mainStage.getIcons().add(AppConstants.ICON);
        } catch (NullPointerException e) {
            logger.error("Icon path is null.", e);
        }

        createTopBar();
        this.setCenter(view);
        createFrameEffects();
        makeWindowResizable();
        Scene scene = new Scene(this, 600, 300);
        scene.setFill(Color.TRANSPARENT);
        mainStage.setScene(scene);
        try {
            String titlebarCSS = getClass().getResource("/css/titlebar.css").toExternalForm();
            String styleCSS = getClass().getResource("/css/generalstyle.css").toExternalForm();
            this.mainStage.getScene().getStylesheets().addAll(titlebarCSS, styleCSS);
        } catch (NullPointerException e) {
            logger.error("Stylesheets not found.", e);
        }
    }
}
