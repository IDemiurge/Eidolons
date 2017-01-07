package main.client.cc.logic;

import main.content.CONTENT_CONSTS.MASTERY;
import main.content.ContentManager;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.ValuePages;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.DC_Formulas;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.DC_MathManager;

import java.util.List;

public class UnitLevelManager {

    private static final String LEVEL = ", Level ";

    public ObjType getLeveledType(ObjType baseType, int levelUps,
                                  boolean generateType) {
        generateType = true; // yeah...
        ObjType newType = (generateType) ? new ObjType(baseType) : baseType;
        // change name? TODO reinit
        // game.initType(heroType);
        awardXP(newType, levelUps);
        awardGold(newType, levelUps);
        // check max level and transitions
        for (int i = 0; i < levelUps; i++) {
            levelUp(newType);
        }
        String name = newType.getName();
        if (name.contains(LEVEL)) {
            name = name.substring(0, name.indexOf(LEVEL));
        }
        newType.setName(
                // .setProperty(G_PROPS.NAME,
                name + LEVEL + newType.getIntParam(PARAMS.UNIT_LEVEL));
        if (generateType)
            baseType.getGame().initType(newType);
        return newType;
    }

    public ObjType getLeveledType(ObjType baseType, int levelUps) {
        return getLeveledType(baseType, levelUps, true);
    }

    private void levelUp(ObjType type) {
        int i = type.getIntParam(PARAMS.UNIT_LEVEL);
        // TODO award xp for 1 level HERE!

        checkBuyPoint(type);

        upMasteries(type, i);

        // buy attr points?
        // upPlanItems(type, i, true);
        // upPlanItems(type, i, false);

        upAttrs(type, i);
        upParams(type, i);
        type.modifyParameter(PARAMS.LEVEL, 1);
        type.modifyParameter(PARAMS.UNIT_LEVEL, 1);
        type.modifyParameter(PARAMS.POWER,
                DC_MathManager.getLeveledUnitPowerBonus(type));
        // special PER LEVEL? >> as PASSIVES?
    }

    private void checkBuyPoint(ObjType type) {
        if (StringMaster.isEmpty(type.getProperty(PROPS.SPELL_PLAN)))
            if (StringMaster.isEmpty(type.getProperty(PROPS.XP_PLAN)))
                buyPoints(false, type);

    }

    private void upParams(ObjType newType, int i) {
        for (PARAMETER p : ContentManager.getPerLevelParams()) {
            if (newType.getIntParam(p) > 0) {
                PARAMETER param = ContentManager.getPARAM(p.toString().replace(
                        StringMaster.PER_LEVEL, ""));
                newType.modifyParameter(param, newType.getIntParam(p));
            }
        }

    }

    public void up(Entity newType, int i, boolean attrs) {
        PARAMS perLevel = (attrs) ? PARAMS.ATTR_POINTS_PER_LEVEL
                : PARAMS.MASTERY_POINTS_PER_LEVEL;
        Integer attrsPerLevel = newType.getIntParam(perLevel);
        if (attrsPerLevel <= 0) {
            int forLevel = newType.getLevel()
                    * DC_Formulas.getPointsPerLevelIncrease(newType, attrs);
            attrsPerLevel = forLevel
                    + DC_Formulas.getAttrPointsPerLevelDefault(newType);
            newType.setParam(perLevel, attrsPerLevel);
            newType.modifyParameter(perLevel, forLevel);
        }
        PARAMS pointsParam = (attrs) ? PARAMS.ATTR_POINTS
                : PARAMS.MASTERY_POINTS;
        newType.modifyParameter(pointsParam, attrsPerLevel);
        Integer points = newType.getIntParam(pointsParam);
        newType.modifyParameter(perLevel,
                DC_Formulas.getPointsPerLevelIncrease(newType, attrs));
        // double for monsters?
        spendPoints(points, newType, attrs);
    }

    private void upMasteries(ObjType newType, int i) {
        up(newType, i, false);
    }

    private void upAttrs(ObjType newType, int i) {
        up(newType, i, true);
    }

