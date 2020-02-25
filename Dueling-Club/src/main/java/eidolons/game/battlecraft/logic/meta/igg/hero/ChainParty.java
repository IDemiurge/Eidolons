package eidolons.game.battlecraft.logic.meta.igg.hero;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;

import java.util.*;
import java.util.stream.Collectors;

public class ChainParty extends Party {
    private  String leaderName;
    Set<Unit> deadHeroes = new LinkedHashSet<>();
    private int totalXp; //during this run; TODO - will it work with transits?
    private int goldStashed;
    private int deathTaxPerc = 35;

    Set<DC_HeroItemObj> stash = new LinkedHashSet<>(); //TODO
    private Unit lastHero;

    public ChainParty(ObjType type, String selectedHero) {
        super(type);
        leaderName = selectedHero;
        initMembers();

    }

    @Override
    public void initMembers() {
        if (leaderName == null) {
            return;
        }
        super.initMembers();
        for (Unit member : members) {
            if ( member.getName().equalsIgnoreCase(leaderName)){
                setLeader(member);
            }else {
                member.setPale(true);
            }

        }
    }

    @Override
    public String getMemberString() {
        return super.getMemberString();
    }

    public Set<Unit> getDeadHeroes() {
        return deadHeroes;
    }

    public Set<DC_HeroItemObj> getStash() {
        return stash;
    }

    public Unit getLastHero() {
        return lastHero;
    }

    @Override
    public void addMember(Unit hero) {
        super.addMember(hero);
//        if (!isSpawnedAlive(hero)) {
//            hideHero(hero);
//        }

        /**
         * now we just add xp at once!
         *
         * but what about gold and the like?
         */
//        hero.xpGained(totalXp);
//        if (deadHeroes.isEmpty())
//            return;

        if (lastHero == null) {
            return;
        } //TODO load?
        String msg = hero + " recovers " + goldStashed +
                " gold from " +
                lastHero.getName() +
                "'s ashes...";
        getLeader().getGame().getLogManager().log(msg);
        EUtils.showInfoText(msg);
        for (DC_HeroItemObj item : stash) {
            msg = hero + " recovers " + item.getName()+"from " +
            lastHero.getName() +
                    "'s ashes...";

            hero.getGame().getLogManager().log(msg);
            hero.addItemToInventory(item);
        }
        stash.clear();
        hero.addParam(PARAMS.GOLD, goldStashed);
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
     * @param xp
     */
    public void xpGained(int xp) {
        totalXp += xp;
        for (Unit deadHero : deadHeroes) {
            deadHero.xpGained(xp);
        }
    }

    public void death() {
         lastHero = getLeader();
        Collection<? extends DC_HeroItemObj> stashed = getStashedItems(lastHero.getInventory());
         stash.addAll(stashed);

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
    private boolean isStashed(DC_HeroItemObj t) {
        if (t.getProperty(G_PROPS.ITEM_GROUP).equalsIgnoreCase("keys")) {
            return true;
        }
        return false;
    }

    private Collection<? extends DC_HeroItemObj> getStashedItems(DequeImpl<DC_HeroItemObj> inventory) {
        return inventory.stream().filter(t -> isStashed(t)).collect(Collectors.toList());
    }

}
