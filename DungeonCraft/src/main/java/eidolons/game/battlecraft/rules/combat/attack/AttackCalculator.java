package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.system.math.roll.DiceMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.NewRpgEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.Map;

import static main.content.enums.entity.NewRpgEnums.HitType.hit;

/**
 * make it into a generic basis? Use methods selectively?
 * <p>
 * the idea was also to have some data model to DISPLAY the ATK properly
 */
public class AttackCalculator {
    Map<ActionEnums.MOD_IDENTIFIER, Integer> bonusMap = new XLinkedMap<>();
    Map<ActionEnums.MOD_IDENTIFIER, Integer> modMap = new XLinkedMap<>();

    Map<ActionEnums.MOD_IDENTIFIER, Integer> posMap = new XLinkedMap<>();
    Map<ActionEnums.MOD_IDENTIFIER, Integer> atkMap = new XLinkedMap<>();
    Map<ActionEnums.MOD_IDENTIFIER, Integer> extraMap = new XLinkedMap<>();
    Map<PARAMETER, Integer> weaponMap = new XLinkedMap<>();
    Map<PARAMETER, Integer> actionMap = new XLinkedMap<>();

    Map<ActionEnums.MOD_IDENTIFIER, Integer> atkModMap = new XLinkedMap<>();
    Map<ActionEnums.MOD_IDENTIFIER, Integer> defModMap = new XLinkedMap<>();

    Map<PARAMETER, Integer> subMap = new XLinkedMap<>();
    boolean precalc;

    protected final Attack attack;
    protected final DC_ActiveObj action;
    protected final Unit attacker;
    protected BattleFieldObject attacked;
    protected final WeaponItem weapon;
    protected final Ref ref;
    protected final boolean counter, offhand, critical, sneak, AoO, instant ;

    protected Integer amount;
    protected boolean min, max;
    protected NewRpgEnums.HitType hitType;

    public AttackCalculator(Attack attack, boolean precalc) {
        this.attack = attack;
        this.action = attack.getAction();
        this.attacker = attack.getAttacker();
        this.attacked = attack.getAttacked();
        this.weapon = attack.getWeapon();
        this.counter = attack.isCounter();
        this.instant = attack.isInstant();
        this.AoO = attack.isAttackOfOpportunity();
        this.offhand = attack.isOffhand();
        this.sneak = attack.isSneak();
        this.critical = attack.isCritical();
        this.ref = action.getRef();
        this.precalc = precalc;
        amount = 0;
        hitType = attack.getHitType();
    }

    public AttackCalculator initTarget(Unit target) {
        attacked = target;
        return this;
    }

    public int calculateFinalDamage() {
        action.toBase();
        if (offhand) {
            LogMaster.log(1, attack + " (offhand) with damage: " + bonusMap);
        }
        initAllModifiers();
        amount = applyDamageBonuses();

        attack.setDamage(amount);
        return amount;
    }


    protected void initAllModifiers() {
        initializeWeaponModifiers();
        initializeActionModifiers();
        initializePositionModifiers();
        initializeExtraModifiers();
        initializeAttackModifiers();
        initializeDefenseModifiers();
        initializeRandomModifiers();

        int atk_mod = 100;
        int dmg_mod = 100;
        if (action.isThrow()) {
            Integer mod = attacker.getIntParam(PARAMS.THROW_DAMAGE_MOD);
            if (mod != 0) {
                dmg_mod += mod - 100;
                modMap.put(ActionEnums.MOD_IDENTIFIER.THROW, mod - 100);
            }
            mod = attacker.getIntParam(PARAMS.THROW_ATTACK_MOD);
            if (mod != 0) {
                atk_mod += mod - 100;
                atkMap.put(ActionEnums.MOD_IDENTIFIER.THROW, mod - 100);
            }
        }

        // TODO we don't do this - instead, let's use "calcMap" with PARAMETER,
        // String
        // use it
        action.multiplyParamByPercent(PARAMS.ATTACK_MOD, atk_mod, false);
        action.multiplyParamByPercent(PARAMS.DAMAGE_MOD, dmg_mod, false);
        // action.modifyParameter(PARAMS.DAMAGE_BONUS, dmg_bonus);
        // action.multiplyParamByPercent(PARAMS.ARMOR_MOD, armor_mod, false);
        // action.modifyParameter(PARAMS.ARMOR_PENETRATION, armor_pen);
        // defense mod?!

    }


