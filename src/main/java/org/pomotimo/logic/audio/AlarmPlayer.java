package org.pomotimo.logic.audio;

import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the playback of a looping alarm sound using JavaFX MediaPlayer.
 * This class handles loading, playing, and stopping a sound file, and is optimized
 * to only reload the media when the sound file path changes.
 */
public class AlarmPlayer {
    private MediaPlayer player;
    private String soundUri;
    private boolean newUri;
    private boolean playing;
    private final static Logger logger = LoggerFactory.getLogger(AlarmPlayer.class);

    /**
     * Constructs an AlarmPlayer and initializes it with a default sound path.
     */
    public AlarmPlayer () {
        this.soundUri = Objects.requireNonNull(getClass()
                .getResource("/sounds/winter_vivaldi.mp3")).toExternalForm();
        newUri = true;
        player = new MediaPlayer(new Media(soundUri));
    }

    /**
     * Sets a new sound file to be used for the alarm.
     *
     * @param soundUri The URI string of the new sound file (e.g., from file.toURI().toString()).
     */
    public void setSoundPath(String soundUri) {
        this.soundUri = soundUri;
        newUri = true;
    }

    /**
     * Starts playing the alarm sound.
     * The sound will loop indefinitely until {@link #stop()} is called. If a new sound path
     * has been set, this method will first dispose of the old player and create a new one.
     */
    public void play() {
        if (soundUri == null) {
            logger.warn("URI to sound is not defined");
            return;
        }
        if (newUri) {
            if (player != null) {
                player.dispose();
            }
            player = new MediaPlayer(new Media(soundUri));
            player.setCycleCount(MediaPlayer.INDEFINITE);
            newUri = false;
        }
        player.seek(Duration.ZERO);
        player.play();
        playing = true;
    }

    /**
     * Stops the playback of the alarm sound if it is currently playing.
     */
    public void stop() {
        if (player == null) {
            logger.info("Trying to stop but player is not defined");
            return;
        }
        player.stop();
        playing = false;
    }

    /**
     * Checks if the alarm sound is currently playing.
     *
     * @return {@code true} if the player is active, {@code false} otherwise.
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     *
     * @return true if the player is muted
     * false otherwise.
     */
    public boolean isMute() {
        return player.isMute();
    }

    /**
     *
     * @param m true to mute, false to unmute
     */
    public void setMute(boolean m) {
        player.setMute(m);
    }
}
