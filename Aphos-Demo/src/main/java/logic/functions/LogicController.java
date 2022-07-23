package logic.functions;

import logic.core.game.Game;
import logic.functions.atb.AtbLogic;
import logic.functions.combat.CombatLogic;
import logic.functions.combat.HeroMoveLogic;

public class LogicController {
    protected final GameController controller;
    protected Game game;

    public LogicController(GameController controller) {
        this.controller = controller;
        this.game = controller.game;
    }

    protected void inputError(String s) {
        System.out.println("ERROR: " + s);
    }

    public HeroMoveLogic getMoveLogic() {
        return controller.getHeroMoveLogic();
    }

    public CombatLogic getCombatLogic() {
        return controller.getCombatLogic();
    }

    public AtbLogic getAtbLogic() {
        return controller.getAtbLogic();
    }
}
