package logic.functions;

import gdx.views.HeroView;
import logic.entity.Hero;

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
}
