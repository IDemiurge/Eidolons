package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.fill.RngFillMaster;
import eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE_GROUP;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class DungeonLevel extends LevelLayer<LevelZone> {
    public static final String NON_VOID_CELL = "Nonvoid Cell";
    public static final String VOID_CELL = "Void Cell";
    TileMap tileMap;
    LevelModel model;
    SUBLEVEL_TYPE sublevelType;
    LOCATION_TYPE locationType;
    List<ObjAtCoordinate> objects = new ArrayList<>();
    Set<ObjAtCoordinate> units;
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
    private String entranceData;
    private boolean pregen;
    private Set<Coordinates> nonVoidCoordinates;
    private Set<Coordinates> voidCoordinates;
    private Map<List<ObjAtCoordinate>, RngMainSpawner.UNIT_GROUP_TYPE> unitGroups = new XLinkedMap<>();
    private Collection<ObjAtCoordinate> unassignedUnits = new ArrayList<>();

    String name;
    private Map<Coordinates, FACING_DIRECTION> unitFacingMap;

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

    public String getObjDataXml() {
        collectUnits();
//        for (LevelBlock block : getBlocks()) { already done?
//            units.addAll(block.getUnits());
//        }
        String xml = "";
        for (ObjAtCoordinate obj : units) {
            xml += obj.getCoordinates() + "=" + obj.getType().getName() + ";";
        }
        for (ObjAtCoordinate obj : objects) {
            xml += obj.getCoordinates() + "=" + obj.getType().getName() + ";";
        }
//objects.stream().map(d-> d.getCoordinates() + "=" +d.getType().getName()).
        return XML_Converter.wrap(RngXmlMaster.OBJECTS_NODE, xml);
    }

    @Override
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
        //         tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ROOM_EXIT).collect(Collectors.toList());
        //        xml +="\n"+ XML_Converter.wrap(LocationBuilder.EXIT_NODE, ContainerUtils.constructStringContainer(exits));

        String z = "";

        for (LevelZone levelZone : getSubParts()) {
            z += "\n" + levelZone.toXml();
        }
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.ZONES_NODE, z);
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.LEVEL_DATA_NODE, getData().toString());
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

    @Override
    public List<LevelZone> getSubParts() {
        return model.getZones();
    }

    public List<LevelZone> getZones() {
        return getSubParts();
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
        if (!ListMaster.isNotEmpty(objects)) {
            objects = new ArrayList<>();
            for (LevelBlock block : getBlocks()) {
                objects.addAll(block.getObjects());
            }
        }
        return objects;
    }

    public Set<ObjAtCoordinate> collectUnits() {
        units = new LinkedHashSet<>();
        for (LevelBlock block : getBlocks()) {
            units.addAll(block.getUnits());
        }
        units.addAll(unassignedUnits);
        return units;
    }

    public LevelBlock getBlockForCoordinate(Coordinates coordinates) {
        if (cache.containsKey(coordinates))
            return cache.get(coordinates);
        for (LevelZone zone : getSubParts()) {
            for (LevelBlock block : zone.getSubParts()) {
                if (block.getCoordinatesList().contains(coordinates)) {
                    cache.put(coordinates, block);
                    return block;
                }
            }
        }
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

    public LevelData getData() {
        return model.getData();
    }

    public void setData(LevelData data) {
        throw new RuntimeException();
    }

    public List<LevelBlock> getBlocks() {
        List<LevelBlock> list = new ArrayList<>();
        for (LevelZone zone : getSubParts()) {
            list.addAll(zone.getSubParts());
        }
        return list;
    }

    public boolean isVoid(int i, int j) {
        if (CoreEngine.isIggDemo()) {
            if (isVoidExplicit(i, j)) {
                return true;
            }
            return false;
        }
        Coordinates c = Coordinates.get(i, j);
        if (tileMap.getMap().get(c) != ROOM_CELL.VOID)
            return false;
        if (nonVoidCoordinates != null) {
            if (nonVoidCoordinates.contains(c))
                return false;
        }
        if (isVoidExplicit(i, j)) {
            return true;
        }
        return getBlockForCoordinate(c) == null;
    }

    public boolean isVoidExplicit(int i, int j) {
        if (voidCoordinates != null) {
            if (voidCoordinates.contains(Coordinates.get(i, j)))
                return true;
        }
        return false;
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
        return mainStyle = map.firstKey();
        //        return new ArrayList<>(map.values()).get(0);
        //        return map.values().iterator().next();
    }


    public void setMainStyle(DUNGEON_STYLE mainStyle) {
        this.mainStyle = mainStyle;
    }

    public boolean isSurface() {
        return surface;
    }

    Map<LevelBlock, CELL_IMAGE> cellTypeMap = new HashMap();

    public String getCellImgPath(int i, int j) {
        LevelBlock block = getBlockForCoordinate(new Coordinates(i, j));
        CELL_IMAGE img = cellTypeMap.get(block);
        if (img == null) {
            DUNGEON_STYLE style = block == null ? getMainStyle() : block.getZone().getStyle();
            img = getCellImageType(style);
            if (!TextureCache.isImage(StrPathBuilder.build(PathFinder.getCellImagesPath(), img + ".png"))) {
                style = getMainStyle();
                img = getCellImageType(style);
            }
            cellTypeMap.put(block, img);
            return StrPathBuilder.build(PathFinder.getCellImagesPath(), img + ".png");
        }
//        CELL_IMAGE_SUFFIX suffix =
        return StrPathBuilder.build(PathFinder.getCellImagesPath(), img + ".png");
    }

    private CELL_IMAGE getCellImageType(DUNGEON_STYLE style) {
        switch (style) {
            case DWARF:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.diamond;
                return CELL_IMAGE.octagonal;
            case SPIDER:
                return CELL_IMAGE.natural;
            case ROGUE:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.tiles;
                return CELL_IMAGE.cross;

            case Knightly:
            case Holy:
                if (RandomWizard.chance(66)) {
                    return CELL_IMAGE.cross;
                }
                return CELL_IMAGE.diamond;
            case Stony:
            case Pagan:
                return CELL_IMAGE.natural;
            case DarkElegance:
            case Somber:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.diamond;
            case PureEvil:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.octagonal;
            case Brimstone:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.circle;
            case Grimy:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.tiles;
            case Cold:
            case Arcane:
                if (RandomWizard.chance(66))
                    return CELL_IMAGE.star;
        }
        return CELL_IMAGE.tiles;
    }

    public String getEntranceData() {
        return entranceData;
    }

    public void setEntranceData(String entranceData) {
        this.entranceData = entranceData;
    }

    public boolean isPregen() {
        return pregen;
    }

    public void setPregen(boolean pregen) {
        this.pregen = pregen;
    }

    public void setNonVoidCoordinates(Set<Coordinates> nonVoidCoordinates) {
        this.nonVoidCoordinates = nonVoidCoordinates;
    }

    public Set<Coordinates> getNonVoidCoordinates() {
        if (nonVoidCoordinates == null) {
            nonVoidCoordinates = new LinkedHashSet<>();
        }
        return nonVoidCoordinates;
    }

    public Set<Coordinates> getVoidCoordinates() {
        if (voidCoordinates == null) {
            voidCoordinates = new LinkedHashSet<>();
        }
        return voidCoordinates;
    }

    public void addUnitGroup(LevelBlock levelBlock,
                             List<ObjAtCoordinate> unitsAtCoordinates, RngMainSpawner.UNIT_GROUP_TYPE groupType) {

        unitGroups.put(unitsAtCoordinates, groupType);
        levelBlock.getUnitGroups().put(unitsAtCoordinates, groupType);
    }

    public void addObj(ObjAtCoordinate obj) {
        if (obj.getType().getName().equalsIgnoreCase(VOID_CELL)) {
            getVoidCoordinates().add(obj.getCoordinates());
            return;
        }
        if (obj.getType().getName().equalsIgnoreCase(NON_VOID_CELL)) {
            getNonVoidCoordinates().add(obj.getCoordinates());
            return;
        }
        if (obj.getType().getOBJ_TYPE_ENUM() == DC_TYPE.UNITS) {
            addUnit(obj);
        } else {
            addStructure(obj);
        }
    }

    private void addStructure(ObjAtCoordinate obj) {
        getObjects().add(obj);
        LevelBlock b = getBlockForCoordinate(obj.getCoordinates());
        if (b != null)
            b.getObjects().add(obj);
    }

    private void addUnit(ObjAtCoordinate obj) {
        LevelBlock b = getBlockForCoordinate(obj.getCoordinates());
        if (b != null)
            b.getUnits().add(obj);
        else {
            getUnassignedUnits().add(obj);
            LogMaster.log(1, "Added into Void  " + obj);
        }
    }

    public Collection<ObjAtCoordinate> getUnassignedUnits() {
        if (unassignedUnits == null) {
            this.unassignedUnits = new LinkedHashSet<>();
        }
        return unassignedUnits;
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


    public enum CELL_IMAGE {
        tiles,
        diamond,
        circle("cr"),
        star,
        cross,
        natural,
        octagonal("oct"),
        ;
        String name;

        CELL_IMAGE() {
            name = name();
        }

        CELL_IMAGE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum CELL_IMAGE_SUFFIX {
        dark,
        lite,
        hl,
        rough,

    }

}
