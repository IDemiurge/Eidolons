package main.game.battlecraft.logic.dungeon.arena;

import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.battlecraft.logic.dungeon.DungeonInitializer;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ArenaInitializer extends DungeonInitializer<ArenaDungeon> {
    public ArenaInitializer(ArenaDungeonMaster master) {
        super(master);
    }



    @Override
    public ArenaDungeon createDungeon(ObjType type) {
        return new ArenaDungeon(new Dungeon(type),master);
    }
}
