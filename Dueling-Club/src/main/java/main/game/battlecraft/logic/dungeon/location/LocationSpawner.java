package main.game.battlecraft.logic.dungeon.location;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.dungeon.universal.Spawner;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.bf.Coordinates;
import main.game.module.adventure.travel.EncounterMaster;
import main.game.module.dungeoncrawl.ai.DungeonCrawler;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.test.PresetMaster;

import java.util.*;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationSpawner extends Spawner<Location> {
    Map<Dungeon, Map<MapBlock, Map<Coordinates, ObjType>>> specialEncounters = new HashMap<>();
    private boolean autoSpawnOn;

    public LocationSpawner(DungeonMaster master) {
        super(master);
    }

    @Override
    public void spawn() {
        super.spawn();
    }

    @Override
    public void spawn(UnitData data, DC_Player player, SPAWN_MODE mode) {
        if (player.isMe() && PresetMaster.getPreset()==null ) {
            List<String> list = ListMaster.toNameList(
             getGame().getMetaMaster().getPartyManager()
              .getParty().getMembers());
            getPositioner().setMaxSpacePercentageTaken(50);
            Iterator<Coordinates> iterator = getPositioner().getPlayerPartyCoordinates(list).iterator();

          for (Unit member:getGame().getMetaMaster().getPartyManager().getParty().getMembers()){
              member.setCoordinates( iterator.next());
              member.setConstructed(false);
              getGame().getState().addObject(member);
              member.setOriginalOwner(player);
              member.setOwner(player);
              //what else should be done to *spawn*?
          }
        } else {
            super.spawn(data, player, mode);
        }


             //so dungeon-units are spawned 'automatically' by DungeonBuilder...
            // what about future waves?
            // how to add them to a mission?
            // via encounters most likely... or unitGroups
            //TODO
            //TODO
            //TODO
            //TODO
            //TODO
            //TODO
            //TODO
            //TODO


        }
//        if (respawn)
//        if (player.isMe()) {
//        List<String> list = ListMaster.toNameList(
//         getGame().getMetaMaster().getPartyManager()
//          .getParty().getMembers());
//        List<String> coordinates =StringMaster.convertToStringList(
//         getPositioner().getPlayerPartyCoordinates(list));
//        data.setValue(PARTY_VALUE.MEMBERS, StringMaster.constructContainer(list));
//        data.setValue(PARTY_VALUE.COORDINATES, StringMaster.constructContainer(coordinates));
//        }
    //on entering room?
    public void spawnDungeon() {
//        getDungeon().getPlan().getObjMap()
//        spawnUnit(type, c, enemy, facing, level);
    }

    public void addDungeonEncounter(Dungeon c_dungeon, MapBlock block, Coordinates c, ObjType type) {
        Map<MapBlock, Map<Coordinates, ObjType>> map = specialEncounters.get(c_dungeon);
        if (map == null) {
            map = new HashMap<>();
            specialEncounters.put(c_dungeon, map);
        }
        Map<Coordinates, ObjType> encounterMap = map.get(block);
        if (encounterMap == null) {
            encounterMap = new HashMap<>();
            map.put(block, encounterMap);
        }
        encounterMap.put(c, type);
    }


    public void spawnDungeonCreeps(Location dungeon) {

        // special units (preset)
        // groups - in rooms/spec places; behavior - per preference

        // for open-air instances - pick areas around entrances or treasures or
        // other objects or just random but zone-based!

        // for (b block : dungeon.getBlocks()) {
        // }
        // dungeon.getMap().getBlock(blockName);

		/*
         * Assign block per creep group? So a dungeon has a repertoire and map template...
		 * then we calculate total power...
		 * First, spawn the 'must have' groups, around entrances and treasures
		 */
        if (dungeon.isSublevel()) {

        } else {
            // different alg?
        }
        // PartyManager.getParty().getTotalPower();
        // int power = DungeonMaster.getDungeonPowerTotal(dungeon);
        // int maxGroups = dungeon.getIntParam(PARAMS.MAX_GROUPS);

        int power = 0;

        int preferredPower = dungeon.getLevel()

                // + PartyManager.getParty().getPower()
                + getBattleMaster().getOptionManager().getOptions().getBattleLevel();
        int min = preferredPower * 2 / 3;
        int max = preferredPower * 3 / 2;

        for (MapBlock block : dungeon.getPlan().getBlocks()) {
            Wave group;

            if (specialEncounters.get(dungeon) != null) {
                Map<Coordinates, ObjType> specEncounters = specialEncounters.get(dungeon)

                        .get(block);
                for (Coordinates c : specEncounters.keySet()) {
                    ObjType waveType = specEncounters.get(c);

                    if (waveType.getGroup().equalsIgnoreCase("Substitute")) {
                        waveType = EncounterMaster.getSubstituteEncounterType(waveType, dungeon.getDungeon(),

                                preferredPower);
                    }

                    group = new Wave(waveType, game, new Ref(), game.getPlayer(false));
                    group.setCoordinates(c);

//                    spawnWave(group, true);
//                    initGroup(group);
                    power += group.getPower();

                }

            } else { // TODO POWER PER BLOCK!
                if (!autoSpawnOn) {
                    continue;
                }
                // if (power < preferredPower)
                // preferredPower = power;
                // if (power < preferredPower / 3)
                // break;
                if (!checkSpawnBlock(block)) {
                    continue;
                }
                // sort blocks! by spawn priority...
                // can be more than 1 group, right? maybe merge?
                group = getCreepGroupForBlock(preferredPower, dungeon.getDungeon(), block, min, max);
                group.setPreferredPower(preferredPower);

//                spawnWave(group, true);
//                initGroup(group);
                // power -= group.getPower();
                power += group.getPower();
            }
        }

        if (power > min) {
            // spawn wandering creeps - apart from groups? in max distance from
            // them?
        }
    }

    private boolean checkSpawnBlock(MapBlock block) {
        if (DungeonCrawler.isAiTestOn()) {
            return block.getId() < 2;
        }
        return block.getRoomType() == ROOM_TYPE.GUARD_ROOM

                || block.getRoomType() == ROOM_TYPE.COMMON_ROOM
                || block.getRoomType() == ROOM_TYPE.THRONE_ROOM
                || block.getRoomType() == ROOM_TYPE.EXIT_ROOM
                || block.getRoomType() == ROOM_TYPE.DEATH_ROOM;
    }

    private Wave getCreepGroupForBlock(int preferredPower, Dungeon dungeon, MapBlock block,
                                       int min, int max) {
        // alt? vielleicht fur einige spezielle orte...
        String property = dungeon.getProperty(PROPS.ENCOUNTERS);
        int mod = block.getSpawningPriority();
        if (mod == 0) {
            mod = 100;
        }
        Wave wave;
        List<ObjType> list = DataManager.toTypeList(property, DC_TYPE.ENCOUNTERS);
        Collections.shuffle(list);
        ObjType type = null;
        for (ObjType t : list) {
            type = t;
            if (EncounterMaster.getPower(type, false) < min * mod / 100) {
                continue;
            }
            if (EncounterMaster.getPower(type, false) > max * mod / 100) {
                continue;
            }
            break;
        }
        if (type == null) {
            type = new RandomWizard<ObjType>().getObjectByWeight(property, ObjType.class);
        }
        wave = new Wave(type, game, new Ref(game), game.getPlayer(false));
        wave.setPreferredPower(preferredPower * mod / 100);
        wave.setBlock(block);

        return wave;
    }
}
