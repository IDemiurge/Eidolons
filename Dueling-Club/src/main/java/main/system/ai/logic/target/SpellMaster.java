package main.system.ai.logic.target;

import main.ability.Abilities;
import main.ability.effects.AddBuffEffect;
import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.ability.effects.MoveEffect;
import main.ability.effects.common.CreateObjectEffect;
import main.ability.effects.common.RaiseEffect;
import main.ability.effects.common.SummonEffect;
import main.ability.effects.containers.customtarget.ShapeEffect;
import main.ability.effects.containers.customtarget.WaveEffect;
import main.ability.effects.oneshot.common.ModifyCounterEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.effects.oneshot.common.OwnershipChangeEffect;
import main.ability.effects.oneshot.common.SpecialTargetingEffect;
import main.ability.effects.oneshot.special.InstantDeathEffect;
import main.ability.effects.special.BehaviorModeEffect;
import main.ability.effects.special.DrainEffect;
import main.ability.effects.special.GatewayEffect;
import main.ability.targeting.TemplateAutoTargeting;
import main.ability.targeting.TemplateSelectiveTargeting;
import main.content.CONTENT_CONSTS.AI_LOGIC;
import main.content.CONTENT_CONSTS.ITEM_GROUP;
import main.content.CONTENT_CONSTS.TARGETING_MODE;
import main.content.C_OBJ_TYPE;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.active.DC_ItemActiveObj;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;
import main.system.util.CounterMaster;

import java.util.List;

public class SpellMaster {
    public static GOAL_TYPE getGoal(DC_ActiveObj spell) {
        AI_LOGIC spellLogic = getSpellLogic(spell);
        spell.setSpellLogic(spellLogic);
        if (spellLogic == null) {
            main.system.auxiliary.LogMaster.log(1, "*** No spell logic for "
                    + spell.getName());
            return GOAL_TYPE.OTHER;
        }
        switch (spellLogic) {
            case MOVE:
                return GOAL_TYPE.MOVE;
            case BUFF_NEGATIVE:
                return GOAL_TYPE.DEBUFF;
            case BUFF_POSITIVE:
                return GOAL_TYPE.BUFF;

            case SELF:
                return GOAL_TYPE.SELF;
            case DEBILITATE:
                return GOAL_TYPE.DEBILITATE;
            case RESTORE:
                return GOAL_TYPE.RESTORE;

            case DAMAGE:
                return GOAL_TYPE.ATTACK;
            case DAMAGE_ZONE:
                return GOAL_TYPE.ZONE_DAMAGE;
            case SUMMON:
                return GOAL_TYPE.SUMMONING;
            case AUTO_DAMAGE:
                return GOAL_TYPE.AUTO_DAMAGE;
            case CUSTOM_HOSTILE:
                return GOAL_TYPE.CUSTOM_HOSTILE;
            case CUSTOM_SUPPORT:
                return GOAL_TYPE.CUSTOM_SUPPORT;
            case COATING:
                return GOAL_TYPE.COATING;
            // ++ teleport -> move
            // ++ vision -> search
            // ++ special spells

            default:
                break;
        }
        return GOAL_TYPE.OTHER;
    }

