package system.scripts;

/**
 * Created by Alexander on 12/7/2022
 */

import main.system.auxiliary.EnumMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static system.scripts.HeroAlgo.Hero;

public class HeroAlgo {
    public static final int ARCH_HERO = 1;
    public static final int ARCH_CREATOR = 2;
    public static final int ARCH_MAGE = 3;
    public static final int ARCH_CARER = 4;
    public static final int ARCH_KILLER = 5;
    public static final int ARCH_WANDERER = 6;
    public static final int SLOTS = 6;

    /*
    Knight | Chaos | Wizard
    Dwarf | Necromancer | Merc
    Knight | Chaos | Seer

    Highlander , Valkyrie , Wizard
    Knight , Chaos , Wizard
    Dwarf , Anubis , Merc
    Knight , Chaos , Seer
    Assassin , Seer , Hunter
    Merc , Alchemist , Artisan

    Dwarf , Knight , Artisan
    Wizard , Assassin , Alchemist
    Occult , Cleric , Hunter
    Assassin , Hermit , Alchemist
    Hunter , Anubis , Hermit
    Cleric , Fel , Assassin
    Highlander , Thief , Seer
    Valkyrie , Fel , Hermit
    Highlander , Artisan , Valkyrie
    Dwarf , Thief , Arcanist

     */
    public enum Hero {
        Knight("Cleric", "Occultist", "Mercenary", 1, 1),
        Assassin("Thief", "Occultist", "Chaos", 1, 1),
        Hunter("hero1", "hero1", "hero1", 1, 1),
        Cleric("hero1", "hero1", "hero1", 1, 1),
        Geomancer("hero1", "hero1", "hero1", 1, 1),
        Occultist("hero1", "hero1", "hero1", 1, 1),

        Valkyrie("hero1", "hero1", "hero1", 1, 1),
        Guardian("hero1", "hero1", "hero1", 1, 1),
        Mercenary("hero1", "hero1", "hero1", 1, 1),
        Arcanist("hero1", "hero1", "hero1", 1, 1),
        Wizard("hero1", "hero1", "hero1", 1, 1),
        Thief("hero1", "hero1", "hero1", 1, 1),

        Highlander("hero1", "hero1", "hero1", 1, 1),
        Chaos("hero1", "hero1", "hero1", 1, 1),
        Druid("hero1", "hero1", "hero1", 1, 1),
        Seer("hero1", "hero1", "hero1", 1, 1),
        Anubis("hero1", "hero1", "hero1", 1, 1),
        Alchemist("hero1", "hero1", "hero1", 1, 1),
        ;
        public final int archetype;
        public final int archetype2;
        public List<Hero> links;

        Hero(String link1, String link2, String link3, int archetype, int archetype2) {
            this.archetype = archetype;
            this.archetype2 = archetype2;
            links = new ArrayList<>(3);
            Hero hero = new EnumMaster<Hero>().retrieveEnumConst(Hero.class, link1);
            if (hero == null) {
                throw new RuntimeException(link1);
            }
            links.add(hero);
            hero = new EnumMaster<Hero>().retrieveEnumConst(Hero.class, link2);
            if (hero == null) {
                throw new RuntimeException(link2);
            }
            links.add(hero);
            hero = new EnumMaster<Hero>().retrieveEnumConst(Hero.class, link3);
            if (hero == null) {
                throw new RuntimeException(link3);
            }
            links.add(hero);
        }
    }

    public static void main(String[] args) {
        List<Hero> heroes = new ArrayList<>(Arrays.asList(Hero.values()));
/*
we need to check if Archetypes are viable
1) Find all viable combinations of 6 heroes in 6 slots for these 18
 */


        //assemble the next random combination by brute force
        List<Hero> slots = new ArrayList<>(6);
        for (int slot = 0; slot < SLOTS; slot++) {
            for (Hero hero : heroes) {
                if (slots.contains(hero))
                    continue;
                // if (global) //TODO
                //     continue;
                slots.add(hero);
            }
        }

        for (int slot = 0; slot < SLOTS; slot++) {
            for (Hero hero : slots) {
                if (hero.archetype == slot || hero.archetype2 == slot) {

                }
            }
        }
        
        /*
For picked 3 slots, give 3 random heroes that can each merge with at least one other.
Then feign drafting
         */
    }
}






















