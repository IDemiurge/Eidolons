package eidolons.game.netherflame.igg;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.SaveHandler;

/**
 * support versions?
 *
 * how to support updates ?
 * what can change in an update?
 *
 * XML
 * Resources
 * Jar (code)
 *
 * little chance there!
 *
 */
public class IGG_SaveMaster extends SaveHandler<IGG_Meta> {

    public IGG_SaveMaster(MetaGameMaster master) {
        super(master);
    }

    public IGG_Meta restore(){
        getMaster().getPartyManager().getParty().setProperty(PROPS.PARTY_MISSION,
                getMetaGame().getMission().getName());

        return null;
    }

}
