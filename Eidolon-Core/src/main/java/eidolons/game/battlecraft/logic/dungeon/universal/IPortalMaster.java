package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.game.bf.Coordinates;

import java.util.Map;

public interface IPortalMaster {
    void unitMoved(Unit obj);

    void init(Map<Coordinates, CellScriptData> textDataMap);
}
