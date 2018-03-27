package main.game.core.atb;

import main.content.PARAMS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.core.atb.AtbController.AtbUnit;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Stack;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbCalculator {
    AtbController controller;

    public AtbCalculator(AtbController controller) {
        this.controller = controller;
        GuiEventManager.bind(GuiEventType.ACTION_HOVERED, p->{
            int index = getIndexAfterAction((DC_ActiveObj) p.get());
            GuiEventManager.trigger(GuiEventType.ATB_POS_PREVIEW, index);
        });
    }
public class AtbPrecalcUnit extends AtbUnitImpl{

    private Float atbReadiness;

    public AtbPrecalcUnit(AtbController atbController, Unit unit) {
        super(atbController, unit);
    }

    @Override
    public void setAtbReadiness(float i) {
        atbReadiness=i;
    }

    @Override
    public float getAtbReadiness() {
        if (atbReadiness==null ){
            atbReadiness =(float) unit.getIntParam(PARAMS.C_INITIATIVE);
        }
        return atbReadiness;
    }

    @Override
    public void setTimeTillTurn(float i) {
        timeTillTurn = i;
    }
}
    public int getIndexAfterAction(DC_ActiveObj action){
        float cost = AtbMaster.getReadinessCost(action);
        AtbController clone = getClone();
        AtbUnit atbUnit = clone.getAtbUnit(action.getOwnerObj());
        atbUnit.setAtbReadiness(atbUnit.getAtbReadiness() - cost);
        clone.processAtbRelevantEvent();
        while(true){
            clone.step();
            break;
        }
        return clone.getUnits().indexOf(atbUnit);
    }

    public AtbController getClone(){
        return new FauxAtbController(controller, this);
    }

    public Stack<AtbUnit> cloneUnits(AtbController original) {
        Stack<AtbUnit> atbUnitStack = new Stack<>();
        for (AtbUnit sub : controller.getUnits()) {
            AtbUnit  clone = new AtbPrecalcUnit(original, sub.getUnit());
            clone.setTimeTillTurn(sub.getTimeTillTurn());
            clone.setAtbReadiness(sub.getAtbReadiness());
            atbUnitStack.add(clone);
        }
        return atbUnitStack;
    }
}
