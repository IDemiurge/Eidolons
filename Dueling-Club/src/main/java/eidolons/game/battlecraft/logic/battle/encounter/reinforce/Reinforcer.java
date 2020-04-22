package eidolons.game.battlecraft.logic.battle.encounter.reinforce;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.encounter.Encounter;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static main.content.enums.EncounterEnums.REINFORCEMENT_CHANCE;

public class Reinforcer {
    /*
    this mechanic changes some things
    - we cannot disengage so easily
    - if we disengage, it should be an EVENT
    -

     */
    Encounter encounter;
    Map<Integer, Reinforcement> reinforceMap = new LinkedHashMap<>();
    public Reinforcer(Encounter encounter) {
        this. encounter=  encounter;
        REINFORCEMENT_CHANCE chance = new EnumMaster<REINFORCEMENT_CHANCE>().retrieveEnumConst(REINFORCEMENT_CHANCE.class,
                encounter.getType().getValue(PROPS.REINFORCEMENT_CHANCE));
//        if (checkChance(chance)){
//            int round = getDelay();
//            Reinforcement reinforcement = new Reinforcement(type, encounter, strength, custom);
//            reinforceMap.put(round, reinforcement);
//        }
        // per encounter?
    }

    private void preCombat(Reinforcement reinforcement ) {
        switch (reinforcement.type) {
            case call_help:
                break;
            case portal:
                break;
            case patrol:
                break;
            case ambush:
                break;
        }
    }
    public void encounterEngaged(){
        //determine reinforcement and init proper info-event


    }
    public void roundEnds(int n){
        Reinforcement reinforcement = reinforceMap.get(n);

        if (reinforcement != null) {
            spawn(reinforcement);
        }

    }

    private void spawn(Reinforcement reinforcement) {
        /*

         */
        preSpawn(reinforcement);
        Coordinates origin = null;
        List<ObjType> types = null;
//        List<Coordinates> coordinates=encounter.getGame().getDungeonMaster().
//                getPositioner().getCoordinates(origin, false, types);
        DC_Player owner=encounter.getGame().getPlayer(false);
        List<Unit> units=encounter.getGame().getDungeonMaster().getSpawner().spawn(
                origin, types, owner, Spawner.SPAWN_MODE.DUNGEON);

        /*
        new ai group

         */

        afterSpawn(reinforcement, origin);

    }

    private void preSpawn(Reinforcement reinforcement) {
    }

    private void afterSpawn(Reinforcement reinforcement, Coordinates origin) {
        switch (reinforcement.type) {
            case call_help:
                break;
            case portal:
                break;
            case patrol:
                break;
            case ambush:
                break;
        }
    }
}
