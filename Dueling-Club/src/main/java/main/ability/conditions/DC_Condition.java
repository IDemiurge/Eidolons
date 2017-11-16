package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 11/16/2017.
 */
public abstract class DC_Condition extends MicroCondition {

    public DC_Game getGame(){
        return (DC_Game) game;
    }
}
