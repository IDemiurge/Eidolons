package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 9/23/2017.
 */
public abstract class AiBehavior {

    protected final AiMaster master;
    protected UnitAI ai;
    protected float timer;
    protected BEHAVIOR_STATUS status;

    public enum BEHAVIOR_STATUS {
        WAITING,
        RUNNING,
        BLOCKED,

    }
    public AiBehavior(AiMaster master, UnitAI ai) {
        this.master = master;
        this.ai = ai;
    }

    public void act(float delta) {
        timer += delta;
        update(delta);
    }

    protected abstract void update(float delta);

    protected boolean isEnabled(UnitAI ai) {
        return true;
    }

    protected AiMaster getMaster(UnitAI ai) {
        return ai.getUnit().getGame().getAiManager();
    }

    protected void initOrders() {

    }

    public abstract ActionSequence getOrders(UnitAI ai);

    protected boolean checkLost() {
        return timer > getTimeBeforeFail();
    }

    protected   float getTimeBeforeFail(){
        return 50;
    }

}
