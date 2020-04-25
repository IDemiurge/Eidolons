package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.FauxDungeonLevel;
import eidolons.game.module.generator.init.RngXmlMaster;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.util.Refactor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class LocationBuilder extends DungeonBuilder {

    public static final String BLOCKS_NODE = StringMaster.getWellFormattedString(RngXmlMaster.BLOCKS_NODE);
    public static final String COORDINATES_NODE = StringMaster.getWellFormattedString(RngXmlMaster.COORDINATES_NODE);
    public static final String OBJ_NODE = StringMaster.getWellFormattedString(RngXmlMaster.OBJECTS_NODE);
    public static final String ZONES_NODE = StringMaster.getWellFormattedString(RngXmlMaster.ZONES_NODE);
    public static final String META_DATA_NODE = "Named_Coordinate_Points";

    private Location location;

    private List<Node> lazyInitZones = new ArrayList<>();

    public LocationBuilder() {
        super(null);
    }

    public LocationBuilder(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location buildDungeon(String path) {
        location = super.buildDungeon(path);
        FauxDungeonLevel level = createFauxDungeonLevel(path, location);
        master.setDungeonLevel(level);
        try {
            location.initMainEntrance();
        } catch (Exception e) {

            main.system.ExceptionMaster.printStackTrace(e);
        }
        return location;
    }

    private FauxDungeonLevel createFauxDungeonLevel(String path, Location location) {
        FauxDungeonLevel level = new FauxDungeonLevel(PathUtils.getLastPathSegment(path));
        return level;
    }



    @Override
    public Location buildDungeon(String path, String data, List<Node> nodeList) {
        location = (super.buildDungeon(path, data, nodeList));
        return location;
    }

    @Override
    public void initLevel(List<Node> nodeList) {
        getFloorLoader().start();

        for (Node n : nodeList) {
            getFloorLoader().processNode(n, location);
        }

        getFloorLoader().finish(location);
    }

    @Refactor
    @Override
    public Location getDungeon() {
        return location;
    }

    public enum ROOM_TYPE {
        THRONE_ROOM(60, 45, 3, 0, 4, 0),
        COMMON_ROOM(25, 25),
        CORRIDOR(15, 15, 1, 4, 3, 6),
        TREASURE_ROOM(25, 15, 3, 7, 2, 6),
        DEATH_ROOM(30, 15, 2, 4, 3, 5),
        GUARD_ROOM(25, 25, 3, 6, 2, 4),
        ENTRANCE_ROOM(15, 35),
        EXIT_ROOM(35, 15),
        SECRET_ROOM(15, 15, 1, 4, 3, 6),
        OUTSIDE(60, 45, 3, 0, 4, 0);

        public static ROOM_TYPE[] mainRoomTypes = {
                THRONE_ROOM,
                COMMON_ROOM,
                TREASURE_ROOM,
                DEATH_ROOM,
                GUARD_ROOM,
                SECRET_ROOM
        };
        public int heightMod;
        public int widthMod;
        public int minX;
        public int maxX;
        public int minY;
        public int maxY;

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

    }

}
