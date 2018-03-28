package main.game.battlecraft.logic.battle.mission;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.BattleConstructor;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/7/2017.
 */
public class MissionConstructor extends BattleConstructor<MissionBattle> {
    public MissionConstructor(BattleMaster<MissionBattle> master) {
        super(master);
    }

    @Override
    public void init() {


//        ScenarioMeta meta = (ScenarioMeta) getMaster().getMetaMaster().getMetaGame();
        String name = getMaster().getMetaMaster().getMissionName();
//         StringMaster.openContainer(
//         meta.getScenario().getProperty(PROPS.SCENARIO_MISSIONS)).get(index);
        ObjType type = DataManager.getType(name, DC_TYPE.MISSIONS);
        if (type == null) {
            if (!StringMaster.getFormat(name).equalsIgnoreCase(".xml")) {
                name = "crawl\\" + name + ".xml";
            } //TODO temporary
            type = new ObjType(StringMaster.getLastPathSegment(StringMaster.cropFormat(name)),
             DC_TYPE.MISSIONS);
            type.setProperty(PROPS.MISSION_FILE_PATH, name);

        }
        String levelPath = type.getProperty(PROPS.MISSION_FILE_PATH);
        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
         levelPath);
        Mission mission = new Mission(type, getMaster().getMetaMaster().getMetaGame().getScenario());

        getBattle().setMission(mission);

        getMaster().getScriptManager().init();

    }

    @Override
    public MissionBattleMaster getMaster() {
        return (MissionBattleMaster) super.getMaster();
    }
}
