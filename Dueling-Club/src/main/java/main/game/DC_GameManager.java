package main.game;

import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.ability.effects.RemoveBuffEffect;
import main.ability.effects.continuous.ContinuousEffect;
import main.client.battle.arcade.PartyManager;
import main.content.CONTENT_CONSTS.*;
import main.content.*;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.obj.specific.BuffObj;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DamageMaster;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.dungeon.Entrance;
import main.game.player.Player;
import main.rules.action.ActionRule;
import main.rules.mechanics.IlluminationRule;
import main.rules.mechanics.UpkeepRule;
import main.swing.builders.DC_Builder;
import main.swing.components.obj.drawing.DrawMaster;
import main.swing.components.panels.page.DC_PagedPriorityPanel;
import main.system.auxiliary.*;
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;
import main.system.text.ToolTipMaster.SCREEN_POSITION;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.*;

/**
 * *
 *
 * @author JustMe
 */
public class DC_GameManager extends GameManager {

    private static final String CANCEL_SELECTING = "Cancel selecting?";
    private static final String DEFEND = "Defend";
    private static final PROPERTY SPELLBOOK = PROPS.SPELLBOOK;
    private static final PROPERTY VERBATIM = PROPS.VERBATIM_SPELLS;
    private static final PROPERTY MEMORIZED = PROPS.MEMORIZED_SPELLS;
    private static final RemoveBuffEffect removeBuffEffect = new RemoveBuffEffect(DEFEND);
    HashMap<MicroObj, Map<ObjType, MicroObj>> spellCache = new HashMap<>();
    private boolean selectingInterrupted = false;
    private DC_Builder bfBuilder;

    // public void setSceneViewer(SceneViewer sv) {
    // dc_getGame().getGUI().setSceneViewer(sv);
    //
    // }

    public DC_GameManager(MicroGameState state, DC_Game game) {
        super(state, game);
        Manager.init(game, state, this);

    }

    @Deprecated
    public static void removeDefendingStatus(DC_HeroObj obj) {
        // if not too heroic...
        if (obj.removeStatus(STATUS.DEFENDING))
            removeBuffEffect.apply(Ref.getSelfTargetingRefNew(obj));
    }

    public static boolean checkInterrupted(Ref ref) {

        if (ref.getObj(KEYS.ACTIVE) != null)
            return ((DC_ActiveObj) ref.getObj(KEYS.ACTIVE)).isInterrupted();

        if (ref.getObj(KEYS.SPELL) != null)
            return ((DC_ActiveObj) ref.getObj(KEYS.SPELL)).isInterrupted();

        return false;
    }

    public MicroGameState getState() {
        return (MicroGameState) state;
    }

    public DC_Game getGame() {
        return (DC_Game) game;
    }

    @Override
    public DC_Obj getInfoObj() {
        return (DC_Obj) super.getInfoObj();
    }

    public DC_HeroObj getInfoUnit() {
        if (getInfoObj() instanceof DC_HeroObj)
            return (DC_HeroObj) getInfoObj();
        return null;
    }

    public boolean activeSelect(final Obj obj) {
        boolean result = true;
        for (ActionRule ar : getGame().getActionRules()) {
            try {
                result &= ar.unitBecomesActive((DC_HeroObj) obj);
            } catch (Exception e) {
                e.printStackTrace();
                result = true;
            }
        }
        if (!result) {
            // WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
            return false;
        }

        setSelectedActiveObj(obj);
        getGame().getBattleField().selectActiveObj(obj, true);
        // if (VisionManager.c)
        // SoundMaster.playEffectSound(SOUNDS.WHAT, obj);

        ColorManager.setCurrentColor(ColorManager.getDarkerColor(ColorManager.getAltAspectColor(obj
                .getType()), 80));
        return true;
    }

