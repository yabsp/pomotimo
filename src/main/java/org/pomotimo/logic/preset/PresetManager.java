package org.pomotimo.logic.preset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.pomotimo.logic.audio.AlarmPlayer;
import org.pomotimo.logic.utils.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the collection of user presets, handling loading, saving, and modification.
 * This class uses a persistence manager to handle disk I/O and schedules save operations
 * to avoid excessive writes.
 */
public class PresetManager {
    private List<Preset> presets;
    private Preset currentPreset;
    private final PersistenceManager persistenceManager;
    public final AlarmPlayer player;
    private static final Logger logger = LoggerFactory.getLogger(PresetManager.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pendingSave;


    /**
     * Default constructor that creates a PresetManager instance and initializes the list of presets
     * by loading them asynchronously.
     */
    public PresetManager() {
        this.presets = new ArrayList<Preset>();
        this.persistenceManager = new PersistenceManager();
        this.player = new AlarmPlayer();
        loadPresetsAsync();
    }

    /**
     * Loads presets asynchronously from the persistence layer.
     * On completion, it updates the internal list and sets the first loaded preset as the current one.
     */
    public void loadPresetsAsync() {
        scheduler.submit(() -> {
            List<Preset> loaded = persistenceManager.loadPresets();
            synchronized (this) {
                this.presets = loaded;
                if (hasPresets()) {
                    setCurrentPreset(presets.getFirst());
                }
            }
            logger.info("Presets loaded successfully.");
        });
    }

    /**
     * Schedules a save operation to run after a short delay.
     * If another save is requested before the delay has passed, the previous request is cancelled
     * and a new one is scheduled, effectively "debouncing" the save action.
     */
    public synchronized void scheduleSave() {
        if (pendingSave != null && !pendingSave.isDone()) {
            pendingSave.cancel(false);
        }
        pendingSave = scheduler.schedule(this::savePresetsSafe, 1, TimeUnit.SECONDS);
    }

    /**
     * Performs the actual save operation by writing the current list of presets to disk.
     * This method is intended to be called by the internal scheduler.
     */
    private void savePresetsSafe() {
        try {
            persistenceManager.savePresets(List.copyOf(this.presets));
            logger.info("Presets saved successfully.");
        } catch (Exception e) {
            logger.error("Failed to save presets. {}", e.getMessage());
        }
    }

    /* --- Utility Methods that trigger saving --- */

    /**
     * Adds a new preset to the manager and schedules a save operation.
     *
     * @param p The {@link Preset} to add.
     */
    public void addPreset(Preset p) {
        synchronized (this) {
            presets.add(p);
        }
        scheduleSave();
    }

    /**
     * Removes a preset from the manager by its name (case-insensitive).
     * If a preset is removed, a save operation is scheduled.
     *
     * @param name The name of the preset to remove.
     * @return {@code true} if a preset was successfully removed, {@code false} otherwise.
     */
    public boolean removePreset(String name) {
        boolean removed;
        synchronized (this) {
            removed = presets.removeIf(p -> p.getName().equalsIgnoreCase(name));
        }
        if (removed) scheduleSave();
        return removed;
    }

    /**
     * Removes the specified preset instance from the manager.
     * If the removed preset was the currently active one, the current preset is reset.
     * A save operation is scheduled upon successful removal.
     *
     * @param p The {@link Preset} instance to remove.
     * @return {@code true} if the preset was successfully removed, {@code false} otherwise.
     */
    public boolean removePreset(Preset p) {
        boolean removed;
        synchronized (this) {
            logger.debug("Removing preset: {}", p);
            removed = presets.remove(p);
            if(p.equals(currentPreset)) {
                currentPreset = null;
                if(!presets.isEmpty()) {
                    currentPreset = presets.getFirst();
                }
            }
        }
        if (removed) scheduleSave();
        return removed;
    }

    /**
     * Removes all presets from the manager and schedules a save operation.
     */
    public void clear() {
        synchronized (this) {
            presets.clear();
        }
        scheduleSave();
    }

    /* --- Other Utility Methods --- */

    /**
     * Gets an immutable copy of the list of all managed presets.
     *
     * @return An immutable {@link List} of {@link Preset} objects.
     */
    public synchronized List<Preset> getPresets() {
        return List.copyOf(presets);
    }

    /**
     * Finds a preset by its name with a case-insensitive search.
     *
     * @param name The name of the preset to find.
     * @return An {@link Optional} containing the found {@link Preset}, or an empty Optional if no match is found.
     */
    public synchronized Optional<Preset> findPresetByName(String name) {
        return presets.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Checks if the manager contains any presets.
     *
     * @return {@code true} if there is at least one preset, {@code false} otherwise.
     */
    public synchronized boolean hasPresets() {
        return !presets.isEmpty();
    }

    /**
     * Gets the total number of presets currently managed.
     *
     * @return The count of presets.
     */
    public synchronized int getPresetCount() {
        return presets.size();
    }

    /**
     * Gets the first preset in the list, if one exists.
     *
     * @return An {@link Optional} containing the first {@link Preset}, or an empty Optional if the list is empty.
     */
    public synchronized Optional<Preset> getFirst() {
        return presets.stream().findFirst();
    }

    /**
     * Gets the currently active preset.
     *
     * @return An {@link Optional} containing the current {@link Preset}, or an empty Optional if no preset is active.
     */
    public synchronized Optional<Preset> getCurrentPreset() {
        return Optional.ofNullable(currentPreset);
    }

    /**
     * Sets the currently active preset.
     *
     * @param pr The {@link Preset} to be set as the current one.
     */
    public synchronized void setCurrentPreset(Preset pr) {
        this.currentPreset = pr;
    }

    /**
     * Shuts down the internal scheduler service.
     * This method should be called upon application exit to ensure a clean shutdown.
     */
    public void shutDownScheduler() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
