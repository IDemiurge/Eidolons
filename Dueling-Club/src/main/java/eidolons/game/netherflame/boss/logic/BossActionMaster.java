package eidolons.game.netherflame.boss.logic;

import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * no control pause?!
 * preset speed ?
 */
public class BossActionMaster<T extends BOSS_ACTION>  {
    public Collection<ActiveObj> getStandardActions(Unit unit, Class<T> c) {
        List<ActiveObj> list = new ArrayList<>();
        for (T value : c.getEnumConstants()) {

            String name = value.getName();
            ObjType type = DataManager.getType(name, DC_TYPE.ACTIONS);
            if (type == null) {
                continue;
            }
            DC_UnitAction action = new  BossAction(type, unit);
            list.add(action);
        }
        return list;
    }


}
