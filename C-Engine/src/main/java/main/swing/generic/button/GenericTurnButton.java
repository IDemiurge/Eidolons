package main.swing.generic.button;

import main.game.GameState;
import main.swing.generic.components.Refreshable;
import main.swing.generic.components.misc.G_Button;

import java.awt.event.ActionEvent;

public abstract class GenericTurnButton extends G_Button implements Refreshable {

    protected GameState state;

    public GenericTurnButton(GameState state2) {
        this.state = state2;

        button.addActionListener(this);
        refresh();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // super.actionPerformed(e); generic sound

        state.getManager().endTurn();
        refresh();
    }
}