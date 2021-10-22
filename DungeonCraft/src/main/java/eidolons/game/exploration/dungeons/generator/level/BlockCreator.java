package eidolons.game.exploration.dungeons.generator.level;

import eidolons.game.exploration.dungeons.generator.model.LevelModel;
import eidolons.game.exploration.dungeons.generator.model.Room;
import eidolons.game.exploration.dungeons.generator.pregeneration.Pregenerator;
import eidolons.game.exploration.dungeons.generator.tilemap.TileMapper;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.data.ListMaster;

/**
 * Created by JustMe on 7/25/2018.
 */
public class BlockCreator {

    public void createBlocks(LevelModel model) {
        for (Room room : model.getRoomMap().values()) {
            LevelBlock block;

            if (model.getMerged() != null) {
                //TODO
            }
            block = new LevelBlock(room.getCoordinates(), room.getZone(),
             room.getType(), room.getWidth(), room.getHeight(), TileMapper.createTileMap(room));
            model.getBlocks().put(room, block);
            room.getZone().getSubParts().add(block);

        }
        for (Coordinates c : model.getAdditionalCells().keySet()) {
            LevelBlock block =
             model.getBlocks().values().stream().sorted(
              new SortMaster<LevelBlock>().getSorterByExpression_(b ->
               (int) (-100 * CoordinatesMaster.getMinDistanceBetweenGroups(
                b.getCoordinatesSet(), new ListMaster<Coordinates>().asList(c), 3)
               ))).findFirst().get();
            block.getTileMap().getMapModifiable().put(c, model.getAdditionalCells().get(c));
if (Pregenerator.TEST_MODE)
  System.out.println(c + " assinged to " + block);

        }
    }
}
