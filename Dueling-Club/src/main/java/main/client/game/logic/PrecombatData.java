package main.client.game.logic;

import main.client.game.logic.PrecombatData.PRECOMBAT_VALUES;
import main.system.net.data.DataUnit;

public class PrecombatData extends DataUnit<PRECOMBAT_VALUES> {

    private boolean aborted;

    public PrecombatData(String precombatData) {
        super(precombatData);
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }

    public enum PRECOMBAT_VALUES {
        DIVINED_SPELLS,
        HIRED_MERCENARIES,
        MEMORIZED_SPELLS,
        PREPARED_SPELLS;
    }
}
