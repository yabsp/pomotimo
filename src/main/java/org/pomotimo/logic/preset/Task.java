package org.pomotimo.logic.preset;

import org.pomotimo.logic.config.AppConstants;

/**
 * Represents a task that is to be done. A task has a name and a priority.
 */
public class Task {
    private String name;
    /**
     * Priority is used as a number to sort tasks. Value 1 means that the priority is the highest.
     * In the task list the numbers refer to the placement in the list and can be used to sort tasks.
     */
    private int priority;

    /**
     * no-args constructor required by Gson
     */
    public Task() {
        this.name = "";
        this.priority = AppConstants.DEFAULT_PRIO;
    }

    /**
     * Constructs a task with custom priority.
     * @param name the name of the task
     * @param priority the priority of the task, lower int value means higher priority.
     */
    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    /**
     * Construct a task with default priority.
     * @param name the name of the task.
     */
    public Task(String name) {
        this.name = name;
        this.priority = AppConstants.DEFAULT_PRIO;
    }

    /**
     * Gets the name of the task.
     *
     * @return The name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the priority of the task.
     *
     * @return The priority value, where a lower number indicates higher priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     *
     * @param priority The new priority value. A lower number indicates higher priority.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Compares this task to another object for equality.
     * Two tasks are considered equal if they have the same name and priority.
     *
     * @param o The object to compare with this task.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if(!(o instanceof Task t)) {
            return false;
        }

        return t.getName().equals(name) && t.getPriority() == priority;
    }

    /**
     * Returns a string representation of the task, including its name and priority.
     *
     * @return A formatted string, e.g., "My Task(1)".
     */
    @Override
    public String toString() {
        return name + "(" + priority + ")";
    }
}
