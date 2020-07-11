package eidolons.game.battlecraft.ai.tools.target;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.containers.customtarget.ShapeEffect;
import eidolons.ability.effects.containers.customtarget.WaveEffect;
import eidolons.ability.effects.continuous.BehaviorModeEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.ability.effects.oneshot.mechanic.DrainEffect;
import eidolons.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import eidolons.ability.effects.oneshot.move.MoveEffect;
import eidolons.ability.effects.oneshot.unit.CreateObjectEffect;
import eidolons.ability.effects.oneshot.unit.RaiseEffect;
import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.ability.targeting.TemplateAutoTargeting;
import eidolons.ability.targeting.TemplateSelectiveTargeting;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.game.core.master.EffectMaster;
import main.ability.Abilities;
import main.ability.effects.Effect;
import main.ability.effects.common.OwnershipChangeEffect;
import main.ability.effects.container.SpecialTargetingEffect;
import main.ability.effects.oneshot.InstantDeathEffect;
import main.content.C_OBJ_TYPE;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.CounterMaster;

import java.util.List;

public class AI_SpellMaster {
    public static GOAL_TYPE getGoal(DC_ActiveObj spell) {
        AI_LOGIC spellLogic = getSpellLogic(spell);
        spell.setSpellLogic(spellLogic);
        if (spellLogic == null) {
            LogMaster.log(1, "*** No spell logic for "
             + spell.getName());
            return AiEnums.GOAL_TYPE.OTHER;
        }
        switch (spellLogic) {
            case MOVE:
                return AiEnums.GOAL_TYPE.MOVE;
            case BUFF_NEGATIVE:
                return AiEnums.GOAL_TYPE.DEBUFF;
            case BUFF_POSITIVE:
                return AiEnums.GOAL_TYPE.BUFF;

            case SELF:
                return AiEnums.GOAL_TYPE.SELF;
            case DEBILITATE:
                return AiEnums.GOAL_TYPE.DEBILITATE;
            case RESTORE:
                return AiEnums.GOAL_TYPE.RESTORE;

            case DAMAGE:
                return AiEnums.GOAL_TYPE.ATTACK;
            case DAMAGE_ZONE:
                return AiEnums.GOAL_TYPE.ZONE_DAMAGE;
            case SUMMON:
                return AiEnums.GOAL_TYPE.SUMMONING;
            case AUTO_DAMAGE:
                return AiEnums.GOAL_TYPE.AUTO_DAMAGE;
            case CUSTOM_HOSTILE:
                return AiEnums.GOAL_TYPE.CUSTOM_HOSTILE;
            case CUSTOM_SUPPORT:
                return AiEnums.GOAL_TYPE.CUSTOM_SUPPORT;
            case COATING:
                return AiEnums.GOAL_TYPE.COATING;
            // ++ teleport -> move
            // ++ vision -> search
            // ++ special spells

            default:
                break;
        }
        return AiEnums.GOAL_TYPE.OTHER;
    }

