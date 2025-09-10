package org.pomotimo.logic;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Provides basic countdown timer functionality using a {@link java.util.Timer}.
 * This class allows for starting, pausing, and resetting a timer that ticks every second.
 */
public class PomoTimer {

    private Timer timer;
    private Consumer<Integer> tickFunc;
    private int remainingSeconds;
    private boolean running = false;

    /**
     * Starts a new timer.
     * The duration is given by the {@link PomoTimer#remainingSeconds} seconds variable.
     * Be sure to set {@link PomoTimer#setRemainingSeconds(int)} appropriately beforehand.
     * @param tickFunc Consumer that takes an input of type {@code int} as argument. The timer ticks every second and accepts the tickFunc consumer,
     *             as long as remaining time is > 0 (Remaining time = duration - running time).
     */
    public void start (Consumer<Integer> tickFunc) {
        if(running){
            stop();
        }
        running = true;
        this.tickFunc = tickFunc;
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(remainingSeconds <= 0) {
                  stop();
                } else {
                    tickFunc.accept(--remainingSeconds);
                }
            }
        }, 0, 1000);
    }

    /**
     * This method will reset any existing timer.
     * If no timer has been started, it will do nothing. One can start a timer with {@link PomoTimer#start(Consumer)}}.
     * @param seconds the seconds to reset the timer, must be of {@code type int}. Must not be {@code null}.
     */

    public void reset(int seconds) {
        stop();
        remainingSeconds = seconds;
        running = false;
    }

    /**
     * Stops the current timer. If no timer exists does nothing.
     */
    private void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Pause the current timer. If no timer exists does nothing.
     */
    public void pause() {
        if (timer == null) return;
        if(running) {
            running = false;
            stop();
        }
    }

    /**
     * Gets the number of seconds currently remaining on the timer.
     *
     * @return The remaining time in seconds.
     */
    public int getRemainingSeconds() {
        return this.remainingSeconds;
    }

    /**
     * Sets the total duration of the timer. This should be called before starting the timer
     * to define its countdown period.
     *
     * @param seconds The total time in seconds for the countdown.
     */
    public void setRemainingSeconds(int seconds) {
        this.remainingSeconds = seconds;
    }

    /**
     * Checks if the timer is currently active and counting down.
     *
     * @return {@code true} if the timer is running, {@code false} otherwise.
     */
    public boolean isRunning(){
        return running;
    }
}
