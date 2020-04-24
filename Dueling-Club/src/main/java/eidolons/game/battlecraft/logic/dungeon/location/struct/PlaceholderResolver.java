package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.init.RngBfObjProvider;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.datatypes.WeightMap;
import main.system.launch.CoreEngine;

public class PlaceholderResolver extends DungeonHandler<Location> {
    public PlaceholderResolver(DungeonMaster master) {
        super(master);
    }

    public ObjType resolve(Module module, ObjType type, Coordinates c){
        if (CoreEngine.isLevelEditor())
            return type;
        String property = type.getProperty(PROPS.PLACEHOLDER_DATA);
        if (type.getProperty(PROPS.PLACEHOLDER_SYMBOL).isEmpty()) {
            return type;
        }
        GeneratorEnums.ROOM_CELL cellType= GeneratorEnums.ROOM_CELL.getBySymbol(
                type.getProperty(PROPS.PLACEHOLDER_SYMBOL)) ;

        ObjType  resolvedType = getType(module,property, cellType,   c);
        return resolvedType;
    }

    private ObjType getType(Module module, String property, GeneratorEnums.ROOM_CELL cellType,
                            Coordinates c) {
        LevelStruct struct =getStructureMaster().findStructWithin(module, c);
        if (struct == null) {
            struct = module;
        }
        switch (cellType) {
            case WALL:
                String wallType = struct.getWallType();
                if (!wallType.isEmpty()) {
                    return getBfType(wallType);
                }
                break;
            case ALT_WALL:
                String wallTypeAlt = struct.getWallTypeAlt();
                if (!wallTypeAlt.isEmpty()) {
                    return getBfType(wallTypeAlt);
                }
                break;
            case LIGHT_EMITTER:
            case DOOR:
//apart from STYLE, what else ?
        }
        DungeonEnums.DUNGEON_STYLE style = struct.getStyle();
        if (style == null) {
            style = DungeonEnums.DUNGEON_STYLE.Somber;
        }
        WeightMap<String> weightMap = RngBfObjProvider.getWeightMap(cellType, style);
        ObjType bfType = getBfType(weightMap.getRandomByWeight());
        while (bfType==null && !weightMap.isEmpty()){
            bfType = getBfType(weightMap.getRandomByWeight());
        }
        return bfType;
    }

    private ObjType getBfType(String type) {
        return DataManager.getType(type, DC_TYPE.BF_OBJ);
    }

}
