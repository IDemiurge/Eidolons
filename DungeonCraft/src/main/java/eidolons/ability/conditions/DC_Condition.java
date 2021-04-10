package eidolons.ability.conditions;

import eidolons.game.core.game.DC_Game;
import main.elements.conditions.MicroCondition;

/**
 * Created by JustMe on 11/16/2017.
 */
public abstract class DC_Condition extends MicroCondition {

    public DC_Game getGame() {
        return (DC_Game) game;
    }
}
