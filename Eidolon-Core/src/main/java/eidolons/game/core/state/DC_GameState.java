package eidolons.game.core.state;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
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
    public static boolean gridChanged;
    private Map<Unit, Stack<ActiveObj>> unitActionStack;
    private int chaosLevel;


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
        this.setRound(DEFAULT_ROUND);

    }


    public Map<Unit, Stack<ActiveObj>> getUnitActionStack() {
        if (unitActionStack == null) {
            unitActionStack = new HashMap<>();
        }
        return unitActionStack;
    }

    public Stack<ActiveObj> getUnitActionStack(Unit ownerObj) {
        Stack<ActiveObj> stack = getUnitActionStack().get(ownerObj);
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

    public int getChaosLevel() {
        return chaosLevel;
    }

    public void increaseChaosLevel() {
        chaosLevel++;
    }
    public void setChaosLevel(int chaosLevel) {
        this.chaosLevel = chaosLevel;
    }
}
