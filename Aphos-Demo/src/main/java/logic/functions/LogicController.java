package logic.functions;

import gdx.views.HeroView;
import logic.entity.Hero;
import logic.functions.atb.AtbLogic;
import logic.functions.combat.CombatLogic;

public class LogicController {
    protected final GameController controller;
    protected Hero hero;
    protected HeroView view;

    public LogicController(GameController controller) {
        this.controller = controller;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setView(HeroView view) {
        this.view = view;
    }

    protected void inputError(String s) {
        System.out.println("ERROR: " + s);
    }

    public Hero getHero() {
        return controller.getHero();
    }

    public MoveLogic getMoveLogic() {
        return controller.getMoveLogic();
    }

    public CombatLogic getCombatLogic() {
        return controller.getCombatLogic();
    }

    public AtbLogic getAtbLogic() {
        return controller.getAtbLogic();
    }
}