    protected Integer getDamageForHitType(Attack attack, Integer amount, Unit attacker,
                                        BattleFieldObject attacked, DC_ActiveObj action, boolean offhand) {
        int mod = HitTypeRule.getDamagePercentage(action, attacked, attack.getHitType());
        //TODO anything else?
        return mod;
    }

    protected int getAttributeDamageBonuses(Obj obj, Unit ownerObj,
                                          Map<PARAMETER, Integer> map) {
        int result = 0;
        subMap = map;
        result += getDamageModifier(PARAMS.STR_DMG_MODIFIER, PARAMS.STRENGTH, ownerObj, obj);
        result += getDamageModifier(PARAMS.AGI_DMG_MODIFIER, PARAMS.AGILITY, ownerObj, obj);
        result += getDamageModifier(PARAMS.INT_DMG_MODIFIER, PARAMS.INTELLIGENCE, ownerObj, obj
        );
        result += getDamageModifier(PARAMS.SP_DMG_MODIFIER, PARAMS.SPELLPOWER, ownerObj, obj);
        return result;
    }

    protected int getDamageModifier(PARAMS dmgModifier, PARAMS value, Unit ownerObj, Obj obj) {
        int amount = obj.getIntParam(dmgModifier) * ownerObj.getIntParam(value) / 100;
        // map = animation.getPhase(type).getArgs()[0];
        if (amount != 0) {
            subMap.put(value, amount);
        }
        return (amount);
    }

    //TODO final formula:
    // amount = (base_damage + totalBonus)*mod
    // totalBonus = rollDice + force + attributeDamageBonuses
    // totalMod = action/weapon/unit dmg_mod
    protected Integer applyDamageBonuses() {
        amount = attacker.getIntParam(PARAMS.BASE_DAMAGE);
        bonusMap.put(ActionEnums.MOD_IDENTIFIER.UNIT, amount);
        int totalBonus = 0;
        int totalMod = 0;
        // DICE
        Integer bonus = rollDice();
        bonusMap.put(ActionEnums.MOD_IDENTIFIER.RANDOM, bonus);
        totalBonus += bonus;
        // FORCE
        //        bonus = ForceRule.getDamage(action, attacker, attacked);
        //        bonusMap.put(MOD_IDENTIFIER.FORCE, bonus);
        //        totalBonus += bonus;

        bonus = action.getIntParam(PARAMS.DAMAGE_BONUS);
        bonus += getAttributeDamageBonuses(action, attacker,
                actionMap);
        bonusMap.put(ActionEnums.MOD_IDENTIFIER.ACTION, bonus);
        totalBonus += bonus;

        // ACTION
        int mod = action.getIntParam(PARAMS.DAMAGE_MOD) - 100;
        if (mod > 0) {
            if (mod >= 100) {
                mod -= 100;
            }

        } // TODO [QUICK FIX] - why 50 becomes 150?!
        modMap.put(ActionEnums.MOD_IDENTIFIER.ACTION, mod);
        totalMod += mod;


        // WEAPON
        mod = weapon.getIntParam(PARAMS.DAMAGE_MOD) - 100;
        modMap.put(ActionEnums.MOD_IDENTIFIER.WEAPON, mod);
        totalMod += mod;

        if (hitType != hit) {
            bonus = getDamageForHitType(attack, totalBonus, attacker, attacked, action, offhand);
            bonusMap.put(ActionEnums.MOD_IDENTIFIER.HIT_TYPE, bonus);
            totalMod += mod;
        }


        bonus = weapon.getIntParam(PARAMS.DAMAGE_BONUS);
        bonus += getAttributeDamageBonuses(weapon, weapon.getOwnerObj(),
                weaponMap);
        weaponMap.put(PARAMS.DAMAGE_BONUS, weapon.getIntParam(PARAMS.DAMAGE_BONUS));

        bonusMap.put(ActionEnums.MOD_IDENTIFIER.WEAPON, bonus);
        totalBonus += bonus;
        // POSITION
        if (modMap.get(ActionEnums.MOD_IDENTIFIER.POS) != null) {
            totalMod += modMap.get(ActionEnums.MOD_IDENTIFIER.POS);
        }
        // EXTRA - already initialized!
        if (modMap.get(ActionEnums.MOD_IDENTIFIER.EXTRA_ATTACK) != null) {
            totalMod += modMap.get(ActionEnums.MOD_IDENTIFIER.EXTRA_ATTACK);
        }
        bonus = bonusMap.get(ActionEnums.MOD_IDENTIFIER.AMMO);
        if (bonus != null)
            totalBonus += bonus;

        // bonusMap.put() inside
        // then add modMap and bonusMap to getOrCreate final bonus! useful to know what
        // part of it was % though
        // TODO MODIFIER NOW DISPLAYED SEPARATED!
        // for (MOD_IDENTIFIER id : bonusMap.keySet()) {
        // bonus = bonusMap.getOrCreate(id);
        // bonus += bonus * totalMod / 100;
        // if (modMap.getOrCreate(id) != null)
        // bonus += (amount - bonus) * modMap.getOrCreate(id) / 100;
        // bonusMap.put(id, bonus);
        // }
        for (Integer sub : posMap.values())
            totalMod += sub;

        amount += totalBonus;
        amount += amount * totalMod / 100;

        // ATK/DEF/CRIT - MUST BE LAST???

        // TODO refactor so that there a method for each that will getOrCreate bonus/mod
        // and build subMap for anim page... the issue is that we also need to
        // keep it real!..

        return amount;
    }

