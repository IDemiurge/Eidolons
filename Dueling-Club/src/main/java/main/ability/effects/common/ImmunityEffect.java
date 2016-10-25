package main.ability.effects.common;

import main.ability.effects.oneshot.common.ModifyPropertyEffect;
import main.content.CONTENT_CONSTS.IMMUNITIES;
import main.content.properties.G_PROPS;

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
