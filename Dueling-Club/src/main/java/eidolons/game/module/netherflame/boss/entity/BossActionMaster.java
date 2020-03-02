package eidolons.game.module.netherflame.boss.entity;

import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * no control pause?!
 * preset speed ?
 */
public class BossActionMaster {
    public static Collection<ActiveObj> getStandardActions(Unit unit) {
        List<ActiveObj> list = new ArrayList<>();

        //any at all?   isn't it dangerous?

        for (BOSS_ACTION_REAPER value : BOSS_ACTION_REAPER.values()) {
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

    public static BOSS_ACTION_REAPER getReaperAction(String name) {
        for (BOSS_ACTION_REAPER value : BOSS_ACTION_REAPER.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    public enum BOSS_ACTION_REAPER {
        SEVER,
        SHATTER,
        DEATH_RAZOR__PURIFY{
            @Override
            public String getName() {
                return "Death Razor - Purify";
            }
        },
        PURIFIER,
        SOUL_RIP,
        TOUCH_OF_TORMENT,
        DEATH_WHIRL, //THROW SCYTHE!

        //SPELL
        NETHER_CALL,
        NETHER_BURST,
        NETHER_WAVE,
        MORTAL_SCREAM,

        VOID_RIFT,
        SUNDER,
        BLACK_MIRROR,
        NETHER_TUNNEL //SWAP
        ;

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }
}
