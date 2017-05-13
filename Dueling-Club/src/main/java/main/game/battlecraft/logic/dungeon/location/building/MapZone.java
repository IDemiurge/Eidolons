package main.game.battlecraft.logic.dungeon.location.building;

import main.content.PROPS;
import main.data.xml.XML_Converter;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapZone {
    int x1, x2, y1, y2, i;
    String xml;
    private List<MapBlock> blocks = new ArrayList<>();
    private String fillerType
            // = "Stone Wall"
            ;
    private String name;
    private List<Coordinates> exceptions;
    private Dungeon dungeon;

    public MapZone(Dungeon dungeon, int i, int x1, int x2, int y1, int y2) {
        this.i = i;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.dungeon = dungeon;
        setName("Zone #" + (i));
    }

    public int getI() {
        return i;
    }

    public String getXml() {
        xml = XML_Converter.openXmlFormatted(getName() + ", "
                + CoordinatesMaster.getBoundsString(x1, x2, y1, y2));
        xml += XML_Converter.openXmlFormatted(LocationBuilder.BLOCKS_NODE);
        String blockData = "";
        for (MapBlock b : blocks) {
            blockData += b.getXml();
        }
        xml += blockData;
        xml += XML_Converter.closeXmlFormatted(LocationBuilder.BLOCKS_NODE);
        xml += XML_Converter.closeXmlFormatted(getName() + ", "
                + CoordinatesMaster.getBoundsString(x1, x2, y1, y2));
        return xml;

    }

    public List<Coordinates> getCoordinates() {
        return new LinkedList<>(CoordinatesMaster.getCoordinatesWithin(x1, x2 - 1, y1, y2 - 1));
    }

    @Override
    public String toString() {
        return getName() + " with " + blocks.size() + " blocks; "
                + CoordinatesMaster.getBoundsString(x1, x2, y1, y2);
    }

    public MapBlock getBlock(Coordinates coordinates) {
        for (MapBlock b : blocks) {
            if (b.getCoordinates().contains(coordinates)) {
                return b;
            }
        }
        return null; // coordinateMap.getOrCreate(coordinates)
    }

    public void addBlock(MapBlock block) {
        if (blocks.contains(block)) {
            return;
        }
        blocks.add(block);
        // for (Coordinates c : block.getCoordinates())
        // coordinateMap.put(c, block);
    }

    public List<MapBlock> getBlocks() {
        return blocks;
    }

    public int getWidth() {
        return Math.abs(getX1() - getX2());
    }

    public int getHeight() {
        return Math.abs(getY1() - getY2());
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public String getFillerType() {
        if (fillerType == null) {
            fillerType = dungeon.getProperty(PROPS.FILLER_TYPE);
            // if (!DataManager.isTypeName(fillerType, OBJ_TYPES.BF_OBJ))
            // fillerType = "Stone Wall";
        }
        return fillerType;
    }

    public void setFillerType(String fillerType) {
        this.fillerType = fillerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Coordinates> getExceptions() {
        if (exceptions == null) {
            exceptions = new LinkedList<>();
        }
        return exceptions;
    }

    public void setExceptions(List<Coordinates> exceptions) {
        this.exceptions = exceptions;
    }

}
