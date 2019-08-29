package eidolons.ability;

import eidolons.ability.conditions.special.GraveCondition;
import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.containers.customtarget.RayEffect;
import eidolons.ability.effects.containers.customtarget.WaveEffect;
import eidolons.ability.effects.containers.customtarget.ZoneEffect;
import eidolons.ability.effects.oneshot.mechanic.RollEffect;
import eidolons.ability.targeting.CoordinateTargeting;
import eidolons.ability.targeting.TemplateAutoTargeting;
import eidolons.ability.targeting.TemplateSelectiveTargeting;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.system.DC_ConditionMaster;
import main.ability.*;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.AbilityEnums.EFFECTS_WRAP;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.entity.AbilityEnums.TARGETING_MODIFIERS;
import main.content.enums.entity.ActionEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.*;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.Active;
import main.entity.obj.ActiveObj;
import main.game.bf.directions.UNIT_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;

public class ActivesConstructor {

    public static final SELECTIVE_TARGETING_TEMPLATES
     DEFAULT_TARGETING_TEMPLATE = SELECTIVE_TARGETING_TEMPLATES.SHOT;

    public static void wrapInSaveRollEffect(Effects effects, String saveRoll) {
        // TODO
        ArrayList<ROLL_TYPES> rolls = new ArrayList<>();
        ArrayList<String> vars = new ArrayList<>();
        for (String roll : ContainerUtils.open(saveRoll, StringMaster.AND_SEPARATOR)) {
            String varArgs = VariableManager.getVarPart(roll);
            roll = roll.replace(varArgs, "");
            rolls.add(new EnumMaster<ROLL_TYPES>().retrieveEnumConst(ROLL_TYPES.class, roll));
            vars.add(varArgs);
        }

        int i = 0;
        for (Effect e : effects) {
            for (ROLL_TYPES roll : rolls) {
                String args = StringMaster.cropParenthesises(vars.get(i));
                e = wrapInRoll(e, roll, args);
                i++;
            }
        }

    }

    public static Effect wrapInRoll(Effect e, ROLL_TYPES roll, String argString) {
        List<String> args = ContainerUtils.openContainer(argString, ",");
        String success = args.get(0);
        String fail = null;
        if (args.size() > 1) {
            fail = args.get(1);
        }
        return new RollEffect(roll, success, e, fail);

    }


    public static void constructActive(TARGETING_MODE mode, DC_ActiveObj entity) {
        if (mode == AbilityEnums.TARGETING_MODE.MULTI) {
            addMultiTargetingMods(entity);
            return;
        }
        if (entity.checkBool(GenericEnums.STD_BOOLS.MULTI_TARGETING)) {
            return; // constructMultiAbilities(entity);
        }
        if (entity.getActives() == null) {
            return;
        }

        List<ActiveObj> list = new ArrayList<>(entity.getActives());

        Effects effects = new Effects();
        for (Active active : list) {
            for (Ability abil : ((AbilityObj) active).getAbilities().getAbils()) {
                for (Effect effect : abil.getEffects().getEffects()) {
                    effects.add(effect);
                }
            }
        }
        // TODO what if the effects should have different targetings like in
        // damage+light?

        String saveRoll = entity.getProperty(PROPS.ROLL_TYPES_TO_SAVE);
        if (!StringMaster.isEmpty(saveRoll)) {
            wrapInSaveRollEffect(effects, saveRoll);
        }

        String wrap = entity.getProperty(PROPS.EFFECTS_WRAP);
        Effect wrappedEffect;
        if (StringMaster.isEmpty(wrap)) {
            wrappedEffect = wrapEffects(mode, effects, entity);
        } else {
            EFFECTS_WRAP WRAP = new EnumMaster<EFFECTS_WRAP>().retrieveEnumConst(
             EFFECTS_WRAP.class, wrap);
            wrappedEffect = wrapEffects(WRAP, effects, entity);
        }
        Targeting targeting = getTargeting(mode, entity);
        if (targeting == null) {
            try {
                targeting = entity.getActives().get(0).getActives().get(0)
                 .getTargeting();
            } catch (Exception e) {
                // targeting = getDefaultSingleTargeting(entity);TODO necessary?
            }
        }
        if (targeting != null)
            entity.setTargeting(targeting);
        Abilities abilities = new Abilities();
        abilities.add(new ActiveAbility(null, wrappedEffect));
        entity.setAbilities(abilities);

        // TODO wrapping in RollEffect - each single effect or the resulting
        // wrapped Effects?

    }

