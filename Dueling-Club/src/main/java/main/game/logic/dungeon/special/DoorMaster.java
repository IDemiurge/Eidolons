package main.game.logic.dungeon.special;

import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.logic.dungeon.Dungeon;

import java.util.ArrayList;
import java.util.List;

public class DoorMaster {
    public static void initDoors(Dungeon dungeon) {
        List<ObjType> doorTypes = new ArrayList<>(); // secret doors, trap doors
        // dungeon tags
        // the things I would set in AV?
        int lockLevel;
        // for (MapBlock b : dungeon.getPlan().getBlocks()) {
        // int doorPool;
        // if (b.getType() == BLOCK_TYPE.CORRIDOR) {
        //
        // } else {
        //
        // }
        // }
    }

    public static boolean isDoor(Entity u) {
        return false;

    }

    public static boolean openDoor(DC_HeroObj door) {
//        door.addStatus(value);
        return true;

    }

    public static boolean isOpen(Entity obj) {

        return false;

    }
}
