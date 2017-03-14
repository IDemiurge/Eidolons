package main.game.logic.combat.damage;

import main.content.PARAMS;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_GameManager;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventType;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.libgdx.anims.phased.PhaseAnimator;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.PhaseAnimation;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

/**
 * Created by JustMe on 3/14/2017.
 */
public class DamageDealer {


    public static int dealDamageOfType(DAMAGE_TYPE damage_type, Unit targetObj, Ref ref,
                                       int amount) {
        Unit attacker = (Unit) ref.getSourceObj();
        // if (global_damage_mod != 0)
        // amount *= OptionsMaster.getGameOptions.getOption(global_damage_mod) /
        // 100;
        if (!processDamageEvent(damage_type, ref, amount,
         STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_DAMAGE)) {
            return -1;
        }
        // VITAL!
        amount = ref.getAmount();
        ref.getGame().getLogManager().logDamageBeingDealt(amount, attacker, targetObj, damage_type);

        if (!processDamageEvent(damage_type, ref, amount, new EventType(
         CONSTRUCTED_EVENT_TYPE.UNIT_IS_DEALT_DAMAGE_OF_TYPE, damage_type.toString()))) {
            return 0;
        }
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        int damageLeft;
        int damageDealt = 0;
        if (damage_type == DAMAGE_TYPE.PURE) {
            damageDealt = dealPureDamage(targetObj, attacker, amount,
             (DamageCalculator.isEnduranceOnly(ref) ? 0
              : amount), ref);
        } else {
            damageDealt = dealDamage(ref, !isAttack(ref), damage_type);
        }

        damageLeft = amount - damageDealt;

        if (!ref.isQuiet()) {
            try {
                active.getRef().setValue(KEYS.DAMAGE_DEALT, damageDealt + "");
            } catch (Exception e) {

            }
        }

        new Event(CONSTRUCTED_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_DAMAGE_OF_TYPE,
         damage_type.toString(), ref).fire(); // TODO
        return damageLeft;

    }

    private static int dealDamage(Ref ref, boolean magical, DAMAGE_TYPE dmg_type) {
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE, ref);
        if (!event.fire()) {
            return -1;
        }
        int amount = ref.getAmount();
        if (amount <= 0) {
            return 0;// sure?
        }
        ref = Ref.getCopy(ref);
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        Unit attacker = (Unit) ref.getSourceObj();
        Unit attacked = (Unit) ref.getTargetObj();
        if (dmg_type == null) {
            dmg_type = active.getEnergyType();
        }


        int blocked = 0;
        if (!DamageCalculator.isUnblockable(ref)) {
            if (ref.getSource() != ref.getTarget()) {
                blocked = getArmorReduction(amount, attacked, attacker, active);
            }
        }

        int t_damage = DamageCalculator.calculateToughnessDamage(attacked, attacker, amount, magical, ref, blocked,
         dmg_type);
        int e_damage = DamageCalculator.calculateEnduranceDamage(attacked, attacker, amount, magical, ref, blocked,
         dmg_type);
        PhaseAnimator.handleDamageAnimAndLog(ref, attacked, magical, dmg_type);

