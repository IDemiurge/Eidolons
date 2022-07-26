package logic.functions.atb;

import content.LOG;
import logic.content.AUnitEnums;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.functions.GameController;
import logic.functions.LogicController;
import logic.functions.combat.CombatLogic;
import main.system.threading.WaitMaster;

public class AtbLogic extends LogicController {
    private AtbLoop loop;

    public AtbLogic(GameController controller) {
        super(controller);
        loop = new AtbLoop();
    }

    public void atbMove(Hero hero, int length, boolean sideJump) {
        if (sideJump)
            atbAction("Side Jump", hero, 75);
        else {
            if (length==2)
                atbAction("Jump", hero, 40);
            if (length==1)
                atbAction("Step", hero, 25);
        }
    }

    public void attackAction(Entity source, CombatLogic.ATK_TYPE type) {
        switch (type) {
            case Power -> atbAction("Power Attack", source, 100);
            case Standard -> atbAction("Standard Attack", source, 75);
            case Quick -> atbAction("Quick Attack", source, 50);
        }
    }
    public void waits(Entity source) {
        atbAction("Wait", source, 25);
    }

    public void atbAction(String action, Entity source, int cost) {
        source.modVal(AUnitEnums.ATB, - (float) cost);
        LOG.log(source," does [", action, "] for ATB: ", cost);
        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ACTION_COMPLETE, source);
    }

    public AtbLoop getLoop() {
        return loop;
    }

    public void remove(Entity entity) {
        loop.removeEntity(entity);
    }

}
