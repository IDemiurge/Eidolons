package eidolons.game.netherflame.boss.logic.action;

import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.logic.BossCycle.BOSS_TYPE;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.generic.ActionManager;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * no control pause?!
 * preset speed ?
 */
public abstract class BossActionMaster<T extends BOSS_ACTION>  extends BossHandler implements ActionManager {
    private final DC_ActionManager dcManager;
    private final Map<T, BossAction> map = new HashMap<>();
    private final Map<BossUnit, Map<String, BossAction>> cache = new HashMap<>();

    public BossActionMaster(BossManager manager) {
        super(manager);
        dcManager = getGame().getActionManager();
    }

    public List<ActiveObj> getStandardActions(BOSS_TYPE bossType, Unit unit ) {
        List<ActiveObj> list = new ArrayList<>();
        Class<T> c = getActionsEnum();
        Map<String, BossAction> actionMap ;
        cache.put((BossUnit) unit,actionMap= new HashMap<>());

        for (T value : c.getEnumConstants()) {
            String name = value.getName();
            ObjType type = DataManager.getType(name, DC_TYPE.ACTIONS);
            //check type //TODO
            if (!check(bossType, value))
                continue;
            if (type == null) {
                continue;
            }
            ////TODO spells?!
            BossAction action = new  BossAction(type, unit);
            list.add(action);
            map.put(value, action);
            actionMap.put(name, action);
        }
        return list;
    }

    protected abstract boolean check(BOSS_TYPE bossType, T value);

    @Override
    public void resetActions(Entity entity) {
        if (ListMaster.isNotEmpty(entity.getActives())) {
            return;
        }
        BOSS_TYPE type=BOSS_TYPE.melee;
        if (getEntity(BOSS_TYPE.caster)==entity) {
              type=BOSS_TYPE.caster;
        }
        List<ActiveObj> actions = getStandardActions(type, (Unit) entity);
        entity.setActives(actions);
// ((Unit) entity).setActionMap();

        // MapMaster.addToListMap(map,

    }
    @Override
    public ActiveObj getAction(String abilTypeName, Entity entity) {
        return cache.get(entity).get(abilTypeName);
    }
    protected abstract Class<T > getActionsEnum();

    public   DC_ActiveObj getAction(T key ) {
        return map.get(key);
    }

    public boolean canActivate(DC_ActiveObj activeObj){
        return true;
    }

    public abstract String getMainAttack() ;

    @Override
    public ActiveObj newAction(ObjType type, Ref ref, Player owner, GenericGame game) {
        return null;
    }

    @Override
    public ActiveObj findCounterAttack(ActiveObj action, Obj _countering) {
        return dcManager.findCounterAttack(action, _countering);
    }
    @Override
    public boolean activateAttackOfOpportunity(ActiveObj action, Obj src, boolean free) {
        return false;
    }


    @Override
    public ActiveObj newAction(String abilTypeName, Entity entity) {
        return null;
    }



    @Override
    public void resetCostsInNewThread() {

    }
}
