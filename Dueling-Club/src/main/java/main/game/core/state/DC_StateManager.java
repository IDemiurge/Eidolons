package main.game.core.state;

import main.ability.effects.Effect;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.elements.conditions.Condition;
import main.elements.conditions.standard.PositionCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.*;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.battlecraft.rules.DC_RuleImpl;
import main.game.battlecraft.rules.counter.DC_CounterRule;
import main.game.battlecraft.rules.counter.DamageCounterRule;
import main.game.battlecraft.rules.round.RoundRule;
import main.game.core.game.DC_Game;
import main.game.core.game.GameManager;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.Rule;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.config.ConfigMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JustMe on 2/15/2017.
 */
public class DC_StateManager extends StateManager {

    private StatesKeeper keeper;
    private OBJ_TYPE[] toBaseIgnoredTypes = {DC_TYPE.SPELLS, DC_TYPE.ACTIONS};
    private boolean savingOn = ConfigMaster.getInstance()
     .getBoolean("SAVING_ON_DEFAULT");

    private Lock resetLock = new ReentrantLock();
    private volatile boolean resetting = false;

    public DC_StateManager(DC_GameState state) {
        this(state, false);
    }

    public DC_StateManager(DC_GameState state, boolean clone) {
        super(state);
        if (clone) {
            keeper = state.getGame().getState().getManager().getKeeper();
        } else {
            keeper = new StatesKeeper(getGame());
        }
    }

    public StatesKeeper getKeeper() {
        return keeper;
    }

    @Override
    public void resetAllSynchronized() {
        if (!resetting) {
            try {
                resetLock.lock();
                if (!resetting) {
                    resetAll();
                    if (DC_Engine.isAtbMode()) {
//                        getGame().getTurnManager().getAtbController().processAtbRelevantEvent();
                    }
                    resetting = false;
                }
            } finally {
                resetLock.unlock();
            }
        }
    }

    @Override
    protected void makeSnapshotsOfUnitStates() {
//       TODO
    }

