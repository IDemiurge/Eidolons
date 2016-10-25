package main.game;

import main.client.battle.arcade.PartyManager;
import main.client.dc.Launcher;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.elements.conditions.Condition;
import main.elements.conditions.standard.PositionCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.obj.specific.BuffObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game.GAME_MODES;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.turn.TurnTimer;
import main.rules.counter.DC_CounterRule;
import main.rules.counter.DamageCounterRule;
import main.rules.generic.RoundRule;
import main.rules.mechanics.IlluminationRule;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * h
 *
 * @author JustMe
 */
public class DC_GameState extends MicroGameState {
    private TurnTimer timer = new TurnTimer(this);
    private OBJ_TYPE[] ignoredTypes = {OBJ_TYPES.SPELLS, OBJ_TYPES.ACTIONS};
    private DequeImpl<DamageCounterRule> damageRules;
    private DequeImpl<DC_CounterRule> counterRules;
    private int timeRemaining;
    private Map<DC_HeroObj, Stack<DC_ActiveObj>> unitActionStack;

    public DC_GameState(MicroGame game) {
        super(game);
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += getGame().getUnits().size() + "UNITS: " + getGame().getUnits() + "\n";

        return string;
    }

    @Override
    protected void applyMods() {
        for (DC_HeroObj obj : getGame().getUnits()) {
            obj.afterBuffRuleEffects();
        }
    }

    @Override
    public void gameStarted(boolean first) {
        if (first)
            this.setRound(DEFAULT_ROUND);
        else
            this.setRound(DEFAULT_ROUND - 1);

    }

    @Override
    public void newRound() {
        getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.NEW_ROUND, getRound());

