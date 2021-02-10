package main.game.core.state;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.ConcurrentMap;
import main.entity.obj.Obj;
import main.game.core.game.GenericGame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JustMe
 *         <p>
 *         State for a MicroGame
 *         Initializes speciafically micro OBJ_TYPE's
 */
public abstract class MicroGameState extends GameState {

    protected Map<OBJ_TYPE, List<Obj>> graveyard = new HashMap<>();

    public MicroGameState(GenericGame game) {
        super(game);

    }

    protected void initTypeMaps() {
        for (OBJ_TYPE TYPE : DC_TYPE.values()) {
            getObjMaps().put(TYPE, new ConcurrentMap<>());
        }
    }

    @Override
    public String toString() {
        String string = "";
        string += effects.size() + "EFFECTS: " + effects + "\n";
        string += triggers.size() + "TRIGGERS: " + triggers + "\n";
        string += attachments.size() + "ATTACHMENTS: " + attachments + "\n";

        return string;
    }

    public GenericGame getGame() {
        return (GenericGame) game;
    }


    public Map<OBJ_TYPE, List<Obj>> getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(Map<OBJ_TYPE, List<Obj>> graveyard) {
        this.graveyard = graveyard;
    }


    public abstract void gameStarted(boolean host);

}
