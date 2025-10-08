package org.pomotimo.logic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.pomotimo.logic.preset.Preset;
import org.pomotimo.logic.preset.PresetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class PresetImporterExporter {

    private static final Logger logger = LoggerFactory.getLogger(PresetImporterExporter.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".pomotimo");
    private static final Path MEDIA_PATH = CONFIG_PATH.resolve("media");

    private final PresetManager presetManager;

    public PresetImporterExporter(PresetManager presetManager) {
        this.presetManager = presetManager;
    }

    /**
     * Exports a single Preset and its associated media files into a single .pomo archive.
     *
     * @param preset The Preset to export.
     * @param destinationFile The file to save the archive to (chosen by the user).
     * @return true if export was successful, false otherwise.
     */
    public boolean exportPreset(Preset preset, File destinationFile) {
        Preset pr = createPresetWithRelativePaths(preset);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destinationFile))) {
            ZipEntry jsonEntry = new ZipEntry("preset.json");
            zos.putNextEntry(jsonEntry);
            zos.write(gson.toJson(pr).getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            addFileToZip(preset.getCurrentAudio().filePath(), zos);

            addFileToZip(preset.getImageFile(), zos);

            logger.info("Successfully exported preset '{}' to {}", preset.getName(), destinationFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
            logger.error("Failed to export preset '{}': {}", preset.getName(), e.getMessage());
            return false;
        }
    }

    /**
     * Imports a Preset from a .pomo archive file.
     *
     * @param sourceFile The .pomo file to import.
     * @return An Optional containing the imported Preset if successful, otherwise empty.
     */
    public Optional<Preset> importPreset(File sourceFile) {
        try {
            Files.createDirectories(MEDIA_PATH);
        } catch (IOException e) {
            logger.error("Could not create media directory: {}", MEDIA_PATH, e);
            return Optional.empty();
        }

        Preset importedPreset = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("preset.json")) {
                    String json = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
                    importedPreset = gson.fromJson(json, Preset.class);
                } else {
                    Path destinationPath = MEDIA_PATH.resolve(entry.getName());
                    Files.copy(zis, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Extracted media file to {}", destinationPath);
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            logger.error("Failed to import preset from {}: {}", sourceFile.getAbsolutePath(), e.getMessage());
            return Optional.empty();
        }

        if (importedPreset != null) {
            updatePathsToAbsolute(importedPreset);
            presetManager.addPreset(importedPreset);
            presetManager.setCurrentPreset(importedPreset);
            logger.info("Successfully imported preset '{}'", importedPreset.getName());
            return Optional.of(importedPreset);
        }

        return Optional.empty();
    }

    // --- Helper Methods ---

    private void addFileToZip(String filePath, ZipOutputStream zos) {
        try{
            if (filePath != null && !filePath.isEmpty()) {
                Path sourcePath = Paths.get(filePath);
                if (Files.exists(sourcePath)) {
                    ZipEntry fileEntry = new ZipEntry(sourcePath.getFileName().toString());
                    zos.putNextEntry(fileEntry);
                    Files.copy(sourcePath, zos);
                    zos.closeEntry();
                }
            }
        } catch (IOException e) {
            logger.error("Error when adding file to zip. {}", e.getMessage());
        }

    }

    private Preset createPresetWithRelativePaths(Preset original) {
        Preset.AudioData originalAudio = original.getCurrentAudio();
        Preset forExport = new Preset(
                original.getName(),
                original.getDurationFocus(),
                original.getDurationShortBreak(),
                original.getDurationLongBreak(),
                original.getCycleAmount()
        );
        if (originalAudio != null) {
            if (originalAudio.isInternalResource()) {
                forExport.setCurrentAudio(Preset.AudioData.createAudioDataFromFile
                                                                  (originalAudio.filePath(),
                                                                          true));
            } else {
                String relPath = Paths.get(originalAudio.filePath()).getFileName().toString();
                forExport.setCurrentAudio(Preset.AudioData.createAudioDataFromFile(relPath, false));
            }

        }
        if (original.getImageFile() != null && !original.getImageFile().isEmpty()) {
            forExport.setImageFile(Paths.get(original.getImageFile()).getFileName().toString());
        }
        if (original.getTaskAmount() > 0) {
            original.getTasks().forEach(forExport::addTask);
        }
        return forExport;
    }

    private void updatePathsToAbsolute(Preset preset) {
        if (preset.getCurrentAudio() != null) {
            if (!preset.getCurrentAudio().isInternalResource()) {
                Path newPath = MEDIA_PATH.resolve(preset.getCurrentAudio().filePath());
                preset.setCurrentAudio(Preset.AudioData.createAudioDataFromFile(newPath.toAbsolutePath().toString(),
                        preset.getCurrentAudio().isInternalResource()));
            }
        }
        if (preset.getImageFile() != null && !preset.getImageFile().isEmpty()) {
            Path newPath = MEDIA_PATH.resolve(preset.getImageFile());
            preset.setImageFile(newPath.toAbsolutePath().toString());
        }
    }
}