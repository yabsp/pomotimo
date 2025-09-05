package org.pomotimo.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.logic.Preset;
import org.pomotimo.logic.PresetManager;
import org.pomotimo.logic.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskPane extends BorderPane {

    @FXML private ListView<Task> taskListView;
    @FXML private TextField taskInput;
    @FXML private Button addTaskButton;
    private final ContextMenu contextMenu = new ContextMenu();
    private final PresetManager presetManager;
    private final Logger logger = LoggerFactory.getLogger(TaskPane.class);

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
        refreshUI();
    }

    private void addTask() {
        if(taskInput.getText() == null || taskInput.getText().trim().isEmpty() || taskInput.getText().isBlank()) {
            return;
        }

        if(presetManager.getCurrentPreset().isPresent()){
            Preset pr = presetManager.getCurrentPreset().get();
            Task t = new Task(taskInput.getText(), pr.getTaskAmount());
            pr.addTask(t);
            taskListView.getItems().add(t);
            logger.info("Task added: " + t);
            presetManager.scheduleSave();
            taskInput.clear();
        } else {
            AlertFactory.createAlert(Alert.AlertType.WARNING, "No Preset Selected",
                    "No Current Preset",
                    "Please select or create a preset in order to add a task!").showAndWait();
        }

    }

    private void enableCellFactory() {
        taskListView.setCellFactory(lv -> {
            ListCell<Task> cell = new ListCell<>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    setText(empty || task == null ? null : task.getName());
                }
            };

            /* --- Context menu functionality --- */

            ContextMenu menu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete");

            deleteItem.setOnAction(e -> {
                Task task = cell.getItem();
                removeTaskItem(task);
            });

            menu.getItems().add(deleteItem);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                cell.setContextMenu(isNowEmpty ? null : menu);
            });

            /* --- Drag and drop functionality --- */
            cell.setOnDragDetected(event -> {
                if (cell.isEmpty())
                    return;

                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();

                int draggedIndex = cell.getIndex();
                content.putString(Integer.toString(draggedIndex));
                db.setContent(content);
                db.setDragView(cell.snapshot(null, null));

                event.consume();
            });

            cell.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (event.getGestureSource() != cell && db.hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);

                    int draggedIdx = Integer.parseInt(db.getString());
                    Task draggedTask = taskListView.getItems().get(draggedIdx);
                    cell.setText(draggedTask.getName());
                    cell.setStyle("-fx-border-color: grey; -fx-border-width: 2 0 0 0; -fx-background-color: lightgrey;");
                }
                event.consume();
            });

            cell.setOnDragExited(event -> {
                Task task = cell.getItem();
                cell.setText(task == null ? null : task.getName());
                cell.setStyle("");
            });

            cell.setOnDragDropped(event -> {
                if (cell.isEmpty())
                    return;

                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    int draggedIdx = Integer.parseInt(db.getString());
                    int thisIdx = cell.getIndex();

                    if (draggedIdx != thisIdx) {
                        Task draggedTask = taskListView.getItems().get(draggedIdx);
                        Task targetTask = taskListView.getItems().get(thisIdx);

                        logger.info("Dragged index: " + draggedIdx);
                        logger.info("Target index: " + thisIdx);
                        logger.info("Dragged task (before prio adjustment): " + draggedTask);
                        logger.info("Target task (before prio adjustment): " + targetTask);

                        taskListView.getItems().set(draggedIdx, targetTask);
                        taskListView.getItems().set(thisIdx, draggedTask);

                        int tempPriority = draggedTask.getPriority();
                        draggedTask.setPriority(targetTask.getPriority());
                        targetTask.setPriority(tempPriority);

                        logger.info("Dragged task (after prio adjustment): " + draggedTask);
                        logger.info("Target task (after prio adjustment): " + targetTask);
                        presetManager.scheduleSave();
                        taskListView.refresh();
                        taskListView.getSelectionModel().select(thisIdx);
                    }

                    event.setDropCompleted(true);
                }
                event.consume();
            });

            return cell;
        });
    }

    private void removeTaskItem(Task task) {
        if (task != null) {
            taskListView.getItems().remove(task);
            presetManager.getCurrentPreset().ifPresent(pr -> pr.removeTask(task));
            presetManager.scheduleSave();
            logger.info("Deleted: {}", task);
        }
    }

    public void refreshTaskListView() {
        presetManager.getCurrentPreset().ifPresent(pr -> {
            taskListView.getItems().setAll(pr.getTasks());
        });
    }

    public void refreshUI() {
        addTaskButton.setOnAction(e -> addTask());
        taskInput.setOnAction(e -> addTaskButton.fire());
        taskListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeTaskItem(taskListView.getSelectionModel().getSelectedItem());
            }
        });
        enableCellFactory();
        presetManager.getCurrentPreset().ifPresent(pr -> {
            taskListView.getItems().setAll(pr.getTasks());
        });
    }

}
