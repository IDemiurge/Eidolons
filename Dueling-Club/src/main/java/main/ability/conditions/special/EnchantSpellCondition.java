package main.ability.conditions.special;

import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS.TARGETING_MODE;
import main.elements.conditions.MicroCondition;
import main.entity.obj.DC_SpellObj;

public class EnchantSpellCondition extends MicroCondition {

    private TARGETING_MODE[] modes = {TARGETING_MODE.ANY_ALLY,
            TARGETING_MODE.ANY_ENEMY, TARGETING_MODE.ANY_UNIT,
            TARGETING_MODE.SINGLE,};

    public EnchantSpellCondition(SPELL_ENCHANT_GROUP type) {

    }

    public EnchantSpellCondition(SPECIAL_EFFECTS_CASE type) {

    }

    public EnchantSpellCondition(Boolean weapon) {

    }

    @Override
    public boolean check() {
        // TODO I could of course leave for freedom to the player
        // - just check the *targeting mode* and if it's fitting, let him choose
        // the spell
        if (ref.getMatchObj() instanceof DC_SpellObj)
            for (TARGETING_MODE t : modes)
                if (t == ((DC_SpellObj) ref.getMatchObj()).getTargetingMode())
                    return true;

        return false;
    }

    public enum SPELL_ENCHANT_GROUP {
        ANY, DEBUFF, BUFF, DAMAGE,

    }

}
