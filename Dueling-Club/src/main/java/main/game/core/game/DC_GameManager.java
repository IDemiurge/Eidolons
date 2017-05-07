package main.game.core.game;

import main.ability.PassiveAbilityObj;
import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.content.C_OBJ_TYPE;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.UnitEnums;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.*;
import main.entity.obj.unit.Unit;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.game.core.master.*;
import main.game.core.state.DC_GameState;
import main.game.core.state.DC_StateManager;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.bf.TargetRunnable;
import main.game.battlecraft.rules.action.ActionRule;
import main.game.battlecraft.rules.mechanics.IlluminationRule;
import main.swing.components.obj.drawing.DrawMasterStatic;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.Manager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.ColorManager;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Set;

import static main.system.GuiEventType.SELECT_MULTI_OBJECTS;

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

    public DC_GameManager(DC_GameState state, DC_Game game) {
        super(state, game);
        Manager.init(game, state, this);

        stateManager = new DC_StateManager(state);
        gameMaster = new DC_GameMaster(game);
        Eidolons.stateManager = getStateManager();
        Eidolons.gameMaster = getGameMaster();
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

    @Override
    public DC_Obj getInfoObj() {
        return (DC_Obj) super.getInfoObj();
    }

    public Unit getInfoUnit() {
        if (getInfoObj() instanceof Unit) {
            return (Unit) getInfoObj();
        }
        return null;
    }

    public boolean activeSelect(final Obj obj) {
        boolean result = true;
        for (ActionRule ar : getGame().getRules().getActionRules()) {
            try {
                result &= ar.unitBecomesActive((Unit) obj);
            } catch (Exception e) {
                e.printStackTrace();
                result = true;
            }
        }
        if (!result) {
            // WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
            return false;
        }

        setSelectedActiveObj(obj);
        try {
            getGame().getBattleField().selectActiveObj(obj, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if (VisionManager.c)
        // SoundMaster.playEffectSound(SOUNDS.WHAT, obj);

        ColorManager.setCurrentColor(ColorManager.getDarkerColor(ColorManager.getAltAspectColor(obj
         .getType()), 80));

        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTIVE_UNIT_SELECTED, getActiveObj());
        return true;
    }

    public void deselectActive() {
        if (selectedActiveObj != null) {
            getGame().getBattleField().deselectActiveObj(selectedActiveObj, true);
//            setSelectedActiveObj(null); TODO null activeObj is source of many bugs!
        }
    }

    public void deselectInfo() {
        if (infoObj != null) {
            getGame().getBattleField().deselectInfoObj(infoObj, true);

            setSelectedInfoObj(null);
        }
    }

    /**
     * @param action
     * @param result false if turn should end
     */
    public void unitActionCompleted(DC_ActiveObj action, Boolean result) {
        if (action != null) {
            getGame().getState().getUnitActionStack(action.getOwnerObj()).push(action);
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
            return;
        }
        getGameMaster().getUnitCache().clear();
        getStateManager().resetAllSynchronized();
        checkForChanges(true);

        resetWallMap();
        try {
            IlluminationRule.initLightEmission(getGame());
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawMasterStatic.getObjImageCache().clear();
        for (Unit u : getGame().getUnits()) {
            u.setOutlineType(null);
        }
        for (Obj u : getGame().getCells()) {
            ((DC_Obj) u).setOutlineType(null);
        }

        GuiEventManager.trigger(GuiEventType.UPDATE_GUI, null);
        GuiEventManager.trigger(GuiEventType.UPDATE_LIGHT, null);
        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, null);
        GuiEventManager.trigger(GuiEventType.UPDATE_EMITTERS, null);
    }

    public void resetWallMap() {
        getGame().getBattleFieldManager().resetWallMap();
    }

    @Override
    public void refreshGUI() {
        refresh(false);
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
                    e.printStackTrace();
                }
            }

            getGame().getBattleField().refresh();

        } catch (Exception e) {
            e.printStackTrace();
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

        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_TARGET_SELECTED);
        try {
            selectingStopped(false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_BF_OBJ, obj.getId());
        }

    }

    @Override
    public void infoSelect(Obj obj) {
        if (!CoreEngine.isSwingOn()) {
            return;
        }
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
        if (!(obj instanceof DC_Obj)) {
            return;
        }


        if (getInfoObj() != null) {
            getInfoObj().setInfoSelected(false);
        }

        super.infoSelect(obj);
        if (
         getGame().getBattleField() != null) {
            getGame().getBattleField().selectInfoObj(obj, true);
        }

        if (game.isDebugMode()) {
            try {
                getGame().getDebugMaster().getDebugPanel().refresh();
            } catch (Exception e) {

            }
        }
    }

    public void objClicked(Obj obj) {
        if (isSelecting()) {
            checkSelectedObj(obj);
            return;
        }
        infoSelect(obj);

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
        GuiEventManager.trigger(SELECT_MULTI_OBJECTS, new EventCallbackParam(p));

        for (Obj obj : new LinkedList<>(selectingSet)) {
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
            SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
            return null;
        }
        setSelecting(true);
        for (Obj obj : selectingSet) {
            DrawMasterStatic.getObjImageCache().remove(obj);
        }

        Integer id = selectAwait();
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
        deathMaster.unitDies(_killed, _killer, leaveCorpse, quietly);
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
                e.printStackTrace();
                return;
            }
        }
        refresh(false);
        setSelecting(false);
    }

    @Override
    public void setSelectedActiveObj(Obj selectedActiveObj) {
        super.setSelectedActiveObj(selectedActiveObj);
        // if (selectedActiveObj == null) {
        // getGame().getBattleField().highlightsOff();
        // }
        // getGame().getBattleField().highlightAvailableCells(selectedActiveObj);

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

    public void activateMyAction(int index, ACTION_TYPE group) {
        try {
            getActiveObj().getActionMap().get(group).get(index).invokeClicked();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }


    @Override
    public void win(Player winningPlayer) {
        if (winningPlayer.isMe()) {

            // getGame().getDialogueManager().victorySequence(); // load
            // dialogue
            // MessageManager.gameWon();
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
    public void endRound() {
        getGame().getRules().getTimeRule().reset();
        getStateManager().endTurn();
    }


    public Unit getActiveObj() {
        if (game.isStarted()) {
            if (selectedActiveObj == null) {
                //it's ticking madness!
//                return DC_PagedPriorityPanel.getClockUnit();
            }
        }
        return (Unit) selectedActiveObj;
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
                try {
                    a.actionComplete(action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean handleEvent(Event event) {
        if (getGame().getDebugMaster() != null) {
            event.getRef().setDebug(getGame().getDebugMaster().isDebugFunctionRunning());
        }

        return super.handleEvent(event);
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
    public DC_GameMaster getGameMaster() {
        return (DC_GameMaster) super.getGameMaster();
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
}
