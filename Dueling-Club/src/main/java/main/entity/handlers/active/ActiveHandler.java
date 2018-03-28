package main.entity.handlers.active;

import main.entity.active.DC_ActiveObj;
import main.entity.handlers.EntityHandler;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 2/24/2017.
 */
public abstract class ActiveHandler extends EntityHandler<DC_ActiveObj> {

//    protected Unit ownerObj;

    public ActiveHandler(DC_ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
//        ownerObj = entity.getOwnerObj();
    }

    public Unit getOwnerObj() {
        return getAction().getOwnerObj();
    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

    public DC_ActiveObj getAction() {
        return super.getEntity();
    }

    @Override
    public Executor getHandler() {
        return getMaster().getHandler();
    }

    @Override
    public ActiveAnimator getAnimator() {
        return getMaster().getAnimator();
    }

    @Override
    public ActiveLogger getLogger() {
        return getMaster().getLogger();
    }

    @Override
    public ActiveInitializer getInitializer() {
        return getMaster().getInitializer();
    }

    @Override
    public ActiveCalculator getCalculator() {
        return getMaster().getCalculator();
    }

    @Override
    public ActiveChecker getChecker() {
        return getMaster().getChecker();
    }

    @Override
    public ActiveResetter getResetter() {
        return getMaster().getResetter();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }
}
