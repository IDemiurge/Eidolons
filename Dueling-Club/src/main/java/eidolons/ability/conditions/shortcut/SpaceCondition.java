package eidolons.ability.conditions.shortcut;

import eidolons.game.core.game.DC_Game;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class SpaceCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        boolean result = false;
        try {
            result = DC_Game.game.getRules().getStackingRule().canBeMovedOnto(
             ref.getSourceObj(), ref.getMatchObj().getCoordinates());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return result;
    }

}
