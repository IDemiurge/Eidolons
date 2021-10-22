package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.PROPS;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import eidolons.netherflame.lord.EidolonLord;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.*;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationSpawner extends Spawner {
    Map<Floor, Map<LevelBlock, Map<Coordinates, ObjType>>> specialEncounters = new HashMap<>();
    private boolean autoSpawnOn;

    public LocationSpawner(DungeonMaster master) {
        super(master);
    }

    @Override
    public void spawn() {
        super.spawn();
    }

    @Override
    @Deprecated
    public List<Unit> spawn(UnitsData data, DC_Player player, SPAWN_MODE mode) {
        if (player.isMe() &&   getGame().getMetaMaster() != null) {


            List<String> list = new ArrayList<>(EidolonLord.lord.getChain().getHeroNames());
            List<Coordinates> coords = getPositioner().getPlayerPartyCoordinates(list);
            Iterator<Coordinates> iterator = coords.iterator();
//            coords.removeIf(c -> c == null); TODO concurrent mod ...
            for (Unit member : EidolonLord.lord.getChain().getHeroes()) {
                if (!iterator.hasNext()) {
                    main.system.auxiliary.log.LogMaster.log(1, "Spawn failed: Coordinates: " + coords +
                            "; Units" + list);
                    break;
                }
                Coordinates c = iterator.next();
                if (c == null) {
//                    c = (Coordinates) RandomWizard.getRandomListObject(coords);
                    c = coords.get(0);
                }
                member.setCoordinates(c, true);
                member.setConstructed(false);
                getGame().getState().addObject(member);
                member.setOriginalOwner(player);
                member.setOwner(player);
                member.setFacing(
                        getFacingAdjuster().getPartyMemberFacing(member));

                applyStartBonuses(member);
                //what else should be done to *spawn*?
            }
        } else {
           return  super.spawn(data, player, mode);
        }


        //so dungeon-units are spawned 'automatically' by DungeonBuilder...
        // what about future waves?
        // how to add them to a mission?
        // via encounters most likely... or unitGroups
        //TODO

        return null;
    }

    private void applyStartBonuses(Unit member) {
        member.addProperty(PROPS.INVENTORY, "Food");
        member.addProperty(PROPS.INVENTORY, "Food");
    }


//    public void addDungeonEncounter(Dungeon c_dungeon, MapBlock block, Coordinates c, ObjType type) {
//        Map<MapBlock, Map<Coordinates, ObjType>> map = specialEncounters.get(c_dungeon);
//        if (map == null) {
//            map = new HashMap<>();
//            specialEncounters.put(c_dungeon, map);
//        }
//        Map<Coordinates, ObjType> encounterMap = map.get(block);
//        if (encounterMap == null) {
//            encounterMap = new HashMap<>();
//            map.put(block, encounterMap);
//        }
//        encounterMap.put(c, type);
//
//    }


}