    public static AI_LOGIC getSpellLogic(DC_ActiveObj spell) {
        AI_LOGIC logic = spell.getAiLogic();
        // multiple? TODO

        // if (logic != null)
        // return logic;
        if (spell.isThrow()) {
            return AI_LOGIC.DAMAGE;
        }
        if (spell instanceof DC_ItemActiveObj) {
            DC_ItemActiveObj itemActiveObj = (DC_ItemActiveObj) spell;
            if (itemActiveObj.getItem().isAmmo()) {
                return AI_LOGIC.OTHER;
            }
            if (itemActiveObj.getItem().getProperty(G_PROPS.ITEM_GROUP)
                    .equalsIgnoreCase(ITEM_GROUP.COATING.toString())) {
                return AI_LOGIC.COATING;
            }
            /*
			 * self for potions essentially, targeting mode should be available
			 * here as well!
			 */
        }

        if (logic == null) {
            if (!spell.isConstructed()) {
                spell.construct();
            }
            try {
                logic = getLogicByTargeting(spell);
            } catch (Exception e) {
                main.system.auxiliary.LogMaster.log(1, spell.getName()
                        + " logic from targeting failed!");
                e.printStackTrace();
            }
        }
        if (logic == null) {
            // String actives = spell.getProperty(G_PROPS.ACTIVES);

            Abilities actives = spell.getAbilities();

            try {
                logic = getZoneLogic(spell);
            } catch (Exception e) {
                e.printStackTrace();
                main.system.auxiliary.LogMaster.log(1, spell.getName()
                        + " no Z Logic");
            }
            if (logic != null) {
                return logic;
            }
            try {
                logic = getBuffLogic(spell);
            } catch (Exception e) {
                e.printStackTrace();
                main.system.auxiliary.LogMaster.log(1, spell.getName()
                        + " no Buff Logic");
            }
            if (logic != null) {
                return logic;
            }
            try {
                logic = getModValueLogic(spell);
            } catch (Exception e) {
                e.printStackTrace();
                main.system.auxiliary.LogMaster.log(1, spell.getName()
                        + " no Mod Logic");
            }
            if (logic != null) {
                return logic;
            }
            if (EffectMaster.check(actives, MoveEffect.class)) {
                return AI_LOGIC.MOVE;
            }// if (EffectMaster.check(actives, MoveEffect.class)) {// return
            // AI_LOGIC.CUSTOM;// }
            if (EffectMaster.check(actives, SummonEffect.class)) {
                return AI_LOGIC.SUMMON;
            }
            if (EffectMaster.check(actives, GatewayEffect.class)) {
                return AI_LOGIC.SUMMON;
            }
            if (EffectMaster.check(actives, CreateObjectEffect.class)) {
                return AI_LOGIC.SUMMON;
            }
            if (EffectMaster.check(actives, DealDamageEffect.class)) {
                return AI_LOGIC.DAMAGE;
            }

            if (EffectMaster.check(actives, InstantDeathEffect.class)) {
                return AI_LOGIC.CUSTOM_HOSTILE;
            }
            if (EffectMaster.check(actives, BehaviorModeEffect.class)) {
                return AI_LOGIC.CUSTOM_HOSTILE;
            }
            if (EffectMaster.check(actives, OwnershipChangeEffect.class)) {
                return AI_LOGIC.CUSTOM_HOSTILE;
            }
            // ++ Resurrect, Status (sleep?)
        }

        // check tags

        return logic;
    }

    private static AI_LOGIC getModValueLogic(DC_ActiveObj spell) {
        Abilities actives = spell.getAbilities();
        List<Effect> effects = EffectMaster.getEffectsOfClass(actives,
                ModifyValueEffect.class);
        if (effects.isEmpty()) {
            effects = EffectMaster.getEffectsOfClass(actives,
                    ModifyCounterEffect.class);
        }
        if (!effects.isEmpty()) {
            Effect effect = effects.get(0);

            if (effect instanceof ModifyCounterEffect) {
                ModifyCounterEffect counterEffect = (ModifyCounterEffect) effect;
                if (isCounterEffectPositive(spell, counterEffect)) {
                    return AI_LOGIC.RESTORE;
                }
                return AI_LOGIC.DEBILITATE;
            }

            if (effect instanceof ModifyValueEffect) {
                if (isModifyValueEffectPositive(spell, effect)) {
                    return AI_LOGIC.RESTORE;
                }
                return AI_LOGIC.DEBILITATE;
            }
        }
        return null;
    }

