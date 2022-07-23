package logic.core.game.handlers;

import logic.core.Aphos;
import logic.core.game.Game;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.combat.CombatLogic;
import main.system.auxiliary.RandomWizard;

public class AiHandler extends GameHandler{
    public AiHandler(Game game) {
        super(game);
    }

    public enum Intent{
        atk_hero,
        atk_core,
        atk_front,

    }

    public void act(Entity entity) {
        Intent intent= RandomWizard.getRandomFrom(Intent.atk_core, Intent.atk_hero, Intent.atk_front);
        Entity target= Aphos.hero;
        switch (intent) {

            case atk_hero -> {
                if (!attack(target, entity))
                    unitWaits(entity);
            }
            case atk_core -> {
                attackCore(entity);
            }
            case atk_front -> {
                if (!attack(target, entity))
                    attackCore(entity);

            }
        }

    }

    private void unitWaits(Entity entity) {
        game.getController().getAtbLogic().waits(entity);
    }

    private void attackCore(Entity entity) {
        CombatLogic.ATK_TYPE atkType= CombatLogic.ATK_TYPE.Standard;
        game.getCoreHandler().attacked(entity, atkType);
        game.getController().getAtbLogic().attackAction(entity, atkType);
    }

    private boolean attack(Entity target, Entity entity) {
        if (!game.getController().getCombatLogic().canAttack(entity,   target))
        return false;

        CombatLogic.ATK_TYPE atkType= CombatLogic.ATK_TYPE.Standard;
        game.getController().getCombatLogic().attack(entity, target, atkType);
        game.getController().getAtbLogic().attackAction(entity, atkType);

        return true;
    }
}
