package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/20/2018.
 */
public class DungeonLevel extends LevelLayer<LevelZone> {
    TileMap tileMap;
    LevelModel model;
    SUBLEVEL_TYPE sublevelType;
    LOCATION_TYPE locationType;
    List<ObjAtCoordinate> objects=     new ArrayList<>() ;
    List<ObjAtCoordinate> units=     new ArrayList<>() ;
    private String directionMapData;
    private int powerLevel;
    private Map<String, DIRECTION> directionMap;
    private Map<String, FLIP> flipMap;
    private ObjType dungeonType;

    public DungeonLevel(TileMap tileMap, LevelModel model, SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        this.tileMap = tileMap;
        this.model = model;
        this.sublevelType = type;
        this.locationType = locationType;
    }

    @Override
    public String toXml() {
        //TODO save original model map!
        String xml="";
        tileMap= new TileMapper(model,model.getData()).joinTileMaps();
         xml +=XML_Converter.wrap(RngXmlMaster.TILEMAP_NODE,  tileMap.toString());


        String values="";
        values +="\n"+ XML_Converter.wrap(RngXmlMaster.LOCATION_TYPE_NODE, locationType.toString());
        values +="\n"+ XML_Converter.wrap(RngXmlMaster.SUBLEVEL_TYPE_NODE, sublevelType.toString());
        values +="\n"+ XML_Converter.wrap(PARAMS.BF_HEIGHT.name(), tileMap.getHeight()+"");
        values +="\n"+ XML_Converter.wrap(PARAMS.BF_WIDTH.name(), tileMap.getWidth()+"");
        values = XML_Converter.wrap( RngXmlMaster.VALUES_NODE, values);
        xml+="\n"+ values;
        //props
//entrances
        List<Coordinates> entrances =
         tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ENTRANCE).collect(Collectors.toList());
        tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ROOM_EXIT).collect(Collectors.toList());
        xml +="\n"+ XML_Converter.wrap(LocationBuilder.ENTRANCE_NODE, ContainerUtils.constructStringContainer(entrances));

        List<Coordinates> exits =
         tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ROOM_EXIT).collect(Collectors.toList());
        xml +="\n"+ XML_Converter.wrap(LocationBuilder.EXIT_NODE, ContainerUtils.constructStringContainer(exits));

        String z="";

        for (LevelZone levelZone : getSubParts()) {
            z +="\n"+ levelZone.toXml();
        }
        xml +="\n"+ XML_Converter.wrap(LocationBuilder.ZONES_NODE, z);
        xml +="\n"+ XML_Converter.wrap(RngXmlMaster.LEVEL_DATA_NODE, getData().toString());
        xml +="\n"+ XML_Converter.wrap(RngXmlMaster.DIRECTION_MAP_NODE, directionMapData==null ?
        "" //TODO
         : directionMapData);

        xml = XML_Converter.wrap("Level", xml);
        return xml;
    }

    @Override
    public List<LevelZone> getSubParts() {
        return model.getZones();
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public LevelModel getModel() {
        return model;
    }

    public SUBLEVEL_TYPE getSublevelType() {
        return sublevelType;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
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
        if (!ListMaster.isNotEmpty(units)) {
            units = new ArrayList<>();
            for (LevelBlock block : getBlocks()) {
                units.addAll(block.getUnits());
            }
        }
        return units;
    }

    public LevelBlock getBlockForCoordinate(Coordinates coordinates) {
        for (LevelZone zone : getSubParts()) {
            for (LevelBlock block : zone.getSubParts()) {
                if (block.getCoordinatesList().contains(coordinates))
                    return block;
            }
        }
        if (model!=null )
        {
            coordinates = coordinates.getOffset(new AbstractCoordinates(model.getLeftMost(), model.getTopMost()));
            for (LevelZone zone : getSubParts()) {
                for (LevelBlock block : zone.getSubParts()) {
                    if (block.getCoordinatesList().contains(coordinates))
                        return block;
                }
            }
        }
        return null;
    }

    public void setDirectionMapData(String directionMapData) {
        this.directionMapData = directionMapData;
    }

    public String getDirectionMapData() {
        return directionMapData;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public void setDirectionMap(Map<String, DIRECTION> directionMap) {
        this.directionMap = directionMap;
    }

    public Map<String, DIRECTION> getDirectionMap() {
        return directionMap;
    }

    public void setFlipMap(Map<String, FLIP> flipMap) {
        this.flipMap = flipMap;
    }

    public Map<String, FLIP> getFlipMap() {
        return flipMap;
    }

    public ObjType getDungeonType() {
        return dungeonType;
    }

    public void setDungeonType(ObjType dungeonType) {
        this.dungeonType = dungeonType;
    }

    public void setLocationType(LOCATION_TYPE locationType) {
        this.locationType = locationType;
    }

    public void setSublevelType(SUBLEVEL_TYPE sublevelType) {
        this.sublevelType = sublevelType;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
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
}
