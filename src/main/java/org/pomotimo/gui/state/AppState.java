package org.pomotimo.gui.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.pomotimo.logic.preset.Preset;

/**
 * This class is used to decouple the state of our application from our model.
 */
public class AppState {

    private final ObjectProperty<Preset> currentPreset =
            new SimpleObjectProperty<>();

    private final ObjectProperty<TimerViewState> timerViewState =
            new SimpleObjectProperty<>(TimerViewState.EMPTY);

    /**
     * Returning and {@link ObjectProperty<Preset>} object of our current preset.
     * @return the {@link ObjectProperty<Preset>} that is currently in use.
     */
    public ObjectProperty<Preset> currentPresetProperty() {
        return currentPreset;
    }

    /**
     *
     * @return {@link Preset} currently in use.
     */
    public Preset getCurrentPreset() {
        return currentPreset.get();
    }

    /**
     * Set the current preset for the whole application.
     * @param preset the new {@link Preset}.
     */
    public void setCurrentPreset(Preset preset) {
        currentPreset.set(preset);
    }

    /**
     *
     * @return {@link ObjectProperty<TimerViewState>} with the view state of the timer pane.
     */
    public ObjectProperty<TimerViewState> mainViewStateProperty() {
        return timerViewState;
    }

    /**
     *  Set the state of the timer view.
     * @param state the new state of the timer. See {@link TimerViewState} for possible states.
     */
    public void setTimerViewState(TimerViewState state) {
        timerViewState.set(state);
    }

}
