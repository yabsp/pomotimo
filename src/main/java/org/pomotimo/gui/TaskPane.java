package org.pomotimo.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import org.pomotimo.logic.PresetManager;

public class TaskPane extends BorderPane {

    @FXML private ListView<String> taskListView;
    @FXML private TextField taskInput;
    @FXML private Button addTaskButton;
    private PresetManager presetManager;

    public TaskPane(PresetManager presetManager) {
        this.presetManager = presetManager;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TimerPane.fxml", e);
        }
    }

    @FXML
    private void initialize(){
        addTaskButton.setOnAction(e -> addTask());
        enableDragAndDrop();

    }

    private void addTask() {
        String newTask = taskInput.getText();
        if (newTask != null && !newTask.isBlank()) {
            taskListView.getItems().add(newTask);
            taskInput.clear();
        }
    }

    private void enableDragAndDrop() {
        taskListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.isEmpty()) return;

                Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(cell.getItem());
                dragboard.setContent(content);
                event.consume();
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                if (cell.isEmpty()) return;

                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()) {
                    int draggedIdx = taskListView.getItems().indexOf(dragboard.getString());
                    int thisIdx = taskListView.getItems().indexOf(cell.getItem());

                    if (draggedIdx != -1 && thisIdx != -1) {
                        String draggedItem = taskListView.getItems().remove(draggedIdx);
                        taskListView.getItems().add(thisIdx, draggedItem);

                        taskListView.getSelectionModel().select(thisIdx);
                    }
                    event.setDropCompleted(true);
                }
                event.consume();
            });

            return cell;
        });
    }


}
