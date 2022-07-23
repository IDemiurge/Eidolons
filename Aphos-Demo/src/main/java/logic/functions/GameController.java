package logic.functions;

import gdx.views.HeroView;
import logic.core.Aphos;
import logic.entity.Hero;
import logic.functions.atb.AtbLogic;
import logic.functions.combat.CombatLogic;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedList;
import java.util.List;

public class GameController { //manager + handler architecture

    private final List<LogicController> controllers;
    private static GameController instance;
    protected CombatLogic combatLogic;
    protected MoveLogic moveLogic;
    protected AtbLogic atbLogic;

    private Hero hero;
    private HeroView view;
    private int round;


    public GameController() {
        controllers = new LinkedList<>();
        controllers.add(combatLogic = new CombatLogic(this));
        controllers.add(moveLogic = new MoveLogic(this));
        controllers.add(atbLogic = new AtbLogic(this));
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void active(Hero hero) {
        setHero(hero);
    }
    public static void heroMove(int length, boolean direction) {
        //comes from GDX thread!
        GuiEventManager.triggerWithParams(GuiEventType.INPUT_MOVE, length, direction );
//        instance.getMoveLogic().move_(length, direction);
    }
    public void setHero(Hero hero) {
        this.hero = hero;
        controllers.forEach(c -> c.setHero(hero));
        Aphos.hero = hero;
    }
    public Hero getHero() {
        return hero;
    }

    public void setHeroView(HeroView view) {
        this.view = view;
        controllers.forEach(c -> c.setView(view));
    }

    public List<LogicController> getControllers() {
        return controllers;
    }

    public MoveLogic getMoveLogic() {
        return moveLogic;
    }

    public CombatLogic getCombatLogic() {
        return combatLogic;
    }

    public AtbLogic getAtbLogic() {
        return atbLogic;
    }
}
