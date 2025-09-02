package org.pomotimo.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Presents utility to manage and store all loaded Presets.
 */
public class PresetManager {
    private List<Preset> presets;

    /**
     * Default constructor that creates a PresetManager instance and initializes the list of presets.
     */
    public PresetManager() {
        this.presets = new ArrayList<Preset>();
    }

    /**
     * Constructs a new PresetManager instance initialized with a given list of presets
     * @param presets list of Preset objects, must not be {@code null}
     */
    public PresetManager(List<Preset> presets) {
        this.presets = presets;
    }

    /* Utility Methods */
    
    public List<Preset> getPresets() {
        return List.copyOf(presets);
    }

    public void addPreset(Preset p) {
        presets.add(p);
    }

    public boolean removePreset(String name) {
        return presets.removeIf(p -> p.getName().equalsIgnoreCase(name));
    }

    public boolean removePreset(Preset p) {
        return presets.remove(p);
    }

    public Optional<Preset> findPresetByName(String name) {
        return presets.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    public boolean hasPresets() {
        return !presets.isEmpty();
    }

    public int getPresetCount() {
        return presets.size();
    }

    public void clear() {
        presets.clear();
    }

    public Optional<Preset> getFirst(){
        return presets.stream().findFirst();
    }
}
