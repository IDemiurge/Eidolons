package eidolons.game.battlecraft.logic.mission.encounter;

import eidolons.content.data.EntityData;
import eidolons.entity.obj.DC_Obj;
import main.system.auxiliary.EnumMaster;

import static main.content.enums.EncounterEnums.SPAWN_MODE;

public class EncounterData extends EntityData<EncounterData.ENCOUNTER_VALUE> {

    public EncounterData(DC_Obj entity) {
        super(entity);
        setValue(ENCOUNTER_VALUE.type, entity.getType().getName());
    }

    @Override
    public Class<? extends ENCOUNTER_VALUE> getEnumClazz() {
        return ENCOUNTER_VALUE.class;
    }

    public SPAWN_MODE getSpawnMode() {
        return new EnumMaster<SPAWN_MODE>().retrieveEnumConst(SPAWN_MODE.class, getValue(EncounterData.ENCOUNTER_VALUE.spawn_mode));
    }

    public enum ENCOUNTER_VALUE {
        type,
        custom,
        adjust_coef,
        target_power,

        reinforcements,
        spawn_mode,
        loot,
        positioning,
        /*
        could all a lot of neat custom shit here for

        Behavior
        Aggro

        apply() logic could be just like with custom types !..

         */
        ;
    }

    public EncounterData(String text) {
        super(text);
    }
}
