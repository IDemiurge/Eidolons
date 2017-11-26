package main.game.battlecraft.logic.dungeon.universal;

import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMapGenerator.MAP_ZONES;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.core.game.DC_Game.GAME_MODES;
import main.system.auxiliary.StringMaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/8/2017.
 */
public class FacingAdjuster<E extends DungeonWrapper> extends DungeonHandler<E> {
    protected Map<Coordinates, FACING_DIRECTION> facingMap = new HashMap<>();

    public FacingAdjuster(DungeonMaster<E> master) {
        super(master);

//        for (MicroObj unit : list) {
//            FACING_DIRECTION facing;
//            if (!game.isOffline()) {
//                // TODO not always vertical!
//                facing = FacingMaster.getFacingFromDirection(getPositioner().getClosestEdgeY(
//                 unit.getCoordinates()).getDirection().flip());
//            } else
////             TODO    if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
//            {
//                facing = FacingMaster.getPresetFacing(me);
//            }
//            ((BattleFieldObject) unit).setFacing(facing);
//        }
    }


    public void adjustFacing(Unit unit) {
        unit.setFacing(unit.isMine()? getPartyMemberFacing(unit )
        :getFacingForEnemy(unit.getCoordinates() ));
    }
        public void adjustFacing(List<Unit> unitsList) {
            unitsList.forEach(unit -> adjustFacing(unit));
    }

    public FACING_DIRECTION getFacingOptimal(Coordinates c, boolean mine) {
        Collection<Obj> units = getGame().getPlayer(!mine ).getControlledUnits();
        return FacingMaster.getOptimalFacingTowardsUnits(c, units);


    }

//    public FACING_DIRECTION getFacingInitial(Coordinates c) {
//        // TODO
//        return FacingMaster
//         .getRelativeFacing(c, getGame().getDungeon().getPlayerSpawnCoordinates());
//
//    }

    boolean isAutoOptimalFacing() {
        return true;
    }

    public FACING_DIRECTION getFacingForEnemy(Coordinates c) {
        return getFacingOptimal(c, false);
    }

    public FACING_DIRECTION getPartyMemberFacing(Unit unit) {
        if (getGame().getGameMode()== GAME_MODES.DUNGEON_CRAWL){
            return FacingMaster.getOptimalFacingTowardsEmptySpaces(unit);
        }
        Coordinates c = unit.getCoordinates();
        if (isAutoOptimalFacing())
        return getFacingOptimal(c, true);
        if (facingMap.containsKey(c)) {
            return facingMap.get(c);
        }
        MAP_ZONES zone = null;
        for (MAP_ZONES z : MAP_ZONES.values()) {
            for (String s : StringMaster.open(z.getCoordinates(), ",")) {
                if (c.toString().equals(s)) {
                    zone = z;
                    break;
                }
            }
        }
        if (zone != null) {
            switch (zone) {
                case SIDE_EAST:
                    return FACING_DIRECTION.WEST;
                case SIDE_NORTH:
                    return FACING_DIRECTION.SOUTH;
                case SIDE_SOUTH:
                    return FACING_DIRECTION.NORTH;
                case SIDE_WEST:
                    return FACING_DIRECTION.EAST;
            }
        }
        return FACING_DIRECTION.NORTH;
    }

    public void unitPlaced(Coordinates adjacentCoordinate, FACING_DIRECTION facingFromDirection) {
    }

}