        ref.setAmount(e_damage);
        // TODO separate event types?
        if (!new Event(magical ? STANDARD_EVENT_TYPE.UNIT_IS_DEALT_PHYSICAL_ENDURANCE_DAMAGE
         : STANDARD_EVENT_TYPE.UNIT_IS_DEALT_PHYSICAL_ENDURANCE_DAMAGE, ref).fire()) {
            return 0;
        }
        ref.setAmount(t_damage);
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_IS_DEALT_PHYSICAL_TOUGHNESS_DAMAGE, ref).fire()) {
            return 0;
        }

        int result = dealPureDamage(attacked, attacker, t_damage, e_damage, ref);
        new Event(magical ?
         STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE
         : STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE, ref).fire();

        attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, attacked, amount);
        return result;
    }


    protected static boolean processDamageEvent(DAMAGE_TYPE damage_type, Ref ref, int amount,
                                                EVENT_TYPE event_type) {
        ref.setValue(KEYS.DAMAGE_TYPE, damage_type.toString());
        ref.setAmount(amount);
        KEYS key = null;
        PARAMETER statsParam = null;
        boolean add = false;
        if (event_type == STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_DAMAGE) {
            key = KEYS.DAMAGE_AMOUNT;
            statsParam = PARAMS.DAMAGE_LAST_AMOUNT;
        } else {
            EventType type = (EventType) event_type;
            if (type.getType().equals(CONSTRUCTED_EVENT_TYPE.UNIT_IS_DEALT_DAMAGE_OF_TYPE)) {
                key = KEYS.DAMAGE_DEALT;
                statsParam = PARAMS.DAMAGE_LAST_DEALT;
            }
            if (type.getType().equals(CONSTRUCTED_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_DAMAGE_OF_TYPE)) {
                key = KEYS.DAMAGE_TOTAL;
                statsParam = PARAMS.DAMAGE_TOTAL;
                add = true;
            }
        }
        if (ref.isQuiet()) {
            return true;
        }
        if (ref.getActive() != null) {
            try {
                Ref REF = ref.getActive().getRef();
                if (add) {
                    REF.setValue(key, (REF.getInteger(statsParam.toString()) + amount) + "");
                    ref.getActive().modifyParameter(statsParam, amount);
                } else {
                    REF.setValue(key, amount + "");
                    ref.getActive().setParam(statsParam, amount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Event event = new Event(event_type, ref);
        return (event.fire());
    }

    private static int dealPureDamage(Unit attacked, Unit attacker, Integer endurance_dmg,
                                      Integer toughness_dmg, Ref ref) {
        // apply Absorption here?

        boolean enduranceOnly = false;
        if (toughness_dmg == null) {
            enduranceOnly = true;
            toughness_dmg = 0;
        }
        // TODO if not started already
        attacked.getGame().getLogManager().newLogEntryNode(true, ENTRY_TYPE.DAMAGE);

        LogMaster.log(1, toughness_dmg + " / " + endurance_dmg + " damage being dealt to "
         + attacked.toString());
        attacked.getGame().getLogManager().logDamageDealt(toughness_dmg, endurance_dmg, attacker, attacked);

        ref.setTarget(attacked.getId());
        ref.setSource(attacker.getId());

        Event event;
        boolean result;
        int actual_t_damage = 0;

        if (toughness_dmg > 0) {
            if (!enduranceOnly) {

                event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_TOUGHNESS_DAMAGE, ref);
                ref.setAmount(toughness_dmg);
                result = event.fire();
                if (DC_GameManager.checkInterrupted(ref)) {
                    return 0;
                }
                toughness_dmg = ref.getAmount(); // triggers may have changed the
                // amount!
                actual_t_damage = Math.min(attacked.getIntParam(PARAMS.C_TOUGHNESS), toughness_dmg);
                ref.setAmount(actual_t_damage);
                // for cleave and other sensitive effects

                //TODO ?? int t_dmg_remaining = actual_t_damage
                // - attacked.getIntParam(PARAMS.C_TOUGHNESS);
                if (result) {
                    attacked.modifyParameter(PARAMS.C_TOUGHNESS, -toughness_dmg);
                }
                event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE, ref);
                result = event.fire();

            }
        }

        int actual_e_damage = 0;
        if (endurance_dmg > 0) {
            ref.setAmount(endurance_dmg);
            event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_ENDURANCE_DAMAGE, ref);

            result = event.fire();
            if (DC_GameManager.checkInterrupted(ref)) {
                attacked.getGame().getLogManager().doneLogEntryNode();
                return 0;
            }

            endurance_dmg = ref.getAmount();
            actual_e_damage = Math.min(attacked.getIntParam(PARAMS.C_ENDURANCE), endurance_dmg);
            ref.setAmount(actual_e_damage);

            if (result) {
                attacked.modifyParameter(PARAMS.C_ENDURANCE, -endurance_dmg);
            }
            event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_DEALT_ENDURANCE_DAMAGE, ref);
            result = event.fire();

        }
        int damageDealt = Math.max(actual_e_damage, actual_t_damage);

        boolean dead = DamageCalculator.isDead(attacked);
        try {
            PhaseAnimation animation = PhaseAnimator.getAnimation(ref, attacked);
            if (animation != null) {
                animation.addPhaseArgs(PHASE_TYPE.DAMAGE_DEALT, toughness_dmg, endurance_dmg, ref
                 .getDamageType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dead) {
            // will start new entry... a good check
            try {
                attacked.kill(attacker, true, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ref.setAmount(damageDealt);
            // if (DC_GameManager.checkInterrupted(ref))
            // return 0; ???
        }
        LogMaster.log(1, toughness_dmg + " / " + endurance_dmg + " damage has been dealt to "
         + attacked.toString());
        attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, attacked,
         damageDealt);

        return damageDealt;
    }

    protected static int getArmorReduction(int base_damage, Unit attacked, Unit attacker,
                                           DC_ActiveObj action) {
        return DamageCalculator.getArmorReduction(base_damage, attacked, attacker, action, false);
    }


    protected static boolean isAttack(Ref ref) {
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            return true;
        }
        return false;
    }

}
