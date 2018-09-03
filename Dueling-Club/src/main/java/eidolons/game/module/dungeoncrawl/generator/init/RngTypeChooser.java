package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.content.PROPS;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/26/2018.
 */
public class RngTypeChooser {
    public static ObjType chooseType(Coordinates c,
                                     ROOM_CELL value,
                                     LevelBlock block, DungeonLevel dungeonLevel) {

        value = checkRandomCellResolves(value);

        DUNGEON_STYLE style = block.getStyle();
        String mapString = RngBfObjProvider.getWeightString(value, style);
        if (mapString == null)
            return null;
        mapString = filter(mapString, c, block, dungeonLevel);
        OBJ_TYPE T = DC_TYPE.BF_OBJ;
        ObjType type = null;
        Loop loop = new Loop(100);
        while (!checkType(type, block) && loop.continues()) {
            type = RandomWizard.getObjTypeByWeight(mapString, T);
        }
        return type;
    }

    private static boolean checkType(ObjType type, LevelBlock block) {
        if (type == null) {
            return false;
        }
        if (block.getColorTheme()!=null )
        if (type.checkProperty(PROPS.COLOR_THEME)) {
            COLOR_THEME theme = new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
             type.getProperty(PROPS.COLOR_THEME));
            if (theme == block.getColorTheme())
                return true;
            if (theme == block.getAltColorTheme())
                if (RandomWizard.chance(50))
                    return true;

            return false;
        }

        return true;
    }

    private static ROOM_CELL checkRandomCellResolves(ROOM_CELL value) {
        if (value.getRandomWeightMap() == null) {
            return value;
        }
        WeightMap<ROOM_CELL> map = new WeightMap(value.getRandomWeightMap(), ROOM_CELL.class);
        return map.getRandomByWeight();
    }

    private static String filter(String mapString, Coordinates c,
                                 LevelBlock block, DungeonLevel dungeonLevel) {
        //remove what?
        return mapString;
    }
}
