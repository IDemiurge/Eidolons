package eidolons.system.text;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DescriptionMaster;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.module.herocreator.logic.AttributeMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DescriptionTooltips {
    public static final String MASTERY_SLOT = "Filled automatically when you attain sufficient Mastery of any type. " +
            "Mastery Ranks are given for every 5 score points, from 5 and 10 for Tier I to 45 and 50 for Tier V Mastery Ranks."
            +"\nHero can have up to [10] Mastery types unlocked (click to learn at any time)";

    private static final String VALUE_SEPARATOR = "==";
    private static final String NAME_SEPARATOR = ">>";
    private static Map<VALUE, String> map;

    public static void init() {
        map = new HashMap<>();

        String path = PathFinder.getEnginePath() + PathFinder.getTextPathLocale() + "descriptions/values.txt";
        String source = FileManager.readFile(
                path);
        parseSource(source);

        path = PathFinder.getEnginePath() + PathFinder.getTextPathLocale() + "descriptions/attributes.txt";
        source = FileManager.readFile(path);
        if (source.isEmpty()) {
            source = compileAndReadSource(path, DC_ContentValsManager.getAttributes());
        }
        parseSource(source);

        path = PathFinder.getEnginePath() + PathFinder.getTextPathLocale() + "descriptions/masteries.txt";
        source = FileManager.readFile(path);
        if (source.isEmpty()) {
            source = compileAndReadSource(path, DC_ContentValsManager.getMasteries());
        }
        parseSource(source);


    }

    private static String compileAndReadSource(String path, List<PARAMETER> vals) {
        List<String> list = vals.stream().map(p ->
                VALUE_SEPARATOR + p.getDisplayedName() + NAME_SEPARATOR +
                        StringMaster.NEW_LINE + extractDescription(p)).collect(Collectors.toList());

        String contents = ContainerUtils.constructStringContainer(list, StringMaster.NEW_LINE);

        FileManager.write(contents, path);
        return contents;
    }

    private static String extractDescription(PARAMETER p) {
        if (p.isAttribute()) {
            return p.getDescription();
        }
        return DescriptionMaster.getDescription(p);
    }

    private static void parseSource(String source) {
        for (String item : source.split(VALUE_SEPARATOR)) {
            if (!item.contains(NAME_SEPARATOR)) {
                continue;
            }
            String name = item.split(NAME_SEPARATOR)[0].trim();
            String value = item.split(NAME_SEPARATOR)[1].trim();
            VALUE val = ContentValsManager.getValueByDisplayedName(name);
            map.put(val, value);

        }
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
        if (map == null) {
            init();
        }
        return value.getDisplayedName() + StringMaster.NEW_LINE + map.get(ContentValsManager.getBaseParameterFromCurrent(value))
                + getSpecialForHero(value, hero);
    }

    private static String getSpecialForHero(PARAMETER value, Unit hero) {
        if (value.isAttribute()) {
            List<String> s = AttributeMaster.getAttributeBonusInfoStrings(
                    DC_ContentValsManager.ATTRIBUTE.getForParameter(value), hero);
            if (s.isEmpty()) {
                return StringMaster.NEW_LINE +"[cannot display values provided!]" ;
            }
            return "Provides: " + StringMaster.NEW_LINE +
                    ContainerUtils.constructStringContainer(s, StringMaster.NEW_LINE);
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
            case C_MORALE:
            case C_STAMINA:
            case C_TOUGHNESS:
                return ContentValsManager.getBaseParameterFromCurrent(params).getDisplayedName();
            case N_OF_ACTIONS:
                if (DC_Engine.isAtbMode()) return "Initiative";
                break;
            case C_INITIATIVE:
                if (DC_Engine.isAtbMode()) return "Readiness";
                break;
            case C_N_OF_COUNTERS:
            case N_OF_COUNTERS:
                return "Extra Attacks";
        }
        return params.getName();
    }
}
