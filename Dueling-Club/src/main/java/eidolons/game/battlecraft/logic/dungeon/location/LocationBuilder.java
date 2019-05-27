package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapZone;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.igg.xml.IGG_XmlMaster;
import eidolons.game.module.dungeoncrawl.dungeon.FauxDungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import main.content.enums.DungeonEnums;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.util.Refactor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationBuilder extends DungeonBuilder<Location> {
    public static final String BLOCK_TYPE_NODE = "Block Type";
    public static final String BLOCKS_NODE = StringMaster.getWellFormattedString(RngXmlMaster.BLOCKS_NODE);
    public static final String ROOM_TYPE_NODE = "Room Type";
    public static final String COORDINATES_NODE = StringMaster.getWellFormattedString(RngXmlMaster.COORDINATES_NODE);
    public static final String OBJ_NODE = StringMaster.getWellFormattedString(RngXmlMaster.OBJECTS_NODE);
    public static final String ENTRANCE_NODE = StringMaster.getWellFormattedString(RngXmlMaster.ENTRANCE_NODE);
    public static final String EXIT_NODE = "Exits";
    public static final String ZONES_NODE = StringMaster.getWellFormattedString(RngXmlMaster.ZONES_NODE);
    public static final String AI_GROUPS_NODE = StringMaster
     .getWellFormattedString("ai groups node");
    @Refactor
    private List<Node> nodeList;
    private Location location;
    private Dungeon dungeon;

    public LocationBuilder() {
        super(null);
    }

    public LocationBuilder(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location buildDungeon(String path) {
        location= super.buildDungeon(path);
        FauxDungeonLevel level = createFauxDungeonLevel(path, location);
        master.setDungeonLevel(level);
        level.getVoidCoordinates().addAll(location.getDungeon(). getVoidCoordinates());
//        location.setEntranceData();
        return location;
    }

    private FauxDungeonLevel createFauxDungeonLevel(String path, Location location) {
        FauxDungeonLevel level = new FauxDungeonLevel(PathUtils.getLastPathSegment(path));

        List<LevelZone> zones=    createFauxZones(location) ;
        level.setZones(zones);
        DungeonEnums.DUNGEON_STYLE mainStyle= DungeonEnums.DUNGEON_STYLE.Somber ;
//        if (mainStyle != null) {
//            for (LevelZone zone : level.getZones()) {
//                if (zone.getStyle() == level.getMainStyle())
//                    zone.setStyle(mainStyle);
//            }
//            level.setMainStyle(mainStyle);
//        }
            level.setEntranceData(location.getEntranceData());
        return level;
    }

    private List<LevelZone> createFauxZones(Location location) {
        List<LevelZone> zones = new ArrayList<>();
//        location.getPlan().getZones()
        Integer w = location.getWidth();
        Integer h = location.getHeight();
        for (int i = 0; i < 2; i++) {
        LevelZone zone = new LevelZone(i);
            LevelBlock block=null;
            zone.addBlock(block = new LevelBlock(zone));

            List<Coordinates> coords =i>0?
                    CoordinatesMaster.getCoordinatesWithin(w/2-1, w  ,
                            -1, h)
                    : CoordinatesMaster.getCoordinatesWithin( -1,  w/2 ,
                    -1, h);
            block.setCoordinatesList(coords);
            block.setCoordinates(coords.get(coords.size()-1));
            zones.add(zone);
            zone.setStyle(DungeonEnums.DUNGEON_STYLE.Somber);
        }
        return zones;
    }

    @Refactor
    //TODO the way it's done, we can't have Structures in non-Location dungeons!!!
    public static MapBlock constructBlock(Node node, int id, MapZone zone, DungeonPlan map,
                                          Dungeon dungeon) {
        List<Coordinates> coordinates = new ArrayList<>();
        Map<Coordinates, ? extends Obj> objectMap = new HashMap<>();
        MapBlock b = new MapBlock(id, null, zone, map, coordinates);
        // TODO b-data, coordinates, objects
        for (Node subNode : XML_Converter.getNodeList(node)) {
            if (StringMaster.compareByChar(subNode.getNodeName(), COORDINATES_NODE)) {
                coordinates = CoordinatesMaster.getCoordinatesFromString(subNode.getTextContent());

            } else if (StringMaster.compareByChar(subNode.getNodeName(), OBJ_NODE)) {

                objectMap = // TODO BETTER IN TYPES?
                  DC_ObjInitializer.initMapBlockObjects(dungeon, b, subNode.getTextContent());
                // TODO encounters?
            } else {
                // BLOCK TYPE
                if (StringMaster.compareByChar(subNode.getNodeName(), BLOCK_TYPE_NODE)) {
                    BLOCK_TYPE type = new EnumMaster<BLOCK_TYPE>().retrieveEnumConst(
                     BLOCK_TYPE.class, subNode.getTextContent());
                    b.setType(type);

                }
                if (StringMaster.compareByChar(subNode.getNodeName(), ROOM_TYPE_NODE)) {
                    ROOM_TYPE type = new EnumMaster<ROOM_TYPE>().retrieveEnumConst(ROOM_TYPE.class,
                     subNode.getTextContent());
                    b.setRoomType(type);
                }

            }

        }

        b.setCoordinates(coordinates);
        if (objectMap == null) {
            return b;
        }
        b.getMap().putAll(objectMap);
        b.getObjects().addAll(objectMap.values());

        return b;
    }


    @Override
    public Location buildDungeon(String path, String data, List<Node> nodeList) {
        this.nodeList = nodeList;
        location = (super.buildDungeon(path, data, nodeList));
        DUNGEON_TEMPLATES template = null;
        DungeonPlan plan = new DungeonPlan(template, (location));
        plan.setLoaded(true);
        for (Node n : nodeList) {
            processNode(n, getDungeon(), plan);

        }
        if (CoreEngine.isIggDemo()){
            location.setEntranceData(IGG_XmlMaster.getEntrancesData(path));
            location.getDungeon().setProperty(PROPS.KEY_DOOR_PAIRS, IGG_XmlMaster.getDoorKeyData(path), true);
        }
        location.initEntrances();
        plan.setStringData(data);
        initDynamicObjData(location, plan);

        return location;
    }

    @Refactor
    @Override
    public Location getDungeon() {
        return location;
    }


    @Override
    protected void processNode(Node n, Location dungeon, DungeonPlan plan) {
        if (StringMaster.compareByChar(n.getNodeName(), (ZONES_NODE))) {
            try {
                initZones(n, plan);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        } else
            super.processNode(n, dungeon, plan);
    }

    private MapZone createZone(DungeonPlan plan, int zoneId, Node zoneNode) {
        String name = zoneNode.getNodeName();
        String nodeName = XML_Formatter.restoreXmlNodeName(name);
        if (!nodeName.contains(",")) {
            nodeName = XML_Formatter.restoreXmlNodeNameOld(name);
        }
        int[] c = CoordinatesMaster.getMinMaxCoordinates(nodeName.split(",")[1]);
        MapZone zone = new MapZone(plan.getDungeon(), zoneId, c[0], c[1], c[2], c[3]);
        return zone;
    }

    private void initZones(Node zonesNode, DungeonPlan plan) {
        int id = 0;
        int zoneId = 0;
        List<MapZone> zones = new ArrayList<>();
        //        Node zonesNode = XML_Converter.getChildAt(planNode, (1));

        for (Node zoneNode : XML_Converter.getNodeList(zonesNode)) {
            MapZone zone;
            try {
                zone = createZone(plan, zoneId, zoneNode);
            } catch (Exception e) {
                //                main.system.ExceptionMaster.printStackTrace(e);
                zone = new MapZone(plan.getDungeon(), zoneId, 0, plan.getDungeon().getWidth(), 0,
                 plan.getDungeon().getHeight());
            } // ++ add coord exceptions
            zoneId++;
            zones.add(zone);
            for (Node node : XML_Converter.getNodeList(XML_Converter.getNodeList(zoneNode).get(0))) {
                // if (node.getNodeName().equalsIgnoreCase(BLOCKS_NODE))
                // blocks = initBlocks(XML_Converter.getStringFromXML(node));
                MapBlock block = constructBlock(node, id, zone, plan, getDungeon().getDungeon());
                id++;
                zone.addBlock(block);
                plan.getBlocks().add(block);
            }
        }
        if (plan.getBlocks().size() == 1) {
            int w = plan.getBlocks().get(0).getWidth();
            int h = plan.getBlocks().get(0).getHeight();
            dungeon.setParam(PARAMS.BF_WIDTH, w, true);
            dungeon.setParam(PARAMS.BF_HEIGHT, h, true);
        }
        plan.setZones(zones);
    }


    public DungeonPlan loadDungeonMap(String data) {
        return buildDungeon("", data, nodeList).getPlan();
    }

    public enum BLOCK_TYPE {
        CLEARING, CORRIDOR, ROOM, CROSS, SMALL_ROOM, CULDESAC, GRID, RING;

        public String getName() {
            return StringMaster.getWellFormattedString(toString());
        }

        // spawning in
        // rooms, ai
        // behavior
        // guided by corridors etc
    }

    public enum DUNGEON_TEMPLATES {
        GREAT_ROOM, STAR, RING, CROSS, LABYRINTH, PROMENADE, SERPENT, CLASSIC, PRISON_CELLS,
    }



    // how to support more loose building for, say, natural caverns?

    public enum ROOM_TYPE {
        THRONE_ROOM(60, 45, 3, 0, 4, 0),
        COMMON_ROOM(25, 25),
        CORRIDOR(15, 15, 1, 4, 3, 6),
        TREASURE_ROOM(25, 15, 3, 7, 2, 6),
        DEATH_ROOM(30, 15, 2, 4, 3, 5),
        GUARD_ROOM(25, 25, 3, 6, 2, 4),
        ENTRANCE_ROOM(15, 35),
        EXIT_ROOM(35, 15),
        SECRET_ROOM(15, 15, 1, 4, 3, 6),
        OUTSIDE(60, 45, 3, 0, 4, 0 );

        public static ROOM_TYPE[] mainRoomTypes={
         THRONE_ROOM,
         COMMON_ROOM,
         TREASURE_ROOM,
         DEATH_ROOM,
         GUARD_ROOM,
         SECRET_ROOM
        };
        private int heightMod;
        private int widthMod;
        private int minX;
        private int maxX;
        private int minY;
        private int maxY;

        ROOM_TYPE(int widthMod, int heightMod, int minX, int maxX, int minY, int maxY) {
            this.maxX = maxX;
            this.maxY = maxY;
            this.minX = minX;
            this.minY = minY;
            this.widthMod = widthMod;
            this.heightMod = heightMod;
        }

        ROOM_TYPE(int widthMod, int heightMod) { // , int minWidth, int maxWidth
            this(widthMod, heightMod, 0, 0, 0, 0);
        }

        public int getWidthMod() {
            return widthMod;
        }

        public void setWidthMod(int widthMod) {
            this.widthMod = widthMod;
        }

        public int getHeightMod() {
            return heightMod;
        }

        public void setHeightMod(int heightMod) {
            this.heightMod = heightMod;
        }

        public int getMinX() {
            return minX;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }
    }
}
