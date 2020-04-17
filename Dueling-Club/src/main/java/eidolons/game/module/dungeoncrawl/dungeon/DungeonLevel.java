package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.generator.LevelData;
import eidolons.game.module.generator.fill.RngFillMaster;
import eidolons.game.module.generator.init.RngMainSpawner;
import eidolons.game.module.generator.init.RngXmlMaster;
import eidolons.game.module.generator.model.LevelModel;
import eidolons.game.module.generator.test.LevelStats;
import eidolons.game.module.generator.tilemap.TileMap;
import eidolons.game.module.generator.tilemap.TileMapper;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE_GROUP;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.launch.CoreEngine;

import java.util.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class DungeonLevel  {

    private TileMap tileMap;
    private LevelModel model;
    private SUBLEVEL_TYPE sublevelType;
    private LOCATION_TYPE locationType;
    private List<ObjAtCoordinate> objects = new ArrayList<>();
    private String directionMapData;
    private int powerLevel;
    private Map<String, DIRECTION> directionMap;
    private Map<String, FLIP> flipMap;
    private ObjType dungeonType;
    private Map<Coordinates, LevelBlock> cache = new HashMap<>();
    private String exitType;
    private String entranceType;
    private float rate;
    private Coordinates entranceCoordinates;
    private Coordinates exitCoordinates;
    private LevelStats stats;
    private DUNGEON_STYLE mainStyle;
    private boolean surface;

    String name;
    private Map<Coordinates, FACING_DIRECTION> unitFacingMap;
    private Map<Coordinates, DungeonEnums.CELL_IMAGE> cellTypeSpecialMap = new HashMap<>();
    private boolean pregen;
    private String entranceData;

    public DungeonLevel(String name) {
        this(null, null, null);
        this.name = name;
    }

    public DungeonLevel(LevelModel model, SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        //        this.tileMap = TileMapper.createTileMap(model);
        this.model = model;
        if (model != null)
            this.tileMap = new TileMapper(model, model.getData()).joinTileMaps();
        this.sublevelType = type;
        setLocationType(locationType);
        if (tileMap != null)
            initEntranceAndExit();
    }

    private void initEntranceAndExit() {

        entranceCoordinates = tileMap.getMap().keySet().stream().filter(c ->
                tileMap.getMap().get(c) == ROOM_CELL.ENTRANCE).findFirst().orElse(null);

        exitCoordinates = tileMap.getMap().keySet().stream().filter(c ->
                tileMap.getMap().get(c) == ROOM_CELL.EXIT).findFirst().orElse(null);

    }

    @Override
    public String toString() {
        return toXml();
    }

    public String toXml() {
        //TODO save original model map!
        model.rebuildCells();
        String xml = "";
        tileMap = new TileMapper(model, model.getData()).joinTileMaps();
        xml +=
                XML_Converter.wrap(RngXmlMaster.TILEMAP_NODE,
                        TileMapper.toASCII_String(tileMap, false, true, true));

        String values = "";
        values += "\n" + XML_Converter.wrap(RngXmlMaster.LOCATION_TYPE_NODE, locationType.toString());
        values += "\n" + XML_Converter.wrap(RngXmlMaster.SUBLEVEL_TYPE_NODE, sublevelType.toString());
        values += "\n" + XML_Converter.wrap(PARAMS.BF_HEIGHT.name(), tileMap.getHeight() + "");
        values += "\n" + XML_Converter.wrap(PARAMS.BF_WIDTH.name(), tileMap.getWidth() + "");
        values = XML_Converter.wrap(RngXmlMaster.VALUES_NODE, values);
        xml += "\n" + values;
        //props
        //entrances
        List<Coordinates> entrances = new ListMaster<Coordinates>()
                .asList(entranceCoordinates, exitCoordinates);

        xml += "\n" + XML_Converter.wrap(RngXmlMaster.ENTRANCE_NODE,
                ContainerUtils.constructStringContainer(entrances));

        xml += getAiData();

        //        List<Coordinates> exits =
        //         tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().getVar(c) == ROOM_CELL.ROOM_EXIT).collect(Collectors.toList());
        //        xml +="\n"+ XML_Converter.wrap(LocationBuilder.EXIT_NODE, ContainerUtils.constructStringContainer(exits));

        String z = "";

//        for (LevelZone levelZone : getSubParts()) {
//            z += "\n" + levelZone.toXml();
//        }
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.ZONES_NODE, z);
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.LEVEL_DATA_NODE, getLevelData().toString());
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.DIRECTION_MAP_NODE, directionMapData == null ?
                "" //TODO
                : directionMapData);

        String s = "";
        for (LevelBlock block : getBlocks()) {
            for (Coordinates c : block.getBoundCells().keySet()) {
                if (block.getBoundCells().get(c) == null)
                    continue;
                s += c + "=" + block.getBoundCells().get(c).toString() + ";";
            }

        }
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.BOUND_NODE, s);

        xml = XML_Converter.wrap("Level", xml);
        return xml;
    }

    public String getAiData() {
        String aiData = "";
        for (LevelBlock block : getBlocks()) {
            for (List<ObjAtCoordinate> list : block.getUnitGroups().keySet()) {
                aiData += block.getUnitGroups().get(list) + RngXmlMaster.AI_GROUP_SEPARATOR +
                        ContainerUtils.toStringContainer(list, ";") +
                        "\n";
            }
        }
        return "\n" + XML_Converter.wrap(RngXmlMaster.AI_GROUPS_NODE,
                aiData);
    }

    @Deprecated
    public List<LevelZone> getZones() {
        return null;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public LevelModel getModel() {
        return model;
    }

    public SUBLEVEL_TYPE getSublevelType() {
        return sublevelType;
    }

    public void setSublevelType(SUBLEVEL_TYPE sublevelType) {
        this.sublevelType = sublevelType;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }

    public void setLocationType(LOCATION_TYPE locationType) {
        this.locationType = locationType;
        if (locationType != null)
            surface = locationType.isSurface();
    }

    public List<ObjAtCoordinate> getObjects() {
//        if (!ListMaster.isNotEmpty(objects)) {
//            objects = new ArrayList<>();
//            for (LevelBlock block : getBlocks()) {
//                objects.addAll(block.getObjects());
//            }
//        }
        return objects;
    }
@Deprecated
    public LevelBlock getBlockForCoordinate(Coordinates coordinates) {
        return null;
    }

    public String getDirectionMapData() {
        return directionMapData;
    }

    public void setDirectionMapData(String directionMapData) {
        this.directionMapData = directionMapData;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public Map<String, DIRECTION> getDirectionMap() {
        if (!MapMaster.isNotEmpty(directionMap)) {
            directionMap = new RandomWizard<DIRECTION>()
                    .constructStringWeightMapInversed(directionMapData, DIRECTION.class);
        }
        return directionMap;
    }

    public void setDirectionMap(Map<String, DIRECTION> directionMap) {
        this.directionMap = directionMap;
    }

    public Map<String, FLIP> getFlipMap() {
        return flipMap;
    }

    public void setFlipMap(Map<String, FLIP> flipMap) {
        this.flipMap = flipMap;
    }

    public ObjType getDungeonType() {
        return dungeonType;
    }

    public void setDungeonType(ObjType dungeonType) {
        this.dungeonType = dungeonType;
    }

    public LevelData getLevelData() {
        return model.getData();
    }

    public void setData(LevelData data) {
        throw new RuntimeException();
    }

    public List<LevelBlock> getBlocks() {
        List<LevelBlock> list = new ArrayList<>();
//        for (LevelZone zone : getSubParts()) {
//            list.addAll(zone.getSubParts());
//        }
        return list;
    }

    public String getExitType() {
        return exitType;
    }

    public void setExitType(String exitType) {
        this.exitType = exitType;
    }

    public String getEntranceType() {
        return entranceType;
    }

    public void setEntranceType(String entranceType) {
        this.entranceType = entranceType;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public Coordinates getEntranceCoordinates() {
        if (entranceCoordinates == null) {
            int i = 0;
            if (CoreEngine.isReverseExit())
                i = 1;
            String s = (getEntranceData().split(";")[i]);
            if (s.contains("(")) {
                s = VariableManager.getVars(s);
            }
            entranceCoordinates = new Coordinates(s);
        }
        return entranceCoordinates;
    }

    public Coordinates getExitCoordinates() {
        if (exitCoordinates == null) {
            int i = 1;
            if (CoreEngine.isReverseExit())
                i = 0;
            exitCoordinates = new Coordinates(getEntranceData().split(";")[i]);
        }
        return exitCoordinates;
    }

    public LevelStats getStats() {
        return stats;
    }

    public void setStats(LevelStats stats) {
        this.stats = stats;
    }

    public float getFillRatio() {
        return new Float(model.getOccupiedCells().size())
                / model.getCurrentWidth() / model.getCurrentHeight();
    }

    public boolean isBoundObjectsSupported() {
        if (locationType.getGroup() == LOCATION_TYPE_GROUP.NATURAL)
            return false;
        return RngFillMaster.BOUND_SUPPORTED;
    }

    public DUNGEON_STYLE getMainStyle() {
        if (mainStyle != null)
            return mainStyle;
        TreeMap<DUNGEON_STYLE, Integer> map = new TreeMap<>();
        for (LevelBlock block : getBlocks()) {
            MapMaster.addToIntegerMap(map, block.getZone().getStyle(), 1);
        }
        if (map.isEmpty()) {
            return DUNGEON_STYLE.Somber;
        }
        return mainStyle = map.firstKey();
        //        return new ArrayList<>(map.values()).getVar(0);
        //        return map.values().iterator().next();
    }


    public void setMainStyle(DUNGEON_STYLE mainStyle) {
        this.mainStyle = mainStyle;
    }

    public boolean isSurface() {
        return surface;
    }

    Map<LevelBlock, DungeonEnums.CELL_IMAGE> cellTypeMap = new HashMap();

    public int getCellVariant(int i, int j) {
        return 0;
    }

    public DungeonEnums.CELL_IMAGE getCellType(int i, int j) {

        Coordinates c = new Coordinates(i, j);
        DungeonEnums.CELL_IMAGE img = cellTypeSpecialMap.get(c);
        if (img != null) {
            return img;
        }

        LevelBlock block = getBlockForCoordinate(c);
        if (block != null) {
            img = block.getCellType();
        }

        if (img == null) {
            DUNGEON_STYLE style = block == null ? getMainStyle() : block.getZone().getStyle();
            img = getCellImageType(style);
//           TODO  if (!TextureCache.isImage(StrPathBuilder.build(PathFinder.getCellImagesPath(), img + ".png"))) {
//                style = getMainStyle();
//                img = getCellImageType(style);
//            }
            cellTypeMap.put(block, img);
        }
        return img;
    }

    public String getCellImgPath(int i, int j) {
        DungeonEnums.CELL_IMAGE img = getCellType(i, j);
//        CELL_IMAGE_SUFFIX suffix =
        return StrPathBuilder.build(PathFinder.getCellImagesPath(), img + ".png");
    }

    public Map<Coordinates, DungeonEnums.CELL_IMAGE> getCellTypeSpecialMap() {
        return cellTypeSpecialMap;
    }

    private DungeonEnums.CELL_IMAGE getCellImageType(DUNGEON_STYLE style) {
        switch (style) {
            case Somber:
                if (RandomWizard.chance(56))
                    return DungeonEnums.CELL_IMAGE.star;
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.octagonal;
            case DWARF:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.diamond;
                return DungeonEnums.CELL_IMAGE.octagonal;
            case SPIDER:
            case Stony:
            case Pagan:
                return DungeonEnums.CELL_IMAGE.natural;
            case ROGUE:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.tiles;
                return DungeonEnums.CELL_IMAGE.cross;

            case Knightly:
            case Holy:
                if (RandomWizard.chance(66)) {
                    return DungeonEnums.CELL_IMAGE.cross;
                }
                return DungeonEnums.CELL_IMAGE.diamond;
            case DarkElegance:
            case PureEvil:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.octagonal;
            case Brimstone:
//                if (RandomWizard.chance(66))
//                    return CELL_IMAGE.circle;
            case Grimy:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.tiles;
            case Cold:
            case Arcane:
                if (RandomWizard.chance(66))
                    return DungeonEnums.CELL_IMAGE.star;
        }
        return DungeonEnums.CELL_IMAGE.tiles;
    }

    public String getEntranceData() {
        return entranceData;
    }

    public boolean isPregen() {
        return pregen;
    }


    public void addUnitGroup(LevelBlock levelBlock,
                             List<ObjAtCoordinate> unitsAtCoordinates, RngMainSpawner.UNIT_GROUP_TYPE groupType) {

        levelBlock.getUnitGroups().put(unitsAtCoordinates, groupType);
    }


    public String getLevelName() {
        return name;
    }

    public Map<Coordinates, FACING_DIRECTION> getUnitFacingMap() {
        return unitFacingMap;
    }

    public void setUnitFacingMap(Map<Coordinates, FACING_DIRECTION> unitFacingMap) {
        this.unitFacingMap = unitFacingMap;
    }

    public void initUnitFacingMap(Map<String, String> customDataMap) {
        Map<Coordinates, FACING_DIRECTION> map = new HashMap<>();
        for (String s : customDataMap.keySet()) {
            map.put(new Coordinates(s), FacingMaster.getFacing(customDataMap.get(s)));
        }
        setUnitFacingMap(map);
    }

    public void initCellTypeMap(Map<String, String> customDataMap) {
        Map<Coordinates, DungeonEnums.CELL_IMAGE> map = new HashMap<>();
        s:
        for (String s : customDataMap.keySet()) {
            for (String substring : ContainerUtils.openContainer(customDataMap.get(s))) {
                DungeonEnums.CELL_IMAGE type = new EnumMaster<DungeonEnums.CELL_IMAGE>().retrieveEnumConst(DungeonEnums.CELL_IMAGE.class, substring);
                if (type != null) {
                    map.put(new Coordinates(s), type);
                    continue s;
                }

            }
        }
        cellTypeSpecialMap = (map);
    }

    public LevelZone getZoneById(int index) {

        for (LevelZone zone : getZones()) {
            if (zone.getIndex() == index) {
                return zone;
            }
        }
        return null;
    }

@Deprecated
    public Module[] getSubParts() {
        return new Module[0];
    }
}
