package eidolons.entity.mngr.action;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.feat.spaces.FeatSpaceInitializer;
import eidolons.entity.feat.spaces.IFeatSpaceInitializer;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.ExtraAttacksRule;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.HIDDEN_ACTIONS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Active;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.generic.ActionManager;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

public class DC_ActionManager implements ActionManager {

    protected GenericGame game;
    protected static ArrayList<ObjType> hiddenActions;
    private IFeatSpaceInitializer spaceManager;
    ActionFactory factory;

    public DC_ActionManager(GenericGame game) {
        this.game = game;
        spaceManager = new FeatSpaceInitializer();
        factory= new ActionFactory(game);
    }
    public void clearCache() {
        factory.clearCache();
    }

    public static void init() {
        hiddenActions = new ArrayList<>();
        for (HIDDEN_ACTIONS name : HIDDEN_ACTIONS.values()) {
            ObjType type = DataManager.getType(StringMaster
                    .format(name.name()), DC_TYPE.ACTIONS);
            hiddenActions.add(type);
        }
    }
    @Override
    public IActiveObj newAction(ObjType type, Ref ref, Player owner, GenericGame game) {
        return factory.newAction(type, ref, owner, game);
    }

    @Override
    public ActiveObj getAction(String typeName, Entity entity) {
        return factory.getAction(typeName, entity, true);
    }

    @Override
    public IActiveObj newAction(String abilTypeName, Entity entity) {
        return null;
    }

    public UnitAction getOrCreateAction(String typeName, Entity entity) {
        return factory.getOrCreateAction(typeName, entity);
    }

    //TODO refactor
    public ActiveObj getOrCreateActionOrSpell(String typeName, Entity entity) {
        return factory.getAction(typeName, entity, false);
    }


    public boolean activateAction(Obj target, Obj source, Active action) {
        Ref ref = source.getRef().getCopy();
        ref.setTarget(target.getId());
        ref.setTriggered(true);
        return action.activatedOn(ref);
    }

    public boolean activateAttackOfOpportunity(IActiveObj action, Obj countering, boolean free) {
        return true;
    }


    @Override
    public ActiveObj findCounterAttack(IActiveObj action, Obj _countering) {
        Unit target = (Unit) action.getOwnerUnit();
        Unit source = (Unit) _countering;
        return (ActiveObj) getCounterAttackAction(target, source,
                (ActiveObj) action);
    }


    public Active getCounterAttackAction(Unit countered, Unit countering,
                                         ActiveObj active) {
        for (ActiveObj attack : ExtraAttacksRule.getCounterAttacks(active, countering)) {
            //            if (AnimMaster.isTestMode())

            if (attack.canBeActivatedAsCounter()) {
                if (attack.canBeTargeted(active.getOwnerUnit().getId())) {
                    return attack;
                }
            }
        }
        return null;
    }

    @Override
    public void resetActions(Entity entity) {

    }


    public void constructActionMaps(Unit unit) {
        for (ACTION_TYPE sub : unit.getActionMap().keySet()) {
            unit.getActionMap().put(sub, new DequeImpl<>());
        }
        for (IActiveObj active : unit.getActives()) {
            UnitAction action = (UnitAction) active;
            ACTION_TYPE type = action.getActionType();
            if (type == null) {
                type = action.getActionType();
            }
            DequeImpl<UnitAction> list = unit.getActionMap().get(type);

            if (!hiddenActions.contains(action.getType())) {
                list.add(action);
            }
        }
    }

    public IFeatSpaceInitializer getSpaceManager() {
        return spaceManager;
    }

    public List<UnitAction> getOrCreateWeaponActions(WeaponItem weaponItem) {
        return factory.getOrCreateWeaponActions(weaponItem);
    }
}
