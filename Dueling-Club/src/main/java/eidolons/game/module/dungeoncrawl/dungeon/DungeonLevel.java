package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.content.PARAMS;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.fill.RngFillMaster;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE_GROUP;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class DungeonLevel extends LevelLayer<LevelZone> {
    TileMap tileMap;
    LevelModel model;
    SUBLEVEL_TYPE sublevelType;
    LOCATION_TYPE locationType;
    List<ObjAtCoordinate> objects = new ArrayList<>();
    List<ObjAtCoordinate> units = new ArrayList<>();
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

        String aiData = "";
        for (LevelBlock block : getBlocks()) {
            for (List<ObjAtCoordinate> list : block.getUnitGroups().keySet()) {
                aiData += block.getUnitGroups().get(list) + "=" +
                 ContainerUtils.toStringContainer(list, ";") +
                 "\n";
            }

        }
        xml += "\n" + XML_Converter.wrap(RngXmlMaster.AI_GROUPS_NODE,
         aiData);

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

    public List<ObjAtCoordinate> getUnits() {
        units = new ArrayList<>();
        for (LevelBlock block : getBlocks()) {
            units.addAll(block.getUnits());
        }
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
        Coordinates c = Coordinates.get(i, j);
        if (tileMap.getMap().get(c) != ROOM_CELL.VOID)
            return false;
        return getBlockForCoordinate(c) == null;
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
        return entranceCoordinates;
    }

    public Coordinates getExitCoordinates() {
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

}
