package main.game.battlecraft.logic.meta.scenario;

import main.client.cc.logic.party.PartyObj;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioPartyManager extends PartyManager<ScenarioMeta> {
    public ScenarioPartyManager(ScenarioMetaMaster master) {
        super(master);
    }

    @Override
    public PartyObj initPlayerParty() {
        //preset
        //choice
        //already as Unit?
        ObjType type = getMetaGame().getScenario().getPartyType();
        if (type==null ){
            //new ? choice?
        }
        PartyObj partyObj = new PartyObj(type);
        DC_Game.game.getState().addObject(partyObj);
        return partyObj;

    }

}

