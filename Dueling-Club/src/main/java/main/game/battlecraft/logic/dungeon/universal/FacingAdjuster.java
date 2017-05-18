package main.game.battlecraft.logic.dungeon.universal;

import main.entity.obj.Obj;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMapGenerator.MAP_ZONES;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.StringMaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 5/8/2017.
 */
public class FacingAdjuster<E extends DungeonWrapper> extends DungeonHandler<E>{
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


    public FACING_DIRECTION getFacingOptimal(Coordinates c) {
        Collection<Obj> units = getGame().getPlayer(true).getControlledUnits();
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
        return getFacingOptimal(c);
    }

    public FACING_DIRECTION getPartyMemberFacing(Coordinates c) {

        if (facingMap.containsKey(c)) {
            return facingMap.get(c);
        }
        MAP_ZONES zone = null;
        for (MAP_ZONES z : MAP_ZONES.values()) {
            for (String s : StringMaster.openContainer(z.getCoordinates(), ",")) {
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
        return  FACING_DIRECTION.NORTH; 
    }

    public void unitPlaced(Coordinates adjacentCoordinate, FACING_DIRECTION facingFromDirection) {
    }
}