        game.getLogManager().log("            >>>Round #" + (getRound() + 1) + "<<<");// get
        // 10-round
        // "><"'s!
        try {
            newTurnTick();
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.NEW_ROUND, game));
            main.system.auxiliary.LogMaster.log(LogMaster.TRIGGER_DEBUG, triggers.toString());
            main.system.auxiliary.LogMaster.log(LogMaster.EFFECT_DEBUG, effects.toString());

            if (getGame().getGameMode() == GAME_MODES.ARENA)
                getGame().getArenaManager().newRound();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resetAll();
        if (game.isStarted()) {
            mngr.resetValues();
            IlluminationRule.initLightEmission(getGame());
            game.getTurnManager().makeTurn();
        } else {
            if (getGame().isGuiBroken()) {
                try {
                    // DialogMaster.inform("Battlefield failed to construct...");
                    DialogMaster.error("Failed to initialize interface!");
                    if (Launcher.getMainManager() != null)
                        Launcher.getMainManager().exitToMainMenu();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            game.setStarted(true);
            try {
                IlluminationRule.initLightEmission(getGame());
            } catch (Exception e) {
                e.printStackTrace();
            }
            game.getTurnManager().makeTurn();
            try {
                getManager().refreshAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getManager().reset();
        game.getLogManager().doneLogEntryNode();
        // if (!activePlayer.isMe())

    }

    private void newTurnTick() {
        for (Attachment attachment : attachments) {
            attachment.tick();
        }
        for (Obj obj : objMaps.get(OBJ_TYPES.ACTIONS).values()) {
            ((DC_ActiveObj) obj).tick();
        }
        for (Obj obj : objMaps.get(OBJ_TYPES.SPELLS).values()) {
            ((DC_SpellObj) obj).tick();
        }
    }

    public void checkContinuousRules() {
        for (DC_HeroObj unit : getGame().getUnits())
            getGame().getRules().applyContinuousRules(unit);

    }

    @Override
    public void resetAll() {
        Chronos.mark("RESET_ALL");

        super.resetAll();
        if (game.isStarted())
            try {
                checkCellBuffs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        Chronos.logTimeElapsedForMark("RESET_ALL");

    }

    private void checkCellBuffs() {
        for (DC_UnitObj unit : getGame().getUnits()) {
            if (unit.isDead())
                continue;
            Obj cell = game.getCellByCoordinate(unit.getCoordinates());
            if (cell == null)
                continue;
            if (cell.getBuffs() == null)
                continue;
            for (BuffObj buff : game.getCellByCoordinate(unit.getCoordinates()).getBuffs()) {
                if (unit.hasBuff(buff.getName()))
                    continue;
                if (buff.isAppliedThrough()) {
                    Condition retainCondition = new PositionCondition(KEYS.SOURCE.toString(), cell);
                    getGame().getManager().copyBuff(buff, unit, retainCondition);

                }
            }
        }

    }

    @Override
    public void endTurn() {
        getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.ROUND_ENDS, getRound());
        setRound(getRound() + 1); // TODO why not on start?
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
        newRound();

    }

    private void applyEndOfTurnRules() {

        for (RoundRule rule : getGame().getRules().getRoundRules())
            try {
                rule.newTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }

        for (DC_CounterRule rule : getCounterRules()) {
            try {
                rule.newTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void checkCounterRules() {
        if (getCounterRules() != null)
            for (DC_HeroObj unit : getGame().getUnits()) {
                for (DC_CounterRule rule : getCounterRules()) {
                    // rule.newTurn();
                    rule.check((unit));
                }
            }
    }

    private void applyEndOfTurnDamage() {
        if (getDamageRules() != null)
            for (DC_HeroObj unit : getGame().getUnits()) {
                for (DamageCounterRule rule : getDamageRules())
                    rule.apply(unit);
            }

    }

    public void resetUnitObjects() {
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (!unit.isDead())
                unit.resetObjects();
        }
    }

    public void resetRawValues() {
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (!unit.isDead())
                unit.resetRawValues();
        }
    }

    public TurnTimer getTimer() {
        return timer;
    }

    @Override
    public void allToBase() {
        for (Obj obj : objMap.values()) {
            if (Arrays.asList(ignoredTypes).contains(obj.getOBJ_TYPE_ENUM()))
                continue;
            if (!obj.isDead())
                try {
                    obj.toBase();
                } catch (Exception e) {
                    e.printStackTrace();
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
        for (Obj obj : getGame().getUnits()) {
            if (!obj.isDead())
                try {
                    obj.afterEffects();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        if (PartyManager.getParty() != null)
            try {
                PartyManager.getParty().afterEffects();
            } catch (Exception e) {
                e.printStackTrace();
            }
        for (Obj obj : PartyManager.getParties()) {
            try {
                obj.afterEffects();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public void addObject(Obj obj) {
        if (obj == null)
            return;
        super.addObject(obj);
        if (obj instanceof DC_HeroObj) {
            getGame().checkAddUnit((DC_HeroObj) obj);
        }
    }

    @Override
    public void removeObject(Integer id) {
        Obj obj = getObjectById(id);
        if (obj == null)
            return;
        if (obj instanceof DC_HeroObj) {
            getGame().getUnits().remove((DC_HeroObj) obj);

            removeAttachedObjects((DC_HeroObj) obj);

        }
        Map<Integer, Obj> map = getObjMaps().get(obj.getOBJ_TYPE_ENUM());
        if (map != null)
            map.remove(id);
        super.removeObject(id);

    }

    private void removeAttachedObjects(DC_HeroObj unit) {
        for (Obj obj : getObjMap().values()) {
            if (obj.getRef() != null)
                if (obj.getRef().getSource() != null)
                    if (obj != unit)
                        if (obj.getRef().getSource().equals(unit.getId()))
                            removeObject(obj.getId());
        }

    }

    public DequeImpl<DamageCounterRule> getDamageRules() {
        if (damageRules == null)
            damageRules = new DequeImpl<>();

        return damageRules;
    }

    public void setDamageRules(DequeImpl<DamageCounterRule> damageRules) {
        this.damageRules = damageRules;
    }

    public DequeImpl<DC_CounterRule> getCounterRules() {
        return counterRules;
    }

    public void setCounterRules(DequeImpl<DC_CounterRule> counterRules) {
        this.counterRules = counterRules;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public Map<DC_HeroObj, Stack<DC_ActiveObj>> getUnitActionStack() {
        if (unitActionStack == null)
            unitActionStack = new HashMap<DC_HeroObj, Stack<DC_ActiveObj>>();
        return unitActionStack;
    }

    public Stack<DC_ActiveObj> getUnitActionStack(DC_HeroObj ownerObj) {
        Stack<DC_ActiveObj> stack = getUnitActionStack().get(ownerObj);
        if (stack == null) {
            stack = new Stack<DC_ActiveObj>();
            getUnitActionStack().put(ownerObj, stack);
        }
        return stack;
    }

}
