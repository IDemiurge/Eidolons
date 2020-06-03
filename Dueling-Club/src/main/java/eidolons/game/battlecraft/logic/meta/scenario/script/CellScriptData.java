package eidolons.game.battlecraft.logic.meta.scenario.script;

import main.system.data.DataUnit;

public class CellScriptData extends DataUnit<CellScriptData.CELL_SCRIPT_VALUE> {
    public CellScriptData(String text) {
        super(text);
    }

    @Override
    public Class<? extends CELL_SCRIPT_VALUE> getEnumClazz() {
        return CELL_SCRIPT_VALUE.class;
    }

    @Override
    public String[] getRelevantValues() {
        return getValueConsts();
    }

    public enum CELL_SCRIPT_VALUE {
        script,
        dialogue,
        keys,
        portals,

        cell_type,
        puzzles,
        facing,
        named_point,

        platform_block, flip, marks,
//        traps,
    }
}
