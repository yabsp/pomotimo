package org.pomotimo.gui.frame;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.pomotimo.gui.DeleteMenu;
import org.pomotimo.gui.TaskPane;
import org.pomotimo.gui.TimerPane;
import org.pomotimo.gui.utils.ElementsFactory;
import org.pomotimo.logic.PresetManager;

public class MenuFrame extends PomoFrame {
    private final Stage parentStage;
    public MenuFrame(PresetManager presetManager,
                     TimerPane timerpane,
                     TaskPane taskPane,
                     Stage parentStage) {
        super(presetManager, timerpane, taskPane, new Stage());
        this.parentStage = parentStage;
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
        DeleteMenu deleteMenu = new DeleteMenu(presetManager, this);
        mainStage.setTitle("Delete Profiles");
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.initModality(Modality.WINDOW_MODAL);
        mainStage.initOwner(parentStage);
        mainStage.getIcons().add(new Image(ICON_PATH));

        createTopBar();
        this.setCenter(deleteMenu);
        createFrameEffects();
        makeWindowResizable();
        Scene scene = new Scene(this, 600, 300);
        scene.setFill(Color.TRANSPARENT);
        mainStage.setScene(scene);
        mainStage.getScene().getStylesheets().addAll("css/titlebar.css", "css/generalstyle.css");
    }

}
