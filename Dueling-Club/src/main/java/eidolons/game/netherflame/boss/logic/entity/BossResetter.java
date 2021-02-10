package eidolons.game.netherflame.boss.logic.entity;

import eidolons.entity.handlers.bf.unit.UnitResetter;
import eidolons.entity.obj.unit.Unit;
import main.entity.handlers.EntityMaster;

public class BossResetter extends UnitResetter {
    public BossResetter(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
        getCalculator().calculateAndSetDamage(false);
    }
    /*

        getGame().getRules().getDynamicBuffRules().checkBuffs(getEntity());

        control freeze?

        toggle types


     */

    private void toggleActive() {

        // master.getCalculator()


    }
        private void finalizeReset() {
        // getGame().getRules().getDynamicBuffRules().checkBuffs(getEntity());
    }
}
