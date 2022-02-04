package eidolons.content.etalon;

import eidolons.content.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.SkillEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

import java.util.LinkedList;
import java.util.List;

public class EtalonGen {
    /*
Base class => Class ranks
Standard perks (to be swapped out for units!)
Skill/Spell points
Masteries aligned with class and unspent <?>
Attributes - spend by weight, mark total

Mastery ranks acquisition with levels
maintain ratio?
we gain increasing number of ranks with each level
calculate the new rank set with closest ratio for N pts spent...
     */


    /**
     * Whenever there are some changes in the RPG system, we'd do this to update our AV content from bottom up!
     */
    public static List<ObjType> generateEtalonTypes() {
        List<ObjType> generated = new LinkedList<>();
        for (EtalonConsts.EtalonType model : EtalonConsts.EtalonType.values()) {
            ObjType type = null;
            for (int i = 1; i < model.maxLevel; i++) {
                try {
                    type = generate(model, i);
                    generated.add(type);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
        return generated;
    }

    private static ObjType generate(EtalonConsts.EtalonType model, int level) {
        //TODO  ObjType base = getBase(DC_TYPE.UNITS); default values should work without this! Or?...
        ObjType baseType = DataManager.getType(model.baseType, DC_TYPE.UNITS);
        ObjType type = new ObjType(model.name+ ", Level "+level, baseType);
        DC_Game.game.initType(type);
        type.setGenerated(true);
        type.setProperty(G_PROPS.GROUP, "Etalon");
        type.setProperty(G_PROPS.UNIT_GROUP, "Etalon");
        type.setProperty(G_PROPS.ASPECT, "Arena");
        type.setProperty(PROPS.BASE_CLASS, model.baseClass.getName());

        EtalonConsts.AttributeData attrs = createAttrData(model.attributeSet.weights, model.attributeSet.spentCoef, level);
        for (ATTRIBUTE attribute : ATTRIBUTE.values()) {
            int base = type.getIntParam(attribute.getBaseParameter()

            ); //TODO
            int pts = attrs.getIntValue(StringMaster.format(attribute.toString()));
            type.setParam(attribute.getBaseParameter(), EtalonCalculator.getAttrValueForPts(base, base, pts)
            );
        }
        EtalonConsts.MasteryData masteryData = createMstrData(model.masterySet.weights, model.masterySet.spentCoef, level);
        for (SkillEnums.MASTERY m : SkillEnums.MASTERY.values()) {
            String value = masteryData.getValue(m);
            if (!StringMaster.isEmpty(value)) {
                type.setParameter(m.getParam(), NumberUtils.getIntParse(value) *5);
            }
        }

        type.setParam(PARAMS.SKILL_POINTS_UNSPENT, EtalonCalculator.getSkillPts(level, model.baseClass, model.masterySet));
        type.setParam(PARAMS.SPELL_POINTS_UNSPENT, EtalonCalculator.getSpellPts(level, model.baseClass, model.masterySet));
        type.setParam(PARAMS.MASTERY_RANKS_UNSPENT, EtalonCalculator.getFreeMasteryRanks(
                level, model.baseClass, model.masterySet.spentCoef));
        type.setParam(PARAMS.ATTR_POINTS, EtalonCalculator.getFreeAttrs(
                level, model.baseClass, model.attributeSet.spentCoef));

        PROPERTY classProp = PROPS.CLASSES_TIER_1;
        if (level >= 6) {
            classProp = PROPS.CLASSES_TIER_2; //TODO etc
        }
        type.addProperty(classProp, getClassRank(model.baseClass)); //TODO baseClass will then be Tier 2?

        // int power=PowerCalculator.unit(type);
        // type.setParam(PARAMS.POWER, power);
        // type.setParam(PARAMS.POWER_USED, power);

        return type;
    }

    private static String getClassRank(ClassEnums.CLASS_GROUP baseClass) {
        switch (baseClass) {
            case SORCERER:
                return "Apostate";
            case WIZARD:
                return "Apprentice";
            case RANGER:
                return "Scout";
        }
        return baseClass.getName();
    }

    public static EtalonConsts.MasteryData createMstrData(String weights, float spentCoef, int level) {
        String statString = createStatsFromWeights(weights, spentCoef, level, true);
        return new EtalonConsts.MasteryData(statString);

    }

    public static EtalonConsts.AttributeData createAttrData(String attrWeights, float spentCoef, int level) {
        String statString = createStatsFromWeights(attrWeights, spentCoef, level, true);
        return new EtalonConsts.AttributeData(statString);
    }

    private static String createStatsFromWeights(String weights, float coef, int level, boolean attr) {
        DataUnit data = (attr ? new EtalonConsts.AttributeData(weights) : new EtalonConsts.MasteryData(weights));

        float totalWeights = data.getSum();
        int total = EtalonCalculator.getTotalStat(level, attr);
        coef = new Float(total) / totalWeights * coef;

        for (Object val : data.getValues().keySet()) {
            data.setValue(val.toString(), "" + Math.round(data.getFloatValue(val.toString()) * coef));
        }

        return data.toString();
    }


}
