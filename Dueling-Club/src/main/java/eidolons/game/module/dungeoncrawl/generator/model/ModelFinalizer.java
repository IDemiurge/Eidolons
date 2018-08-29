package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.GRAPH_NODE_ATTRIBUTE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator;
import eidolons.game.module.dungeoncrawl.generator.graph.GraphPath;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.level.ZoneCreator;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.content.enums.DungeonEnums.LOCATION_TYPE_GROUP;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 7/24/2018.
 * <p>
 * <p>
 * Additional ‘rounding’ of the model
 * Identify walls that can be attached to;
 * those would be our treasure rooms and secret rooms…
 */
public class ModelFinalizer {

    private final LevelModelBuilder builder;
    RoomTemplateMaster templateMaster;
    RoomAttacher attacher;
    private int maxRooms;

    public ModelFinalizer(RoomTemplateMaster templateMaster, RoomAttacher attacher, LevelModelBuilder builder) {
        this.templateMaster = templateMaster;
        this.attacher = attacher;
        this.builder = builder;
    }

    public static void randomizeEdges(LevelModel model) {
        model.offsetCoordinates();
        Map<Coordinates, ROOM_CELL> modelMap = TileMapper.createTileMap(model).getMapModifiable();
        for (Room room : model.getRoomMap().values()) {
            Map<Coordinates, ROOM_CELL> map = TileMapper.createTileMap(room).getMap();
            List<Coordinates> edgeCells = map.keySet().stream()
             .filter(c -> map.get(c) == ROOM_CELL.INDESTRUCTIBLE)
             .filter(c -> TilesMaster.getAdjacentCount(modelMap, c, ROOM_CELL.VOID) > 2)
             .collect(Collectors.toList());
            int last =0;
            for (Coordinates edgeCell : edgeCells) {

                List<Coordinates> voidCells = TilesMaster.getAdjacentCells(modelMap,false, edgeCell, ROOM_CELL.VOID);

//                if (RandomWizard.chance(84))
                    if (RandomWizard.chance(50+last)) {
                        last-=25;
                    Coordinates cell = new RandomWizard<Coordinates>().getRandomListItem(voidCells);
                    model.getAdditionalCells().put(cell, ROOM_CELL.INDESTRUCTIBLE);
                    modelMap.put(cell, ROOM_CELL.INDESTRUCTIBLE);
                        if (RandomWizard.chance(65))
                    if (edgeCell.x == cell.x || edgeCell.y == cell.y) {
                        Coordinates c = room.relative(edgeCell);
                        room.getCells()[c.x][c.y] = ROOM_CELL.FLOOR.getSymbol();
                    }
                }
                else last += 25;
//                    for (Coordinates cell : voidCells) {
//                        if (RandomWizard.chance(89)) {
//                            continue;
//                        }
//                        model.getAdditionalCells().put(cell, ROOM_CELL.INDESTRUCTIBLE);
//                        modelMap.put(cell, ROOM_CELL.INDESTRUCTIBLE);
//                        if (edgeCell.x == cell.x || edgeCell.y == cell.y) {
//                            Coordinates c = room.relative(edgeCell);
//                            room.getCells()[c.x][c.y] = ROOM_CELL.FLOOR.getSymbol();
//                        }
//                    }

            }
        }
    }

    public void substituteRoomModels(LevelModel model) {
        //alternative - remove rooms if their linkcount fails to be met
        for (Room room : model.getRoomMap().values()) {
            if (room.getUsedExits().size() == ExitMaster.getExitCount(room.getExitTemplate()))
                continue;
            EXIT_TEMPLATE template = ExitMaster.getExitTemplateToLinks(room.getUsedExits().size(),
             room.getEntrance(), room.getType());

            if (template == EXIT_TEMPLATE.ANGLE || template == EXIT_TEMPLATE.THROUGH) {
                if (room.getUsedExits().get(0) == room.getEntrance().flip()) {
                    template = EXIT_TEMPLATE.THROUGH;
                } else template = EXIT_TEMPLATE.ANGLE;
            }

            log(1, "Substituting cells for " + room + " with "
             + template);

            RoomModel roomModel = templateMaster.getRandomModelToSubstitute(room.getWidth(),
             room.getHeight(), template, room.getType(), room.getEntrance(
             ), room.getZone().getTemplateGroup(),
             room.getUsedExits());

            if (roomModel == null)
                continue;

            room.setCells(roomModel.getCells());
            room.setRotationsOnly(roomModel.getRotations());
            room.resetExitCells();
            room.setExitTemplate(template);
            room.setExits(RotationMaster.getRotatedExits( //TODO this is wrong
             roomModel.getRotations(), room.getExits()));


            log(1, "New cells for " + room);
            if (ZoneCreator.TEST_MODE) {
                room.setZone(new LevelZone(9));
            }
            //TODO block?
        }

    }

