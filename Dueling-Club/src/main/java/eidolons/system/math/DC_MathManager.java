package eidolons.system.math;

import eidolons.client.cc.logic.PointMaster;
import eidolons.content.DC_ContentManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.DC_Formulas;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.List;

public class DC_MathManager extends MathMaster {

    public static final int DEFAULT_FREE_MASTERY_COUNT = 2;

    public DC_MathManager(MicroGame game) {
        this.game = game;
    }

    public static Integer getStartingFocus(Obj obj) {
        return MathMaster.applyMod(obj.getIntParam(PARAMS.STARTING_FOCUS), obj.getIntParam(PARAMS.ORGANIZATION));
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

        int reduction = 1 + MathMaster.round(max - DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_ARMOR)
         / DC_Formulas.DURABILITY_DAMAGE_FACTOR_ARMOR;
        return reduction;
    }

    private static int getWeaponDurabilityForDamage(int damage, int armor) {
        if (armor < DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_WEAPON) {
            return 0;
        }
        int max = armor;
        if (damage < armor) {
            max = damage;
        }

        int reduction = 1 + MathMaster.round(max - DC_Formulas.DURABILITY_DAMAGE_THRESHOLD_WEAPON)
         / DC_Formulas.DURABILITY_DAMAGE_FACTOR_WEAPON;
        return reduction;

    }

    public static int getDurabilityForDamage(int damage, int armor, DC_HeroItemObj item) {
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
        return attacked.getIntParam(DC_ContentManager.getDamageTypeResistance(type));
    }

    public static int getBuyAttributeXpCost(Entity hero) {
        return DC_Formulas.BUY_ATTRIBUTE_XP_COST + hero.getIntParam(PARAMS.ATTR_BOUGHT_WITH_XP);
    }

    public static int getBuyAttributeGoldCost(Entity hero) {
        return DC_Formulas.BUY_ATTRIBUTE_GOLD_COST + 2 * hero.getIntParam(PARAMS.ATTR_BOUGHT_WITH_GOLD);
    }

    public static int getBuyMasteryGoldCost(Entity hero) {
        return DC_Formulas.BUY_MASTERY_GOLD_COST + 2 * hero.getIntParam(PARAMS.MASTERY_BOUGHT_WITH_GOLD);
    }

    public static int getBuyMasteryXpCost(Entity hero) {
        return DC_Formulas.BUY_MASTERY_XP_COST + hero.getIntParam(PARAMS.MASTERY_BOUGHT_WITH_XP);
    }

    public static int getBuyCost(boolean attr, boolean gold, Entity hero) {
        if (attr) {
            return (gold) ? getBuyAttributeGoldCost(hero) : getBuyAttributeXpCost(hero);
        }
        return (gold) ? getBuyMasteryGoldCost(hero) : getBuyMasteryXpCost(hero);

    }

    public static List<PARAMETER> getUnlockedMasteries(Entity entity) {
        List<PARAMETER> list = new ArrayList<>();
        for (PARAMS p : DC_ContentManager.getMasteryParams()) {
            if (isMasteryUnlocked(entity, p)) {
                list.add(p);
            }
        }
        return list;
    }

    public static boolean isMasteryUnlocked(Entity entity, PARAMETER p) {
        return entity.getIntParam(p, true) > 0;
    }

    public static int getMasteriesUnlocked(Entity entity) {
        return getUnlockedMasteries(entity).size();
    }

    public static int getMasteryDiscount(Entity entity) {
        return entity.getIntParam(PARAMS.FREE_MASTERIES);
    }

    public static int getFreeMasteryPoints(Unit hero, PARAMETER masteryParam) {
        return getMasteryPoints(hero, masteryParam) - calculateUsedMasteryPoints(hero, masteryParam);
    }

    public static int getMasteryPoints(Unit hero, PARAMETER masteryParam) {
        int pts = 0;
        for (int i = 0; i <= hero.getIntParam(masteryParam); i++) {
            pts += PointMaster.getPointCost(i, hero, masteryParam);
        }
        return pts;
    }

    public static int calculateUsedMasteryPoints(Unit hero, PARAMETER masteryParam) {
        int points = 0;
        for (DC_FeatObj skill : hero.getSkills()) {
            if (StringMaster.compare(skill.getProperty(G_PROPS.MASTERY), masteryParam.getName(), true)) {
                points += skill.getIntParam(PARAMS.SKILL_DIFFICULTY);
            }
        }
        return points;

    }

    public static int getSkillXpCost(Integer skillDifficulty) {
        return DC_Formulas.calculateFormula(DC_Formulas.XP_COST_PER_SKILL_DIFFICULTY, skillDifficulty);

    }

    public static int getMaxDivinationSD(Unit hero) {
        return DC_Formulas.DIVINATION_MAX_SD_FORMULA.getInt(hero.getRef());
    }

