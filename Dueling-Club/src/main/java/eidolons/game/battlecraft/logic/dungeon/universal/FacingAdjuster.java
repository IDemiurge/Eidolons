package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.game.DC_Game.GAME_MODES;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/8/2017.
 */
public class FacingAdjuster extends DungeonHandler  {
    protected Map<Coordinates, FACING_DIRECTION> facingMap = new HashMap<>();

    public FacingAdjuster(DungeonMaster  master) {
        super(master);
    }


    public void adjustFacing(Unit unit) {
        unit.setFacing(unit.isMine() ? getPartyMemberFacing(unit)
         : getFacingForUnit(unit.getCoordinates(), unit.getName()));
    }

    public void adjustFacing(List<Unit> unitsList) {
        unitsList.forEach(unit -> adjustFacing(unit));
    }

    public FACING_DIRECTION getFacingOptimal(Coordinates c, boolean mine) {
        Collection<Obj> units = getGame().getPlayer(!mine).collectControlledUnits();
        return FacingMaster.getOptimalFacingTowardsUnits(c, units);


    }

    boolean isAutoOptimalFacing() {
        return true;
    }

    public FACING_DIRECTION getFacingForUnit(Coordinates c, String typeName) {
        return getFacingOptimal(c, false);
    }

    public FACING_DIRECTION getPartyMemberFacing(Unit unit) {
        if (unit.isPlayerCharacter()) {
            FACING_DIRECTION presetFacing = EidolonsGame.getPresetFacing(unit);
            if (presetFacing != null) {
                return presetFacing;
            }
        }
        if (getGame().getGameMode() == GAME_MODES.DUNGEON_CRAWL) {
            return FacingMaster.getOptimalFacingTowardsEmptySpaces(unit);
        }
        Coordinates c = unit.getCoordinates();
        if (isAutoOptimalFacing())
            return getFacingOptimal(c, true);
        if (facingMap.containsKey(c)) {
            return facingMap.get(c);
        }
        return main.game.bf.directions.FACING_DIRECTION.NORTH;
    }

    public void unitPlaced(Coordinates adjacentCoordinate, FACING_DIRECTION facingFromDirection) {
        //what was the idea?..

    }
}
