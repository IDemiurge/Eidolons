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
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.core.game.GameManager;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.generic.PartyManager;
import main.rules.counter.DC_CounterRule;
import main.rules.counter.DamageCounterRule;
import main.rules.mechanics.IlluminationRule;
import main.rules.round.RoundRule;
import main.system.config.ConfigMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.Arrays;
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
        if (clone) keeper = state.getGame().getState().getManager().getKeeper();
        else keeper = new StatesKeeper(getGame());
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
                    resetting = false;
                }
            } finally {
                resetLock.unlock();
            }
        }
    }

    private void resetAll() {

        super.resetAllSynchronized();
        if (getGame().isStarted()) {
            try {
                checkCellBuffs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (savingOn)
            keeper.save();
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
        unit.resetPercentages();
        unit.resetCurrentValues();
    }








    public void checkContinuousRules() {
        for (Unit unit : getGame().getUnits()) {
            checkContinuousRules (unit);
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
                rule.check((unit));
            }
        }
    }
    private void applyEndOfTurnDamage() {
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
            if (!obj.isDead()) {
                try {
                    obj.toBase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // TODO Auto-generated method stub

    }

    protected void resetCurrentValues() {
        for (Obj obj : getGame().getUnits()) {
            obj.resetCurrentValues();
        }
    }

    public void afterEffects() {
        for (Obj obj : getGame().getBfObjects()) {
            if (!obj.isDead()) {
                try {
                    obj.afterEffects();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (PartyManager.getParty() != null) {
            try {
                PartyManager.getParty().afterEffects();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Obj obj : PartyManager.getParties()) {
            try {
                obj.afterEffects();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void applyEndOfTurnRules() {

        for (RoundRule rule : getGame().getRules().getRoundRules()) {
            try {
                rule.newTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (DC_CounterRule rule : getGame().getRules().getCounterRules()) {
            try {
                rule.newTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void resetUnitObjects() {
        for (Unit unit : getGame().getUnits()) {
            if (!unit.isDead()) {
                unit.resetObjects();
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
            unit.afterBuffRuleEffects();
        }
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
        getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.ROUND_ENDS, state.getRound());
        state.setRound(state.getRound() + 1); // TODO why not on start?
        try {
            if (game.isStarted()) {
                applyEndOfTurnRules();
                applyEndOfTurnDamage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new Event(STANDARD_EVENT_TYPE.ROUND_ENDS, new Ref(getGame())).fire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGame().getLogManager().doneLogEntryNode();

    }

    public void newRound() {
        getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.NEW_ROUND, state.getRound());

        game.getLogManager().log("            >>>Round #" + (state.getRound() + 1) + "<<<");

        try {
            newTurnTick();
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.NEW_ROUND, game));
//            LogMaster.log(LogMaster.TRIGGER_DEBUG, triggers.toString());
//            LogMaster.log(LogMaster.EFFECT_DEBUG, effects.toString());

            if (getGame().getGameMode() == GAME_MODES.ARENA) {
                getGame().getArenaManager().newRound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (game.isStarted())
        {
            getGameManager().reset();
        }
        else {
            resetAllSynchronized();
        }
        if (game.isStarted()) {
            getGameManager().resetValues();
            IlluminationRule.initLightEmission(getGame());
            game.getTurnManager().newRound();
        } else {

            game.setStarted(true);
            try {
                IlluminationRule.initLightEmission(getGame());
            } catch (Exception e) {
                e.printStackTrace();
            }

            game.getTurnManager().newRound();
//            getGameManager().refreshAll();
        }
//        getGameManager().reset();
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


}
