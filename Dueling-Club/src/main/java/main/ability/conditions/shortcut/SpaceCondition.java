package main.ability.conditions.shortcut;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.game.core.game.DC_Game;

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
