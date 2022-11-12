package eidolons.system.text;

import eidolons.content.ATTRIBUTE;
import eidolons.content.DescriptionMaster;
import eidolons.content.PARAMS;
import eidolons.netherflame.eidolon.heromake.model.AttributeConsts;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.data.filesys.PathFinder.*;
import static main.system.auxiliary.data.FileManager.*;

public class DescriptionTooltips {
    public static final String MASTERY_SLOT = "Filled automatically when you attain sufficient Mastery of any type. " +
            "Mastery Ranks are given for every 5 score points, from 5 and 10 for Tier I to 45 and 50 for Tier V Mastery Ranks."
            + "\nHero can have up to [10] Mastery types unlocked (click to learn at any time)";

    private static final String VALUE_SEPARATOR = "==";
    private static final String NAME_SEPARATOR = ">>";

    private static final String DESCR = "descriptions";
    private static final String TUTORIAL = "tutorial";
    private static final String TIPS = "tips";
    private static final String LORE = "lore";

    private static Map<VALUE, String> paramMap;
    static boolean init;
    private static final Map<String, Map<String, String>> maps = new HashMap();

    public static void initTutorialMap() {
        String path = getRootPath() + getTextPathLocale() + "tutorial/tutorial.txt";
        String source = readFile(path);
        parseSource(source, getMap(TUTORIAL), false);
    }

    private static boolean isParam(String name) {
        return name.equalsIgnoreCase("values.txt")
                || name.equalsIgnoreCase("masteries.txt")
                || name.equalsIgnoreCase("attributes.txt");
    }

    public static void init() {
        if (init)
            return;
        init = true;
        paramMap = new XLinkedMap<>();

        //VALUES
        String source;
        for (File file : getFilesFromDirectory(getRootPath() + getTextPathLocale() + "/descriptions",
                false, true)) {
            source = readFile(file);
            String name = StringMaster.cropFormat(file.getName());
            parseSource(source, getMap(name), isParam(name));
        }
        //TIPS
        for (File file : getFilesFromDirectory(getRootPath() + getTextPathLocale() + TIPS,
                false, true)) {
            source = readFile(file);
            String name = StringMaster.cropFormat(file.getName());
            parseSource(source, getMap(name), false);
        }


        String path = StrPathBuilder.build(getTextPath(),
                TextMaster.getLocale(), DESCR, "types");
        source = readFile(path + "/heroes info.txt");
        parseSource(source, getMap(DESCR), false);

        path = StrPathBuilder.build(getTextPath(),
                TextMaster.getLocale(), DESCR, "types");
        source = readFile(path + "/heroes.txt");
        parseSource(source, getMap(LORE), false);

        initTutorialMap();

        /**
         * quests
         *
         *
         *
         */
    }

    public static Map<String, String> getMap(String name) {
        Map<String, String> map = maps.get(name);
        if (map == null) {
            map = new XLinkedMap<>();
            maps.put(name, map);
        }
        return map;
    }


    private static String compileAndReadSource(String path, List<PARAMETER> vals) {
        List<String> list = vals.stream().map(p ->
                VALUE_SEPARATOR + p.getDisplayedName() + NAME_SEPARATOR +
                        Strings.NEW_LINE + extractDescription(p)).collect(Collectors.toList());
        String contents = ContainerUtils.constructStringContainer(list, Strings.NEW_LINE);
        write(contents, path);
        return contents;
    }

    private static String extractDescription(PARAMETER p) {
        if (p.isAttribute()) {
            return p.getDescription();
        }
        return DescriptionMaster.getDescription(p);
    }

    public static Map<VALUE, String> getParamMap() {
        return paramMap;
    }

    public static Map<String, String> getTipMap() {
        return getMap(TIPS);
    }

    public static Map<String, String> getLoreMap() {
        return getMap(LORE);
    }

    public static Map<String, String> getTutorialMap() {
        return getMap(TUTORIAL);
    }

    public static Map<String, String> getDescrMap() {
        return getMap(DESCR);
    }

    private static void parseSource(String source, boolean paramOrTip) {
        parseSource(source, null, paramOrTip);
    }

    private static void parseSource(String source, Map map, boolean paramOrTip) {
        for (String item : source.split(VALUE_SEPARATOR)) {
            if (!item.contains(NAME_SEPARATOR)) {
                continue;
            }
            String name = item.split(NAME_SEPARATOR)[0].trim();
            String value = item.split(NAME_SEPARATOR)[1].trim();
            map.put(name.toLowerCase(), value);

            if (paramOrTip) {
                VALUE val = ContentValsManager.getValueByDisplayedName(name);
                paramMap.put(val, value);
            }
        }
        //     else {
        //         if (map != null) {
        //             map.put(name.toLowerCase(), value);
        //             continue;
        //         }
        //         TextEvent tip = Tips.getTipConst(name);
        //         if (tip == null) {
        //             tipMap.put(name.toLowerCase(), value);
        //             continue;
        //         } else
        //             tipMap.put(tip.toString().toLowerCase(), value);
        //         tip.setMessage(value);
        //     }
        //
        // }
    }

    public static String slotTooltip(VALUE displayedParam) {
        /**
         *
         Perk Slot
         Fill the two Class Rank slots above to unlock a free Perk chosen from the pool of the related classes. If they are the same, the unique class perk will be available.

         Filled automatically when you attain sufficient Mastery of one kind or another. Mastery Ranks begin with the score of 5 and 10 for Tier I and continue at same rate, i.e. up to 45 and 50 for Tier V Mastery Rank.

         Fill the two Mastery Rank slots above to buy a skill for Experience points, chosen from the pool of the two related masteries. If the two are the same, some skills can be chosen twice.




         */
        return null;
    }

    public static String tooltip(PARAMETER displayedParam) {
        return tooltip(displayedParam, null);
    }

    public static String tooltip(PARAMETER value, Unit hero) {
        if (paramMap == null) {
            init();
        }
        return value.getDisplayedName() + Strings.NEW_LINE + paramMap.get(ContentValsManager.getBaseParameterFromCurrent(value))
                + getSpecialForHero(value, hero);
    }

    private static String getSpecialForHero(PARAMETER value, Unit hero) {
        if (value.isAttribute()) {
            List<String> s = AttributeConsts.getAttributeBonusInfoStrings(
                    ATTRIBUTE.getForParameter(value), hero);
            if (s.isEmpty()) {
                return Strings.NEW_LINE + "[cannot display values provided!]";
            }
            return "Provides: " + Strings.NEW_LINE +
                    ContainerUtils.constructStringContainer(s, Strings.NEW_LINE);
        }
        if (value == PARAMS.C_FOCUS) {
            return "Base value: " + hero.getIntParam(PARAMS.STARTING_FOCUS);
        }
        return "";
    }

    public static String getDisplayedName(PARAMS params) {
        switch (params) {
            case C_ENDURANCE:
            case C_FOCUS:
            case C_TOUGHNESS:
                return ContentValsManager.getBaseParameterFromCurrent(params).getDisplayedName();
            case INITIATIVE:
                if (DC_Engine.isAtbMode()) return "Initiative";
                break;
            case C_ATB:
                if (DC_Engine.isAtbMode()) return "Readiness";
                break;
            case C_EXTRA_ATTACKS:
            case EXTRA_ATTACKS:
                return "Extra Attacks";
        }
        return params.getName();
    }
}
