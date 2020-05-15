package eidolons.game.battlecraft.logic.mission.encounter;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import eidolons.game.battlecraft.logic.mission.quest.QuestMission;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.MissionHandler;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.List;
import java.util.Map;

import static main.system.auxiliary.log.LogMaster.log;


public class EncounterSpawner extends MissionHandler<QuestMission> {

    public enum ENCOUNTER_STATUS {
        ENGAGED, ALERT, IDLE, REGROUPING
    }

    public EncounterSpawner(MissionMaster master) {
        super(master);
    }

    public void checkMergeEncounters(List<Encounter> encounters) {
        /*

         */
    }
        public void spawnEncounters(List<Encounter> encounters) {
        Map<Integer, String> dataMap = getGame().getMetaMaster().getDungeonMaster().
                getDataMap(DataMap.encounters);
        for (Encounter encounter : encounters) {
            try {
                EncounterData data = dataMap == null ? new EncounterData(encounter) :
                        new EncounterData(dataMap.get(encounter.getOrigId()));
                spawnEncounter(data, encounter, encounter.getCoordinates());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            //some would be concealed after spawn - ambush.
        }
    }


    public void spawnEncounter(EncounterData data, Encounter encounter, Coordinates coordinates) {
        log(1, "Spawning " + encounter.getName() +
                " at " + coordinates);
        float adjustCoef = data.getFloatValue(EncounterData.ENCOUNTER_VALUE.adjust_coef);
        if (adjustCoef == 0) {
            adjustCoef = new Float(encounter.getType().getIntParam(PARAMS.ADJUST_COEF));
        }
        adjustCoef /= 100;

        int targetPower = data.getIntValue(EncounterData.ENCOUNTER_VALUE.target_power);


        new EncounterAdjuster(master).adjustEncounter(encounter, targetPower, adjustCoef);

        DC_Player owner = getGame().getPlayer(false);

        List<ObjType> types = encounter.getTypes();
        List<Unit> units = getSpawner().spawn(coordinates, types, owner, Spawner.SPAWN_MODE.DUNGEON);

        encounter.setUnits(units);
        //TODO leader by type
        AiData aiData = getGame().getAiManager().getGroupHandler().getAiData(encounter.getOrigId());
        log(1, "AiData= " + aiData);
        encounter.setSpawned(true);
        encounter.setAiData(aiData);
        encounter.setGroupAI(getGame().getAiManager().getGroupHandler().createEncounterGroup(encounter, aiData));

        log(1, "Spawned " + encounter.getName() +
                " with " + units);
    }


}