    public Integer rollDice(DC_Obj object) {
        return rollDice(); // TODO second map?
    }

    public Integer rollDice() {
        // add from hero?
        if (weapon == null) {
            return 0;
        }
        // TODO +action?
        GenericEnums.DieType die = weapon.getDieType();
        Integer dieNumber =
                DiceMaster.getDefaultDieNumber(attack.getAttacker()) +
                        weapon.getIntParam(PARAMS.DICE);

        int sValue = DiceMaster.roll(die, attack.getAttacker(), dieNumber, false);
        int tValue = DiceMaster.roll(die, attack.getAttacked(),
                DiceMaster.getDefaultDieNumber(attack.getAttacked()), false);
        int result = sValue - tValue;
        // mapDieRoll()

        return result;
    }

    @Deprecated
    /*
    ATK TYPES and SPELL TYPES
     */
    protected void initializeActionModifiers() {
        int dmg_bonus = getAttributeDamageBonuses(action, attacker,
                new XLinkedMap<>());
        Integer actionDmgMod = action.getIntParam(PARAMS.DAMAGE_MOD);
        int dmg_mod = offhand ? attacker.getIntParam(PARAMS.OFFHAND_DAMAGE_MOD) - 100
                + actionDmgMod : actionDmgMod;
        Integer actionAtkMod = action.getIntParam(PARAMS.ATTACK_MOD);
        int atk_mod = offhand ? attacker.getIntParam(PARAMS.OFFHAND_ATTACK_MOD) - 100
                + actionAtkMod : actionAtkMod;
        int armor_pen = action.getIntParam(PARAMS.ARMOR_PENETRATION);

        if (action.isThrow()) {
            atk_mod = applyVisibilityPenalty(atk_mod);
        } else if (action.isRanged()) {
            atk_mod = applyVisibilityPenalty(atk_mod);
            Obj ranged = action.getOwnerUnit().getRef().getObj(KEYS.RANGED);
            if (ranged == null) {
                return; //TODO ??
            }
            DC_Obj ammo = ((WeaponItem) ranged).getAmmo();
            if (ammo != null) {
                // weapon. //IN DC_WEAPONOBJ
                // addSpecialEffect(SPECIAL_EFFECTS_CASE.ON_ATTACK,
                // ammo.getSpecialEffects().getOrCreate(SPECIAL_EFFECTS_CASE.ON_ATTACK));
                int bonus = ammo.getIntParam(PARAMS.DAMAGE_BONUS) + rollDice(ammo);
                bonusMap.put(ActionEnums.MOD_IDENTIFIER.AMMO, bonus);
                dmg_bonus += bonus;
                dmg_mod = dmg_mod * ammo.getIntParam(PARAMS.DAMAGE_MOD) / 100;
                armor_pen += ammo.getIntParam(PARAMS.ARMOR_PENETRATION);
                ref.setValue(KEYS.DAMAGE_TYPE, ammo.getProperty(PROPS.DAMAGE_TYPE));
            }
        }
        // actionMap.put(PARAMS.DAMAGE_MOD, dmg_mod);
        // actionMap.put(PARAMS.ATTACK, atk_mod); TODO
        // if (armor_pen != 0)
        // actionMap.put(PARAMS.ARMOR_PENETRATION, armor_pen);
        // actionMap.put(PARAMS.ARMOR_MOD, armor_mod);
        // action.modifyParameter(PARAMS.DAMAGE_MOD, dmg_mod); DAMAGE WILL BE
        // COUNTED LAST!
        action.modifyParameter(PARAMS.ATTACK, atk_mod);
        action.modifyParameter(PARAMS.ARMOR_PENETRATION, armor_pen);

        modMap.put(ActionEnums.MOD_IDENTIFIER.ACTION, dmg_mod);
        bonusMap.put(ActionEnums.MOD_IDENTIFIER.ACTION, dmg_bonus);

    }

