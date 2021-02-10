package eidolons.game.battlecraft.rules.round;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;

public abstract class RoundRule {

    protected DC_Game game;

    public RoundRule(DC_Game game) {
        this.game = game;
    }

    public void newTurn() {
        for (Unit hero : game.getUnits()) {
            if (isOutsideCombatIgnored())
                if (game.getState().getManager().checkUnitIgnoresReset(hero))
                    continue;
            if (check(hero)) {
                apply(hero, 1f);
            }
        }
    }

    protected boolean isOutsideCombatIgnored() {
        return true;
    }

    public abstract boolean check(Unit unit);

    public abstract void apply(Unit unit, float delta);

}
