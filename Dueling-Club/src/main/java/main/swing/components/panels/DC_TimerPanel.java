package main.swing.components.panels;

import main.game.MicroGameState;
import main.game.turn.TurnHandler;
import main.game.turn.TurnTimer;
import main.swing.generic.components.G_Panel;

import javax.swing.*;

public class DC_TimerPanel extends G_Panel implements TurnHandler {

    private JProgressBar bar;
    private TurnTimer timer;
    private MicroGameState state;

    public DC_TimerPanel(TurnTimer timer) {
        this.timer = timer;
        this.state = timer.getState();
        timer.setHandler(this);
        bar = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                timer.getMaxTurnTime());
        add(bar, "pos 0 0 "
                // + ", w " + GuiManager.getBattleFieldCellsX()
                // * GuiManager.getCellSize() + ", h "
                // + GuiManager.getSmallObjSize()
        );
    }

    @Override
    public void updateGraphics() {

        bar.setValue(timer.getTimeLeft());
        // super.refresh();
    }

    @Override
    public void timerElapsed() {
        if (state.isMyTurn() || state.getGame().isOffline()
                || state.getGame().isHotseatMode())
            state.getManager().endTurn();

    }

    @Override
    public void timerStarted() {
        bar.setMaximum(timer.getMaxTurnTime());

    }
}
