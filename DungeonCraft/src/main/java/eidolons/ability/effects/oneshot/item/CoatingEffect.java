package eidolons.ability.effects.oneshot.item;

import eidolons.ability.effects.DC_Effect;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.unit.Unit;
import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

public class CoatingEffect extends DC_Effect {
    String amount;
    String counter;

    public CoatingEffect(String amount, String counter) {
        this.amount = amount;
        this.counter = counter;
    }

    @Override
    public boolean applyThis() {
        Unit hero = (Unit) ref.getSourceObj();
        Integer val = new Formula(amount).applyFactor(StringMaster.getValueRef(Ref.KEYS.SOURCE, PROPS.COATING_MOD)).getInt(ref);

        DC_WeaponObj offhand = hero.getOffhandWeapon();

        if (hero.getMainWeapon() != null)
            if (offhand != null) {
            val = val * 3 / 4;
        }
        if (offhand != null) {
            hero.getOffhandWeapon().addCounter(counter, "" + val);
            hero.getGame().getLogManager().logCounterModified(offhand, counter, val);
        }
        if (hero.getMainWeapon() != null) {
            hero.getMainWeapon().addCounter(counter, "" + val);
            hero.getGame().getLogManager().logCounterModified(hero.getMainWeapon(), counter, val);
        }
//        hero.getGame().getLogManager().log(""+ val);
        return true;
    }
}
