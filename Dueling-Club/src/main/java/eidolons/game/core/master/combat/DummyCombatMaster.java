package eidolons.game.core.master.combat;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 6/2/2017.
 */
public class DummyCombatMaster extends CombatMaster {
    private String blockActionExceptions;
    private boolean actionsBlocked;

    public DummyCombatMaster(DC_Game game) {
        super(game);
    }

    @Override
    public boolean isActionBlocked(DC_ActiveObj activeObj) {
        if (blockActionExceptions != null)
            if (blockActionExceptions.contains(activeObj.getName()))
                return false;
        return actionsBlocked;
    }

    public String getBlockActionExceptions() {
        return blockActionExceptions;
    }

    public void setBlockActionExceptions(String blockActionExceptions) {
        this.blockActionExceptions = blockActionExceptions;
    }

    public boolean isActionsBlocked() {
        return actionsBlocked;
    }

    public void setActionsBlocked(boolean actionsBlocked) {
        this.actionsBlocked = actionsBlocked;
    }
}
