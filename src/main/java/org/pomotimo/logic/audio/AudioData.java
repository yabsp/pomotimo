package org.pomotimo.logic.audio;

import org.pomotimo.logic.config.AppConstants;

public record AudioData (String name, String filePath) {

    /**
     * Creates an AudioData record from a file Path.
     * Extracts the name without the extension and gets the absolute file path.
     *
     * @param filePath The path where the audio file is located.
     *             Assumes that a file exists under this path.
     * @return  A new AudioData instance.
     */
    public static AudioData createAudioDataFromFile(String filePath) {
        String fileName = getFileNameFromString(filePath);
        String nameWithoutExtension = fileName;
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            nameWithoutExtension = fileName.substring(0, lastDotIndex);
        }
        return new AudioData(nameWithoutExtension, filePath);
    }

    /**
     * Extracts the filename from any path/URL/URI string by finding the last separator.
     * @param pathString The full path or URL string.
     * @return The filename with its extension.
     */
    public static String getFileNameFromString(String pathString) {
        if (pathString == null || pathString.isEmpty()) {
            return "";
        }
        int lastSlash = 0;
        switch (AppConstants.os) {
            case WINDOWS -> lastSlash = pathString.lastIndexOf('\\');
            case LINUX, MAC -> lastSlash = pathString.lastIndexOf('/');

        }
        if (lastSlash >= 0) {
            return pathString.substring(lastSlash + 1);
        }
        return pathString;
    }

    @Override
    public String toString() {
        return this.name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof AudioData(String nameOther, String path)
                && this.name.equals(nameOther)
                && this.filePath.equals(path);
    }
}
