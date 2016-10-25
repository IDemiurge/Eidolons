package main.entity;

import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ActionType;
import main.game.MicroGame;
import main.game.player.Player;

public interface ActionManager {

    ActiveObj newAction(ActionType type, Ref ref, Player owner, MicroGame game);

    ActiveObj activateCounterAttack(ActiveObj action, Obj _countering);

    ActiveObj getAction(String abilTypeName, Entity entity);

    ActiveObj newAction(String abilTypeName, Entity entity);

    boolean activateAttackOfOpportunity(ActiveObj action, Obj src, boolean free);

    void resetActions(Entity entity);

}