    private void resetAll() {
        if (getGame().getDungeonMaster().getExplorationMaster() != null) {
            getGame().getDungeonMaster().getExplorationMaster()
             .getCrawler().checkStatusUpdate();
        }
        if (getGame().isStarted() && ExplorationMaster.isExplorationOn()) {
            // we will need full reset: after traps or other spec. effects; for Cells/Illumination


            getGame().getDungeonMaster().getExplorationMaster().getResetter().resetAll();
            if (getGame().getDungeonMaster().getExplorationMaster().getResetter().isFirstResetDone()) {
                getGame().getBfObjects().forEach(obj -> obj.setBufferedCoordinates(obj.getCoordinates()));
                triggerOnResetGuiEvents();
                return;
            } else getGame().getDungeonMaster().getExplorationMaster().getResetter().setFirstResetDone(true);

        }
        super.resetAllSynchronized();
        if (getGame().isStarted()) {
            try {
                checkCellBuffs();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        triggerOnResetGuiEvents();

        if (savingOn) {
            keeper.save();
        }
    }


    private void triggerOnResetGuiEvents() {
        List<BattleFieldObject> list = new ArrayList<>(getGame().getBfObjects());
        list.removeIf(obj -> {
            if (!VisionManager.checkVisible(obj))
                return true;
            if ((obj).isWall())
                return true;
            return (obj).isOverlaying();
        });
        GuiEventManager.trigger(GuiEventType.HP_BAR_UPDATE_MANY, list);
    }

    public void reset(Unit unit) {
        unit.toBase();
        checkCounterRules(unit);
        applyEffects(Effect.ZERO_LAYER, unit);
        unit.resetObjects();
        unit.resetRawValues();
        applyEffects(Effect.BASE_LAYER, unit);
        unit.afterEffects();
        applyEffects(Effect.SECOND_LAYER, unit);
        applyEffects(Effect.BUFF_RULE, unit);
        checkContinuousRules(unit);
        unit.afterBuffRuleEffects();
        unit.resetCurrentValues();
        unit.resetPercentages();
    }


    public void checkContinuousRules() {
        for (Unit unit : getGame().getUnits()) {
            checkContinuousRules(unit);
        }
    }

    private void checkContinuousRules(Unit unit) {
        getGame().getRules().applyContinuousRules(unit);
    }

    public void checkCounterRules() {
        for (Unit unit : getGame().getUnits()) {
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
            for (Unit unit : getGame().getUnits()) {
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
        for (Unit unit : getGame().getUnits()) {
            if (!checkUnitIgnoresReset(unit))
                unit.resetCurrentValues();
        }
    }

    public void afterEffects() {
        for (BattleFieldObject obj : getGame().getBfObjects()) {
            if (!checkUnitIgnoresReset(obj)) {
                obj.afterEffects();

            }
        }
        if (PartyHelper.getParty() != null) {
            PartyHelper.getParty().afterEffects();
        }
        for (Obj obj : PartyHelper.getParties()) {
            obj.afterEffects();
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
        for (Unit unit : getGame().getUnits()) {
            if (!checkUnitIgnoresReset(unit))
                unit.resetObjects();
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
        for (Unit unit : getGame().getUnits()) {
            if (!unit.isDead()) {
                unit.resetRawValues();
            }
        }
    }


    protected void afterBuffRuleEffects() {
        for (Unit unit : getGame().getUnits()) {
            if (!checkUnitIgnoresReset(unit))
                unit.afterBuffRuleEffects();
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
        for (BattleFieldObject unit : getGame().getBfObjects()) {
            if (unit.isDead()) {
                continue;
            }
            Obj cell = game.getCellByCoordinate(unit.getCoordinates());
            if (cell == null) {
                continue;
            }
            if (cell.getBuffs() == null) {
                continue;
            }
            for (BuffObj buff : game.getCellByCoordinate(unit.getCoordinates()).getBuffs()) {
                if (unit.hasBuff(buff.getName())) {
                    continue;
                }
                if (buff.isAppliedThrough()) {
                    Condition retainCondition = new PositionCondition(KEYS.SOURCE.toString(), cell);
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

        game.getLogManager().log("            >>>Round #" + (state.getRound() + 1) + "<<<"
        );
        main.system.auxiliary.log.LogMaster.log(1, "Units= " +
         getGame().getUnits());
        newTurnTick();
        Ref ref = new Ref(getGame());
        ref.setAmount(state.getRound());
        boolean started = game.isStarted();
        getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.NEW_ROUND, ref));

        if (started) {
            getGameManager().reset();
            getGameManager().resetValues();
//            IlluminationRule.applyLightEmission(getGame());
            game.getTurnManager().newRound();
        } else {

            resetAllSynchronized();
            game.setStarted(true);
            getGame().getRules().getIlluminationRule().resetIllumination();
            getGame().getRules().getIlluminationRule().applyLightEmission();

            game.getTurnManager().newRound();
//            getGameManager().refreshAll();
        }
//        getGameManager().reset();

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
        for (Obj obj : state.getObjMaps().get(DC_TYPE.ACTIONS).values()) {
            ((DC_ActiveObj) obj).tick();
        }
        for (Obj obj : state.getObjMaps().get(DC_TYPE.SPELLS).values()) {
            ((DC_SpellObj) obj).tick();
        }
    }


    public void addObject(Obj obj) {
        if (obj == null) {
            return;
        }
        super.addObject(obj);
        getGame().getMaster().checkAddUnit(obj);

    }

    public void removeObject(Integer id) {
        Obj obj = game.getObjectById(id);
        if (obj == null) {
            return;
        }
        if (obj instanceof BattleFieldObject) {
            if (obj instanceof Structure) {
                getGame().getStructures().remove(obj);
            }
            if (obj instanceof Unit) {
                getGame().getUnits().remove(obj);
            }

            removeAttachedObjects((Unit) obj);

        }
        Map<Integer, Obj> map = state.getObjMaps().get(obj.getOBJ_TYPE_ENUM());
        if (map != null) {
            map.remove(id);
        }
//        super.removeObject(id);

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
