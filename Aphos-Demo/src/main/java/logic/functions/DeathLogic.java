package logic.functions;

import logic.entity.Entity;

public class DeathLogic extends LogicController{
    public DeathLogic(GameController controller) {
        super(controller);
    }

    public void killed(Entity entity, Entity source) {
//        if ()
        controller.getAtbLogic().remove(entity);
    }
}
