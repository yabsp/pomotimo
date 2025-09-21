package org.pomotimo.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.pomotimo.logic.preset.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JavaFX view component for managing the list of tasks associated with a preset.
 * It allows users to add new tasks, delete existing ones, and reorder them
 * using drag-and-drop functionality.
 */
public class TaskPane extends BorderPane {

    @FXML private ListView<Task> taskListView;
    @FXML private TextField taskInput;
    @FXML private Button addTaskButton;
    private final ContextMenu contextMenu = new ContextMenu();
    private final PresetManager presetManager;
    private final Logger logger = LoggerFactory.getLogger(TaskPane.class);

    /**
     * Constructs the TaskPane.
     *
     * @param presetManager The manager for accessing and modifying the task list of the current preset.
     */
    public TaskPane(PresetManager presetManager) {
        this.presetManager = presetManager;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            getStylesheets().add(getClass().getResource("/css/generalstyle.css").toExternalForm());
        } catch (IOException e) {
            logger.error("Failed to load TaskPane.fxml", e);
        } catch (NullPointerException e) {
            logger.error("Stylesheet not found", e);
        }
    }

    @FXML
    private void initialize(){
        this.setOnMousePressed(event -> this.requestFocus());
        refreshUI();
    }

    private void addTask() {
        if(taskInput.getText() == null || taskInput.getText().trim().isEmpty() || taskInput.getText().isBlank()) {
            return;
        }

        if(presetManager.getCurrentPreset().isPresent()){
            logger.info("Current Preset is present.");
            Preset pr = presetManager.getCurrentPreset().get();
            Task t = new Task(taskInput.getText(), pr.getTaskAmount());
            pr.addTask(t);
            taskListView.getItems().add(t);
            logger.info("Task added: " + t);
            presetManager.scheduleSave();
            taskInput.clear();
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

    /**
     * Refreshes the task list view to display the tasks from the currently active preset.
     */
    public void refreshTaskListView() {
        presetManager.getCurrentPreset().ifPresent(pr -> {
            taskListView.getItems().setAll(pr.getTasks());
        });
    }

    /**
     * Initializes or refreshes the entire UI of the task pane.
     * This method sets up event handlers for the input field and add button,
     * configures the list view's cell factory for context menus and drag-and-drop,
     * and populates the list with tasks from the current preset.
     */
    public void refreshUI() {
        addTaskButton.setOnAction(e -> addTask());
        taskInput.setOnAction(e -> addTaskButton.fire());
        taskListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeTaskItem(taskListView.getSelectionModel().getSelectedItem());
            }
        });
        taskListView.getItems().clear();
        enableCellFactory();
        presetManager.getCurrentPreset().ifPresent(pr -> {
            logger.info("Adding all tasks of current presets to ListView");
            taskListView.getItems().setAll(pr.getTasks());
        });
    }

}
