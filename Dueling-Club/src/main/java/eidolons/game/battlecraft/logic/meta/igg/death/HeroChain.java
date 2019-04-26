package eidolons.game.battlecraft.logic.meta.igg.death;

import java.util.List;

/**
 * so it's replacing the PARTY?
 * no it's created on top of it
 */
public class HeroChain {
    List<ChainHero> heroes;

    public int getLivesPerMission() {
        return 1;
    }

    public HeroChain(List<ChainHero> heroes) {
        this.heroes = heroes;
    }

    public boolean isFinished() {
        return false;
    }
}
