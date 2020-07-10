package eidolons.game.netherflame.main.solo;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.module.cinematic.CinematicLib;
import eidolons.game.netherflame.main.NF_PartyManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

public class SoloPartyManager extends NF_PartyManager {
    public static final boolean TEST_MODE = true;

    public SoloPartyManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    public boolean deathEndsGame() {
        return true;
    }

    @Override
    protected ObjType getPartyType() {
        return DataManager.getType("Anphis", DC_TYPE.PARTY);
    }

    @Override
    protected Coordinates getRespawnCoordinates(ObjType type) {
        return super.getRespawnCoordinates(type);
    }

    @Override
    public boolean heroUnconscious(Unit unit) {
        //instead of all unconscious mechanics
        CinematicLib.run(CinematicLib.StdCinematic.UNCONSCIOUS_BEFORE);

        Coordinates respawnCoordinates = getRespawnCoordinates(null);
        Unit hero = Eidolons.getMainHero();
        hero.setCoordinates(respawnCoordinates);
        hero.cleanReset();
        getGame().getMovementManager().moved(hero, true);

        CinematicLib.run(CinematicLib.StdCinematic.UNCONSCIOUS_AFTER, hero);
        return true;
    }
}
