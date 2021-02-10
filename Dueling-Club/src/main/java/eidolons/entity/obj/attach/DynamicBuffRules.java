package eidolons.entity.obj.attach;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.main.NF_MetaMaster;
import main.ability.AbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.enums.GenericEnums;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.images.ImageManager;

import java.util.ArrayList;

public class DynamicBuffRules {
    DC_Game game;

    public DynamicBuffRules(DC_Game game) {
        this.game = game;
    }

    public void checkBuffs(Unit unit) {
//on top of buffRules?
        for (BuffObj buff : new ArrayList<>(unit.getBuffs())) {
            if (buff.isDynamic()) {
                buff.remove();
            }
        }
        if (unit.isShadow()) {
            if (game.getMetaMaster() instanceof NF_MetaMaster) {
                //TODO localize
                addDynamicBuff("Eidolon Shadow", unit, "", "This is what I am now without the Arts");
            }
        }


        if (unit.checkDualWielding()) {
//            new AddBuffEffect(type, fx, true);
            addDynamicBuff("Dual Wielding", unit, "(" +
                    (unit.getIntParam(PARAMS.DUAL_WIELDING_MASTERY) * 100 / 20) +
                    "% mastery) - use Radial for Dual Attacks");

        }

        for (AbilityObj passive : unit.getPassives()) {
            checkAddPassiveBuff(passive, unit);

        }
        for (Effect.SPECIAL_EFFECTS_CASE effectsCase : unit.getSpecialEffects().keySet()) {

            Effect effects = unit.getSpecialEffects().get(effectsCase);
            if (effects != null) {
                addSpecialEffectsBuff(effectsCase, effects, unit);
            }
        }
    }

    private void addSpecialEffectsBuff(Effect.SPECIAL_EFFECTS_CASE effectsCase, Effect effects, Unit unit) {
        String descr = "";
        String name = effectsCase.getName() + " effects: ";
        if (effects instanceof Effects) {
            for (Effect effect : ((Effects) effects)) {
                if (effect.getRef() == null) {
                    effect.setRef(new Ref()); //TODO
                }
                try {
                    descr += effect.getTooltip() + Strings.NEW_LINE;
                } catch (Exception e) {
//                    main.system.ExceptionMaster.printStackTrace(e);
                }

            }
        }
        BuffObj buff = addDynamicBuff(name, unit, "", descr);
        String image = getFxCaseImg(effectsCase);
        buff.setImage(image);
    }

    private String getFxCaseImg(Effect.SPECIAL_EFFECTS_CASE effectsCase) {
        switch (effectsCase) {
            case BEFORE_ATTACK:
            case ON_ATTACK:
                return ImageManager.getValueIconPath(PARAMS.DAMAGE);
            case ON_HIT:
            case BEFORE_HIT:
                return ImageManager.getValueIconPath(PARAMS.TOUGHNESS);
            case ON_KILL:
                return UnitEnums.COUNTER.Poison.getImagePath();
            case ON_DEATH:
                return ImageManager.getDamageTypeImagePath(GenericEnums.DAMAGE_TYPE.DEATH.getName(), true);

            case ON_CRIT:
            case ON_CRIT_HIT:
            case ON_CRIT_SELF:
            case ON_CRIT_HIT_SELF:
                return ImageManager.getDamageTypeImagePath(GenericEnums.DAMAGE_TYPE.PIERCING.getName(), true);
            case ON_DODGE:
            case ON_DODGE_SELF:
                return ImageManager.getValueIconPath(PARAMS.DEFENSE);
            case ON_SHIELD_BLOCK_SELF:
            case ON_SHIELD_BLOCK:
                return ImageManager.getMasteryGroupPath(SkillEnums.SKILL_GROUP.DEFENSE.toString());
            case ON_PARRY:
            case ON_PARRY_SELF:
                return ImageManager.getValueIconPath(PARAMS.EXTRA_ATTACKS);
            case ON_SNEAK_ATTACK:
            case ON_SNEAK_ATTACK_SELF:
            case ON_SNEAK_HIT_SELF:
            case ON_SNEAK_HIT:
            case ON_SNEAK_CRIT_SELF:
            case ON_SNEAK_CRIT:
                return ImageManager.getValueIconPath(PARAMS.STEALTH);
            case SPELL_IMPACT:
                return ImageManager.getDamageTypeImagePath(GenericEnums.DAMAGE_TYPE.ARCANE.getName(), true);
            case SPELL_HIT:
                return ImageManager.getDamageTypeImagePath(GenericEnums.DAMAGE_TYPE.MAGICAL.getName(), true);
            case SPELL_RESISTED:
            case SPELL_RESIST:
                return ImageManager.getValueIconPath(PARAMS.RESISTANCE);
            case MOVE:
                return ImageManager.getValueIconPath(PARAMS.MOBILITY_MASTERY);
            case NEW_TURN:
            case END_TURN:
                return ImageManager.getValueIconPath(PARAMS.INITIATIVE);
        }
        return
                "ui/content/value icons/effect cases" + VariableManager.removeVarPart(effectsCase.getName());
    }

    private void checkAddPassiveBuff(AbilityObj passive, Unit unit) {
//        passive.getGroupingKey()
        /**
         *
         */
    }

    private BuffObj addDynamicBuff(String name, Unit unit, String suffx) {
        return addDynamicBuff(name, unit, suffx, null);
    }

    public BuffObj addDynamicBuff(String name, Unit unit, String variableSuffix, String description) {

        BuffObj buff = new DC_BuffObj(name, unit, 0);
        if (description != null)
            buff.setDescription(description);
        if (!StringMaster.isEmpty(variableSuffix)) {
            buff.setName(name + " " + variableSuffix);
            String descr = buff.getDescription();
            descr = VariableManager.substitute(descr, variableSuffix);
            buff.setProperty(G_PROPS.DESCRIPTION, descr);
        }
        buff.setTransient(true);
        buff.setDynamic(true);
        unit.getGame().getManager().buffCreated(buff, unit);
//        unit.addBuff(buff);

        return buff;
    }

}
