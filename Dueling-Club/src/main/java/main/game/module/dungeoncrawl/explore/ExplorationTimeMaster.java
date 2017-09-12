package main.game.module.dungeoncrawl.explore;

import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.mode.MODE;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationTimeMaster extends ExplorationHandler {
    public static final float secondsPerAP = 2f;
    private float time = 0;

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
        master.getAiMaster().checkAiActs();
        processTimedEffects( );
    }

    private void processTimedEffects( ) {
        master.getAiMaster().getActiveUnitAIs().forEach(ai -> {
            if (ai.getUnit().getMode()!=null ){
                processModeEffect(  ai.getUnit(), ai.getUnit().getMode());
            } //modes could increase regen...
            //bleeding? blaze?
            processRegen(ai.getUnit());
        });
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
                unit.getAI().setExplorationTimeOfModeEffect(time);
            }
        }
    }

    private int getParamRestoration(float delta, PARAMETER param, float modifier) {
        if (param instanceof PARAMS) {
            switch ((PARAMS) param) {
                case STAMINA:
                   return  Math.round(modifier*delta /2);
                case FOCUS:
                    return    Math.round(delta /3);
                case ESSENCE:
                   return  Math.round(delta /4);
            }

            }
        return 0;
    }

    private void processModeEffect(  Unit unit, MODE mode) {
        float last = unit.getAI().getExplorationTimeOfModeEffect();
         float delta = time - last;
        if (delta<2)
            return ;
        int max=100;
        int min=0;
        PARAMETER param = ContentManager.getPARAM(mode.getParameter());
        int value=getParamRestoration(delta, param, 1);
        if (mode instanceof STD_MODES) {
            switch ((STD_MODES) mode) {
                case RESTING:
                    value = Math.round(delta /2);
                    max = unit.getIntParam(PARAMS.STAMINA);
                    break;
                case CONCENTRATION:
                    value = Math.round(delta /3);
                    max = unit.getIntParam(PARAMS.BASE_FOCUS);//*3/2;
//                    unit.getIntParam(PARAMS.FOCUS_RETAINMENT) ;
                    break;
                case MEDITATION:
                    value = Math.round(delta /4);
                    max = unit.getIntParam(PARAMS.ESSENCE);
                    break;

            }
            value = MathMaster.getMinMax(value, min, max);
            if (value>0){
                unit.modifyParameter(param, value, true);
                unit.getAI().setExplorationTimeOfModeEffect(time);
            }
        }
    }

    public float getTime() {
        return time;
    }
}
