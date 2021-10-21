package eidolons.game.eidolon.chain;

import eidolons.entity.obj.unit.Unit;

import java.util.List;
import java.util.Map;

public class EidolonChain {
    Map<Integer, Eidolon> map;
    private List<Unit> heroes;
    private List<String> heroNames;


    public List<Unit> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Unit> heroes) {
        this.heroes = heroes;
    }

    public List<String> getHeroNames() {
        return heroNames;
    }

    public void setHeroNames(List<String> heroNames) {
        this.heroNames = heroNames;
    }
}
