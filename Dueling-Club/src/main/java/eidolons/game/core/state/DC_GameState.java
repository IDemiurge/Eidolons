package eidolons.game.core.state;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.game.core.game.GenericGame;
import main.game.core.state.MicroGameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * h
 *
 * @author JustMe
 */
public class DC_GameState extends MicroGameState {
    private Map<Unit, Stack<DC_ActiveObj>> unitActionStack;


    public DC_GameState(GenericGame game) {
        super(game);
    }

    @Override
    public DC_StateManager getManager() {
        return (DC_StateManager) super.getManager();
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += getGame().getUnits().size() + "UNITS: " + getGame().getUnits() + "\n";

        return string;
    }

    @Override
    public void gameStarted(boolean first) {
        if (first) {
            this.setRound(DEFAULT_ROUND);
        } else {
            this.setRound(DEFAULT_ROUND);
//          ???  this.setRound(DEFAULT_ROUND - 1);
        }

    }


    public Map<Unit, Stack<DC_ActiveObj>> getUnitActionStack() {
        if (unitActionStack == null) {
            unitActionStack = new HashMap<>();
        }
        return unitActionStack;
    }

    public Stack<DC_ActiveObj> getUnitActionStack(Unit ownerObj) {
        Stack<DC_ActiveObj> stack = getUnitActionStack().get(ownerObj);
        if (stack == null) {
            stack = new Stack<>();
            getUnitActionStack().put(ownerObj, stack);
        }
        return stack;
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public void store() {
        // TODO stateStack.push(serialize());

    }

    public void restore() {
        // TODO Auto-generated method stub

    }
}
