package eidolons.ability.conditions.special;

import eidolons.entity.feat.active.Spell;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class EnchantSpellCondition extends MicroCondition {

    private TARGETING_MODE[] modes = {AbilityEnums.TARGETING_MODE.ANY_ALLY,
     AbilityEnums.TARGETING_MODE.ANY_ENEMY, AbilityEnums.TARGETING_MODE.ANY_UNIT,
     AbilityEnums.TARGETING_MODE.SINGLE,};

    public EnchantSpellCondition(SPELL_ENCHANT_GROUP type) {

    }

    public EnchantSpellCondition(SPECIAL_EFFECTS_CASE type) {

    }

    public EnchantSpellCondition(Boolean weapon) {

    }

    @Override
    public boolean check(Ref ref) {
        // TODO I could of course leave for freedom to the player
        // - just preCheck the *targeting mode* and if it's fitting, let him choose
        // the spell
        if (ref.getMatchObj() instanceof Spell) {
            for (TARGETING_MODE t : modes) {
                if (t == ((Spell) ref.getMatchObj()).getTargetingMode()) {
                    return true;
                }
            }
        }

        return false;
    }

    public enum SPELL_ENCHANT_GROUP {
        ANY, DEBUFF, BUFF, DAMAGE,

    }

}
