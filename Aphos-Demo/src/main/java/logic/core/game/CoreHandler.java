package logic.core.game;

import content.LOG;
import logic.content.AUnitEnums;
import logic.core.game.handlers.GameHandler;
import logic.entity.Entity;
import logic.functions.combat.CombatLogic;

public class CoreHandler extends GameHandler {

    private int coreHp = 100;
    private int coreArmor = 5;

    public CoreHandler(Game game) {
        super(game);
    }

    public void attacked(Entity entity, CombatLogic.ATK_TYPE atkType) {
        int damage = entity.getInt(AUnitEnums.DAMAGE);
        switch (atkType) {
            case Power -> damage = damage * 3 / 2;
            case Quick -> damage = damage / 3 * 2;
        }
        damage -= coreArmor;
        if (damage <= 0) {
            LOG.log("Core resisted damage ", damage);
            return;
        }
        coreHp -= damage;
        if (coreHp <= 0)
            game.getRoundHandler().setGameOver(true);
        LOG.log("Core damaged by ", damage);
        LOG.log("Core hp left: ", coreHp);
        //callback to update a label?
    }
}
