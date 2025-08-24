package org.pomtim.logic;

import java.util.List;

/**
 * Represents a User preset. Contains duration of focus time, breaks, custom alarm sounds,
 * images and a List of {@link org.pomtim.logic.Task} objects.
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
    private List tasks;

    public Preset(String name, int durationFocus, int durationShortBreak, int durationLongBreak, String soundFile, String imageFile, List<Task> tasks) {
        this.name = name;
        this.durationFocus = durationFocus;
        this.durationShortBreak = durationShortBreak;
        this.durationLongBreak = durationLongBreak;
        this.soundFile = soundFile;
        this.imageFile = imageFile;
        this.tasks = tasks;
    }

    public Preset() {

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

    public void setDurationFocus(int durationFocus) {
        this.durationFocus = durationFocus;
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

    @Override
    public String toString() {
        return name + " (focus: " + durationFocus + "s, short break: " + durationShortBreak + "s, long break: " + durationLongBreak + "s)";
    }
}
