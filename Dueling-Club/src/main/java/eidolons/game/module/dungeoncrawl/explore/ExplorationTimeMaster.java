package eidolons.game.module.dungeoncrawl.explore;

import eidolons.ability.conditions.special.RestCondition;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehaviorManager;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.battlecraft.rules.round.RoundRule;
import eidolons.game.core.atb.AtbController;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.ContentValsManager;
import main.content.mode.MODE;
import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationTimeMaster extends ExplorationHandler {
    public static final float secondsPerAP = 5f;
    private static final float REGEN_MODIFIER = 0.2f;
    private float time = 0;
    private float lastTimeChecked;
    private float round_delta = 0;
    private float ai_delta = 0;
    private float delta;
    private boolean guiDirtyFlag;

    public ExplorationTimeMaster(ExplorationMaster master) {
        super(master);
        GuiEventManager.bind(GuiEventType.TIME_PASSED, t -> {
            time += (float) t.get();
        });
    }

    public void aiActionActivated(UnitAI ai, DC_ActiveObj activeObj) {
        //        int time = getTimeForAction(activeObj);
        //        ai.setExplorationTimePassed(ai.getExplorationTimePassed() + time);
        ai.setExplorationTimeOfLastAction(time);
    }


    public String getDisplayedTime() {
        return NumberUtils.getFormattedTimeString(((int) time / 3600), 2)
         + ":" + NumberUtils.getFormattedTimeString(((int) time / 60), 2)
         + ":" + NumberUtils.getFormattedTimeString(((int) time % 60), 2);
        //        return TimeMaster.getFormattedTime((long) time, true, false);
    }

    public int getTimeForAction(DC_ActiveObj activeObj) {
        //speed factor of the unit?
        return (int) Math.round(secondsPerAP * activeObj.getParamDouble(PARAMS.AP_COST));
    }

    public int getTimePassedSinceAiActions(UnitAI ai) {
        return (int) (time - ai.getExplorationTimeOfLastAction());
        //        return ai.getExplorationTimePassed();
    }

    public void act(float delta) {
        time += delta;
        master.act(delta);
    }

    public Boolean playerRests(float timeInSeconds) {
        return playerWaits(timeInSeconds, true);
    }

    public void playerWaits(float timeInSeconds) {
        playerWaits(timeInSeconds, false);
    }

    public Boolean playerWaits(float timeInSeconds, boolean rest) {
        if (wait(timeInSeconds, rest))
            return false;

        Boolean result = (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.WAIT_COMPLETE);
        return result;
    }

    private boolean wait(float timeInSeconds, boolean rest) {
        if (!ExplorationMaster.isExplorationOn())
            return false;
        if (!new RestCondition().preCheck(new Ref())) {
            return false;
        }
        new Thread(new Runnable() {
            public void run() {
                float wakeUpTime = time + timeInSeconds;
                float speedFactor = rest ? 25 : 10;
                int period = 100;
                float actPeriod = period * speedFactor / 1000;
                ExplorationMaster.setWaiting(true);
                Boolean result = true;
                try {
                    DungeonScreen.getInstance().setSpeed(speedFactor);
                    while (true) {
                        if (time >= wakeUpTime)
                            break;
                        WaitMaster.WAIT(period);
                        //                DungeonScreen.getInstance().render(actPeriod);
                        //            act(actPeriod );
                        //                checkTimedEvents();
                        if (!ExplorationMaster.isWaiting()) {
                            if (rest) {
                                ExplorationMaster.setWaiting(true);
                            } else {
                                //interrupted
                                break;
                            }
                        }
                        master.getAggroMaster().checkStatusUpdate();
                        if (!ExplorationMaster.isExplorationOn()) {
                            result = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                DungeonScreen.getInstance().setSpeed(null);
                ExplorationMaster.setWaiting(false);
                WaitMaster.receiveInput(WAIT_OPERATIONS.WAIT_COMPLETE, result);
            }
        }, " thread").start();
        return true;
    }

    public void checkTimedEvents() {
        delta = time - lastTimeChecked;
        if (delta == 0) return;
        lastTimeChecked = time;
        round_delta += delta;
        ai_delta = +delta;
        if (AiBehaviorManager.isNewAiOn()){
        boolean aiActs = false;
        try {
            aiActs = master.getAiMaster().getExploreAiManager().getBehaviorManager().update();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
            master.getAiMaster().setAiActs(aiActs);
            if (aiActs)
                master.getGame().getLoop().signal();
        }
        else
            master.getAiMaster().checkAiActs();
        processTimedEffects();
        //TODO queue this on gameloop?
    }

    private void processTimedEffects() {
        guiDirtyFlag = false;
        master.getAiMaster().getAlliesAndActiveUnitAIs(false).forEach(ai -> {
            if (ai.getUnit().getModeFinal() != null) {
                processModeEffect(ai.getUnit(), ai.getUnit().getModeFinal());
            } //modes could increase regen...
            //bleeding? blaze?
            processRegen(ai.getUnit());
        });

        if (round_delta >= getRoundEffectPeriod()) {
            //            round_delta -= getRoundEffectPeriod();
            round_delta = 0;
            processEndOfRoundEffects();
        }
        if (ai_delta >= getAiCheckPeriod()) {
            //            ai_delta -= getAiCheckPeriod();
            ai_delta = 0;
            processAiChecks();
        }
        if (guiDirtyFlag) {
            GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
            guiDirtyFlag = false;
        }
    }

    private void processAiChecks() {
        master.getPartyMaster().timedCheck();
    }

    private float getAiCheckPeriod() {
        return 4;//* OptionsMaster.get
    }

    private float getRoundEffectPeriod() {
        if (DC_Engine.isAtbMode()) {
            return 1;
        }
        return 10;
    }

    private void processEndOfRoundEffects() {
        //        processCounterRules();
        master.getGame().getStateManager().applyEndOfTurnRules();
        if (DC_Engine.isAtbMode()) {
            master.getGame().getRules().timePassed(getRoundEffectPeriod());
        } else {
            master.getGame().getStateManager().applyEndOfTurnDamage();
        }

        processCustomRules();

    }

    private void processCustomRules() {
        // List<RoundRule> list = new ArrayList<>();

        master.getGame().getUnits().forEach(unit -> {
            float delta = getRoundEffectPeriod() / AtbController.TIME_IN_ROUND;
            unit.getResetter().regenerateToughness(delta);
            for (RoundRule sub : master.getGame().getRules().getRoundRules()) {
                //                if (master.getGame().getRules().getUnconsciousRule().checkStatusUpdate(unit)) {
                if (sub.check(unit)) {
                    sub.apply(unit, delta);
                    if (sub == master.getGame().getRules().getUnconsciousRule())
                        if (!unit.isAnnihilated())
                            master.getGame().getStateManager().reset(unit);
                }
            }
        });
    }

    private void processCounterRules() {
        master.getGame().getRules().getDamageRules().forEach(rule -> {
            rule.newTurn();
        });
        master.getGame().getRules().getCounterRules().forEach(rule -> {
            rule.newTurn(); // ???
            master.getGame().getUnits().forEach(unit -> {
                if (checkCounterRuleApplies(unit, rule)) {
                    if (rule.checkApplies(unit)) {
                        //TODO reset unit?
                    }

                }
            });

        });
    }

    private boolean checkCounterRuleApplies(Unit unit, DC_CounterRule rule) {
        if (rule.getCounter() == null) {
            return false;
        }
        switch (rule.getCounter()) {
            case Bleeding:
            case Blaze:
            case Poison:
            case Disease:
                return true;
        }
        return false;
    }

    private void processRegen(Unit unit) {
        //TODO
        float last = unit.getAI().getExplorationTimeOfRegenEffects();
        float delta = time - last;
        for (PARAMETER param : DC_ContentValsManager.REGENERATED_PARAMS) {
            int value = getParamRestoration(delta, param,
             unit.getParamFloat(ContentValsManager.getRegenParam(param))
            );
            if (value > 0) {
                unit.modifyParameter(ContentValsManager.getCurrentParam(param), value,
                 unit.getIntParam(param), true);
                unit.getAI().setExplorationTimeOfRegenEffects(time);
            }
        }
    }
    //restore focus/morale

    private void processModeEffect(Unit unit, MODE mode) {
        if (mode.getParameter() == null)
            return;
        float last = unit.getAI().getExplorationTimeOfModeEffect();
        float delta = time - last;
        if (delta < 2)
            return;
        PARAMETER param = (ContentValsManager.getPARAM(mode.getParameter()));
        PARAMETER base = ContentValsManager.getBaseParameterFromCurrent(param);
        int value = getParamRestoration(delta,
         base, 1);
        int max =
         base == PARAMS.FOCUS ? unit.getIntParam(PARAMS.STARTING_FOCUS) * 3 / 2 :
          unit.getIntParam(base);

        if (base == PARAMS.FOCUS)
            max += unit.getIntParam(PARAMS.FOCUS_RETAINMENT)
             * max / 100;

        int min = 0;
        if (unit.getGame().isDebugMode())
            if (base != PARAMS.FOCUS) {
                min = unit.getIntParam(base);
            }
        //                    max = unit.getIntParam(PARAMS.BASE_FOCUS);//*3/2;
        ////                    unit.getIntParam(PARAMS.FOCUS_RETAINMENT) ;
        value = MathMaster.getMinMax(value, min, max);
        if (value > 0) {
            unit.modifyParameter(param, value, max, true);
            unit.resetPercentage(param);
            unit.getAI().setExplorationTimeOfModeEffect(time);
            guiDirtyFlag = true;
        }
    }

    private int getParamRestoration(float delta, PARAMETER param, float modifier) {
        return Math.round(modifier * delta * getRegenModifier() * getRegenModifier(param) + getRegenBonus(param));
    }

    private float getRegenModifier() {
        if (CoreEngine.isTestingMode())
            return REGEN_MODIFIER * 5;
        return REGEN_MODIFIER;
    }

    private float getRegenModifier(PARAMETER param) {
        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
                case STAMINA:
                    return 1f;
                case FOCUS:
                    return 1.5f;
                case ESSENCE:
                    return 1f;
            }
        }
        return 1f;
    }

    private float getRegenBonus(PARAMETER param) {
        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
                case STAMINA:
                    return 1f;
                case ESSENCE:
                    return 1f;
            }
        }
        return 0f;
    }

    public float getTime() {
        return time;
    }

    public void unitActivatesMode(Unit unit) {
        unit.getAI().setExplorationTimeOfModeEffect(time);
    }

    public void setGuiDirtyFlag(boolean guiDirtyFlag) {
        this.guiDirtyFlag = guiDirtyFlag;
    }

    public void playerWaits() {
        if (master.getGame().isPaused())
            return;
        int defaultWaitTime = OptionsMaster.getGameplayOptions().getIntValue(
         GAMEPLAY_OPTION.DEFAULT_WAIT_TIME);
        wait(defaultWaitTime, false);
    }
}
