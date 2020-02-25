package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.party.Party;
import main.entity.type.ObjType;
import main.system.launch.CoreEngine;

import java.util.List;
import java.util.stream.Collectors;

/**
 * so it's replacing the PARTY?
 * no it's created on top of it
 */
public class HeroChain {
    private final List<ObjType> types;
    List<ChainHero> heroes;
    Party party;

    public HeroChain(Party party, int lives) {
        this.party = party;
        types = party.getMemberTypes();
//        party.initMembers();
        heroes = party.getMembers().stream().map(member -> new ChainHero(lives, member)).collect(Collectors.toList());
//        heroes = party.getMemberTypes().stream().map(member -> new ChainHero(lives, member)).collect(Collectors.toList());
    }

    public void spawned(Unit hero) {
        if (CoreEngine.isActiveTestMode()) {

        }
    }

    public Party getParty() {
        return party;
    }

    public void death() {
        findHero(party.getLeader().getName()).death();
    }

    public List<ObjType> getTypes() {
        return types;
    }

    public List<ChainHero> getHeroes() {
        return heroes;
    }

    public int getLivesPerMission() {
        return 1;
    }


    public boolean isFinished() {

        for (ChainHero hero : heroes) {
            if (hero.getLives() > 0)
                return false;
        }

        return true;
    }

    public ChainHero findHero(String name) {
        for (ChainHero hero : heroes) {
            if (hero.getType().getName().equalsIgnoreCase(name)) {
                return hero;
            }
        }
        return null;
    }

}
