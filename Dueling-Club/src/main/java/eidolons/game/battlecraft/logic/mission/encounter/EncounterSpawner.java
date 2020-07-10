package eidolons.game.battlecraft.logic.mission.encounter;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import eidolons.game.battlecraft.logic.mission.quest.QuestMission;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.MissionHandler;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import main.content.enums.EncounterEnums;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.ListMaster;

import java.util.Arrays;
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
        if (!ListMaster.isNotEmpty(encounters)) {
            return;
        }
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


    public List<Unit> spawnEncounter(EncounterData data, Encounter encounter, Coordinates... coordinates) {
        return spawnEncounter(data, null, encounter, coordinates);
    }

    public List<Unit> spawnEncounter(EncounterData data, AiData aiData, Encounter encounter, Coordinates... coordinates) {
        log(1, "Spawning " + encounter.getName() +
                " at " + coordinates);

        EncounterEnums.SPAWN_MODE mode =
                data.getSpawnMode();
        if (mode != null)
            switch (mode) {
                case on_approach_clean_respawn:
                    //just create triggers? also set field for encounter so we know to cleanup after it
                    return null;
            }

        float adjustCoef = data.getFloatValue(EncounterData.ENCOUNTER_VALUE.adjust_coef);
        if (adjustCoef == 0) {
            adjustCoef = new Float(encounter.getType().getIntParam(PARAMS.ADJUST_COEF));
        }
        adjustCoef /= 100;

        int targetPower = data.getIntValue(EncounterData.ENCOUNTER_VALUE.target_power);

        if (!encounter.isAdjustmentProhibited()) {
            new EncounterAdjuster(master).adjustEncounter(encounter, targetPower, adjustCoef);
        }

        DC_Player owner = getGame().getPlayer(false);

        List<ObjType> types = encounter.getTypes();

        List<Unit> units;
        if (coordinates.length == 1) {
            units = getSpawner().spawn(coordinates[0], types, owner, Spawner.SPAWN_MODE.DUNGEON);
        } else {
            UnitsData uData = new UnitsData(Arrays.asList(coordinates), types);
            units = getSpawner().spawn(uData, owner, Spawner.SPAWN_MODE.DUNGEON);
        }

        //TODO leader by type
        if (aiData == null) {
            aiData = getGame().getAiManager().getGroupHandler().getAiData(encounter.getOrigId());
        }
        log(1, "AiData= " + aiData);
        encounter.setSpawned(true);
        encounter.setAiData(aiData);
        GroupAI groupAI = getGame().getAiManager().getGroupHandler().createEncounterGroup(
                encounter, aiData, units);
        encounter.setGroupAI(groupAI);

        log(1, "Spawned " + encounter.getName() +
                " with " + units);
        return units;
    }


}
