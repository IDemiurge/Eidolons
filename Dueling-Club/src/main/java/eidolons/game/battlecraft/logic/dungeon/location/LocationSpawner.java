package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.test.PresetMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.data.DataUnitFactory;

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
    public List<Unit> spawn(UnitData data, DC_Player player, SPAWN_MODE mode) {
        if (data.getValue(PARTY_VALUE.MEMBERS) != null) {
            String units = data.getValue(PARTY_VALUE.MEMBERS).
                    replace(DataUnitFactory.getContainerSeparator(UnitData.FORMAT), "");
            if (FileManager.isFile(units))
                return spawnUnitGroup(player.isMe(), units);
        }
        if (player.isMe() && PresetMaster.getPreset() == null && getGame().getMetaMaster() != null) {
            Party party = getGame().getMetaMaster().getPartyManager()
                    .getParty();
            if (party == null) {
                return new LinkedList<>();
            }
            List<String> list = ListMaster.toNameList(
                    party.getMembers());
            getPositioner().setMaxSpacePercentageTaken(50);
            List<Coordinates> coords = getPositioner().getPlayerPartyCoordinates(list);
            Iterator<Coordinates> iterator = coords.iterator();

            for (Unit member : party.getMembers()) {
                if (!iterator.hasNext()) {
                    main.system.auxiliary.log.LogMaster.log(1, "Spawn failed: Coordinates: " + coords +
                            "; Units" + list);
                    break;
                }
                member.setCoordinates(iterator.next());
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
            super.spawn(data, player, mode);
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


}