package eidolons.game.battlecraft.logic.meta.scenario.script;

import main.system.data.DataUnit;

public class CellScriptData extends DataUnit<CellScriptData.CELL_SCRIPT_VALUE> {
    public CellScriptData(String text) {
        super(text);
        relevantValues = new String[]{
                "keys",
                "portals",
                "dialogue",
                "traps",
                "puzzles",
        };
    }

    public enum CELL_SCRIPT_VALUE {
        cell_type,
        keys,
        portals,
        dialogue,
        traps,
        puzzles,
        facing, named_point, script,
    }
}
