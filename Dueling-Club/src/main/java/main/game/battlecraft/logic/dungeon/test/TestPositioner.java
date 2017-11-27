package main.game.battlecraft.logic.dungeon.test;

import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import main.game.battlecraft.logic.dungeon.universal.Positioner;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.bf.Coordinates;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/10/2017.
 */
public class TestPositioner<E extends DungeonWrapper> extends Positioner<E> {
    public TestPositioner(DungeonMaster master) {
        super(master);
    }

    @Override
    public Coordinates getEnemySpawningCoordinates() {
        return super.getEnemySpawningCoordinates();
    }

    public List<Coordinates> initPartyCoordinates(List<String> partyTypes,
                                                  Boolean mine_enemy_third) {

        String partyData = "";
//        getPositioner().setMaxSpacePercentageTaken(MAX_SPACE_PERC_PARTY);
        List<Coordinates> coordinates = null;

        if (PartyHelper.getParty() != null) {
            if (MapMaster.isNotEmpty(PartyHelper.getParty().getPartyCoordinates())) {
                coordinates = new ArrayList<>(PartyHelper.getParty().getPartyCoordinates()
                 .values());
                partyTypes = ListMaster.toNameList(PartyHelper.getParty().getPartyCoordinates()
                 .keySet());
            }

        }
        if (coordinates == null) {
            coordinates =  getPartyCoordinates(null, BooleanMaster
             .isTrue(mine_enemy_third), partyTypes);
        }

        int i = 0;

        for (String subString : partyTypes) {
            Coordinates c = coordinates.get(i);
            if (c == null) {
                LogMaster.log(1, subString + " coordinate BLAST!!!");
            }
            i++;
            subString = c + DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR + subString;
            partyData += subString + DC_ObjInitializer.OBJ_SEPARATOR;
            //TODO string not needed?
        }


        return coordinates;
    }
}
