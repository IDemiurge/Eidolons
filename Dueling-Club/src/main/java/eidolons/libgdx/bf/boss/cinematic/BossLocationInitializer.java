package eidolons.libgdx.bf.boss.cinematic;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.entity.type.ObjType;

public class BossLocationInitializer extends LocationInitializer {

    public BossLocationInitializer(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location createDungeon(ObjType type) {
        return new Location((LocationMaster) getMaster(), new Dungeon(type, false)){
            @Override
            public String getMapBackground() {
                return "atlas.txt";
            }
        };
    }
}
