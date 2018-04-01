package main.game.bf;

import main.entity.obj.Obj;
import main.game.core.game.MicroGame;
import main.game.core.state.MicroGameState;
import main.swing.generic.components.Builder;
import main.system.launch.CoreEngine;

import javax.swing.*;

//for Level Editor and other legacy....
public abstract class SwingBattleField implements BattleField {
    protected BattleFieldGrid grid;
    protected Builder builder;
    MicroGameState state;
    MicroGame game;
    private boolean initialized = false;

    private Obj activeSelectedObj;
    private Obj infoSelectedObj;

    public SwingBattleField(MicroGameState state) {
        this.state = state;
        this.game = state.getGame();

    }


    public Builder getBuilder() {
        return builder;
    }

    public void refresh() {
        if (!CoreEngine.isSwingOn()) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            builder.refresh();
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            builder.refresh();
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                });
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
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


    public Obj getCell(Coordinates coordinates) {
        return getGrid().getCell(coordinates);
    }
}
