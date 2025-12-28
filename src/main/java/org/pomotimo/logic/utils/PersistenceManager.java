package org.pomotimo.logic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.pomotimo.logic.config.AppConstants;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.audio.AudioData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles saving and loading of presets to and from a JSON file.
 */
public class PersistenceManager {

    // Gson instance for JSON serialization/deserialization. Pretty printing makes it human-readable.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    private static final List<AudioData> audioDataList = new ArrayList<>();
    public static final List<AudioData> readOnlyAudioDataList = Collections.unmodifiableList(audioDataList);

    static {
        logger.debug("Running static initializer for PersistenceManager...");
        try {
            if (!Files.exists(AppConstants.MEDIA_DIR)) {
                Files.createDirectories(AppConstants.MEDIA_DIR);
            }
        } catch (IOException e) {
            logger.warn("Couldn't set up directory structure");
        }
        extractDefaultSounds();
        refreshAudioDataList();
    }

    /**
     * Saves the provided list of presets to a JSON file.
     *
     * @param presets The list of presets to save.
     */
    public void savePresets(List<Preset> presets) {
        try {
            if (!Files.exists(AppConstants.CONFIG_DIR)) {
                Files.createDirectories(AppConstants.CONFIG_DIR);
            }
        } catch (IOException e){
            logger.error("Error when trying to create directories for presets.{}", e.getMessage());
        }

        try (FileWriter writer = new FileWriter(AppConstants.PRESETS_FILE.toFile())) {
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
        if (!Files.exists(AppConstants.PRESETS_FILE)) {
            return new ArrayList<>();
        }

        Type presetListType = new TypeToken<ArrayList<Preset>>() {}.getType();

        try (FileReader reader = new FileReader(AppConstants.PRESETS_FILE.toFile())) {
            List<Preset> presets = gson.fromJson(reader, presetListType);

            return presets == null ? new ArrayList<>() : presets;
        } catch (IOException e) {
            logger.error("Error loading presets from file: {}, {}", AppConstants.PRESETS_FILE, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Refresh {@link PersistenceManager#readOnlyAudioDataList}.
     * <p>Reads file names that are persistently stored, compares with the ones on the disk
     * and adds the missing ones or removes the ones that have been deleted.</p>
     * <p>This method is usually called on application startup.</p>
     */

    public static void refreshAudioDataList() {
        if (!Files.exists(AppConstants.MEDIA_DIR)) {
            return;
        }
        List<AudioData> audioFiles = new ArrayList<>();
        try (Stream<Path> stream = Files.list(Paths.get(AppConstants.MEDIA_DIR.toUri()))) {
             audioFiles = stream
                    .filter(e1 -> {
                        if (Files.isRegularFile(e1)) {
                            return e1.toString().endsWith(".wav");
                        }
                        return false;
                    })
                    .map(e -> {
                        logger.debug("Following audio added to list: {}", e.toString());
                        return AudioData.createAudioDataFromFile(e.toString());

                    })
                    .toList();
        } catch (IOException e){
            logger.error("Error reading directory: {}, {}", AppConstants.MEDIA_DIR, e.getMessage());
        }
        List<AudioData> filesToAdd = new ArrayList<>(audioFiles);
        filesToAdd.removeAll(audioDataList);
        audioDataList.addAll(filesToAdd);
    }

    /**
     *  Adds an entry to the {@link PersistenceManager#audioDataList}.
     * @param entry the {@link AudioData} record that is added.
     */
    public void addAudioDataEntry (AudioData entry) {
        audioDataList.add(entry);
    }

    /**
     *  Removes an entry from the {@link PersistenceManager#audioDataList}.
     * @param entry the {@link AudioData} record that is removed.
     */
    public void removeAudioDataEntry (AudioData entry) {
        audioDataList.remove(entry);
    }

    private static void extractDefaultSounds() {
        logger.info("Extracting default sounds to {}", AppConstants.MEDIA_DIR);

        for (String name : AppConstants.DEFAULT_SOUNDS) {
            Path target = AppConstants.MEDIA_DIR.resolve(name);
            if (!Files.exists(target)) {
                try (InputStream in = PersistenceManager.class.getResourceAsStream("/sounds/" + name)) {
                    if (in != null) {
                        Files.copy(in, target);
                        logger.info("Copied default sound: {}", name);
                    } else {
                        logger.warn("Missing internal sound resource: {}", name);
                    }
                } catch (IOException e) {
                    logger.error("Failed to copy default sound {}: {}", name, e.getMessage());
                }
            }
        }
    }

}