    private static AI_LOGIC getBuffLogic(DC_ActiveObj spell) {
        Abilities actives = spell.getAbilities();
        if (EffectMaster.check(actives, AddBuffEffect.class)) {
            if (((AddBuffEffect) EffectMaster.getEffectsOfClass(actives,
                    AddBuffEffect.class).get(0)).getEffect().getFormula()
                    .getInt(spell.getOwnerObj().getRef()) > 0) {
                return AI_LOGIC.BUFF_POSITIVE;
            } else {
                return AI_LOGIC.BUFF_NEGATIVE;
            }
        }
        return null;
    }

    private static AI_LOGIC getZoneLogic(DC_ActiveObj spell) {
        List<Effect> zoneEffects = EffectMaster.getEffectsOfClass(spell,
                SpecialTargetingEffect.class);
        if (!zoneEffects.isEmpty()) {
            for (Effect effect : zoneEffects) {
                SpecialTargetingEffect zoneEffect = (SpecialTargetingEffect) effect;
                if (EffectMaster.check(zoneEffect.getEffect(),
                        DealDamageEffect.class)) {
                    if (zoneEffect instanceof WaveEffect) {
                        return AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (zoneEffect instanceof ShapeEffect) {
                        return AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (spell.getTargetingMode() == TARGETING_MODE.NOVA) {
                        return AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (spell.getTargetingMode() == TARGETING_MODE.SPRAY) {
                        return AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (spell.getTargetingMode() == TARGETING_MODE.WAVE) {
                        return AI_LOGIC.AUTO_DAMAGE;
                    }

                    return AI_LOGIC.DAMAGE_ZONE;
                }
                if (EffectMaster.check(zoneEffect.getEffect(),
                        ModifyValueEffect.class)) {
                    if (isModifyValueEffectPositive(spell, zoneEffect)) {
                        return AI_LOGIC.RESTORE_ZONE;
                    } else {
                        return AI_LOGIC.DEBILITATE_ZONE;
                    }
                }

                // Effect e = EffectMaster.getEffectsOfClass(
                // zoneEffect.getEffect(), ModifyCounterEffect.class)
                // .getOrCreate(0);

                if (EffectMaster.check(zoneEffect.getEffect(),
                        ModifyCounterEffect.class)) {
                    Effect e = EffectMaster.getEffectsOfClass(
                            zoneEffect.getEffect(), ModifyCounterEffect.class)
                            .get(0);
                    ModifyCounterEffect counterEffect = (ModifyCounterEffect) e;
                    if (isCounterEffectPositive(spell, counterEffect)) {
                        return AI_LOGIC.RESTORE_ZONE;
                    } else {
                        return AI_LOGIC.DEBILITATE_ZONE;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isModifyValueEffectPositive(DC_ActiveObj spell,
                                                       Effect effect) {
        try {
            return effect.getFormula().getInt(spell.getOwnerObj().getRef()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isCounterEffectPositive(DC_ActiveObj spell,
                                                   ModifyCounterEffect counterEffect) {
        boolean positive = CounterMaster.isCounterPositive(counterEffect
                .getCounterName());

        if (counterEffect.getFormula().getInt(spell.getOwnerObj().getRef()) < 0) {
            positive = !positive;
        }
        return positive;
    }

    private static AI_LOGIC getLogicByTargeting(DC_ActiveObj spell) {
        Targeting t = spell.getTargeting();
        TARGETING_MODE mode = spell.getTargetingMode();
        if (mode == null) {
            if (t instanceof TemplateSelectiveTargeting) {
                mode = ((TemplateSelectiveTargeting) t).getTemplate().getMode();
            }
            if (t instanceof TemplateAutoTargeting) {
                mode = ((TemplateAutoTargeting) t).getTemplate().getMode();
            }
        }
        Abilities actives = spell.getAbilities();
        if (mode != null) {

            switch (mode) {

                case ENEMY_WEAPON:
                case ENEMY_ARMOR:
                    return AI_LOGIC.BUFF_NEGATIVE;
                case MY_WEAPON:
                case MY_ARMOR:
                case SELF:
                    return AI_LOGIC.SELF;
                case ANY_ALLY:
                    if (EffectMaster.check(actives, AddBuffEffect.class)) {
                        return AI_LOGIC.BUFF_POSITIVE;
                    }
                    if (EffectMaster.check(actives, ModifyValueEffect.class)) {
                        return AI_LOGIC.RESTORE;
                    }
                case ANY_ENEMY:
                    if (EffectMaster.check(actives, DealDamageEffect.class)) {
                        return AI_LOGIC.DAMAGE;
                    }
                    if (EffectMaster.check(actives, AddBuffEffect.class)) {
                        return AI_LOGIC.BUFF_NEGATIVE;
                    }
                    if (EffectMaster.check(actives, ModifyValueEffect.class)) {
                        return AI_LOGIC.DEBILITATE;
                    }
                    if (EffectMaster.check(actives, DrainEffect.class)) {
                        return AI_LOGIC.DEBILITATE;
                    }
                    break;
                case ANY_UNIT:

                    if (EffectMaster.check(actives, AddBuffEffect.class)) {
                        if (((AddBuffEffect) EffectMaster.getEffectsOfClass(
                                actives, AddBuffEffect.class).get(0))
                                .getEffect().getFormula()
                                .getInt(spell.getOwnerObj().getRef()) > 0) {
                            return AI_LOGIC.BUFF_POSITIVE;
                        } else {
                            return AI_LOGIC.BUFF_NEGATIVE;
                        }
                    }
                    List<Effect> effects = EffectMaster.getEffectsOfClass(
                            actives, ModifyValueEffect.class);
                    if (effects.isEmpty()) {
                        effects = EffectMaster.getEffectsOfClass(actives,
                                ModifyCounterEffect.class);
                    }
                    Effect effect = effects.get(0);

                    if (effect instanceof ModifyCounterEffect) {
                        ModifyCounterEffect counterEffect = (ModifyCounterEffect) effect;
                        boolean positive = isCounterEffectPositive(spell,
                                counterEffect);

                        if (positive) {
                            return AI_LOGIC.RESTORE;
                        }
                        return AI_LOGIC.DEBILITATE;
                    }

                    if (effect instanceof ModifyValueEffect) {
                        if (isModifyValueEffectPositive(spell, effect)) {
                            return AI_LOGIC.RESTORE;
                        }
                        return AI_LOGIC.DEBILITATE;

                    }

                    break;
                case BLAST:
                    break;
                case CELL:
                    break;
                case RAY:
                    break;
                default:
                    break;

            }
        }

        return null;
    }

    public static ObjType getSummonedUnit(DC_ActiveObj active, Ref ref) {
        ObjType type = null;
        List<Effect> list = EffectMaster.getEffectsOfClass(
                active.getAbilities(), GatewayEffect.class);
        if (!list.isEmpty()) {
            GatewayEffect effect = (GatewayEffect) list.get(0);
            return DataManager.getType(effect.getUnitType(), C_OBJ_TYPE.BF_OBJ);
        }

        SummonEffect effect = null;
        try {
            effect = (SummonEffect) EffectMaster.getEffectsOfClass(
                    active.getAbilities(), SummonEffect.class).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (effect instanceof RaiseEffect) {
            RaiseEffect raiseEffect = (RaiseEffect) effect;
            effect.setRef(ref);
            return DataManager.getType(raiseEffect.getUnitType(),
                    C_OBJ_TYPE.BF_OBJ);
        }

        if (effect != null) {
            type = DataManager.getType(effect.getTypeName(), C_OBJ_TYPE.BF_OBJ);
            return type;
        }

        if (effect instanceof RaiseEffect) {
            RaiseEffect raiseEffect = (RaiseEffect) effect;
            return DataManager.getType(raiseEffect.getUnitType(),
                    C_OBJ_TYPE.BF_OBJ);

        }

        if (effect instanceof RaiseEffect) {
            RaiseEffect raiseEffect = (RaiseEffect) effect;
            return DataManager.getType(raiseEffect.getUnitType(),
                    C_OBJ_TYPE.BF_OBJ);

        }
        return null;
    }

}
