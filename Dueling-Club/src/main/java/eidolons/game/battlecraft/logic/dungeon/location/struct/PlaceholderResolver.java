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
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

public class PlaceholderResolver extends DungeonHandler<Location> {
    public PlaceholderResolver(DungeonMaster<Location> master) {
        super(master);
    }

    public void resolve(ObjType type, Coordinates c){
        String property = type.getProperty(PROPS.PLACEHOLDER_DATA);
        GeneratorEnums.ROOM_CELL cellType= GeneratorEnums.ROOM_CELL.getBySymbol(
                type.getProperty(PROPS.PLACEHOLDER_SYMBOL)) ;
        Module module= getModule();
        //style?
        //

        ObjType  resolvedType = getType(property, cellType, module, c);
    }

    private ObjType getType(String property, GeneratorEnums.ROOM_CELL cellType,
                            Module module, Coordinates c) {
        LevelStruct struct =getStructureMaster().findLowestStruct(c);

        switch (cellType) {
            case WALL:
                if (!struct.getWallType().isEmpty()) {
                    return getBfType(struct.getWallType());
                }
            case ALT_WALL:
                if (!struct.getWallTypeAlt().isEmpty()) {
                    return getBfType(struct.getWallTypeAlt());
                }
            case LIGHT_EMITTER:
            case DOOR:
//apart from STYLE, what else ?
        }
        return getBfType(RngBfObjProvider.getWeightMap(cellType, struct.getStyle()).getRandomByWeight());
    }

    private ObjType getBfType(String type) {
        return DataManager.getType(type, DC_TYPE.BF_OBJ);
    }

}
