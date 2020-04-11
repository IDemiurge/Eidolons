package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.encounter.EncounterData.ENCOUNTER_VALUE;
import eidolons.game.battlecraft.logic.battle.mission.MissionBattle;
import eidolons.game.battlecraft.logic.battle.universal.BattleHandler;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.List;
import java.util.Map;

import static eidolons.game.battlecraft.logic.battle.encounter.EncounterSpawner.ENCOUNTER_STATUS.ENGAGED;


public class EncounterSpawner extends BattleHandler<MissionBattle> {

    public EncounterSpawner(BattleMaster<MissionBattle> master) {
        super(master);
    }

    public enum ENCOUNTER_STATUS {
        ENGAGED, ALERT, IDLE, REGROUPING
    }

    public void newRound(Encounter encounter) {
/*
if it is engaged...
some groups will be defensive, relying on Reinforcements!
 */
        if (encounter.getStatus() == ENGAGED) {
            //turns before spawn
//            getTotalSpawnedPower();
//            getCurrentPower();
//            checkCanSpawnReinforcements();
        }

    }

    //on enter module?
    public void spawnEncounter(Integer id) {
        Map<Integer, String> dataMap = getGame().getMetaMaster().getDungeonMaster().getDataMap(DataMap.encounter);
        EncounterData data =
                new EncounterData(dataMap.get(id));

        ObjType encounterType = DataManager.getType(data.getValue(ENCOUNTER_VALUE.type)
                , DC_TYPE.ENCOUNTERS);
        Coordinates coordinates;

    }

    public void spawnEncounter(ObjType encounterType, Coordinates coordinates, EncounterData data) {
        float adjustCoef = data.getFloatValue(ENCOUNTER_VALUE.adjust_coef);
//        new Encounter(data);
        //can we have 2+ encounters in a fight/block?

        UnitsData sdata = new UnitsData(""); //can be partially specified in LE?
//        sdata.setValue(UnitData.PARTY_VALUE.COORDINATES, coordinatesString);
        List<Unit> units = getSpawner().spawn(sdata, getGame().getPlayer(false), Spawner.SPAWN_MODE.DUNGEON);

        //getMap()
//        AiData aiData = getGame().getAiManager().getGroupHandler().getAiData(id);
//        createGroup(units);
        /*
        hero variants - depending on what eidolon goes?
        soulforce smart adjust - don't spawn deadly if no SF?


//                PROPS.PRESET_GROUP,
//                PROPS.EXTENDED_PRESET_GROUP,
//                PROPS.SHRUNK_PRESET_GROUP,
//                PROPS.UNIT_TYPES,
//                PROPS.FILLER_TYPES

  int height = UnitGroupMaster.getGroupSizeY(owner);// UnitGroupMaster.getCurrentGroupHeight();
            int width = UnitGroupMaster.getGroupSizeX(owner);


  if (UnitGroupMaster.getFlip() == FLIP.CW90) {
                int buffer = c.x;
                c.setX(c.y);
                c.setY(buffer);
            } else if (UnitGroupMaster.getFlip() == FLIP.CCW90) {
                int buffer = width - c.x;
                c.setX(height - c.y);
                c.setY(buffer);

            }
            if (UnitGroupMaster.isMirror()) {
                if (UnitGroupMaster.getFlip() == FLIP.CW90
                        || UnitGroupMaster.getFlip() == FLIP.CCW90) {
                    c.setX(width - c.x);
                } else {

                    c.setY(height - c.y);
                }

            }
         */
    }

}
