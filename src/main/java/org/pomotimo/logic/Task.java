package org.pomotimo.logic;

/**
 * Represents a task that is to be done. A task has a name and a priority.
 */
public class Task {
    private final String name;
    /**
     * Priority is used as a number to sort tasks. Value 1 means that the priority is the highest.
     * In the task list the numbers refer to the placement in the list and can be used to sort tasks.
     */
    private int priority;

    public final int DEFAULT_PRIO = 0;

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
        this.priority = DEFAULT_PRIO;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

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

    @Override
    public String toString() {
        return name;
    }
}