    public static AI_LOGIC getSpellLogic(DC_ActiveObj spell) {
        AI_LOGIC logic = spell.getAiLogic();
        // multiple? TODO

        // if (logic != null)
        // return logic;
        if (spell.isThrow()) {
            return AiEnums.AI_LOGIC.DAMAGE;
        }
        if (spell instanceof DC_QuickItemAction) {
            DC_QuickItemAction itemActiveObj = (DC_QuickItemAction) spell;
            if (itemActiveObj.getItem().isAmmo()) {
                return AiEnums.AI_LOGIC.OTHER;
            }
            if (itemActiveObj.getItem().getProperty(G_PROPS.ITEM_GROUP)
             .equalsIgnoreCase(ItemEnums.ITEM_GROUP.COATING.toString())) {
                return AiEnums.AI_LOGIC.COATING;
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
                LogMaster.log(1, spell.getName()
                 + " logic from targeting failed!");
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (logic == null) {
            // String actives = spell.getProperty(G_PROPS.ACTIVES);

            Abilities actives = spell.getAbilities();

            try {
                logic = getZoneLogic(spell);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                LogMaster.log(1, spell.getName()
                 + " no Z Logic");
            }
            if (logic != null) {
                return logic;
            }

            if (EffectMaster.check(actives, DealDamageEffect.class)) {
                return AiEnums.AI_LOGIC.DAMAGE;
            }
            try {
                logic = getBuffLogic(spell);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                LogMaster.log(1, spell.getName()
                 + " no Buff Logic");
            }
            if (logic != null) {
                return logic;
            }
            try {
                logic = getModValueLogic(spell);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                LogMaster.log(1, spell.getName()
                 + " no Mod Logic");
            }
            if (logic != null) {
                return logic;
            }
            if (EffectMaster.check(actives, MoveEffect.class)) {
                return AiEnums.AI_LOGIC.MOVE;
            }// if (EffectMaster.preCheck(actives, MoveEffect.class)) {// return
            // AI_LOGIC.CUSTOM;// }
            if (EffectMaster.check(actives, SummonEffect.class)) {
                return AiEnums.AI_LOGIC.SUMMON;
            }
            if (EffectMaster.check(actives, CreateObjectEffect.class)) {
                return AiEnums.AI_LOGIC.SUMMON;
            }

            if (EffectMaster.check(actives, InstantDeathEffect.class)) {
                return AiEnums.AI_LOGIC.CUSTOM_HOSTILE;
            }
            if (EffectMaster.check(actives, BehaviorModeEffect.class)) {
                return AiEnums.AI_LOGIC.CUSTOM_HOSTILE;
            }
            if (EffectMaster.check(actives, OwnershipChangeEffect.class)) {
                return AiEnums.AI_LOGIC.CUSTOM_HOSTILE;
            }
            // ++ Resurrect, Status (sleep?)
        }

        // preCheck tags

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
                    return AiEnums.AI_LOGIC.RESTORE;
                }
                return AiEnums.AI_LOGIC.DEBILITATE;
            }

            if (effect instanceof ModifyValueEffect) {
                if (isModifyValueEffectPositive(spell, effect)) {
                    return AiEnums.AI_LOGIC.RESTORE;
                }
                return AiEnums.AI_LOGIC.DEBILITATE;
            }
        }
        return null;
    }

    private static AI_LOGIC getBuffLogic(DC_ActiveObj spell) {
        Abilities actives = spell.getAbilities();
        if (EffectMaster.check(actives, AddBuffEffect.class)) {
            if (((AddBuffEffect) EffectMaster.getEffectsOfClass(actives,
             AddBuffEffect.class).get(0)).getEffect().getFormula()
             .getInt(spell.getOwnerUnit().getRef()) > 0) {
                return AiEnums.AI_LOGIC.BUFF_POSITIVE;
            } else {
                return AiEnums.AI_LOGIC.BUFF_NEGATIVE;
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
                        return AiEnums.AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (zoneEffect instanceof ShapeEffect) {
                        return AiEnums.AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (spell.getTargetingMode() == AbilityEnums.TARGETING_MODE.NOVA) {
                        return AiEnums.AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (spell.getTargetingMode() == AbilityEnums.TARGETING_MODE.SPRAY) {
                        return AiEnums.AI_LOGIC.AUTO_DAMAGE;
                    }
                    if (spell.getTargetingMode() == AbilityEnums.TARGETING_MODE.WAVE) {
                        return AiEnums.AI_LOGIC.AUTO_DAMAGE;
                    }

                    return AiEnums.AI_LOGIC.DAMAGE_ZONE;
                }
                if (EffectMaster.check(zoneEffect.getEffect(),
                 ModifyValueEffect.class)) {
                    if (isModifyValueEffectPositive(spell, zoneEffect)) {
                        return AiEnums.AI_LOGIC.RESTORE_ZONE;
                    } else {
                        return AiEnums.AI_LOGIC.DEBILITATE_ZONE;
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
                        return AiEnums.AI_LOGIC.RESTORE_ZONE;
                    } else {
                        return AiEnums.AI_LOGIC.DEBILITATE_ZONE;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isModifyValueEffectPositive(DC_ActiveObj spell,
                                                       Effect effect) {
        try {
            return effect.getFormula().getInt(spell.getOwnerUnit().getRef()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isCounterEffectPositive(DC_ActiveObj spell,
                                                   ModifyCounterEffect counterEffect) {
        boolean positive = CounterMaster.isCounterPositive(counterEffect
         .getCounterName());

        if (counterEffect.getFormula().getInt(spell.getOwnerUnit().getRef()) < 0) {
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
                    return AiEnums.AI_LOGIC.BUFF_NEGATIVE;
                case MY_WEAPON:
                case MY_ARMOR:
                case SELF:
                    return AiEnums.AI_LOGIC.SELF;
                case ANY_ALLY:
                    if (EffectMaster.check(actives, AddBuffEffect.class)) {
                        return AiEnums.AI_LOGIC.BUFF_POSITIVE;
                    }
                    if (EffectMaster.check(actives, ModifyValueEffect.class)) {
                        return AiEnums.AI_LOGIC.RESTORE;
                    }
                case ANY_ENEMY:
                    if (EffectMaster.check(actives, DealDamageEffect.class)) {
                        return AiEnums.AI_LOGIC.DAMAGE;
                    }
                    if (EffectMaster.check(actives, AddBuffEffect.class)) {
                        return AiEnums.AI_LOGIC.BUFF_NEGATIVE;
                    }
                    if (EffectMaster.check(actives, ModifyValueEffect.class)) {
                        return AiEnums.AI_LOGIC.DEBILITATE;
                    }
                    if (EffectMaster.check(actives, DrainEffect.class)) {
                        return AiEnums.AI_LOGIC.DEBILITATE;
                    }
                    break;
                case ANY_UNIT:

                    if (EffectMaster.check(actives, AddBuffEffect.class)) {
                        if (((AddBuffEffect) EffectMaster.getEffectsOfClass(
                         actives, AddBuffEffect.class).get(0))
                         .getEffect().getFormula()
                         .getInt(spell.getOwnerUnit().getRef()) > 0) {
                            return AiEnums.AI_LOGIC.BUFF_POSITIVE;
                        } else {
                            return AiEnums.AI_LOGIC.BUFF_NEGATIVE;
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
                            return AiEnums.AI_LOGIC.RESTORE;
                        }
                        return AiEnums.AI_LOGIC.DEBILITATE;
                    }

                    if (effect instanceof ModifyValueEffect) {
                        if (isModifyValueEffectPositive(spell, effect)) {
                            return AiEnums.AI_LOGIC.RESTORE;
                        }
                        return AiEnums.AI_LOGIC.DEBILITATE;

                    }

                    break;
                default:
                    break;

            }
        }

        return null;
    }

    public static ObjType getSummonedUnit(DC_ActiveObj active, Ref ref) {
        ObjType type;

        SummonEffect effect = null;
        try {
            effect = (SummonEffect) EffectMaster.getEffectsOfClass(
             active.getAbilities(), SummonEffect.class).get(0);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
