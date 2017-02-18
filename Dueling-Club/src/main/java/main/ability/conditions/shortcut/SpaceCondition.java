package main.ability.conditions.shortcut;

import main.elements.conditions.MicroCondition;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public class SpaceCondition extends MicroCondition {

    @Override
    public boolean check() {
        boolean result = false;
        try {
            result = DC_Game.game.getRules().getStackingRule().canBeMovedOnto(
                    (Unit) ref.getSourceObj(), ref.getMatchObj().getCoordinates());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
