package main.game.logic.battle.turn;

import main.game.core.state.MicroGameState;
import main.system.auxiliary.log.LogMaster;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TurnTimer implements ActionListener {
    public static final int PAUSE_POOL = 100000;
    public static final int FIRST_TURN_TIME = 50000;
    public static final int BASE_TIME = 20000;
    public static final int TURN_INCREMENT = 1000;
    public static final int period = 50;
    Timer timer;
    TurnHandler handler;
    private int turnMax;
    private MicroGameState state;
    private int timeLeft;

    public TurnTimer(MicroGameState state) {
        this.state = state;
        timer = new Timer(period, this);

    }

    /**
     * @return the pausePool
     */
    public static int getPausePool() {
        return PAUSE_POOL;
    }

    /**
     * @return the firstTurnTime
     */
    public static int getFirstTurnTime() {
        return FIRST_TURN_TIME;
    }

    /**
     * @return the baseTime
     */
    public static int getBaseTime() {
        return BASE_TIME;
    }

    /**
     * @return the turnIncrement
     */
    public static int getTurnIncrement() {
        return TURN_INCREMENT;
    }

    /**
     * @return the period
     */
    public static int getPeriod() {
        return period;
    }

    public void start() {
        LogMaster.log(0, "TIMER STARTED!!! seconds:"
         + turnMax / 1000);
        update();
        handler.timerStarted();
        timer.start();

    }

    public int getMaxTurnTime() {
        return turnMax;
    }

    public void update() {
        int turnNumber = state.getRoundDisplayedNumber();
        turnMax = BASE_TIME + TURN_INCREMENT * turnNumber;
        timeLeft = turnMax;
        handler.updateGraphics();

    }

    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * @param timeLeft the timeLeft to set
     */
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        timeLeft -= period;
        handler.updateGraphics();
        if (timeLeft <= 0) {
            LogMaster.log(0, "TIMER ELAPSED!!!");
            handler.timerElapsed();
            update();
        }
    }

    /**
     * @return the turnMax
     */
    public int getTurnMax() {
        return turnMax;
    }

    /**
     * @param turnMax the turnMax to set
     */
    public void setTurnMax(int turnMax) {
        this.turnMax = turnMax;
    }

    /**
     * @return the state
     */
    public MicroGameState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(MicroGameState state) {
        this.state = state;
    }

    /**
     * @return the timer
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * @param timer the timer to set
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * @return the handler
     */
    public TurnHandler getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(TurnHandler handler) {
        this.handler = handler;
    }
}
