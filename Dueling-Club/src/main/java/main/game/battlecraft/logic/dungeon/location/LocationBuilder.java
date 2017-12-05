package main.game.battlecraft.logic.dungeon.location;

import main.content.PARAMS;
import main.content.PROPS;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.location.building.BuildHelper;
import main.game.battlecraft.logic.dungeon.location.building.BuildHelper.BUILD_PARAMS;
import main.game.battlecraft.logic.dungeon.location.building.BuildHelper.BuildParameters;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.location.building.MapZone;
import main.game.battlecraft.logic.dungeon.test.TestDungeonInitializer;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.swing.XDimension;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.math.MathMaster;
import main.system.util.Refactor;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

public class LocationBuilder extends DungeonBuilder<Location> {
    public static final DIRECTION DEFAULT_BUILD_DIRECTION = DIRECTION.DOWN_RIGHT;
    public static final String BLOCK_TYPE_NODE = "Block Type";
    public static final String BLOCKS_NODE = "Blocks";
    public static final String ROOM_TYPE_NODE = "Room Type";
    public static final String COORDINATES_NODE = "Coordinates";
    public static final String OBJ_NODE = "Objects";
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
    public static final String ZONES_NODE = "Zones";
    public static final String AI_GROUPS_NODE = StringMaster
            .getWellFormattedString("ai groups node");
    private DungeonPlan plan;
    private Dungeon dungeon;
    private BuildHelper helper;
    private DUNGEON_TEMPLATES template;
    private boolean flipX;
    private boolean flipY;
    private BuildParameters params;
    @Refactor
    private List<Node> nodeList;
    private Location location;

    public LocationBuilder() {
        super(null);
    }

    public LocationBuilder(DungeonMaster master) {
        super(master);
    }

