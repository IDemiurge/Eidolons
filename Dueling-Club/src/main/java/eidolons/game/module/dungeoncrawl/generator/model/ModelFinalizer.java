package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/24/2018.
 * <p>
 * <p>
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
        List<Room> edgeRooms =getEdgeRooms(model);

        List<LevelGraphNode> unbuiltNodes = builder.graph.getNodes().stream().filter(
         node -> !builder.nodeModelMap.containsKey(node)).collect(Collectors.toList());

        //identify where there is the most empty space... to sort beans on each loop
        Boolean N_S = false;
        Boolean W_E = false;

        main.system.auxiliary.log.LogMaster.log(1,"FINALIZING: edgeRooms="+edgeRooms+
        "\n; nodes= " + unbuiltNodes);
        while (!unbuiltNodes.isEmpty()) {
            Collections.sort(edgeRooms, new SortMaster<Room>()
             .getSorterByExpression_(room -> getSorterValue(model, room.getCoordinates(), N_S, W_E)));

            Room room = new RandomWizard<Room>().getRandomListItem(edgeRooms);
            edgeRooms =getEdgeRooms(model);

//            Coordinates p = room.getCoordinates();
//            FACING_DIRECTION roomExit = BooleanMaster.isTrue(N_S) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
//            if (N_S == null)
//                roomExit = BooleanMaster.isTrue(W_E) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;

            LevelGraphNode node = unbuiltNodes.remove(0);
            FACING_DIRECTION  roomExit = room.getRandomUnusedExit();
            Room newRoom = builder.findFittingAndAttach(room,
             getTemplate(room, node), node.getRoomType(),
             roomExit, room.getZone());
            if (newRoom != null)
            {
                builder.makeExits(roomExit, null, room, null, newRoom, false);
                main.system.auxiliary.log.LogMaster.log(1,"ADDITIONAL ROOM: "+newRoom+
                 "\n; attached to " + room);
                continue;
            }

            //            int n = ModelMaster.getAdjacentToVoid(model, room, roomExit);
            //            List<Coordinates> exits = ModelMaster.getPossibleExits(roomExit, room);

            //            break;
        }
    }

    private EXIT_TEMPLATE getTemplate(Room room, LevelGraphNode node) {
        return EXIT_TEMPLATE.CROSSROAD;
    }

    private List<Room> getEdgeRooms(LevelModel model) {
        return model.getRoomMap().values().stream()
         .filter(room-> room.getExitCoordinates().size()<room.getExits().length)
         .filter(room -> ModelMaster.isRoomOnEdge(room, model)).collect(Collectors.toList());
    }

    private Integer getSorterValue(LevelModel model, Coordinates point, boolean n_s, boolean w_e) {
        //        transformed =         model.getOccupiedCells().stream().map(c->
        //         new Coordinates(true, c.x, c.y)).collect(Collectors.toList());

//        roomExit = room.getExits()[room.getExitCoordinates().size()];
//        getPrioritizedDirection(room);
//        int n = ModelMaster.getAdjacentToVoid(model, room, side);
        return model.getTopMost();

        //try to make more square
        // prioritized direction

        //        CoordinatesMaster.getFarmostCoordinateInDirection()
        //         CoordinatesMaster.getEdgeCoordinatesFromSquare()
    }
}
