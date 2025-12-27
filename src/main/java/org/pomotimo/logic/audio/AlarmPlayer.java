package org.pomotimo.logic.audio;

import java.nio.file.Paths;
import java.util.Objects;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.pomotimo.logic.utils.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the playback of a looping alarm sound using JavaFX MediaPlayer.
 * This class handles loading, playing, and stopping a sound file, and is optimized
 * to only reload the media when the sound file path changes.
 */
public class AlarmPlayer {
    private AudioClip clip;
    private String soundUri;
    private boolean playing = false;
    private boolean isMuted = false;
    private final static Logger logger = LoggerFactory.getLogger(AlarmPlayer.class);

    /**
     * Constructs an AlarmPlayer and initializes it with a default sound path.
     */
    public AlarmPlayer () {
        this.soundUri = Paths.get(PersistenceManager.readOnlyAudioDataList.getFirst().filePath()).toUri().toString();
        loadClip(this.soundUri);
    }

    private void loadClip(String uri) {
        try {
            clip = new AudioClip(uri);
        } catch (Exception e) {
            logger.error("Failed to load audio clip: {}", uri, e);
        }
    }
    /**
     * Sets a new sound file to be used for the alarm.
     *
     * @param soundUri The URI string of the new sound file (e.g., from file.toURI().toString()).
     */
    public void setSoundPath(String soundUri) {
        if (this.soundUri.equals(soundUri)) return;
        stop();
        this.soundUri = soundUri;
        loadClip(soundUri);
    }

    /**
     * Starts playing the alarm sound.
     * The sound will loop indefinitely until {@link #stop()} is called. If a new sound path
     * has been set, this method will first dispose of the old player and create a new one.
     */
    public void play() {
        if (clip == null) {
            logger.warn("AudioClip is not initialized");
            return;
        }
        if (playing && clip.isPlaying()) {
            return;
        }
        clip.setCycleCount(AudioClip.INDEFINITE);
        clip.setVolume(isMuted ? 0.0 : 1.0);

        clip.play();
        playing = true;
    }

    /**
     * Stops the playback of the alarm sound if it is currently playing.
     */
    public void stop() {
        if (clip != null && playing) {
            clip.stop();
        }
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
        return isMuted;
    }

    /**
     *
     * @param m true to mute, false to unmute
     */
    public void setMute(boolean m) {
        this.isMuted = m;
        if (clip != null) {
            clip.setVolume(isMuted ? 0.0 : 1.0);
        }
    }
}
