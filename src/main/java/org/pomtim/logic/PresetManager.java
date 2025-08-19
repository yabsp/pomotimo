package org.pomtim.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.pomtim.logic.Preset;

public class PresetManager {
    private final List<Preset> presets = new ArrayList<>();

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

    public void clear() {
        presets.clear();
    }
}
