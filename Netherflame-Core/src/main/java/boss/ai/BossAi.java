package boss.ai;

import boss.BossHandler;
import boss.logic.BossCycle;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.core.Core;
import boss.BossManager;

public abstract class BossAi extends BossHandler {
    protected Unit unit;

    public BossAi(BossManager manager) {
        super(manager);
    }

    public AiAction getAction(Unit unit) {
        this.unit = unit;
        if (getEntity(BossCycle.BOSS_TYPE.caster)==unit) {
            return getSpell();
        }
        //TODO others
        return getAttack();
    }

    protected AiAction getAttack() {
        DC_ActiveObj active = unit.getAction(getActionMaster().getMainAttack());
        //zone ? it's also delayed!
        return new AiAction(active, Core.getMainHero());
    }


    protected AiAction getSpell() {
        return null;
    }

}
