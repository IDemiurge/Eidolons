package main.game.logic.dungeon.special;

import main.content.PARAMS;
import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;
import main.game.battlefield.ZCoordinates;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.Entrance;
import main.game.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;
import main.game.logic.dungeon.building.MapBlock;

import java.util.*;

public class TrapMaster {

    private static Map<ZCoordinates, Set<Trap>> trapMap = new HashMap<>();

    /*
     *  dual obj - bfObj and itemObj
     *  coordinates?
     *  stealth level...
     *
     *
     *
     */
    public static boolean unitMoves(DC_HeroObj unit) {
        // trajectory? some traps would trigger even if 'jumped over'
//        List<DC_HeroObj> objects = unit.getGame().getPassableObjectsForCoordinate(
//                unit.getCoordinates());
        // before finishing move()?
        trapMap.get(unit.getCoordinates());

        // traps.add(obj);
        return false;
    }

    public static void initTraps(Dungeon dungeon) {
        // in corridors, on doors, on chests, on entrances
        // this method can be run last... or I can init traps one at a time for
        // each of those
        int trapDangerPool = dungeon.getIntParam(PARAMS.BATTLE_SPIRIT);
        for (MapBlock b : dungeon.getPlan() .getBlocks()) {

            if (b.getType() == BLOCK_TYPE.CORRIDOR) {
                // at random point
            } else {
                b.getKeyCoordinate();
                // if (b.getRoomType()==ROOM_TYPE.DEATH_ROOM ||
            }
        }
        for (Entrance e : dungeon.getEntrances()) {
            // Trap trap = new Trap(type, owner, game, ref);

        }
    }

    public static Set<Trap> getTraps(DC_HeroObj unit) {
        return trapMap.get(new ZCoordinates(unit.getCoordinates().x, unit.getCoordinates().y, unit
                .getZ()));
    }

    public static List<Trap> getTrapsToDisarm(DC_HeroObj unit) {
        List<Trap> list = new LinkedList<>();
        Set<Trap> set = trapMap.get(new ZCoordinates(unit.getCoordinates().x,
                unit.getCoordinates().y, unit.getZ()));
        if (set == null) {
            return list;
        }
        for (Trap trap : set) {
            // if (t) //visible, disarmable
            list.add(trap);
        }

        return list;
    }

    public static boolean checkTrapTriggers(DC_HeroObj unit, Trap trap) {
        // by weight, by unit type...

//        trap.getUnitVisionStatus();
//        trap.getVisibilityLevel();
//
//        if (unit.checkPassive(STANDARD_PASSIVES.IMMATERIAL))
//            if (!trap.checkProperty(PROPS.TRAP_TYPE, TRAP_TYPES.MAGICAL))
//                return false;
//
//        trap.getIntParam(PARAMS.GIRTH);
//        unit.getIntParam(PARAMS.GIRTH);
        return false;
    }

    public static boolean trapTriggers(Trap trap) {

//        trap.triggered();
//        if (!trap.isPermanent()) {
//            trap.removeCharge();
//            trapMap.remove(key);
//        }
        return false;
    }

    public static boolean tryDisarmTrap(Trap trap) {

//        result = RollMaster.roll(roll_type, ref);
        return false;
    }

    public static boolean checkTrapOnLock(Entity lockedObj) {
        // TODO Auto-generated method stub
        return false;
    }

    public enum TRAP_TYPES {

    }

}
