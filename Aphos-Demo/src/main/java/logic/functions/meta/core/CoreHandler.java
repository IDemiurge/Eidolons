package logic.functions.meta.core;

import content.LOG;
import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.core.game.Game;
import logic.core.game.handlers.GameHandler;
import logic.entity.Entity;
import logic.functions.combat.CombatLogic;

public class CoreHandler extends GameHandler {

    Core core;

    public CoreHandler(Game game) {
        super(game);
        core = new Core();
        Aphos.core=core;
    }

    public void attacked(Entity entity, CombatLogic.ATK_TYPE atkType) {
        int damage = entity.getInt(AUnitEnums.DAMAGE);
        switch (atkType) {
            case Power -> damage = damage * 3 / 2;
            case Quick -> damage = damage / 3 * 2;
        }
        boolean result = core.dealDamage(damage);
        if (!result)
                game.getRoundHandler().setGameOver(true);

    }

    public void explodeDamage(int damage) {
        boolean result = core.dealDamage(damage);
        if (!result)
            game.getRoundHandler().setGameOver(true);
    }
}
