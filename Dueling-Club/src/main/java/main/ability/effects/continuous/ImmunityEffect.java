package main.ability.effects.continuous;

import main.ability.effects.common.ModifyPropertyEffect;
import main.content.enums.entity.UnitEnums.IMMUNITIES;
import main.content.values.properties.G_PROPS;

public class ImmunityEffect extends ModifyPropertyEffect {

    public ImmunityEffect(IMMUNITIES value) {
        this(value.toString());
    }

    public ImmunityEffect(String value) {
        super(G_PROPS.IMMUNITIES, MOD_PROP_TYPE.ADD, value);
    }

	/*
     * or should it be immunity rule?
	 * 
	 * where do I check?
	 * 
	 * Examples: ,Mind Affection, ,Poison, ,Wounds, ,Ensnare,
	 * 
	 * Counter Rules Roll Types Spell Tags (e.g. death magic) Effect types (?) -
	 * instant death, knockdown,
	 */

}
