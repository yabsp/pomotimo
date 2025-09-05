package org.pomotimo.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User preset. Contains duration of focus time, breaks, custom alarm sounds,
 * images and a List of {@link org.pomotimo.logic.Task} objects.
 */
public class Preset {
    private String name;
    /* Durations in seconds */
    private int durationFocus;
    private int durationShortBreak;
    private int durationLongBreak;

    /* sound and image file location */
    private String soundFile;
    private String imageFile;
    private ArrayList<Task> tasks;

    /* Default for paths is empty path */
    public final String DEFAULT_PATH = "";
    /* 25 minutes in seconds */
    public final int DEFAULT_FOCUS_TIME = 1500;
    /* 5 minutes in seconds */
    public final int DEFAULT_SHORT_BR_TIME = 300;
    /* 15 minutes in seconds */
    public final int DEFAULT_LONG_BR_TIME = 900;

    /**
     * No-arg constructor for Gson deserialization.
     */
    public Preset() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Construct a Preset instance with new values.
     * @param name name of the preset
     * @param durationFocus duration in seconds of the focus period
     * @param durationShortBreak duration in seconds of a small break
     * @param durationLongBreak duration in seconds of a big break
     * @param soundFile path to the alarm sound file
     * @param imageFile path to the user profile picture
     * @param tasks List of {@link org.pomotimo.logic.Task} objects
     */
    public Preset(String name, int durationFocus, int durationShortBreak, int durationLongBreak, String soundFile, String imageFile, ArrayList<Task> tasks) {
        this.name = name;
        this.durationFocus = durationFocus;
        this.durationShortBreak = durationShortBreak;
        this.durationLongBreak = durationLongBreak;
        this.soundFile = soundFile;
        this.imageFile = imageFile;
        this.tasks = tasks;
    }

    /**
    * Construct a Preset instance with new name and durations.
     * @param name name of the preset
     * @param durationFocus duration in seconds of the focus period
     * @param durationShortBreak duration in seconds of a small break
     * @param durationLongBreak duration in seconds of a big break
    */
    public Preset(String name, int durationFocus, int durationShortBreak, int durationLongBreak) {
        this.name = name;
        this.durationFocus = durationFocus;
        this.durationShortBreak = durationShortBreak;
        this.durationLongBreak = durationLongBreak;
        this.soundFile = DEFAULT_PATH;
        this.imageFile = DEFAULT_PATH;
        this.tasks = new ArrayList<Task>();
    }

    /**
     * Constructor for Default preset creation
     * @param name name of the preset (must be provided)
     */
    public Preset(String name) {
        this.name = name;
        this.durationFocus = DEFAULT_FOCUS_TIME;
        this.durationShortBreak = DEFAULT_SHORT_BR_TIME;
        this.durationLongBreak = DEFAULT_LONG_BR_TIME;
        this.soundFile = DEFAULT_PATH;
        this.imageFile = DEFAULT_PATH;
        this.tasks = new ArrayList<Task>();
    }

    // --- Getters & Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationFocus() {
        return durationFocus;
    }

    public void setDurationFocus(int duration) {
        this.durationFocus = duration;
    }

    public int getDurationShortBreak() {
        return durationShortBreak;
    }

    public void setDurationShortBreak(int duration) {
        this.durationShortBreak = duration;
    }

    public int getDurationLongBreak() {
        return durationLongBreak;
    }

    public void setDurationLongBreak(int duration) {
        this.durationLongBreak = duration;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public int getTaskAmount() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Add a task to the preset. See {@link Task} for more information.
     * @param t Task that should be added to the preset.
     * @return {@code true} if the task has been added successfully, {@code false} otherwise.
     * {@code false} is an indicator that the task already exists in the presets task list.
     */
    public boolean addTask(Task t) {
        if(!tasks.contains(t)){
            tasks.add(t);
            return true;
        }
        return false;
    }

    /**
     * Remove a task from the task list.
     * @param t the Task that should be removed.
     * @return True if removal was successful, false otherwise.
     */
    public boolean removeTask(Task t) {
        return tasks.remove(t);
    }

    @Override
    public String toString() {
        return name + " (focus: " + durationFocus + "s, short break: " + durationShortBreak + "s, long break: " + durationLongBreak + "s)";
    }
}
