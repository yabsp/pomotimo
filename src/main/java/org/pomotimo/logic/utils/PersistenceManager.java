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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handles saving and loading of presets to and from a JSON file.
 */
public class PersistenceManager {

    // Store presets in a .pomotimo folder in the user's home directory.
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".pomotimo");
    private static final Path PRESETS_FILE_PATH = CONFIG_PATH.resolve("presets.json");
    private static final Path MEDIA_PATH = CONFIG_PATH.resolve("media");

    // Gson instance for JSON serialization/deserialization. Pretty printing makes it human-readable.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    private static final List<Preset.AudioData> audioDataList = new ArrayList<>();
    public static final List<Preset.AudioData> readOnlyAudioDataList = Collections.unmodifiableList(audioDataList);

    static {
        logger.debug("Running static initializer for PersistenceManager...");
        final String resourceDir = "/sounds";
        try {
            URL resourceUrl = PersistenceManager.class.getResource(resourceDir);
            if (resourceUrl == null) {
                throw new IOException("Resource directory not found: " + resourceDir);
            }
            URI uri = resourceUrl.toURI();
            Path resourcePath;
            // Handle the difference between running from a file system (in IDE) and from a JAR file.
            if (uri.getScheme().equals("jar")) {
                // If the resource is in a JAR, we create a temporary file system to access it.
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    resourcePath = fileSystem.getPath(resourceDir);
                    loadAudioFromPath(resourcePath);
                }
            } else {
                // If the resource is a regular file on disk (running from IDE).
                resourcePath = Paths.get(uri);
                loadAudioFromPath(resourcePath);
            }

            logger.debug("Static audio data list initialized. Total sounds: {}", audioDataList.size());

        } catch (IOException |
                 URISyntaxException e) {
            logger.error("FATAL: Failed to load audio resources from '" + resourceDir + "'.", e);
        }
    }

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
            return new ArrayList<>();
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

    /**
     * Refresh {@link PersistenceManager#readOnlyAudioDataList}.
     * <p>Reads file names that are persistently stored, compares with the ones on the disk
     * and adds the missing ones or removes the ones that have been deleted.</p>
     * <p>This method is usually called on application startup.</p>
     */

    public void refreshAudioDataList() {
        if (!Files.exists(MEDIA_PATH)) {
            return;
        }
        List<Preset.AudioData> audioFiles = new ArrayList<>();
        try (Stream<Path> stream = Files.list(Paths.get(MEDIA_PATH.toUri()))) {
             audioFiles = stream
                    .filter(Files::isRegularFile)
                    .map(e -> Preset.AudioData
                            .createAudioDataFromFile(e.getFileName().toString(), false))
                    .toList();
        } catch (IOException e){
            logger.error("Error reading directory: {}, {}", MEDIA_PATH, e.getMessage());
        }
        List<Preset.AudioData> filesToAdd = new ArrayList<>(audioFiles);
        filesToAdd.removeAll(audioDataList);
        audioDataList.addAll(filesToAdd);
    }

    /**
     *  Adds an entry to the {@link PersistenceManager#audioDataList}.
     * @param entry the {@link Preset.AudioData} record that is added.
     */
    public void addAudioDataEntry (Preset.AudioData entry) {
        audioDataList.add(entry);
    }

    /**
     *  Removes an entry from the {@link PersistenceManager#audioDataList}.
     * @param entry the {@link Preset.AudioData} record that is removed.
     */
    public void removeAudioDataEntry (Preset.AudioData entry) {
        audioDataList.remove(entry);
    }

    /**
     * Helper method to walk a given path and load all .mp3 files into the static list.
     * @param path The directory path to scan for audio files.
     * @throws IOException if an I/O error occurs when walking the path.
     */
    private static void loadAudioFromPath(Path path) throws IOException {
        try (Stream<Path> paths = Files.walk(path, 1)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().toLowerCase().endsWith(".mp3"))
                 .forEach(e -> {
                     Preset.AudioData temp = Preset.AudioData.createAudioDataFromFile(e.toAbsolutePath().toString(), false);
                     audioDataList.add(temp);
                 });
        }
    }
}
