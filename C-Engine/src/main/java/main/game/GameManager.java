package main.game;

import main.ability.Interruptable;
import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.ability.effects.continuous.ContinuousEffect;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.ai.AI;
import main.game.battlefield.Coordinates;
import main.game.battlefield.pathing.Path;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.player.Player;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static main.system.GuiEventType.INGAME_EVENT_TRIGGERED;
import static main.system.GuiEventType.UPDATE_BUFFS;

/**
 * With gamestate being mostly a data holder, this class is a method container
 * Selection Top level actions - kill(), ... (no direct data manipulations)
 *
 * @author JustMe
 */
public abstract class GameManager implements GenericGameManager {
    protected boolean spellBookInitialized = false;
    protected GameState state;
    protected Game game;
    protected boolean myTurn;
    protected boolean selecting;
    protected boolean infoObjSelected;
    protected boolean activeObjSelected;
    protected Obj infoObj;
    protected Obj selectedActiveObj;
    protected AI ai;
    protected Set<Obj> selectingSet;
    protected Obj hoverObj;
    private boolean activatingPassives;
    private Path path;
    private Obj lastMovedUnit;
    private ActiveObj activatingAction;
    private Entity infoEntity;
    private boolean triggerBeingActivated;
    private boolean triggerBeingChecked;

    public GameManager() {

    }

    // +target
    public GameManager(MicroGameState state, MicroGame game) {
        this.setState(state);
        this.game = game;
        state.setManager(this);
        getGame().setManager(this);
    }

    public static boolean checkInterrupted(Ref ref) {

        if (ref.getObj(KEYS.ACTIVE) instanceof Interruptable) {
            return ((Interruptable) ref.getObj(KEYS.ACTIVE)).isInterrupted();
        }

        if (ref.getObj(KEYS.SPELL) instanceof Interruptable) {
            return ((Interruptable) ref.getObj(KEYS.SPELL)).isInterrupted();
        }

        return false;
    }

    public void infoSelect(Obj obj) {
        setSelectedInfoObj(obj);
        dataChanged();

    }

    public boolean activeSelect(Obj obj) {
        return false;
    }

    public abstract void objClicked(Obj obj);

    public abstract void win(Player winningPlayer);

    /**
     * @return the infoObjSelected
     */
    public boolean isInfoObjSelected() {
        return infoObjSelected;
    }

    /**
     * @param infoObjSelected the infoObjSelected to set
     */
    public void setInfoObjSelected(boolean infoObjSelected) {
        this.infoObjSelected = infoObjSelected;
    }

    /**
     * @return the activeObjSelected
     */
    public boolean isActiveObjSelected() {
        return activeObjSelected;
    }

    /**
     * @param activeObjSelected the activeObjSelected to set
     */
    public void setActiveObjSelected(boolean activeObjSelected) {
        this.activeObjSelected = activeObjSelected;
    }

    /**
     * @return the selectedInfoObj
     */
    public Obj getInfoObj() {
        return infoObj;
    }

    /**
     * @param selectedInfoObj the selectedInfoObj to set
     */
    public void setSelectedInfoObj(Obj selectedInfoObj) {
        this.infoObj = selectedInfoObj;
        setInfoObjSelected(selectedInfoObj != null);
    }

    /**
     * @return the selectedActiveObj
     */
    public Obj getActiveObj() {
        return selectedActiveObj;
    }

    // TURN

