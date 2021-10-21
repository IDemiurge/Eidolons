package eidolons.game.eidolon.chain;

import eidolons.entity.obj.unit.Unit;
import eidolons.entity.obj.unit.netherflame.TrueForm;
import main.entity.type.ObjType;

import java.util.List;
import java.util.Map;

public class EidolonChain {
    Map<Integer, Eidolon> map;
    private List<Unit> heroes;
    private List<String> heroNames;
    private TrueForm hero;

    public EidolonChain(TrueForm hero, ObjType type) {
    /*
    do we really need an ObjType for Chain?
    apart from heroes, what's it got?
     */
        this.hero = hero;
    }


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
