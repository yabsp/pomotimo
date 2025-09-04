package org.pomotimo.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.logic.PresetManager;
import org.pomotimo.logic.Task;

public class TaskPane extends BorderPane {

    @FXML private ListView<Task> taskListView;
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
        if(taskInput.getText() == null || taskInput.getText().trim().isEmpty() || taskInput.getText().isBlank()) {
            return;
        }

        if(presetManager.getCurrentPreset().isPresent()){
            Task t = new Task(taskInput.getText(), 0);
            presetManager.getCurrentPreset().get().addTask(t);
            taskListView.getItems().add(t);
            taskInput.clear();
        } else {
            AlertFactory.createAlert(Alert.AlertType.WARNING, "No Preset Selected",
                    "No Current Preset",
                    "Please select or create a preset in order to add a task!").showAndWait();
        }

    }

    private void enableDragAndDrop() {
        taskListView.setCellFactory(lv -> {
            ListCell<Task> cell = new ListCell<>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    setText(empty || task == null ? null : task.getName());
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.isEmpty())
                    return;

                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(cell.getItem().getName());
                db.setContent(content);
                event.consume();
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                if (cell.isEmpty())
                    return;

                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    Task draggedTask = taskListView.getItems().stream()
                                                   .filter(t -> t.getName().equals(db.getString()))
                                                   .findFirst()
                                                   .orElse(null);

                    if (draggedTask != null) {
                        int draggedIdx = taskListView.getItems().indexOf(draggedTask);
                        int thisIdx = cell.getIndex();

                        taskListView.getItems().remove(draggedIdx);
                        taskListView.getItems().add(thisIdx, draggedTask);
                        taskListView.getSelectionModel().select(thisIdx);

                        event.setDropCompleted(true);
                    }
                }
                event.consume();
            });

            return cell;
        });
    }

}