    public Integer spendPoints(Entity newType, boolean attrs) {
        PARAMS pointsParam = (attrs) ? PARAMS.ATTR_POINTS
                : PARAMS.MASTERY_POINTS;
        Integer points = newType.getIntParam(pointsParam);
        return spendPoints(points, newType, attrs);
    }

    public Integer spendPoints(int points, Entity newType, boolean attrs) {
        String progression = newType
                .getProperty((attrs) ? PROPS.ATTRIBUTE_PROGRESSION
                        : PROPS.MASTERY_PROGRESSION);
        if (StringMaster.isEmpty(progression)) {
            progression = generateProgression(newType, attrs); // TODO set for
            newType.setProperty((attrs) ? PROPS.ATTRIBUTE_PROGRESSION
                    : PROPS.MASTERY_PROGRESSION, progression); // type?
        }
        List<String> list = StringMaster.openContainer(progression);
        PARAMS pointsParam = (attrs) ? PARAMS.ATTR_POINTS
                : PARAMS.MASTERY_POINTS;

        if (list.isEmpty()) {
            return points;
        }

        Loop.startLoop(50);
        while (points > 0 && !Loop.loopEnded())
        // for (String valueName : list)
        {
            PARAMETER param = null;
            try {
                param = getRandomParameter(attrs, newType);
            } catch (Exception e) {

            }
            if (param == null)
                continue;
            int cost = PointMaster.getPointCost(newType.getIntParam(param),
                    newType, param);
            if (points >= cost) {
                newType.modifyParameter(param, 1);
                points -= cost;
            }
        }
        newType.setParam(pointsParam, points);
        return points;
    }

    private PARAMETER getRandomParameter(boolean attrs, Entity newType) {
        PARAMETER param = null;
        if (attrs) {
            ATTRIBUTE a = new RandomWizard<ATTRIBUTE>().getObjectByWeight(
                    newType.getProperty(PROPS.ATTRIBUTE_PROGRESSION),
                    ATTRIBUTE.class);
            param = ContentManager.getBaseAttribute(ContentManager.getPARAM(a
                    .toString()));
        } else {
            MASTERY mstr = new RandomWizard<MASTERY>().getObjectByWeight(
                    newType.getProperty(PROPS.MASTERY_PROGRESSION),
                    MASTERY.class);
            if (mstr == null)
                return null;
            // TODO complete the ENUM!!!
            param = ContentManager.getPARAM(mstr.toString());

            if (param == null)
                param = ContentManager.findPARAM(mstr.toString());
        }
        return param;
    }

    private String generateProgression(Entity newType, boolean attrs) {
        String progression = "";
        if (attrs)
            for (ATTRIBUTE attr : ATTRIBUTE.values()) {
                progression += attr.getParameter().getName()
                        + StringMaster.wrapInParenthesis(newType.getParam(attr
                        .getParameter())) + ";";
            }
        else {
            for (PARAMETER mstr : ValuePages.MASTERIES) {
                Integer value = newType.getIntParam(mstr);
                if (value > 0)
                    progression += mstr.getName()
                            + StringMaster.wrapInParenthesis(value + "") + ";";
            }
        }
        return progression;
    }

    public ObjType awardXP(ObjType newType, int levelUps) {
        return awardXP(newType, levelUps, true);
    }

    public ObjType awardXP(ObjType newType, int n, boolean fromLevel) {
        if (fromLevel) {
            int totalXp = DC_Formulas.getTotalXpForLevel(newType
                    .getIntParam(PARAMS.LEVEL) + n);
            // int addedXp = totalXp
            // - DC_Formulas.getTotalXpForLevel(newType
            // .getIntParam(PARAMS.LEVEL));
            // award XP constant? no - a leveled unit will concur with std
            // numbers
            // non-leveled will have xp per power
            // newType.modifyParameter(PARAMS.XP, addedXp); //let's say we add
            // dynamic xp in DC_UnitObj method
            newType.setParam(PARAMS.TOTAL_XP, totalXp);
        } else {
            // newType.modifyParameter(PARAMS.XP, n);
            // newType.modifyParameter(PARAMS.TOTAL_XP, n);
            int levelUps = DC_Formulas.getLevelForXp(newType
                    .getIntParam(PARAMS.TOTAL_XP) + n)
                    - newType.getIntParam(PARAMS.LEVEL);
            if (levelUps <= 0)
                return newType;
            return getLeveledType(newType, levelUps, true);
        }
        return newType;

    }

