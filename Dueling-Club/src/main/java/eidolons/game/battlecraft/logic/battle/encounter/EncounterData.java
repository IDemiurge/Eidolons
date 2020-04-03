package eidolons.game.battlecraft.logic.battle.encounter;

import main.system.data.DataUnit;

public class EncounterData extends DataUnit<EncounterData.ENCOUNTER_VALUE> {

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
