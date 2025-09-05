package org.pomotimo.logic;

import javafx.concurrent.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.pomotimo.logic.utils.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Presents utility to manage and store all loaded Presets.
 */
public class PresetManager {
    private List<Preset> presets;
    private Preset currentPreset;
    private final PersistenceManager persistenceManager;
    private static final Logger logger = LoggerFactory.getLogger(PresetManager.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pendingSave;


    /**
     * Default constructor that creates a PresetManager instance and initializes the list of presets.
     */
    public PresetManager() {
        this.presets = new ArrayList<Preset>();
        this.persistenceManager = new PersistenceManager();
        loadPresetsAsync();
    }

    /**
     * Loads presets asynchronously (queued to the same executor).
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
     * Schedule a save in ~500ms. If another save is requested before that,
     * the old one is cancelled and rescheduled.
     */
    public synchronized void scheduleSave() {
        if (pendingSave != null && !pendingSave.isDone()) {
            pendingSave.cancel(false);
        }
        pendingSave = scheduler.schedule(this::savePresetsSafe, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Actually performs the save in the executor thread.
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

    public void addPreset(Preset p) {
        synchronized (this) {
            presets.add(p);
        }
        scheduleSave();
    }

    public boolean removePreset(String name) {
        boolean removed;
        synchronized (this) {
            removed = presets.removeIf(p -> p.getName().equalsIgnoreCase(name));
        }
        if (removed) scheduleSave();
        return removed;
    }

    public boolean removePreset(Preset p) {
        boolean removed;
        synchronized (this) {
            removed = presets.remove(p);
        }
        if (removed) scheduleSave();
        return removed;
    }

    public void clear() {
        synchronized (this) {
            presets.clear();
        }
        scheduleSave();
    }

    /* --- Other Utility Methods --- */

    public synchronized List<Preset> getPresets() {
        return List.copyOf(presets);
    }

    public synchronized Optional<Preset> findPresetByName(String name) {
        return presets.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    public synchronized boolean hasPresets() {
        return !presets.isEmpty();
    }

    public synchronized int getPresetCount() {
        return presets.size();
    }

    public synchronized Optional<Preset> getFirst() {
        return presets.stream().findFirst();
    }

    public synchronized Optional<Preset> getCurrentPreset() {
        return Optional.ofNullable(currentPreset);
    }

    public synchronized void setCurrentPreset(Preset pr) {
        this.currentPreset = pr;
    }

    public void shutDownScheduler() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