    public void deselectActive() {
        if (selectedActiveObj != null) {
            getGame().getBattleField().deselectActiveObj(selectedActiveObj, true);
            setSelectedActiveObj(null);
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
        if (action != null)
            getGame().getState().getUnitActionStack(action.getOwnerObj()).push(action);
        // HOSTILE_ACTION
        // SpellMaster.getSpellLogic(getActivatingAction());
        // getGame().getPlayer(false).getAI().setSituation(SITUATION.ENGAGED) ;

        // try {
        // getGame().getAnimationManager().animateValuesModified(
        // action.getRef().getTargetObj());
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // getGame().getAnimationManager().clearModValues();
        //
        // }
        if (result == null)
            WaitMaster.interrupt(WAIT_OPERATIONS.TURN_CYCLE);
        else
            WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, result);
        // ++ TODO animate costs!

        // WaitMaster.waitForInput(WAIT_OPERATIONS.TURN_CYCLE);
    }

    @Override
    public void reset() {
        if (!game.isStarted())
            return;
        getGame().getUnitCache().clear();
        getState().resetAll();

        resetWallMap();
        try {
            IlluminationRule.initLightEmission(getGame());
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawMaster.getObjImageCache().clear();
        for (DC_HeroObj u : getGame().getUnits()) {
            u.setOutlineType(null);
        }
        for (Obj u : getGame().getCells()) {
            ((DC_Obj) u).setOutlineType(null);
        }
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
        if (game.isSimulation())
            return;
        try {
            if (visibility)
                try {
                    getGame().getVisionManager().refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            getGame().getBattleField().refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // checkForChanges();
    }

    public boolean isMyTurn() {
        return getState().isMyTurn();
    }

    public Player getActivePlayer() {
        return getState().getActivePlayer();
    }

    private void checkSelectedObj(Obj obj) {
        boolean selectionObj = selectingSet.contains(obj);

        if (!selectionObj) {
            if (C_OBJ_TYPE.BF_OBJ.equals(obj.getOBJ_TYPE_ENUM()))
                selectionObj = selectingSet.contains(getGame().getCellByCoordinate(
                        obj.getCoordinates()));
        }
        if (!selectionObj) {
            // if (MessageManager.confirm(CANCEL_SELECTING)) {
            ActiveObj activatingAction = getActivatingAction();
            if (activatingAction != null)
                activatingAction.playCancelSound();
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
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
        if (!(obj instanceof DC_Obj))
            return;

        if (getInfoObj() instanceof DC_Cell) {
            if (obj instanceof DC_HeroObj) {
                if (obj.isDead()) {
                    getBfBuilder().getCellInfoPanel().selected(obj);
                    return;
                    // TODO display in secondary infoPanel! (below)
                }
            } else if (obj instanceof DC_HeroItemObj) {
                // TODO
                getBfBuilder().getCellInfoPanel().selected(obj);
                return;
            }

        }
        if (getInfoObj() != null)
            getInfoObj().setInfoSelected(false);

        super.infoSelect(obj);
        getGame().getBattleField().selectInfoObj(obj, true);

        if (game.isDebugMode())
            try {
                getGame().getDebugMaster().getDebugPanel().refresh();
            } catch (Exception e) {

            }
    }

    public void objClicked(Obj obj) {
        if (isSelecting()) {
            checkSelectedObj(obj);
            return;
        }
        // if (!isMyTurn()) {
        // deselectInfo();
        // infoSelect(obj);
        // return;
        // }
        //
        // if (obj.getOwner() == getActivePlayer()) {
        // deselectActive();
        // activeSelect(obj);
        // return;
        // }
        // deselectInfo();
        infoSelect(obj);

    }

    // a single-method spell, Warp Time: take another turn...
    public void resetValues(Player owner) {
        for (Obj obj : getUnits()) {
            DC_HeroObj unit = null;
            if (obj instanceof DC_HeroObj)
                unit = (DC_HeroObj) obj;

            if (unit.getOwner() == owner)
                unit.newRound();
            unit.regen();
        }
    }

    @Override
    public void resetValues() {
        for (Obj obj : getUnits()) {
            DC_HeroObj unit = null;
            if (obj instanceof DC_HeroObj)
                unit = (DC_HeroObj) obj;
            unit.newRound();
        }
    }

    private Set<Obj> getUnits() {
        Set<Obj> set = new HashSet<>();

        for (OBJ_TYPES type : DC_ContentManager.getBF_TYPES()) {
            set.addAll(state.getObjMaps().get(type).values());
        }

        return set;
    }

    @Override
    public Integer select(Filter<Obj> filter, Ref ref) {
        if (ref.getActive() instanceof DC_ActiveObj)
            if (getGame().getToolTipMaster()
                    .isTargetingTooltipShown((DC_ActiveObj) ref.getActive()))
                getGame().getToolTipMaster().initTargetingTooltip((DC_ActiveObj) ref.getActive());
        selectingSet = filter.getObjects();
        return select(selectingSet);
    }

    @Override
    public Integer select(Set<Obj> selectingSet) {

        for (Obj obj : new LinkedList<>(selectingSet)) {
            if (obj instanceof DC_Obj) {
                DC_Obj unit = (DC_Obj) obj;
                if (getActiveObj() != null)
                    if (!getActiveObj().getName().equals(DC_PagedPriorityPanel.CLOCK_UNIT))
                        if (getActiveObj().getZ() != unit.getZ())
                            selectingSet.remove(unit);
            }
        }
        this.selectingSet = selectingSet;

        if (selectingSet.isEmpty()) {
            getGame().getToolTipMaster().addTooltip(SCREEN_POSITION.ACTIVE_UNIT_BOTTOM,
                    "No targets available!");
            SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
            return null;
        }
        selectingInterrupted = false;
        setSelecting(true);
        for (Obj obj : selectingSet)
            DrawMaster.getObjImageCache().remove(obj);
        try {
            highlightsOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            highlight(selectingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO highlight active!

        Integer id = selectAwait();

        return id;
    }

    @Override
    public void highlight(Set<Obj> set) {
        getGame().getBattleField().highlight(set);

    }

    @Override
    public void highlightsOff() {
        getGame().getBattleField().highlightsOff();
    }

    public Integer selectAwait() {

        // add Cancel button? add hotkey listener?
        Integer selectedId = (Integer) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECT_BF_OBJ);
        // selecting = false;
        // cancelSelecting();
        return selectedId;
    }

    private void selectingStopped(boolean cancelled) {
        selectingSet.clear();
        try {
            highlightsOff();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (cancelled) {
            try {
                setActivatingAction(null);
                WaitMaster.interrupt(WAIT_OPERATIONS.SELECT_BF_OBJ);
                main.system.auxiliary.LogMaster.log(1, "SELECTING CANCELLED!");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            selectingInterrupted = true;
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

    public void unitAnnihilated(Obj _killed, Obj _killer) {
        getGame().getGraveyardManager().removeCorpse(_killed);
//	TODO 	getGame().getDroppedItemManager().remove((DC_HeroObj) _killed, item);

    }

    public void unitDies(Obj _killed, Obj _killer, boolean leaveCorpse, boolean quietly) {
        DC_HeroObj killed = (DC_HeroObj) _killed;
        DC_HeroObj killer = (DC_HeroObj) _killer;
        Ref ref = Ref.getCopy(killed.getRef());
        ref.setSource(killer.getId());
        ref.setTarget(killed.getId());

        // List<Attachment> attachments = getState().getAttachmentsMap()
        // .get(killed);
        if (killed.getBuffs() != null)
            for (Attachment attach : killed.getBuffs()) {
                if (!attach.isRetainAfterDeath()) {
                    getState().getAttachmentsMap().get(killed).remove(attach);
                    attach.remove();

                }
            }
        if (!leaveCorpse) {
            // leave a *ghost*?
            // destroy items?
        } else {
            try {
                getGame().getDroppedItemManager().dropDead(killed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                getGame().getGraveyardManager().unitDies(killed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getGame().getUnits().remove(killed);

        // getGame().getBattleField().remove(killed); // TODO GRAVEYARD
        if (!quietly) {
            Ref REF = Ref.getCopy(killer.getRef());
            REF.setTarget(killed.getId());
            REF.setSource(killer.getId());

            getGame().getBattleManager().unitDies(killed);
            getGame().getRules().getMoraleKillingRule().unitDied(killed,
                    killer.getRef().getAnimationActive());
            LogEntryNode node = game.getLogManager().newLogEntryNode(ENTRY_TYPE.DEATH, killed);

            if (killer.getRef().getAnimationActive() != null) {
                ANIM animation = killer.getRef().getAnimationActive().getAnimation();
                if (animation != null) {
                    animation.addPhase(new AnimPhase(PHASE_TYPE.DEATH, killer, killed));
                    node.setLinkedAnimation(animation);
                }
            }

            SoundMaster.playEffectSound(SOUNDS.DEATH, killed);

            game.getLogManager().logDeath(killed, killer);
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED, REF));
            game.getLogManager().doneLogEntryNode();
        }

        // refreshAll();
    }

    public List<DC_SpellObj> getSpells(DC_HeroObj obj) {
        return getSpells(obj, false);
    }

    public List<DC_SpellObj> getSpells(DC_HeroObj obj, boolean reset) {
        if (obj == null)
            return new LinkedList<>();
        List<DC_SpellObj> spells = ((DC_HeroObj) obj).getSpells();
        if (spells != null && !reset)
            if (!spells.isEmpty())
                return spells;

        spells = new LinkedList<>(initSpellpool(obj, VERBATIM));
        spells.addAll(initSpellpool(obj, MEMORIZED));
        return spells;
    }

    private List<DC_SpellObj> getMySpells() {
        return ((DC_HeroObj) getGame().getPlayer(true).getHeroObj()).getSpells();
    }

    private LinkedList<DC_SpellObj> initSpellpool(MicroObj obj, PROPERTY PROP) {
        LinkedList<DC_SpellObj> spells = new LinkedList<DC_SpellObj>();
        String spellList = obj.getProperty(PROP);
        List<String> spellpool;

        spellpool = StringMaster.openContainer(spellList);

        for (String typeName : spellpool) {
            Ref ref = Ref.getCopy(obj.getRef());
            ObjType type = DataManager.getType(typeName, OBJ_TYPES.SPELLS);
            if (type == null)
                continue;
            Map<ObjType, MicroObj> cache = spellCache.get(obj);
            if (cache == null) {
                cache = new HashMap<>();
                spellCache.put(obj, cache);
            }
            MicroObj spell = cache.get(type);
            if (spell == null) {
                spell = getGame().createSpell(type, obj, ref);
                cache.put(type, spell);
            }

            SPELL_POOL spellPool = new EnumMaster<SPELL_POOL>().retrieveEnumConst(SPELL_POOL.class,
                    PROP.getName());
            if (spellPool != null)
                spell.setProperty(G_PROPS.SPELL_POOL, spellPool.toString());
            else
                main.system.auxiliary.LogMaster.log(1, PROP.getName()
                        + " spell pool not found for " + typeName);

            spells.add((DC_SpellObj) spell);
        }
        return spells;
    }

    @Override
    public void checkForChanges(boolean after) {
        if (after)
            checkForDeaths();
        else
            checkForDispels();

    }

    public void continuousEffectApplies(ContinuousEffect effect) {
        // new AddBuffEffect(buffTypeName, effect)
    }

    private void checkForDispels() {
        for (Attachment attachment : getState().getAttachments()) {
            attachment.checkRetainCondition();
        }

    }

    private void checkForDeaths() {
        for (Obj unit : getState().getObjMaps().get(OBJ_TYPES.UNITS).values()) {
            if (!unit.isDead())
                if (DamageMaster.checkDead((DC_HeroObj) unit))
                    unit.kill(unit, true, false);
        }
        for (Obj unit : getState().getObjMaps().get(OBJ_TYPES.CHARS).values()) {
            if (!unit.isDead())
                if (DamageMaster.checkDead((DC_HeroObj) unit))
                    unit.kill(unit, true, false);
        }
        for (Obj unit : getState().getObjMaps().get(OBJ_TYPES.BF_OBJ).values()) {
            if (!unit.isDead())
                if (DamageMaster.checkDead((DC_HeroObj) unit))
                    unit.kill(unit, true, false);
        }
    }

    public void activateMySpell(int index) {
        main.system.auxiliary.LogMaster.log(1, "spell hotkey pressed " + index);
        getMySpells().get(index).invokeClicked();
    }

    public void selectMyHero() {
        getGame().getPlayer(true).getHeroObj().invokeClicked();

    }

    public void activateMyAction(int index, ACTION_TYPE group) {
        try {
            ((DC_UnitAction) getActiveObj().getActionMap().get(group).get(index)).invokeClicked();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    public void activateSceneViewer(boolean fullscreen) {
        // dc_getGame().getGUI().showSceneViewer(fullscreen);
        // setSceneViewerMode(true);
        // stopTimer();
        // disableHotkeys();
        // blockBattleField();

    }

    // @Override
    // public List<? extends Active> getUnitActions(Obj obj) {
    // acts = actionsMap.get(obj);
    // if (acts != null)
    // return acts;
    //
    // String actions = obj.getProperty(PROPS.ACTIVES);
    // OBJ_TYPE obj_type = obj.getOBJ_TYPE_ENUM();
    //
    // acts = new LinkedList<ActionType>();
    //
    // List<ActionType> stdActions = DC_ActionManager
    // .getStandardActionTypes(obj_type);
    // for (ActionType stdActionType : stdActions) {
    // if (stdActionType == null)
    // continue;
    // stdActionType.setGame(game);
    // acts.add(stdActionType);
    // }
    // for (String name : actions.split(StringMaster.getSeparator())) {
    // ObjType type = DataManager.getType(name, OBJ_TYPES.ACTIONS);
    // if (type == null) {
    // main.system.auxiliary.LogMaster.log(0, "Action not found: "
    // + name);
    // continue;
    // }
    // type.setGame(game);
    // acts.add((ActionType) type);
    //
    // }
    // actionsMap.put(obj, acts);
    // return acts;
    // }

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
        // Ref ref = effect.getRef();
        // if (!getGame().fireEvent(new
        // Event(STANDARD_EVENT_TYPE.EFFECT_APPLIES,
        // ref))) {
        // return false;
        // }

        return game.getEffectManager().checkNotResisted(effect);

    }

    @Override
    public void endTurn() {
        getGame().getRules().getTimeRule().reset();
        getState().endTurn();
    }

    public DC_Builder getBfBuilder() {
        if (bfBuilder == null)
            bfBuilder = (DC_Builder) getGame().getBattleField().getBuilder();
        return bfBuilder;
    }

    public void setBfBuilder(DC_Builder bfBuilder) {
        this.bfBuilder = bfBuilder;
    }

    public boolean isAI_Turn() {
        if (getActiveObj() == null)
            return false;
        return getActiveObj().getOwner().isAi();
    }

    public DC_HeroObj getActiveObj() {
        if (game.isStarted())
            if (selectedActiveObj == null) {
                // endTurn();
                // SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
                return DC_PagedPriorityPanel.getClockUnit();

            }
        return (DC_HeroObj) selectedActiveObj;
    }

    public void killAllUnits(boolean retainPlayerParty) {
        killAllUnits(false, retainPlayerParty);
    }

    public void killAllUnits(boolean removeBfObjects, boolean retainPlayerParty) {
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (!removeBfObjects)
                if (unit.isBfObj()) {
                    // if (unit.getRef().getObj(KEYS.SUMMONER) == null)
                    continue;
                }
            if (retainPlayerParty)
                if (PartyManager.getParty() != null)
                    if (PartyManager.getParty().getMembers().contains(unit))
                        continue;
            killUnitQuietly(unit);
            getGame().remove(unit);
        }
        // reset();
        // refreshAll();
        // WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
    }

    public void killAll(boolean retainSelected) {
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (retainSelected) {
                if (unit.isActiveSelected())
                    continue;
                if (unit.getOwner().isMe())
                    if (getInfoObj().getOwner().isMe()) {
                        continue;
                    }
            }
            killUnitQuietly(unit);
        }
        reset();
        refreshAll();
        WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
    }

    public void killUnitQuietly(DC_HeroObj unit) {
        unit.kill(unit, false, true);

    }

    @Override
    public MicroObj createSpell(ObjType type, MicroObj obj, Ref ref) {
        return createSpell(type, obj.getOwner(), ref);
    }

    @Override
    // , boolean toSpellbook
    public MicroObj createSpell(ObjType type, Player player, Ref ref) {
        DC_SpellObj spell = new DC_SpellObj(type, player, getGame(), ref);
        // state.addObject(spell);
        return spell;
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
        if (!CoreEngine.isArcaneVault())
            if (!CoreEngine.isLevelEditor())
                if (!type.isGenerated()) {
                    type = new ObjType(type);
                    game.initType(type);
                }
        DC_HeroObj obj = null;
        if (type.checkProperty(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.ENTRANCE.toString()))
            obj = new Entrance(x, y, type, getGame().getDungeon(), null);
        else
            obj = new DC_HeroObj(type, x, y, owner, getGame(), ref);
        game.getState().addObject(obj);
        if (CoreEngine.isLevelEditor())
            return obj;
        try {
            obj.toBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            obj.resetObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            obj.afterEffects();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // state.addObject(obj);
        if (!game.isSimulation())
            if (owner != null)
                if (getGame().getBattleField() != null)
                    getGame().getBattleField().createObj(obj);
        return obj;

    }

    @Override
    public BuffObj createBuff(BuffType type, Obj active, Player player, Ref ref, Effect effect,
                              int duration, Condition retainCondition) {
        ref = Ref.getCopy(ref);
        if (type.getName().equals(BuffObj.DUMMY_BUFF_TYPE)) {
            try {
                String name = ref.getObj(KEYS.ACTIVE.name()).getName() + "'s buff";
                String img = ref.getObj(KEYS.ACTIVE.name()).getProperty(G_PROPS.IMAGE);
                type = new BuffType(type);
                type.setProperty(G_PROPS.NAME, name);
                type.setProperty(G_PROPS.IMAGE, img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Obj basis = game.getObjectById(ref.getBasis());
        if (basis == null)
            return null;
        DC_BuffObj buff = (DC_BuffObj) basis.getBuff(type.getName());
        if (buff != null) {
            if (!type.checkBool(STD_BOOLS.STACKING) && !active.checkBool(STD_BOOLS.STACKING)) {
                basis.removeBuff(type.getName());
                // TODO duration or do nothing
            } else {
                if (buff.isMaxStacks())
                    return buff;
                buff.modifyParameter(PARAMS.BUFF_STACKS, 1);
            }
        } else {
            // check cache
        }

        buff = new DC_BuffObj(type, player, getGame(), ref, effect, duration, retainCondition);
        buff.setActive(active);

        buff.applyEffect(); // be careful!

        buffCreated(buff, basis);
        if (type.checkBool(STD_BOOLS.APPLY_THRU) || active.checkBool(STD_BOOLS.APPLY_THRU)) {
            buff.setAppliedThrough(true);
            if (basis instanceof DC_HeroObj) {
                Ref REF = ref.getCopy();
                Obj cell = game.getCellByCoordinate(basis.getCoordinates());
                if (!cell.hasBuff(buff.getName())) {
                    REF.setBasis(cell.getId());
                    REF.setTarget(cell.getId());
                    // copy buff
                    Effect copy = effect.getCopy();
                    if (copy == null)
                        LogMaster.error("APPLY THRU ERROR: " + effect + " HAS NO CONSTRUCT");
                    else
                        createBuff(type, active, player, REF, copy, duration, retainCondition)
                                .setAppliedThrough(true);
                }
            }
        }
        return buff;
    }

    public boolean addBuff(Effect effect, String buffName) {
        Ref copy = effect.getRef().getCopy();

        if (copy.getTargetObj() == null)
            return false;

        copy.getTargetObj().getBuff(buffName);
        return true;
    }

    @Override
    public void buffCreated(BuffObj buff, Obj basis) {
        super.buffCreated(buff, basis);
        UpkeepRule.addUpkeep(buff);
    }

    public void copyBuff(BuffObj buff, Obj obj, Condition retainCondition) {
        Ref REF = buff.getRef().getCopy();
        REF.setBasis(obj.getId());
        REF.setTarget(obj.getId());
        createBuff(buff.getType(), buff.getActive(), buff.getOwner(), REF, buff.getEffect(), buff
                .getDuration(), Conditions.join(buff.getRetainConditions(), retainCondition));

    }

    public void applyActionRules(DC_ActiveObj action) {
        if (action != null)
            for (ActionRule a : getGame().getActionRules()) {
                try {
                    a.actionComplete(action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }

    public void freezeUnit(DC_HeroObj unit) {
        unit.addStatus(STATUS.IMMOBILE.toString());
        unit.setParam(PARAMS.C_N_OF_ACTIONS, 0);

    }

}