    protected int applyVisibilityPenalty(int atk_mod) {
        int penalty = 0;
        if (attacked.getUnitVisionStatus() == UNIT_VISION.IN_SIGHT) {
            // as opposed to IN_PLAIN_SIGHT
            penalty = applyParamMod(-20, (PARAMS.RANGED_PENALTY_MOD));
        } else if (attacked.getUnitVisionStatus() == UNIT_VISION.BEYOND_SIGHT) {
            penalty = applyParamMod(-35, (PARAMS.RANGED_PENALTY_MOD));
        } else if (attacked.getUnitVisionStatus() == UNIT_VISION.CONCEALED) {
            penalty = applyParamMod(-50, (PARAMS.RANGED_PENALTY_MOD));
        }

        atk_mod = MathMaster.applyModIfNotZero(atk_mod, penalty);
        atkModMap.put(ActionEnums.MOD_IDENTIFIER.SIGHT_RANGE, penalty);
        return atk_mod;
    }

    protected int applyParamMod(int amount, PARAMS param) {
        amount = MathMaster.applyModOrFactor(amount, attacker, (param));
        amount = MathMaster.applyModIfNotZero(amount, action.getIntParam(param));
        amount = MathMaster.applyModIfNotZero(amount, weapon.getIntParam(param));
        return amount;
    }

    protected void initializePositionModifiers() {
        int dmg_mod = 0;
        int atk_mod = 0;
        if (ref.getTargetObj() == null) {
            if (attack.getAttacked() != null) {
                ref.setTarget(attack.getAttacked().getId());
            } else {
                return;
            }
        }
        if (PositionMaster.inLineDiagonally(action.getOwnerUnit().getCoordinates(), ref
                .getTargetObj().getCoordinates())) {
            Integer diagonalMod = action.getIntParam(PARAMS.DIAGONAL_ATTACK_MOD) - 100;
            if (diagonalMod != 0 && diagonalMod != -100) {
                // action.getModsMap().put("Diagonal Attack", diagonalMod);
                atk_mod += diagonalMod;
                posMap.put(ActionEnums.MOD_IDENTIFIER.DIAGONAL_ATTACK, diagonalMod);
            }
            diagonalMod = action.getIntParam(PARAMS.DIAGONAL_DAMAGE_MOD) - 100;
            if (diagonalMod != 0 && diagonalMod != -100) {
                // action.getModsMap().put("Diagonal Attack", diagonalMod);
                dmg_mod += diagonalMod;
                posMap.put(ActionEnums.MOD_IDENTIFIER.DIAGONAL_ATTACK, diagonalMod);
            }
            // TODO DO FOR WEAPONS AND MAKE A SUB MAP!
        }
        if (sneak) {
            int rangedMod = 100;
            if (action.isRanged()) {
                rangedMod = attacker.getIntParam(PARAMS.SNEAK_RANGED_MOD);
            }
            if (rangedMod != 0) {
                if (action.getGame().getMissionMaster().getOptionManager().
                        getDifficulty().isEnemySneakAttacksOn()) {
                    dmg_mod += initSneakMods(action, rangedMod);
                    dmg_mod += initSneakMods(weapon, rangedMod);
                }
            }
        }
        // addToCalcMap(PARAMS. ATTACK_MOD, atkMod);
        // addToCalcMap(PARAMS. DAMAGE_MOD, dmg_mod);
        if (dmg_mod != 0) {
            modMap.put(ActionEnums.MOD_IDENTIFIER.POS, dmg_mod);
        }

        action.modifyParameter(PARAMS.ATTACK_MOD, atk_mod, false);
        // action.modifyParameter(PARAMS.DAMAGE_MOD, dmg_mod, false);
    }

