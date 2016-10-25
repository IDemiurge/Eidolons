package main.swing.components.buttons;

import main.game.GameState;
import main.game.MicroGameState;
import main.swing.generic.button.GenericTurnButton;

public class TurnButton extends GenericTurnButton {
    private static final String MY = "End the Turn";
    private static final String NOT_MY = "Enemy moves...";

    public TurnButton(GameState state2) {
        super(state2);

    }

    public MicroGameState getState() {
        return (MicroGameState) state;
    }

    @Override
    public void refresh() {
        removeAll();
        add(button, "pos 0 0");
        revalidate();
        if (!getState().getGame().isHotseatMode())
            button.setEnabled(getState().isMyTurn());
        button.setText((getState().isMyTurn()) ? MY : NOT_MY);
        repaint();
    }
}
