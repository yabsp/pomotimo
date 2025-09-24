package org.pomotimo.logic.preset;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a User preset. Contains duration of focus time, breaks, custom alarm sounds,
 * images and a List of {@link Task} objects.
 */
public class Preset {
    private String name;
    /* Durations in seconds */
    private int durationFocus;
    private int durationShortBreak;
    private int durationLongBreak;
    private int cycleAmount;

    /* sound and image file location */
    private String soundFile;
    private String imageFile;
    private ArrayList<Task> tasks;

    private String soundName;

    /* Default for paths is empty path */
    public static final String DEFAULT_PATH = "";
    /* 25 minutes in seconds */
    public static final int DEFAULT_FOCUS_TIME = 1500;
    /* 5 minutes in seconds */
    public static final int DEFAULT_SHORT_BR_TIME = 300;
    /* 15 minutes in seconds */
    public static final int DEFAULT_LONG_BR_TIME = 900;
    public static final int DEFAULT_CYCLE_AMOUNT = 4;

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
     * @param tasks List of {@link Task} objects
     */
    public Preset(String name, int durationFocus, int durationShortBreak,
                  int durationLongBreak, String soundFile, String imageFile, int cycleAmount, ArrayList<Task> tasks) {
        this.name = name;
        this.durationFocus = durationFocus;
        this.durationShortBreak = durationShortBreak;
        this.durationLongBreak = durationLongBreak;
        this.soundFile = soundFile;
        this.imageFile = imageFile;
        this.cycleAmount = cycleAmount;
        this.soundName = DEFAULT_PATH;
        this.tasks = tasks;
    }

    /**
    * Construct a Preset instance with new name and durations.
     * @param name name of the preset
     * @param durationFocus duration in seconds of the focus period
     * @param durationShortBreak duration in seconds of a small break
     * @param durationLongBreak duration in seconds of a big break
    */
    public Preset(String name, int durationFocus, int durationShortBreak, int durationLongBreak, int cycleAmount) {
        this.name = name;
        this.durationFocus = durationFocus;
        this.durationShortBreak = durationShortBreak;
        this.durationLongBreak = durationLongBreak;
        this.cycleAmount = cycleAmount;
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
        this.cycleAmount = DEFAULT_CYCLE_AMOUNT;
        this.soundFile = DEFAULT_PATH;
        this.imageFile = DEFAULT_PATH;
        this.soundName = DEFAULT_PATH;
        this.tasks = new ArrayList<>();
    }

    // --- Getters & Setters ---
    /**
     * Gets the name of the preset.
     * @return The name of the preset.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the preset.
     * @param name The new name for the preset.
     */
    public Preset setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the duration of the focus period in seconds.
     * @return The focus duration in seconds.
     */
    public int getDurationFocus() {
        return durationFocus;
    }

    /**
     * Sets the duration of the focus period.
     * @param duration The new focus duration in seconds.
     */
    public Preset setDurationFocus(int duration) {
        this.durationFocus = duration;
        return this;
    }

    /**
     * Gets the duration of the short break period in seconds.
     * @return The short break duration in seconds.
     */
    public int getDurationShortBreak() {
        return durationShortBreak;
    }

    /**
     * Sets the duration of the short break period.
     * @param duration The new short break duration in seconds.
     */
    public Preset setDurationShortBreak(int duration) {
        this.durationShortBreak = duration;
        return this;
    }

    /**
     * Gets the duration of the long break period in seconds.
     * @return The long break duration in seconds.
     */
    public int getDurationLongBreak() {
        return durationLongBreak;
    }

    /**
     * Sets the duration of the long break period.
     * @param duration The new long break duration in seconds.
     */
    public Preset setDurationLongBreak(int duration) {
        this.durationLongBreak = duration;
        return this;
    }

    /**
     *  Sets the amount of pomodoro cycles
     * @param ca the new amount of pomodoro cycles
     */
    public Preset setCycleAmount(int ca) {
        this.cycleAmount = ca;
        return this;
    }

    /**
     * Get the cycle amount.
     * @return the current amount of cycles for this preset.
     */
    public int getCycleAmount() {
        return this.cycleAmount;
    }

    /**
     * Gets the file path for the alarm sound.
     * @return The path to the sound file.
     */
    public String getSoundFile() {
        return soundFile;
    }

    /**
     * Sets the file path for the alarm sound.
     * @param soundFile The new path to the sound file.
     */
    public Preset setSoundFile(String soundFile) {
        this.soundFile = soundFile;
        return this;
    }

    /**
     * Gets the file path for the user image.
     * @return The path to the image file.
     */
    public String getImageFile() {
        return imageFile;
    }

    /**
     * Sets the file path for the user image.
     * @param imageFile The new path to the image file.
     */
    public Preset setImageFile(String imageFile) {
        this.imageFile = imageFile;
        return this;
    }

    /**
     * Gets the number of tasks associated with this preset.
     * @return The total count of tasks.
     */
    public int getTaskAmount() {
        return tasks.size();
    }

    /**
     * Gets the list of tasks associated with this preset.
     * @return The list of {@link Task} objects.
     */
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

    /**
     * Returns a string representation of the preset, including its name and timer durations.
     *
     * @return A formatted string summarizing the preset's configuration.
     */
    @Override
    public String toString() {
        return name + " (focus: " + String.format("%02d:%02d", durationFocus / 60, durationFocus % 60)
                + ", short break: " + String.format("%02d:%02d", durationShortBreak / 60, durationShortBreak % 60)
                + ", long break: " + String.format("%02d:%02d", durationLongBreak / 60, durationLongBreak % 60) + ")";
    }

    /**
     * Compares this preset to another object for equality.
     * Two presets are considered equal if their name, durations, file paths, and task lists are all identical.
     *
     * @param o The object to compare with this preset.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if(!(o instanceof Preset p)) {
            return false;
        }

        return p.getName().equals(name) && p.getDurationFocus() == durationFocus
                && p.getDurationShortBreak() == durationShortBreak
                && p.getDurationLongBreak() == durationLongBreak
                && p.getCycleAmount() == cycleAmount
                && p.getImageFile().equals(imageFile)
                && p.getSoundFile().equals(soundFile)
                && tasks.containsAll(p.getTasks());
    }
}
