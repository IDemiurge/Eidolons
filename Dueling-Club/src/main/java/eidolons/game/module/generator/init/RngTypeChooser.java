package eidolons.game.module.generator.init;

import eidolons.content.PROPS;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.generator.tilemap.TilesMaster;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.data.xml.XML_Formatter;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 7/26/2018.
 */
public class RngTypeChooser {

    public static ObjType getType(
            ROOM_CELL value,
            DUNGEON_STYLE style, boolean random) {
        OBJ_TYPE T = DC_TYPE.BF_OBJ;
        String mapString = RngBfObjProvider.getWeightString(value, style);
        WeightMap<ObjType> map = RandomWizard.constructWeightMap(mapString, T);
        if (random) {
            return map.getRandomByWeight();
        }
        return map.getGreatest();
    }

    public static ObjType chooseType(Coordinates c,
                                     ROOM_CELL value,
                                     LevelBlock block ) {
        value = checkRandomCellResolves(value);
        if (!isCellTranslated(value))
            return null;
        DUNGEON_STYLE style = block.getStyle();
        String mapString = RngBfObjProvider.getWeightString(value, style);
        mapString = XML_Formatter.restoreXmlNodeName(mapString);
        //for all special chars!
        if (mapString == null)
            return null;
        OBJ_TYPE T = DC_TYPE.BF_OBJ;
        ObjType type = null;
        Loop loop = new Loop(100);

        WeightMap<ObjType> map = RandomWizard.constructWeightMap(mapString, T);
        map.setObjType(T);
//        if (){
//            map.getGreatest();
//        }

        while (!checkType(type, block) && loop.continues()) {
            type = map.getRandomByWeight();

        }
        if (isNeverRepeat(value)) {
            map.remove(type);
            if (map.isEmpty()) {
                RandomWizard.constructWeightMap(mapString, T, false);
            }
        }
        return type;
    }

    private static boolean isNeverRepeat(ROOM_CELL value) {
        switch (value) {
            case ART_OBJ:
            case SPECIAL_ART_OBJ:
                return true;
        }
        return false;
    }

    private static boolean checkType(ObjType type, LevelBlock block) {
        if (type == null) {
            return false;
        }
        if (block.getColorTheme() != null)
            if (type.checkProperty(PROPS.COLOR_THEME)) {
                COLOR_THEME theme = new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                        type.getProperty(PROPS.COLOR_THEME));
                if (theme == block.getColorTheme())
                    return true;
                if (theme == block.getAltColorTheme())
                    return RandomWizard.chance(50);

                return false;
            }

        return true;
    }

    private static ROOM_CELL checkRandomCellResolves(ROOM_CELL value) {
        if (CoreEngine.isFullFastMode()) {
            if (TilesMaster.isPassable(value)) {
                return ROOM_CELL.FLOOR;
            }
        }
        if (value.getRandomWeightMap() == null) {
            return value;
        }
        WeightMap<ROOM_CELL> map = new WeightMap(value.getRandomWeightMap(), ROOM_CELL.class);
        return map.getRandomByWeight();
    }


    public static boolean isCellTranslated(ROOM_CELL value) {
        switch (value) {
            case VOID:
            case TRAP:
            case GUARDS:
            case PATROL:
            case AMBUSH:
            case CROWD:
            case IDLERS:
            case STALKER:
            case MINI_BOSS:
            case BOSS:
            case LOCAL_KEY:
            case GLOBAL_KEY:
            case RANDOM_PASSAGE:
            case RANDOM_SPAWN_GROUP:
            case RANDOM_OBJECT:
            case FLOOR:
            case ROOM_EXIT:
            case TREASURE_ROOM:
            case THRONE_ROOM:
            case DEATH_ROOM:
            case GUARD_ROOM:
            case COMMON_ROOM:
            case EXIT_ROOM:
            case SECRET_ROOM:
            case ENTRANCE_ROOM:
            case CORRIDOR:
                return false;

        }
        return true;
    }
}
