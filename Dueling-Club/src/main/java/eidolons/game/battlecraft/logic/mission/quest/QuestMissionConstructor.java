package eidolons.game.battlecraft.logic.mission.quest;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.mission.universal.MissionConstructor;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/7/2017.
 */
public class QuestMissionConstructor extends MissionConstructor<QuestMission> {
    public QuestMissionConstructor(MissionMaster<QuestMission> master) {
        super(master);
    }

    @Override
    public void init() {
        String name = getMaster().getMetaMaster().getMetaDataManager(). getMissionName();
        ObjType type = DataManager.getType(name, DC_TYPE.DUNGEONS);
        if (type == null) {
            type = new ObjType("Fake dungeon" , DC_TYPE.DUNGEONS);
        }
        String levelPath = type.getProperty(PROPS.MISSION_FILE_PATH);
        levelPath = "crawl\\utmar hold.xml";
        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
         levelPath);
        Dungeon mission = new Dungeon(type);
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
