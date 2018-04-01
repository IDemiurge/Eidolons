package eidolons.ability.effects.oneshot.spell;

import eidolons.client.cc.logic.spells.DivinationMaster;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;

public class DivinationEffect extends MicroEffect implements OneshotEffect {

    @Override
    public boolean applyThis() {
        Unit hero = (Unit) ref.getSourceObj();
        try {
            DivinationMaster.divine(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }

        return true;
    }
}
