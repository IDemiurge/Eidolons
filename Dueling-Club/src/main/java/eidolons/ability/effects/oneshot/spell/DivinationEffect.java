package eidolons.ability.effects.oneshot.spell;

import eidolons.entity.obj.unit.Unit;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import eidolons.client.cc.logic.spells.DivinationMaster;

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
