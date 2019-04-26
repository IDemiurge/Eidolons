package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * this is the thing to be constructed from save data?
 *
 */
public class IGG_Meta extends MetaGame {

    private int missionIndex;
    private HeroChain chain;
IGG_Mission mission;

    public IGG_Meta(MetaGameMaster master) {
        super(master);
    }

    public HeroChain getHeroChain() {
        return chain;
    }
}