    protected void addToMap(Map<PARAMETER, Integer> calcMap, PARAMETER param, int mod) {
        Integer value = calcMap.get(param);
        if (value != null) {
            value += mod;
        } else {
            value = mod;
        }
        calcMap.put(param, value);
        LogMaster.log(1, " ");
    }

    protected void addModifier(PARAMS param, int mod) {
        action.modifyParameter(param, mod - 100, false);
    }

    protected void addParameter(PARAMS param, int mod) {
        action.modifyParameter(param, mod, false);
    }

    protected void addModifier(Map<ActionEnums.MOD_IDENTIFIER, Integer> map, ActionEnums.MOD_IDENTIFIER id, PARAMS param,
                             Integer integer) {
        addModifier(action, map, id, param, integer);
    }

    protected void addModifier(Entity e, Map<ActionEnums.MOD_IDENTIFIER, Integer> map, ActionEnums.MOD_IDENTIFIER id,
                             PARAMS param, Integer integer) {
        map.put(id, integer);
        e.modifyParamByPercent(param, integer);
    }


    protected void initializeAttackModifiers() {
        Integer integer = weapon.getIntParam(PARAMS.ATTACK_MOD);
        addModifier(atkModMap, ActionEnums.MOD_IDENTIFIER.WEAPON, PARAMS.ATTACK, integer);

        //      TODO   if (attacked.isEngagedWith(attacker)) {
        //            integer = weapon.getIntParam(PARAMS.ATTACK_MOD);
        //            addModifier(atkModMap, MOD_IDENTIFIER.WEAPON, PARAMS.ATTACK, integer);
        //        }
        if (AoO) {
            integer = attacker.getIntParam(PARAMS.AOO_ATTACK_MOD);
            addModifier(atkModMap, ActionEnums.MOD_IDENTIFIER.AOO, PARAMS.ATTACK, integer);
        }
        if (instant) {
            integer = attacker.getIntParam(PARAMS.INSTANT_ATTACK_MOD);
            addModifier(atkModMap, ActionEnums.MOD_IDENTIFIER.INSTANT_ATTACK, PARAMS.ATTACK, integer);
        }
        if (counter) {
            integer = attacker.getIntParam(PARAMS.COUNTER_ATTACK_MOD);
            addModifier(atkModMap, ActionEnums.MOD_IDENTIFIER.COUNTER_ATTACK, PARAMS.ATTACK, integer);
        }
    }

    protected void initializeDefenseModifiers() {
        Integer integer;
        if (AoO) {
            integer = attacked.getIntParam(PARAMS.AOO_DEFENSE_MOD);
            addModifier(attacked, defModMap, ActionEnums.MOD_IDENTIFIER.AOO, PARAMS.DEFENSE, integer);
        }
        if (instant) {
            integer = attacked.getIntParam(PARAMS.INSTANT_DEFENSE_MOD);
            addModifier(attacked, defModMap, ActionEnums.MOD_IDENTIFIER.INSTANT_ATTACK, PARAMS.DEFENSE, integer);
        }
        if (counter) {
            integer = attacked.getIntParam(PARAMS.COUNTER_DEFENSE_MOD);
            addModifier(attacked, defModMap, ActionEnums.MOD_IDENTIFIER.COUNTER_ATTACK, PARAMS.DEFENSE, integer);
        }


    }


