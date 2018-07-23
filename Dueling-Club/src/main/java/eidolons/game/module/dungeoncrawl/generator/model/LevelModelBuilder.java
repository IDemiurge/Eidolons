package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphEdge;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.PointX;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelModelBuilder {

    private static final boolean MERGE_LINKS = false;
    LevelData data;
    List<LevelGraphNode> builtNodes = new ArrayList<>();
    private LevelModel model;
    private LevelGraph graph;
    private RoomAttacher attacher;
    private RoomTemplateMaster templateMaster;
    private Map<LevelGraphNode, Room> nodeModelMap = new HashMap<>();

    LevelZone zone; //current zone

    public LevelModelBuilder(LevelData data) {
        this.data = data;
    }

    public LevelModel buildModel(LevelGraph graph) {
        model = new LevelModel(data);
        this.graph = graph;
        templateMaster = new RoomTemplateMaster(data, model);
        this.attacher = new RoomAttacher(data, model, templateMaster);
        try {
            build();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return model;
    }

    public void build() {
        build(graph.getNodeById(0), getBasePoint(), null);

    }

    private boolean isMergeLinks(LevelGraphNode node, Set<LevelGraphEdge> links,
                                 Set<LevelGraphNode> linkedNodes) {
        //TODO  check edges are uniform
        return MERGE_LINKS;
    }

    private void buildLinked(LevelGraphNode sub, Room link, FACING_DIRECTION side) {
        build(sub, ExitMaster.getExitPoint(link, side), side);

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
     * @param entrancePoint
     * @param entrance
     * @return
     */
    public void build(LevelGraphNode node,
                      Point entrancePoint, FACING_DIRECTION entrance) { //'parent' in args? point?
        zone = model.getZone(node);

        Set<LevelGraphEdge> links = new LinkedHashSet<>(graph.getAdjList().get(node));
        //sort by relevance?
        links.removeIf(link->link.getNodeTwo()==node);//no duplication!
        Set<LevelGraphNode> linkedNodes = new LinkedHashSet<>();
        for (LevelGraphEdge sub : links) {

                linkedNodes.add(sub.getNodeTwo());
        }

        //into 1 corridor with multiple exits if need be
        boolean mergeLinks = isMergeLinks(node, links, linkedNodes);
        EXIT_TEMPLATE exitTemplate = ExitMaster.getExitTemplate(mergeLinks, node, links.size());
        EXIT_TEMPLATE roomExitTemplate = !mergeLinks ? exitTemplate :
         ExitMaster.getRandomSingleExitTemplate();
        //recursive build - will it ensure that Main Paths are built?

        Room room = getOrCreateRoomForNode(node, entrancePoint, entrance, roomExitTemplate);
        Set<LevelGraphNode> next = null;
        if (room != null)
            if (mergeLinks) {
                //attach entrance to the room that we just built
                // but this method is only entered once, next we go into buildLinks?
                //                int x = entrancePoint.x;
                //                int y = entrancePoint.y;
                //            TODO      mergeLinks(entrance, x, y, linkedNodes, exitTemplate,
                //                 roomExitTemplate, room);
            } else {
                next = buildLinks(room, links, false);
            }

        for (LevelGraphNode nextNode : next) {
            room = nodeModelMap.get(nextNode);
            if (room == null)
                continue;
            build(nextNode, room.getEntrancePoint(), room.getEntrance());
        }

    }

    public Room getOrCreateRoomForNode(LevelGraphNode node,
                                       Point entrancePoint,
                                       FACING_DIRECTION entrance,
                                       EXIT_TEMPLATE roomExitTemplate) {
        Room room = nodeModelMap.get(node);
        //if we already built this node by following another path ...
        if (room != null)
            return room;


        room = findFittingAndAttach(entrancePoint, roomExitTemplate, node.getRoomType()
         , entrance);
        if (room == null) {
            return null; //don't break the chain!
        }
        nodeModelMap.put(node, room);
        room.setExitTemplate(roomExitTemplate);
        return room;
    }

    public Set<LevelGraphNode> buildLinks(
     Room room, Set<LevelGraphEdge> links, boolean recursion) {
        Set<LevelGraphNode> nextToBuild = new LinkedHashSet<>();
        int i = 0;
        for (LevelGraphEdge edge : links) {
            if (room.getExits().length <= i) {
                break;
            }
            FACING_DIRECTION side = room.getExits()[i++];
            Point entrancePoint = attacher.adjust(room.getPoint(), side, room, true);
            Room link = findFittingAndAttach(entrancePoint, ExitMaster.getRandomSingleExitTemplate(),
             ROOM_TYPE.CORRIDOR, side);
            if (link == null) {
                //link-less attach, same point
            } else {
                room.makeExit(side, true);
                side =link.getExits()[0];//TODO cycle thru new RandomWizard<FACING_DIRECTION>().getRandomArrayItem(link.getExits());
                entrancePoint = attacher.adjust(link.getPoint(),side, link, true);

                link.makeExit(side, true);
            }
            Room newRoom =
             getOrCreateRoomForNode(edge.getNodeTwo(),
              entrancePoint,  side, //just rotate always?
              ExitMaster.getExitTemplate(MERGE_LINKS, edge.getNodeTwo(), graph.getAdjList().get(edge.getNodeTwo()).size()));


            if (newRoom != null) {
                nextToBuild.add(edge.getNodeTwo());
                if (link == null)
                    room.makeExit(side, true);
                newRoom.makeExit(FacingMaster.rotate180(side), true);
                //          main.system.auxiliary.log.LogMaster.log(1," " +model);
            }
            if (newRoom == null && !recursion) {
                //TODO rotate what? it's DEAD-END!
                //                for (int n = 0; n < 4; n++) {
                //                    side = FacingMaster.rotate(side, true);
                //                    buildLinks(entrancePoint, entrance, room, links, true);
                //                }
            }
        }
        return nextToBuild;
    }

    private Room findFittingAndAttach(Point entrancePoint, EXIT_TEMPLATE roomExitTemplate,
                                      ROOM_TYPE roomType, FACING_DIRECTION parentEntrance) {
        return attacher.findFittingAndAttach(entrancePoint,
         roomExitTemplate, roomType, parentEntrance, zone);
    }


    private Point getBasePoint() {
        FACING_DIRECTION side =
         //         RandomWizard.random() ?
         FACING_DIRECTION.NONE
         //          :FacingMaster.getRandomFacing()
         ;
        switch (side) {
            case NONE:
                return new PointX(
                 data.getX() / 2,
                 data.getY() / 2);
        }
        return null;
    }


    public Room mergeLinks(FACING_DIRECTION entrance, int x, int y,
                           Set<LevelGraphNode> linkedNodes, EXIT_TEMPLATE exitTemplate, EXIT_TEMPLATE roomExitTemplate, Room room) {
        FACING_DIRECTION side = ExitMaster.getExit(roomExitTemplate, entrance);//link entrance
        room.makeExit(side, true);
        Room link = findFittingAndAttach
         (new PointX(x, y), exitTemplate,
          ROOM_TYPE.CORRIDOR, side);
        side = ExitMaster.getExit(exitTemplate, side); //now for node entrance
        attacher.attach(room, link, side);
        //            for (LevelGraphNode sub : linkedNodes) {
        //                side = getExit(exitTemplate, side); //TODO ???
        //                attach( sub,link,  side);
        //            }
        //BFS?
        //this is how the algorithm goes forward, it is DPS and

        for (FACING_DIRECTION sub : link.getExits()) {
            DIRECTION dir = entrance.getDirection();
            //why rotate?
            sub = FacingMaster.getFacingFromDirection(DirectionMaster.rotate90(dir, true));
            buildLinked(linkedNodes.iterator().next(), link, sub);
        }
        return room;
    }

}
