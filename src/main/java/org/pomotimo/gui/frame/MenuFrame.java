package org.pomotimo.gui.frame;

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
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.utils.PresetImporterExporter;

public class MenuFrame extends PomoFrame {
    private final Stage parentStage;
    private final ViewType viewType;
    public MenuFrame(PresetManager presetManager,
                     PresetImporterExporter importerExporter,
                     TimerPane timerpane,
                     TaskPane taskPane,
                     Stage parentStage,
                     ViewType viewType) {
        super(presetManager, importerExporter, timerpane, taskPane, new Stage());
        this.parentStage = parentStage;
        this.viewType = viewType;
        initialize();
    }

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

    @Override
    protected void initialize() {
        BorderPane view;
        switch(viewType) {
            case DELETE_VIEW -> {
                view = new DeletePresetView(presetManager, this);
                mainStage.setTitle("Delete Profiles");
            }
            case EXPORT_VIEW -> {
                view = new ExportPresetView(presetManager, importerExporter, this);
                mainStage.setTitle("Export Profiles");
            }
            default -> view = new BorderPane();
        }
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.initModality(Modality.WINDOW_MODAL);
        mainStage.initOwner(parentStage);
        mainStage.getIcons().add(new Image(ICON_PATH));

        createTopBar();
        this.setCenter(view);
        createFrameEffects();
        makeWindowResizable();
        Scene scene = new Scene(this, 600, 300);
        scene.setFill(Color.TRANSPARENT);
        mainStage.setScene(scene);
        mainStage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalstyle.css");
    }

}
