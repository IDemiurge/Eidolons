package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.battle.mission.MissionBattle;
import eidolons.game.battlecraft.logic.battle.universal.BattleHandler;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.List;
import java.util.Map;


public class EncounterSpawner extends BattleHandler<MissionBattle> {

    public enum ENCOUNTER_STATUS {
        ENGAGED, ALERT, IDLE, REGROUPING
    }

    public EncounterSpawner(BattleMaster  master) {
        super(master);
    }

    public void spawnEncounters(List<Encounter> encounters) {
        Map<Integer, String> dataMap = getGame().getMetaMaster().getDungeonMaster().getDataMap(DataMap.encounter);
        for (Encounter encounter : encounters) {
            EncounterData data =
                    new EncounterData(dataMap.get(encounter.getOrigId()));
            float adjustCoef = data.getFloatValue(EncounterData.ENCOUNTER_VALUE.adjust_coef);
            int targetPower;
            spawnEncounter(encounter, encounter.getCoordinates());
            //some would be concealed after spawn - ambush.
        }
    }


    public void spawnEncounter(Encounter encounter, Coordinates coordinates ) {
        new EncounterAdjuster(master).adjustEncounter(encounter, null );

        DC_Player owner = getGame().getPlayer(false);

        List<ObjType> types = encounter.getTypes();
        List<Unit> units = getSpawner().spawn(coordinates, types, owner, Spawner.SPAWN_MODE.DUNGEON);

        encounter.setUnits(units);
        //TODO leader by type

        AiData data = getGame().getAiManager().getGroupHandler().getAiData(encounter.getOrigId());
        encounter.setSpawned(true);
        encounter.setAiData(data);
        encounter.setGroupAI(getGame().getAiManager().getGroupHandler().createEncounterGroup(encounter, data));

    }


}
