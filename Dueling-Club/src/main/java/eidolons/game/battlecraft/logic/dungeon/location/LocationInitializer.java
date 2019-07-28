package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Eidolons;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationInitializer extends DungeonInitializer<Location> {
    public LocationInitializer(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location initDungeon() {
        if (getDungeonPath() == null) {
            String data =
                    Eidolons.getGame().getMetaMaster().getMetaDataManager().getMissionPath();
            if (data != null) {
                setDungeonPath(data);
            } else
                setDungeonPath(game.getDataKeeper().getDungeonData()
                        .getContainerValue(DUNGEON_VALUE.PATH, 0));
        }
        //or take mission directly?
        return (Location) getBuilder().buildDungeon(getDungeonPath());
    }

    @Override
    public Location createDungeon(ObjType type) {
        return new Location(getMaster(), new Dungeon(type, false));
    }

}
