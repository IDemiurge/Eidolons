package eidolons.game.core.state;

import com.google.inject.internal.util.ImmutableList;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.Spell;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.round.RoundRule;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.content.consts.Images;
import eidolons.system.text.DC_GameLogManager;
import main.ability.AbilityObj;
import main.ability.effects.Effect;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.elements.conditions.Condition;
import main.elements.conditions.standard.PositionCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.game.core.game.GameManager;
import main.game.core.state.StateManager;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.Rule;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 2/15/2017.
 */
public class DC_StateManager extends StateManager {

    private final OBJ_TYPE[] toBaseIgnoredTypes = {DC_TYPE.SPELLS, DC_TYPE.ACTIONS};
    private boolean savingOn;

    private final Lock resetLock = new ReentrantLock();
    private volatile boolean resetting = false;
    private Set<BattleFieldObject> objectsToReset;
    private Set<Unit> unitsToReset;

    public DC_StateManager(DC_GameState state) {
        this(state, false);
    }

    public DC_StateManager(DC_GameState state, boolean clone) {
        super(state);
    }

    public boolean isResetting() {
        return resetting;
    }

    @Override
    public void resetAllSynchronized() {
        resetAllSynchronized_(true);
    }

    public void resetAllSynchronized_(boolean recursion) {
        if (!recursion)
            if (Flags.isIDE()) {
                Core.tryIt(() -> resetAllSynchronized_(false));
                return;
            }
        if (!resetting) {
            // resetLock.lock(); //TODO core review - this is dangerous, why do we need this?
            if (!resetting) {
                if (!isSelectiveResetOn() || (ExplorationMaster.isExplorationOn() && !isSelectiveResetInExplore())) {
                    objectsToReset = new LinkedHashSet<>(getGame().getBfObjects());
                    unitsToReset = new LinkedHashSet<>(getGame().getUnits());
                } else {
                    objectsToReset = new LinkedHashSet<>();
                    unitsToReset = new LinkedHashSet<>();
                    for (BattleFieldObject obj : isResetUnitsOnly()? getGame().getUnits(): getGame().getBfObjects()) {

                        if ((ExplorationMaster.isExplorationOn() && obj.isOutsideCombat()) ||
                                getGame().getVisionMaster().getVisionRule().
                                        isResetRequiredSafe(Core.getMainHero(), obj)
                                || isAlwaysReset(obj)

                        ) {
                            if (obj instanceof Unit) {
                                unitsToReset.add((Unit) obj);
                            }
                            objectsToReset.add(obj); //for illumination too ?


                        }
                    }
                }

                log(1, objectsToReset.size() + " objects To Reset   ");

                log(1, unitsToReset.size() + " Units to reset = " +
                        unitsToReset);

                resetAll();
                resetting = false;
            }
            // resetLock.unlock();
        }
    }

    //TODO Core Review - too extreme? we do need to reset structs somehow
    private boolean isResetUnitsOnly() {
        return true;
    }

    private boolean isAlwaysReset(BattleFieldObject obj) {
        return obj.isPlayerCharacter();
    }

    private boolean isSelectiveResetOn() {
        return true;
    }

    private boolean isSelectiveResetInExplore() {
        return true;
    }

    @Override
    protected void makeSnapshotsOfUnitStates() {
        //       TODO
    }

