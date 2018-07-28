package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.secondary.BooleanMaster;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/24/2018.
 *
 *
 * Additional ‘rounding’ of the model
 * Identify walls that can be attached to;
 * those would be our treasure rooms and secret rooms…

 */
public class ModelFinalizer {

    RoomTemplateMaster templateMaster;
    RoomAttacher attacher;

    public ModelFinalizer(RoomTemplateMaster templateMaster, RoomAttacher attacher) {
        this.templateMaster = templateMaster;
        this.attacher = attacher;
    }

    public void finalize(LevelModel model, LevelData data, LevelModelBuilder builder) {
        List<Room> beans = model.getRoomMap().values().stream().filter(
         room -> ModelMaster.isRoomOnEdge(room, model)).collect(Collectors.toList());

        //identify where there is the most empty space... to sort beans on each loop
        Boolean N_S = false;
        Boolean W_E = false;
        while (true) {
            Collections.sort(beans, new SortMaster<Room>()
             .getSorterByExpression_(room-> getSorterValue(model, room.getCoordinates(), N_S, W_E)));

            Room room = beans.remove(0);
            Coordinates p = room.getCoordinates();
            FACING_DIRECTION roomExit = BooleanMaster.isTrue(N_S) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
            if (N_S==null )
                roomExit = BooleanMaster.isTrue(W_E) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;

            Room newRoom =builder.findFittingAndAttach(room,  EXIT_TEMPLATE.CUL_DE_SAC, ROOM_TYPE.TREASURE_ROOM,
             roomExit, room.getZone());

            int n = ModelMaster.getAdjacentToVoid(model, room, roomExit);

            List<Coordinates> exits = ModelMaster.getPossibleExits(roomExit, room);

            break;
        }
    }

    private Integer getSorterValue(LevelModel model, Coordinates point, boolean n_s, boolean w_e) {
//        transformed =         model.getOccupiedCells().stream().map(c->
//         new Coordinates(true, c.x, c.y)).collect(Collectors.toList());
return model.getTopMost();


//        CoordinatesMaster.getFarmostCoordinateInDirection()
//         CoordinatesMaster.getEdgeCoordinatesFromSquare()
    }
}
