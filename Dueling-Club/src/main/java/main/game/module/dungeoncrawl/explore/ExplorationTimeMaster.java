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
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationTimeMaster extends ExplorationHandler {
    public static final float secondsPerAP = 3f;
    private float time = 0;
    private float lastTimeChecked;
    private float delta;

    public ExplorationTimeMaster(ExplorationMaster master) {
        super(master);
    }

    public void aiActionActivated(UnitAI ai, DC_ActiveObj activeObj) {
//        int time = getTimeForAction(activeObj);
//        ai.setExplorationTimePassed(ai.getExplorationTimePassed() + time);
        ai.setExplorationTimeOfLastAction(time);
    }



    public String getDisplayedTime() {
        return StringMaster.getFormattedTimeString( ((int) time / 3600), 2)
         + ":"+StringMaster.getFormattedTimeString( ((int) time / 60), 2)
         + ":" + StringMaster.getFormattedTimeString( ((int) time % 60), 2);
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
        delta =time - lastTimeChecked;
        lastTimeChecked= time;
        master.getAiMaster().checkAiActs();
        processTimedEffects( );
        //TODO queue this on gameloop?
    }

    private void processTimedEffects( ) {
        master.getAiMaster().getAlliesAndActiveUnitAIs(false).forEach(ai -> {
            if (ai.getUnit().getMode()!=null ){
                processModeEffect(  ai.getUnit(), ai.getUnit().getMode());
            } //modes could increase regen...
            //bleeding? blaze?
            processRegen(ai.getUnit());
        });

        if (delta >= getRoundEffectPeriod()) {
            delta -=getRoundEffectPeriod();
            processEndOfRoundEffects();
        }
    }

    private float getRoundEffectPeriod() {
        return 10;//* OptionsMaster.get
    }

    private void processEndOfRoundEffects() {
        processCounterRules();

    }

    private void processCounterRules(   ) {
        master.getGame().getRules().getCounterRules().forEach(rule->{
            rule.newTurn();
            master.getGame().getUnits().forEach(unit -> {
                if (checkCounterRuleApplies(unit ,rule)){
                    if (rule.checkApplies(unit)){
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

    private void processRegen(  Unit unit ) {
        //TODO
        float last = unit.getAI().getExplorationTimeOfRegenEffects();
        float delta = time - last;
        for (PARAMETER param: DC_ContentManager.REGEN_PARAMS){
            int value = getParamRestoration(delta, param,
                unit.getParamFloat(ContentManager.getRegenParam(param))/5
            );
            if (value>0){
                unit.modifyParameter(param, value, true);
                unit.getAI().setExplorationTimeOfRegenEffects(time);
            }
        }
    }


    private void processModeEffect(  Unit unit, MODE mode) {
        if (mode.getParameter()==null )
            return ;
        float last = unit.getAI().getExplorationTimeOfModeEffect();
         float delta = time - last;
        if (delta<2)
            return ;
        int max=100;
        int min=0;
        PARAMETER param =ContentManager.getBaseParameterFromCurrent(ContentManager.getPARAM(mode.getParameter()));
        int value=getParamRestoration(delta, param, 1);
//                    max = unit.getIntParam(PARAMS.BASE_FOCUS);//*3/2;
////                    unit.getIntParam(PARAMS.FOCUS_RETAINMENT) ;
            value = MathMaster.getMinMax(value, min, max);
            if (value>0){
                unit.modifyParameter(param, value, true);
                unit.getAI().setExplorationTimeOfModeEffect(time);
            }
    }

    private int getParamRestoration(float delta, PARAMETER param, float modifier) {
        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
                case STAMINA:
                    return  Math.round(modifier*delta /2);
                case FOCUS:
                    return    Math.round(modifier*delta /3);
                case ESSENCE:
                    return  Math.round(modifier*delta /4);
            }

        }
        return 0;
    }
    public float getTime() {
        return time;
    }
}