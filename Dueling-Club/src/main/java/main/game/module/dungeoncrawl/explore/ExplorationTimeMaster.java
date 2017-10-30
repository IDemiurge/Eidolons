package main.game.module.dungeoncrawl.explore;

import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.mode.MODE;
import main.content.values.parameters.PARAMETER;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.rules.counter.DC_CounterRule;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationTimeMaster extends ExplorationHandler {
    public static final float secondsPerAP = 3f;
    private float time = 0;
    private float lastTimeChecked;
    private float round_delta = 0;
    private float ai_delta = 0;
    private float delta;
    private boolean guiDirtyFlag;

    public ExplorationTimeMaster(ExplorationMaster master) {
        super(master);
    }

    public void aiActionActivated(UnitAI ai, DC_ActiveObj activeObj) {
//        int time = getTimeForAction(activeObj);
//        ai.setExplorationTimePassed(ai.getExplorationTimePassed() + time);
        ai.setExplorationTimeOfLastAction(time);
    }


    public String getDisplayedTime() {
        return StringMaster.getFormattedTimeString(((int) time / 3600), 2)
         + ":" + StringMaster.getFormattedTimeString(((int) time / 60), 2)
         + ":" + StringMaster.getFormattedTimeString(((int) time % 60), 2);
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
    }

    public void checkTimedEvents() {
        delta = time - lastTimeChecked;
        round_delta += delta;
        ai_delta = +delta;
        master.getAiMaster().checkAiActs();
        processTimedEffects();
        //TODO queue this on gameloop?
    }

    private void processTimedEffects() {
        guiDirtyFlag = false;
        master.getAiMaster().getAlliesAndActiveUnitAIs(false).forEach(ai -> {
            if (ai.getUnit().getMode() != null) {
                processModeEffect(ai.getUnit(), ai.getUnit().getMode());
            } //modes could increase regen...
            //bleeding? blaze?
            processRegen(ai.getUnit());
        });

        if (round_delta >= getRoundEffectPeriod()) {
            round_delta -= getRoundEffectPeriod();
            processEndOfRoundEffects();
        }
        if (ai_delta >= getAiCheckPeriod()) {
            ai_delta -= getAiCheckPeriod();
            processAiChecks();
        }
        if (guiDirtyFlag)
        {
            GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
            guiDirtyFlag=false;
        }
    }

    private void processAiChecks() {
        master.getPartyMaster().timedCheck();
    }

    private float getAiCheckPeriod() {
        return 4;//* OptionsMaster.get
    }

    private float getRoundEffectPeriod() {
        return 10;//* OptionsMaster.get
    }

    private void processEndOfRoundEffects() {
        processCounterRules();
        processCustomRules();

    }

    private void processCustomRules() {

        master.getGame().getUnits().forEach(unit -> {
           if ( master.getGame().getRules().getUnconsciousRule().checkStatusUpdate(unit)){
               master.getGame().getRules().getUnconsciousRule().apply(unit);
               master.getGame().getStateManager().reset(unit);
           }
        });
    }

    private void processCounterRules() {
        master.getGame().getRules().getCounterRules().forEach(rule -> {
            rule.newTurn();
            master.getGame().getUnits().forEach(unit -> {
                if (checkCounterRuleApplies(unit, rule)) {
                    if (rule.checkApplies(unit)) {
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
        for (PARAMETER param : DC_ContentManager.REGEN_PARAMS) {
            int value = getParamRestoration(delta, param,
             unit.getParamFloat(ContentManager.getRegenParam(param)) / 5
            );
            if (value > 0) {
                unit.modifyParameter(ContentManager.getCurrentParam(param), value);
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
        PARAMETER param = (ContentManager.getPARAM(mode.getParameter()));
        PARAMETER base = ContentManager.getBaseParameterFromCurrent(param);
        int value = getParamRestoration(delta,
         base, 1);
        int max =
         base == PARAMS.FOCUS ? unit.getIntParam(PARAMS.STARTING_FOCUS)*3/2 :
          unit.getIntParam(base);

        if (base == PARAMS.FOCUS)
            max += unit.getIntParam(PARAMS.FOCUS_RETAINMENT)
             * max / 100;

        int min = 0;
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
        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
                case STAMINA:
                    return Math.round(modifier * delta / 2);
                case FOCUS:
                    return Math.round(modifier * delta / 3);
                case ESSENCE:
                    return Math.round(modifier * delta / 4);
            }

        }
        return 0;
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
}