    public void loopBack(LevelModel model) {
        //ensure graph paths
        //        for (GraphPath path : builder.graph.getPaths()) {
        //            Room room = getLastBuiltRoom(path);
        //            if (room == builder.nodeModelMap.get(path.getEndNode())) {
        //                continue;
        //            }
        //            Room room2 = chooseAltRoom(path, room, model); //closest?
        //            connect(room, room2);
        //            // choose randomly adjacent rooms to connect
        //        }

        //geometrically - try to find low-exit rooms that have a lot of void adjacent in between

        for (Room room : model.getRoomMap().values()) {
            for (Room room2 : model.getRoomMap().values()) {
                if (room != room2)
                    if (checkLoopBack(room, room2, model)) {
                        connect(room, room2);
                    }

            }
        }

    }

    private boolean checkLoopBack(Room room, Room room2, LevelModel model) {
        //rooms must be close enough and have nothing between them

        //we could instead *search* for closest room...

        if (!(checkAligned(room, room2, true) || checkAligned(room, room2, false))) {
            return false;
        }
        Coordinates c = room.getCoordinates();
        Coordinates c2 = room2.getCoordinates();
        int diffX = (c.x < c2.x ? room : room2).getWidth() +
         Math.abs(c.x - c2.x);
        int diffY = (c.y < c2.y ? room : room2).getHeight() +
         Math.abs(c.y - c2.y);
        boolean xOrY = diffX < diffY;
        return !(
         model.getRoomMap().values().stream().anyMatch(r -> {
             if (r == room || r == room2)
                 return false;
             return ModelMaster.isBetween(r, room, room2, xOrY);
         }));

    }

    private boolean checkAligned(Room room, Room room2, boolean onXorY) {
        //from the smaller room, get the farthest aligned line -1 and see if it is close enough to center
        Map<Coordinates, ROOM_CELL> map = TileMapper.createTileMap(room).getMap();
        Map<Coordinates, ROOM_CELL> map2 = TileMapper.createTileMap(room2).getMap();
        List<Integer> coordinates = map.keySet().stream().map(c -> c.getXorY(onXorY)).collect(Collectors.toList());
        List<Integer> coordinates2 = map2.keySet().stream().map(c -> c.getXorY(onXorY)).collect(Collectors.toList());

        List<Integer> aligned =
         coordinates.stream().filter(c -> coordinates2.contains(c)).collect(Collectors.toList());

        if (aligned.size() < 3)
            return false;

        //additional conditions? TODO
        return true;
    }

    private void connect(Room room, Room room2) {
        //try place room? or just a line?
        //        List<Coordinates> toClear; //
        FACING_DIRECTION side;
        if (checkAligned(room, room2, true)) {
            side = room.getCoordinates().y < room2.getCoordinates().y ? FACING_DIRECTION.SOUTH : FACING_DIRECTION.NORTH;
        } else {
            side = room.getCoordinates().x < room2.getCoordinates().x ? FACING_DIRECTION.EAST : FACING_DIRECTION.WEST;

        }

        Room link =
         builder.findFitting(room, EXIT_TEMPLATE.THROUGH, ROOM_TYPE.CORRIDOR, side, false);
        if (link == null) {
            return;
        }
        Coordinates c = link.getCoordinates();
        Coordinates offset = new AbstractCoordinates(0, 0);
        while (true) {
            //           TODO  if (new Traverser().checkTraversable(link, side, offset))
            //                if (new Traverser().checkTraversable(room, side, offset)) {
            //                    builder.addRoom(link, room);
            //                    break;
            //                }
            int x = side.isVertical() ? 0 : 1;
            int y = !side.isVertical() ? 0 : 1;
            offset.offset(new AbstractCoordinates(x, y));
            link.setCoordinates(c.getOffset(offset));
        }
        //if it fails, it's not critical...

        //builder.makeExits();
        //make exits

    }

    private Room chooseAltRoom(GraphPath path, Room room, LevelModel model) {
        List<Coordinates> sortedCandidates = model.getRoomMap().keySet().stream().sorted(new SortMaster<Coordinates>().getSorterByExpression_(
         c -> c.dst(room.getCoordinates())
        )).collect(Collectors.toList());
        for (Coordinates c : sortedCandidates) {
            //TODO if
            return model.getRoomMap().get(c);
        }
        return room;
    }

    private Room getLastBuiltRoom(GraphPath path) {
        Room lastRoom = null;
        for (Integer n : path.getNodes().keySet()) {
            LevelGraphNode node = path.getNodes().get(n);
            if ((builder.nodeModelMap.get(node)) == null)
                break;
            lastRoom = builder.nodeModelMap.get(node);
        }
        return lastRoom;
    }

