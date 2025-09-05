package org.pomotimo.logic.audio;

import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmPlayer {
    private MediaPlayer player;
    private String soundUri;
    private boolean newUri;
    private boolean playing;
    private final static Logger logger = LoggerFactory.getLogger(AlarmPlayer.class);

    public AlarmPlayer () {
        this.soundUri = Objects.requireNonNull(getClass()
                .getResource("/sounds/winter_vivaldi.mp3")).toExternalForm();
        newUri = true;
    }

    public void setSoundPath(String soundUri) {
        this.soundUri = soundUri;
        newUri = true;
    }

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

    public void stop() {
        if (player == null) {
            logger.info("Trying to stop but player is not defined");
            return;
        }
        player.stop();
        playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }
}
