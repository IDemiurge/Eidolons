package eidolons.system.math;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.netherflame.eidolon.heromake.model.PointMaster;
import eidolons.content.DC_Formulas;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.system.math.MathMaster;

public class DC_MathManager extends MathMaster {

    public static final int DEFAULT_FREE_MASTERY_COUNT = 2;

    public DC_MathManager(GenericGame game) {
        this.game = game;
    }

    public static Integer getStartingFocus(Obj obj) {
        return obj.getIntParam(PARAMS.STARTING_FOCUS);
        // return MathMaster.applyMod(obj.getIntParam(PARAMS.STARTING_FOCUS),
        //         obj.getIntParam(PARAMS.ORGANIZATION));
    }


    public static int getDurabilityForDamage(int damage, int armor, OBJ_TYPE TYPE) {
        if (TYPE == DC_TYPE.WEAPONS) {
            return getWeaponDurabilityForDamage(damage, armor);
        } else if (TYPE == DC_TYPE.ARMOR) {
            return getArmorDurabilityForDamage(damage, armor);
        }
        return 0;
    }

    private static int getArmorDurabilityForDamage(int damage, int armor) {
        if (damage < DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_ARMOR) {
            return 0;
        }

        int max = damage;
        if (damage > armor) {
            max = armor;
        }

        return 1 + MathMaster.round(max - DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_ARMOR)
                / DC_Formulas.DURABILITY_DAMAGE_FACTOR_ARMOR;
    }

    private static int getWeaponDurabilityForDamage(int damage, int armor) {
        if (armor < DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_WEAPON) {
            armor = DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_WEAPON;
        }
        int max = armor;
        if (damage < armor) {
            max = damage;
        }

        return 1 + MathMaster.round(max - DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_WEAPON)
                / DC_Formulas.DURABILITY_DAMAGE_FACTOR_WEAPON;

    }
    public static int getDurabilityForDamage(int damage, int armor, HeroItem item) {
        // armor penetration
        // action ref
        return 0;
    }

    public static int getDamageTypeResistance(BattleFieldObject attacked, DAMAGE_TYPE type) {

        if (type == null) {
            return 0;
        }
        if (type == GenericEnums.DAMAGE_TYPE.PHYSICAL) {
            return MathMaster.getAverage(attacked.getIntParam(PARAMS.SLASHING_RESISTANCE),
                    attacked.getIntParam(PARAMS.PIERCING_RESISTANCE), attacked.getIntParam(PARAMS.SLASHING_RESISTANCE));
        }
        // AETHER/ASTRAL -> average of all elemental/astral TODO
        if (type == GenericEnums.DAMAGE_TYPE.PURE) {
            return 0;
        }
        if (!type.isMagical() && attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            type = GenericEnums.DAMAGE_TYPE.MAGICAL;
        }
        if (type == GenericEnums.DAMAGE_TYPE.MAGICAL) {
            return attacked.getIntParam(PARAMS.RESISTANCE);
        }
        return attacked.getIntParam(DC_ContentValsManager.getDamageTypeResistance(type));
    }


    public static int getMasteryPoints(Unit hero, PARAMETER masteryParam) {
        int pts = 0;
        for (int i = 0; i <= hero.getIntParam(masteryParam); i++) {
            pts += PointMaster.getPointCost(i, hero, masteryParam);
        }
        return pts;
    }

    public static int getParamPercentage(Unit unit, PARAMETER p) {
        return unit.getIntParam(ContentValsManager.getPercentageParam(p));
    }

    public static int getLeveledUnitPowerBonus(ObjType type) {
        int level = DC_Formulas.getLevelForXp(type.getIntParam(PARAMS.POWER) * DC_Formulas.POWER_XP_FACTOR);
        // MathManager.getFormulaArg("amount",
        return calculateFormula(DC_Formulas.UNIT_LEVEL_POWER_BONUS, level);

    }

    @Override
    public Integer getStartingEssence(Obj obj) {
        return obj.getIntParam(PARAMS.ESSENCE) * DC_Formulas.STARTING_ESSENCE_PERCENTAGE / 100;
    }

}
