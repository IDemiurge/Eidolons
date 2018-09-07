package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
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
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.DUNGEONS_OBJ_TYPES;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.MAP_BACKGROUND;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster.UNITS_NODE;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngLocationBuilder extends LocationBuilder {
    private String entranceData;

    public RngLocationBuilder(LocationMaster locationMaster) {
        super(locationMaster);
    }

    public Location buildDungeon(String path) {
        String data = FileManager.readFile(path);

        if (data.isEmpty()) {
            data = FileManager.readFile(
             path.contains(PathFinder.getDungeonLevelFolder()) ? path
              : PathFinder.getDungeonLevelFolder() + path);
        }
        if (data.isEmpty()) {
            data = path;
        }
        DungeonLevel level = loadLevel(path);
        master.setDungeonLevel(level);
        new RngLevelInitializer().init(level);
        Location location = new Location((LocationMaster) getMaster(), new Dungeon(level.getDungeonType()));
        initWidthAndHeight(location);
        location.setEntranceData(entranceData);
        RngLevelPopulator.populate(level);
        spawnLevel(level);
        location.setLevelFilePath(path.replace(PathFinder.getDungeonLevelFolder(), ""));
        location.initEntrances();
        //        initDynamicObjData();
        return location;
    }

    public void spawnLevel(DungeonLevel level) {
        DC_Game game = getGame();
        for (ObjAtCoordinate at : level.getObjects()) {
            //            DC_ObjInitializer.initMapBlockObjects()
            game.createUnit(at.getType(), at.getCoordinates().x, at.getCoordinates().y, DC_Player.NEUTRAL);
            main.system.auxiliary.log.LogMaster.log(1, at + " spawed");
        }
        for (ObjAtCoordinate at : level.getUnits()) {
            game.createUnit(at.getType(), at.getCoordinates().x, at.getCoordinates().y,
             game.getPlayer(false));
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


    public DungeonLevel loadLevel(String path) {
        String xml = FileManager.readFile(path);
        //        TODO
        DungeonLevel level = new RestoredDungeonLevel();
        Party party = getGame().getMetaMaster().getPartyManager().
         getParty();

        level.setPowerLevel(getGame().getMetaMaster().getPartyManager().
         getParty().getParamSum(PARAMS.POWER) * (1 + party.getMembers().size()) / party.getMembers().size());

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
                processNode(node, level);
            }
        }

        //        style, ambi, color, ill, id
        //spawn(); TODO so lvls are really all except units
        return level;
    }

    private String getBaseDungeonTypeName(DungeonLevel level) {
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

    protected void processNode(Node n, DungeonLevel level) {

        if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.LEVEL_DATA_NODE)) {
            initData(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.BOUND_NODE)) {
            try {
                initBound(n, level);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.ENTRANCES_NODE)) {
            initEntrances(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.TILEMAP_NODE)) {
            initTileMap(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), RngXmlMaster.VALUES_NODE)) {
            //            SUBLEVEL_TYPE sublevelType;
            initValuesNode(n, level);
        } else if (StringMaster.compareByChar(n.getNodeName(), (FLIP_MAP_NODE))) {
            level.setFlipMap(new RandomWizard<FLIP>().constructStringWeightMapInversed(n
             .getTextContent(), FLIP.class));
        } else if (StringMaster.compareByChar(n.getNodeName(), (DIRECTION_MAP_NODE))) {
            level.setDirectionMap(new RandomWizard<DIRECTION>()
             .constructStringWeightMapInversed(n.getTextContent(), DIRECTION.class));

        }
        //        else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PARAMS_NODE))) {
        //            TypeBuilder.setParams(type, n);
        //        } else if (StringMaster.compareByChar(n.getNodeName(), (CUSTOM_PROPS_NODE))) {
        //            TypeBuilder.setProps(type, n);
        //        }
    }

    private void initBound(Node n, DungeonLevel level) {
        for (String s : ContainerUtils.openContainer(n.getTextContent())) {
            AbstractCoordinates c = new AbstractCoordinates(s.split("=")[0]);
            AbstractCoordinates c2 = new AbstractCoordinates(s.split("=")[1]);
            LevelBlock b = level.getBlockForCoordinate(c);
            RngFillMaster.bindCoordinates(b, c, c2);
        }
    }

    private void initData(Node n, DungeonLevel level) {
        level.setData(new LevelData(n.getTextContent()));
    }

    private void initEntrances(Node n, DungeonLevel level) {
        this.entranceData = n.getTextContent();
    }

    private void processZoneValues(LevelZone zone, Node subNode) {
        for (Node node : XML_Converter.getNodeList(subNode)) {
            String name = node.getNodeName().toUpperCase();
            switch (name) {
                case RngXmlMaster.ZONE_STYLE_NODE:
                    zone.setStyle(new EnumMaster<DUNGEON_STYLE>()
                     .retrieveEnumConst(DUNGEON_STYLE.class, node.getTextContent()));
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

    private void initTileMap(Node n, DungeonLevel level) {
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

    private void initValuesNode(Node n, DungeonLevel level) {
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
         || !TextureCache.isImage(type.getProperty(PROPS.MAP_BACKGROUND)))
        {
            type.setProperty(PROPS.MAP_BACKGROUND, MAP_BACKGROUND.DUNGEON.getBackgroundFilePath());
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

    protected LevelBlock createBlock(Node node, LevelZone zone) {
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
                }else if (StringMaster.compareByChar(subNode.getNodeName(), RngXmlMaster.COLOR_THEME_ALT)) {
                    b.setAltColorTheme(new EnumMaster<COLOR_THEME>().
                     retrieveEnumConst(COLOR_THEME.class, subNode.getTextContent()));
                }
            }
        }
        return b;
    }

    private boolean isPrespawned() {
        return false;
    }
}
