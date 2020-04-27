package eidolons.game.module.dungeoncrawl.objects;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.igg.event.TIP;
import eidolons.game.netherflame.igg.event.TipMessageMaster;
import eidolons.game.netherflame.igg.event.TipMessageSource;
import eidolons.system.audio.MusicMaster;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

import java.util.HashMap;
import java.util.Map;

public class TrapMaster extends DungeonHandler {

    private Map<Coordinates, Trap> trapMap = new HashMap<>();

    public TrapMaster(DungeonMaster master) {
        super(master);
    }


    public Trap createTrap(Floor floor, String name, Coordinates coordinates) {
        return new Trap() {
            @Override
            public void trigger(Unit victim) {
                if (name.contains(victim.getName())) {
                    return;
                }
                if (!victim.isPlayerCharacter())
                    return;

                TIP tip = new EnumMaster<TIP>().
                        retrieveEnumConst(TIP.class, name);
                TipMessageSource data = new TipMessageSource(tip.message, tip.img, "Onward!", false,
                        () -> {
                            tip.run();
                            victim.kill(victim, true, null);
                            try {
                                victim.getGame().getLoop().actionInput(null);
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                            }
//                            GuiEventManager.trigger(GuiEventType.GAME_RESET);
                            DC_Game.game.getMetaMaster().getDefeatHandler().isEnded(true, true);
                        });

                MusicMaster.playMoment(MusicMaster.MUSIC_MOMENT.FALL);
                TipMessageMaster.tip(data);
            }
        };
        //how does death work with items?
        /**
         * Tutorial deaths should be special?
         * Option to getVar back to lvl+1 heroes
         */
    }

    public void initTraps(Floor floor) {
//        for (String s : dungeon.getCustomDataMap().keySet()) {
//            if (dungeon.getCustomDataMap().get(s).toLowerCase().contains("trap")) {
//                Coordinates c = new Coordinates(s);
//                trapMap.put(c, createTrap(dungeon, dungeon.getCustomDataMap().get(s),
//                        c));
//            }
//        }
    }

    public void unitMoved(Unit unit) {
        Trap traps = trapMap.get(unit.getCoordinates());
        if (traps != null) {
//            for (Trap trap : traps) {
            if (traps.isRemoveOnTrigger()) {
                trapMap.remove(unit.getCoordinates());
            }
            traps.trigger(unit);
//            }
        }
    }

    // trajectory? some traps would trigger even if 'jumped over'
//        List<DC_HeroObj> objects = unit.getGame().getPassableObjectsForCoordinate(
//                unit.getCoordinates());
    // before finishing move()?

    // traps.add(obj);
//        return result;
//    }

//    public static void initTraps(Dungeon dungeon) {
    // in corridors, on doors, on chests, on entrances
    // this method can be run last... or I can init traps one at a time for
    // each of those
//        int trapDangerPool = dungeon.getIntParam(PARAMS.BATTLE_SPIRIT);
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
//    }

//    public static Set<Trap> getTraps(Unit unit) {
//        return trapMap.getVar(new ZCoordinates(unit.getCoordinates().x, unit.getCoordinates().y, unit
//                .getZ()));
//    }
//
//    public static List<Trap> getTrapsToDisarm(Unit unit) {
//        List<Trap> list = new ArrayList<>();
//        Set<Trap> set = trapMap.getVar(new ZCoordinates(unit.getCoordinates().x,
//                unit.getCoordinates().y, unit.getZ()));
//        if (set == null) {
//            return list;
//        }
//        for (Trap trap : set) {
//            // if (t) //visible, disarmable
//            list.add(trap);
//        }
//
//        return list;
//    }

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
