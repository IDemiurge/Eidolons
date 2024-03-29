package main.game.logic.generic;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public interface ActionManager {

    IActiveObj newAction(ObjType type, Ref ref, Player owner, GenericGame game);

    IActiveObj findCounterAttack(IActiveObj action, Obj _countering);

    IActiveObj getAction(String abilTypeName, Entity entity);

    IActiveObj newAction(String abilTypeName, Entity entity);

    boolean activateAttackOfOpportunity(IActiveObj action, Obj src, boolean free);

    void resetActions(Entity entity);

}
