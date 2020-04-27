package main.level_editor.backend.sim.impl;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;

public class LE_ObjInitializer extends DC_ObjInitializer {
    public LE_ObjInitializer(DungeonMaster master) {
        super(master);
    }

    @Override
    protected DC_Obj createEncounter(ObjType type, Coordinates c, Integer id) {
        return LevelEditor.getManager().getObjHandler().createEncounter(type, c, id);
    }
}
