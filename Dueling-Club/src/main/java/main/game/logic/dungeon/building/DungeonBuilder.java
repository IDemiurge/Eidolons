package main.game.logic.dungeon.building;

import main.content.CONTENT_CONSTS.FLIP;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.G_PARAMS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DC_ObjInitializer;
import main.game.battlefield.FacingMaster;
import main.game.battlefield.XDimension;
import main.game.battlefield.map.DungeonMapGenerator;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.DungeonMaster;
import main.game.logic.dungeon.building.BuildHelper.BUILD_PARAMS;
import main.game.logic.dungeon.building.BuildHelper.BuildParameters;
import main.game.logic.macro.utils.CoordinatesMaster;
import main.game.player.DC_Player;
import main.system.auxiliary.*;
import main.system.launch.CoreEngine;
import main.system.launch.TypeBuilder;
import main.system.math.MathMaster;
import main.system.text.NameMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

public class DungeonBuilder {
    public static final DIRECTION DEFAULT_BUILD_DIRECTION = DIRECTION.DOWN_RIGHT;
    public static final String BLOCK_TYPE_NODE = "Block Type";
    public static final String BLOCKS_NODE = "Blocks";
    public static final String ROOM_TYPE_NODE = "Room Type";
    public static final String COORDINATES_NODE = "Coordinates";
    public static final String OBJ_NODE = "Objects";
    public static final String DUNGEON_TYPE_NODE = "Dungeon Type";
    public static final Coordinates BASE_COORDINATE = new Coordinates(1, 1);
    public static final boolean testMode = true;
    public static final DUNGEON_TEMPLATES testTemplate = DUNGEON_TEMPLATES.GREAT_ROOM;
    public static final String ENTRANCE_NODE = "Entrances";
    public static final String EXIT_NODE = "Exits";
    public static final ROOM_TYPE[] std_room_types = {ROOM_TYPE.ENTRANCE_ROOM,

            ROOM_TYPE.COMMON_ROOM, ROOM_TYPE.COMMON_ROOM, ROOM_TYPE.COMMON_ROOM, ROOM_TYPE.TREASURE_ROOM,
            ROOM_TYPE.TREASURE_ROOM, ROOM_TYPE.SECRET_ROOM};
    public static final ROOM_TYPE[] spec_room_types = {ROOM_TYPE.DEATH_ROOM, ROOM_TYPE.GUARD_ROOM,
            ROOM_TYPE.GUARD_ROOM,};
    public static final String CUSTOM_PARAMS_NODE = "Custom Params";
    public static final String CUSTOM_PROPS_NODE = "Custom Props";
    public static final String ZONES_NODE = "Zones";
    public static final String DIRECTION_MAP_NODE = "Direction Map";
    public static final String WALL_OBJ_DATA_NODE = "Wall Objects";
    public static final String AI_GROUPS_NODE = StringMaster
            .getWellFormattedString("ai groups node");
    private static final String FLIP_MAP_NODE = "Flipping";
    private DungeonPlan plan;
    private Dungeon dungeon;
    private BuildHelper helper;
    private DUNGEON_TEMPLATES template;
    private boolean flipX;
    private boolean flipY;
    private BuildParameters params;
    public DungeonBuilder() {
    }

    public DungeonBuilder(BuildParameters params) {
        this.params = params;
    }

    public static List<ROOM_TYPE> getDefaultMainRooms(DUNGEON_TEMPLATES template) {
        List<ROOM_TYPE> list = new LinkedList<>();
        switch (template) {
            case PRISON_CELLS:
                return new ListMaster<ROOM_TYPE>().getList(ROOM_TYPE.DEATH_ROOM,
                        ROOM_TYPE.GUARD_ROOM, ROOM_TYPE.COMMON_ROOM);
            case CLASSIC:
                return new ListMaster<ROOM_TYPE>().getList(ROOM_TYPE.ENTRANCE_ROOM,
                        ROOM_TYPE.COMMON_ROOM, ROOM_TYPE.TREASURE_ROOM);
            case PROMENADE:
                return new ListMaster<ROOM_TYPE>().getList(ROOM_TYPE.DEATH_ROOM,
                        ROOM_TYPE.TREASURE_ROOM, ROOM_TYPE.SECRET_ROOM);
            case GREAT_ROOM:
                return new ListMaster<ROOM_TYPE>().getList(ROOM_TYPE.THRONE_ROOM,
                        ROOM_TYPE.TREASURE_ROOM, ROOM_TYPE.SECRET_ROOM);
        }
        return list;
    }

