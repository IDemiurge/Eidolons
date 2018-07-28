package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphEdge;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;

import java.util.*;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelModelBuilder {

    private static final boolean MERGE_LINKS = false;
    LevelData data;
    List<LevelGraphNode> builtNodes = new ArrayList<>();
    LevelZone zone; //current zone
    private LevelModel model;
    private LevelGraph graph;
    private RoomAttacher attacher;
    private RoomTemplateMaster templateMaster;
    private Map<LevelGraphNode, Room> nodeModelMap = new HashMap<>();

    public LevelModelBuilder(LevelData data) {
        this.data = data;
    }

    public LevelModel buildModel(LevelGraph graph) {
        model = new LevelModel(data);
        model.setZones(graph.getZones());
        this.graph = graph;
        templateMaster = new RoomTemplateMaster(data, model);
        this.attacher = new RoomAttacher(data, model, templateMaster);
        try {
            build();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        new ModelFinalizer(templateMaster, attacher).finalize(model, data, this);
        return model;
    }

    public void build() {
        build(false, graph.getNodeById(0), getBaseCoordinates(), null);
//        if (isBuildFromExit()) {
//            build(true, graph.getNodeById(1), getExitCoordinates(), FacingMaster.getRandomFacing());
//        }
    }

    private Coordinates getExitCoordinates() {
        return new AbstractCoordinates(model.getCurrentWidth() / 2, model.getTopMost());
    }

    private boolean isBuildFromExit() {
        if (model.getRoomMap().values().stream().filter(room -> room.getType()
         == ROOM_TYPE.EXIT_ROOM).count() > 0)
            return false;
        return true;
    }

    private boolean isMergeLinks(LevelGraphNode node, Set<LevelGraphEdge> links,
                                 Set<LevelGraphNode> linkedNodes) {
        //TODO  check edges are uniform
        if (node.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM)
            return false;
        if (node.getRoomType() == ROOM_TYPE.EXIT_ROOM)
            return false;
        if (links.size() == 1)
            return false;
        return isMergeLinksAllowed();
        //        return RandomWizard.random();
    }

    private void buildLinked(LevelGraphNode sub, Room link, FACING_DIRECTION side) {
        build(false, sub, ExitMaster.getExitCoordinates(link, side), side);

    }

    /**
     * ###E###
     * OOOOOOO
     * OO#O#OO
     * #OOOOO#
     * #O#O#O#
     * entance == NORTH
     * node is not a room yet, so we don't have any real map of it
     *
     * @param node
     * @param entranceCoordinates
     * @param entrance
     * @return
     */
    public void build(boolean reverse, LevelGraphNode node,
                      Coordinates entranceCoordinates, FACING_DIRECTION entrance) { //'parent' in args? point?
        zone = model.getZone(node);

        Set<LevelGraphEdge> links = new LinkedHashSet<>(graph.getAdjList().get(node));
        //sort by relevance?
        links.removeIf(link -> (reverse ? link.getNodeOne() : link.getNodeTwo()) == node);//no duplication!
        Set<LevelGraphNode> linkedNodes = new LinkedHashSet<>();
        for (LevelGraphEdge sub : links) {

            linkedNodes.add(sub.getNodeTwo());
        }

        //into 1 corridor with multiple exits if need be
        boolean mergeLinks = isMergeLinks(node, links, linkedNodes);
        EXIT_TEMPLATE exitTemplate = ExitMaster.getExitTemplateToLinks(links.size());
        EXIT_TEMPLATE roomExitTemplate = mergeLinks
         ? ExitMaster.getRandomSingleExitTemplate()
         : exitTemplate;
        //recursive build - will it ensure that Main Paths are built?

        Room room = getOrCreateRoomForNode(node, null, entranceCoordinates, entrance, roomExitTemplate);
        Set<LevelGraphNode> next = null;
        if (room == null)
            throw new RuntimeException();

        if (mergeLinks) {
            //attach entrance to the room that we just built
            // but this method is only entered once, next we go into buildLinks?
            next = mergeLinks(linkedNodes, room);
        }

        if (next == null)
            next = buildLinks(room, links);

        for (LevelGraphNode nextNode : next) {
            room = nodeModelMap.get(nextNode);
            if (room == null)
                continue;
            build(reverse, nextNode, room.getEntranceCoordinates(), room.getEntrance());
        }

    }

    public Set<LevelGraphNode> mergeLinks(
     Set<LevelGraphNode> linkedNodes, Room room) {

        EXIT_TEMPLATE exitTemplate = ExitMaster.getExitTemplateToLinks(linkedNodes.size());
        Set<FACING_DIRECTION> exits = new HashSet<>(Arrays.asList(FACING_DIRECTION.normalFacing));
        Room link = null;

        for (FACING_DIRECTION exitSide : exits) {

            Coordinates entransdceCoordinates = attacher.adjust(room.getCoordinates(),
             FacingMaster.rotate180(exitSide), room, true);

            link = findFittingAndAttach
             (room, exitTemplate,
              ROOM_TYPE.CORRIDOR, FacingMaster.rotate180(exitSide));
            if (link == null)
                continue;
            room.makeExit(exitSide, true);
        }
        if (link == null)
            return null;

        Set<LevelGraphNode> next = new LinkedHashSet<>();
        int i = 0;
        for (LevelGraphNode linkedNode : linkedNodes) {
            FACING_DIRECTION exit = link.getExits()[i++];
            Coordinates p = attacher.adjust(link.getCoordinates(), exit, link, true);
            EXIT_TEMPLATE roomExitTemplate = ExitMaster.getExitTemplateToLinks(
             graph.getAdjList().get(linkedNode).size());
            Room newRoom =
             getOrCreateRoomForNode(linkedNode, link, p, FacingMaster.rotate180(exit), roomExitTemplate);
            boolean door = RandomWizard.chance(data.getIntValue(LEVEL_VALUES.DOOR_CHANCE_COMMON));

            makeExits(null, exit, room, link, newRoom, door, true);

            //            DIRECTION dir = entrance.getDirection();
            //why rotate?
            //            sub = FacingMaster.getFacingFromDirection(DirectionMaster.rotate90(dir, true));
            //            buildLinked(linkedNodes.iterator().next(), link, sub);
        }
        return next;
    }

    public Room getOrCreateRoomForNode(LevelGraphNode node,
                                       Room parent,
                                       Coordinates entranceCoordinates,
                                       FACING_DIRECTION entrance,
                                       EXIT_TEMPLATE roomExitTemplate) {
        Room room = nodeModelMap.get(node);
        //if we already built this node by following another path ...
        if (room != null)
            return room;


        room = findFittingAndAttach(parent, roomExitTemplate, node.getRoomType()
         , entrance);
        if (room == null) {
            return null; //don't break the chain!
        }
        nodeModelMap.put(node, room);
        room.setExitTemplate(roomExitTemplate);
        return room;
    }

    public Set<LevelGraphNode> buildLinks(
     Room room, Set<LevelGraphEdge> links) {
        Set<LevelGraphNode> nextToBuild = new LinkedHashSet<>();
        int i = 0;
        boolean door = false;

        for (LevelGraphEdge edge : links) {
            if (room.getExits().length <= i) {
                break;
            }
            FACING_DIRECTION roomExit = room.getExits()[i++];
            FACING_DIRECTION linkExit = roomExit;
            Coordinates entranceCoordinates = null;
            Room link = findFittingAndAttach(room,

             ExitMaster.getRandomSingleExitTemplate(),
             ROOM_TYPE.CORRIDOR, linkExit);
            if (link == null) {
                //TODO link-less attach, same point
                entranceCoordinates = null;
            } else {
                door = RandomWizard.chance(data.getIntValue(LEVEL_VALUES.DOOR_CHANCE_COMMON));
                room.makeExit(roomExit, door ? false : door);

                if (isShearWalls()) {
                    model.shearWallsFromSide(link, FacingMaster.rotate180(roomExit));
                } else
                    link.makeExit(FacingMaster.rotate180(roomExit), !door ? false : door);

                linkExit =
                 new RandomWizard<FACING_DIRECTION>().getRandomArrayItem(link.getExits());
                entranceCoordinates = attacher.adjust(link.getCoordinates(), linkExit, link, true);
            }
            Room newRoom =
             getOrCreateRoomForNode(edge.getNodeTwo(), link == null ? room : link,
              entranceCoordinates, linkExit,
              ExitMaster.getExitTemplateToLinks(graph.getAdjList().get(edge.getNodeTwo()).size()));


            if (newRoom != null) {
                nextToBuild.add(edge.getNodeTwo());
                makeExits(roomExit, linkExit, room, link, newRoom, door, false);
            }
        }
        return nextToBuild;
    }

    private void makeExits(FACING_DIRECTION roomExit, FACING_DIRECTION linkExit, Room room, Room link, Room newRoom, boolean door, boolean mergedLinks) {
        if (link == null) {
            if (!door)
                door = RandomWizard.chance(data.getIntValue(LEVEL_VALUES.DOOR_CHANCE_COMMON));
            else door = false;
            //door only on one end at most; or at none
            room.makeExit(roomExit, door ? false : door);
            if (isShearWalls()) {
                model.shearWallsFromSide(newRoom, FacingMaster.rotate180(roomExit));
            } else
                newRoom.makeExit(FacingMaster.rotate180(roomExit), !door ? false : door);
        } else {
            door = RandomWizard.chance(data.getIntValue(LEVEL_VALUES.DOOR_CHANCE_COMMON));
            //door only on one end at most; or at none
            link.makeExit(linkExit, door ? false : door);
            if (isShearWalls()) {
                model.shearWallsFromSide(newRoom, FacingMaster.rotate180(linkExit));
            } else
                newRoom.makeExit(FacingMaster.rotate180(linkExit), !door ? false : door);

        }
    }

    private boolean isShearWalls() {
        return true;
    }

    public Room findFittingAndAttach(Room room,
                                     EXIT_TEMPLATE exitTemplate, ROOM_TYPE type,
                                     FACING_DIRECTION parentExit, LevelZone zone) {
        this.zone=zone;
        return findFittingAndAttach(room, exitTemplate, type, parentExit);
    }
    public Room findFittingAndAttach(Room parent, EXIT_TEMPLATE roomExitTemplate,
                                      ROOM_TYPE roomType, FACING_DIRECTION parentExit) {
        Coordinates entranceCoordinates;
        if (parent != null)
            entranceCoordinates = RoomAttacher.adjust(parent.getCoordinates(), parentExit, parent, true);
        else
            entranceCoordinates = getBaseCoordinates();
        Room room = attacher.findFitting(entranceCoordinates,
         roomExitTemplate, roomType, parentExit, zone);
        if (isAltExitsAllowed())
        if (room == null)
        if (parentExit!=null ){
            List<Coordinates> alternativeExits =
             ModelMaster.getPossibleExits(parentExit, parent);
            alternativeExits.removeIf(p -> p.x == entranceCoordinates.x && p.y == entranceCoordinates.y);
            Collections.shuffle(alternativeExits);
            for (Coordinates exit : alternativeExits) {
                exit = exit.offset(entranceCoordinates);
                room = attacher.findFitting(exit,
                 roomExitTemplate, roomType, parentExit, zone);
                if (room!=null )
                    break;
            }
        }
        //        main.system.auxiliary.log.LogMaster.log(1, "Placing  " + room + " at " +
        //         p + "; "+ " with parent exit to the " + parentExit);

        if (room != null)
            model.addRoom(room);
        return room;
    }

    private boolean isAltExitsAllowed() {
        return false;
    }
    private boolean isMergeLinksAllowed() {
        return true;
    }


    private Coordinates getBaseCoordinates() {
        FACING_DIRECTION side =
         //         RandomWizard.random() ?
         FACING_DIRECTION.NONE
         //          :FacingMaster.getRandomFacing()
         ;
        switch (side) {
            case NONE:
                return new AbstractCoordinates(
                 data.getX() / 2,
                 data.getY() / 2);
        }
        return null;
    }


}
