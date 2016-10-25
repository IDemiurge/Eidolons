package main.ability.conditions.special;

import main.elements.conditions.MicroCondition;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;

public class FleeCondition extends MicroCondition {

    KEYS key = KEYS.SOURCE;

    public FleeCondition(KEYS key) {
        this.key = key;
    }

    public FleeCondition() {

    }

    @Override
    public boolean check() {
        if (!(ref.getObj(key) instanceof DC_HeroObj))
            return false;
        DC_HeroObj obj = (DC_HeroObj) ref.getObj(key);

        if (!(obj.getX() == obj.getGame().getBattleField().getGrid().getWidth() - 1 || obj
                .getX() == 0))
            return false;
        if (!(obj.getY() == obj.getGame().getBattleField().getGrid()
                .getHeight() - 1 || obj.getY() == 0))
            return false;

        if (obj.getCoordinates().getAdjacentCoordinate(
                obj.getFacing().getDirection()) != null)
            return false;
        return true;
    }
}