    public static Dungeon preloadDungeon(String path) {
        return loadDungeon(path, true);
    }

    public static Dungeon loadDungeon(String path) {
        return loadDungeon(path, false);
    }

    public static Dungeon loadDungeon(String path, boolean preload) {

        return new DungeonBuilder().initDungeon(path, preload);
    }

    public static MapBlock constructBlock(Node node, int id, MapZone zone, DungeonPlan map,
                                          Dungeon dungeon) {
        List<Coordinates> coordinates = new LinkedList<>();
        Map<Coordinates, ? extends Obj> objectMap = new HashMap<Coordinates, Obj>();
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
        if (objectMap == null)
            return b;
        b.getMap().putAll(objectMap);
        b.getObjects().addAll(objectMap.values());

        return b;
    }

    public DungeonPlan transformDungeonPlan(DungeonPlan plan) {
        Coordinates.setFlipX(plan.isFlippedX());
        Coordinates.setFlipY(plan.isFlippedY());
        Coordinates.setRotated(plan.isRotated());
        // TODO CHANGE DIMENSIONS
        try {
            plan = loadDungeonMap(plan.getXml());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Coordinates.setFlipX(false);
            Coordinates.setFlipY(false);
            Coordinates.setRotated(false);
        }
        return plan;
    }

    // static use and save as templates...
    public DungeonPlan buildDungeonPlan(Dungeon dungeon) {
        // location = new Location(dungeon, 1);
        // dungeon.getLocation();

        if (helper == null)
            helper = new BuildHelper(dungeon, params);
        this.setDungeon(dungeon);
        if (StringMaster.isEmpty(helper.getParams().getValue(BUILD_PARAMS.FILLER_TYPE))) {
            {
                if (!dungeon.isUnderground()) {
                    // return;
                }
                plan = new DungeonPlan(testTemplate, dungeon);
                int x1 = 0;
                int x2 = params.getIntValue(BUILD_PARAMS.WIDTH);
                if (x2 <= 0)
                    x2 = dungeon.getCellsX();
                else
                    dungeon.setParam(PARAMS.BF_WIDTH, x2);
                int y1 = 0;
                int y2 = params.getIntValue(BUILD_PARAMS.HEIGHT);
                if (y2 <= 0)
                    y2 = dungeon.getCellsY();
                else
                    dungeon.setParam(PARAMS.BF_HEIGHT, y2);
                MapZone zone = new MapZone(dungeon, 0, x1, x2, y1, y2);
                List<Coordinates> coordinates = CoordinatesMaster.getCoordinatesWithin(x1 - 1,
                        x2 - 1, y1 - 1, y2 - 1);
                new MapBlock(0, BLOCK_TYPE.ROOM, zone, plan, coordinates);
                plan.getZones().add(zone);

                return plan;
            }
        }
        if (DungeonMaster.PRESET_PLAN != null) {
            File file = FileManager.getFile(PathFinder.getDungeonLevelFolder()
                    + DungeonMaster.PRESET_PLAN);
            if (file.isFile()) {
                String data = FileManager.readFile(file);
                DungeonPlan plan = null;
                try {
                    plan = loadDungeonMap(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return plan;
            }
        }
        // preset in location-mission, based
        // on Level?
        // yes, any dungeon could have any template more or less,

        template = dungeon.getTemplate();

        if (testMode)
            template = testTemplate;
        // else if ()
        // template = new
        // RandomWizard<DUNGEON_TEMPLATES>().getObjectByWeight(dungeon
        // .getProperty(PROPS.DUNGEON_TEMPLATES), DUNGEON_TEMPLATES.class);

        if (template == null)
            template = DUNGEON_TEMPLATES.CLASSIC;
        List<MapBlock> blocks = null;
        Map<ObjType, Coordinates> objMap = null;
        plan = new DungeonPlan(template, dungeon);
        dungeon.setPlan(plan);
        plan.setBlocks(blocks);
        plan.setObjMap(objMap);
        plan.setZones(createMapZones());

        // Entrance exit = dungeon.getMainExit();
        // Entrance entrance = dungeon.getMainEntrance();
        // plan.setBaseAnchor(
        // entrance.getCoordinates()
        // );
        // plan.setEndAnchor(exit.getCoordinates());

        // if (!dungeon.isIgnoreRotate())
        plan.setRotated(dungeon.isRotated());

        plan.setFlippedX(dungeon.isFlippedX());
        plan.setFlippedY(dungeon.isFlippedY());

        placeMainRooms();
        if (!dungeon.isSurface())
            placeCulDeSacs();
        if (!dungeon.isSurface() && !helper.getParams().isNoRandomRooms())
            placeAdditionalRooms();
        if (!dungeon.isSurface() && !helper.getParams().isNoCorridors())
            linkWithCorridors();
        // initBlockSpecials();

        return plan;
    }

    private List<MapZone> createMapZones() {
        List<MapZone> list = new LinkedList<>();
        int i = 0;
        MapZone zone = new MapZone(getDungeon(), i, 0, getDungeon().getCellsX(), 0, getDungeon()
                .getCellsY());
        list.add(zone);
        // if (plan.getTemplate() == DUNGEON_TEMPLATES.GREAT_ROOM) {
        // // this is where it all starts... we need point of entry and exit
        // Coordinates c = helper.getMainRoomBase(ROOM_TYPE.THRONE_ROOM);
        // XDimension size = helper.getRoomSize(ROOM_TYPE.THRONE_ROOM);
        // MapZone zone = new MapZone(i, x1, x2, y1, y2);
        // // perhaps after MAIN_ROOMS - zone per main room, then split
        // }
        return list;
    }

    private void placeMainRooms() {
        List<ROOM_TYPE> list = null;
        if (!helper.getParams().getValue(BUILD_PARAMS.MAIN_ROOMS).isEmpty()) {
            // TODO
        } else

            list = getDefaultMainRooms(template);
        // add custom from dungeon

        if (getDungeon().isSurface())
            placeMainRoom(ROOM_TYPE.BATTLEFIELD, template);
        else
            for (ROOM_TYPE type : list) {
                placeMainRoom(type, template);
            }
    }

    private void placeMainRoom(ROOM_TYPE type, DUNGEON_TEMPLATES template) {
        int sizeMod = 100; // some big dungeons may want this to go down! TODO
        XDimension size = helper.getRoomSize(type);
        int width = size.getWidth();
        int height = size.getHeight();
        Coordinates c = helper.getMainRoomCoordinates(template, type, width, height);
        if (!helper.tryPlaceRoom(type, c, width, height)) {
            // throw new RuntimeException();
        }
    }

    private void placeAdditionalRooms() {
        // TODO SET ROOM_TYPE AFTER TO FIT BEST? E.G.TREASURE/SECRET MOST
        // DISTANT FROM ENTRANCE
        int attempts = plan.getCellsX() * plan.getCellsY() * 10;
        int remainingAttempts = attempts;
        while (remainingAttempts > 0) {
            remainingAttempts--;
            boolean spec = false;
            if (attempts - remainingAttempts > 6)
                spec = RandomWizard.chance(85);

            String string = getDungeon().getProperty(PROPS.ADDITIONAL_ROOM_TYPES);
            ROOM_TYPE roomType = new RandomWizard<ROOM_TYPE>().getObjectByWeight(string,
                    ROOM_TYPE.class);

            if (roomType == null) {
                string = helper.getParams().getValue(BUILD_PARAMS.ADDITIONAL_ROOMS);
                roomType = new RandomWizard<ROOM_TYPE>().getRandomListItem(Arrays
                        .asList(spec ? spec_room_types : std_room_types));
            }
            Coordinates c = getRandomCoordinate(roomType);
            if (c == null) {
                continue;
            }
            XDimension size = helper.getRoomSize(roomType);

            // boolean flipX = location.isFlipX(c);
            // boolean flipY = location.isFlipY(c); // TODO
            helper.tryPlaceRoom(roomType, c, size.width, size.height, flipX, flipY);

            if (helper.getUsedCoordinates().size() * 100
                    / (getDungeon().getCellsX() * getDungeon().getCellsY()) > helper.getParams().PREFERRED_FILL_PERCENTAGE)
                break;
        }
        // distance from other blocks?
    }

    // public static ObjType getDungeonTypeFromLevelData(String data) {
    // return null;
    // }

    private void placeCulDeSacs() {
        int n = helper.getParams().CUL_DE_SACS;
        n = MathMaster.applyMod(n, getDungeon().getPlan().getWidthMod());
        n = RandomWizard.getRandomIntBetween(n / 2, n * 2);
        Loop.startLoop(10000);
        while (!Loop.loopEnded()) {
            MapBlock block = new RandomWizard<MapBlock>().getRandomListItem(plan.getBlocks());

            FACING_DIRECTION direction = FacingMaster.getRandomFacing();
            Coordinates baseCoordinate = helper.getRandomWallCoordinate(direction, block);
            if (helper.tryPlaceCorridor(block, baseCoordinate, direction, true))
                n--;
            if (n < 0)
                break;

        }
    }

    private void linkWithCorridors() {
        // TODO link each room, some more than once...
        List<MapBlock> blocksToLink = new LinkedList<>(plan.getBlocks()); // TODO
        for (MapBlock b : plan.getBlocks())
            if (b.getType() == BLOCK_TYPE.CULDESAC)
                blocksToLink.remove(b);

        Loop.startLoop(10000); // TODO fail condition? u
        while (!blocksToLink.isEmpty()) {
            List<MapBlock> blocksToRemove = new LinkedList<>();
            for (MapBlock block : blocksToLink) {
                if (block.getConnectedBlocks().size() > 1) {
                    blocksToRemove.add(block);
                    continue;
                }
                FACING_DIRECTION direction = FacingMaster.getRandomFacing();
                Coordinates baseCoordinate = helper.getRandomWallCoordinate(direction, block);
                if (helper.tryPlaceCorridor(block, baseCoordinate, direction)) {
                    if (block.getConnectedBlocks().size() > 1) // TODO depends!
                        blocksToRemove.add(block);
                }
            }
            if (Loop.loopEnded())
                break;
            blocksToLink.removeAll(blocksToRemove);
        }
    }

    private Coordinates getRandomCoordinate(ROOM_TYPE roomType) {
        // TODO totally random? a fair start...
        boolean corner = false;
        boolean center = false;
        ROOM_TYPE[] adjacentRoomRequired = null;
        switch (roomType) {
            case DEATH_ROOM:
            case GUARD_ROOM:
                adjacentRoomRequired = new ROOM_TYPE[]{ROOM_TYPE.TREASURE_ROOM,
                        ROOM_TYPE.THRONE_ROOM, ROOM_TYPE.EXIT_ROOM,};
                break;
            case SECRET_ROOM:
            case TREASURE_ROOM:
                corner = true;
                break;
        }
        Coordinates c = null;
        if (adjacentRoomRequired != null) {
            Loop.startLoop(500);
            while (!Loop.loopEnded()) {
                MapBlock block = new RandomWizard<MapBlock>().getRandomListItem(plan.getBlocks());
                if (Arrays.asList(adjacentRoomRequired).contains(block.getRoomType())) {
                    List<Coordinates> list = CoordinatesMaster.getAdjacentToSquare(block
                            .getCoordinates());
                    c = new RandomWizard<Coordinates>().getRandomListItem(list);
                    if (c.isInvalid())
                        continue;
                    if (helper.getUsedCoordinates().contains(c))
                        continue;
                    return c;
                }
            }
            return null;
        }

        Loop.startLoop(100);
        while (!Loop.loopEnded()) {
            c = CoordinatesMaster.getRandomCoordinate(getDungeon().getCellsX(), getDungeon()
                    .getCellsY());
            if (helper.getUsedCoordinates().contains(c))
                continue;
            // add the outer walls to usedCoordinates?
            if (corner) {
                if (Math.min(plan.getBorderX() - c.x, c.x) + Math.min(plan.getBorderY() - c.y, c.y) > 6)
                    continue;
            }
            break;
        }
        return c;
    }

    public DungeonPlan loadDungeonMap(String data) {
        return new DungeonBuilder().initDungeon(data, false).getPlan();
    }

    public Dungeon initDungeon(String path, boolean preload) {
        String data = FileManager.readFile(path);
        if (data.isEmpty()) {
            data = FileManager.readFile(
             path.contains(PathFinder.getDungeonLevelFolder()) ? path
                    : PathFinder.getDungeonLevelFolder() + path);
        }
        if (data.isEmpty())
            data = path;
        Document levelDocument = XML_Converter.getDoc(data, true);

        Node levelNode = XML_Converter.getChildAt(levelDocument, 0);
        Node planNode = XML_Converter.getChildByName(levelNode, "Plan");
        List<Node> nodeList = XML_Converter.getNodeList(planNode);
        Node typeNode = nodeList.get(0);
        ObjType type = null;

        if (StringMaster.compareByChar(typeNode.getNodeName(), (DUNGEON_TYPE_NODE))) {
            String name = typeNode.getTextContent();
            if (name.contains(NameMaster.VERSION))
                name = name.split(NameMaster.VERSION)[0];
            type = DataManager.getType(name, OBJ_TYPES.DUNGEONS);
        } else {
            type = TypeBuilder.buildType(typeNode, type); // custom base type
        }
        setDungeon(new Dungeon(type));
        getDungeon().setLevelFilePath(path.replace(PathFinder.getDungeonLevelFolder(), ""));
        // getDungeon().setName(name)
        DUNGEON_TEMPLATES template = null;
        DungeonPlan plan = new DungeonPlan(template, getDungeon());
        plan.setLoaded(true);
        for (Node n : XML_Converter.getNodeList(levelNode)) {

            if (StringMaster.compareByChar(n.getNodeName(), (WALL_OBJ_DATA_NODE))) {

                String wallObjData = n.getTextContent();

                if (!StringMaster.isEmpty(wallObjData))
                    plan.setWallObjects(DC_ObjInitializer.processObjData(DC_Player.NEUTRAL,
                            wallObjData));

            }

            if (StringMaster.compareByChar(n.getNodeName(), (FLIP_MAP_NODE))) {
                plan.setFlipMap(new RandomWizard<FLIP>().constructStringWeightMapInversed(n
                        .getTextContent(), FLIP.class));

            } else if (StringMaster.compareByChar(n.getNodeName(), (DIRECTION_MAP_NODE))) {
                plan.setDirectionMap(new RandomWizard<DIRECTION>()
                        .constructStringWeightMapInversed(n.getTextContent(), DIRECTION.class));

            } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PARAMS_NODE))) {
                TypeBuilder.setParams(getDungeon(), n);
                getDungeon().getGame().getDungeonMaster().setDungeon(getDungeon());
                // TypeBuilder.setParams(type, n); // toBase()? TODO new type?
            } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PROPS_NODE))) {
                TypeBuilder.setProps(getDungeon(), n);
                // TypeBuilder.setProps(type, n);
            } else if (StringMaster.compareByChar(n.getNodeName(), ("Plan"))) {
                try {
                    initZones(n, plan);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        getDungeon().setPlan(plan);
        plan.setMap(new DungeonMapGenerator().generateMap(getDungeon()));
        plan.setStringData(data);

        if (!CoreEngine.isLevelEditor()) {
            initDynamicObjData(plan);
        }

        return dungeon;

    }

    private void initDynamicObjData(DungeonPlan plan) {
        List<DC_HeroObj> fullObjectList = new LinkedList<>();
        int z = getDungeon().getIntParam(G_PARAMS.Z_LEVEL);
        for (MapBlock b : plan.getBlocks()) {
            LinkedList<Obj> objects = new LinkedList<>(b.getObjects());
            for (Obj obj : objects) {
                fullObjectList.add((DC_HeroObj) obj);
                // TODO of course - the issue was that I added an object to
                // block too! ... init?
                DC_HeroObj unit = (DC_HeroObj) obj;

                if (z != 0)
                    unit.setZ(z);

            }
        }
        for (MapZone zone : plan.getZones()) {
            ObjType type1 = DataManager.getType(zone.getFillerType(), OBJ_TYPES.BF_OBJ);
            List<Coordinates> list = zone.getCoordinates();
            for (MapBlock b : zone.getBlocks()) {
                list.removeAll(b.getCoordinates());
            }

        }

        for (Obj obj : plan.getWallObjects()) {
            DC_HeroObj unit = (DC_HeroObj) obj;
            fullObjectList.add(unit);

            if (z != 0)
                unit.setZ(z);
        }
        if (plan.getDirectionMap() != null)
            try {
                DC_ObjInitializer.initDirectionMap(z, plan.getDirectionMap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (plan.getFlipMap() != null)
            try {
                DC_ObjInitializer.initFlipMap(z, plan.getFlipMap());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void initZones(Node planNode, DungeonPlan plan) {
        int id = 0;
        int zoneId = 0;
        List<MapZone> zones = new LinkedList<>();
        Node zonesNode = XML_Converter.getChildAt(planNode, (1));

        for (Node zoneNode : XML_Converter.getNodeList(zonesNode)) {
            MapZone zone = null;
            try {
                zone = createZone(plan, zoneId, zoneNode);
            } catch (Exception e) {
//                e.printStackTrace();
                zone = new MapZone(plan.getDungeon(), zoneId, 0, plan.getDungeon().getWidth(), 0,
                        plan.getDungeon().getHeight());
            } // ++ add coord exceptions
            zoneId++;
            zones.add(zone);
            for (Node node : XML_Converter.getNodeList(XML_Converter.getNodeList(zoneNode).get(0))) {
                // if (node.getNodeName().equalsIgnoreCase(BLOCKS_NODE))
                // blocks = initBlocks(XML_Converter.getStringFromXML(node));
                MapBlock block = constructBlock(node, id, zone, plan, getDungeon());
                id++;
                zone.addBlock(block);
                plan.getBlocks().add(block);
            }
        }

        plan.setZones(zones);
    }

    private MapZone createZone(DungeonPlan plan, int zoneId, Node zoneNode) {
        String name = zoneNode.getNodeName();
        String nodeName = XML_Writer.restoreXmlNodeName(name);
        if (!nodeName.contains(","))
            nodeName = XML_Writer.restoreXmlNodeNameOld(name);
        int[] c = CoordinatesMaster.getMinMaxCoordinates(nodeName.split(",")[1]);
        MapZone zone = new MapZone(plan.getDungeon(), zoneId, c[0], c[1], c[2], c[3]);
        return zone;
    }

    public DungeonPlan selectDungeonMap(Dungeon dungeon) {
        File file = FileManager.getRandomFile(getMapsPath()
                + dungeon.getProperty(PROPS.DUNGEON_TEMPLATE_TYPE));
        String data = FileManager.readFile(file);
        return loadDungeonMap(data);
    }

    public String getMapsPath() {
        return PathFinder.getXmlTypesFolderPath() + "dungeon maps\\";
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        if (dungeon != null)
            dungeon.getGame().getDungeonMaster().setDungeon(dungeon);
    }

    public enum ROOM_TEMPLATE {
        // DARK_STUDY("0-0=Bookshelf;"),;
    }

    public enum ROOM_TYPE {
        TREASURE_ROOM(25, 15, 3, 7, 2, 6),
        THRONE_ROOM(60, 45, 3, 0, 4, 0),
        DEATH_ROOM(30, 15, 2, 4, 3, 5),
        GUARD_ROOM(25, 25, 3, 6, 2, 4),
        COMMON_ROOM(25, 25),
        ENTRANCE_ROOM(15, 35),
        EXIT_ROOM(35, 15),
        SECRET_ROOM(15, 15, 1, 4, 3, 6),
        BATTLEFIELD(100, 100, 3, 0, 4, 0),;

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

    // how to support more loose building for, say, natural caverns?

    public enum DUNGEON_TEMPLATES {
        GREAT_ROOM, STAR, RING, CROSS, LABYRINTH, PROMENADE, SERPENT, CLASSIC, PRISON_CELLS,
    }

    // public void initBlockSpecials() {
    // for (MapBlock block : plan.getBlocks())
    // initBlock(block);
    // }
    // public void initBlock(MapBlock block) {
    // if (!(block.getType() == BLOCK_TYPE_NODE.ROOM || block.getType() ==
    // BLOCK_TYPE_NODE.SMALL_ROOM))
    // return;
    //
    // MapZone zone = block.getZone();
    // // block.getId();
    // // block.getSize();
    // ROOM_TYPE roomType = null;
    // // for (t type: roomTypes){
    // // roomType=type;
    // // if () break;
    // // }
    // block.setRoomType(roomType);
    // int spawningPriority = 0;
    // block.setSpawningPriority(spawningPriority); // relative or absolute?
    // // divided between
    // // blocks?
    //
    // if ((block.getType() == BLOCK_TYPE_NODE.ROOM || block.getType() ==
    // BLOCK_TYPE_NODE.SMALL_ROOM)) {
    // // block.setKeyCoordinates(c);
    // }
    // if (roomType == ROOM_TYPE.TREASURE_ROOM || roomType ==
    // ROOM_TYPE.THRONE_ROOM) {
    // int treasureValue = 0;
    // block.setTreasureValue(treasureValue);
    // }
    //
    // }
    //
    // public Coordinates[] getLinkCoordinates(MapBlock block, MapBlock block2)
    // {
    // // 1 cell in the opposite direction from base_c of the last block
    // // what about corridors and the like?
    // // perhaps in some cases I may want to "getAdjacent(1)" from the
    // // start...
    // List<Coordinates> adjacentToBothBlocks =
    // CoordinatesMaster.getAdjacentToBothGroups(block
    // .getCoordinates(), block2.getCoordinates());
    // int linkNumber = 1;
    // if (block.getType() == BLOCK_TYPE_NODE.ROOM && block2.getType() ==
    // BLOCK_TYPE_NODE.ROOM) {
    // if (block.getSquare() > 20)
    // linkNumber++;
    // if (block2.getSquare() > 20)
    // linkNumber++;
    // }
    // Boolean horizontal =
    // CoordinatesMaster.isHorizontalLine(adjacentToBothBlocks);
    // Boolean prefLessMoreMiddle = null;
    // DIRECTION d = !horizontal ? DEFAULT_BUILD_DIRECTION.getXDirection()
    // : DEFAULT_BUILD_DIRECTION.getYDirection();
    // Coordinates[] array = new Coordinates[linkNumber];
    // if (linkNumber == 1) {
    // array[0] = CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // adjacentToBothBlocks,
    // prefLessMoreMiddle);
    // return array;
    // }
    // List<Coordinates> list = new LinkedList<>();
    // if (linkNumber >= 3)
    // for (Boolean b : BooleanMaster.NULL_TRUE_FALSE) {
    // list.add(CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // adjacentToBothBlocks,
    // b));
    // }
    // else
    // for (Boolean b : BooleanMaster.NULL_TRUE_FALSE) {
    // list.add(CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // adjacentToBothBlocks,
    // b));
    // }
    // array = list.toArray(new Coordinates[list.size()]);
    // return array;
    // }
    // public List<MapZone> createMapZones(DungeonPlan plan) {
    // List<MapZone> zones = new ArrayList<>();
    // boolean horizontal = true;
    // Boolean prefLessMoreMiddle = null;
    // boolean equalSplitX = true; // for classic only?
    // boolean splitByX = true; // "vertical/horizontal" - to be derived from
    // // where the *entrance* is, e.g. if it's on
    // // w/e for promenade, then it's hor.;
    // // int manualX; // percentage?
    // int n = 4; // auto-zones;after special ones;to be used in loop (serpent)
    // int x1 = plan.getWallWidth();
    // int x2 = plan.getBorderX();
    // int y1 = plan.getWallWidth();
    // int y2 = plan.getBorderY();
    // if (reverseX)
    // x1 = x2;
    // if (reverseY)
    // y1 = y2;
    // int i = 0;
    // int width = plan.getCellsX(); // if (!equalSplitX) ...
    // int height = plan.getCellsY();
    // if (splitByX)
    // width = width / n;
    // else
    // height = height / n;
    //
    // boolean auto = true;
    // switch (plan.getTemplate()) {
    // case PROMENADE:
    // break;
    // case CROSS:
    // n = 4; // perhaps a single zone with
    // break;
    // case GREAT_ROOM: // depending on size...
    // MapZone zone = new MapZone(0, x1, x2, y1, y2);
    // zones.add(zone);
    // i++;
    // break;
    // }
    // if (auto) {
    // width = reverseX ? -width : width;
    // height = reverseY ? -height : height;
    // for (; i < n; i++) {
    // if (i > 0)
    // if (splitByX)
    // x1 += width;
    // else
    // y1 += height;
    //
    // MapZone zone = new MapZone(i, x1, x1 + width, y1, y1 + height);
    // main.system.auxiliary.LogMaster.log(1,
    // "New Zone: " + zone.toString());
    // zones.add(zone);
    // }
    //
    // }
    // return zones;
    // }
    // determining room types - treasure, boss, ... - how exactly?
    // public void buildBlocksClassic(Dungeon dungeon, DungeonPlan map,
    // List<MapBlock> blocks, Map<ObjType, Coordinates> objMap,
    // DUNGEON_TEMPLATES template) {
    // int i = 0;
    // while (true) {
    // // entrance point - base coordinate for next block
    // // new MapBlock(b, zone, map, coordinates);
    // BLOCK_TYPE b = !altBlock ? BLOCK_TYPE.ROOM : BLOCK_TYPE.CORRIDOR;
    //
    // while (i < map.getZones().size()) {
    // MapZone zone = map.getZones().getOrCreate(i);
    // MapZone targetZone = map.getZones().getOrCreate(i);
    // MapBlock block = buildBlock(template, zone, targetZone, b, map);
    // if (block != null)
    // break;
    // i++;
    // }
    // // spec func to link for each block_type
    // map.getBlocks().add(block);
    // // block.setRoomType(roomType); TODO if (?)
    // if (i >= map.getZones().size())
    // break;
    // }
    // }
    // switch (template) {
    // case CLASSIC:
    // // room-corridor pairs: random at each step - choose room to add
    // // corridor, choose corridor to add room...
    // break;
    // case CROSS:
    // // 4 cul-de-sacs with center being base (or not?)
    // for (int i = 0; i < 4; i++) {
    // // ++ offsets? min/max?
    // // buildBlock(template, MAP_ZONES.CENTER,
    // // MAP_ZONES.SIDE_EAST,
    // // null, map);
    // }
    // break;
    // case GREAT_ROOM:
    // // corridors all leading up to the Room; randomize small rooms
    // // and culdesacs
    // break;
    // case LABYRINTH:
    // // randomized serpent with cul-de-sacs
    // break;
    // case PRISON_CELLS:
    // // cross/corridor with mini rooms along it
    // break;
    // case PROMENADE:
    // // single wide corridor
    // // additionalTemplates = 2;
    // break;
    // case RING:
    // // invert circle room
    // // additionalTemplates = 2;// zones!
    // break;
    // case SERPENT:
    // // edge corridors with growing centric offset, maximum walking
    // // distance basically
    // break;
    // case STAR:
    // break;
    // }
    // // ++ additional deviations?
    // // initBlockSpecials();
}