    public void finalize(LevelModel model) {
        log(1, "FINALIZING: \n" + model);
        maxRooms =
         //model.getRoomMap().size() + 4;
         model.getData().getReqs().getIntValue(LEVEL_REQUIREMENTS.maxRooms);
        tryBuildUnbuiltGraphNodes(builder, model);

        tryAdditionalBuild(model);

        if (model.getData().isLoopBackAllowed())
            try {
                loopBack(model);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (model.getData().isSubstituteRoomsAllowed())
            try {
                substituteRoomModels(model);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        if (model.getData().getLocationType().getGroup() == LOCATION_TYPE_GROUP.NATURAL
         ) {
            randomizeEdges(model);
        }
    }

    private boolean checkBuildDone(LevelModel model, LevelData data) {
        if (model.getRoomMap().size() > maxRooms) {
            return true;
        }
        return false;
    }

    private void tryAdditionalBuild(LevelModel model) {
        List<Room> edgeRooms = ModelMaster.getEdgeRooms(model);
        LevelValidator validator = new LevelValidator(false);
        log(1, "tryAdditionalBuild: edgeRooms=" + edgeRooms);
        Loop loop = new Loop(500);
        //        (!validator.validateModel(builder.graph, model) TODO
        //         || !checkBuildDone(model, model.getData()))
        //         &&
        while (loop.continues()) {

            if (checkBuildDone(model, model.getData()))
                break;
            Room room = new RandomWizard<Room>().getRandomListItem(edgeRooms);

            FACING_DIRECTION roomExit = room.getSortedUnusedExit(new SortMaster<FACING_DIRECTION>().getSorterByExpression_(exit ->
             ModelMaster.getExitSortValue(exit, room, model)));
            LevelGraphNode node = createAdditionalNode(model, model.getData());
            log(1, "additional Build for" + room +
             "\n; node= " + node);
            Room newRoom = builder.findFittingAndAttach(room,
             getTemplate(room, node), node.getRoomType(),
             roomExit, room.getZone());
            if (newRoom != null) {
                //                builder.graph.addNode()
                edgeRooms.remove(room);
                builder.makeExits(roomExit, null, room, null, newRoom, false);
                log(1, "ADDITIONAL ROOM: " + newRoom +
                 "\n; attached to " + room + "\n" + model);
            }
        }


    }

    private LevelGraphNode createAdditionalNode(LevelModel model, LevelData data) {
        WeightMap<ROOM_TYPE> map = new WeightMap<>();
        for (ROOM_TYPE roomType : ROOM_TYPE.mainRoomTypes) {
            map.put(roomType, data.getRoomCoeF(roomType));
        }
        ROOM_TYPE type = map.getRandomByWeight();
        GRAPH_NODE_ATTRIBUTE attrs = null;
        return new LevelGraphNode(type, attrs);
    }

    private void tryBuildUnbuiltGraphNodes(LevelModelBuilder builder, LevelModel model) {
        List<Room> edgeRooms = ModelMaster.getEdgeRooms(model);

        List<LevelGraphNode> unbuiltNodes = builder.graph.getNodes().stream().filter(
         node -> !builder.nodeModelMap.containsKey(node)
        ).collect(Collectors.toList());

        log(1, "tryBuildUnbuiltGraphNodes: edgeRooms=" + edgeRooms +
         "\n; nodes= " + unbuiltNodes);

        buildNodes(model, edgeRooms, unbuiltNodes);

    }

    public Integer getAttachRoomSortValue(Room room, LevelGraphNode node, LevelModel model) {
        int value = RandomWizard.getRandomInt(20);
        value += ModelMaster.getSorterEdgeValue(room, model);
        for (FACING_DIRECTION exit : room.getExits()) {
            if (room.getUsedExits().contains(exit))
                continue;
            value += ModelMaster.getExitSortValue(exit, room, model) / room.getExits().length;
        }

        if (builder.graph.getAdjList().get(node).iterator().next().getOtherNode(node).getRoomType() ==
         room.getType())
            value += 10000;

        return value;
    }

    private void buildNodes(LevelModel model, List<Room> edgeRooms, List<LevelGraphNode> unbuiltNodes
    ) {
        Boolean N_S = false;
        Boolean W_E = false;
        //identify where there is the most empty space... to sort beans on each loop
        for (LevelGraphNode node : unbuiltNodes) {

            Collections.sort(edgeRooms, new SortMaster<Room>()
             .getSorterByExpression_(room -> getAttachRoomSortValue(room, node, model)));

            Room room = new RandomWizard<Room>().getRandomListItem(edgeRooms);
            edgeRooms = ModelMaster.getEdgeRooms(model);

            //            Coordinates p = room.getCoordinates();
            //            FACING_DIRECTION roomExit = BooleanMaster.isTrue(N_S) ? FACING_DIRECTION.NORTH : FACING_DIRECTION.SOUTH;
            //            if (N_S == null)
            //                roomExit = BooleanMaster.isTrue(W_E) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;

            FACING_DIRECTION roomExit = room.getRandomUnusedExit();
            Room newRoom = builder.findFittingAndAttach(room,
             getTemplate(room, node), node.getRoomType(),
             roomExit, room.getZone());
            if (newRoom != null) {
                builder.makeExits(roomExit, null, room, null, newRoom, false);
                log(1, "UNBUILT ADDITIONAL ROOM: " + newRoom +
                 "\n; attached to " + room);
                continue;
            }

        }
        //            int n = ModelMaster.getAdjacentToVoid(model, room, roomExit);
        //            List<Coordinates> exits = ModelMaster.getPossibleExits(roomExit, room);
    }

    private EXIT_TEMPLATE getTemplate(Room room, LevelGraphNode node) {
        return EXIT_TEMPLATE.CROSSROAD;
    }

}
