package main.game.logic.generic;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ActionType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;

public interface ActionManager {

    ActiveObj newAction(ActionType type, Ref ref, Player owner, MicroGame game);

    ActiveObj activateCounterAttack(ActiveObj action, Obj _countering);

    ActiveObj getAction(String abilTypeName, Entity entity);

    ActiveObj newAction(String abilTypeName, Entity entity);

    boolean activateAttackOfOpportunity(ActiveObj action, Obj src, boolean free);

    void resetActions(Entity entity);

    void resetCostsInNewThread();
}
