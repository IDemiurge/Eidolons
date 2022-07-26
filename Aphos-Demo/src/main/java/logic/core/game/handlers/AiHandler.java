package logic.core.game.handlers;

import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.core.game.Game;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.combat.CombatLogic;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.Chronos;
import main.system.threading.WaitMaster;

public class AiHandler extends GameHandler{
    private static final long MIN_TIME = 100;

    public AiHandler(Game game) {
        super(game);
    }

    public enum Intent{
        atk_hero,
        atk_core,
        atk_front, explode,

    }

    public void act(Entity entity) {
        long millisSinceLast=
                Chronos.getTimeElapsedForMark("AI");
        int minWait = (int) (MIN_TIME - millisSinceLast);
        if (minWait>0)
            WaitMaster.WAIT(minWait);
        Chronos.mark("AI");
        Intent intent=getIntent(entity);

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
            case explode -> {
                explode(entity);
            }
        }

    }


    private Intent getIntent(Entity entity) {
        Object o = entity.getValueMap().get(AUnitEnums.TYPE);
        if (o instanceof AUnitEnums.UnitType) {
            switch (((AUnitEnums.UnitType) o)) {

                case Ranged -> {
                }
                case Caster -> {
                }
                case Explode -> {
                    return Intent.explode;
                }
            }
        }
        return RandomWizard.getRandomFrom(Intent.atk_core, Intent.atk_hero, Intent.atk_front);
    }

    private void unitWaits(Entity entity) {
        game.getController().getAtbLogic().waits(entity);
    }

    private void explode(Entity entity) {
        game.getController().getCombatLogic().explode((Unit) entity);
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
