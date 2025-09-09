package org.pomotimo.logic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.pomotimo.logic.preset.Preset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading of presets to and from a JSON file.
 */
public class PersistenceManager {

    // Store presets in a .pomotimo folder in the user's home directory.
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".pomotimo");
    private static final Path PRESETS_FILE_PATH = CONFIG_PATH.resolve("presets.json");

    // Gson instance for JSON serialization/deserialization. Pretty printing makes it human-readable.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    /**
     * Saves the provided list of presets to a JSON file.
     *
     * @param presets The list of presets to save.
     */
    public void savePresets(List<Preset> presets) {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH);
            }
        } catch (IOException e){
            logger.error("Error when trying to create directories for presets.{}", e.getMessage());
        }

        try (FileWriter writer = new FileWriter(PRESETS_FILE_PATH.toFile())) {
            gson.toJson(presets, writer);
        } catch (IOException e) {
            logger.error("Error when trying to write presets{}", e.getMessage());
        }
    }

    /**
     * Loads the list of presets from the JSON file.
     *
     * @return A list of loaded presets. Returns an empty list if the file doesn't exist or an error occurs.
     */
    public List<Preset> loadPresets() {
        if (!Files.exists(PRESETS_FILE_PATH)) {
            return new ArrayList<>(); // Return empty list if no file exists yet.
        }

        Type presetListType = new TypeToken<ArrayList<Preset>>() {}.getType();

        try (FileReader reader = new FileReader(PRESETS_FILE_PATH.toFile())) {
            List<Preset> presets = gson.fromJson(reader, presetListType);

            return presets == null ? new ArrayList<>() : presets;
        } catch (IOException e) {
            logger.error("Error loading presets from file: {}, {}", PRESETS_FILE_PATH, e.getMessage());
            return new ArrayList<>();
        }
    }
}
