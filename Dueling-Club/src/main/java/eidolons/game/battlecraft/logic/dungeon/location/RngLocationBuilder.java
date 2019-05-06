package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.ability.UnitTrainingMaster;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.fill.RngFillMaster;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelPopulator;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.*;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Node;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster.SEPARATOR;
import static eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster.UNITS_NODE;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngLocationBuilder extends LocationBuilder {
    private String entranceData;
    private DUNGEON_STYLE mainStyle;

    public RngLocationBuilder(LocationMaster locationMaster) {
        super(locationMaster);
    }

    public Location buildDungeon(String path) {
        if (game.getMetaMaster().getMetaDataManager().getMetaGame() instanceof ScenarioMeta) {
            String prop = ((ScenarioMeta) game.getMetaMaster().getMetaDataManager().getMetaGame()).getScenario().getProperty(
                    PROPS.DUNGEON_STYLE);
            mainStyle = new EnumMaster<DUNGEON_STYLE>().retrieveEnumConst(DUNGEON_STYLE.class, prop);
        }
        String data = FileManager.readFile(path);

        if (data.isEmpty()) {
            data = FileManager.readFile(
                    path.contains(PathFinder.getDungeonLevelFolder()) ? path
                            : PathFinder.getDungeonLevelFolder() + path);
        }
        if (!data.isEmpty()) {
            path = data;
        }
        DungeonLevel level = loadLevel(path);
        initPower(level);
        master.setDungeonLevel(level);

        if (mainStyle != null) {
            for (LevelZone zone : level.getZones()) {
                if (zone.getStyle() == level.getMainStyle())
                    zone.setStyle(mainStyle);
            }
            level.setMainStyle(mainStyle);
        }
        new RngLevelInitializer().init(level);
        Location location = new Location((LocationMaster) getMaster(), new Dungeon(level.getDungeonType()));
        initWidthAndHeight(location);
        location.setEntranceData(level.getEntranceData());
        location.setLevelFilePath(path.replace(PathFinder.getDungeonLevelFolder(), ""));
        location.initEntrances();
        level.getObjects().removeIf(c ->
                !EntityCheckMaster.isEntrance(c.getType()) && (
                        c.getCoordinates().equals(location.getMainEntrance().getCoordinates())
                                || c.getCoordinates().equals(location.getMainExit().getCoordinates()))
        );
        //        initDynamicObjData();


        return location;
    }

    private void initPower(DungeonLevel level) {
        Party party = getGame().getMetaMaster().getPartyManager().
                getParty();
        try {
            level.setPowerLevel(getGame().getMetaMaster().getPartyManager().
                    getParty().getParamSum(PARAMS.POWER) * (1 + party.getMembers().size()) / party.getMembers().size());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    public void initLevel() {
        RngLevelPopulator.populate(getMaster().getDungeonLevel());
        spawnLevel(getMaster().getDungeonLevel());
    }

    public void spawnLevel(DungeonLevel level) {
        DC_Game game = getGame();
        for (ObjAtCoordinate at : level.getObjects()) {
            //            DC_ObjInitializer.initMapBlockObjects()
//           if (CoreEngine.isFullFastMode()){
//               if (!EntityCheckMaster.isWall(at.getType()))
//               if (!EntityCheckMaster.isEntrance(at.getType()))
//               if (!EntityCheckMaster.isDoor(at.getType()))
//                   continue;
//           }
            game.createUnit(at.getType(), at.getCoordinates().x, at.getCoordinates().y, DC_Player.NEUTRAL);
            main.system.auxiliary.log.LogMaster.log(1, at + " spawed");
        }
        for (ObjAtCoordinate at : level.collectUnits()) {
            Unit unit = (Unit) game.createUnit(at.getType(), at.getCoordinates().x, at.getCoordinates().y,
                    game.getPlayer(false));
            UnitTrainingMaster.train(unit);
            main.system.auxiliary.log.LogMaster.log(1, at + " unit spawed");
        }


        if (level.getDirectionMap() != null) {
            try {
                DC_ObjInitializer.initDirectionMap(0, level.getDirectionMap());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (level.getFlipMap() != null) {
            try {
                DC_ObjInitializer.initFlipMap(0, level.getFlipMap());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    public static DungeonLevel loadLevel(String path) {
        String xml = FileManager.readFile(path);
        if (xml.isEmpty())
            xml = path;
        //        TODO
        DungeonLevel level = new RestoredDungeonLevel();

        int n = 0;
        for (Node node : XML_Converter.getNodeListFromFirstChild(XML_Converter.getDoc(xml), true)) {
            //TODO
            if (node.getNodeName().equalsIgnoreCase(RngXmlMaster.ZONES_NODE)) {
                LevelZone zone = new LevelZone(n++);
                List<Node> subNodes = XML_Converter.getNodeList(node);
                for (Node zoneNode : new ArrayList<>(subNodes)) {
                    subNodes = XML_Converter.getNodeList(zoneNode);
                    for (Node subNode : subNodes) {
                        if (subNode.getNodeName().equalsIgnoreCase(RngXmlMaster.BLOCKS_NODE)) {
                            for (Node blockNode : XML_Converter.getNodeList(subNode)) {
                                zone.getSubParts().add(createBlock(blockNode, zone));
                            }
                        } else {
                            if (subNode.getNodeName().equalsIgnoreCase(RngXmlMaster.VALUES_NODE)) {
                                processZoneValues(zone, subNode);
                            }
                        }
                    }
                }
                level.getSubParts().add(zone);
            } else {
                String output = processNode(node, level);
                if (output != null) {
                    level.setEntranceData(output);
                }
            }
        }

        //        style, ambi, color, ill, id
        //spawn(); TODO so lvls are really all except units
        return level;
    }

    private static String getBaseDungeonTypeName(DungeonLevel level) {
        String s = StringMaster.getWellFormattedString(
                level.getLocationType().toString());
        if (DataManager.isTypeName(s
                , DC_TYPE.DUNGEONS))
            return s;
        switch (level.getLocationType()) {
            case CAVE:
                return DUNGEONS_OBJ_TYPES.CAVERN.getName();
            case TOWER:
                return DUNGEONS_OBJ_TYPES.ARCANE_TOWER.getName();
        }
        return s;
    }

    protected static String processNode(Node n, DungeonLevel level) {


        if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.NON_VOID_NODE)) {
            initNonVoidData(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.OBJECTS_NODE)) {
            initObjData(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.AI_GROUPS_NODE)) {
            initAiData(n.getTextContent(), level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.LEVEL_DATA_NODE)) {
            initData(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.BOUND_NODE)) {
            try {
                initBound(n, level);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.ENTRANCES_NODE)) {
            return n.getTextContent();
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.TILEMAP_NODE)) {
            initTileMap(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.VALUES_NODE)) {
            //            SUBLEVEL_TYPE sublevelType;
            initValuesNode(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), (FLIP_MAP_NODE))) {
            level.setFlipMap(new RandomWizard<FLIP>().constructStringWeightMapInversed(n
                    .getTextContent(), FLIP.class));
        } else if (StringMaster.compareByChar(n.getNodeName(), (DIRECTION_MAP_NODE))) {
            level.setDirectionMapData(n.getTextContent());
            level.setDirectionMap(new RandomWizard<DIRECTION>()
                    .constructStringWeightMapInversed(n.getTextContent(), DIRECTION.class));

        }
        //        else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PARAMS_NODE))) {
        //            TypeBuilder.setParams(type, n);
        //        } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PROPS_NODE))) {
        //            TypeBuilder.setProps(type, n);
        //        }
        return null;
    }

    private static void initNonVoidData(Node n, DungeonLevel level) {
        Set<Coordinates> coords = new LinkedHashSet<>(CoordinatesMaster.getCoordinatesFromString(n.getTextContent()));
        level.setNonVoidCoordinates(coords);
    }

    private static void initObjData(Node n, DungeonLevel level) {
        //units will be init at AigroupsInit()
        String separator = SEPARATOR;
        if (!n.getTextContent().contains(separator)) {
            separator = ",";
        }
        for (String s : n.getTextContent().split(separator)) {
            ObjAtCoordinate obj = new ObjAtCoordinate(s, DC_TYPE.BF_OBJ);
            if (!obj.isValid()) {
                obj = new ObjAtCoordinate(s, DC_TYPE.UNITS);
            }

            level.addObj(obj);

        }
        level.setPregen(true);
    }

    public static Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> initAiData(String data, DungeonLevel level) {

        Map<List<ObjAtCoordinate>, UNIT_GROUP_TYPE> map = null;

        for (String line : StringMaster.splitLines(data)) {
            String[] parts = line.split(RngXmlMaster.AI_GROUP_SEPARATOR);
            UNIT_GROUP_TYPE type = UNIT_GROUP_TYPE.valueOf(
                    parts[0].toUpperCase());
            if (parts.length < 2) {
                continue;
            }
            List<ObjAtCoordinate> group = createObjGroup(parts[1]);

            if (level == null) {
                if (map == null) {
                    map = new HashMap<>();
                }
            } else
                for (ObjAtCoordinate obj : group) {
                    LevelBlock block = level.getBlockForCoordinate(obj.getCoordinates());
                    if (block == null)
                        continue;
                    map = block.getUnitGroups();
                    break;
                }
            map.put(group, type);

        }
        if (!data.isEmpty())
            if (level != null)
                level.setPregen(true);
        return map;
    }

    public static List<ObjAtCoordinate> createObjGroup(String data) {
        return Arrays.stream(data.split(";")).map(s -> new ObjAtCoordinate(s,
                DC_TYPE.UNITS)).collect(Collectors.toList());
    }

    private static void initBound(Node n, DungeonLevel level) {
        for (String s : ContainerUtils.openContainer(n.getTextContent())) {
            AbstractCoordinates c = new AbstractCoordinates(s.split("=")[0]);
            AbstractCoordinates c2 = new AbstractCoordinates(s.split("=")[1]);
            LevelBlock b = level.getBlockForCoordinate(c);
            RngFillMaster.bindCoordinates(b, c, c2);
        }
    }

    private static void initData(Node n, DungeonLevel level) {
        level.setData(new LevelData(n.getTextContent()));
    }


    private static void processZoneValues(LevelZone zone, Node subNode) {
        for (Node node : XML_Converter.getNodeList(subNode)) {
            String name = node.getNodeName().toUpperCase();
            switch (name) {
                case RngXmlMaster.ZONE_STYLE_NODE:
                    DUNGEON_STYLE style = new EnumMaster<DUNGEON_STYLE>()
                            .retrieveEnumConst(DUNGEON_STYLE.class, node.getTextContent());
                    zone.setStyle(style);
                    break;
                case RngXmlMaster.ZONE_TYPE_NODE:
                    zone.setType(new EnumMaster<ZONE_TYPE>()
                            .retrieveEnumConst(ZONE_TYPE.class, node.getTextContent()));
                    break;
                case RngXmlMaster.ZONE_TEMPLATE_GROUP_NODE:
                    zone.setTemplateGroup(new EnumMaster<ROOM_TEMPLATE_GROUP>()
                            .retrieveEnumConst(ROOM_TEMPLATE_GROUP.class, node.getTextContent()));
                    break;
            }
        }
    }

    private static void initTileMap(Node n, DungeonLevel level) {
        String data = n.getTextContent();
        int lineN = 0;
        int w = 0;
        Map<Coordinates, ROOM_CELL> map = new LinkedHashMap<>();
        String[] lines = StringMaster.splitLines(data);
        for (String line : lines) {
            if (lineN++ < RngXmlMaster.SKIPPED_LINES)
                continue;
            if (lineN > lines.length - RngXmlMaster.SKIPPED_LINES) {
                break;
            }
            String row = line.split(Pattern.quote(RngXmlMaster.TILEMAP_ROW_SEPARATOR))[1];
            row = row.replace(" ", "");
            int i = 0;
            for (char c : row.toCharArray()) {
                ROOM_CELL cell = ROOM_CELL.getBySymbol(c + "");
                i++;
                if (cell != null) {
                    map.put(new AbstractCoordinates(i - 1, lineN - RngXmlMaster.SKIPPED_LINES - 1), cell);
                } else {
                    continue;
                }
            }
            if (i > w)
                w = i;
        }
        TileMap tileMap = new TileMap(map);
        //        String[][] cells = TileMapper.toSymbolArray(TileMapper.getCells(tileMap));
        //        ArrayMaster.
        level.setTileMap(tileMap);
    }

    private static void initValuesNode(Node n, DungeonLevel level) {
        Node node = XML_Converter.find(n, RngXmlMaster.LOCATION_TYPE_NODE);
        if (node != null) {
            LOCATION_TYPE locationType = new EnumMaster<LOCATION_TYPE>().
                    retrieveEnumConst(LOCATION_TYPE.class, node.getTextContent());
            level.setLocationType(locationType);
        }
        node = XML_Converter.find(n, RngXmlMaster.SUBLEVEL_TYPE_NODE);
        if (node != null) {
            SUBLEVEL_TYPE locationType = new EnumMaster<SUBLEVEL_TYPE>().
                    retrieveEnumConst(SUBLEVEL_TYPE.class, node.getTextContent());
            level.setSublevelType(locationType);
        }
        ObjType type = DataManager.getType(getBaseDungeonTypeName(level),
                DC_TYPE.DUNGEONS);
        if (type == null) {
            type = DataManager.getRandomType(DC_TYPE.DUNGEONS, null);
        }
        if (!type.checkProperty(PROPS.MAP_BACKGROUND)
                || !TextureCache.isImage(type.getProperty(PROPS.MAP_BACKGROUND))
                || isPresetBackground()) {
            type.setProperty(PROPS.MAP_BACKGROUND, getBackground(level.getLocationType()).getBackgroundFilePath());
        }
        node = XML_Converter.find(n, PARAMS.BF_HEIGHT.name());
        if (node != null) {
            type.setValue(PARAMS.BF_HEIGHT.name(), node.getTextContent());
        }
        node = XML_Converter.find(n, PARAMS.BF_WIDTH.name());
        if (node != null) {
            type.setValue(PARAMS.BF_WIDTH.name(), node.getTextContent());
        }
        level.setDungeonType(type);

    }

    private static boolean isPresetBackground() {
        return true;
    }

    private static MAP_BACKGROUND getBackground(LOCATION_TYPE locationType) {
        switch (locationType) {
            case CEMETERY:
                if (RandomWizard.chance(35)) {
                    return RandomWizard.random() ? MAP_BACKGROUND.RAVENWOOD_EVENING : MAP_BACKGROUND.RAVENWOOD;
                }
                return MAP_BACKGROUND.CEMETERY;
            case CRYPT:
                if (RandomWizard.chance(35)) {
                    return MAP_BACKGROUND.BASTION_DARK;
                }
                if (RandomWizard.chance(35)) {
                    return MAP_BACKGROUND.BASTION;
                }
                return MAP_BACKGROUND.CEMETERY;

            case DUNGEON:
                if (RandomWizard.chance(35)) {
                    return MAP_BACKGROUND.CAVE;
                }
                return MAP_BACKGROUND.TUNNEL;

            case CAVE:
            case BARROW:
                if (RandomWizard.chance(35)) {
                    return MAP_BACKGROUND.TUNNEL;
                }
                return MAP_BACKGROUND.CAVE;
            //            case SHIP:
            //                return MAP_BACKGROUND.SHIP;

            case CASTLE:
                if (RandomWizard.chance(65)) {
                    return MAP_BACKGROUND.BASTION;
                }
                if (RandomWizard.chance(35)) {
                    return MAP_BACKGROUND.BASTION_DARK;
                }
            case TOWER:
                if (RandomWizard.chance(50)) {
                    return MAP_BACKGROUND.SHIP;
                }
                return MAP_BACKGROUND.TOWER;
            case TEMPLE:
                if (RandomWizard.chance(30)) {
                    return MAP_BACKGROUND.ELVEN_RUINS;
                }
                return MAP_BACKGROUND.TOWER;


            case GROVE:
                return RandomWizard.random() ? MAP_BACKGROUND.RAVENWOOD_EVENING : MAP_BACKGROUND.RAVENWOOD;

            case HIVE:
            case DEN:
                return MAP_BACKGROUND.SPIDER_GROVE;
            case RUIN:
                return MAP_BACKGROUND.ELVEN_RUINS;
        }
        return MAP_BACKGROUND.CAVE;
    }

    protected static LevelBlock createBlock(Node node, LevelZone zone) {
        LevelBlock b = new LevelBlock(zone);

        for (Node subNode : XML_Converter.getNodeList(node)) {
            if (StringMaster.compareByChar(subNode.getNodeName(), COORDINATES_NODE)) {
                //                if (subNode.getTextContent().isEmpty()){
                //              TODO     b.initDefaultCoordinatesList();
                //                } else
                b.setCoordinatesList(CoordinatesMaster.
                        getCoordinatesFromString(subNode.getTextContent()));
            } else if (StringMaster.compareByChar(subNode.getNodeName(), OBJ_NODE)) {
                for (String s : ContainerUtils.open(subNode.getTextContent())) {
                    b.getObjects().add(
                            new ObjAtCoordinate(s.split("=")[1], s.split("=")[0], DC_TYPE.BF_OBJ));
                }
                //                 DC_ObjInitializer.initMapBlockObjects(dungeon, b, subNode.getTextContent());
            } else {
                if (StringMaster.compareByChar(subNode.getNodeName(), UNITS_NODE)) {
                    if (isPrespawned()) {
                        for (String s : ContainerUtils.open(subNode.getTextContent())) {
                            b.getObjects().add(
                                    new ObjAtCoordinate(s.split("=")[0], s.split("=")[1], DC_TYPE.UNITS));
                        }
                    }

                } else if (StringMaster.compareByChar(subNode.getNodeName(), RngXmlMaster.BLOCK_ROOM_TYPE_NODE)) {
                    b.setRoomType(new EnumMaster<ROOM_TYPE>().
                            retrieveEnumConst(ROOM_TYPE.class, subNode.getTextContent()));
                } else if (StringMaster.compareByChar(subNode.getNodeName(), RngXmlMaster.COLOR_THEME)) {
                    b.setColorTheme(new EnumMaster<COLOR_THEME>().
                            retrieveEnumConst(COLOR_THEME.class, subNode.getTextContent()));
                } else if (StringMaster.compareByChar(subNode.getNodeName(), RngXmlMaster.COLOR_THEME_ALT)) {
                    b.setAltColorTheme(new EnumMaster<COLOR_THEME>().
                            retrieveEnumConst(COLOR_THEME.class, subNode.getTextContent()));
                }
            }
        }
        return b;
    }

    private static boolean isPrespawned() {
        return false;
    }
}
