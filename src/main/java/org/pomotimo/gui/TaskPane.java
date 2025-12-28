package org.pomotimo.gui;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import org.pomotimo.gui.state.AppState;
import org.pomotimo.gui.state.TaskViewState;
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
    private final AppState appState;
    private final Logger logger = LoggerFactory.getLogger(TaskPane.class);

    /**
     * Constructs the TaskPane.
     *
     * @param presetManager The manager for accessing and modifying the task list of the current preset.
     */
    public TaskPane(PresetManager presetManager, AppState appState) {
        this.presetManager = presetManager;
        this.appState = appState;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
            getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
        } catch (IOException e) {
            logger.error("Failed to load TaskPane.fxml", e);
        } catch (NullPointerException e) {
            logger.error("Stylesheet not found", e);
        }
    }

    @FXML
    private void initialize(){
        appState.currentPresetProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateTaskList(newValue);
            }
        });
        appState.taskViewStateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue == TaskViewState.EMPTY) {
                    taskListView.getItems().clear();
                }
            }
        });
        this.setOnMousePressed(event -> this.requestFocus());
        setupTaskPaneFunctionality();
    }

    private void addTask() {
        if (taskInput.getText() == null || taskInput.getText().trim().isEmpty() || taskInput.getText().isBlank()) {
            return;
        }

        if (presetManager.getCurrentPreset().isPresent()){
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

                content.putString(cell.getItem().getUUId());
                db.setContent(content);

                db.setDragView(cell.snapshot(null, null));

                event.consume();
            });

            cell.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (event.getGestureSource() != cell && db.hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    if (cell.getIndex() <= taskListView.getItems().size()) {
                        cell.setStyle("-fx-border-color: grey; -fx-border-width: 2 0 0 0; -fx-background-color: lightgrey;");
                    }
                }
                event.consume();
            });

            cell.setOnDragExited(event -> {
                Task task = cell.getItem();
                cell.setText(task == null ? null : task.getName());
                cell.setStyle("");
            });

            cell.setOnDragDropped(event -> {
                int newIndex;
                ObservableList<Task> list = taskListView.getItems();
                if (cell.getIndex() > list.size())
                    return;

                Dragboard db =  event.getDragboard();
                if (!db.hasString()) return;
                String draggedId = db.getString();

                Optional<Task> t = findTaskById(draggedId);
                if (t.isPresent()) {
                    Task draggedTask = t.get();

                    int oldIndex = list.indexOf(draggedTask);
                    newIndex = cell.isEmpty()? list.size() : cell.getIndex();

                    if (oldIndex < newIndex) newIndex--;

                    if (oldIndex != newIndex) {
                        list.remove(draggedTask);
                        list.add(newIndex, draggedTask);

                        normalizePriorities(list);
                        presetManager.scheduleSave();
                    }

                    event.setDropCompleted(true);
                };

                event.consume();
            });

            return cell;
        });
    }

    private void normalizePriorities(ObservableList<Task> items) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPriority(i);
        }
        presetManager.scheduleSave();
    }

    private void removeTaskItem(Task task) {
        if (task != null) {
            taskListView.getItems().remove(task);
            presetManager.getCurrentPreset().ifPresent(pr -> pr.removeTask(task));
            presetManager.scheduleSave();
            logger.info("Deleted: {}", task);
        }
    }

    private Optional<Task> findTaskById(String uuid) {
        if (presetManager.getCurrentPreset().isPresent()) {
            return presetManager.findTaskByUUID(presetManager.getCurrentPreset().get(), uuid);
        }
        else {
            return Optional.empty();
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

    private void updateTaskList (Preset p) {
        taskListView.getItems().clear();
        enableCellFactory();

        logger.info("Adding all tasks of current presets to ListView");
        List<Task> tasks = p.getTasks();
        taskListView.setItems(FXCollections.observableArrayList(tasks));
    }

    /**
     * Initializes or refreshes the entire UI of the task pane.
     * This method sets up event handlers for the input field and add button,
     * configures the list view's cell factory for context menus and drag-and-drop,
     * and populates the list with tasks from the current preset.
     */
    private void setupTaskPaneFunctionality() {
        addTaskButton.setOnAction(e -> addTask());
        taskInput.setOnAction(e -> addTaskButton.fire());
        taskListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeTaskItem(taskListView.getSelectionModel().getSelectedItem());
            }
        });
        presetManager.getCurrentPreset().ifPresent(this::updateTaskList);
    }
}
