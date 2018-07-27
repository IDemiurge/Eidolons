package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.level.BlockCreator;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngFillMaster {
public enum FILLER_TYPE{
    DECOR,
    CONTAINER,
    DESTRUCTIBLE,
}
    public void fill(LevelModel model, LevelData data){
        new BlockCreator().createBlocks(model);

       WeightMap<ROOM_CELL> weightMap = getMap(FILLER_TYPE.DECOR, data);

        new RngDecorFiller(model, weightMap);

//        new RngContainerFiller();
//        new RngDestructibleFiller();
    }

    private WeightMap<ROOM_CELL> getMap(FILLER_TYPE type, LevelData data) {
        WeightMap<ROOM_CELL> map = new WeightMap<>("", ROOM_CELL.class);
        switch (type) {
            case DECOR:
                map.put(ROOM_CELL.ART_OBJ, 3);
                map.put(ROOM_CELL.SPECIAL_ART_OBJ, 1);
                break;
            case CONTAINER:
                map.put(ROOM_CELL.CONTAINER, 3);
                map.put(ROOM_CELL.SPECIAL_CONTAINER, 1);
                break;
            case DESTRUCTIBLE:
                map.put(ROOM_CELL.DESTRUCTIBLE_WALL, 3);
                map.put(ROOM_CELL.DESTRUCTIBLE, 1);
                break;
        }
        return map;
    }

}
