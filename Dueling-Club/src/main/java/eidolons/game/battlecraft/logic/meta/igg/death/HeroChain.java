package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.game.module.herocreator.logic.party.Party;
import main.entity.type.ObjType;

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

    public HeroChain(Party party) {
        this.party = party;
        types = party.getMembers().stream().map(member -> member.getType()).collect(Collectors.toList());
        heroes = party.getMembers().stream().map(member -> new ChainHero(member)).collect(Collectors.toList());
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
