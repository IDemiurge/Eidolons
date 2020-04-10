package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.content.data.EntityData;
import eidolons.entity.obj.BattleFieldObject;

public class EncounterData extends EntityData<EncounterData.ENCOUNTER_VALUE> {

    public EncounterData(BattleFieldObject entity) {
        super(entity);
    }

    public enum ENCOUNTER_VALUE {
        type,
        custom,
        coordinates,
        adjust_coef,
        ;
    }

    public EncounterData(String text) {
        super(text);
    }
}
