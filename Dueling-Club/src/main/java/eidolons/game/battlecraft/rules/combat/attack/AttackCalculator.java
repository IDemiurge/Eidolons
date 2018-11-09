package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.rules.action.WatchRule;
import eidolons.game.battlecraft.rules.perk.RangeRule;
import eidolons.system.DC_Formulas;
import eidolons.system.graphics.DC_ImageMaster;
import main.content.ContentValsManager;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttackCalculator {
    Attack attack;
    Map<MOD_IDENTIFIER, Integer> bonusMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> modMap = new XLinkedMap<>();

    Map<MOD_IDENTIFIER, Integer> posMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> atkMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> extraMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> randomMap = new XLinkedMap<>();
    Map<PARAMETER, Integer> weaponMap = new XLinkedMap<>();
    Map<PARAMETER, Integer> actionMap = new XLinkedMap<>();

    Map<MOD_IDENTIFIER, Integer> weaponModMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> actionModMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> posModMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> atkModMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> defModMap = new XLinkedMap<>();
    Map<MOD_IDENTIFIER, Integer> extraModMap = new XLinkedMap<>();

    Map<PARAMETER, Integer> subMap = new XLinkedMap<>();
    List<Integer> dieList = new ArrayList<>();

    // Map<PARAMETER, String> calcMap;
    boolean precalc;
    DC_AttackMaster master;
    private DC_ActiveObj action;
    private Unit attacker;
    private BattleFieldObject attacked;
    private DC_WeaponObj weapon;
    private boolean counter;
    private boolean offhand;
    private boolean critical;
    private boolean sneak;
    private Ref ref;
    private Integer amount;
    private boolean AoO;
    private boolean instant;
    private boolean disengage;
    private boolean min;
    private boolean max;

    public AttackCalculator(Attack attack, boolean precalc) {
        this.attack = attack;
        this.action = attack.getAction();
        this.attacker = attack.getAttacker();
        this.attacked = attack.getAttacked();
        this.weapon = attack.getWeapon();
        this.disengage = attack.isDisengagement();
        this.counter = attack.isCounter();
        this.instant = attack.isInstant();
        this.AoO = attack.isAttackOfOpportunity();
        this.offhand = attack.isOffhand();
        this.sneak = attack.isSneak();
        this.critical = attack.isCritical();
        this.ref = action.getRef();
        this.precalc = precalc;
        amount = 0;
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

        if (CoreEngine.isPhaseAnimsOn())
            if (!precalc  ) {
                 //TODO
            }
        attack.setDamage(amount);
        return amount;
    }


    private void initAllModifiers() {
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
                modMap.put(MOD_IDENTIFIER.THROW, mod - 100);
            }
            mod = attacker.getIntParam(PARAMS.THROW_ATTACK_MOD);
            if (mod != 0) {
                atk_mod += mod - 100;
                atkMap.put(MOD_IDENTIFIER.THROW, mod - 100);
            }
        }

        action.modifyParameter(PARAMS.IMPACT_AREA, weapon.getIntParam(PARAMS.IMPACT_AREA));

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


    private Integer getAttackDefenseDamageBonus(Attack attack, Integer amount, Unit attacker,
                                                BattleFieldObject attacked, DC_ActiveObj action, boolean offhand) {
        int attackValue = DefenseVsAttackRule.getAttackValue(offhand, attacker, attacked, action);
        // TODO
        int defense = DefenseVsAttackRule.getDefenseValue(attacker, attacked, action);
        int diff = attackValue - defense;
        boolean negative = false;
        if (diff < 0) {
            negative = true;
        }
        diff = Math.abs(diff);
        float mod = (!negative) ? DC_Formulas.ATTACK_DMG_INCREASE
         : DC_Formulas.DEFENSE_DMG_DECREASE;

        float limit = (negative) ? DC_Formulas.ATTACK_DMG_INCREASE_LIMIT
         : DC_Formulas.DEFENSE_DMG_DECREASE_LIMIT;
        diff = Math.round(Math.min(limit, diff));
        if (negative) {
            diff = -diff;
        }
        int bonus = Math.round(amount * (mod * diff) / 100);
        if (CoreEngine.isPhaseAnimsOn())
            if (!precalc) {
                 //TODO
            }
        // TODO atkMap - where are the atk mods from? - sneak, position, ...
        bonusMap.put(MOD_IDENTIFIER.ATK_DEF, bonus);
        return bonus;
    }

    private Integer getCriticalDamageBonus(Attack attack, Integer amount, Unit attacker,
                                           BattleFieldObject attacked, DC_ActiveObj action, boolean offhand) {
        if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.CRITICAL_IMMUNE)) {
            return 0;
        }
        int mod = CriticalAttackRule.
         getCriticalDamagePercentage(action, attacked);

        int bonus = MathMaster.applyMod(amount, mod);
        bonusMap.put(MOD_IDENTIFIER.CRIT, bonus);
        return bonus;
    }

    private int getAttributeDamageBonuses(Obj obj, Unit ownerObj,
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

    private int getDamageModifier(PARAMS dmgModifier, PARAMS value, Unit ownerObj, Obj obj) {
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
    private Integer applyDamageBonuses() {
        amount = attacker.getIntParam(PARAMS.BASE_DAMAGE);
        bonusMap.put(MOD_IDENTIFIER.UNIT, amount);
        int totalBonus = 0;
        int totalMod = 0;
        // DICE
        Integer bonus = rollDice();
        bonusMap.put(MOD_IDENTIFIER.RANDOM, bonus);
        totalBonus += bonus;
        // FORCE
//        bonus = ForceRule.getDamage(action, attacker, attacked);
//        bonusMap.put(MOD_IDENTIFIER.FORCE, bonus);
//        totalBonus += bonus;

        bonus = action.getIntParam(PARAMS.DAMAGE_BONUS);
        bonus += getAttributeDamageBonuses(action, attacker,
         actionMap);
        bonusMap.put(MOD_IDENTIFIER.ACTION, bonus);
        totalBonus += bonus;

        // ACTION
        int mod = action.getIntParam(PARAMS.DAMAGE_MOD) - 100;
        if (mod > 0) {
            if (mod >= 100) {
                mod -= 100;
            }

        } // TODO [QUICK FIX] - why 50 becomes 150?!
        modMap.put(MOD_IDENTIFIER.ACTION, mod);
        totalMod += mod;


        // WEAPON
        mod = weapon.getIntParam(PARAMS.DAMAGE_MOD) - 100;
        modMap.put(MOD_IDENTIFIER.WEAPON, mod);
        totalMod += mod;
        bonus = weapon.getIntParam(PARAMS.DAMAGE_BONUS);
        bonus += getAttributeDamageBonuses(weapon, weapon.getOwnerObj(),
           weaponMap);
        weaponMap.put(PARAMS.DAMAGE_BONUS, weapon.getIntParam(PARAMS.DAMAGE_BONUS));

        bonusMap.put(MOD_IDENTIFIER.WEAPON, bonus);
        totalBonus += bonus;
        // POSITION
        if (modMap.get(MOD_IDENTIFIER.POS) != null) {
            totalMod += modMap.get(MOD_IDENTIFIER.POS);
        }
        // EXTRA - already initialized!
        if (modMap.get(MOD_IDENTIFIER.EXTRA_ATTACK) != null) {
            totalMod += modMap.get(MOD_IDENTIFIER.EXTRA_ATTACK);
        }
        bonus = bonusMap.get(MOD_IDENTIFIER.AMMO);
        if (bonus != null)
            totalBonus += bonus;

        bonus = getAttackDefenseDamageBonus(attack, totalBonus, attacker, attacked, action,
         offhand);
        bonusMap.put(MOD_IDENTIFIER.ATK_DEF, bonus);
        if (critical) {
            bonus /= 3 * 2;
            bonus += getCriticalDamageBonus(attack, totalBonus, attacker, attacked, action, offhand);
            bonusMap.put(MOD_IDENTIFIER.CRIT, bonus);
        }
        // bonusMap.put() inside
        totalBonus += bonus;
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
        Integer dieSize = weapon.getIntParam(PARAMS.DIE_SIZE);
        Integer dieNumber = weapon.getIntParam(PARAMS.DICE);
        new MapMaster<MOD_IDENTIFIER, Integer>().addToIntegerMap(randomMap,
         MOD_IDENTIFIER.DIE_SIZE, dieSize);
        new MapMaster<MOD_IDENTIFIER, Integer>().addToIntegerMap(randomMap,
         MOD_IDENTIFIER.DIE_NUMBER, dieNumber);

        Integer result = null;
        if (max) {
            result = dieSize * dieNumber;
        } else if (min) {
            result = dieNumber;
        }
        if (result == null) result =
         RandomWizard.initDice(dieNumber, dieSize, dieList, precalc);

        new MapMaster<MOD_IDENTIFIER, Integer>().addToIntegerMap(randomMap,
         MOD_IDENTIFIER.DIE_RESULT, result);

        return result;
    }

    private void initializeActionModifiers() {
        int dmg_bonus = getAttributeDamageBonuses(action, attacker,
         new XLinkedMap<>());
        Integer actionDmgMod = action.getIntParam(PARAMS.DAMAGE_MOD);
        int dmg_mod = offhand ? attacker.getIntParam(PARAMS.OFFHAND_DAMAGE_MOD) - 100
         + actionDmgMod : actionDmgMod;
        Integer actionAtkMod = action.getIntParam(PARAMS.ATTACK_MOD);
        int atk_mod = offhand ? attacker.getIntParam(PARAMS.OFFHAND_ATTACK_MOD) - 100
         + actionAtkMod : actionAtkMod;
        int armor_pen = action.getIntParam(PARAMS.ARMOR_PENETRATION);
        int armor_mod = action.getIntParam(PARAMS.ARMOR_MOD);


        if (action.isThrow()) {
            atk_mod = applyVisibilityPenalty(atk_mod);
        } else if (action.isRanged()) {
            atk_mod = applyVisibilityPenalty(atk_mod);
            Obj ranged = action.getOwnerUnit().getRef().getObj(KEYS.RANGED);
            if (ranged == null) {
                return; //TODO ??
            }
            DC_Obj ammo = ((DC_WeaponObj) ranged).getAmmo();
            if (ammo != null) {
                action.modifyParameter(PARAMS.IMPACT_AREA, ammo.getIntParam(PARAMS.IMPACT_AREA));
                // weapon. //IN DC_WEAPONOBJ
                // addSpecialEffect(SPECIAL_EFFECTS_CASE.ON_ATTACK,
                // ammo.getSpecialEffects().getOrCreate(SPECIAL_EFFECTS_CASE.ON_ATTACK));
                int bonus = ammo.getIntParam(PARAMS.DAMAGE_BONUS) + rollDice(ammo);
                bonusMap.put(MOD_IDENTIFIER.AMMO, bonus);
                dmg_bonus += bonus;
                dmg_mod = dmg_mod * ammo.getIntParam(PARAMS.DAMAGE_MOD) / 100;
                armor_pen += ammo.getIntParam(PARAMS.ARMOR_PENETRATION);
                armor_mod = armor_mod * ammo.getIntParam(PARAMS.ARMOR_MOD) / 100;
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
        action.modifyParameter(PARAMS.ARMOR_MOD, armor_mod);

        modMap.put(MOD_IDENTIFIER.ACTION, dmg_mod);
        bonusMap.put(MOD_IDENTIFIER.ACTION, dmg_bonus);

    }

    private int applyVisibilityPenalty(int atk_mod) {
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
        atkModMap.put(MOD_IDENTIFIER.SIGHT_RANGE, penalty);
        return atk_mod;
    }

    private int applyParamMod(int amount, PARAMS param) {
        amount = MathMaster.applyModOrFactor(amount, attacker, (param));
        amount = MathMaster.applyModIfNotZero(amount, action.getIntParam(param));
        amount = MathMaster.applyModIfNotZero(amount, weapon.getIntParam(param));
        return amount;
    }

    private void initializePositionModifiers() {
        int dmg_mod = 0;
        int atk_mod = 0;
        Boolean close_long = RangeRule.isCloseQuartersOrLongReach(attacker, attacked, weapon,
         action);
        if (close_long != null) {
            MOD_IDENTIFIER identifier = close_long ? MOD_IDENTIFIER.CLOSE_QUARTERS
             : MOD_IDENTIFIER.LONG_REACH;
            int damageMod = RangeRule.getMod(true, close_long, attacker, attacked, weapon, action);
            posMap.put(identifier, damageMod);
            dmg_mod += damageMod;

            int atkMod = RangeRule.getMod(false, close_long, attacker, attacked, weapon, action);
            atkMap.put(identifier, atkMod);
            atk_mod += atkMod;
        }
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
                posMap.put(MOD_IDENTIFIER.DIAGONAL_ATTACK, diagonalMod);
            }
            diagonalMod = action.getIntParam(PARAMS.DIAGONAL_DAMAGE_MOD) - 100;
            if (diagonalMod != 0 && diagonalMod != -100) {
                // action.getModsMap().put("Diagonal Attack", diagonalMod);
                dmg_mod += diagonalMod;
                posMap.put(MOD_IDENTIFIER.DIAGONAL_ATTACK, diagonalMod);
            }
            // TODO DO FOR WEAPONS AND MAKE A SUB MAP!
        } else if (FacingMaster.getSingleFacing(action.getOwnerUnit(), (BfObj) ref
         .getTargetObj()) == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
            Integer sideMod = action.getIntParam(PARAMS.SIDE_ATTACK_MOD) - 100;
            if (sideMod != 0 && sideMod != -100) {
                atk_mod += sideMod;
                modMap.put(MOD_IDENTIFIER.POS, sideMod); // TODO
                posMap.put(MOD_IDENTIFIER.SIDE_ATTACK, sideMod);
            }
            sideMod = action.getIntParam(PARAMS.SIDE_DAMAGE_MOD) - 100;
            if (sideMod != 0 && sideMod != 100) {
                // generic?
                modMap.put(MOD_IDENTIFIER.POS, sideMod);
                dmg_mod += sideMod;
                posMap.put(MOD_IDENTIFIER.SIDE_ATTACK, sideMod);
            }
        }
        if (sneak) {
            int rangedMod = 100;
            if (action.isRanged()) {
                rangedMod = attacker.getIntParam(PARAMS.SNEAK_RANGED_MOD);
            }
            if (rangedMod != 0) {
                if (action.getGame().getBattleMaster().getOptionManager().
                 getDifficulty().isEnemySneakAttacksOn()){
                    dmg_mod += initSneakMods(action, rangedMod);
                    dmg_mod += initSneakMods(weapon, rangedMod);
                }
            }

        }
        // addToCalcMap(PARAMS. ATTACK_MOD, atkMod);
        // addToCalcMap(PARAMS. DAMAGE_MOD, dmg_mod);
        if (dmg_mod != 0) {
            modMap.put(MOD_IDENTIFIER.POS, dmg_mod);
        }

        action.modifyParameter(PARAMS.ATTACK_MOD, atk_mod, false);
        // action.modifyParameter(PARAMS.DAMAGE_MOD, dmg_mod, false);
    }

    private void addToMap(Map<PARAMETER, Integer> calcMap, PARAMETER param, int mod) {
        Integer value = calcMap.get(param);
        if (value != null) {
            value += mod;
        } else {
            value = mod;
        }
        calcMap.put(param, value);
        LogMaster.log(1, " ");
    }

    private void addModifier(PARAMS param, int mod) {
        action.modifyParameter(param, mod - 100, false);
    }

    private void addParameter(PARAMS param, int mod) {
        action.modifyParameter(param, mod, false);
    }

    private void initializeAttackModifiers() {
        Integer integer = weapon.getIntParam(PARAMS.ATTACK_MOD);
        addModifier(atkModMap, MOD_IDENTIFIER.WEAPON, PARAMS.ATTACK, integer);

//      TODO   if (attacked.isEngagedWith(attacker)) {
//            integer = weapon.getIntParam(PARAMS.ATTACK_MOD);
//            addModifier(atkModMap, MOD_IDENTIFIER.WEAPON, PARAMS.ATTACK, integer);
//        }
        if (AoO) {
            integer = attacker.getIntParam(PARAMS.AOO_ATTACK_MOD);
            addModifier(atkModMap, MOD_IDENTIFIER.AOO, PARAMS.ATTACK, integer);
        }
        if (instant) {
            integer = attacker.getIntParam(PARAMS.INSTANT_ATTACK_MOD);
            addModifier(atkModMap, MOD_IDENTIFIER.INSTANT_ATTACK, PARAMS.ATTACK, integer);
            if (WatchRule.checkWatched(attacker, attacked)) {
                addModifier(atkModMap, MOD_IDENTIFIER.WATCHED, PARAMS.ATTACK, MathMaster
                 .applyModIfNotZero(MathMaster.applyModIfNotZero(
                  WatchRule.INSTANT_ATTACK_MOD, attacker
                   .getIntParam(PARAMS.WATCH_ATTACK_MOD)), attacked
                  .getIntParam(PARAMS.WATCHED_ATTACK_MOD))

                );

                // addModifier(attacked, map, id, param, integer);
            }
        }
        if (counter) {
            integer = attacker.getIntParam(PARAMS.COUNTER_ATTACK_MOD);
            addModifier(atkModMap, MOD_IDENTIFIER.COUNTER_ATTACK, PARAMS.ATTACK, integer);
        }
        if (attack.getInstantAttackType() != null) {
            PARAMETER param = ContentValsManager.getPARAM(attack.getInstantAttackType().toString()
             + "_ATTACK_MOD");
            integer = attacked.getIntParam(param);
            addModifier(atkModMap, MOD_IDENTIFIER.INSTANT_ATTACK, PARAMS.ATTACK, integer);
        }
    }

    private void initializeDefenseModifiers() {
        Integer integer;
        if (AoO) {
            integer = attacked.getIntParam(PARAMS.AOO_DEFENSE_MOD);
            addModifier(attacked, defModMap, MOD_IDENTIFIER.AOO, PARAMS.DEFENSE, integer);
        }
        if (instant) {
            integer = attacked.getIntParam(PARAMS.INSTANT_DEFENSE_MOD);
            addModifier(attacked, defModMap, MOD_IDENTIFIER.INSTANT_ATTACK, PARAMS.DEFENSE, integer);
        }
        if (counter) {
            integer = attacked.getIntParam(PARAMS.COUNTER_DEFENSE_MOD);
            addModifier(attacked, defModMap, MOD_IDENTIFIER.COUNTER_ATTACK, PARAMS.DEFENSE, integer);
        }

        if (disengage) {
            PARAMETER param = ContentValsManager.getPARAM(attack.getInstantAttackType().toString()
             + "_DEFENSE_MOD");
            integer = attacked.getIntParam(param);
            addModifier(attacked, defModMap, MOD_IDENTIFIER.INSTANT_ATTACK, PARAMS.DEFENSE, integer);
        }
        // if ( engaged){
        // integer = attacked.getIntParam(PARAMS.ENGAGED_ATTACK_MOD);
        // addModifier(defModMap, MOD_IDENTIFIER.ENGAGED, PARAMS.DEFENSE,
        // integer);
        // }

    }

    private void addModifier(Map<MOD_IDENTIFIER, Integer> map, MOD_IDENTIFIER id, PARAMS param,
                             Integer integer) {
        addModifier(action, map, id, param, integer);
    }

    private void addModifier(Entity e, Map<MOD_IDENTIFIER, Integer> map, MOD_IDENTIFIER id,
                             PARAMS param, Integer integer) {
        map.put(id, integer);
        e.modifyParamByPercent(param, integer);
    }

    private void initializeExtraModifiers() {
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
            mod = MathMaster.applyMod(mod, weapon.getIntParam(param));
            extraMap.put(MOD_IDENTIFIER.WEAPON, weapon.getIntParam(param));
        }
        mod = MathMaster.applyModIfNotZero(mod, action.getIntParam(param));
        mod = MathMaster.applyModIfNotZero(mod, attacker.getIntParam(param));
        // mod += weapon.getIntParam(param) - 100;
        // mod += action.getIntParam(param) - 100;
        extraMap.put(MOD_IDENTIFIER.ACTION, action.getIntParam(param));
        // mod += attacker.getIntParam(param) - 100;
        extraMap.put(MOD_IDENTIFIER.UNIT, attacker.getIntParam(param));
        mod -= 100;
        modMap.put(MOD_IDENTIFIER.EXTRA_ATTACK, mod);
    }

    private void initializeRandomModifiers() {
        // ??
    }

    private void initializeWeaponModifiers() {
        if (weapon == null) {
            return;
        }
        int armor_pen = weapon.getIntParam(PARAMS.ARMOR_PENETRATION);
        int armor_mod = weapon.getIntParam(PARAMS.ARMOR_MOD);
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

        addModifier(PARAMS.ARMOR_MOD, armor_mod);
        addModifier(PARAMS.ATTACK_MOD, atk_mod);
        addModifier(PARAMS.DAMAGE_MOD, dmg_mod);

        modMap.put(MOD_IDENTIFIER.WEAPON, dmg_mod);
        atkMap.put(MOD_IDENTIFIER.WEAPON, atk_mod);
    }

    private int     initSneakMods(Obj obj, int rangedMod) {
        int mod = obj.getIntParam(PARAMS.SNEAK_ATTACK_MOD) * rangedMod / 100;
        MapMaster.addToIntegerMap(atkMap, MOD_IDENTIFIER.SNEAK, mod);
        addModifier(PARAMS.ATTACK_MOD, mod);
        int dmg_mod = obj.getIntParam(PARAMS.SNEAK_DAMAGE_MOD) * rangedMod / 100;
        MapMaster.addToIntegerMap(posMap, MOD_IDENTIFIER.SNEAK, dmg_mod);
        addModifier(PARAMS.DAMAGE_MOD, dmg_mod);
        // TODO add up Mod and Bonus for AtkMap!
        mod = obj.getIntParam(PARAMS.SNEAK_ATTACK_BONUS) * rangedMod / 100;
        addParameter(PARAMS.ATTACK_BONUS, mod);
        mod = obj.getIntParam(PARAMS.SNEAK_DAMAGE_BONUS) * rangedMod / 100;
        addParameter(PARAMS.DAMAGE_BONUS, mod);
        // bonusMap.put(key, value);
        mod = obj.getIntParam(PARAMS.SNEAK_ARMOR_MOD);
        addModifier(PARAMS.ARMOR_MOD, mod);
        mod = obj.getIntParam(PARAMS.SNEAK_ARMOR_PENETRATION);
        addParameter(PARAMS.ARMOR_PENETRATION, mod);

        mod = obj.getIntParam(PARAMS.SNEAK_DEFENSE_MOD);
        if (mod > 0) {
            Integer param = attacked.getIntParam(PARAMS.SNEAK_PROTECTION);
            if (param != 0) {
                mod = mod * 100 / param;
            }
            if (rangedMod != 0) {
                mod = mod * 100 / rangedMod;
            }
            mod = -mod;
        }
        addModifier(PARAMS.DEFENSE_MOD, mod);
        mod = obj.getIntParam(PARAMS.SNEAK_DEFENSE_PENETRATION) * rangedMod / 100;
        addParameter(PARAMS.DEFENSE_PENETRATION, mod);
        return dmg_mod;
    }

    public void setMin(boolean min) {
        this.min = min;
    }

    public void setMax(boolean max) {
        this.max = max;
    }

    public enum MOD_IDENTIFIER {
        ATK_DEF,
        CRIT("ui/value icons/identifiers/CRIT.jpg"),
        ACTION,
        WEAPON,
        UNIT,
        EXTRA_ATTACK,
        RANDOM,
        AMMO,
        FORCE,

        POS("ui/value icons/identifiers/POS.png"),
        SNEAK("ui/value icons/identifiers/SNEAK.jpg"),
        CLOSE_QUARTERS("ui/value icons/identifiers/CLOSE_QUARTERS.jpg"),
        LONG_REACH("ui/value icons/identifiers/LONG_REACH.jpg"),
        DIAGONAL_ATTACK("ui/value icons/identifiers/DIAGONAL_ATTACK.png"),
        SIDE_ATTACK("ui/value icons/identifiers/SIDE_ATTACK.png"),

        ARMOR("ui/value icons/identifiers/ARMOR.jpg"),
        RESISTANCE("ui/value icons/identifiers/RESISTANCE.jpg"),
        DIE_SIZE("ui/value icons/identifiers/DIE_SIZE.png"),
        DIE_NUMBER("ui/value icons/identifiers/DIE_NUMBER.png"),
        DIE_RESULT,
        THROW(STD_IMAGES.THROW.getPath()),
        INSTANT_ATTACK(STD_IMAGES.INSTANT_ATTACK.getPath()),
        AOO(STD_IMAGES.ATTACK_OF_OPPORTUNITY.getPath()),
        COUNTER_ATTACK(STD_IMAGES.COUNTER_ATTACK.getPath()),
        DISENGAGEMENT,
        WATCHED(STD_IMAGES.EYE.getPath()),
        SIGHT_RANGE(STD_IMAGES.EYE.getPath());
        private String imagePath;

        MOD_IDENTIFIER() {

        }

        MOD_IDENTIFIER(String path) {
            this.imagePath = path;
        }

        public String getImagePath() {
            return imagePath;
        }

        public Image getImage(Object... values) {
            if (imagePath == null) {
                try {
                    return DC_ImageMaster.getImageDynamic(this, values);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    return ImageManager.getImage(ImageManager.getEmptyListIconSmall());
                }
            }
            return ImageManager.getImage(imagePath);
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

    }
}
