package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.launch.CustomLaunch;
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
                    Eidolons.getGame().getMetaMaster().getMetaDataManager().getSoloDungeonPath();
            if (data == null) {
                CustomLaunch customLaunch = EngineLauncher.getInstance().getCustomLaunch();
                if (customLaunch != null) {
                    main.system.auxiliary.log.LogMaster.important("*******Custom Launch xml path: " +
                            customLaunch.getValue(CustomLaunch.CustomLaunchValue.xml_path));
                    data = customLaunch.getValue(CustomLaunch.CustomLaunchValue.xml_path);
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