    /**
     * @param selectedActiveObj the selectedActiveObj to set
     */
    public void setSelectedActiveObj(Obj selectedActiveObj) {
        this.selectedActiveObj = selectedActiveObj;
        setActiveObjSelected(selectedActiveObj != null);
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public abstract void endTurn();

    public abstract void resetValues(Player owner);

    public abstract Integer select(Filter<Obj> filter, Ref ref);

    public boolean isSelecting() {
        return selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public Game getGame() {
        return game;
    }

    public void buffRemoved(BuffObj buff) {
        if (!buff.isDead()) {
            buff.kill();
            return;
        }
        getState().removeObject(buff.getId());
        buff.getBasis().getBuffs().remove(buff);
        attachmentRemoved(buff, buff.getBasis());
        getState().getAttachmentsMap().get(buff.getBasis()).remove(buff);
        getState().getAttachments().remove(buff);
        GuiEventManager.trigger(UPDATE_BUFFS, new EventCallbackParam(buff));
        // if (game.isStarted())
        // refreshGUI();
    }

    public abstract void refreshAll();

    public void attachmentRemoved(Attachment attachment, Obj basis) {
        for (Effect e : attachment.getEffects()) {
            // TODO
            e.remove();
            getState().removeEffect(e);
            // when addTrigger effect is removed, so is the trigger
        }
        // if (getState().getAttachedEffects().get(attachment) != null) {
        // if (!ListMaster.checkList(getState().getAttachedEffects()
        // .get(attachment)))
        // LogMaster
        // .log(LogMaster.EFFECT_DEBUG, " no effects attached to "
        // + attachment.toString() + " on " + basis);
        // else
        // for (Effect eff : getState().getAttachedEffects()
        // .get(attachment)) {
        // getState().removeEffect(eff);
        // }
        // getState().getAttachedEffects().remove(attachment);
        // }
        // if (getState().getAttachedTriggers().get(attachment) != null) {
        // for (Trigger trig : getState().getAttachedTriggers()
        // .get(attachment)) {
        // getState().removeTrigger(trig);
        // }
        // getState().getAttachedTriggers().remove(attachment);
        // }
    }

    public void passiveCreated(PassiveAbilityObj ability, Obj basis) {
        addAttachment(ability, basis);
    }

    public void buffCreated(BuffObj buff, Obj basis) {
        getState().addObject(buff);
        addAttachment(buff, basis);

        // if (game.isStarted())
        // refreshGUI();
    }

    // protected void attachPassive(PassiveAbilityObj attachment, Obj basis) {
    //
    // }
    public void addAttachment(Attachment attachment, Obj basis) {
        List<Attachment> list = getState().getAttachmentsMap().get(basis);
        if (list == null) {
            list = new LinkedList<>();
            getState().getAttachmentsMap().put(basis, list);
        }
        if (attachment instanceof BuffObj) {
            basis.addBuff((BuffObj) attachment);
        }
        getState().addAttachment(attachment);
        list.add(attachment);
        if (attachment.isTransient()) // e.g. auras
        {
            return;
        }
        for (Effect e : attachment.getEffects()) {
            // e.apply(basis.getRef()); // how to add retain conditions?
            // else
            // if (!(e instanceof AttachmentEffect))
            getState().addEffect(e);
        }
    }

    public void continuousEffectApplies(ContinuousEffect effect) {
        // attachEffect(effect);
        // getState().addEffect(effect);
    }

    protected void attachEffect(ContinuousEffect effect) {

        Obj source = effect.getRef().getObj(Ref.KEYS.BUFF);
        if (source == null) {
            source = effect.getRef().getObj(KEYS.ABILITY);
        }
        if (source instanceof BuffObj) {
            BuffObj buff = (BuffObj) source;

            List<Effect> list = getState().getAttachedEffects().get(buff);
            if (list == null) {
                list = new LinkedList<>();
                getState().getAttachedEffects().put(buff, list);
            } else {
                LogMaster.log(LogMaster.EFFECT_DEBUG, effect
                        + " - another effect attached to " + source);

            }
            LogMaster.log(LogMaster.EFFECT_DEBUG, effect
                    + " - Effect attached to " + source);
            list.add(effect);
        } else {
            LogMaster.log(1, "UNATTACHED EFFECT: " + effect);
        }
    }

    public void addTrigger(Trigger trigger) {
        getState().addTrigger(trigger);
        // attachTrigger(trigger);
        LogMaster.log(LogMaster.CORE_DEBUG, "trigger added!");
    }

    protected void attachTrigger(Trigger trigger) {
        Obj source = trigger.getRef().getObj(KEYS.BUFF);
        if (source == null) {
            source = trigger.getRef().getObj(KEYS.ABILITY);
        }
        Attachment attachment;
        if (source instanceof Attachment) { // passive ability?
            attachment = (Attachment) source;

            List<Trigger> list = getState().getAttachedTriggers().get(source);
            // TODO what if the trigger has a different BASIS than buff?
            if (list == null) {
                list = new LinkedList<>();
                getState().getAttachedTriggers().put(attachment, list);
            }
            list.add(trigger);
        } else {
            LogMaster.log(1, "UNATTACHED TRIGGER: " + trigger);
        }

    }

    public List<Attachment> getAttachments(Obj obj) {
        return getState().getAttachmentsMap().get(obj);
    }

    public boolean isSpellpoolInitialized() {
        return spellBookInitialized;
    }

    public void setSbInitialized(boolean sbInitialized) {
        this.spellBookInitialized = sbInitialized;
    }

    public abstract void reset();

    public boolean enactAI() {
        return ai.makeTurn();

    }

    public AI getAI() {
        return ai;
    }

    public void setAI(AI ai) {
        this.ai = ai;

    }

    public boolean isActivatingPassives() {
        return activatingPassives;
    }

    public void setActivatingPassives(boolean b) {
        this.activatingPassives = b;
    }

    public boolean isActivatingAction() {
        return getActivatingAction() != null;
    }

    public void resetValues() {
        // TODO Auto-generated method stub

    }

    public boolean effectApplies(EffectImpl effect) {
        Ref ref = effect.getRef();
        ref.setEffect(effect);
        return (!getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.EFFECT_IS_BEING_APPLIED, ref)));

    }

