package eidolons.game.battlecraft.logic.meta.tutorial;

import eidolons.client.cc.logic.party.Party;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TutorialPartyManager extends PartyManager<TutorialMeta> {
    private static final java.lang.String TUTORIAL_PARTY = "Demo Party";//"Tutorial Party";

    public TutorialPartyManager(TutorialMetaMaster tutorialMetaMaster) {
        super(tutorialMetaMaster);
    }

    @Override
    public Party initPlayerParty() {
        ObjType type = DataManager.getType(TUTORIAL_PARTY, DC_TYPE.PARTY);
        party = new Party(type);

        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));
        return party;

    }
}
