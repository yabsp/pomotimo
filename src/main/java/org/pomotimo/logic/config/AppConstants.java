package org.pomotimo.logic.config;

import java.nio.file.Path;
import java.util.Objects;

import javafx.scene.image.Image;

import org.pomotimo.gui.utils.AlertFactory;
import org.pomotimo.platform.OperatingSystem;

public final class AppConstants {

    private AppConstants() {}


    // --- App Icons ---
    public static final Image ICON = new Image(Objects.requireNonNull(AppConstants.class.
            getResourceAsStream("/icons/app_icon.png")));
    public static final Image ICON_24x24 = new Image(Objects.requireNonNull(AppConstants.class.
            getResourceAsStream("/icons/app_icon_24x24.png")));

    // --- Operating System ---
    public static final OperatingSystem os = OperatingSystem.detect();

    // --- File system ---
    public static final Path CONFIG_DIR = Path.of(System.getProperty("user.home"), ".pomotimo");
    public static final Path MEDIA_DIR = CONFIG_DIR.resolve("media");
    public static final Path PRESETS_FILE = CONFIG_DIR.resolve("presets.json");

    // --- Application metadata ---
    public static final String APP_NAME = "Pomotimo";
    public static final String VERSION = "1.1.2";
    public static final String FILE_TYPE = ".pomo";

    // --- Default presets / settings ---
    public static final int DEFAULT_FOCUS_TIME = 1500;  // 25 min
    public static final int DEFAULT_SHORT_BREAK = 300;  // 5 min
    public static final int DEFAULT_LONG_BREAK = 900;   // 15 min
    public static final int DEFAULT_CYCLE_AMOUNT = 4;
    public static final int DEFAULT_PRIO = 1;

    // --- Default sounds ---
    public static final String[] DEFAULT_SOUNDS = {
            "winter_vivaldi.mp3",
            "zarathustra_strauss.mp3",
            "alarm_clock_digital.mp3"
    };
}
