package org.pomtim.logic;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class PomodoroTimer {

    private Timer timer;
    private Consumer<Integer> tickFunc;
    private int remainingSeconds;
    private boolean paused = false;

    /**
     * Starts a new timer.
     * @param seconds The duration of the timer in seconds. Must be of type {@code int} and must not be {@code null}.
     * @param tickFunc Consumer that takes an input of type {@code int} as argument. The timer ticks every second and accepts the tickFunc consumer,
     *             as long as remaining time is > 0 (Remaining time = duration - running time).
     */
    public void start (int seconds, Consumer<Integer> tickFunc) {
        stop();
        this.tickFunc = tickFunc;
        this.remainingSeconds = seconds;
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
     * If no timer has been started, it will do nothing. One can start a timer with {@link PomodoroTimer#start(int, Consumer)}}.
     * @param seconds the seconds to reset the timer, must be of {@code type int}. Must not be {@code null}.
     */

    public void reset(int seconds) {
        stop();
        remainingSeconds = seconds;
    }

    /**
     * Stops the current timer. If no timer exists does nothing.
     */

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void pause() {
        if (timer == null) return;
        if (paused) {
            paused = false;
            start(remainingSeconds, tickFunc);
        } else {
            paused = true;
            stop();
        }
    }
}
