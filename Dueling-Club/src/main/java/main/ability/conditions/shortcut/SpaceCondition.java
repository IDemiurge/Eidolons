package main.ability.conditions.shortcut;

import main.elements.conditions.MicroCondition;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;

public class SpaceCondition extends MicroCondition {

    @Override
    public boolean check() {
        boolean result = false;
        try {
            result = DC_Game.game.getRules().getStackingRule().canBeMovedOnto(
                    (DC_HeroObj) ref.getSourceObj(), ref.getMatchObj().getCoordinates());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
