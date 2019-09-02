package eidolons.game.core.atb;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.system.GuiEventManager;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.NumberUtils;
import main.system.text.LogManager;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static main.system.GuiEventType.INITIATIVE_CHANGED;

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
        float mod =RandomWizard.getRandomFloat()*
                (0.25f+ new Float(unit.getIntParam(PARAMS.ATB_START_MOD)) / 100);
        Float preset = new Float( unit.getIntParam(PARAMS.ATB_START_PRESET)) / 100;
        if (preset!=0){
            mod = Math.min(1, preset);
        }
        return  AtbController.TIME_TO_READY * (mod);
    }

    @Override
    public float getAtbReadiness() {

        return NumberUtils.getFloat(unit.getParam(PARAMS.C_INITIATIVE))
         /AtbController.TIME_LOGIC_MODIFIER;
    }

    @Override
    public void setAtbReadiness(float i) {

        if (i > 1.01f * AtbController.TIME_TO_READY) {
            main.system.auxiliary.log.LogMaster.log(1, " Bad ATB status:" +
             getUnit().getName() + " has " +
             i + " readiness value");
            i = AtbController.TIME_TO_READY;
        }

        double value = (i) * AtbController.TIME_LOGIC_MODIFIER;

        atbController.getManager().getGame().getLogManager().
         log(LogManager.LOGGING_DETAIL_LEVEL.FULL,
         getUnit().getName() + " has " +
          (getDisplayedAtbReadiness()) + "%" + " readiness");

        if (unit.getIntParam(PARAMS.C_INITIATIVE) == value)
            return;
        unit.setParam(PARAMS.C_INITIATIVE, value + "");
        triggerQueueEvent();
    }

    @Override
    public boolean isImmobilized() {
        return !unit.canActNow();
    }

    @Override
    public float getInitiative() {
        return new Float(unit.getParamDouble(PARAMS.N_OF_ACTIONS));
    }

    @Override
    public float getTimeTillTurn() {
        return timeTillTurn;
    }

    @Override
    public void setTimeTillTurn(float i) {
        if (timeTillTurn != i) {
            timeTillTurn = i;

            if (i > AtbController.TIME_TO_READY || i < 0) {
                main.system.auxiliary.log.LogMaster.log(1, " Bad setTimeTillTurn:" +
                 getUnit().getName() + " to " + i);
            } else {
                main.system.auxiliary.log.LogMaster.log(1,
                 getUnit().getName() + " setTimeTillTurn to " +
                  i + " sec ");
            }

            triggerQueueEvent();
        }
    }

    @Override
    public int getDisplayedAtbReadiness() {
        return Math.round(getAtbReadiness() * 100 / AtbController.TIME_TO_READY);
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
