package boss.ai;

import boss.BossHandler;
import boss.logic.BossCycle;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.core.Eidolons;
import boss.BossManager;

public abstract class BossAi extends BossHandler {
    protected Unit unit;

    public BossAi(BossManager manager) {
        super(manager);
    }

    public Action getAction(Unit unit) {
        this.unit = unit;
        if (getEntity(BossCycle.BOSS_TYPE.caster)==unit) {
            return getSpell();
        }
        //TODO others
        return getAttack();
    }

    protected Action getAttack() {
        DC_ActiveObj active = unit.getAction(getActionMaster().getMainAttack());
        //zone ? it's also delayed!
        return new Action(active, Eidolons.getMainHero());
    }


    protected Action getSpell() {
        return null;
    }

}
