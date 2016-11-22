package main.game.battlefield;

import main.entity.obj.Obj;
import main.game.MicroGame;
import main.game.MicroGameState;
import main.game.player.Player;
import main.swing.generic.components.Builder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public abstract class SwingBattleField implements BattleField {
    protected BattleFieldGrid grid;
    protected Component c;
    protected Player player2;
    protected Player player1;
    protected Builder builder;
    MicroGameState state;
    MicroGame game;
    private boolean initialized = false;

    private Obj activeSelectedObj;
    private Obj infoSelectedObj;

    public SwingBattleField(Player player1, Player player2, MicroGameState state) {
        this.state = state;
        this.player1 = player1;
        this.player2 = player2;
        this.game = state.getGame();

    }

    public void addMouseWheelListener(MouseWheelListener l) {
        c.addMouseWheelListener(l);
    }

    public Component getBF_Comp() {
        if (c == null)
            init();
        return c;
    }

    public abstract void init();

    public Builder getBuilder() {
        return builder;
    }

    public void refresh() {
        if (SwingUtilities.isEventDispatchThread())
            builder.refresh();
        else
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            builder.refresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Error | InvocationTargetException | InterruptedException e) {
                e.printStackTrace();
            }

        // c.revalidate();
        // c.repaint();
    }

    public BattleFieldGrid getGrid(Integer z) {
        return null;
    }

    public MicroGameState getState() {
        return state;
    }

    public void setState(MicroGameState state) {
        this.state = state;
    }

    public BattleFieldGrid getGrid() {
        return grid;
    }

    public void setGrid(BattleFieldGrid grid) {
        this.grid = grid;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Obj getObj(Coordinates coordinates) {
        try {
            return getGrid().getTopObj(coordinates);
        } catch (Exception e) {
            return null;
        }
    }

    public Obj getActiveSelectedObj() {
        return activeSelectedObj;
    }

    public void setActiveSelectedObj(Obj activeSelectedObj) {
        this.activeSelectedObj = activeSelectedObj;
    }

    public Obj getInfoSelectedObj() {
        return infoSelectedObj;
    }

    public void setInfoSelectedObj(Obj infoSelectedObj) {
        this.infoSelectedObj = infoSelectedObj;
    }

    public abstract void refreshSpellbook();

    public Obj getCell(Coordinates coordinates) {
        return getGrid().getCell(coordinates);
    }
}
