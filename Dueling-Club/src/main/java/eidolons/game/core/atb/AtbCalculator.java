package eidolons.game.core.atb;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.atb.AtbController.AtbUnit;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbCalculator {
    AtbController controller;
    private AtbController clone;

    public AtbCalculator(AtbController controller) {
        this.controller = controller;
        GuiEventManager.bind(GuiEventType.ACTION_HOVERED, p -> {
            if ( ExplorationMaster.isExplorationOn())
                return;
            Stack<AtbUnit> units = controller.getUnits();
            int index = getIndexAfterAction((DC_ActiveObj) p.get());
            GuiEventManager.trigger(GuiEventType.
             ATB_POS_PREVIEW, index);
            List<Integer> list = new ArrayList<>();

            for (AtbUnit sub : units) {
                list.add(
                 clone.getAtbUnit(sub.getUnit()).getDisplayedAtbReadiness());
            }
            GuiEventManager.trigger(GuiEventType.PREVIEW_ATB_READINESS, list);
        });
    }

    public int getIndexAfterAction(DC_ActiveObj action) {
        float cost = AtbMaster.getReadinessCost(action);
        clone = getClone();
        AtbUnit atbUnit = clone.getAtbUnit(action.getOwnerObj());
        atbUnit.setAtbReadiness(atbUnit.getAtbReadiness() - cost);
        clone.processAtbRelevantEvent();
        while (true) {
            clone.step();
            break;
        }
        return clone.getUnits().indexOf(atbUnit);
    }

    public AtbController getClone() {
        return new FauxAtbController(controller, this);
    }

    public Stack<AtbUnit> cloneUnits(AtbController original) {
        Stack<AtbUnit> atbUnitStack = new Stack<>();
        for (AtbUnit sub : controller.getUnits()) {
            AtbUnit clone = new AtbPrecalcUnit(original, sub.getUnit());
            clone.setTimeTillTurn(sub.getTimeTillTurn());
            clone.setAtbReadiness(sub.getAtbReadiness());
            atbUnitStack.add(clone);
        }
        return atbUnitStack;
    }

    public class AtbPrecalcUnit extends AtbUnitImpl {

        private Float atbReadiness;

        public AtbPrecalcUnit(AtbController atbController, Unit unit) {
            super(atbController, unit);
        }

        @Override
        public float getAtbReadiness() {
            if (atbReadiness == null) {
                atbReadiness = (float) unit.getIntParam(PARAMS.C_INITIATIVE);
            }
            return atbReadiness;
        }

        @Override
        public void setAtbReadiness(float i) {
            atbReadiness = i;
        }

        @Override
        public void setTimeTillTurn(float i) {
            timeTillTurn = i;
        }
    }
}
