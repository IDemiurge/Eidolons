package main.game.core.game;

import main.ability.Interruptable;
import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
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
import main.game.bf.Coordinates;
import main.game.bf.pathing.Path;
import main.game.core.state.GameState;
import main.game.core.state.MicroGameState;
import main.game.core.state.StateManager;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;

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
    protected StateManager stateManager;
    protected GameMaster gameMaster;
    protected boolean myTurn;
    protected boolean selecting;
    protected boolean infoObjSelected;
    protected boolean activeObjSelected;
    protected Obj infoObj;
    protected Obj selectedActiveObj;
    protected AI ai;
    protected Set<Obj> selectingSet;
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


    public void setInfoObjSelected(boolean infoObjSelected) {
        this.infoObjSelected = infoObjSelected;
    }


    public void setActiveObjSelected(boolean activeObjSelected) {
        this.activeObjSelected = activeObjSelected;
    }

    public Obj getInfoObj() {
        return infoObj;
    }

    public void setSelectedInfoObj(Obj selectedInfoObj) {
        this.infoObj = selectedInfoObj;
        setInfoObjSelected(selectedInfoObj != null);
    }

    public Obj getActiveObj() {
        return selectedActiveObj;
    }

    public abstract void buffCreated(BuffObj buff, Obj basis);

    public void setSelectedActiveObj(Obj selectedActiveObj) {
        this.selectedActiveObj = selectedActiveObj;
        setActiveObjSelected(selectedActiveObj != null);
    }

    public abstract void endRound();

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
    }

    public abstract void refreshAll();

    public void attachmentRemoved(Attachment attachment, Obj basis) {
        for (Effect e : attachment.getEffects()) {
            e.remove();
            getState().removeEffect(e);
            //TODO when addTrigger effect is removed, so is the trigger
        }
    }

    public void addTrigger(Trigger trigger) {
        getState().addTrigger(trigger);
        // attachTrigger(trigger);
        LogMaster.log(LogMaster.CORE_DEBUG, "trigger added!");
    }


    public void setSbInitialized(boolean sbInitialized) {
        this.spellBookInitialized = sbInitialized;
    }

    public abstract void reset();

    public AI getAI() {
        return ai;
    }

    public void setAI(AI ai) {
        this.ai = ai;

    }

    public void setActivatingPassives(boolean b) {
        this.activatingPassives = b;
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

    public void dataChanged() {
        if (getGame().getGui() != null) {
            getGame().getGui().dataChanged();
        }
    }

    public boolean handleEvent(Event event) {
        if (event.getRef().getEffect() != null) {
            if (event.getRef().getEffect().isQuietMode()) {
                return true;
            }
        }

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

    public void deselectActive() {

    }

    public void refreshGUI() {

    }

    public void rightClicked(Obj obj) {
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

    public StateManager getStateManager() {
        return stateManager;
    }

    public GameMaster getGameMaster() {
        return gameMaster;
    }



    public abstract Integer select(Set<Obj> selectingSet, Ref ref);

    public void addAttachment(PassiveAbilityObj abil, Obj obj) {
    }
}
