package eidolons.game.battlecraft.rules.combat.damage;

import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.armor.ArmorMaster;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.master.EffectMaster;
import eidolons.game.exploration.dungeon.struct.Entrance;
import eidolons.system.libgdx.GdxStatic;
import main.ability.effects.container.SpecialTargetingEffect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.ActionEnums;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.IActiveObj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventType;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

/**
 * Created by JustMe on 3/14/2017. Handles the process of damage dealing for AttackEffect and DealDamageEffect
 */
public class DamageDealer {
    private static boolean logOn;

    /**
     * Accepts Damage Object that encapsulates all necessary data
     *
     * @param damage
     * @return
     */
    public static int dealDamage(Damage damage) {
        boolean bonus = damage.getRef().isTriggered();
        return dealDamage(damage, bonus);
    }

    private static int dealDamage(Damage damage, boolean isBonusDamage) {
        logOn = true;
        if (checkDamageImmune(damage)) {
            if (!isBonusDamage)
                GdxStatic.floatingText(VisualEnums.TEXT_CASES.REQUIREMENT,
                        "Ineffective!", damage.getTarget());
            // DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.LAUGH, damage.getTarget());
            return 0;
        }
        int result = dealDamageOfType(damage, damage.getDmgType(),
                damage.getTarget()
                , damage.getRef(), damage.getAmount(), isBonusDamage);

        if (damage instanceof MultiDamage) {
            logOn = false;
            int bonus = dealBonusDamage((MultiDamage) damage, result);
            result += bonus;
        }

        logOn = true;
        return result;
    }

    private static boolean checkDamageImmune(Damage damage) {
        if (damage.getTarget() instanceof Entrance)
            return true;
        return damage.getTarget().isInvulnerable();
    }

    /**
     * @param multiDamage
     * @param dealt       damage already dealt by main Damage object
     * @return
     */
    private static int dealBonusDamage(MultiDamage multiDamage, int dealt) {
        int bonus = 0;
        for (Damage bonusDamage : multiDamage.getAdditionalDamage()) {
            bonusDamage.setAction(multiDamage.getAction());
            bonusDamage.setRef(multiDamage.getRef());
            bonusDamage.getRef().setQuiet(true);
            if (bonusDamage instanceof ConditionalDamage) {
                //TODO
            }
            if (bonusDamage instanceof FormulaDamage) {
                if (((FormulaDamage) bonusDamage).isPercentage()) {
                    int percent = bonusDamage.getAmount();
                    if (((FormulaDamage) bonusDamage).isFromRaw()) {
                        bonusDamage.setAmount(multiDamage.getAmount() * percent / 100);
                    } else {
                        bonusDamage.setAmount(dealt * percent / 100);
                    }
                }
            }
            int damageDealt = dealDamage(bonusDamage, true);
            main.system.auxiliary.log.LogMaster.log(1, "Bonus damage dealt: " + damageDealt
                    + StringMaster.wrapInParenthesis(bonusDamage.getDmgType().getName()));
            bonus += damageDealt;

        }
        return bonus;
    }

