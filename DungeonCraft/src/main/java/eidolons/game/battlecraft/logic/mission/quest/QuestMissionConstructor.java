package eidolons.game.battlecraft.logic.mission.quest;

import eidolons.content.PROPS;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.game.battlecraft.logic.mission.universal.MissionConstructor;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.libgdx.gui.utils.FileChooserX;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 5/7/2017.
 */
public class QuestMissionConstructor extends MissionConstructor<QuestMission> {
    public QuestMissionConstructor(MissionMaster<QuestMission> master) {
        super(master);
    }

    @Override
    public void init() {
        String name = getMaster().getMetaMaster().getMetaDataManager().getMissionName();
        ObjType type = DataManager.getType(name, DC_TYPE.FLOORS);
        if (type == null) {
            type = new ObjType("Fake dungeon", DC_TYPE.FLOORS);
        }
        String levelPath = type.getProperty(PROPS.FLOOR_FILE_PATH);
        if (MainLauncher.levelPath != null) {
            levelPath = MainLauncher.levelPath;
        } else if (CoreEngine.SELECT_LEVEL) {
            levelPath = FileChooserX.chooseFile(PathFinder.getDungeonLevelFolder(), "xml",
                    DungeonScreen.getInstance().getOverlayStage());

        }
        // levelPath = "crawl\\evarinath catacombs.xml";

        main.system.auxiliary.log.LogMaster.log(1,"INIT: Level Path set to " +levelPath);

        EidolonsGame.lvlPath = levelPath;
        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
                levelPath);
        Floor mission = new Floor(type);
        mission.setLevelFilePath(levelPath);
        //, getMaster().getMetaMaster().getMetaGame().getScenario()

        getMission().setFloor(mission);

        getMaster().getScriptManager().init();

    }

    @Override
    public QuestMissionMaster getMaster() {
        return (QuestMissionMaster) super.getMaster();
    }
}
