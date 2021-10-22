package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.exploration.dungeons.generator.init.RngXmlMaster;
import main.system.auxiliary.Refactor;
import main.system.auxiliary.StringMaster;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class LocationBuilder extends DungeonBuilder {

    public static final String BLOCKS_NODE = StringMaster.format(RngXmlMaster.BLOCKS_NODE);
    public static final String COORDINATES_NODE = StringMaster.format(RngXmlMaster.COORDINATES_NODE);
    public static final String OBJ_NODE = StringMaster.format(RngXmlMaster.OBJECTS_NODE);
    public static final String ZONES_NODE = StringMaster.format(RngXmlMaster.ZONES_NODE);
    public static final String META_DATA_NODE = "Named_Coordinate_Points";

    private Location location;

    private final List<Node> lazyInitZones = new ArrayList<>();

    public LocationBuilder() {
        super(null);
    }

    public LocationBuilder(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location buildDungeon(String path) {
        location = super.buildDungeon(path);
        return location;
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
    public Location getFloorWrapper() {
        return location;
    }

    public enum ROOM_TYPE {
        THRONE_ROOM,
        COMMON_ROOM,
        CORRIDOR,
        TREASURE_ROOM,
        DEATH_ROOM,
        GUARD_ROOM,
        ENTRANCE_ROOM,
        EXIT_ROOM,
        SECRET_ROOM,
        OUTSIDE,
        PLATFORM,
;
        public static ROOM_TYPE[] mainRoomTypes = {
                THRONE_ROOM,
                COMMON_ROOM,
                TREASURE_ROOM,
                DEATH_ROOM,
                GUARD_ROOM,
                SECRET_ROOM
        };

    }

}
