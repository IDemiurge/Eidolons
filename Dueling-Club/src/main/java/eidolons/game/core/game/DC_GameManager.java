package eidolons.game.core.game;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.Eidolons;
import eidolons.game.core.master.*;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.bf.TargetRunnable;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.content.C_OBJ_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.UnitEnums;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.GameManager;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.Manager;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Set;

import static main.system.GuiEventType.*;

/**
 * *
 *
 * @author JustMe
 */
public class DC_GameManager extends GameManager {

    private BuffMaster buffMaster;
    private EffectMaster effectMaster;
    private SpellMaster spellMaster;
    private DeathMaster deathMaster;
    private ObjCreator objCreator;
    private BattleFieldObject highlightedObj;

    public DC_GameManager(DC_GameState state, DC_Game game) {
        super(state, game);
        Manager.init(game, state, this);

        stateManager = new DC_StateManager(state);
        gameObjMaster = game.getMaster();// new DC_GameMaster(game);
        Eidolons.stateManager = getStateManager();
        Eidolons.gameMaster = getGameObjMaster();
        Eidolons.game = game;
        Eidolons.gameManager = this;
        state.setManager(getStateManager());

    }

    public static boolean checkInterrupted(Ref ref) {

        if (ref.getObj(KEYS.ACTIVE) != null) {
            return ((DC_ActiveObj) ref.getObj(KEYS.ACTIVE)).isInterrupted();
        }

        if (ref.getObj(KEYS.SPELL) != null) {
            return ((DC_ActiveObj) ref.getObj(KEYS.SPELL)).isInterrupted();
        }

        return false;
    }

    public void init() {
        buffMaster = new BuffMaster(getGame());
        effectMaster = new EffectMaster(getGame());
        spellMaster = new SpellMaster(getGame());
        deathMaster = new DeathMaster(getGame());
        objCreator = new ObjCreator(getGame());

    }

    @Override
    public BuffObj createBuff(BuffType type, Obj active, Player player, Ref ref, Effect effect, int duration, Condition retainCondition) {
        return buffMaster.createBuff(type, active, player, ref, effect, duration, retainCondition);
    }

    public DC_Game getGame() {
        return (DC_Game) game;
    }

