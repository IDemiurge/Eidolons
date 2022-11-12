
package eidolons.entity.handlers.bf.unit;

import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.entity.handlers.EntityAnimator;
import main.entity.handlers.EntityHandler;
import main.entity.handlers.EntityLogger;
import main.entity.handlers.EntityMaster;
import main.game.logic.generic.ActionManager;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitMaster extends EntityMaster<Unit> {

    public UnitMaster(Unit entity) {
        super(entity);
    }

    public Unit getUnit() {
        return getEntity();
    }

    @Override
    protected EntityAnimator<Unit> createEntityAnimator() {
//        return new UnitAnimator(getUnit(), this);
        return null;
    }

    @Override
    public ActionManager getActionManager() {
        return getGame().getActionManager();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    protected EntityLogger createEntityLogger() {
        return new UnitLogger(getUnit(), this);
    }

    @Override
    protected UnitInitializer createInitializer() {
        return new UnitInitializer(getUnit(), this);
    }

    @Override
    protected UnitChecker createEntityChecker() {
        return new UnitChecker(getUnit(), this);
    }

    @Override
    protected UnitResetter createResetter() {
        return new UnitResetter(getUnit(), this);
    }

    @Override
    protected UnitCalculator createCalculator() {
        return new UnitCalculator(getUnit(), this);
    }

    @Override
    protected EntityHandler createHandler() {
        return new UnitHandler(getUnit(), this);
    }
}
