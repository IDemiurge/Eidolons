package eidolons.game.module.dungeoncrawl.generator.level;

import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/27/2018.
 */
public class ZoneCreator {

    public static final boolean TEST_MODE = false;
    private static DUNGEON_STYLE lastStyle;

    public static List<LevelZone> createZones(LevelData data) {
        List<LevelZone> list = new ArrayList<>();
        int n = data.getIntValue(LEVEL_VALUES.ZONES);
        List<ZONE_TYPE> types = createTypesList(n, data.getLocationType());
        for (int i = 0; i < n; i++) {
            ZONE_TYPE zone_type = types.get(i);
            DUNGEON_STYLE style = getStyle(zone_type, data.getLocationType());
            ROOM_TEMPLATE_GROUP group = getRoomGroup(data
             , zone_type, style);
            LevelZone zone = new LevelZone(zone_type, group, style, i);
            COLOR_THEME[] colors = getColors(style);
            zone.setColorTheme(colors[0]);
            if (colors.length>1)
                zone.setAltColorTheme(colors[1]);
            list.add(zone);
        }
        return list;
    }

    private static ROOM_TEMPLATE_GROUP getRoomGroup(LevelData data,
                                                    ZONE_TYPE zone_type,
                                                    DUNGEON_STYLE style) {

        //TODO check style sync
        return new RandomWizard<ROOM_TEMPLATE_GROUP>().getRandomArrayItem(data.getTemplateGroups());
    }

    private static String getWeightString(LOCATION_TYPE subdungeonType) {
        return null;
    }

    private static List<ZONE_TYPE> createTypesList(int n,
                                                   LOCATION_TYPE subdungeonType) {
        List<ZONE_TYPE> list = new ArrayList<>();

        SUBLEVEL_TYPE sublevelType;
        for (int i = 0; i < n + 1; i++) {
            switch (i) {
                case 0:
                    list.add(ZONE_TYPE.ENTRANCE);
                    break;
                case 1:
                    list.add(ZONE_TYPE.MAIN_AREA);
                    break;
                case 2:
                    list.add(ZONE_TYPE.BOSS_AREA);
                    break;
                default:
                    list.add(ZONE_TYPE.OUTSKIRTS);
                    break;
            }

        }
        //        WeightMap<ZONE_TYPE> map = new WeightMap<>
        //        (getWeightString(subdungeonType), ZONE_TYPE.class);
        //        list.add(map.getRandomByWeight());

        return list;
    }

    private static COLOR_THEME[] getColors(DUNGEON_STYLE style) {
        boolean b = RandomWizard.random();
        switch (style) {
            case Stony:
                return
                 b? new COLOR_THEME[]{
                  COLOR_THEME.PURPLE,
                  COLOR_THEME.YELLOW,
                 }
                 :new COLOR_THEME[]{
                  COLOR_THEME.CYAN,
                  COLOR_THEME.PINK,
                 };
            case Somber:
                return
                 b? new COLOR_THEME[]{
                  COLOR_THEME.PURPLE,
                  COLOR_THEME.BLUE,
                 }
                  :new COLOR_THEME[]{
                  COLOR_THEME.BLUE,
                  COLOR_THEME.CYAN,
                 };
            case Brimstone:
                return
                 b? new COLOR_THEME[]{
                  COLOR_THEME.ORANGE,
                  COLOR_THEME.YELLOW,
                 }
                  :new COLOR_THEME[]{
                  COLOR_THEME.RED,
                  COLOR_THEME.ORANGE,
                 };
            case Cold:
                return
                 b? new COLOR_THEME[]{
                  COLOR_THEME.CYAN,
                  COLOR_THEME.BLUE,
                 }
                  :new COLOR_THEME[]{
                  COLOR_THEME.BLUE,
                  COLOR_THEME.LIGHT,
                 };
            case Arcane:
                return
                 b? new COLOR_THEME[]{
                  COLOR_THEME.PURPLE,
                  COLOR_THEME.CYAN,
                 }
                  :new COLOR_THEME[]{
                  COLOR_THEME.BLUE,
                  COLOR_THEME.PINK,
                 };
            case DarkElegance:

                return
                 b? new COLOR_THEME[]{
                  COLOR_THEME.PURPLE,
                  COLOR_THEME.LIGHT,
                 }
                  :new COLOR_THEME[]{
                  COLOR_THEME.DARK,
                  COLOR_THEME.PURPLE,
                 };
        }
        return getColors(DungeonEnums.DUNGEON_STYLE.Somber);
    }

    public static DUNGEON_STYLE getStyle(ZONE_TYPE zone_type,
                                          LOCATION_TYPE subdungeonType) {
        boolean alt = false;
        switch (zone_type) {
            case BOSS_AREA:
            case OUTSKIRTS:
                alt = true;
                break;
        }
        DUNGEON_STYLE style = TileConverter.getStyle(subdungeonType, alt);
        while (lastStyle == style && RandomWizard.chance(80)) {
            style = TileConverter.getStyle(subdungeonType, true);
        }
        lastStyle = style;
        return style;
    }
}