    public void setLastTraversedPath(Path path) {
        this.path = path;
        LogMaster.log(LogMaster.COMBAT_DEBUG, "Path traversed " + path);
    }

    public void clearLastTraversedPath() {
        if (path != null) {
            path.clearGraphics();
        }
    }

    public Obj getLastMovedUnit() {
        return lastMovedUnit;
    }

    public void setLastMovedUnit(Obj lastMovedUnit) {
        this.lastMovedUnit = lastMovedUnit;
    }

    public void dataChanged() {
        getGame().getGui().dataChanged();
    }

    public boolean handleEvent(Event event) {
        GuiEventManager.trigger(INGAME_EVENT_TRIGGERED, new EventCallbackParam(event));
        if (!game.isStarted()) {
            return true;
        }
        if (event.getRef().isQuiet()) {
            return true;
        }
        LogMaster.log(LogMaster.EVENT_DEBUG, "*** Event being handled: " + event);
        getState().checkTriggers(event);
        getState().checkRules(event);
        if (getState().isDirty()) {
            reset(); // ???
        }

        boolean result = true;
        if (event.canBeInterrupted()) {
            result = !checkInterrupted(event.getRef());
        }
        game.getLogManager().logEvent(event, result);
        return result;

    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void deselectInfo() {

    }

    public abstract void highlight(Set<Obj> set);

    public abstract void highlightsOff();

    public void deselectActive() {

    }

    public void refreshGUI() {
        // TODO Auto-generated method stub

    }

    public void rightClicked(Obj obj) {

        // deselectInfo();
        infoSelect(obj);

    }

    public MicroObj createUnit(ObjType type, int x, int y, Player owner) {
        return createUnit(type, x, y, owner, new Ref());
    }

    public MicroObj createUnit(ObjType type, Coordinates c, Player owner) {
        return createUnit(type, c.x, c.y, owner);
    }

    public abstract MicroObj createSpell(ObjType type, MicroObj owner, Ref ref);

    public abstract MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref);

    public abstract MicroObj createSpell(ObjType type, Player player, Ref ref);

    public abstract BuffObj createBuff(BuffType type, Obj active, Player player, Ref ref,
                                       Effect effect, int duration, Condition retainCondition);

    public ActiveObj getActivatingAction() {
        return activatingAction;
    }

    public void setActivatingAction(ActiveObj activeObj) {
        this.activatingAction = activeObj;
    }

    public void checkForChanges(boolean after) {
    }

    public void infoSelect(Entity entity) {
        this.setInfoEntity(entity);
    }

    public Entity getInfoEntity() {
        return infoEntity;
    }

    public void setInfoEntity(Entity infoEntity) {
        this.infoEntity = infoEntity;
    }

    public boolean isTriggerBeingActivated() {
        return triggerBeingActivated;
    }

    public void setTriggerBeingActivated(boolean b) {
        this.triggerBeingActivated = b;
    }

    public boolean isTriggerBeingChecked() {
        return triggerBeingChecked;
    }

    public void setTriggerBeingChecked(boolean b) {
        this.triggerBeingChecked = b;
    }

    public Set<Obj> getSelectingSet() {
        return selectingSet;
    }

    public void refresh(boolean visibility) {

    }

    public Obj getHoverObj() {
        return hoverObj;
    }

    public void setHoverObj(Obj obj) {
        this.hoverObj = obj;

    }

    public abstract Integer select(Set<Obj> selectingSet);

}
