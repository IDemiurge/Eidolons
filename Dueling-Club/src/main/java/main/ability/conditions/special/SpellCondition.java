package main.ability.conditions.special;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.active.DC_SpellObj;
import main.entity.obj.unit.Unit;

public class SpellCondition extends MicroCondition {

    // AE should be able to recognize varargs as containers!!!!
    public SpellCondition(SPELL_CHECK check) {

    }

    @Override
    public boolean check(Ref ref) {
        if (!(ref.getMatchObj() instanceof DC_SpellObj)) {
            return false;
        }
        DC_SpellObj spell = (DC_SpellObj) ref.getMatchObj();
        Unit hero = (Unit) ref.getSourceObj();
        if (!hero.getSpells().contains(spell)) {
            return false;
        }

        // if (spell.canBeActivated(ref, true))
        // return false;

        return true;
    }

    public enum SPELL_CHECK {
        ACTIVE,
    }

}