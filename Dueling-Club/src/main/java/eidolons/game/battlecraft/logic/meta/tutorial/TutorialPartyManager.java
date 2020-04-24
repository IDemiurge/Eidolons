package eidolons.game.battlecraft.logic.meta.tutorial;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.module.herocreator.logic.party.Party;
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
        getGame().getDataKeeper().addUnitData(new UnitsData(party));
        return party;

    }

    @Override
    protected Unit findMainHero() {
        return null;
    }
}
