package logic.functions.atb;

import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.LogicController;
import logic.functions.combat.CombatLogic;
import main.system.auxiliary.NumberUtils;
import main.system.threading.WaitMaster;

import static logic.content.consts.CombatConsts.*;

public class AtbLogic extends LogicController {
    private AtbLoop loop;

    public AtbLogic(GameController controller) {
        super(controller);
        loop = new AtbLoop();
    }

    public void atbMove(Hero hero, int length, boolean sideJump) {
        if (sideJump)
            atbAction(hero, 75);
        else {
            if (length==2)
                atbAction(hero, 40);
            if (length==1)
                atbAction(hero, 25);
        }
    }

    public void attackAction(Entity source, CombatLogic.ATK_TYPE type) {
        switch (type) {
            case Power -> atbAction(source, 100);
            case Standard -> atbAction(source, 75);
            case Quick -> atbAction(source, 50);
        }
    }
    public void waits(Entity source) {
        atbAction(source, 25);
    }

    public void atbAction(Entity source, int cost) {
        source.modVal(AUnitEnums.ATB, -cost);
        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ACTION_COMPLETE, source);
    }

    public AtbLoop getLoop() {
        return loop;
    }

    public void remove(Entity entity) {
        loop.removeEntity(entity);
    }

}
