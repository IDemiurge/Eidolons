package eidolons.game.battlecraft.rules.combat.attack.dual;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.attachment.AddTriggerEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.system.DC_Formulas;
import main.ability.ActiveAbility;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.conditions.standard.ChanceCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;

public class DualWieldingRule {
    public static final String MAIN_HAND = ActionEnums.ACTION_TAGS.MAIN_HAND.toString();
    public static final String OFF_HAND = ActionEnums.ACTION_TAGS.OFF_HAND.toString();
    private static final G_PROPS PROP = G_PROPS.ACTION_TAGS;
    // private static final String DEFAULT_STA_REDUCTION = "-25";
    // private static final String DEFAULT_AP_REDUCTION = "-33";
    public static final String buffTypeNameOffHand = StringMaster.getWellFormattedString(MetaEnums.STD_BUFF_NAME.Off_Hand_Cadence.name());
    public static final String buffTypeNameMainHand = StringMaster.getWellFormattedString(MetaEnums.STD_BUFF_NAME.Main_Hand_Cadence.name());
    private static final Formula DURATION = new Formula("1");

    private static boolean checkSingleWeaponCadence(Unit unit, DC_UnitAction action) {
        if (unit.getActiveWeapon(action.isOffhand()).getIntParam(PARAMS.CADENCE_BONUS) > 0) {
            return true;
        }

        return action.getIntParam(PARAMS.CADENCE_BONUS) > 0;

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

        return buff.getRef().getObj(KEYS.WEAPON) != action.getRef().getObj(KEYS.WEAPON);
        // preCheck new weapon - ? Buff ref?
    }

    public static void checkDualAttackCadence(DC_UnitAction action, Unit unit) {
        if (action.getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            return;
        }
        boolean singleCadence = checkSingleWeaponCadence(unit, action);
        boolean natural =isNaturalAllowed() && UnitAnalyzer.checkDualNaturalWeapons(unit) ;
        if (!UnitAnalyzer.checkDualWielding(unit) && !natural
         && !singleCadence
            // || checkSingleCadence(action)
         ) {
            return;
        }
        Boolean offhand = null;
        if (action.checkProperty(PROP, MAIN_HAND)) {
            offhand = false;
        } else if (action.checkProperty(PROP, OFF_HAND)) {
            offhand = true;
        } else if (singleCadence)
        // offhand = !action.isOffhand();
        {
            offhand = false;
        }
        if (offhand == null) {
            return;
        }
        Ref ref = new Ref(unit.getGame(), unit.getId());
        DC_WeaponObj weapon = unit.getActiveWeapon(offhand);
        List<Obj> targets = new ArrayList<>();

        if (unit.getWeapon(!offhand) != null) {
            targets.add(unit.getWeapon(!offhand));
        }
        if (unit.getNaturalWeapon(!offhand) != null) {
            targets.add(unit.getNaturalWeapon(!offhand));
        }

        //TODO
        targets.add(unit);

        GroupImpl group = new GroupImpl(targets);
        LogMaster.log(LogMaster.RULES_DEBUG, "Cadence Rule applies to " + group);
        ref.setGroup(group);

        // INIT COST CADENCE EFFECTS
        if (checkFocusBonusApplies(unit, action, singleCadence)) {
            Integer amount = action.getOwnerUnit().getIntParam(PARAMS.CADENCE_FOCUS_BOOST);
            amount += action.getIntParam(PARAMS.CADENCE_FOCUS_BOOST);
            amount += action.getOwnerUnit().getActiveWeapon(!offhand).getIntParam(
             PARAMS.CADENCE_FOCUS_BOOST);
            action.getOwnerUnit().modifyParameter(PARAMS.C_FOCUS, amount, 100);
        }
        Effects effects = new Effects();
        String cadence = unit.getParam(PARAMS.CADENCE_AP_MOD);
        if (cadence.isEmpty()) {
            cadence = DC_Formulas.DEFAULT_CADENCE_AP_MOD + "";
        }
        ModifyValueEffect valueEffect = new ModifyValueEffect(
         PARAMS.ATTACK_AP_PENALTY,
         MOD.MODIFY_BY_CONST, cadence);

        valueEffect.appendFormulaByMod(100 + weapon.getIntParam(PARAMS.CADENCE_BONUS));
        effects.add(valueEffect);
        cadence = unit.getParam(PARAMS.CADENCE_STA_MOD);
        if (cadence.isEmpty()) {
            cadence = DC_Formulas.DEFAULT_CADENCE_STA_MOD + "";
        }
        valueEffect = new ModifyValueEffect(
         PARAMS.ATTACK_STA_PENALTY,
         MOD.MODIFY_BY_CONST, cadence);
        valueEffect.appendFormulaByMod(100 + weapon.getIntParam(PARAMS.CADENCE_BONUS));

        effects.add(valueEffect);
        if (unit.getIntParam(PARAMS.CADENCE_DAMAGE_MOD) > 0) {
            effects.add(new ModifyValueEffect(
             PARAMS.DAMAGE_MOD,
             MOD.MODIFY_BY_CONST, unit
             .getParam(PARAMS.CADENCE_DAMAGE_MOD)));
        }
        if (unit.getIntParam(PARAMS.CADENCE_ATTACK_MOD) > 0) {
            effects.add(new ModifyValueEffect(
             PARAMS.ATTACK_MOD
             , MOD.MODIFY_BY_CONST, unit
             .getParam(PARAMS.CADENCE_ATTACK_MOD)));
        }


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

        AddBuffEffect addBuffEffect = new AddBuffEffect(buffTypeName,
         effects, DURATION);
        // preCheck perk
        addBuffEffect.addEffect(new AddTriggerEffect( // what about
         // counters/AoO?
         STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE, new RefCondition(KEYS.EVENT_SOURCE,
         KEYS.SOURCE), new ActiveAbility(new FixedTargeting(KEYS.SOURCE),
         new RemoveBuffEffect(buffTypeName))));

        Integer param = unit.getIntParam(PARAMS.CADENCE_DEFENSE_MOD);
        if (param != 0) {
            addBuffEffect.addEffect(new ModifyValueEffect(PARAMS.DEFENSE_MOD,
             MOD.MODIFY_BY_CONST, "" + param));
        }

        addBuffEffect.setIrresistible(true);
        addBuffEffect.apply(ref);

        addHeroBuff(action.getOwnerUnit(), offhand);
        // TODO defense mod effect
    }

    private static boolean isNaturalAllowed() {
        return false;
    }

    private static void addHeroBuff(Unit unit, Boolean offhand) {

    }


}
