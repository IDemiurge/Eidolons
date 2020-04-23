package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.content.data.EntityData;
import eidolons.entity.obj.DC_Obj;

public class EncounterData extends EntityData<EncounterData.ENCOUNTER_VALUE> {

    public EncounterData(DC_Obj entity) {
        super(entity);
        setValue(ENCOUNTER_VALUE.type, entity.getType().getName());
    }

    @Override
    public Class<? extends ENCOUNTER_VALUE> getEnumClazz() {
        return ENCOUNTER_VALUE.class;
    }

    public enum ENCOUNTER_VALUE {
        type,
        custom,
        adjust_coef,
        target_power,
        /*
        could all a lot of neat custom shit here for
        Positioning
        Behavior
        Scripting
        Loot
        Reinforcements
        Aggro

        apply() logic could be just like with custom types !..

         */
        ;
    }

    public EncounterData(String text) {
        super(text);
    }
}
