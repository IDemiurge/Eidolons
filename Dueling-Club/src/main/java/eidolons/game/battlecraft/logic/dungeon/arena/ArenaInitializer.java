package eidolons.game.battlecraft.logic.dungeon.arena;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ArenaInitializer extends DungeonInitializer<ArenaDungeon> {
    public ArenaInitializer(ArenaDungeonMaster master) {
        super(master);
    }


    @Override
    public ArenaDungeon createDungeon(ObjType type) {
        return new ArenaDungeon(new Dungeon(type), master);
    }
}