    private void resetAll() {
        Ref ref = new Ref(game);
        ActiveObj active = null;
        if (getGame().getLoop().getLastAction() != null) {
            if (getGame().getLoop().getLastAction() == null) {
                ref.setObj(KEYS.ACTIVE, active = getGame().getLoop().getLastActionEvent());
            } else {
                getGame().getLoop().setLastActionEvent(active = getGame().getLoop().getLastAction());
                getGame().getLoop().setLastAction(null);
            }
        }

        game.fireEvent(new Event(STANDARD_EVENT_TYPE.RESET_STARTS, ref));

        getGame().getDroppedItemManager().reset();

        //TODO Explo revamp
        if (getGame().isStarted() && ExplorationMaster.isExplorationOn()) {
            // we will need full reset: after traps or other spec. effects; for Cells/Illumination
            // getGame().getDungeonMaster().getExplorationMaster().getResetter().resetAll();
            // if (getGame().getDungeonMaster().getExplorationMaster().
            //         getResetter().isResetNotRequired()) {
            //     objectsToReset.forEach(obj -> obj.setBufferedCoordinates(obj.getCoordinates()));
            //     triggerOnResetGuiEvents();
            //     return;
            // } else
            //     getGame().getDungeonMaster().getExplorationMaster().getResetter().setResetNotRequired(true);
        }
        getGame().getRules().getBuffRules().forEach(
                buffRule -> buffRule.clearCache());

        super.resetAllSynchronized();
        if (getGame().isStarted()) {
            checkCellBuffs();
        }
        if (!CoreEngine.isGraphicsOff())
            triggerOnResetGuiEvents();

        if (active != null) {
            if (active instanceof Spell) {
                Spell s = (Spell) active;
                if (s.isChanneling())
                    if (!s.isChannelingNow()) {
                        for (AbilityObj passive : s.getPassives()) {
                            passive.activatedOn(ref);
                        }
                        ref.setObj(KEYS.ACTIVE, active);
                        game.fireEvent(new Event(STANDARD_EVENT_TYPE.CHANNELING_DONE, ref));
                    }
            }
        }
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.RESET_DONE, ref));
    }


    private void triggerOnResetGuiEvents() {
        //TODO DC cleanup - why walls and overlaying? this is some kind of a hack..
        // GuiEventManager.trigger(GuiEventType.HP_BAR_UPDATE_MANY, objectsToReset.stream().filter(obj -> {
        //     if (!VisionHelper.checkVisible(obj))
        //         return true;
        //     if ((obj).isWall())
        //         return true;
        //     return (obj).isOverlaying();
        // }).collect(Collectors.toList()));
    }

    /**
     * @param unit
     */
    public void reset(Unit unit) {
        unit.toBase();
        //TODO refactor to unity
        checkCounterRules(unit);
        applyEffects(Effect.ZERO_LAYER, unit);

        unit.resetObjects();
        unit.resetRawValues();
        if (!getGame().isSimulation())
            applyDifficulty(unit);
        applyEffects(Effect.BASE_LAYER, unit);
        unit.afterEffects();
        applyEffects(Effect.SECOND_LAYER, unit);
        applyEffects(Effect.BUFF_RULE, unit);
        checkContinuousRules(unit);
        unit.afterBuffRuleEffects();
        unit.resetCurrentValues();
        unit.resetPercentages();
    }

    protected void applyDifficulty() {
        if (!getGame().isSimulation())
            unitsToReset.forEach(this::applyDifficulty);
    }

    private void applyDifficulty(Unit unit) {
        getGame().getMissionMaster().getOptionManager().applyDifficulty(unit);
    }


    public void checkContinuousRules() {
        for (Unit unit : unitsToReset) {
            checkContinuousRules(unit);
        }
    }

    private void checkContinuousRules(Unit unit) {
        getGame().getRules().applyContinuousRules(unit);
    }

    public void checkCounterRules() {
        for (Unit unit : unitsToReset) {
            checkCounterRules(unit);
        }
    }

    private void checkCounterRules(Unit unit) {
        if (getGame().getRules().getCounterRules() != null) {
            for (DC_CounterRule rule : getGame().getRules().getCounterRules()) {
                // rule.newTurn();
                try {
                    rule.check((unit));
                } catch (Exception e) {

                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

    public void applyEndOfTurnDamage() {
        if (getGame().getRules().getDamageRules() != null) {
            for (Unit unit : unitsToReset) {
                for (DamageCounterRule rule : getGame().getRules().getDamageRules()) {
                    rule.apply(unit);
                }
            }
        }

    }

    public void allToBase() {
        for (Obj obj : state.getObjMap().values()) {
            if (Arrays.asList(toBaseIgnoredTypes).contains(obj.getOBJ_TYPE_ENUM())) {
                continue;
            }

            if (checkObjIgnoresToBase(obj)) {
                if (obj instanceof DC_Obj)
                    ((DC_Obj) obj).outsideCombatReset();
                continue;
            }
            obj.toBase();
        }

    }

    @Override
    public boolean checkObjIgnoresToBase(Obj obj) {
        if (obj.isDead())
            return true;
        return obj.isOutsideCombat();
    }

    protected void resetCurrentValues() {
        for (Unit unit : unitsToReset) {
            if (!checkUnitIgnoresReset(unit))
                unit.resetCurrentValues();
        }
    }

    public void applyEffects(int layer) {
        applyEffects(layer, objectsToReset);
    }

    public void afterEffects() {
        for (BattleFieldObject obj : objectsToReset) {
            if (!checkUnitIgnoresReset(obj)) {
                obj.afterEffects();

            }
        }
    }

    public void applyEndOfTurnRules() {

        for (RoundRule rule : getGame().getRules().getRoundRules()) {
            try {
                rule.newTurn();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        for (DC_CounterRule rule : getGame().getRules().getCounterRules()) {
            try {
                rule.newTurn();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    public void resetUnitObjects() {
        for (Unit unit : unitsToReset) {
            if (!checkUnitIgnoresReset(unit)) {
                unit.resetObjects();
            }
        }
    }

    @Override
    public void checkRules(Event e) {
        DequeImpl<DC_RuleImpl> triggerRules = getGame().getRules().getTriggerRules();
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

    public void resetRawValues() {
        for (Unit unit : unitsToReset) {
            if (!unit.isDead()) {
                unit.resetRawValues();
            }
        }
    }


    protected void afterBuffRuleEffects() {
        for (Unit unit : unitsToReset) {
            if (!checkUnitIgnoresReset(unit))
                unit.afterBuffRuleEffects();
            // unit.removeProperty(G_PROPS.STD_BOOLS,
            //         UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE.getName());
        }
    }

    public boolean checkUnitIgnoresReset(BattleFieldObject obj) {
        //        if (obj.isDead())
        //            return true;
        ////        if (!ExplorationMaster.isExplorationOn())
        //            if (obj instanceof Unit)
        //                if (((Unit) obj).getAI().isOutsideCombat())
        //                    return true;


        return checkObjIgnoresToBase(obj);
    }

    private void checkCellBuffs() {
        for (BattleFieldObject unit : objectsToReset) {
            if (unit.isDead()) {
                continue;
            }
            Obj cell = game.getCell(unit.getCoordinates());
            if (cell == null) {
                continue;
            }
            if (cell.getBuffs() == null) {
                continue;
            }
            for (BuffObj buff : game.getCell(unit.getCoordinates()).getBuffs()) {
                if (unit.hasBuff(buff.getName())) {
                    continue;
                }
                if (buff.isAppliedThrough()) {
                    Condition retainCondition = new PositionCondition(KEYS.SOURCE.toString(), cell.getCoordinates());
                    getGame().getManager().copyBuff(buff, unit, retainCondition);

                }
            }
        }

    }

    public void endTurn() {
        try {
            if (game.isStarted()) {
                applyEndOfTurnRules();
                applyEndOfTurnDamage();
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        try {
            new Event(STANDARD_EVENT_TYPE.ROUND_ENDS, new Ref(getGame())).fire();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void newRound() {
        //        getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.NEW_ROUND, state.getRound());

        if (!ExplorationMaster.isExplorationOn()) {
            if (getState().getRound() > 0) {
                GuiEventManager.trigger(GuiEventType.SHOW_LARGE_TEXT,
                        ImmutableList.of("Round " + getState().getRoundDisplayedNumber(), "Fight on!", 3f));
                //really just some flavor text there? no useful info?
                // stats, reinf. status, total power remaining
            }
        }

        getGame().getLogManager().addImageToLog(Images.SEPARATOR_NARROW);

        game.getLogManager().log(
                DC_GameLogManager.ALIGN_CENTER +
                        "                                        [Round #" + (state.getRound() + 1) + "]"
        );
        newTurnTick();
        Ref ref = new Ref(getGame());
        ref.setAmount(state.getRound());
        boolean started = game.isStarted();
        getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.NEW_ROUND, ref));

        if (started) {
            getGameManager().reset();
            getGameManager().resetValues();
            //            IlluminationRule.applyLightEmission(getGame());
        } else {

            resetAllSynchronized();
            game.setStarted(true);
            if (!VisionMaster.isNewVision()) {
                getGame().getVisionMaster().getIllumination().resetIllumination(true);
            }
            //            getGameManager().refreshAll();
        }

        getState().increaseChaosLevel();
        game.getTurnManager().newRound();
        //        getGameManager().reset();

        if (!started)
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.GAME_STARTED, game));
        game.getLogManager().doneLogEntryNode();
        // if (!activePlayer.isMe())
    }

    private GameManager getGameManager() {
        return getGame().getManager();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    private void newTurnTick() {
        if (DC_Engine.isAtbMode())
            return;
        if (getState().getRound() > 0)
            for (Attachment attachment : state.getAttachments()) {
                attachment.tick();
            }
    }


    public void addObject(Obj obj) {
        if (obj == null) {
            return;
        }
        super.addObject(obj);
        getGame().getObjMaster().objAdded(obj);
    }

    public void removeObject(Integer id) {
        Obj obj = game.getObjectById(id);
        if (obj == null) {
            return;
        }
        Map<Integer, Obj> map = state.getObjMaps().get(obj.getOBJ_TYPE_ENUM());
        if (map != null) {
            map.remove(id);
        }
        if (obj instanceof BattleFieldObject) {
            if (obj instanceof Structure) {
                getGame().getStructures().remove(obj);
            }
            if (obj instanceof Unit) {
                getGame().getUnits().remove(obj);
                removeAttachedObjects((Unit) obj);
                if (unitsToReset != null) {
                    unitsToReset.remove(obj);
                }
            }
        }
        //        super.removeObject(id);
    }

    public void removeObject(Integer id, OBJ_TYPE TYPE) {
        removeObject(id);
    }

    private void removeAttachedObjects(BattleFieldObject unit) {
        for (Obj obj : state.getObjMap().values()) {
            if (obj.getRef() != null) {
                if (obj.getRef().getSource() != null) {
                    if (obj != unit) {
                        if (obj.getRef().getSource().equals(unit.getId())) {
                            removeObject(obj.getId());
                        }
                    }
                }
            }
        }

    }

    @Override
    public void clear() {
        state.getEffects().clear();
        state.getTriggers().clear();
        state.getObjects().clear();
        state.getEffects().clear();
    }
}
