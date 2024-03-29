package eidolons.game.core.atb;

import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import main.system.GuiEventManager;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.text.Log;
import main.system.text.LogManager;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static main.system.GuiEventType.INITIATIVE_CHANGED;
import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbUnitImpl implements AtbUnit {
    protected AtbController atbController;
    protected Unit unit;
    protected float timeTillTurn;

    public AtbUnitImpl(AtbController atbController, Unit unit) {
        this.atbController = atbController;
        this.unit = unit;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public float getInitialInitiative() {
        float mod =0.25f+RandomWizard.getRandomFloat()*
                (0.5f+ new Float(unit.getIntParam(PARAMS.ATB_START_MOD)) / 100);
        Float preset = new Float( unit.getIntParam(PARAMS.ATB_START_PRESET)) / 100;
        if (preset!=0){
            mod = Math.min(1, preset);
        }
        return  AtbController.ATB_TO_READY * (mod);
//        return   (mod)
//                * AtbController.TIME_LOGIC_MODIFIER / AtbController.TIME_TO_READY ;
//        return  AtbController.TIME_TO_READY * (mod)
//                / AtbController.TIME_LOGIC_MODIFIER * AtbController.TIME_TO_READY ;
    }

    @Override
    public float getAtbReadiness() {

        return NumberUtils.getFloat(unit.getParam(PARAMS.C_ATB))
                /AtbController.TIME_LOGIC_MODIFIER;
    }

    @Override
    public void setAtbReadiness(float i) {

        if (i >  AtbController.ATB_TO_READY) {  //1.01f *

            if (Log.check(Log.LOG_CASE.atb))
                log(1, " Bad ATB status:" +
             getUnit().getName() + " has " +
             i + " readiness value");
            i = AtbController.ATB_TO_READY;
        }

        double value = (i) * AtbController.TIME_LOGIC_MODIFIER;

        atbController.getManager().getGame().getLogManager().
         log(LogManager.LOGGING_DETAIL_LEVEL.FULL,
         getUnit().getName() + " has " +
          (getDisplayedAtbReadiness()) + "%" + " readiness");

        if (unit.getIntParam(PARAMS.C_ATB) == value)
            return;
        unit.setParam(PARAMS.C_ATB, value + "");
        triggerQueueEvent();
    }

    @Override
    public boolean isImmobilized() {
        if (getInitiative()<=0) {
            return false;
        }
        return  unit.checkModeDisablesActions();
    }

    @Override
    public float getInitiative() {
        return new Float(unit.getParamDouble(PARAMS.INITIATIVE));
    }

    @Override
    public float getTimeTillTurn() {
        return timeTillTurn;
    }

    @Override
    public void setTimeTillTurn(float i) {
        if (timeTillTurn != i) {
            timeTillTurn = i;

            if (i > AtbController.ATB_TO_READY || i < 0) {

                if (Log.check(Log.LOG_CASE.atb))
                    log(1, " Bad setTimeTillTurn:" +
                 getUnit().getName() + " to " + i);
            } else {
                // main.system.auxiliary.log.LogMaster.log(1,
                //  getUnit().getName() + " setTimeTillTurn to " +
                //   i + " sec ");
            }

            triggerQueueEvent();
        }
    }

    @Override
    public int getDisplayedAtbReadiness() {
        return Math.round(getAtbReadiness()/ AtbController.ATB_TO_READY * 100 );
    }


    private void triggerQueueEvent() {
        GuiEventManager.trigger(
         INITIATIVE_CHANGED,
         new ImmutablePair<>(getUnit(), new ImmutablePair<>(
          getDisplayedAtbReadiness(),
          getTimeTillTurn()))
        );
    }
}
