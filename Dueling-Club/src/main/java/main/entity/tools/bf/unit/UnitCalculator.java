package main.entity.tools.bf.unit;

import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityCalculator;
import main.entity.tools.EntityMaster;
import main.entity.type.ObjType;
import main.game.battlefield.attack.DC_AttackMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.DC_MathManager;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitCalculator extends EntityCalculator<Unit> {
    public UnitCalculator(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public UnitInitializer getInitializer() {
        return (UnitInitializer) super.getInitializer();
    }

    @Override
    public UnitChecker getChecker() {
        return (UnitChecker) super.getChecker();
    }

    @Override
    public UnitResetter getResetter() {
        return (UnitResetter) super.getResetter();
    }

    public int calculateRemainingMemory() {
        int memory = getIntParam(PARAMS.MEMORIZATION_CAP) - calculateMemorizationPool();
        setParam(PARAMS.MEMORY_REMAINING, memory);
        return memory;
    }

    public int calculateMemorizationPool() {
        int memory = 0;
        for (ObjType type : DataManager.toTypeList(StringMaster
         .openContainer(getProperty(PROPS.MEMORIZED_SPELLS)), DC_TYPE.SPELLS)) {
            memory += type.getIntParam(PARAMS.SPELL_DIFFICULTY);
        }
        return memory;
    }

    // @Deprecated
    public int calculatePower() {
        if (getChecker().isBfObj()) {
            return 0; // TODO into new class!
        }

        int power = DC_MathManager.getUnitPower(getEntity());
        if (!getChecker().isHero()) {
            if (power != 0) {
                return 0;
            }
        }
        setParam(PARAMS.POWER, power);
        // GetParam(PARAMS.UNIT_LEVEL, power);
        return power;
    }

    public int calculateWeight() {
        int weight = calculateCarryingWeight();
        setParam(PARAMS.C_CARRYING_WEIGHT, weight);
        int result = getIntParam(PARAMS.CARRYING_CAPACITY) - weight;
        weight += getIntParam(PARAMS.WEIGHT);
        setParam(PARAMS.TOTAL_WEIGHT, weight);
        return result;

    }


    public Integer calculateAndSetDamage(boolean offhand) {
        return calculateDamage(offhand, true);
    }

    public Integer calculateDamage(boolean offhand) {
        return calculateDamage(offhand, false);
    }

    public Integer calculateDamage(boolean offhand, boolean set) {
        int dmg = DC_AttackMaster.getUnitAttackDamage(getEntity(), offhand);
        Integer mod;
        mod = getIntParam((offhand) ? PARAMS.OFFHAND_DAMAGE_MOD : PARAMS.DAMAGE_MOD);
        if (mod != 0) {
            dmg = dmg * mod / 100;
        }
        PARAMS minDamage = (offhand) ? PARAMS.OFF_HAND_MIN_DAMAGE : PARAMS.MIN_DAMAGE;

        if (set)
            setParam(minDamage, dmg);
        PARAMS damage = (offhand) ? PARAMS.OFF_HAND_DAMAGE : PARAMS.DAMAGE;
        DC_WeaponObj weapon = getEntity().getWeapon(offhand);
        if (weapon == null) {
            weapon = getEntity().getNaturalWeapon(offhand);
        }
        Integer dieSize = (weapon == null) ? getIntParam(PARAMS.DIE_SIZE) : weapon
         .getIntParam(PARAMS.DIE_SIZE);

        if (mod != 0) {
            dieSize = dieSize * mod / 100;
        }
        if (set)
            setParam(damage, MathMaster.getAverage(dmg, dmg + dieSize));
        PARAMS maxDamage = (offhand) ? PARAMS.OFF_HAND_MAX_DAMAGE : PARAMS.MAX_DAMAGE;
        if (set)
            setParam(maxDamage, dmg + dieSize);
        return MathMaster.getAverage(dmg, dmg + dieSize);
    }

    // Java 8 collections methods?
    public int calculateCarryingWeight() {
        int weight = 0;
        if (getEntity().getMainWeapon() != null) {
            weight += getEntity().getMainWeapon().getIntParam(PARAMS.WEIGHT);
        }
        if (getEntity().getSecondWeapon() != null) {
            weight += getEntity().getSecondWeapon().getIntParam(PARAMS.WEIGHT);
        }
        if (getEntity().getArmor() != null) {
            weight += getEntity().getArmor().getIntParam(PARAMS.WEIGHT);
        }

        if (game.isSimulation()) {
            for (ObjType type : DataManager.toTypeList(StringMaster
             .openContainer(getProperty(PROPS.INVENTORY)), C_OBJ_TYPE.ITEMS)) {
                weight += type.getIntParam(PARAMS.WEIGHT);
            }
            for (ObjType type : DataManager.toTypeList(StringMaster
             .openContainer(getProperty(PROPS.QUICK_ITEMS)), C_OBJ_TYPE.ITEMS)) {
                weight += type.getIntParam(PARAMS.WEIGHT);
            }
        } else {

            for (DC_HeroItemObj item : getEntity().getInventory()) {
                weight += item.getIntParam(PARAMS.WEIGHT);
            }
            for (DC_HeroItemObj item : getEntity().getQuickItems()) {
                weight += item.getIntParam(PARAMS.WEIGHT);
            }
        }
        return weight;

    }
}