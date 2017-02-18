package main.game.core.state;

import main.ability.effects.Effect;
import main.content.OBJ_TYPE;
import main.data.ConcurrentMap;
import main.elements.triggers.Trigger;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.game.MicroGame;
import main.game.logic.event.Event;
import main.game.logic.event.Rule;
import main.system.auxiliary.log.LogMaster;
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
    protected boolean dirty = false;
    protected boolean interrupted = false;
    protected Map<Integer, Obj> objMap = new ConcurrentMap<>();
    protected Map<OBJ_TYPE, Map<Integer, Obj>> objMaps = new ConcurrentMap<>();
    protected Map<OBJ_TYPE, Map<Integer, ObjType>> typeMaps = new ConcurrentMap<>();
    protected Map<Integer, ObjType> typeMap = new ConcurrentMap<>();
    protected Map<Attachment, List<Effect>> attachedEffects = new ConcurrentMap<>();
    protected Map<Attachment, List<Trigger>> attachedTriggers = new ConcurrentMap<>();
    protected Map<Attachment, List<Obj>> attachedObjects = new ConcurrentMap<>();
    protected Map<Obj, List<Attachment>> attachmentsMap = new ConcurrentMap<>();
    protected DequeImpl<Trigger> triggers = new DequeImpl<>();
    protected DequeImpl<Rule> triggerRules = new DequeImpl<>();
    protected DequeImpl<Effect> effects = new DequeImpl<>();
    protected Map<Integer, Effect> effectsMap = new ConcurrentMap<>();
    protected DequeImpl<Attachment> attachments = new DequeImpl<>();
    //TODO remove spaghetti!
    protected StateManager manager;
    protected Game game;
    private int round = ROUND_NOT_SET;

    public GameState(Game game) {
        this.game = game;
        initTypeMaps();
    }

    protected void initTypeMaps() {
    }

    public void init() {
        objMap = new ConcurrentMap<>();
        objMaps = new HashMap<>();
        typeMaps = new HashMap<>();
        typeMap = new ConcurrentMap<>();

        attachedEffects = new HashMap<>();
        attachedTriggers = new HashMap<>();
        attachedObjects = new HashMap<>();
        attachmentsMap = new HashMap<>();

        triggers = new DequeImpl<>();
        effects = new DequeImpl<>();
        effectsMap = new ConcurrentMap<>();
        attachments = new DequeImpl<>();
    }

    public void reset() {
        setRound(DEFAULT_ROUND - 1);
    }


    public int getRoundDisplayedNumber() {
        return getRound() + 1;
    }

    public synchronized int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(MicroGame game) {
        this.game = game;
    }

    public Map<Integer, Obj> getObjects() {
        return objMap;
    }

    public Map<Integer, ObjType> getTypes() {
        return typeMap;
    }

    public void removeTrigger(Trigger trigger) {
        LogMaster.log(LogMaster.TRIGGER_DEBUG, "Trigger removed: " + trigger);
        triggers.remove(trigger);
    }

    public void removeEffect(Effect effect) {
        if (!effects.remove(effect)) {
            LogMaster.log(LogMaster.EFFECT_DEBUG, "Effect could not be removed: " + effect);
        } else {
            LogMaster.log(LogMaster.EFFECT_DEBUG, "Effect removed: " + effect);
        }

        // setDirty(true);
        // resetAll();
    }

    public void removeObject(Integer id) {
        Obj obj = objMap.get(id);
        LogMaster.log(LogMaster.CORE_DEBUG_1, "Obj removed: " + obj);
        objMap.remove(id);
    }


    public Map<OBJ_TYPE, Map<Integer, ObjType>> getTypeMaps() {
        return typeMaps;
    }

    public Map<OBJ_TYPE, Map<Integer, Obj>> getObjMaps() {
        return objMaps;
    }

    public Map<Obj, List<Attachment>> getAttachmentsMap() {
        return attachmentsMap;
    }


    public Map<Attachment, List<Effect>> getAttachedEffects() {
        return attachedEffects;
    }


    public Map<Attachment, List<Trigger>> getAttachedTriggers() {
        return attachedTriggers;
    }


    public Map<Attachment, List<Obj>> getAttachedObjects() {
        return attachedObjects;
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

    protected boolean checkObjDirty() {
        boolean result = false;
        for (Obj obj : getObjects().values()) {
            result |= obj.isDirty();
        }
        return result;

    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public synchronized Map<Integer, Obj> getObjMap() {
        return objMap;
    }

    public synchronized Map<Integer, ObjType> getTypeMap() {
        return typeMap;
    }

    public synchronized DequeImpl<Trigger> getTriggers() {
        return triggers;
    }

    public synchronized DequeImpl<Rule> getTriggerRules() {
        return triggerRules;
    }


    public synchronized DequeImpl<Effect> getEffects() {
        return effects;
    }

    public synchronized StateManager getManager() {
        return manager;
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

    public ObjType getTypeById(Integer id) {
        return manager.getTypeById(id);
    }

    public void setManager(StateManager manager) {
        this.manager = manager;
    }
}
