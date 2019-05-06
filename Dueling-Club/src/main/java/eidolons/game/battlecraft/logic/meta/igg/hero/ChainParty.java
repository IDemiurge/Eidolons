package eidolons.game.battlecraft.logic.meta.igg.hero;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.game.module.herocreator.logic.party.Party;
import main.entity.type.ObjType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChainParty extends Party {
    Set<Unit> deadHeroes = new LinkedHashSet<>();
    private int totalXp; //during this run; TODO - will it work with transits?

    public ChainParty(ObjType type, String selectedHero) {
        super(type);

        addMember(HeroCreator.initHero(selectedHero));
    }

    @Override
    public void initMembers() {
    }

    @Override
    public String getMemberString() {
        return super.getMemberString();
    }

    @Override
    public void addMember(Unit hero) {
        super.addMember(hero);
//        if (!isSpawnedAlive(hero)) {
//            hideHero(hero);
//        }
        hero.xpGained(totalXp);
    }

    private void hideHero(Unit hero) {
        hero.setHidden(true);
        hero.kill();

    }

    private boolean isSpawnedAlive(Unit hero) {
        return hero.isLeader();
    }

    /**
     * grant this amount to newly spawned heroes
     * the dead heroes will also receive it here
     * @param xp
     */
    public void xpGained(int xp) {
        totalXp+=xp;
        for (Unit deadHero : deadHeroes) {
            deadHero.xpGained(xp);
        }
    }

    public void death() {
        deadHeroes.add(getLeader());

    }
//        public void newHero(Unit hero) {
//    }
}
