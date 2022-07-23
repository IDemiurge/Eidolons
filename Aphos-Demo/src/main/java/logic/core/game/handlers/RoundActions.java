package logic.core.game.handlers;

import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.entity.Unit;

public class RoundActions {

    public void roundAction(Unit unit) {
        AUnitEnums.UnitType type =  unit.getType();
        switch (type) {
            case Melee, Explode, Sneak, Guard -> {
                tryMove(unit);
            }
            case Ranged, Caster -> {
            }
            case Multiclass -> {
            }
            case Bonus -> {
            }
            case Boss -> {
            }
        }
    }

    private void tryMove(Unit unit) {
        int i = Aphos.controller().getUnitMoveLogic().maxDstMoveForward(unit);
        if (i<=0)
            return;
        Aphos.controller().getUnitMoveLogic().unitMoveForward(unit, i);
    }
}
