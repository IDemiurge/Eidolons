package main.rules.combat;

import main.ability.ActiveAbility;
import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.RemoveBuffEffect;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.oneshot.common.AddTriggerEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.targeting.TemplateAutoTargeting;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.conditions.StringComparison;
import main.elements.conditions.standard.ChanceCondition;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.BuffObj;
import main.entity.obj.unit.Unit;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.rules.UnitAnalyzer;
import main.system.DC_Formulas;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

public class CadenceRule {
    public static final String MAIN_HAND = ActionEnums.ACTION_TAGS.MAIN_HAND.toString();
    public static final String OFF_HAND = ActionEnums.ACTION_TAGS.OFF_HAND.toString();
    private static final G_PROPS PROP = G_PROPS.ACTION_TAGS;
    // private static final String DEFAULT_STA_REDUCTION = "-25";
    // private static final String DEFAULT_AP_REDUCTION = "-33";
    private static final String buffTypeNameOffHand = MetaEnums.STD_BUFF_NAMES.Off_Hand_Cadence.name();
    private static final String buffTypeNameMainHand = MetaEnums.STD_BUFF_NAMES.Main_Hand_Cadence.name();
    private static final Formula DURATION = new Formula("1");

    private static boolean checkSingleWeaponCadence(Unit unit, DC_UnitAction action) {
        if (unit.getActiveWeapon(action.isOffhand()).getIntParam(PARAMS.CADENCE_BONUS) > 0) {
            return true;
        }

        if (action.getIntParam(PARAMS.CADENCE_BONUS) > 0) {
            return true;
        }

        return false;
    }

    private static boolean checkFocusBonusApplies(Unit unit, DC_UnitAction action,
                                                  boolean singleCadence) {
        BuffObj buff = unit.getBuff(buffTypeNameOffHand, false);
        if (buff == null) {
            buff = unit.getBuff(buffTypeNameMainHand, false);
        }
        if (buff == null) {
            return false;
        }

        if (buff.getRef().getObj(KEYS.WEAPON) == action.getRef().getObj(KEYS.WEAPON)) {
            return false;
        }
        // check new weapon - ? Buff ref?

        return true;
    }

    public static void checkDualAttackCadence(DC_UnitAction action, Unit unit) {
        if (action.getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            return;
        }
        boolean singleCadence = checkSingleWeaponCadence(unit, action);
        if (!UnitAnalyzer.checkDualWielding(unit) && !UnitAnalyzer.checkDualNaturalWeapons(unit)
                && !singleCadence
            // || checkSingleCadence(action)
                ) {
            return;
        }
        Boolean offhand = null;
        if (action.checkProperty(PROP, MAIN_HAND)) {
            offhand = false;
        }
        if (action.checkProperty(PROP, OFF_HAND)) {
            offhand = true;
        }
        if (singleCadence)
        // offhand = !action.isOffhand();
        {
            offhand = false;
        }

        if (offhand == null) {
            return;
        }
        LogMaster.log(LogMaster.RULES_DEBUG, "Cadence Rule applies! ");

        Ref ref = new Ref(unit.getGame(), unit.getId());
        ref.setTarget(unit.getId());

        if (checkFocusBonusApplies(unit, action, singleCadence)) {
            Integer amount = action.getOwnerObj().getIntParam(PARAMS.CADENCE_FOCUS_BOOST);
            amount += action.getIntParam(PARAMS.CADENCE_FOCUS_BOOST);
            amount += action.getOwnerObj().getActiveWeapon(!offhand).getIntParam(
                    PARAMS.CADENCE_FOCUS_BOOST);
            action.getOwnerObj().modifyParameter(PARAMS.C_FOCUS, amount, 100);
        }
        // INIT COST CADENCE EFFECTS
        Effects effects = new Effects();
        String cadence = unit.getParam(PARAMS.CADENCE_AP_MOD);
        if (cadence.isEmpty()) {
            cadence = DC_Formulas.DEFAULT_CADENCE_AP_MOD + "";
        }
        ModifyValueEffect valueEffect = new ModifyValueEffect(PARAMS.AP_COST,
                MOD.MODIFY_BY_PERCENT, cadence);
        DC_WeaponObj weapon = unit.getActiveWeapon(offhand);
        valueEffect.modifyFormula(100 + weapon.getIntParam(PARAMS.CADENCE_BONUS));
        effects.add(valueEffect);
        cadence = unit.getParam(PARAMS.CADENCE_STA_MOD);
        if (cadence.isEmpty()) {
            cadence = DC_Formulas.DEFAULT_CADENCE_STA_MOD + "";
        }
        valueEffect = new ModifyValueEffect(PARAMS.STA_COST, MOD.MODIFY_BY_PERCENT, cadence);
        valueEffect.modifyFormula(100 + weapon.getIntParam(PARAMS.CADENCE_BONUS));

        effects.add(valueEffect);
        effects.add(new ModifyValueEffect(PARAMS.DAMAGE_MOD, MOD.MODIFY_BY_PERCENT, unit
                .getParam(PARAMS.CADENCE_DAMAGE_MOD)));
        effects.add(new ModifyValueEffect(PARAMS.ATTACK_MOD, MOD.MODIFY_BY_PERCENT, unit
                .getParam(PARAMS.CADENCE_ATTACK_MOD)));
        String buffTypeName = (!offhand) ? buffTypeNameOffHand : buffTypeNameMainHand;

        // ADD REMOVE TRIGGER
        int percentage = 100 - unit.getIntParam(PARAMS.CADENCE_RETAINMENT_CHANCE)
                - action.getIntParam(PARAMS.CADENCE_RETAINMENT_CHANCE)
                - weapon.getIntParam(PARAMS.CADENCE_RETAINMENT_CHANCE);
        Conditions conditions = new Conditions(new RefCondition(KEYS.EVENT_SOURCE, KEYS.SOURCE));
        if (percentage != 100) {
            conditions.add(new ChanceCondition(new Formula("" + percentage)));
        }
        effects.add(new AddTriggerEffect(STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE, conditions,
                new ActiveAbility(new FixedTargeting(KEYS.BASIS),
                        new RemoveBuffEffect(buffTypeName))));

        Condition condition = new StringComparison(StringMaster.getValueRef(KEYS.MATCH, PROP),
                (offhand) ? MAIN_HAND : OFF_HAND, false);
        AddBuffEffect effect = new AddBuffEffect(buffTypeName, effects, DURATION);

        // retain condition - hero hasBuff()

        // add remove trigger on attack? either off/main hand, so there is no
        // stacking...

        // linked buffs?
        effect.setIrresistible(false);
        AddBuffEffect addBuffEffect = new AddBuffEffect(buffTypeName, new CustomTargetEffect(
                new TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES.ACTIONS, condition), effect),
                DURATION);
        // check perk
        addBuffEffect.addEffect(new AddTriggerEffect( // what about
                // counters/AoO?
                STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE, new RefCondition(KEYS.EVENT_SOURCE,
                KEYS.SOURCE), new ActiveAbility(new FixedTargeting(KEYS.SOURCE),
                new RemoveBuffEffect(buffTypeName))));

        Integer param = unit.getIntParam(PARAMS.CADENCE_DEFENSE_MOD);
        if (param != 0) {
            addBuffEffect.addEffect(new ModifyValueEffect(PARAMS.DEFENSE_MOD,
                    MOD.MODIFY_BY_PERCENT, "" + param));
        }

        addBuffEffect.setIrresistible(true);
        addBuffEffect.apply(ref);
        // TODO defense mod effect
    }

}
