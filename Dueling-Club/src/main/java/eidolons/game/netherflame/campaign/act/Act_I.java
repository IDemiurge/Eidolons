package eidolons.game.netherflame.campaign.act;

public class Act_I {
    //INK pipeline?

    // we could go with AV-based solution - filters
    public static final String[][] eidolon_hero_pools = {
            {""
            }
    };

    public enum QD_TYPE { //_ACT_I {
        Mistvale,
        Temple
    }

    //template that will help create them
    public enum ACT_I_FLOOR {
        /*
        Mistvale - [Gang, Magi, Fae Drug]

Floor 1: Wilderness - [grove, cemetery, ash fields,
Floor 2: Misty Ruins
Floor 3: Crypts - [ ] => Gang
Floor 4: Caverns - [crystal cave, brimstone cavern, acidic caves] => Drug
Floor 5: Undercity - [old sewers, slums, prison, ] => Magi


Temple - [Runes, Relic, Ritual]

Crags [Stone Maze, Misty Shore, Cliffs]
Courtyard [Antechamber, Pyre, Charred Wall
Forsaken Temple [Mess Hall, Chancel, Ossuary, Sanctuary] => Ritual
Elf Ruins - [Ruins, Secret Garden, Gateway Chambers] => Runes
Nether Realm => Relic
         */
    }

    public enum ACT_I_MODULES {
        //temple
        cliffs("Stone Maze;"),
        wind_shore,

        cloister,
        chapel,
        mess_hall,
        catacombs,

        nether_realm,

        //        old sewers, slums, prison
        //mistvale
        grove,
        mist_slums,
        burned_ruins,
        mist_cemetery,
        ash_field,

        crystal_cave,
        //        brimstone cavern, acidic caves
        elf_ruins,
        elf_temple,
        ;
        String[] module_names;

        ACT_I_MODULES(String... module_names) {
            this.module_names = module_names;
        }

/*
        size, placement order, zones,
        ai perks, encounter info,

        what part of it will be made by-hand?

        can we somehow use a hand-made template to... 'teach' a system to generate similar shit?
        > use obj-placement patterns
        > use room configs
        > placing scripts?
        entrance/exit
        module borders

         */

    }

    public enum SUBJECT_ACT_I {
        gang, fae_drug, rebel_magi,
        ritual, dark_relic, secret_runes,
        ;
    }


}
