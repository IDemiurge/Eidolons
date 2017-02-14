package main.game;

import main.ability.effects.Effect;
import main.content.OBJ_TYPE;
import main.data.ConcurrentMap;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.event.Event;
import main.game.event.Rule;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;
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
    protected Game game;
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
    protected GameManager mngr;
    private int round = ROUND_NOT_SET;

    public GameState(Game game) {
        this.game = game;
        initTypeMaps();
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

    public void checkTriggers(Event e) {
        if (triggers.size() == 0) {
            return;
        }
        LogMaster.log(0, triggers.size() + "");
        for (Trigger trigger : triggers) {

            trigger.check(e);
        }
    }

    public void checkRules(Event e) {
        if (triggerRules.size() == 0) {
            return;
        }

        for (Rule rule : triggerRules) {
            if (rule.isOn()) {
                if (rule.check(e)) {
                    Ref ref = Ref.getCopy(e.getRef());
                    ref.setEvent(e);
                    rule.apply(ref);
                }
            }
        }
    }

    public void addTrigger(Trigger t) {
        LogMaster.log(LogMaster.TRIGGER_DEBUG, " added " + t);
        this.triggers.add(t);
    }

    public void addEffect(Effect effect) {
        effect.initLayer();
        // effect.getRef().getBasis(); // TODO ref cloning revealed its purpose
        // at
        // last!!!
        effects.add(effect);
        LogMaster.log(LogMaster.EFFECT_DEBUG, effect.getClass()
                .getSimpleName()
                + " effect added : " + effects.size() + effects);
        // if (game.isStarted())
        // resetAll();
    }

    protected abstract void initTypeMaps();

    public void addObject(Obj obj) {

        objMap.put(obj.getId(), obj);
        if (!game.isSimulation()) {
            OBJ_TYPE TYPE = obj.getOBJ_TYPE_ENUM();
            if (TYPE == null) {
                LogMaster.log(1, obj.toString() + " has no TYPE!");
                return;
            }
            Map<Integer, Obj> map = getObjMaps().get(TYPE);
            if (map == null) {
                return;
            }
            // if (!map.containsValue(obj))
            map.put(obj.getId(), obj);
        }
    }

    public ObjType getTypeById(Integer id) {
        if (id == null) {
            LogMaster.log(LogMaster.CORE_DEBUG,
                    "searching for type by null id");
            return null;
        }
        try {
            return typeMap.get(id);
        } catch (Exception e) {
            LogMaster
                    .log(LogMaster.CORE_DEBUG_1, "no type found by id " + id);
            return null;
        }
    }

    public Obj getObjectById(Integer id) {
        if (id == null) {
            LogMaster.log(LogMaster.CORE_DEBUG,
                    "searching for obj by null id");
            return null;
        }
        try {
            return objMap.get(id);
        } catch (Exception e) {
            LogMaster.log(LogMaster.CORE_DEBUG_1, "no obj found by id " + id);
            return null;
        }

    }

    public void resetAll() {
        // store();
        triggers.clear();
        mngr.checkForChanges(false);
        // effects.clear();
        Chronos.mark("ALL TO BASE");
        try {
            allToBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chronos.logTimeElapsedForMark("ALL TO BASE");

        try {
            checkCounterRules();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chronos.mark("Apply Effects");
        try {
            applyEffects(Effect.ZERO_LAYER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO (Attributes and percentages) = >
        Chronos.logTimeElapsedForMark("Apply Effects");
        Chronos.mark("Reset Unit Objects");
        try {
            resetUnitObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chronos.logTimeElapsedForMark("Reset Unit Objects");
        resetRawValues();
        try {
            applyEffects(Effect.BASE_LAYER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Chronos.mark("After Effects");
        try {
            afterEffects();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // changes to derived values?
        Chronos.logTimeElapsedForMark("After Effects");

        Chronos.mark("Apply Effects SECOND_LAYER");
        try {
            applyEffects(Effect.SECOND_LAYER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chronos.logTimeElapsedForMark("Apply Effects SECOND_LAYER");
        Chronos.mark("Apply Effects BUFF_RULE");
        try {
            applyEffects(Effect.BUFF_RULE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Chronos.logTimeElapsedForMark("Apply Effects BUFF_RULE");
        try {
            checkContinuousRules();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            applyMods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetCurrentValues();
        mngr.checkForChanges(true);

        GuiEventManager.trigger(GuiEventType.UPDATE_LIGHT, null);
        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, null);
        GuiEventManager.trigger(GuiEventType.UPDATE_EMITTERS, null);
    }

    public abstract void checkContinuousRules();

    // this should only be implemented in game's subclass libs!

    public void resetRawValues() {
    }

    public abstract void checkCounterRules();

    public void resetUnitObjects() {

    }

    public void applyEffects(int layer) {
        if (effects.size() == 0) {
            return;
        }
        for (Effect effect : effects) {
            if (effect.getLayer() == layer) {
                LogMaster.log(LOG_CHANNELS.EFFECT_DEBUG, layer
                        + " Layer, applying effect : " + effects);
                if (!effect.apply()) {
                    LogMaster.log(LogMaster.EFFECT_DEBUG, layer
                            + " Layer, effect failed: " + effects);
                }
            }
        }
    }

    public GameManager getManager() {
        return mngr;

    }

    public void setManager(GameManager gameManager) {
        this.mngr = gameManager;
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

    public abstract void endTurn();

    public abstract void newRound(); // TODO only when *my* turn ends!

    public abstract void allToBase();

    protected abstract void resetCurrentValues();

    public void afterEffects() {
        for (Obj obj : objMap.values()) {
            obj.afterEffects();
        }
    }

    protected abstract void applyMods();

    public Map<OBJ_TYPE, Map<Integer, ObjType>> getTypeMaps() {
        return typeMaps;
    }

    public Map<OBJ_TYPE, Map<Integer, Obj>> getObjMaps() {
        return objMaps;
    }

    public void setObjMaps(Map<OBJ_TYPE, Map<Integer, Obj>> objMaps) {
        this.objMaps = objMaps;
    }

    public Map<Obj, List<Attachment>> getAttachmentsMap() {
        return attachmentsMap;
    }

    public void setAttachmentsMap(Map<Obj, List<Attachment>> attachmentsMap) {
        this.attachmentsMap = attachmentsMap;
    }

    public Map<Attachment, List<Effect>> getAttachedEffects() {
        return attachedEffects;
    }

    public void setAttachedEffects(Map<Attachment, List<Effect>> attachedEffects) {
        this.attachedEffects = attachedEffects;
    }

    public Map<Attachment, List<Trigger>> getAttachedTriggers() {
        return attachedTriggers;
    }

    public void setAttachedTriggers(Map<Attachment, List<Trigger>> attachedTriggers) {
        this.attachedTriggers = attachedTriggers;
    }

    public Map<Attachment, List<Obj>> getAttachedObjects() {
        return attachedObjects;
    }

    public void setAttachedObjects(Map<Attachment, List<Obj>> attachedObjects) {
        this.attachedObjects = attachedObjects;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public DequeImpl<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(DequeImpl<Attachment> attachments) {
        this.attachments = attachments;

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

    public synchronized void setTriggerRules(DequeImpl<Rule> rules) {
        this.triggerRules = rules;
    }

    public synchronized DequeImpl<Effect> getEffects() {
        return effects;
    }

    public synchronized GameManager getMngr() {
        return mngr;
    }

}
