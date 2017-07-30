package main.game.battlecraft.logic.meta.party.warband;

import main.client.cc.logic.party.PartyObj;
import main.game.battlecraft.logic.meta.party.FormationMaster.FORMATION;

import java.util.Map;

/**
 * Created by JustMe on 7/29/2017.
 */
public class Warband {
    Map<FORMATION, PartyObj> formationsMap;

    public Warband(){
    }

    public Map<FORMATION, PartyObj> getFormationsMap() {
        return formationsMap;
    }
}
