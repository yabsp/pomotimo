package org.pomtim.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class TaskPane extends BorderPane {

    @FXML private ListView<String> taskListView;
    @FXML private TextField taskInput;
    @FXML private Button addTaskButton;

    public TaskPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskPane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        this.getStylesheets().add("css/generalStyle.css");

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TimerPane.fxml", e);
        }
    }

    @FXML
    private void initialize(){
        addTaskButton.setOnAction(e -> addTask());

    }

    private void addTask() {
        String newTask = taskInput.getText();
        if (newTask != null && !newTask.isBlank()) {
            taskListView.getItems().add(newTask);
            taskInput.clear();
        }
    }

}
