package main.game.battlecraft.logic.meta.scenario;

import main.client.cc.logic.party.PartyObj;
import main.content.PROPS;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.meta.universal.PartyManager;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioPartyManager extends PartyManager<ScenarioMeta> {

    public ScenarioPartyManager(ScenarioMetaMaster master) {
        super(master);
    }

    @Override
    public void gameStarted() {
//  TODO       getData()
        party.setProperty(PROPS.PARTY_MISSION,
     StringMaster.openContainer(getMetaGame().getScenario().
      getProperty(PROPS.SCENARIO_MISSIONS)).get(0), true);
    }

    @Override
    public PartyObj initPlayerParty() {
        //preset
        //choice
        //already as Unit?
        ObjType type = getMetaGame().getScenario().getPartyType();
        if (type == null) {
            //new ? choice?
        }
        party = new PartyObj(type);
        if (party.getNextMission().isEmpty()) {
            party.setProperty(PROPS.PARTY_MISSIONS_NEXT,
             StringMaster.openContainer(getMetaGame().getScenario().
              getProperty(PROPS.SCENARIO_MISSIONS)).get(0), true);
        }
        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));
        return party;

    }

}

