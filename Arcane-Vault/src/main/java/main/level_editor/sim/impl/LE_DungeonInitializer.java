package main.level_editor.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import main.entity.type.ObjType;

public class LE_DungeonInitializer extends DungeonInitializer {
    public LE_DungeonInitializer(LE_DungeonMaster le_dungeonMaster) {
        super(le_dungeonMaster);
    }

    @Override
    public DungeonWrapper createDungeon(ObjType type) {
//        return super.createDungeon(type);
        return null;
    }

    @Override
    public DungeonWrapper initDungeon() {
        return super.initDungeon();
    }
}