    private void awardGold(ObjType newType, int levelUps) {
        int gold = DC_Formulas.getGoldForLevel(newType
                .getIntParam(PARAMS.LEVEL) + levelUps);
        newType.modifyParameter(PARAMS.GOLD, gold);

    }

    private void upPlanItems(ObjType newType, int i, boolean xp) {
        List<String> array = StringMaster.openContainer(newType
                .getProperty((xp) ? PROPS.GOLD_PLAN : PROPS.XP_PLAN));
        boolean success = false;
        for (String item : array) { // getOrCreate all the xp items next in line while
            // there is xp to spend
            if (!checkItem(newType, item, xp))
                continue;
            if (!tryAward(newType, item, xp))
                break;
            else
                success = true;
        }
        if (!success) {
            buyPoints(!xp, newType);
        }
    }

    public void buyPoints(boolean gold, Entity type) {
        buyPoints(true, gold, type);
    }

    public void buyPoints(boolean attrs, boolean gold, Entity type) {
        boolean result = true;
        while (result) {
            result = buyPoint(attrs, gold, type);
        }
    }

    public boolean buyPoint(boolean attr, boolean gold, Entity type) {
        PARAMS cost_param = (gold) ? PARAMS.GOLD : PARAMS.XP;
        PARAMS param = (attr) ? PARAMS.ATTR_POINTS : PARAMS.MASTERY_POINTS;

        PARAMS buyParam = (attr) ? (gold) ? PARAMS.ATTR_BOUGHT_WITH_GOLD
                : PARAMS.ATTR_BOUGHT_WITH_XP
                : (gold) ? PARAMS.MASTERY_BOUGHT_WITH_GOLD
                : PARAMS.MASTERY_BOUGHT_WITH_XP;

        int amount = DC_MathManager.getBuyCost(attr, gold, type);

        if (((gold) ? type.getIntParam(PARAMS.GOLD) : type
                .getIntParam(PARAMS.XP)) < amount)
            return false;

        type.modifyParameter(cost_param, -amount);
        type.modifyParameter(param, 1);
        type.modifyParameter(buyParam, 1);
        return true;
    }

    private boolean tryAward(ObjType newType, String item, boolean xp) {
        Integer xpRemaining = newType.getIntParam(PARAMS.TOTAL_XP);
        XpItem nextItem = getXpItem(item);
        if (nextItem.getXpCost() <= xpRemaining) {
            award(newType, nextItem);
            return true;
        }
        return false;
    }

    private void award(ObjType newType, XpItem nextItem) {
        newType.addProperty(nextItem.getProperty(), nextItem.getName());
        newType.modifyParameter(PARAMS.TOTAL_XP, -nextItem.getXpCost());

    }

    private XpItem getXpItem(String item) {
        return new XpItem(item);
    }

    private boolean checkItem(ObjType newType, String item, boolean xp) {
        if (!xp) {
            if (newType.checkSingleProp(G_PROPS.MAIN_HAND_ITEM, item))
                return true;
            if (newType.checkSingleProp(G_PROPS.OFF_HAND_ITEM, item))
                return true;
            if (newType.checkSingleProp(G_PROPS.ARMOR_ITEM, item))
                return true;
            if (newType.checkContainerProp(PROPS.INVENTORY, item))
                return true;
            return newType.checkContainerProp(PROPS.QUICK_ITEMS, item);
        }
        if (newType.checkContainerProp(PROPS.VERBATIM_SPELLS, item))
            return true;
        if (newType.checkContainerProp(G_PROPS.ACTIVES, item))
            return true;
        return newType.checkContainerProp(PROPS.SKILLS, item);
    }

}
