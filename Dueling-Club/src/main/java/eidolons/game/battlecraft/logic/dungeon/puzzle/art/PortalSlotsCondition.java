package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.portal.PortalPuzzle;
import eidolons.game.core.Eidolons;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class PortalSlotsCondition extends DC_Condition {
    PortalPuzzle puzzle;

    public PortalSlotsCondition(PortalPuzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public boolean check(Ref ref) {
        for (Coordinates coordinates : puzzle.getSlots().keySet()) {
            PortalPuzzle.POWER_SLOT slot = puzzle.getSlots().get(coordinates);

            for (BattleFieldObject object : getGame().getObjectsOnCoordinateNoOverlaying(coordinates)) {
                if (!checkSlotFilled(slot, object))
                    return false;
            }
        }

        return true;
    }

    private boolean checkSlotFilled(PortalPuzzle.POWER_SLOT slot, BattleFieldObject object) {
        if (slot.allyValid) {
            if (object.isMine()) {
                return true;
            }
        }
            if (slot.selfValid) {
            if (object== Eidolons.getMainHero()) {
                return true;
            }
        }
        for (String s : slot.validObjectNames) {
            if (s.equalsIgnoreCase(object.getName())) {
                return true;
            }
        }
        return false;








    }
}
