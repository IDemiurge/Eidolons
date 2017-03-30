package main.game.core.state;

import main.game.core.game.DC_Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;

import java.util.Stack;

/**
 * Created by JustMe on 3/15/2017.
 */
public class StatesKeeper {
    private static StatesKeeper instance;
    DC_Game game;
    Stack<DC_GameState> states = new Stack<>();
    int head = 0;
    int index = 0;
    private StateCloner cloner;

    public StatesKeeper(DC_Game game) {
        this.game = game;
        cloner = new StateCloner(game);
        instance = this;
    }

    public static void testLoad() {
        Chronos.setOn(true);
        instance.goBack();
    }

    public void goBack() {
        index--;
        loadLastState();
    }

    private void loadLastState() {
        DC_GameState state = states.pop();
        game.setState(state);
        GuiEventManager.trigger(GuiEventType.REFRESH_GRID, null );
    }

    public void save() {
        DC_GameState clone = cloner.clone(game.getState());
        states.add(clone);
        head = states.size();
        index = head;
    }

    public void reset() {
        index = head;

    }

    public StateCloner getCloner() {
        return cloner;
    }
}