    /**
     * This method accepts amount of damage already reduced by everything <b>except Resistance and Armor</b>  (defense,
     * shield...)
     *
     * @param damage_type enum const, null will be set to active.getEnergyType()
     * @param targetObj   BattleFieldObject to deal dmg to
     * @param ref         contains all the other info we may need
     * @param amount      total amount of damage to be reduced by Resistance and Armor (unless damage_type==PURE) and
     *                    dealt as PURE
     * @return actual amount of damage dealt ( max(min(Toughness*(1-DEATH_BARRIER), Toughness dmg),min(Endurance,
     * Endurance dmg))
     */
    private static int dealDamageOfType(Damage damage, DAMAGE_TYPE damage_type, BattleFieldObject targetObj, Ref ref,
                                        int amount, boolean bonus) {
        BattleFieldObject attacker = (BattleFieldObject) ref.getSourceObj();
        if (!processDamageEvent(damage_type, ref, amount,
                STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_DAMAGE)) {
            return -1;
        }
        // VITAL!
        amount = ref.getAmount();
        if (isLogOn())
            if (isLogged(attacker, targetObj, ref.getActive())) {
                ref.getGame().getLogManager().logDamageBeingDealt(amount, attacker, targetObj, damage_type);
            }

        if (!processDamageEvent(damage_type, ref, amount, new EventType(
                CONSTRUCTED_EVENT_TYPE.UNIT_IS_DEALT_DAMAGE_OF_TYPE, damage_type.toString()))) {
            return 0;
        }
        ActiveObj active = (ActiveObj) ref.getActive();
        int damageDealt;
        if (damage_type == DAMAGE_TYPE.PURE) {
            damageDealt = dealPureDamage(targetObj, attacker,
                    (DamageCalculator.isEnduranceOnly(ref) ? 0
                            : amount), amount, ref);
        } else {
            damageDealt = dealReducedDamage(damage);
        }

        if (!ref.isQuiet()) {
            try {
                active.getRef().addValue(KEYS.DAMAGE_DEALT, damageDealt);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        addDamageDealt(active, damage_type, damageDealt, !bonus, ref);
        return damageDealt;

    }


    private static boolean isLogged(BattleFieldObject attacker, BattleFieldObject targetObj, IActiveObj active) {
        if (active == null || EffectMaster.getFirstEffectOfClass((ActiveObj) active, SpecialTargetingEffect.class) != null) {
            //don't log every crate being damaged...
            return !(targetObj instanceof Structure);
        }
        return true;
    }

    //proceeds to deal the damage - to toughness and endurance sequentially and with appropriate events
    private static int dealReducedDamage(Damage damage) {
        Ref ref = damage.getRef();
        DAMAGE_TYPE dmg_type = damage.dmg_type;
        boolean magical = dmg_type.isMagical();

        ref.setValue(KEYS.DAMAGE_TYPE, dmg_type.getName());
        Event event = new Event(
                magical ? STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_SPELL_DAMAGE :
                        STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE, ref);
        if (!event.fire()) {
            return -1;
        }
        int amount = ref.getAmount();
        if (amount <= 0) {
            return 0;
        }
        ref = Ref.getCopy(ref);
        ActiveObj active = (ActiveObj) ref.getActive();
        BattleFieldObject attacker = (BattleFieldObject) ref.getSourceObj();
        BattleFieldObject attacked = (BattleFieldObject) ref.getTargetObj();
        if (dmg_type == null) {
            dmg_type = active.getEnergyType();
        }

        int blocked = 0;
        if (attacked instanceof Unit)
            if (!DamageCalculator.isUnblockable(ref)) {
                if (ref.getSource() != ref.getTarget()) {
                    damage = attacked.getGame().getArmorMaster().processDamage(damage,
                            damage.isSneak(), damage.getHitType(), damage.isAttack());
                    blocked = damage.getBlocked();
                    amount = damage.getAmount() - blocked;
                }
            }

        StringBuilder log = damage.getLogBuilder();

        int resistance = ResistMaster.getResistanceForDamageType(attacked, attacker, dmg_type);
        amount = amount - MathMaster.applyPercent(amount, resistance);
        int armor = ArmorMaster.getArmorValue(attacked, dmg_type);
        if (log != null) {
            log.append("Damage reduced by " +
                    resistance +
                    "% (Resistance), plus additional " +
                    armor + " (Natural Armor)");
        }
        amount -=armor;

        if (amount<=0){
            //TODO !!!
        }

        int t_damage = DamageCalculator.calculateToughnessDamage(attacked, attacker, amount, ref,
                dmg_type, log);
        ref.setAmount(t_damage);
        if (!new Event(magical ? STANDARD_EVENT_TYPE.UNIT_IS_DEALT_MAGICAL_TOUGHNESS_DAMAGE
                : STANDARD_EVENT_TYPE.UNIT_IS_DEALT_PHYSICAL_TOUGHNESS_DAMAGE, ref).fire()) {
            return 0;
        }
        int e_damage = DamageCalculator.calculateEnduranceDamage(attacked, attacker,
                //NF Rules
                amount- t_damage
                , ref,
                dmg_type, log);

        ref.setAmount(e_damage);
        // TODO separate event types?
        if (!new Event(magical ? STANDARD_EVENT_TYPE.UNIT_IS_DEALT_MAGICAL_ENDURANCE_DAMAGE
                : STANDARD_EVENT_TYPE.UNIT_IS_DEALT_PHYSICAL_ENDURANCE_DAMAGE, ref).fire()) {
            return 0;
        }
        ref.setValue(KEYS.DAMAGE_TYPE, dmg_type.getName());
        int result = dealPureDamage(attacked, attacker, t_damage, e_damage, ref);
        ref.setAmount(result);
        new Event(magical ?
                STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE
                : STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE, ref).fire();


        if (isLogOn()) {
            attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, attacked, amount);
        }
        return result;
    }

    // for floatingText anims
    protected static void addDamageDealt(ActiveObj active, DAMAGE_TYPE damage_type,
                                         int amount, boolean main, Ref ref) {
        if (active == null)
            return;

        if (main) {
            active.setDamageDealt(DamageFactory.getGenericDamage(damage_type, amount, ref));
            return;
        }

        MultiDamage multiDamage;
        if (active.getDamageDealt() instanceof MultiDamage) {
            multiDamage = (MultiDamage) active.getDamageDealt();
        } else {
            multiDamage = DamageFactory.getMultiDamage(active.getDamageDealt());
        }

        multiDamage.getAdditionalDamage().add(DamageFactory.
                getGenericDamage(damage_type, amount, ref));

        active.setDamageDealt(multiDamage);
    }

    // writes values to appropriate parameters of the damage-dealing action, checks event-interruptions
    protected static boolean processDamageEvent(DAMAGE_TYPE damage_type, Ref ref,
                                                int amount,
                                                EVENT_TYPE event_type) {
        if (damage_type != null) {
            ref.setValue(KEYS.DAMAGE_TYPE, damage_type.toString());
        }
        ref.setAmount(amount);
        KEYS key = null;
        PARAMETER statsParam = null;
        boolean add = false;

        if (event_type == STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_DAMAGE) {
            key = KEYS.DAMAGE_AMOUNT;
            statsParam = PARAMS.DAMAGE_LAST_AMOUNT;
        } else if (event_type.equals(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE)) {
            key = KEYS.DAMAGE_DEALT;
            statsParam = PARAMS.DAMAGE_LAST_DEALT;
        } else {
            if (((EventType) event_type).getType().equals(CONSTRUCTED_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_DAMAGE_OF_TYPE)) {
                key = KEYS.DAMAGE_TOTAL;
                statsParam = PARAMS.DAMAGE_TOTAL;
                add = true;
            }
        }
        if (ref.isQuiet()) {
            return true;
        }
        if (ref.getActive() != null && statsParam != null) {
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
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }


        Event event = new Event(event_type, ref);
        return (event.fire());
    }

    private static int dealPureDamage(BattleFieldObject attacked, BattleFieldObject attacker,
                                      Integer toughness_dmg, Integer endurance_dmg, Ref ref) {
        // apply Absorption here?

        boolean enduranceOnly = false;
        if (toughness_dmg == null) {
            enduranceOnly = true;
            toughness_dmg = 0;
        }
        // TODO if not started already

        if (isLogOn()) {
            attacked.getGame().getLogManager().logDamageDealt(toughness_dmg, endurance_dmg, attacker, attacked);
        }
        LogMaster.log(1, toughness_dmg + " / " + endurance_dmg + " damage being dealt to "
                + attacked.toString());
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
                actual_t_damage = toughness_dmg; //TODO
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

        int actual_e_damage;
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

        } else {
            return 1;
        }
        int damageDealt = Math.max(actual_e_damage, actual_t_damage);

        processDamageEvent(null, ref, damageDealt, STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE);

        boolean dead = attacked.checkBool(STD_BOOLS.FAUX)
                || DamageCalculator.isDead(attacked);

        boolean annihilated =dead && (attacked instanceof Unit && attacked.getGame().getRules().getUnconsciousRule().
                checkUnitAnnihilated((Unit) attacked));
        boolean unconscious = false;

        if (dead) {
            // will start new entry... a good preCheck
            try {
                if (!attacked.kill(attacker, !annihilated, false)) {
                } else {
                    if (annihilated) {
                        attacked.getGame().getManager().getDeathMaster().
                                unitAnnihilated(attacked, attacker);

                    }
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            ref.setAmount(damageDealt);
            // if (DC_GameManager.checkInterrupted(ref))
            // return 0; ???
        }
        //NF Rules
        unconscious = attacked.getGame().getRules().getUnconsciousRule().checkFallsUnconscious((Unit) attacked);
        if (unconscious) {
            attacked.getGame().getRules().getUnconsciousRule().
                    fallUnconscious((Unit) attacked);
        }
        if (toughness_dmg < 0 || endurance_dmg < 0) {
            LogMaster.log(1, toughness_dmg + "NEGATIVE DAMAGE " + endurance_dmg);
        } else
            LogMaster.log(1, toughness_dmg + " / " + endurance_dmg + " damage has been dealt to "
                    + attacked.toString());

        if (isLogOn()) {
            attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, attacked,
                    damageDealt);
        }

            GdxStatic.checkHpBarReset(attacked);

        return damageDealt;
    }


    protected static boolean isAttack(Ref ref) {
        ActiveObj active = (ActiveObj) ref.getActive();
        if (active == null) {
            return false;
        }
        return active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
    }

    public static boolean isLogOn() {
        return logOn;
    }


}
