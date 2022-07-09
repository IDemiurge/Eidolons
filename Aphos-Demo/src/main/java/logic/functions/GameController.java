package logic.functions;

import gdx.general.anims.ActionAnims;
import gdx.views.HeroView;
import logic.entity.Hero;
import logic.functions.combat.CombatController;
import logic.lane.HeroPos;

import java.util.LinkedList;
import java.util.List;

public class GameController { //manager + handler architecture

    private final List<LogicController> controllers;
    protected CombatController combatController;
    private static GameController instance;

    private Hero hero;
    private HeroView view;
    private int round;

    public void active(Hero hero){
        setHero(hero);
    }

    public GameController() {
        controllers = new LinkedList<>();
        controllers.add(combatController = new CombatController(this));
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void move_(int length, boolean direction) {

        HeroPos prev= hero.getPos();
        if (prev.isLeftSide())
            direction = !direction;
        int mod = direction ? length : -length;
        HeroPos pos = new HeroPos(prev.getCell() + mod, prev.isLeftSide()); //TODO
        hero.setPos(pos);
        boolean triggered=false;
//        HeroView view = FrontField.get().getView(hero);
        if (triggered){
//            GuiEventManager.trigger(GuiEventType. )
        } else {
            //direct call - but we can't do it all from logic thread, eh?
            ActionAnims.moveHero(view, prev, pos);
        }
    }

    public static void heroMove(int length, boolean direction) {
        //comes from GDX thread!
       instance.move_(length, direction);
    }

    public void setHero(Hero hero) {
        this.hero = hero;
        controllers.forEach(c-> c.setHero(hero));
    }

    public void setHeroView(HeroView view) {
        this.view = view;
        controllers.forEach(c-> c.setView(view));
    }

    public List<LogicController> getControllers() {
        return controllers;
    }

    public CombatController getCombatController() {
        return combatController;
    }

    public Hero getHero() {
        return hero;
    }
}