    public static void addMultiTargetingMods(DC_ActiveObj entity) {
        for (Active active : entity.getActives()) {
            for (Ability abil : ((AbilityObj) active).getAbilities().getAbils()) {
                addTargetingMods(abil.getTargeting(), entity);
            }

        }

    }

    public static Targeting getDefaultSingleTargeting(DC_ActiveObj entity) {
        Conditions conditions = new Conditions(DC_ConditionMaster
         .getSelectiveTargetingTemplateConditions(DEFAULT_TARGETING_TEMPLATE));
        Targeting targeting = new SelectiveTargeting(conditions);
        return targeting;
    }

    public static Targeting getTargeting(TARGETING_MODE mode, DC_ActiveObj obj) {
        Targeting targeting = null;
        switch (mode) {
            case FRONT:
                return new CoordinateTargeting(UNIT_DIRECTION.AHEAD);
            case BF_OBJ:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.BF_OBJ);
                break;
            case CORPSE:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.GRAVE_CELL);
                break;
            case BOLT_ANY:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.SHOT);

                break;
            case BOLT_UNITS:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.SHOT,
                 ConditionMaster.getBFObjTypesCondition());
                break;

            case SELF:
                targeting = getSelfTargeting(obj);
                break;
            case SINGLE:
                targeting = getSingleTargeting(obj);
                break;

            case RAY:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.RAY);
                break;
            case CELL:
                // bolt-targeting?
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.CELL);

                break;
            case BLAST:
                // bolt-targeting?
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.BLAST);
                break;
            case DOUBLE:
                targeting = new MultiTargeting(getSingleTargeting(obj), getSingleTargeting(obj));
                ((MultiTargeting) targeting).setIgnoreGroupTargeting(false);
                break;
            case MULTI:
                // each ability will use its own targeting?

                // return new MultiTargeting(duplicates, array);

            case TRIPPLE:
                break;
            case ALL_UNITS:
                targeting = new TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES.ALL_UNITS);
                break;
            case ANY_UNIT:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ANY_UNIT);
                break;
            case ANY_ENEMY:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ANY_ENEMY);
                break;
            case ANY_ALLY:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ANY_ALLY);
                break;
            case ALL_ALLIES:
                targeting = new TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES.ALL_ALLIES);
                break;

            case ALL_ENEMIES:
                targeting = new TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES.ALL_ENEMIES);
                break;
            case ALL:
                targeting = new TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES.ALL);
                break;
            case RAY_AUTO:
            case NOVA:
                targeting = new FixedTargeting(KEYS.SOURCE);
                break;
            case WAVE:
                targeting = new FixedTargeting(KEYS.SOURCE);
                break;
            case SPRAY:
                targeting = new FixedTargeting(KEYS.SOURCE);
                break;
            case TRAP:
                break;
            case ANY_ARMOR:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ANY_ARMOR);
                break;
            case ANY_ITEM:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ANY_ITEM);
                break;
            case ANY_WEAPON:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ANY_WEAPON);
                break;
            case ENEMY_ARMOR:
                targeting = new TemplateSelectiveTargeting(
                 SELECTIVE_TARGETING_TEMPLATES.ENEMY_ARMOR);
                break;
            case ENEMY_ITEM:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.ENEMY_ITEM);
                break;
            case ENEMY_WEAPON:
                targeting = new TemplateSelectiveTargeting(
                 SELECTIVE_TARGETING_TEMPLATES.ENEMY_WEAPON);
                break;
            case MY_ARMOR:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.MY_ARMOR);
                break;
            case MY_ITEM:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.MY_ITEM);
                break;
            case MY_WEAPON:
                targeting = new TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES.MY_WEAPON);
                break;
            default:
                break;

        }
        if (targeting == null) {
            targeting = getSingleTargeting(obj);
        }
        if (targeting != null) {
            if (!targeting.isModsAdded()) {
                addTargetingMods(targeting, obj);
            }
        }

        return targeting;
    }

    public static Targeting getSelfTargeting(DC_ActiveObj obj) {
        return new FixedTargeting(KEYS.SOURCE);
    }

    public static void addTargetingMods(Targeting targeting, DC_ActiveObj obj) {
        Conditions c = new Conditions();
        String property = obj.getProperty(PROPS.TARGETING_MODIFIERS);
        if (obj.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
            // if (!(obj instanceof DC_SpellObj))
            // SPELL_TAGS.TELEPORT
            if (!obj.checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.FLYING.toString())) {
                if (!obj.checkProperty(PROPS.TARGETING_MODIFIERS, AbilityEnums.TARGETING_MODIFIERS.CLEAR_SHOT
                 .toString())) {
                    property += AbilityEnums.TARGETING_MODIFIERS.CLEAR_SHOT.toString();
                }
                if (!obj.checkProperty(PROPS.TARGETING_MODIFIERS, AbilityEnums.TARGETING_MODIFIERS.SPACE
                 .toString())) {
                    property += AbilityEnums.TARGETING_MODIFIERS.SPACE.toString();
                }
            }
        }

        if (StringMaster.isEmpty(property)) {
            return;
        }

        for (String mod : ContainerUtils.open(property)) {
            Condition targetingModConditions;
            TARGETING_MODIFIERS MOD = new EnumMaster<TARGETING_MODIFIERS>().retrieveEnumConst(
             TARGETING_MODIFIERS.class, mod);
            if (MOD == null) {
                targetingModConditions = ConditionMaster.toConditions(mod);
            } else {
                targetingModConditions = DC_ConditionMaster.getTargetingModConditions(MOD);
            }
            if (targetingModConditions != null) {
                if (!c.contains(targetingModConditions))
                // if (ConditionMaster.contains(c,
                // targetingModConditions.getClass())) //you sure?
                {
                    c.add(targetingModConditions);
                }
            }
        }
        if (targeting instanceof  SelectiveTargeting) {
            SelectiveTargeting tst = ( SelectiveTargeting) targeting;
            if (tst.getTemplate() !=null ){
                c.add(DC_ConditionMaster.getSelectiveTargetingTemplateConditions(tst.getTemplate()));
            }
            if (tst.getTemplate() == SELECTIVE_TARGETING_TEMPLATES.GRAVE_CELL) {// TODO
                if (tst.getConditions() != null) {
                    try {
                        ((GraveCondition) tst.getConditions().get(0)).getConditions().add(c);
                    } catch (Exception e) {
                        main.system.auxiliary.log.LogMaster.log(1,
                         tst.getConditions().get(0) + " in " + obj);
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
            }
        }
        if (!c.isEmpty()) {
            targeting.getFilter().addCondition(c);
        }
        targeting.setModsAdded(true);
    }

    public static Effect wrapEffects(EFFECTS_WRAP wrap, Effect effects, Entity entity) {
        Formula radius = new Formula(entity.getParam(G_PARAMS.RADIUS));
        Boolean allyOrEnemyOnly = null;
        if (entity.checkBool(GenericEnums.STD_BOOLS.NO_FRIENDLY_FIRE)) {
            allyOrEnemyOnly = false;
        }
        if (entity.checkBool(GenericEnums.STD_BOOLS.NO_ENEMY_FIRE)) {
            allyOrEnemyOnly = true;
        }
        boolean notSelf = entity.checkBool(GenericEnums.STD_BOOLS.NO_SELF_FIRE);
        switch (wrap) {
            case CHAIN:
                break;
            case CUSTOM_ZONE_CROSS:
                break;
            case CUSTOM_ZONE_STAR:
                // new ShapeEffect() {
                // protected SHAPES getShape() {
                // return null;
                // }
                //
                // protected FACING_DIRECTION getFacing() {
                // return null;
                // }
                //
                // protected Coordinates getBaseCoordinate() {
                // // TODO Auto-generated method stub
                // return null;
                // }
                // };
                break;
            case ZONE:
                effects = new Effects(new ZoneEffect(effects, radius, allyOrEnemyOnly, notSelf));
                break;

            case SINGLE_BUFF:

                Effects buffEffects = new Effects();

                if (effects instanceof Effects) {
                    for (Effect effect : (Effects) effects) {
                        if (effect instanceof AddBuffEffect) {
                            buffEffects.add(((AddBuffEffect) effect).getEffect());
                        } else {
                            buffEffects.add(effect);
                        }
                    }
                } else {
                    buffEffects.add(effects);
                }

                String buffName = entity.getProperty(PROPS.BUFF_NAME);
                if (buffName == null) {

                }
                effects = new AddBuffEffect(buffName, buffEffects);
                break;

            default:
                break;
        }

        return effects;
    }

    public static Effect wrapEffects(TARGETING_MODE mode, Effect effects, DC_ActiveObj entity) {
        Formula radius = new Formula(entity.getParam(G_PARAMS.RADIUS));
        Formula range = new Formula(entity.getParam(PARAMS.RANGE));
        Boolean allyOrEnemyOnly = null;
        if (entity.checkBool(GenericEnums.STD_BOOLS.NO_FRIENDLY_FIRE)) {
            allyOrEnemyOnly = false;
        }
        if (entity.checkBool(GenericEnums.STD_BOOLS.NO_ENEMY_FIRE)) {
            allyOrEnemyOnly = true;
        }
        boolean notSelf = entity.checkBool(GenericEnums.STD_BOOLS.NO_SELF_FIRE);
        switch (mode) {
            case SPRAY:
                effects = new WaveEffect(effects, radius, range, allyOrEnemyOnly, true, false,
                 entity.checkBool(GenericEnums.STD_BOOLS.APPLY_THRU) ? C_OBJ_TYPE.BF : C_OBJ_TYPE.BF_OBJ);
                break;
            case WAVE:
                effects = new WaveEffect(effects, radius, range, allyOrEnemyOnly, true, true,
                 entity.checkBool(GenericEnums.STD_BOOLS.APPLY_THRU) ? C_OBJ_TYPE.BF : C_OBJ_TYPE.BF_OBJ); // expanding
                // -
                // targeting
                // modifier?
                break;
            case RAY:
            case RAY_AUTO:
                effects = new RayEffect(effects);
                // ++ special conditions for allyOrEnemyOnly
                break;

            case DOUBLE:
                break;
            case MULTI:
                break;

            case TRIPPLE:
                break;

            case BLAST:
                if (entity.getIntParam(G_PARAMS.RADIUS) > 0) // ideally, I
                // shouldn't mix
                // up targeting
                // and effect
                // wrapping
                {
                    effects = new ZoneEffect(effects, radius, allyOrEnemyOnly, notSelf);
                }
                break;

            case NOVA:
                // TODO
                effects = new Effects(new ZoneEffect(effects, radius, allyOrEnemyOnly, true));
                break;
            case SINGLE:
                break;

            case TRAP:
                break;
            default:
                break;

        }

        return effects;
    }

    public static void constructSingleTargeting(DC_ActiveObj obj) {
        Targeting t = getSingleTargeting(obj);
        if (t != null) {
            obj.setTargeting(t);
        }
    }

    public static Targeting getSingleTargeting(DC_ActiveObj obj) {
        List<ActiveObj> actives = obj.getActives();
        if (actives == null) {
            return null;
        }
        if (actives.size() < 1) {
            return null;
        }
        if (actives.get(0) == null) {
            return null;
        }
        Targeting targeting;
        try {
            targeting = ((AbilityObj) actives.get(0)).getType().getAbilities()
             .getTargeting();
        } catch (Exception e) {
            targeting = getDefaultSingleTargeting(obj);
        }

        return targeting;
    }

    public static Effect getEffectsFromAbilityType(String abilName) {

        AbilityType abilType = (AbilityType) DataManager.getType(abilName, DC_TYPE.ABILS);
        if (abilType == null) {
            abilType = VariableManager.getVarType(abilName);
            if (abilType == null) {
                LogMaster

                 .log(1, "getEffectsFromAbilityType: no such ability - " + abilName);
                return null;
            }
        }
        Abilities abils = AbilityConstructor.constructAbilities(abilType.getDoc());

        Effects effects = new Effects();
        for (Ability ability : abils) {
            effects.add(ability.getEffects());

        }
        return effects;
    }

}
