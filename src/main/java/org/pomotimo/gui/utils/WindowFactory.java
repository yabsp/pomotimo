package org.pomotimo.gui.utils;

import javafx.stage.Stage;

//TODO: As the project grows initializing windows should be moved from PomotimoGUI.java to here.
// At the moment only the enum is used.
public interface WindowFactory {

    enum WindowType {
        MAIN_WINDOW,
        DIALOG_WINDOW,
        MENU_WINDOW
    }

}