    public static List<ROOM_TYPE> getDefaultMainRooms(DUNGEON_TEMPLATES template) {
        List<ROOM_TYPE> list = new ArrayList<>();
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


    public LocationBuilder setParam(BuildParameters params) {
        this.params = params;
        return this;
    }

    @Override
    public Location buildDungeon(String data, List<Node> nodeList) {
        this.nodeList = nodeList;
        location = (super.buildDungeon(data, nodeList));
        DUNGEON_TEMPLATES template = null;
        DungeonPlan plan = new DungeonPlan(template, (location));
        plan.setLoaded(true);
        for (Node n : nodeList) {
            processNode(n, getDungeon(), plan);

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

    public DungeonPlan transformDungeonPlan(DungeonPlan plan) {
        Coordinates.setFlipX(plan.isFlippedX());
        Coordinates.setFlipY(plan.isFlippedY());
        Coordinates.setRotated(plan.isRotated());
        // TODO CHANGE DIMENSIONS
        try {
            plan = loadDungeonMap(plan.getXml());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            Coordinates.setFlipX(false);
            Coordinates.setFlipY(false);
            Coordinates.setRotated(false);
        }
        return plan;
    }

    // static use and save as templates...
    public DungeonPlan buildDungeonPlan(Location location) {
        // location = new Location(dungeon, 1);
        // dungeon.getLocation();

        if (helper == null) {
            helper = new BuildHelper(location, params);
            helper.setPlan(plan);
        }
        if (StringMaster.isEmpty(helper.getParams().getValue(BUILD_PARAMS.FILLER_TYPE))) {
            {
                if (!location.isUnderground()) {
                    // return;
                }
                plan = new DungeonPlan(testTemplate, location);
                int x1 = 0;
                int x2 = params.getIntValue(BUILD_PARAMS.WIDTH);
                if (x2 <= 0) {
                    x2 = location.getCellsX();
                } else {
                    location.setParam(PARAMS.BF_WIDTH, x2);
                }
                int y1 = 0;
                int y2 = params.getIntValue(BUILD_PARAMS.HEIGHT);
                if (y2 <= 0) {
                    y2 = location.getCellsY();
                } else {
                    location.setParam(PARAMS.BF_HEIGHT, y2);
                }
                MapZone zone = new MapZone(location.getDungeon(), 0, x1, x2, y1, y2);
                List<Coordinates> coordinates = CoordinatesMaster.getCoordinatesWithin(x1 - 1,
                        x2 - 1, y1 - 1, y2 - 1);
                new MapBlock(0, BLOCK_TYPE.ROOM, zone, plan, coordinates);
                plan.getZones().add(zone);

                return plan;
            }
        }
        if (TestDungeonInitializer.PRESET_PLAN != null) {
            File file = FileManager.getFile(PathFinder.getDungeonLevelFolder()
                    + TestDungeonInitializer.PRESET_PLAN);
            if (file.isFile()) {
                String data = FileManager.readFile(file);
                DungeonPlan plan = null;
                try {
                    plan = loadDungeonMap(data);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                return plan;
            }
        }
        // preset in location-mission, based
        // on Level?
        // yes, any dungeon could have any template more or less,

        template = location.getTemplate();

        if (testMode) {
            template = testTemplate;
        }
        // else if ()
        // template = new
        // RandomWizard<DUNGEON_TEMPLATES>().getObjectByWeight(dungeon
        // .getProperty(PROPS.DUNGEON_TEMPLATES), DUNGEON_TEMPLATES.class);

        if (template == null) {
            template = DUNGEON_TEMPLATES.CLASSIC;
        }
        List<MapBlock> blocks = null;
        Map<ObjType, Coordinates> objMap = null;
        plan = new DungeonPlan(template, location);
        location.setPlan(plan);
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
        plan.setRotated(location.isRotated());

        plan.setFlippedX(location.isFlippedX());
        plan.setFlippedY(location.isFlippedY());

        placeMainRooms();
        if (!location.isSurface()) {
            placeCulDeSacs();
        }
        if (!location.isSurface() && !helper.getParams().isNoRandomRooms()) {
            placeAdditionalRooms();
        }
        if (!location.isSurface() && !helper.getParams().isNoCorridors()) {
            linkWithCorridors();
        }
        // initBlockSpecials();

        return plan;
    }

    private List<MapZone> createMapZones() {
        List<MapZone> list = new ArrayList<>();
        int i = 0;
        MapZone zone = new MapZone(getDungeon().getDungeon(), i, 0, getDungeon().getCellsX(), 0, getDungeon()
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

        {
            list = getDefaultMainRooms(template);
        }
        // add custom from dungeon

        if (getDungeon().isSurface()) {
            placeMainRoom(ROOM_TYPE.BATTLEFIELD, template);
        } else {
            for (ROOM_TYPE type : list) {
                placeMainRoom(type, template);
            }
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
            if (attempts - remainingAttempts > 6) {
                spec = RandomWizard.chance(85);
            }

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
                    / (getDungeon().getCellsX() * getDungeon().getCellsY()) > helper.getParams().PREFERRED_FILL_PERCENTAGE) {
                break;
            }
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
            if (helper.tryPlaceCorridor(block, baseCoordinate, direction, true)) {
                n--;
            }
            if (n < 0) {
                break;
            }

        }
    }

    private void linkWithCorridors() {
        // TODO link each room, some more than once...
        List<MapBlock> blocksToLink = new ArrayList<>(plan.getBlocks()); // TODO
        for (MapBlock b : plan.getBlocks()) {
            if (b.getType() == BLOCK_TYPE.CULDESAC) {
                blocksToLink.remove(b);
            }
        }

        Loop.startLoop(10000); // TODO fail condition? u
        while (!blocksToLink.isEmpty()) {
            List<MapBlock> blocksToRemove = new ArrayList<>();
            for (MapBlock block : blocksToLink) {
                if (block.getConnectedBlocks().size() > 1) {
                    blocksToRemove.add(block);
                    continue;
                }
                FACING_DIRECTION direction = FacingMaster.getRandomFacing();
                Coordinates baseCoordinate = helper.getRandomWallCoordinate(direction, block);
                if (helper.tryPlaceCorridor(block, baseCoordinate, direction)) {
                    if (block.getConnectedBlocks().size() > 1) // TODO depends!
                    {
                        blocksToRemove.add(block);
                    }
                }
            }
            if (Loop.loopEnded()) {
                break;
            }
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
                    if (c.isInvalid()) {
                        continue;
                    }
                    if (helper.getUsedCoordinates().contains(c)) {
                        continue;
                    }
                    return c;
                }
            }
            return null;
        }

        Loop.startLoop(100);
        while (!Loop.loopEnded()) {
            c = CoordinatesMaster.getRandomCoordinate(getDungeon().getCellsX(), getDungeon()
                    .getCellsY());
            if (helper.getUsedCoordinates().contains(c)) {
                continue;
            }
            // add the outer walls to usedCoordinates?
            if (corner) {
                if (Math.min(plan.getBorderX() - c.x, c.x) + Math.min(plan.getBorderY() - c.y, c.y) > 6) {
                    continue;
                }
            }
            break;
        }
        return c;
    }

    public DungeonPlan loadDungeonMap(String data) {
        return buildDungeon(data, nodeList).getPlan();
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

    public DungeonPlan selectDungeonMap(Dungeon dungeon) {
        File file = FileManager.getRandomFile(getMapsPath()
                + dungeon.getProperty(PROPS.DUNGEON_TEMPLATE_TYPE));
        String data = FileManager.readFile(file);
        return loadDungeonMap(data);
    }

    public String getMapsPath() {
        return PathFinder.getTYPES_PATH() + "dungeon maps\\";
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

    public enum ROOM_TEMPLATE {
        // DARK_STUDY("0-0=Bookshelf;"),;
    }

    // how to support more loose building for, say, natural caverns?

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
    // List<Coordinates> list = new ArrayList<>();
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
