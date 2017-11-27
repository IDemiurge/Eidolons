package main.game.battlecraft.logic.dungeon.location.building;

import main.content.CONTENT_CONSTS.FLIP;
import main.data.xml.XML_Converter;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.location.Location;
import main.game.battlecraft.logic.dungeon.location.LocationBuilder;
import main.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import main.game.battlecraft.logic.dungeon.location.building.BuildHelper.BUILD_PARAMS;
import main.game.battlecraft.logic.dungeon.location.building.BuildHelper.BuildParameters;
import main.game.battlecraft.logic.dungeon.test.TestDungeonBuilder;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.module.dungeoncrawl.dungeon.DungeonLevelMaster;
import main.game.module.dungeoncrawl.dungeon.DungeonLevelMaster.ENTRANCE_LAYOUT;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class DungeonPlan {
    DUNGEON_TEMPLATES template;
    // Map<MAP_ZONE, DUNGEON_TEMPLATES> subtemplates
    List<MapBlock> blocks;
    Map<ObjType, Coordinates> objMap;
    List<? extends MicroObj> wallObjects;
    private Location location;
    private Dungeon dungeon;
    private List<MapZone> zones;
    private Integer wallWidth = 1;
    private Coordinates base;
    private Coordinates end;
    private boolean rotated;
    private boolean flippedY;
    private boolean flippedX;
    private int widthMod;
    private int heightMod;
    private int sizeMod;
    private ENTRANCE_LAYOUT exitLayout;
    private ENTRANCE_LAYOUT entranceLayout;
    private String stringData;
    private BuildParameters params;
    private Map<String, DIRECTION> directionMap;
    private boolean loaded;
    private LinkedHashMap<String, FLIP> flipMap;

    public DungeonPlan(DUNGEON_TEMPLATES template, Location location) {
        // or maybe it should be 1 main... can use generic-templates for Zones
        // specifically? E.g. WEST==PRISON_CELLS
        this.template = template;
        this.location = location;
        this.dungeon = location.getDungeon();
        params = location.getBuildParams();
        if (params != null) {
            if (params.getIntValue(BUILD_PARAMS.WIDTH_MOD) > 0) {
                setWidthMod(params.getIntValue(BUILD_PARAMS.WIDTH_MOD));
            } else {
                widthMod = location.getCellsX() * 100 / TestDungeonBuilder.BASE_WIDTH;
            }
            if (params.getIntValue(BUILD_PARAMS.HEIGHT_MOD) > 0) {
                setHeightMod(params.getIntValue(BUILD_PARAMS.HEIGHT_MOD));
            } else {
                heightMod = location.getCellsY() * 100 / TestDungeonBuilder.BASE_HEIGHT;
            }
            if (params.getIntValue(BUILD_PARAMS.SIZE_MOD) > 0) {
                setSizeMod(params.getIntValue(BUILD_PARAMS.SIZE_MOD));
            } else {
                sizeMod = widthMod / 2 + heightMod / 2;
            }
        } else {
            widthMod = location.getCellsX() * 100 / TestDungeonBuilder.BASE_WIDTH;
            heightMod = location.getCellsY() * 100 / TestDungeonBuilder.BASE_HEIGHT;
            sizeMod = widthMod / 2 + heightMod / 2;
        }
    }

    public String toString() {
        return dungeon.getName() + " Dungeon Plan with [" + zones.size() + "] Zones: "
                + zones.toString() + " and [" + blocks.size() + "] Blocks: " + blocks.toString();
    }

    public String getStringData() {
        if (stringData == null) {
            stringData = getXml();
        }
        return stringData;
    }

    public void setStringData(String data) {
        stringData = data;
    }

    public DUNGEON_TEMPLATES getTemplate() {
        return template;
    }

    public void setTemplate(DUNGEON_TEMPLATES template) {
        this.template = template;
    }

    public void addBlock(MapBlock mapBlock) {
        if (!getBlocks().contains(mapBlock)) {
            getBlocks().add(mapBlock);
        }
    }

    public List<MapBlock> getBlocks() {
        if (blocks == null) {
            blocks = new ArrayList<>();
        }
        // else
        // Collections.sort(blocks, new Comparator<MapBlock>() {
        // public int compare(MapBlock o1, MapBlock o2) {
        // if (o1.getSpawningPriority() == o2.getSpawningPriority())
        // return 0;
        // if (o1.getSpawningPriority() > o2.getSpawningPriority())
        // return 1;
        // return -1;
        // }
        // });
        return blocks;
    }

    public void setBlocks(List<MapBlock> blocks) {
        this.blocks = blocks;
    }

    public MapBlock getBlockByCoordinate(Coordinates c) {
        for (MapZone z : getZones()) {
            if (!CoordinatesMaster.isWithinBounds(c, z.x1, z.x2, z.y1, z.y2)) {
                continue;
            }
            return z.getBlock(c);
        }
        for (MapBlock b : getBlocks()) {
            if (b.getCoordinates().contains(c)) {
                return b;
            }
        }
        return null;
    }

    public Map<ObjType, Coordinates> getObjMap() {
        if (objMap == null) {
            objMap = new HashMap<>();
        }
        return objMap;
    }

    public void setObjMap(Map<ObjType, Coordinates> objMap) {
        this.objMap = objMap;
    }

    public Integer getCellsX() {
        return dungeon.getCellsX();
    }

    public Integer getCellsY() {
        return dungeon.getCellsY();
    }

    public int getZ() {
        return dungeon.getZ();
    }

    public List<MapZone> getZones() {
        if (zones == null) {
            zones = new ArrayList<>();
        }
        return zones;
    }

    public void setZones(List<MapZone> zones) {
        this.zones = zones;
    }

    public int getBorderY() {
        return getCellsY() - getWallWidth();
    }

    public int getBorderX() {
        return getCellsX() - getWallWidth();
    }

    public Integer getWallWidth() {
        return wallWidth;
    }

    public void setWallWidth(Integer wallWidth) {
        this.wallWidth = wallWidth;
    }

    public boolean isCoordinateMappedToBlock(Coordinates c1) {
        for (MapBlock b : getBlocks()) {
            if (b.getCoordinates().contains(c1)) {
                return true;
            }
        }
        return false;
    }

    public void setBaseAnchor(Coordinates coordinates) {
        base = coordinates;

    }

    public void setEndAnchor(Coordinates coordinates) {
        end = coordinates;

    }

    public Coordinates getBase() {
        return base;
    }

    public void setBase(Coordinates base) {
        this.base = base;
    }

    public Coordinates getEnd() {
        return end;
    }

    public void setEnd(Coordinates end) {
        this.end = end;
    }

    public boolean isRotated() {
        return rotated;
    }

    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }

    public boolean isFlippedY() {
        return flippedY;
    }

    public void setFlippedY(boolean flippedY) {
        this.flippedY = flippedY;
    }

    public boolean isFlippedX() {
        return flippedX;
    }

    public void setFlippedX(boolean flippedX) {
        this.flippedX = flippedX;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public int getSizeMod() {
        return sizeMod;
    }

    public void setSizeMod(int intValue) {
        sizeMod = intValue;
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

    public DungeonPlan getCopy() {
        DungeonPlan plan = new DungeonPlan(template, location);
        int i = 0;
        for (MapZone z : getZones()) {
            MapZone zone = new MapZone(dungeon, i, z.getX1(), z.getX2(), z.getY1(), z.getY2());
            plan.getZones().add(zone);
            i++;
            for (MapBlock b : z.getBlocks()) {
                MapBlock block = new MapBlock(b.getId(), b.getType(), zone, plan, b
                        .getCoordinates());
                zone.addBlock(block);
                block.setRoomType(b.getRoomType());
            }
        }
        return plan;
    }

    public String getXml() {
        String xml = XML_Converter.openXmlFormatted("Plan");

        xml += XML_Converter.wrapLeaf(DungeonBuilder.DUNGEON_TYPE_NODE, dungeon.getOriginalName());

        // ENTRANCE_LAYOUT enterLayout =
        // getDungeon().getPlan().getEntranceLayout();
        // if (enterLayout != null)
        // xml += XML_Converter.wrapLeaf(DungeonBuilder.ENTRANCE_NODE,
        // dungeon.getOriginalName());
        // // TODO CATEGORY PREFIX!
        // ENTRANCE_LAYOUT exitLayout =
        // getDungeon().getPlan().getExitLayout();
        // if (exitLayout != null)

        xml += XML_Converter.openXmlFormatted("Zones");
        for (MapZone z : getZones()) {
            xml += z.getXml();
        }
        xml += XML_Converter.closeXmlFormatted("Zones");

        if (location.getMainEntrance() != null) {
            if (entranceLayout == null) {
                entranceLayout = DungeonLevelMaster.getLayout(this, location.getMainEntrance()
                        .getCoordinates());
            }
            setEntranceLayout(entranceLayout);
            xml += XML_Converter.openXmlFormatted(LocationBuilder.ENTRANCE_NODE);
            xml += StringMaster.getWellFormattedString(entranceLayout.toString());
            xml += XML_Converter.closeXmlFormatted(LocationBuilder.ENTRANCE_NODE);
        }
        if (location.getMainExit() != null) {
            xml += XML_Converter.openXmlFormatted(LocationBuilder.EXIT_NODE);
            if (exitLayout == null) {
                exitLayout = DungeonLevelMaster.getLayout(this, location.getMainExit()
                        .getCoordinates());
            }
            setExitLayout(exitLayout);
            xml += StringMaster.getWellFormattedString(exitLayout.toString());
            xml += XML_Converter.closeXmlFormatted(LocationBuilder.EXIT_NODE);
        }
        xml += XML_Converter.closeXmlFormatted("Plan");
        return xml;
    }

    public ENTRANCE_LAYOUT getExitLayout() {
        return exitLayout;
    }

    public void setExitLayout(ENTRANCE_LAYOUT layout) {
        exitLayout = layout;

    }

    public ENTRANCE_LAYOUT getEntranceLayout() {
        return entranceLayout;
    }

    public void setEntranceLayout(ENTRANCE_LAYOUT layout) {
        entranceLayout = layout;
    }



    public int getDimension(boolean xOrY) {
        return xOrY ? getCellsX() : getCellsY();
    }

    public List<? extends MicroObj> getWallObjects() {
        if (wallObjects == null) {
            wallObjects = new ArrayList<>();
        }
        return wallObjects;
    }

    public void setWallObjects(List<? extends MicroObj> list) {
        wallObjects = list;
    }

    public LinkedHashMap<String, FLIP> getFlipMap() {
        return flipMap;
    }

    public void setFlipMap(LinkedHashMap<String, FLIP> constructStringWeightMapInversed) {
        flipMap = constructStringWeightMapInversed;

    }

    public Map<String, DIRECTION> getDirectionMap() {
        return directionMap;
    }

    public void setDirectionMap(Map<String, DIRECTION> constructStringWeightMapInversed) {
        directionMap = constructStringWeightMapInversed;

    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

}
