package org.pomotimo.platform;

public enum OperatingSystem {
    WINDOWS,
    MAC,
    LINUX,
    OTHER;

    public static OperatingSystem detect() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("mac")) {
            return MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return LINUX;
        } else {
            return OTHER;
        }
    }
}
