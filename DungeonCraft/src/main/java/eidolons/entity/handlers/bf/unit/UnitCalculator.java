package eidolons.entity.handlers.bf.unit;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.core.game.DC_Game;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.handlers.EntityCalculator;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
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

    public int calculateWeight() {
        int weight = calculateCarryingWeight();
        setParam(PARAMS.C_CARRYING_WEIGHT, weight);
        int result = getIntParam(PARAMS.CARRYING_CAPACITY) - weight;
        weight += getIntParam(PARAMS.WEIGHT);
        setParam(PARAMS.TOTAL_WEIGHT, weight);
        return result;

    }

    @Deprecated
    public Integer calculateAndSetDamage(boolean offhand) {
        return calculateDamage(offhand, true);
    }

    @Deprecated
    public Integer calculateDamage(boolean offhand) {
        return calculateDamage(offhand, false);
    }

    @Deprecated
    public Integer calculateDamage(boolean offhand, boolean set) {
        int dmg = DamageCalculator.getUnitAttackDamage(getEntity(), offhand);
        Integer mod;
        mod = getIntParam((offhand) ? PARAMS.OFFHAND_DAMAGE_MOD : PARAMS.DAMAGE_MOD);
        if (mod != 0) {
            dmg = dmg * mod / 100;
        }
        PARAMS minDamage = (offhand) ? PARAMS.OFF_HAND_MIN_DAMAGE : PARAMS.MIN_DAMAGE;

        if (set) {
            setParam(minDamage, dmg);
        }
        PARAMS damage = (offhand) ? PARAMS.OFF_HAND_DAMAGE : PARAMS.DAMAGE;
        WeaponItem weapon = getEntity().getWeapon(offhand);
        if (weapon == null) {
            weapon = getEntity().getNaturalWeapon(offhand);
        }
        Integer dieSize = (weapon == null) ? getIntParam(PARAMS.DIE_SIZE) : weapon
         .getIntParam(PARAMS.DIE_SIZE);

        if (mod != 0) {
            dieSize = dieSize * mod / 100;
        }
        if (set) {
            setParam(damage, MathMaster.getAverage(dmg, dmg + dieSize));
        }
        PARAMS maxDamage = (offhand) ? PARAMS.OFF_HAND_MAX_DAMAGE : PARAMS.MAX_DAMAGE;
        if (set) {
            setParam(maxDamage, dmg + dieSize);
        }

        if (isDiceAccountedElsewhere()) { //TODO review this
            Integer min = getIntParam(minDamage);
            setParameter(damage, min);
            setParameter(maxDamage, min);
            return min;
        }

        return MathMaster.getAverage(dmg, dmg + dieSize);
    }

    private boolean isDiceAccountedElsewhere() {
        return true;
    }

    // Java 8 collections methods?
    public int calculateCarryingWeight() {
        if (getEntity().getInventory()==null )
            return 0;
        int weight = 0;
        if (getEntity().getMainWeapon() != null) {
            weight += getEntity().getMainWeapon().getIntParam(PARAMS.WEIGHT);
        }
        if (getEntity().getOffhandWeapon() != null) {
            weight += getEntity().getOffhandWeapon().getIntParam(PARAMS.WEIGHT);
        }
        if (getEntity().getArmor() != null) {
            weight += getEntity().getArmor().getIntParam(PARAMS.WEIGHT);
        }

        if (false) {
//        TODO when is this necessary?    if (!getEntity().isItemsInitialized()) {
            for (ObjType type : DataManager.toTypeList(ContainerUtils
             .openContainer(getProperty(PROPS.INVENTORY)), C_OBJ_TYPE.ITEMS)) {
                weight += type.getIntParam(PARAMS.WEIGHT);
            }
            for (ObjType type : DataManager.toTypeList(ContainerUtils
             .openContainer(getProperty(PROPS.QUICK_ITEMS)), C_OBJ_TYPE.ITEMS)) {
                weight += type.getIntParam(PARAMS.WEIGHT);
            }
        } else {

            for (HeroItem item : getEntity().getInventory()) {
                weight += item.getIntParam(PARAMS.WEIGHT);
            }
            for (HeroItem item : getEntity().getQuickItems()) {
                weight += item.getIntParam(PARAMS.WEIGHT);
            }
        }
        return weight;

    }


    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

}