    public static int getDivinationPool(Unit hero) {
        Formula divinationPoolFormula = DC_Formulas.DIVINATION_POOL_FORMULA;
        String property = hero.getProperty(PROPS.DIVINATION_PARAMETER);
        if (!property.isEmpty()) {
            if (!property.contains("{")) {
                property = StringMaster.wrapInCurlyBraces(property);
            }

            divinationPoolFormula = new Formula(DC_Formulas.DIVINATION_POOL_FORMULA.toString().toLowerCase()
             .replace("{charisma}", hero.getProperty(PROPS.DIVINATION_PARAMETER)));
        }
        return divinationPoolFormula.getInt(hero.getRef());
    }

    public static int getUnitPower(Entity unit) {
        return DC_Formulas.getPowerFromUnitXP(unit.getIntParam(PARAMS.TOTAL_XP));

    }

    public static String getUnitXP(ObjType type) {
        return "" + type.getIntParam(PARAMS.POWER) * 10
         // + DC_Formulas
         // .getTotalXpForLevel(type.getIntParam(PARAMS.LEVEL))

         ;
    }

    public static int getParamPercentage(Unit unit, PARAMETER p) {
        return unit.getIntParam(ContentManager.getPercentageParam(p));
    }

    public static String getSpellXpCost(ObjType type) {

        return "" + (type.getIntParam(PARAMS.SPELL_DIFFICULTY) * DC_Formulas.XP_COST_PER_SPELL_DIFFICULTY);
    }

    public static int getLeveledUnitPowerBonus(ObjType type) {
        int level = DC_Formulas.getLevelForXp(type.getIntParam(PARAMS.POWER) * DC_Formulas.POWER_XP_FACTOR);
        // MathManager.getFormulaArg("amount",

        return calculateFormula(DC_Formulas.UNIT_LEVEL_POWER_BONUS, level);

    }

    public static int getLevelForPower(Integer power) {
        return DC_Formulas.getLevelForXp(power * DC_Formulas.POWER_XP_FACTOR);
    }

    @Override
    public Integer evaluateConcentration(Obj obj) {
        // if (new PositionCondition(POSITION.ADJACENT_ANY,
        // true, source, CRYSTAL_CHARACTERISTICS).preCheck(obj.getRef())){
        // }
        return DC_Formulas.getFocusConstForConcentration(calculateClaimedObjects(obj, "Crystal"));
    }

    @Override
    public Integer evaluateRest(Obj obj) {

        // DC_PARAMS.STAMINA_BONUS
        return DC_Formulas.getStaminaConstForRest();
    }

    @Override
    public Integer evaluateClaimThreshold(Obj obj, String s) {
        // s?
        Formula formula = new Formula(DC_Formulas.getCrystalThreshold());
        if (StringMaster.compare(StringMaster.GATEWAY, obj.getProperty(G_PROPS.BF_OBJECT_TYPE))) {
            formula = new Formula(DC_Formulas.getGatewayThreshold());
        }
        return formula.getInt(obj.getRef());
    }

    @Override
    public Integer evaluateClaimOutput(Obj obj, String s) {

        return new Formula(DC_Formulas.getClaimOutput()).getInt(obj.getRef());
    }

    @Override
    public Integer evaluateMeditation(Obj obj) {
        // crystals will have a continuous aura of ESSENCE_BONUS?
        // ++ no essence upkeep for units that are adjacent to a crystal

        return DC_Formulas.getEssenceConstForMeditation();
    }

    @Override
    public Integer getStartingEssence(Obj obj) {
        return obj.getIntParam(PARAMS.ESSENCE) * DC_Formulas.STARTING_ESSENCE_PERCENTAGE / 100;
    }

    @Override
    public int calculateClaimedObjects(Obj obj, String BF_OBJECT_TYPE) {
        Conditions conditions = ConditionMaster.getClaimedBfObjConditions(BF_OBJECT_TYPE);

        Ref ref = new Ref(game, obj.getId());
        int i = new Filter<>(ref, conditions).getObjects().size();
        PARAMETER param = ContentManager.getPARAM("CLAIMED_" + BF_OBJECT_TYPE + "S");
        obj.setParam(param, i);
        return i;

    }

    @Override
    public Integer evaluateSummonEnergyCost(Entity obj, String s) {
        // 5+5*level +(level^2 -1)
        Formula formula = new Formula("4+ 5*{SOURCE_LEVEL}+{SOURCE_LEVEL}*{SOURCE_LEVEL}");
        Integer cost = formula.getInt(obj.getRef());
        return cost;
    }

    @Override
    public Integer calculateEssenceRegenBonus(Obj obj) {
        calculateClaimedObjects(obj, "Crystal");
        calculateClaimedObjects(obj, "Gateway");

        int amount = obj.getIntParam(PARAMS.CLAIMED_CRYSTALS) * DC_Formulas.ESS_REGEN_PER_CRYSTAL;

        // TODO matching aspects?

        return amount;
    }

}
