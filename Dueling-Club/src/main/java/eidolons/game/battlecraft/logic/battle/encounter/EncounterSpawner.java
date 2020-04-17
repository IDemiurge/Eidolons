package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.encounter.EncounterData.ENCOUNTER_VALUE;
import eidolons.game.battlecraft.logic.battle.mission.MissionBattle;
import eidolons.game.battlecraft.logic.battle.universal.BattleHandler;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.List;
import java.util.Map;


public class EncounterSpawner extends BattleHandler<MissionBattle> {

    public EncounterSpawner(BattleMaster  master) {
        super(master);
    }

    public enum ENCOUNTER_STATUS {
        ENGAGED, ALERT, IDLE, REGROUPING
    }

    //on enter module?
    public void spawnEncounter(Integer id) {
        Map<Integer, String> dataMap = getGame().getMetaMaster().getDungeonMaster().getDataMap(DataMap.encounter);
        EncounterData data =
                new EncounterData(dataMap.get(id));

        ObjType encounterType = DataManager.getType(data.getValue(ENCOUNTER_VALUE.type)
                , DC_TYPE.ENCOUNTERS);
        Coordinates coordinates = null;
        spawnEncounter(encounterType, coordinates, data);
    }

    public void spawnEncounter(ObjType encounterType, Coordinates coordinates, EncounterData data) {
        DC_Player owner = getGame().getPlayer(false);
        Encounter encounter = new Encounter(encounterType, game, new Ref(), owner, coordinates);
        float adjustCoef = data.getFloatValue(ENCOUNTER_VALUE.adjust_coef);
//        new Encounter(data);
        //can we have 2+ encounters in a fight/block?
        //into handlers? GOOD practice to create in advance

        int targetPower;
        new EncounterAdjuster(master).adjustEncounter(encounter, null );
        List<ObjType> types = encounter.getTypes();
        List<Unit> units = getSpawner().spawn(coordinates, types, owner, Spawner.SPAWN_MODE.DUNGEON);
//        GuiEventManager.trigger(GuiEventType.UNIT_CREATED);
        /*
        multiple in one event + some more visuals?
         */
        encounter.setUnits(units);

//        GroupAI groupAI = new GroupAI(encounter.getLeader());
//        units.forEach(unit -> groupAI.add(unit));
//        AiData aiData = getGame().getAiManager().getGroupHandler().getAiData(id);

//        aiData.getArg()

//        groupAI.setType(RngMainSpawner.UNIT_GROUP_TYPE.BOSS);
    }


}
