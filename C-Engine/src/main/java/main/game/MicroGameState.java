package main.game;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.entity.obj.Obj;
import main.game.player.Player;
import main.game.turn.TurnTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JustMe
 */
public abstract class MicroGameState extends GameState {

    protected boolean myTurn;
    protected Player activePlayer;

    protected Map<OBJ_TYPE, List<Obj>> graveyard = new HashMap<OBJ_TYPE, List<Obj>>();
    protected List<Obj> mySpellbook = new ArrayList<Obj>();
    protected List<Obj> enemySpellbook = new ArrayList<Obj>();

    public MicroGameState(MicroGame game) {
        super(game);

    }

    protected void initTypeMaps() {
        for (OBJ_TYPE TYPE : OBJ_TYPES.values()) {

            getObjMaps().put(TYPE, new HashMap<Integer, Obj>());
        }
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public boolean isMyTurn() {
        if (getGame().isHotseatMode())
            return true;
        return myTurn;
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

    public abstract TurnTimer getTimer();

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

    public void addSpell(Obj obj) {
        if (obj.getOwner().isMe()) {
            getMySpellbook().add(obj);
        } else {
            getEnemySpellbook().add(obj);
        }
    }

    public List<Obj> getMySpellbook() {
        return mySpellbook;
    }

    public void setMySpellbook(List<Obj> mySpellbook) {
        this.mySpellbook = mySpellbook;
    }

    public List<Obj> getEnemySpellbook() {
        return enemySpellbook;
    }

    public void setEnemySpellbook(List<Obj> enemySpellbook) {
        this.enemySpellbook = enemySpellbook;
    }

    public abstract void gameStarted(boolean host);

}
