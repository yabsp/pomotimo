/**
 * Defines the module for the Pomotimo application.
 */
module org.pomotimo {
    // --- Required Modules ---
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.web;
    requires javafx.swing;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires org.slf4j;
    requires com.google.gson;

    requires com.sun.jna;
    requires com.sun.jna.platform;

    // --- Package Access for Reflection ---
    // This allows JavaFX (FXML) and Gson (JSON) to access your code at runtime.
    opens org.pomotimo.gui to javafx.fxml, javafx.graphics;
    opens org.pomotimo.gui.frame to javafx.fxml;
    opens org.pomotimo.gui.utils to javafx.fxml;

    opens org.pomotimo.logic.preset to com.google.gson;
    opens org.pomotimo.logic.utils to com.google.gson;
    opens org.pomotimo.logic to com.google.gson;
    opens org.pomotimo.logic.audio to com.google.gson;

}

