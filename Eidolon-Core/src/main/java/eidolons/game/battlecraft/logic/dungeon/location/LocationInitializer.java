package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Core;
import eidolons.game.core.launch.TestLaunch;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationInitializer extends DungeonInitializer {
    public LocationInitializer(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location initDungeon() {
        if (getDungeonPath() == null) {
            String data =
                    Core.getGame().getMetaMaster().getMetaDataManager().getSoloDungeonPath();
            if (data == null) {
                TestLaunch testLaunch = EngineLauncher.getInstance().getCustomLaunch();
                if (testLaunch != null) {
                    main.system.auxiliary.log.LogMaster.important("*******Custom Launch xml path: " +
                            testLaunch.getValue(TestLaunch.TestValue.module));
                    data = testLaunch.getValue(TestLaunch.TestValue.module);
                }
            }
            if (data != null) {
                setDungeonPath(data);
            } else
                setDungeonPath(game.getDataKeeper().getDungeonData()
                        .getContainerValue(DUNGEON_VALUE.PATH, 0));
        }
        //or take mission directly?


        return getBuilder().buildDungeon(getDungeonPath());
    }

    @Override
    public String getDungeonPath() {
        if (getMetaMaster().getMissionMaster().getFloor() != null) {
            return getMetaMaster().getMissionMaster().getFloor().getLevelFilePath();
        }
        return super.getDungeonPath();
    }

    @Override
    public Location createDungeon(ObjType type) {
        return new Location(getMaster(), getMetaMaster().getFloor());
    }

}
