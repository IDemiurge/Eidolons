package main.content.enums.rules;

import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class ArcadeEnums {
    public enum ARCADE_LOOT_TYPE { // should implement VarHolder and have
        // some args I suppose
        JEWELRY(LOOT_GROUP.JEWELRY, LOOT_GROUP.JEWELRY_SPECIAL),
        MAGIC_CHEST(LOOT_GROUP.MAGIC_ITEMS, LOOT_GROUP.MAGIC_ITEMS, LOOT_GROUP.MAGIC_ITEMS,

         LOOT_GROUP.JEWELRY_SPECIAL),
        TREASURE_CHEST,
        MAGIC_ITEM,

        WAR_GEAR,
        GOLD_STASH,
        GLORY,

        CAGED_BEASTS,
        PRISONERS, // good idea, free some prisoners, man up
        // some deserters, gain gratitude of the
        // hardy townsfolk...
        ;
        private LOOT_GROUP[] groups;

        ARCADE_LOOT_TYPE(LOOT_GROUP... groups) {
            this.groups = groups;
        }
    }

    // What is the disadvantage of using Enums over Types, why not?
    public enum ARCADE_REGION {
        // ++ general background picture
        DUSK_DALE(5, 6, "Cemetary;Bandit Camp;Crystal Cave;Ancient Crypt;Dungeon;"),
        // >> Ancient Crypt -
        // *Spec Loot: ancient magical items // jewelry
        // >> Ruined Fort - deserters, necro/demon cultists, fallen knight boss
        // *Spec Loot: fair weaponry or magical items
        // >> Dungeon - goblins, kobolds,
        // *Spec Loot: poison flasks
        GREYLEAF_WOODS(3, 4, "Ruined Fort;Demon Shrine;Forlorn Ruins;Plague House;Cavern;"),
        // *Spec Loot: glory
        // >> Forlorn Ruins
        // *Spec Loot: random
        // ++ Demon Shrine, Ancient Crypt
        WRAITH_MARSHES(3, 4, "Abandoned Sewer;Serpent Lair;Drowned Tomb;Haunted Mansion;Dwarven Halls;Ship Graveyard"),
        // *Spec Loot: poison flasks
        // >> Cavern
        // *Spec Loot: poison flasks
        // >> Haunted Mansion - warlocks
        // Shipwreck
        MISTY_SHORES(3, 4, "Dark-Reef Isle;Dark Castle;Arcane Tower;Elementum;Lichyard;Smuggler Grottos"),
        // ++ naga
        // >> Dwarven Halls - dark dwarves // orcs and trolls
        // *Spec Loot: magic metal items
        // >> Elementum -

        BLIGHTSTONE_DESERT(1, 2, "Undercity;Sanctuary;Crystal Chamber;Abyss;Doom Forge"),;
        // CITY OF ANCIENTS
        // EXCAVATION
        // MONOLITH

        // TODO perhaps *here* we need a dungeon sequence! just 1 final dungeon
        // might seem little!
        // keep-forge-core, mausoleum-undercity-crystal chamber,
        // Blightstone Desert (1/2 to loot), (level 12+)
        // >> Crystal Chamber- [great demons]||wraiths, void servants, archangel
        // Final Prize: Crystal Shard (Aspect?)
        // >> Undercity - undead, dungeon beasts, monstrous insects, black
        // dragon
        // Final Prize: Void Mask
        // >> Sanctuary- witch spawn, arcane guardians, elementals
        // Final Prize: Aether Cloak
        // >> Mechanicum Core// <?>
        // Final Prize: Warp Ring
        int minToLoot;
        int minPoolSize;
        String dungeonPool;

        ARCADE_REGION(int minToLoot, int minPoolSize, String dungeons) {
            this.minPoolSize = minPoolSize;
            this.dungeonPool = dungeons;
            this.minToLoot = minToLoot;

        }

        // shop "level" - materials, qualities, special items *level*
        public String toString() {
            return StringMaster.getWellFormattedString(name());

        }

        public ARCADE_REGION getNext() {
            int i = new ListMaster<ARCADE_REGION>().getList(values()).indexOf(this);
            i++;
            if (values().length == i) {
                return null;
            }
            return values()[i];
        }

        public int getMinToLoot() {
            return minToLoot;
        }

        public int getMinPoolSize() {
            return minPoolSize;
        }

        public String getDungeonPool() {
            return dungeonPool;
        }

    }

    // via *Quests*?
    public enum ARCADE_REWARD_TYPE {

        VOLUNTEERS, HONOR_GUARD, GOLD, GLORY, DISCOUNTS,

    }

    // the
    public enum ARCADE_ROUTE {
        WILD_PATH(75, 1, "Dire Grizzly;Warg Pack;", "Wolf Pack;Feline Predators", ""),
        PLAINS_OF_DESOLATION(50, 1, "Naga Patrol;Mutant Pack;", "", ""),
        DARK_PATH(75, 1, "Wolf Pack;", "Night Terrors", ""),
        HIGH_ROAD(35, 1, "Robber Band;", "Sharadrim Squad", ""),

        MURKY_GROVE(50, 1, "Naga Patrol;Mutant Pack;", "", ""),
        MISTVEILED_TRAIL(65, 1, "Naga Patrol;Mutant Pack;", "", ""),

        EERIE_COAST(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        ROCKY_CLIMB(65, 1, "Griff;Ogre;", "Goblin Band;Orc Band;", ""),
        SHALLOW_FORD(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        SWAMPY_TRAIL(65, 1, "Naga Patrol;Mutant Pack;", "", ""),

        BLACK_SANDS(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        DEFILED_OASIS(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        PLAINS_OF_DEATH(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        SULFUROUS_WASTES(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        CANYON(65, 1, "Naga Patrol;Mutant Pack;", "", ""),;
        // TODO player should have at least 2 choices, right?
        // Day/Night should be used for ALTERNATION!
        // basically just counting by %n_of_dungeons_completed
        int danger;
        int length;
        String encounterPool;
        String altEncounterPool;

        // time will just consume gold I suppose? :)

        ARCADE_ROUTE(int danger, int length, String encounterPool, String altEncounterPool,
                     String imgPath) {

        }

        public int getDanger() {
            return danger;
        }

        public int getLength() {
            return length;
        }

        public String getEncounterPool() {
            return encounterPool;
        }

        public String getAltEncounterPool() {
            return altEncounterPool;
        }

    }

    public enum LOOT_GROUP {

        AMMO, POTIONS, POISONS, CONCONCTIONS,

        JEWELRY, JEWELRY_SPECIAL,

        WEAPONS, ARMOR,

        LIGHT_WEAPONS, LIGHT_ARMOR, HEAVY_WEAPONS, HEAVY_ARMOR,

        WEIRD_WEAPONS, WEIRD_ARMOR, WEIRD_ITEMS, // ANCIENT/DAMAGED + weird
        // materials

        MAGIC_ITEMS,
        MAGIC_ARMOR,
        MAGIC_WEAPONS,
        MAGIC_MATERIAL_WEAPONS,;

        // materials/ench

    }

    public enum LOOT_TYPE {
        COMMON,

    }
}
