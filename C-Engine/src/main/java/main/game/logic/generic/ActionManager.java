package main.game.logic.generic;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public interface ActionManager {

    ActiveObj newAction(ObjType type, Ref ref, Player owner, GenericGame game);

    ActiveObj activateCounterAttack(ActiveObj action, Obj _countering);

    ActiveObj getAction(String abilTypeName, Entity entity);

    ActiveObj newAction(String abilTypeName, Entity entity);

    boolean activateAttackOfOpportunity(ActiveObj action, Obj src, boolean free);

    void resetActions(Entity entity);

    void resetCostsInNewThread();
}
