package eidolons.game.battlecraft.logic.meta.igg.hero;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.game.module.herocreator.logic.party.Party;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChainParty extends Party {
    Set<Unit> deadHeroes = new LinkedHashSet<>();
    private int totalXp; //during this run; TODO - will it work with transits?
    private int goldStashed;
    private int deathTaxPerc = 35;

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
        if (deadHeroes.isEmpty())
            return;

        String msg = hero + " inherits " + goldStashed +
                " Gold!";
        hero.addParam(PARAMS.GOLD, goldStashed);
        EUtils.showInfoText(msg);
        getLeader().getGame().getLogManager().log(msg);
        goldStashed = 0;
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
     *
     * @param xp
     */
    public void xpGained(int xp) {
        totalXp += xp;
        for (Unit deadHero : deadHeroes) {
            deadHero.xpGained(xp);
        }
    }

    public void death() {
        deadHeroes.add(getLeader());
        goldStashed = getLeader().getIntParam(PARAMS.GOLD);
        int deathTax = goldStashed * deathTaxPerc / 100;
        deathTax += goldStashed * RandomWizard.getRandomInt(deathTaxPerc) / 100;
        goldStashed -= deathTax;

        String msg = "Death claims " + deathTax +
                " Gold!";
        EUtils.showInfoText(msg);
        getLeader().getGame().getLogManager().log(msg + " Inventory items lay still among the ashes of " + getLeader().getName());
    }
}
