package main.game.logic.battle.turn;

import main.entity.obj.Obj;
import main.system.datatypes.DequeImpl;

/**
 * Initiative
 * <portrait>
 * <portrait>
 * <portrait>
 * Lock-in the Active Unit ++ Action Panel ++ Remove BF highlight
 */

public interface TurnManager {
    void newRound();

    DequeImpl<? extends Obj> getUnitQueue();

    Obj getActiveUnit();

    void init();

    DequeImpl<? extends Obj> getDisplayedUnitQueue();

    int getTimeModifier();

}
