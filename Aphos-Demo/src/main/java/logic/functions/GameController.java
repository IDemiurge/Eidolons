package logic.functions;

import logic.core.game.Game;
import logic.entity.Entity;
import logic.functions.atb.AtbLogic;
import logic.functions.combat.CombatLogic;
import logic.functions.combat.HeroMoveLogic;
import logic.functions.combat.UnitMoveLogic;
import main.system.GuiEventManager;
import content.AphosEvent;

import java.util.LinkedList;
import java.util.List;

public class GameController { //manager + handler architecture

    protected final List<LogicController> controllers;
    protected CombatLogic combatLogic;
    protected HeroMoveLogic heroMoveLogic;
    protected UnitMoveLogic unitMoveLogic;
    protected AtbLogic atbLogic;
    protected DeathLogic deathLogic;

    protected Game game;
    private Entity active;

    public GameController(Game game) {
        this.game = game;
        controllers = new LinkedList<>();
        controllers.add(combatLogic = new CombatLogic(this));
        controllers.add(heroMoveLogic = new HeroMoveLogic(this));
        controllers.add(unitMoveLogic = new UnitMoveLogic(this));
        controllers.add(atbLogic = new AtbLogic(this));
        controllers.add(deathLogic = new DeathLogic(this));
    }

    public void active(Entity entity) {
        setActive(entity);
        GuiEventManager.trigger(AphosEvent.ATB_ACTIVE, entity);
    }


    public List<LogicController> getControllers() {
        return controllers;
    }

    public HeroMoveLogic getHeroMoveLogic() {
        return heroMoveLogic;
    }

    public CombatLogic getCombatLogic() {
        return combatLogic;
    }

    public AtbLogic getAtbLogic() {
        return atbLogic;
    }

    public UnitMoveLogic getUnitMoveLogic() {
        return unitMoveLogic;
    }

    public DeathLogic getDeathLogic() {
        return deathLogic;
    }

    public void setActive(Entity active) {
        this.active = active;
    }
}
