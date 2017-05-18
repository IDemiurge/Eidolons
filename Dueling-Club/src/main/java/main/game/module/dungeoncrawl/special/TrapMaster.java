package main.game.module.dungeoncrawl.special;

import main.content.PARAMS;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.bf.ZCoordinates;

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
    public static boolean unitMoves(Unit unit) {
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
//        for (MapBlock b : dungeon.getPlan().getBlocks()) {
//
//            if (b.getType() == BLOCK_TYPE.CORRIDOR) {
//                // at random point
//            } else {
//                b.getKeyCoordinate();
//                // if (b.getRoomType()==ROOM_TYPE.DEATH_ROOM ||
//            }
//        }
//        for (Entrance e : dungeon.getEntrances()) {
//            // Trap trap = new Trap(type, owner, game, ref);
//
//        }
    }

    public static Set<Trap> getTraps(Unit unit) {
        return trapMap.get(new ZCoordinates(unit.getCoordinates().x, unit.getCoordinates().y, unit
                .getZ()));
    }

    public static List<Trap> getTrapsToDisarm(Unit unit) {
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

    public static boolean checkTrapTriggers(Unit unit, Trap trap) {
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
