package eidolons.game.core.atb.explore;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.atb.AtbController;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.atb.AtbUnit;

public class ExploreAtbController extends AtbController {
    public ExploreAtbController(AtbTurnManager manager, Unit unit) {
        super(manager);
    }


    @Override
    protected void processTimeElapsed(Float time) {
        /*
        cannot really throw events each 0.1 seconds or so for each unit, we want to enable
        smooth MASS COMBAT

        alternative:

        when to call step()?

        what time to pass in step?

        what to do with update results?

        if they are as expected, keep to yourself
        if not...

        just cache their last known initiative value?
        > we could of course have a switch between full-event mode and this optimized one

        we need some common points

        bind on change
        just the current value?


         */
//GuiEventManager.trigger(GuiEventType.explore_atb_changed);
        manager.getGame().getManager().atbTimeElapsed(time);
    }

    @Override
    protected float getDefaultTimePeriod() {
        return super.getDefaultTimePeriod();
    }

    @Override
    protected boolean checkAllInactive() {
        return super.checkAllInactive();
    }

    @Override
    public void newRound() {
        super.newRound();
    }

    @Override
    protected float getAtbGainForUnit(Float time, AtbUnit unit) {
        return super.getAtbGainForUnit(time, unit);
    }

    @Override
    protected void updateTurnOrder() {
        super.updateTurnOrder();
    }

    @Override
    public boolean isNextTurn() {
        return super.isNextTurn();
    }
}
