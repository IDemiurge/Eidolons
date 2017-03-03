package main.rules.rpg;

import main.content.DC_ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.text.TextParser;

import java.util.Map;

public class PrincipleMaster {

    public static void processPrincipleValues(ObjType type) {
        switch ((DC_TYPE) type.getOBJ_TYPE_ENUM()) {
            case SKILLS:
                fixParamBonuses(type);
                break;
            case CLASSES:
                // could be done dynamically but why bother... look up that DCH
                // map!
                initAlignmentParams(type);
                break;
            case CHARS:
                fixIdentityNumbers(type);
                initIdentityParams(type);

                autoSpendIdentityPoints(type);
                break;
            case UNITS:
                if (!initIdentityParams(type)) {
                    generateUnitIdentityParams(type);
                }
                generateUnitAlignmentParams(type);
                break;
            case DEITIES:
                fixIdentityNumbers(type);
                initIdentityParams(type);
                break;
        }

    }

    private static void autoSpendIdentityPoints(ObjType type) {
        // award points
        Integer points = type.getIntParam(PARAMS.IDENTITY_POINTS);

        points = +type.getIntParam(PARAMS.HERO_LEVEL)
                * type.getIntParam(PARAMS.IDENTITY_POINTS_PER_LEVEL);

        // maximize by alignment first, randomize last?

        // weight map? or a loop that checks which investment will give most
        // integrity!

    }

    private static void fixIdentityNumbers(ObjType type) {
        String prop = type.getProperty(G_PROPS.PRINCIPLES);
        prop = prop.replaceAll("3", "2");
        prop = prop.replaceAll("4", "2");
        for (String substring : StringMaster.openContainer(prop)) {
            if (prop.contains("(0)")) {
                prop = prop.replace(substring + ";", "");
            }

        }

        type.setProperty(G_PROPS.PRINCIPLES, prop);
    }

    private static void generateUnitIdentityParams(ObjType type) {

    }

    private static void generateUnitAlignmentParams(ObjType type) {
        // TODO level/2 * [n] , after identity has been generated...

    }

    private static boolean initParams(ObjType type, boolean alignmentOrIdentity) {
        Map<PRINCIPLES, String> map = new RandomWizard<PRINCIPLES>().constructStringWeightMap(type
                .getProperty(G_PROPS.PRINCIPLES), PRINCIPLES.class);
        if (map.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (PRINCIPLES principle : map.keySet()) {
            // Integer amount =StringMaster.getInteger(map.getOrCreate(principle));
            // if (amount == 0)
            // continue;
            result = true;
            PARAMETER param = alignmentOrIdentity ? DC_ContentManager
                    .getAlignmentForPrinciple(principle) : DC_ContentManager
                    .getIdentityParamForPrinciple(principle);
            type.setParam(param, map.get(principle));
        }
        return result;
    }

    private static boolean initIdentityParams(ObjType type) {
        return initParams(type, false);
    }

    private static boolean initAlignmentParams(ObjType type) {
        return initParams(type, true);

    }

    private static void fixParamBonuses(ObjType type) {
        String prop = type.getProperty(PROPS.PARAMETER_BONUSES);
        Map<PARAMS, String> map = new RandomWizard<PARAMS>().constructStringWeightMap(prop,
                PARAMS.class);
        Ref ref = new Ref(type.getGame());
        ref.setID(KEYS.INFO, type.getId());
        for (PARAMS param : map.keySet()) {
            if (!(param.getName().contains(StringMaster.ALIGNMENT) || param.getName().contains(
                    StringMaster.IDENTITY))) {
                continue;
            }
            String string = map.get(param);
            // string = TextParser.parse(string, ref,
            // TextParser.INFO_PARSING_CODE);
            // Ref.setInfoObject(type); TODO support {n} or is it useless?
            int amount = StringMaster.getInteger(TextParser.parse(string, ref,
                    TextParser.INFO_PARSING_CODE));
            // if (param.getName().contains(StringMaster.ALIGNMENT)) {
            //
            // type.modifyParameter(param, amount);
            // } else if (param.getName().contains(StringMaster.IDENTITY)) {
            type.setParameter(param, amount);

            String value = param.getName() + StringMaster.wrapInParenthesis(string + "");
            type.removeProperty(PROPS.PARAMETER_BONUSES, value);
            LogMaster.log(1, value + " move to parameter on " + type);
        }

        // TODO from prop to params

    }

    public static void initPrincipleIdentification(ObjType type) {
        String prop = type.getProperty(G_PROPS.PRINCIPLES);

        if (prop.contains("(")) {
            return;
        }

        String newValue = "";
        int i = Math.min(3, StringMaster.openContainer(prop).size());
        for (String substring : StringMaster.openContainer(prop)) {
            newValue += substring + StringMaster.wrapInParenthesis(i + "") + ";";
            PRINCIPLES principle = new EnumMaster<PRINCIPLES>().retrieveEnumConst(PRINCIPLES.class,
                    substring);
            newValue += principle.getOpposite().toString()
                    + StringMaster.wrapInParenthesis(-i + "") + ";";
            i--;
        }

        type.setProperty(G_PROPS.PRINCIPLES, newValue);
    }

    public static String getHelpInfo() {
        return "Each Hero has an Identity that is defined by some Principles and determines how certain acts, skills and classes will affect their personal Integrity. As their path goes on and their Identity strengthens, they receive additional Identity Points to be invested in Principles of their choice. Heros Deity and Background determine their initial Identity, but they can also choose their Principles. Whether through deeds or training in skills and classes, heroes accumulate Alignment with each of the 10 Principles, and will receive an Integrity modifier equal to the product of their Identity and Alignment. For example, negative Identity and negative Alignment will generate a bonus to Integrity which in turn affects their Mastery scores (double for Leadership and Tactics), Gold Prices, Experience Gain, Divination modifier, Battle Spirit, Organization and all Personality modifiers in range of 50 to 200%. ";
    }

}
