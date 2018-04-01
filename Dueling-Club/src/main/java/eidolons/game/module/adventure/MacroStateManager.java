package eidolons.game.module.adventure;

import main.entity.obj.Obj;
import main.game.core.state.StateManager;
import main.game.logic.event.Event;

/**
 * Created by JustMe on 2/8/2018.
 */
public class MacroStateManager extends StateManager {
    public MacroStateManager(MacroGameState state) {
        super(state);
    }

    @Override
    protected void makeSnapshotsOfUnitStates() {

    }

    @Override
    protected void afterBuffRuleEffects() {

    }

    @Override
    public boolean checkObjIgnoresToBase(Obj obj) {
        return false;
    }

    @Override
    protected void resetCurrentValues() {

    }

    @Override
    protected void allToBase() {

    }

    @Override
    public void checkContinuousRules() {

    }

    @Override
    public void checkCounterRules() {

    }

    @Override
    public void checkRules(Event e) {

    }

    @Override
    public void clear() {

    }
}
