package main.game.battlefield.attack;

import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_GameManager;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventType;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.rules.round.UnconsciousRule;
import main.system.auxiliary.Manager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AttackAnimation;
import main.system.graphics.PhaseAnimation;
import main.system.math.DC_MathManager;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;

//NOT MULTITHREADED
public class DamageMaster extends Manager {

    private static int getResistanceForDamageType(int amount, Unit attacked,
                                                  Unit attacker, DAMAGE_TYPE type) {
        if (type == null) {
            return 0;
        }
        int resistance = DC_MathManager.getDamageTypeResistance(attacked, type);
        if (type.isMagical()) {
            resistance -= attacker.getIntParam(PARAMS.RESISTANCE_PENETRATION);
        }
        return resistance;
    }

    // [OUTDATED] private static int applyResistanceMagicReduction(int amount,
    // DC_HeroObj attacked, Obj attacker) {
    // int resistance = attacked.getIntParam(PARAMS.RESISTANCE)
    // - attacker.getIntParam(PARAMS.RESISTANCE_PENETRATION);
    // return applyPercentReduction(amount, resistance);
    // }

    private static int applyPercentReduction(int base_damage, int percent) {
        int damage = base_damage - Math.round(base_damage * percent / 100);

        return damage;
    }

    public static int dealDamage(Ref ref, boolean magical, DAMAGE_TYPE dmg_type) {
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_PHYSICAL_DAMAGE, ref);
        if (!event.fire()) {
            return -1;
        }
        ref = Ref.getCopy(ref);
        int amount = ref.getAmount();
        if (amount <= 0) {
            return 0;// sure?
        }
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        Unit attacker = (Unit) ref.getSourceObj();
        Unit attacked = (Unit) ref.getTargetObj();
        if (dmg_type == null) {
            dmg_type = active.getEnergyType();
        }
        LogEntryNode entry = attacked.getGame().getLogManager().newLogEntryNode(true,
                ENTRY_TYPE.DAMAGE);

        int blocked = 0;
        if (!isPeriodic(ref)) {
            if (ref.getSource() != ref.getTarget()) {
                blocked = getArmorReduction(amount, attacked, attacker, active);
            }
        }

        int t_damage = calculateToughnessDamage(attacked, attacker, amount, magical, ref, blocked,
                dmg_type);
        int e_damage = calculateEnduranceDamage(attacked, attacker, amount, magical, ref, blocked,
                dmg_type);
        PhaseAnimation animation = magical ? getActionAnimation(ref, attacked) : getAttackAnimation(ref,
                attacked);

        entry.addLinkedAnimations(animation);
        entry.setAnimPhasesToPlay(PHASE_TYPE.DAMAGE_DEALT);
//active.getAnimator(). TODO
        if (animation != null)
        animation.addPhaseArgs(true, PHASE_TYPE.REDUCTION_NATURAL, dmg_type);
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
        new Event(magical ? STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE
                : STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PHYSICAL_DAMAGE, ref).fire();

        attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, attacked, amount);
        return result;
    }

    private static AttackAnimation getAttackAnimation(Ref ref, Unit obj) {
        return (AttackAnimation) obj.getGame().getAnimationManager().getAnimation(
                AttackAnimation.generateKey((DC_ActiveObj) ref.getActive()));
    }

    private static PhaseAnimation getAnimation(Ref ref, Unit obj) {
        PhaseAnimation a = getAttackAnimation(ref, obj);
        if (a != null) {
            return a;
        }
        return getActionAnimation(ref, obj);
    }

    private static PhaseAnimation getActionAnimation(Ref ref, Unit obj) {
        return obj.getGame().getAnimationManager().getAnimation(
                ((DC_ActiveObj) ref.getActive()).getAnimationKey());
    }

    @Deprecated
    public static int magicalDamage(Unit attacked, Ref ref, int amount) {

        ref.setAmount(amount);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_SPELL_DAMAGE, ref);
        if (!event.fire()) {
            return -1;
        }
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();

        attacked.getGame().getLogManager().newLogEntryNode(true, ENTRY_TYPE.DAMAGE);
        Unit attacker = (Unit) ref.getSourceObj();
        if (active != null) {
            if (!isPeriodic(ref)) {
                if (ref.getSource() != ref.getTarget()) {
                    int blocked = getArmorReduction(amount, attacked, attacker, active);
                    amount -= blocked;
                }
            }
        }

        ref.setAmount(amount);

        if (!new Event(STANDARD_EVENT_TYPE.UNIT_IS_DEALT_SPELL_DAMAGE, ref).fire()) {
            return 0;
        }
        // TODO influence the amount?
        int result = dealPureDamage(attacked, attacker, amount, amount, ref);
        new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_SPELL_DAMAGE, ref).fire();

        attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, result);
        return result;

    }

    public static int dealDamageOfType(DAMAGE_TYPE damage_type, Unit targetObj,
                                       Unit attacker, int amount) {
        Ref ref = Ref.getSelfTargetingRefNew(targetObj);
        ref.setSource(attacker.getId());
        return dealDamageOfType(damage_type, targetObj, ref, amount);
    }

    public static int getDamage(Ref ref) {
        int amount = ref.getAmount();
        // if (ref.getActive().isSpell())

        DAMAGE_TYPE damageType = ref.getDamageType();
        if (damageType == null) {
            if (ref.getActive() instanceof DC_ActiveObj) {
                DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                damageType = activeObj.getDamageType();
            }
        }
        Unit sourceObj = (Unit) ref.getSourceObj();
        Damage damage = new Damage(damageType, amount, sourceObj, (Unit) ref.getTargetObj());
        int blocked = sourceObj.getGame().getArmorSimulator().getArmorBlockDamage(damage);
        amount -= blocked;
        amount = getResistanceForDamageType(amount, (Unit) ref.getTargetObj(), sourceObj,
                damageType);

        return amount;// applySpellArmorReduction(amount, (DC_HeroObj)
        // ref.getTargetObj(), ref.getSourceObj());

    }

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
        if (damage_type == GenericEnums.DAMAGE_TYPE.PURE || damage_type == GenericEnums.DAMAGE_TYPE.POISON) {
            damageDealt = dealPureDamage(targetObj, attacker, amount, (isPeriodic(ref) ? null
                    : amount), ref);
        } else {
            dealDamage(ref, !isAttack(ref), damage_type);
        }

        // if
        // (damage_type.isMagical()) {
        // damageDealt = magicalDamage(targetObj, ref, amount);
        // } else
        // damageDealt = physicalDamage(targetObj, attacker, ref);
        // if not killed, this should be zero, right?

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

    private static boolean isAttack(Ref ref) {
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            return true;
        }
        return false;
    }

    private static boolean processDamageEvent(DAMAGE_TYPE damage_type, Ref ref, int amount,
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

    public static int dealPureDamage(Unit attacked, Unit attacker, Integer e_damage,
                                     Integer t_damage, Ref ref) {
        // apply Absorption here?

        boolean enduranceOnly = false;
        if (t_damage == null) {
            enduranceOnly = true;
            t_damage = 0;
        }
        // TODO if not started already
        attacked.getGame().getLogManager().newLogEntryNode(true, ENTRY_TYPE.DAMAGE);

        LogMaster.log(1, t_damage + " / " + e_damage + " damage being dealt to "
                + attacked.toString());
        attacked.getGame().getLogManager().logDamageDealt(t_damage, e_damage, attacker, attacked);

        ref.setTarget(attacked.getId());
        ref.setSource(attacker.getId());

        Event event;
        boolean result;
        int actual_t_damage = 0;

        if (t_damage > 0) {
            if (!enduranceOnly) {

                event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_TOUGHNESS_DAMAGE, ref);
                ref.setAmount(t_damage);
                result = event.fire();
                if (DC_GameManager.checkInterrupted(ref)) {
                    return 0;
                }
                t_damage = ref.getAmount(); // triggers may have changed the
                // amount!
                actual_t_damage = Math.min(attacked.getIntParam(PARAMS.C_TOUGHNESS), t_damage);
                ref.setAmount(actual_t_damage); // for cleave and other
                // sensitive
                // effects
                // int t_dmg_remaining = actual_t_damage
                // - attacked.getIntParam(PARAMS.C_TOUGHNESS);
                if (result) {
                    attacked.modifyParameter(PARAMS.C_TOUGHNESS, -t_damage);
                }
                event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE, ref);
                result = event.fire();

            }
        }

        int actual_e_damage = 0;
        if (e_damage > 0) {
            ref.setAmount(e_damage);
            event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_DEALT_ENDURANCE_DAMAGE, ref);

            result = event.fire();
            if (DC_GameManager.checkInterrupted(ref)) {
                attacked.getGame().getLogManager().doneLogEntryNode();
                return 0;
            }

            e_damage = ref.getAmount();
            actual_e_damage = Math.min(attacked.getIntParam(PARAMS.C_ENDURANCE), e_damage);
            ref.setAmount(actual_e_damage);

            // DamageAnimation animation = new DamageAnimation(ref);
            // attacked.getGame().getAnimationManager().newAnimation(animation);

            // int e_dmg_remaining = actual_e_damage
            // - attacked.getIntParam(PARAMS.C_ENDURANCE);
            if (result) {
                attacked.modifyParameter(PARAMS.C_ENDURANCE, -e_damage);
            }
            event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_DEALT_ENDURANCE_DAMAGE, ref);
            result = event.fire();

        }
        int damageDealt = Math.max(actual_e_damage, actual_t_damage);

        boolean dead = checkDead(attacked);
        try {
            // TODO old anim
            // attacked.getGame().getAnimationManager().damageDealt(t_damage,
            // e_damage, ref,
            // DAMAGE_TYPE.PURE, dead);

            PhaseAnimation animation = getAnimation(ref, attacked);
            if (animation != null) {
                animation.addPhaseArgs(PHASE_TYPE.DAMAGE_DEALT, t_damage, e_damage, ref
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
        LogMaster.log(1, t_damage + " / " + e_damage + " damage has been dealt to "
                + attacked.toString());
        attacked.getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.DAMAGE, attacked,
                damageDealt);

        // ++ volume proportional to
        // health lost?
        // if (actual_e_damage > 0 || actual_t_damage > 0)
        // SoundMaster.playHitSound(attacked);
        return damageDealt;
    }

    private static boolean isPeriodic(Ref ref) {
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_SOURCE), GenericEnums.DAMAGE_MODIFIER.PERIODIC
                .toString());
    }

    private static int calculateToughnessDamage(Unit attacked, Unit attacker,
                                                int base_damage, boolean magical, Ref ref, int blocked, DAMAGE_TYPE damage_type) {
        // if (attacker.hasVorpal())
        // return base_damage;

        return calculateDamage(false, attacked, attacker, base_damage, magical, ref, blocked,
                damage_type);
    }

    private static int calculateDamage(boolean endurance, Unit attacked, Unit attacker,
                                       int base_damage, boolean magical, Ref ref, int blocked, DAMAGE_TYPE damage_type) {

        if (!endurance) {
            if (isPeriodic(ref)) {
                // PhaseAnimation animation = getAttackAnimation(ref, attacked);
                // animation.addPhaseArgs(PHASE_TYPE.REDUCTION_NATURAL, 0, 0,
                // 0);
                return 0;
            }
        }

        int i = base_damage - blocked;
        int resistance = getResistanceForDamageType(i, attacked, attacker, damage_type);
        i = applyPercentReduction(i, resistance);
        int armor = ArmorMaster.getArmorValue(attacked, damage_type);
        PhaseAnimation animation = magical ? getActionAnimation(ref, attacker) : getAttackAnimation(ref,
                attacked);
        if (animation != null) {
            if (resistance != 0 || armor != 0) {
                animation.addPhaseArgs(PHASE_TYPE.REDUCTION_NATURAL, armor, resistance, base_damage
                        - blocked);
            }
        }
        return Math.max(0, i - armor);
    }

    private static int calculateEnduranceDamage(Unit attacked, Unit attacker,
                                                int base_damage, boolean magical, Ref ref, int blocked, DAMAGE_TYPE damage_type) {
        return calculateDamage(true, attacked, attacker, base_damage, magical, ref, blocked,
                damage_type);
    }

    private static int getArmorReduction(int base_damage, Unit attacked, Unit attacker,
                                         DC_ActiveObj action) {
        return getArmorReduction(base_damage, attacked, attacker, action, false);
    }

    private static int applyAverageShieldReduction(int amount, Unit attacked,
                                                   Unit attacker, DC_ActiveObj action, DC_WeaponObj weapon, DAMAGE_TYPE damage_type) {
        if (!attacked.getGame().getArmorSimulator().checkCanShieldBlock(action, attacked)) {
            return 0;
        }
        int blocked = attacked.getGame().getArmorSimulator().getShieldDamageBlocked(amount,
                attacked, attacker, action, weapon, damage_type);

        return blocked;
    }

    private static int getArmorReduction(int base_damage, Unit attacked, Unit attacker,
                                         DC_ActiveObj action, boolean simulation) {
        // if (attacked.checkPassive(STANDARD_PASSIVES.IMMATERIAL)
        // || attacker.checkPassive(STANDARD_PASSIVES.IMMATERIAL))
        // return applySpellArmorReduction(base_damage, attacked, attacker);
        if (attacked.getArmor() == null) {
            return 0;
        }
        int blocked = (simulation ? attacked.getGame().getArmorSimulator() : attacked.getGame()
                .getArmorMaster()).getArmorBlockDamage(base_damage, attacked, attacker, action);

        // [OUTDATED] int armor_pen =
        // attacker.getIntParam(PARAMS.ARMOR_PENETRATION);
        // int armor_mod = attacker.getIntParam(PARAMS.ARMOR_MOD);
        // if (action != null) {
        // armor_pen += action.getIntParam(PARAMS.ARMOR_PENETRATION);
        // armor_mod *= action.getIntParam(PARAMS.ARMOR_MOD);
        // armor_mod /= 100;
        // }
        // int armor = attacked.getIntParam(PARAMS.ARMOR);
        // if (attacker.getIntParam(PARAMS.ARMOR_MOD) != 0) {
        // armor = Math.round(armor * armor_mod / 100);
        // }
        // if (armor > 0)
        // armor = Math.max(0, armor - armor_pen);

        return Math.min(base_damage, blocked);
    }

    public static int getDamage(Attack attack) {
        Unit attacked = attack.getAttacked();
        Unit attacker = attack.getAttacker();
        if (!attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.SNEAK_IMMUNE)) {
            attack.setSneak(DC_AttackMaster.checkSneak(attack.getRef()));
        }
        // TODO ref.setFuture(true) -> average dice, auto-reset action etc
        int amount = attack.getPrecalculatedDamageAmount();
        DAMAGE_TYPE dmg_type = attack.getDamageType();
        if (dmg_type == GenericEnums.DAMAGE_TYPE.PURE || dmg_type == GenericEnums.DAMAGE_TYPE.POISON) {
            return amount;
        }
        amount -= applyAverageShieldReduction(amount, attacked, attacker, attack.getAction(),
                attack.getWeapon(), attack.getDamageType());
        amount -= getArmorReduction(amount, attacked, attacker, attack.getAction(), true);

        // amount = applyResistanceTypeReduction(amount, attacked, attacker,
        // dmg_type);
        // if (dmg_type.isMagical()) {
        // amount = applySpellArmorReduction(amount, attacked, attacker);
        // }
        if (attack.getAction().isAttack()) {
            return amount;
        }
        return amount;
    }

    public static boolean checkDead(BattleFieldObject unit) {
        if (unit instanceof Unit) {
            return UnconsciousRule.checkUnitDies((Unit) unit);
        }
        if (0 >= unit.getIntParam(PARAMS.C_ENDURANCE)) {
            return true;
        }
        if (0 >= unit.getIntParam(PARAMS.C_TOUGHNESS)) {
            return true;
        }
        return false;
    }

    public static boolean isLethal(int damage, Obj targetObj) {
        if (damage >= targetObj.getIntParam(PARAMS.C_TOUGHNESS)) {
            return true;
        }
        if (damage >= targetObj.getIntParam(PARAMS.C_ENDURANCE)) {
            return true;
        }
        return false;
    }

}
