package eidolons.entity.mngr.action;

import eidolons.ability.ActionGenerator;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.Spell;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.StringMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.IActiveObj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 2/2/2022
 */
public class ActionFactory {
    protected HashMap<Entity, Map<String, IActiveObj>> actionsCache = new HashMap<>();
    private GenericGame game;

    public ActionFactory(GenericGame game) {
        this.game = game;
    }
    public void clearCache() {
        actionsCache.clear();
    }

    public ActiveObj newActive(String typeName, Entity entity, boolean spell) {
        Map<String, IActiveObj> map = actionsCache.get(entity);
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(entity, map);
        }
        if (map.get(typeName) != null) {
            return (UnitAction) map.get(typeName);
        }

        ObjType type = DataManager.getType(typeName, spell ? DC_TYPE.SPELLS : DC_TYPE.ACTIONS);
        if (type == null) {
            LogMaster.log(1, "no such active: " + typeName);
            throw new RuntimeException("no such active: " + typeName);
        }
        Ref ref = Ref.getCopy(entity.getRef());
        if (spell) {
            return newSpell(type, ref, entity.getOwner(), game);
        }
        return newAction(type, ref, entity.getOwner(), game);
    }

    public ActiveObj getAction(String typeName, Entity entity, boolean onlyIfAlreadyPresent) {
        typeName = typeName.trim();
        if (actionsCache.get(entity) == null) {
            actionsCache.put(entity, new StringMap<>());
        }
        IActiveObj action = actionsCache.get(entity).get(typeName);
        if (action == null) {

            if (entity instanceof Unit) {
                action = ((Unit) entity).getSpell(typeName);
                if (action != null) return (ActiveObj) action;
            }

            if (onlyIfAlreadyPresent) {
                if (!StringMaster.contains(entity.getProperty(G_PROPS.ACTIVES), typeName)) {
                    return null;
                }
            }

            if (DataManager.isTypeName(typeName, DC_TYPE.SPELLS)) {
                action = newSpell(typeName, entity);
            } else action = newAction(typeName, entity);
            actionsCache.get(entity).put(typeName, action);
        }
        return (ActiveObj) action;
    }

    public UnitAction newAction(ObjType type, Ref ref, Player owner, GenericGame game) {
        UnitAction action = new UnitAction(type, owner, game, ref);
        game.getState().addObject(action);

        Map<String, IActiveObj> map = actionsCache.get(ref.getSourceObj());
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(ref.getSourceObj(), map);
        }
        map.put(action.getName(), action);
        return action;
    }

    public Spell newSpell(ObjType type, Ref ref, Player owner, GenericGame game) {
        Spell action = new Spell(type, owner, (DC_Game) game, ref);
        game.getState().addObject(action);
        Map<String, IActiveObj> map = actionsCache.get(ref.getSourceObj());
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(ref.getSourceObj(), map);
        }
        map.put(action.getName(), action);
        return action;
    }
    private Spell newSpell(String typeName, Entity entity) {
        return (Spell) newActive(typeName, entity, true);
    }

    public UnitAction newAction(String typeName, Entity entity) {
        return (UnitAction) newActive(typeName, entity, false);
    }

    public UnitAction getOrCreateAction(String typeName, Entity entity) {
        return (UnitAction) getAction(typeName, entity, false);
    }
    public List<UnitAction> getOrCreateWeaponActions(WeaponItem weapon) {
        if (weapon == null) {
            return new ArrayList<>();
        }
        Unit obj = weapon.getOwnerObj();
        if (obj == null) {
            return new ArrayList<>();
        }
        List<UnitAction> list = weapon.getAttackActions();
        if (list != null) {
            return new ArrayList<>(list);
        }
        list = new ArrayList<>();
        for (String typeName : ContainerUtils.open(weapon.getProperty(PROPS.WEAPON_ATTACKS))) {
            if (!weapon.isMainHand()) {
                typeName = ActionGenerator.getOffhandActionName(typeName);
            }
            UnitAction action = getOrCreateAction(typeName, obj);

            // action.setObjTypeGroup(ACTION_TYPE_GROUPS.HIDDEN);
            // TODO so it's inside normal attack then?
            if (action == null) {
                continue;
            }
            list.add(action);
        }
        if (checkAddThrowAction(weapon.getOwnerObj(), weapon)) {
            //TODO
            // DC_UnitAction throwAction = getOrCreateAction(weapon.isMainHand() ? THROW_MAIN : THROW_OFFHAND,
            //         obj);
            // throwAction.setName(getThrowName(weapon.getName()));
            // list.add(throwAction);
        }
        weapon.setAttackActions(new ArrayList<>(list));
        return list;
    }

    protected boolean checkAddThrowAction(Unit unit, WeaponItem weapon) {
        if (!RuleKeeper.checkFeature(RuleEnums.FEATURE.THROW_WEAPON))
            return false;
        if (weapon == null) {
            return false;
        }

        if (weapon.getName().contains("Battle Spear")) {
            return true;
        }
        if (weapon.isShield()) {
            if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BUCKLERS) {
                if (unit.checkBool(GenericEnums.STD_BOOLS.BUCKLER_THROWER)) {
                    return false;
                }
            }
        }
        if (!weapon.isWeapon()) {
            return false;
        }
        if (weapon.isRanged()) {
            return false;
        }
        if (weapon.isNatural()) {
            return false;
        }
        Integer bonus = unit.getIntParam(PARAMS.THROW_SIZE_BONUS);

        if (bonus > -2) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.TINY) {
                return true;
            }
        }
        if (bonus > -1) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.SMALL) {
                return true;
            }
        }
        if (bonus > 2) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.HUGE) {
                return true;
            }
        }
        if (bonus > 1) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.LARGE) {
                return true;
            }
        }
        if (bonus > 0) {
            return weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.MEDIUM;
        }
        return false;
    }
}
