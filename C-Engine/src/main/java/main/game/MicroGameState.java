package main.game;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.entity.obj.Obj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JustMe
 *
 * State for a MicroGame
 * Initializes speciafically micro OBJ_TYPE's
 */
public abstract class MicroGameState extends GameState {

    protected Map<OBJ_TYPE, List<Obj>> graveyard = new HashMap<>();

    public MicroGameState(MicroGame game) {
        super(game);

    }

    protected void initTypeMaps() {
        for (OBJ_TYPE TYPE : OBJ_TYPES.values()) {

            getObjMaps().put(TYPE, new HashMap<>());
        }
    }

    @Override
    public String toString() {
        String string = "";
        string += effects.size() + "EFFECTS: " + effects + "\n";
        string += triggers.size() + "TRIGGERS: " + triggers + "\n";
        string += attachments.size() + "ATTACHMENTS: " + attachments + "\n";
        string += attachedEffects.size() + "ATTACHED EFFECTS: "
                + attachedEffects + "\n";
        string += attachedTriggers.size() + "ATTACHED TRIGGERS: "
                + attachedTriggers + "\n";
        return string;
    }

    public MicroGame getGame() {
        return (MicroGame) game;
    }


    public void store() {
        // TODO stateStack.push(serialize());

    }

    public void restore() {
        // TODO Auto-generated method stub

    }

    public Map<OBJ_TYPE, List<Obj>> getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(Map<OBJ_TYPE, List<Obj>> graveyard) {
        this.graveyard = graveyard;
    }




    public abstract void gameStarted(boolean host);

}
