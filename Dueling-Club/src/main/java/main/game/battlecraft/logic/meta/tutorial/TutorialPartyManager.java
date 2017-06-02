package main.game.battlecraft.logic.meta.tutorial;

import main.client.cc.logic.party.PartyObj;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.meta.universal.PartyManager;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TutorialPartyManager extends PartyManager<TutorialMeta> {
    private static final java.lang.String TUTORIAL_PARTY = "Tutorial Party";

    public TutorialPartyManager(TutorialMetaMaster tutorialMetaMaster) {
        super(tutorialMetaMaster);
    }
    @Override
    public PartyObj initPlayerParty() {
        ObjType type = DataManager.getType(TUTORIAL_PARTY, DC_TYPE.PARTY);
        party = new PartyObj(type);

        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));
        return party;

    }
}
