package main.game.core.state;

import main.ability.effects.Effect;
import main.content.OBJ_TYPE;
import main.data.ConcurrentMap;
import main.elements.triggers.Trigger;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.game.GenericGame;
import main.game.logic.event.Event;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all the data about a Game (like a save) stored in maps
 * //TODO master-classes should be linked here too, e.g. DungeonMaster,
 * Objects are mapped to ids
 * Provides methods for retrieving it and some for managing it (refactor needed)
 */
public abstract class GameState {

    protected static final int DEFAULT_ROUND = 0;
    protected static final int ROUND_NOT_SET = -1;
    private static boolean resetDone;
    protected boolean dirty = false;
    protected boolean interrupted = false;


    protected Map<Integer, Obj> objMap = new ConcurrentMap<>();
    protected Map<OBJ_TYPE, Map<Integer, Obj>> objMaps = new ConcurrentMap<>();
    protected Map<Integer, ObjType> typeMap = new ConcurrentMap<>();
    protected Map<Obj, List<Attachment>> attachmentsMap = new ConcurrentMap<>();
    protected DequeImpl<Trigger> triggers = new DequeImpl<>();
    protected DequeImpl<Effect> effects = new DequeImpl<>();
    protected DequeImpl<Attachment> attachments = new DequeImpl<>();


    public StateManager manager;
    protected Game game;
    private int round = ROUND_NOT_SET;
    private boolean cloned;

    public GameState(Game game) {
        this.game = game;
        initTypeMaps();
    }

    public static boolean isResetDone() {
        return resetDone;
    }

    public static void setResetDone(boolean resetDone) {
        GameState.resetDone = resetDone;
    }

    protected void initTypeMaps() {
    }

    public void init() {
        objMap = new ConcurrentMap<>();
        objMaps = new HashMap<>();
        typeMap = new ConcurrentMap<>();

        attachmentsMap = new HashMap<>();

        triggers = new DequeImpl<>();
        effects = new DequeImpl<>();
        attachments = new DequeImpl<>();
    }

    public void reset() {
        setRound(DEFAULT_ROUND - 1);
    }


    public int getRoundDisplayedNumber() {
        return getRound() + 1;
    }

    public  int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(GenericGame game) {
        this.game = game;
    }

    public Map<Integer, Obj> getObjects() {
        return objMap;
    }


    protected void removed(Obj obj) {
    }


    public Map<OBJ_TYPE, Map<Integer, Obj>> getObjMaps() {
        return objMaps;
    }

    public Map<Obj, List<Attachment>> getAttachmentsMap() {
        return attachmentsMap;
    }


    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public DequeImpl<Attachment> getAttachments() {
        return attachments;
    }


    public boolean isDirty() {
        return dirty
         // || checkObjDirty()
         // TODO FIX!!!
         ;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }


    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public  Map<Integer, Obj> getObjMap() {
        return objMap;
    }

    public  Map<Integer, ObjType> getTypeMap() {
        return typeMap;
    }

    public  DequeImpl<Trigger> getTriggers() {
        return triggers;
    }


    public  DequeImpl<Effect> getEffects() {
        return effects;
    }

    public  StateManager getManager() {
        return manager;
    }

    public void setManager(StateManager manager) {
        this.manager = manager;
    }

    public void addObject(Obj obj) {
        manager.addObject(obj);
    }

    public void checkTriggers(Event e) {
        manager.checkTriggers(e);
    }

    public void checkRules(Event e) {
        manager.checkRules(e);
    }

    public void addTrigger(Trigger t) {
        manager.addTrigger(t);
    }

    public void addEffect(Effect effect) {
        manager.addEffect(effect);
    }

    public boolean isCloned() {
        return cloned;
    }

    public void setCloned(boolean cloned) {
        this.cloned = cloned;
    }

    public void increaseChaosLevel() {

    }
}
