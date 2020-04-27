package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.universal.BattleConstructor;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/7/2017.
 */
public class MissionConstructor extends BattleConstructor<QuestMission> {
    public MissionConstructor(BattleMaster<QuestMission> master) {
        super(master);
    }

    @Override
    public void init() {


//        ScenarioMeta meta = (ScenarioMeta) getMaster().getMetaMaster().getMetaGame();
        String name = getMaster().getMetaMaster().getMetaDataManager(). getMissionName();
//         StringMaster.openContainer(
//         meta.getScenario().getProperty(PROPS.SCENARIO_MISSIONS)).getVar(index);
        ObjType type = DataManager.getType(name, DC_TYPE.MISSIONS);
        if (type == null) {
            if (!StringMaster.getFormat(name).equalsIgnoreCase(".xml")) {
                if (getGame().getMetaMaster().isRngDungeon())
                {
                    name = "generated/" + name + ".xml";
                }
                else {
                    name = "crawl/" + name + ".xml";

                }
            } //TODO temporary
            type = new ObjType(PathUtils.getLastPathSegment(StringMaster.cropFormat(name)),
             DC_TYPE.MISSIONS);
            type.setProperty(PROPS.MISSION_FILE_PATH, name);

        }
        String levelPath = type.getProperty(PROPS.MISSION_FILE_PATH);
        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
         levelPath);
        Mission mission = new Mission(type);
        //, getMaster().getMetaMaster().getMetaGame().getScenario()

        getBattle().setMission(mission);

        getMaster().getScriptManager().init();

    }

    @Override
    public MissionBattleMaster getMaster() {
        return (MissionBattleMaster) super.getMaster();
    }
}
