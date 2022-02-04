package eidolons.ability.conditions.special;

import eidolons.entity.feat.active.Spell;
import eidolons.entity.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class SpellCondition extends MicroCondition {

    // AE should be able to recognize varargs as containers!!!!
    public SpellCondition(SPELL_CHECK check) {

    }

    @Override
    public boolean check(Ref ref) {
        if (!(ref.getMatchObj() instanceof Spell)) {
            return false;
        }
        Spell spell = (Spell) ref.getMatchObj();
        Unit hero = (Unit) ref.getSourceObj();
        return hero.getSpells().contains(spell);

        // if (spell.canBeActivated(ref, true))
        // return false;
    }

    public enum SPELL_CHECK {
        ACTIVE,
    }

}
