package main.ability.effects.common;

import main.ability.AddSpecialEffects;
import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effects;
import main.ability.effects.containers.EnergyCostEffect;
import main.ability.effects.media.AnimationEffect;
import main.ability.effects.media.SoundEffect;
import main.ability.effects.oneshot.MicroEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.targeting.TemplateSelectiveTargeting;
import main.client.cc.logic.items.Enchanter;
import main.content.PARAMS;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_SpellObj;
import main.entity.type.BuffType;
import main.game.ai.tools.target.EffectFinder;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster.SOUNDS;

public class EnchantItemEffect extends MicroEffect {

    private static final String buffName = "Enchantment";
    private SPECIAL_EFFECTS_CASE case_type;
    private boolean weapon;
    private String energy;
    private Boolean selectSpell;
    private DC_SpellObj spell;

    // SUPPORT CUSTOM TARGETING - E.G. heal on self upon being attacked!

    public EnchantItemEffect(String energy, Boolean weapon, DC_SpellObj spell) {
        this(energy, weapon);
        this.spell = spell;
    }

    public EnchantItemEffect(String energy, SPECIAL_EFFECTS_CASE type,
                             Boolean weapon) {
        this.case_type = type;
        this.weapon = weapon;
        this.energy = energy;
    }

    public EnchantItemEffect(String energy, Boolean weapon, Boolean selectSpell) {
        this.energy = energy;
        this.weapon = weapon;
        this.selectSpell = selectSpell;

    }

    public EnchantItemEffect(String energy, Boolean weapon) {
        this.energy = energy;
        this.weapon = weapon;
        selectSpell = true;
    }

    @Override
    public boolean applyThis() {
        // TODO select spell to use!
        // perhaps it is better to invoke spell with a special effect!
        // Ref REF = ref.getCopy();
        // this should be an effect - for other things too like traps
        // add spell filter!!!

        // if (selectSpell)

        // game.getManager().infoSelect(ref.getSourceObj());

        spell = (DC_SpellObj) ref.getTargetObj();

        Effects effects = EffectFinder.getEffectsFromSpell(spell);
        effects.add(new AnimationEffect(spell));
        effects.add(new SoundEffect(SOUNDS.IMPACT, ref.getTargetObj()));
        // TODO fail sound on energy lapse?
        // ++ energy bar for items!
        // String passive = "";
        // TODO why was only that 1st spell in SB filtered in???
        if (!new TemplateSelectiveTargeting(
                (weapon) ? SELECTIVE_TARGETING_TEMPLATES.MY_WEAPON
                        : SELECTIVE_TARGETING_TEMPLATES.MY_ARMOR).select(ref)) {
            return false;
        }
        // new ModifyPropertyEffect(G_PROPS.PASSIVES, MOD_PROP_TYPE.ADD,
        // passive)
        // .apply(ref);
        //
        KEYS key;
        if (!weapon) {
            key = KEYS.ARMOR;
        } else {
            if (ref.getTargetObj() == ref.getObj(KEYS.WEAPON)) {
                key = KEYS.WEAPON;
            } else {
                key = KEYS.OFFHAND;
            }
        }
        int cost = Enchanter.calculateSpellEnergyCost(spell);

        if (case_type == null) {
            case_type = (weapon) ? SPECIAL_EFFECTS_CASE.ON_ATTACK
                    : SPECIAL_EFFECTS_CASE.ON_HIT;
        }
        // another layer of customTargetEffect if ON SELF or so ! Some may even
        // be Zone-targeted!
        BuffType t = new BuffType(new Ref(ref.getGame(), ref.getSource()));
        t.setImage(ref.getActive().getProperty(G_PROPS.IMAGE, false));
        t.setName(buffName + " "
                + StringMaster.wrapInParenthesis(spell.getName()));
        t.setParam(G_PARAMS.DURATION,
                ref.getActive().getIntParam(G_PARAMS.DURATION, false));
        new AddBuffEffect(t, new AddSpecialEffects(case_type,
                new EnergyCostEffect(cost, key, effects))).apply(ref);

        ModifyValueEffect addEnergyEffect = new ModifyValueEffect(
                PARAMS.C_ENERGY, MOD.MODIFY_BY_CONST, energy);
        addEnergyEffect.setValueOverMax(true);
        addEnergyEffect.apply(ref);

        // so the weapon can transfer specialEffects which are wrapped in
        // EnergyCostEffect

		/*
         * Ok, don't forget that there will be more to it than that!
		 * 
		 * Energy cost FROM THE WEAPON Add energy TO THE WEAPON
		 */

        return true;
    }
    /*
     * must have a passive to transfer that will add SpecialEffects
	 */

}