    protected void initializeExtraModifiers() {
        Integer mod = 100;
        PARAMS param = null;

        if (counter) {
            param = PARAMS.COUNTER_MOD;
        }
        if (AoO) {
            // watch bonuses here!
            param = PARAMS.AOO_DAMAGE_MOD;
        }
        if (instant) {
            param = PARAMS.INSTANT_DAMAGE_MOD;
        }
        if (param == null) {
            return;
        }
        if (weapon != null) {
            mod = MathMaster.applyPercent(mod, weapon.getIntParam(param));
            extraMap.put(ActionEnums.MOD_IDENTIFIER.WEAPON, weapon.getIntParam(param));
        }
        mod = MathMaster.applyModIfNotZero(mod, action.getIntParam(param));
        mod = MathMaster.applyModIfNotZero(mod, attacker.getIntParam(param));
        // mod += weapon.getIntParam(param) - 100;
        // mod += action.getIntParam(param) - 100;
        extraMap.put(ActionEnums.MOD_IDENTIFIER.ACTION, action.getIntParam(param));
        // mod += attacker.getIntParam(param) - 100;
        extraMap.put(ActionEnums.MOD_IDENTIFIER.UNIT, attacker.getIntParam(param));
        mod -= 100;
        modMap.put(ActionEnums.MOD_IDENTIFIER.EXTRA_ATTACK, mod);
    }

    protected void initializeRandomModifiers() {
        // ??
    }

    protected void initializeWeaponModifiers() {
        if (weapon == null) {
            return;
        }
        int armor_pen = weapon.getIntParam(PARAMS.ARMOR_PENETRATION);
        int atk_mod = weapon.getIntParam(PARAMS.ATTACK_MOD);
        int dmg_mod = weapon.getIntParam(PARAMS.DAMAGE_MOD);
        addParameter(PARAMS.ARMOR_PENETRATION, armor_pen);
        if (action.isThrow()) {
            Integer mod = weapon.getIntParam(PARAMS.THROW_DAMAGE_MOD);
            if (mod != 0) {
                dmg_mod += mod - 100;
            }
            mod = weapon.getIntParam(PARAMS.THROW_ATTACK_MOD);
            if (mod != 0) {
                atk_mod += mod - 100;
            }
            // modMap
        }

        addModifier(PARAMS.ATTACK_MOD, atk_mod);
        addModifier(PARAMS.DAMAGE_MOD, dmg_mod);

        modMap.put(ActionEnums.MOD_IDENTIFIER.WEAPON, dmg_mod);
        atkMap.put(ActionEnums.MOD_IDENTIFIER.WEAPON, atk_mod);
    }

    //TODO rpg Review
    protected int initSneakMods(Obj obj, int rangedMod) {
        int mod = obj.getIntParam(PARAMS.SNEAK_ATTACK_MOD) * rangedMod / 100;
        MapMaster.addToIntegerMap(atkMap, ActionEnums.MOD_IDENTIFIER.SNEAK, mod);
        addModifier(PARAMS.ATTACK_MOD, mod);
        int dmg_mod = obj.getIntParam(PARAMS.SNEAK_DAMAGE_MOD) * rangedMod / 100;
        MapMaster.addToIntegerMap(posMap, ActionEnums.MOD_IDENTIFIER.SNEAK, dmg_mod);
        addModifier(PARAMS.DAMAGE_MOD, dmg_mod);
        // TODO add up Mod and Bonus for AtkMap!
        mod = obj.getIntParam(PARAMS.SNEAK_ATTACK_BONUS) * rangedMod / 100;
        addParameter(PARAMS.ATTACK_BONUS, mod);
        mod = obj.getIntParam(PARAMS.SNEAK_DAMAGE_BONUS) * rangedMod / 100;
        addParameter(PARAMS.DAMAGE_BONUS, mod);
        // bonusMap.put(key, value);
        addModifier(PARAMS.DEFENSE_MOD, mod);

        mod = obj.getIntParam(PARAMS.SNEAK_DEFENSE_PENETRATION)
                - obj.getIntParam(PARAMS.SNEAK_PROTECTION) * rangedMod / 100;
        addParameter(PARAMS.DEFENSE_PENETRATION, mod);
        return dmg_mod;
    }

    public void setMin(boolean min) {
        this.min = min;
    }

    public void setMax(boolean max) {
        this.max = max;
    }

    public void setHitType(NewRpgEnums.HitType hitType) {
        this.hitType = hitType;
    }
}
