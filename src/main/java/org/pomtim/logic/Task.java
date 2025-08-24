package org.pomtim.logic;

/**
 * Represents a task that is to be done. A task has a name and a priority.
 */
public class Task {
    private final String name;
    private int priority;

    public Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public Task(String name) {
        this.name = name;
        this.priority = 0;
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
}
