package main.game.battlecraft.logic.battle.mission;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.BattleConstructor;
import main.game.battlecraft.logic.battle.universal.BattleMaster;

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
        ObjType type= DataManager.getType(name, DC_TYPE.MISSIONS);
        Mission mission = new Mission(type, getMaster().getMetaMaster().getMetaGame().getScenario() );

        getBattle().setMission(mission);

        getMaster().getScriptManager().init();

    }

    @Override
    public MissionBattleMaster getMaster() {
        return (MissionBattleMaster) super.getMaster();
    }
}