    public boolean activeSelect(final Obj obj) {
        boolean result = true;
        for (ActionRule ar : getGame().getRules().getActionRules()) {
            try {
                result &= ar.unitBecomesActive((Unit) obj);
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
                result = true;
            }
        }
        if (!result) {
            return false;
        }
        // DC_SoundMaster.playEffectSound(SOUNDS.WHAT, obj);

        GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, getActiveObj());
        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTIVE_UNIT_SELECTED, getActiveObj());
        return true;
    }

    @Override
    public void objClicked(Obj obj) {
        if (isSelecting()) {
            checkSelectedObj(obj);
            return;
        }
    }


    /**
     * @param action
     * @param result false if turn should end
     */
    public void unitActionCompleted(DC_ActiveObj action, Boolean result) {
        if (action != null) {
            getGame().getState().getUnitActionStack(action.getOwnerUnit()).push(action);
        }
        if (result == null) {
            WaitMaster.interrupt(WAIT_OPERATIONS.ACTION_COMPLETE);
        } else {
            WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, result);
        }
    }


    @Override
    public void reset() {
        if (!game.isStarted()) {
            updateGraphics();
            resetWallMap();
            return;
        }
        getGameObjMaster().clearCaches();
        FutureBuilder.clearCaches();
        ArrayMaster.resetBufferArray();
        getStateManager().resetAllSynchronized();
        checkForChanges(true);

        resetWallMap();

        VisionManager.refresh();

        updateGraphics();
    }

    private void updateGraphics() {
        //set dirty flag?
        GuiEventManager.trigger(UPDATE_GUI, null);
        //        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, null);
        if (!getMainHero().isDead())
            GuiEventManager.trigger(UPDATE_MAIN_HERO, getMainHero());
        else {
            if (getActiveObj().isMine())
                GuiEventManager.trigger(UPDATE_MAIN_HERO, getActiveObj() );
        }
    }

    public void resetWallMap() {
        getGame().getBattleFieldManager().resetWallMap();

    }

    public void refreshAll() {
        refresh(true);
    }

    public void refresh(boolean visibility) {
        if (game.isSimulation()) {
            return;
        }
        try {
            if (visibility) {
                try {
                    getGame().getVisionMaster().refresh();
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }


        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }

        // checkForChanges();
    }


    private void checkSelectedObj(Obj obj) {
        boolean selectionObj = selectingSet.contains(obj);

        if (!selectionObj) {
            if (C_OBJ_TYPE.BF_OBJ.equals(obj.getOBJ_TYPE_ENUM())) {
                selectionObj = selectingSet.contains(getGame().getCellByCoordinate(
                 obj.getCoordinates()));
            }
        }
        if (!selectionObj) {
            // if (MessageManager.confirm(CANCEL_SELECTING)) {
            ActiveObj activatingAction = getActivatingAction();
            if (activatingAction != null) {
                activatingAction.playCancelSound();
            }
            selectingStopped(true);
            return;

        }

        DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_TARGET_SELECTED);
        try {
            selectingStopped(false);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        } finally {
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_BF_OBJ, obj.getId());
        }

    }


    // a single-method spell, Warp Time: take another turn...
    public void resetValues(Player owner) {
        for (Obj obj : getGame().getBfObjects()) {
            BattleFieldObject unit = null;
            if (obj instanceof BattleFieldObject) {
                unit = (BattleFieldObject) obj;
                if (owner == null || unit.getOwner() == owner) {
                    unit.newRound();
                }
            }
            unit.regen();
        }
    }

    @Override
    public void resetValues() {
        resetValues(null);
    }


    @Override
    public Integer select(Filter<Obj> filter, Ref ref) {
        selectingSet = filter.getObjects();


        return select(selectingSet, ref);
    }

    @Override
    public Integer select(Set<Obj> selectingSet, Ref ref) {
        Pair<Set<Obj>, TargetRunnable> p = new ImmutablePair<>(selectingSet, (t) -> {
            if (ref.getActive() instanceof DC_ActiveObj) {
                //TODO CLICK ON ANY OTHER OBJ MUST RESULT IN SELECTION STOP!
                //                    ((DC_ActiveObj) ref.getActive()).activateOn(t);
                //                    WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_BF_OBJ, t.getId());
                t.invokeClicked();
            }
        });
        GdxMaster.setTargetingCursor();
        GuiEventManager.trigger(SELECT_MULTI_OBJECTS, p);

        for (Obj obj : new ArrayList<>(selectingSet)) {
            if (obj instanceof DC_Obj) {
                DC_Obj unit = (DC_Obj) obj;
                if (getActiveObj() != null) {
                    if (getActiveObj().getZ() != unit.getZ()) {
                        selectingSet.remove(unit);
                    }
                }
            }
        }
        this.selectingSet = selectingSet;

        if (selectingSet.isEmpty()) {
            //            getGame().getToolTipMaster().addTooltip(SCREEN_POSITION.ACTIVE_UNIT_BOTTOM,
            //             "No targets available!");
            DC_SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
            return null;
        }
        setSelecting(true);

        Integer id = selectAwait();
        GdxMaster.setDefaultCursor();

        if (id == null) {
            if (ref.getTarget() != null) {
                return ref.getTarget();
            }
        }
        return id;
    }


    public Integer selectAwait() {

        // add Cancel button? add hotkey listener?
        LogMaster.log(1, "***** awaiting selection from: " + selectingSet);
        Integer selectedId = (Integer) WaitMaster.waitForInput(
         WAIT_OPERATIONS.SELECT_BF_OBJ);
        // selecting = false;
        // cancelSelecting();
        return selectedId;
    }

    public void addAttachment(PassiveAbilityObj abil, Obj obj) {
        buffMaster.addAttachment(abil, obj);
    }


    public void unitDies(Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {
        unitDies(null, _killed, _killer, leaveCorpse, quietly);
    }

    public void unitDies(DC_ActiveObj activeObj, Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {
        deathMaster.unitDies(activeObj, _killed, _killer, leaveCorpse, quietly);
    }

    @Override
    public void buffCreated(BuffObj buff, Obj basis) {
        buffMaster.buffCreated(buff, basis);
    }

    public void copyBuff(BuffObj buff, Obj obj, Condition retainCondition) {
        buffMaster.copyBuff(buff, obj, retainCondition);
    }

    public void selectingStopped(boolean cancelled) {
        selectingSet.clear();
        if (cancelled) {
            try {
                setActivatingAction(null);
                WaitMaster.interrupt(WAIT_OPERATIONS.SELECT_BF_OBJ);
                LogMaster.log(1, "SELECTING CANCELLED!");
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
                return;
            }
        }
        refresh(false);
        setSelecting(false);
    }

    @Override
    public void checkForChanges(boolean after) {
        if (after) {
            deathMaster.checkForDeaths();
        } else {
            buffMaster.checkForDispels();
        }

    }

    public void activateMySpell(int index) {
        spellMaster.activateMySpell(index);
    }

    public void selectMyHero() {
        getGame().getPlayer(true).getHeroObj().invokeClicked();

    }

    public void previewMyAction(int index, ACTION_TYPE group) {
        if (ExplorationMaster.isExplorationOn())
            return;
        DC_UnitAction action = getActiveObj().getActionMap().get(group).get(index);
        GuiEventManager.trigger(ACTION_HOVERED, action);
    }

    public void activateMyAction(int index, ACTION_TYPE group) {
        getActiveObj().getActionMap().get(group).get(index).invokeClicked();
    }


    @Override
    public void win(Player winningPlayer) {
        if (winningPlayer.isMe()) {
        } else {

        }

    }

    @Override
    public boolean effectApplies(EffectImpl effect) {
        Ref ref = effect.getRef();
        if (!getGame().fireEvent(new
         Event(STANDARD_EVENT_TYPE.EFFECT_IS_BEING_APPLIED,
         ref))) {
            return false;
        }

        return game.getEffectManager().checkNotResisted(effect);

    }

    @Override
    public boolean endRound() {

        getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.ROUND_ENDS, state.getRound());
        state.setRound(state.getRound() + 1); // TODO why not on start?
        if (getGame().getBattleMaster().getOutcomeManager().checkTimedOutcome() != null) {
            getGame().getLogManager().doneLogEntryNode();
            return false;
        }
        getStateManager().endTurn();
        getGame().getLogManager().doneLogEntryNode();
        return true;
    }


    public Unit getControlledObj() {
        Unit unit = getActiveObj();
        if (unit == null) {
            unit = getMainHero();
        }
        return unit;
    }

    public Unit getActiveObj() {
        Unit active = null;;
        if (game.isStarted()) {
            active =   getGame().getLoop().getActiveUnit();
        }
        if (ExplorationMaster.isExplorationOn())
        if ( active==null || active.isAiControlled() ){
            return Eidolons.getMainHero();
        }
        return active;
    }

    @Override
    public MicroObj createSpell(ObjType type, MicroObj obj, Ref ref) {
        return spellMaster.createSpell(type, obj.getOwner(), ref);
    }


    @Override
    public MicroObj createUnit(ObjType type, int x, int y, Player owner) {
        return createUnit(type, x, y, owner, new Ref());
    }

    @Override
    public MicroObj createUnit(ObjType type, Coordinates c, Player owner) {
        return createUnit(type, c.x, c.y, owner);
    }

    @Override
    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        return objCreator.createUnit(type, x, y, owner, ref);
    }

    @Override
    public MicroObj createSpell(ObjType type, Player player, Ref ref) {
        return spellMaster.createSpell(type, player, ref);
    }


    public void applyActionRules(DC_ActiveObj action) {
        if (action != null) {
            for (ActionRule a : getGame().getRules().getActionRules()) {
                if (a.isAppliedOnExploreAction(action)) try {
                    a.actionComplete(action);
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }
        }

    }

    @Override
    public boolean handleEvent(Event event) {
        getGame().getMetaMaster().getEventHandler().handle(event);
        if (getGame().getDebugMaster() != null) {
            event.getRef().setDebug(getGame().getDebugMaster().isDebugFunctionRunning());
        }
        if (event.getRef().getSourceObj() != null) {
            if (!AnimMaster.isAnimationOffFor(event.getRef().getSourceObj(), null))
                if (AnimMaster.isPreconstructEventAnims()) if (AnimMaster.isOn()) {
                        try {
                            AnimConstructor.preconstruct(event);
                        } catch (Exception e) {
                            ExceptionMaster.printStackTrace(e);
                        }
                }
        } else {
            event.getRef().getSourceObj(); //TODO debug this
        }
        checkDefaultEventTriggers(event);
        boolean result = super.handleEvent(event);

        try {
            getGame().getBattleMaster().getStatManager().eventBeingHandled(event);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return result;
    }

    private void checkDefaultEventTriggers(Event event) {
        if (HpBar.isResetOnLogicThread())
            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_ENTERED_COMBAT ||

             event.getType().name().startsWith("PARAM_MODIFIED")
              && GuiEventManager.isParamEventAlwaysFired(event.getType().getArg())) {

                try {
                    DungeonScreen.getInstance().getGridPanel().getGridManager().
                     checkHpBarReset(event.getRef().getSourceObj());
                } catch (NullPointerException e) {
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }
    }


    @Override
    protected void checkEventIsGuiHandled(Event event) {
        if (GuiEventManager.checkEventIsGuiHandled(event))
            GuiEventManager.trigger(INGAME_EVENT_TRIGGERED, event);
        else {
            if (FloatingTextMaster.getInstance().isEventDisplayable(event)) {
                GuiEventManager.trigger(INGAME_EVENT_TRIGGERED, event);
            } else if (EventAnimCreator.isEventAnimated(event))
                GuiEventManager.trigger(INGAME_EVENT_TRIGGERED, event);
        }
    }

    public void freezeUnit(Unit unit) {
        unit.addStatus(UnitEnums.STATUS.IMMOBILE.toString());
        unit.setParam(PARAMS.C_N_OF_ACTIONS, 0);

    }

    @Override
    public DC_GameState getState() {
        return (DC_GameState) super.getState();
    }

    @Override
    public DC_StateManager getStateManager() {
        return (DC_StateManager) super.getStateManager();
    }

    @Override
    public DC_GameObjMaster getGameObjMaster() {
        return (DC_GameObjMaster) super.getGameObjMaster();
    }

    public BuffMaster getBuffMaster() {
        return buffMaster;
    }

    public EffectMaster getEffectMaster() {
        return effectMaster;
    }

    public SpellMaster getSpellMaster() {
        return spellMaster;
    }

    public DeathMaster getDeathMaster() {
        return deathMaster;
    }

    public ObjCreator getObjCreator() {
        return objCreator;
    }

    public Unit getMainHero() {
        return Eidolons.getMainHero();
    }

    public void atbTimeElapsed(Float time) {
        getGame().getRules().timePassed(time);
        buffMaster.atbTimeElapsed(time);


    }


    public boolean checkAutoCameraCenter() {
        if (AI_Manager.isRunning()) {
            return false;
        }
        return true;
    }

    public void setHighlightedObj(BattleFieldObject highlightedObj) {
        this.highlightedObj = highlightedObj;
    }

    public BattleFieldObject getHighlightedObj() {
        return highlightedObj;
    }
}
