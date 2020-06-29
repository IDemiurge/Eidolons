package main.content.enums;

import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 2/14/2017.
 */
public class DungeonEnums {

    public enum DUNGEON_GROUP {
        UNDERWORLD, ARCANE, UNDEAD, HUMAN, MISC,


    }

    public enum CELL_IMAGE {
        crypt,
        garden,
        ossuary,
        tiles,
        diamond,
        circle("cr"),
        star,
        cross,
        natural,
        ornate,
        octagonal("oct"),
        iron,
        rock,
        mossy,
        bare,
        ancient,
        carved,
        dark_star,
red_stone,
        //wood
        //bridge
        ;
        String name;

        CELL_IMAGE() {
            name = name();
        }

        CELL_IMAGE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    /**
     * Encapsulates Ambience, decor, illumination
     */
    public enum DUNGEON_STYLE {
       //DEMO_FILL_STYLE
        ROGUE,
        DWARF,
        SPIDER,

        CAVE,
        TELRAZI,
        MONASTERY,

        CRYPTS,
        PRISON,
        BASTION,

        NIGHTMARE,

        Knightly,
        Holy,
        Stony,
        Pagan,
        DarkElegance,
        PureEvil,
        Brimstone,
        Grimy,
        Somber,
        Arcane,
        Cold,
    }

    public enum DUNGEON_TAGS {
        INTERIOR, UNDERGROUND, SURFACE, NIGHT, PERMA_DUSK,
    }


    public enum LOCATION_TYPE {
        CEMETERY(true), CAVE, CRYPT,
       DUNGEON,        TOWER(33), TEMPLE(50),
        CASTLE(75),
        HOUSE(true),
        GROVE(true),DEN, RUIN(66),
        CAMP(true),  BARROW,
        SEWER, HELL, ASTRAL,
        HIVE, ;

        private boolean surface;
        private int surfaceChance;

        LOCATION_TYPE(int surfaceChance) {
            this.surfaceChance = surfaceChance;
        }

        LOCATION_TYPE(boolean surface) {
            this.surface = surface;
        }

        LOCATION_TYPE() {
        }

        public boolean isSurface() {
            if (surfaceChance!=0)
                return RandomWizard.chance(surfaceChance);
            return surface;
        }

        public void setSurface(boolean surface) {
            this.surface = surface;
        }

        public LOCATION_TYPE_GROUP getGroup() {
            switch (this) {
                case BARROW:
                case CAVE:
                case DEN:
                case HIVE:
                    return LOCATION_TYPE_GROUP.NATURAL;
                case CRYPT:
                case TOWER:
                case SEWER:
                    return LOCATION_TYPE_GROUP.NARROW;

                case ASTRAL:
                case DUNGEON:
                case HELL:
                    return LOCATION_TYPE_GROUP.AVERAGE;

                case TEMPLE:
                case CASTLE:
                    return LOCATION_TYPE_GROUP.WIDE;

                case CAMP:
                case RUIN:
                case HOUSE:
                case GROVE:
                case CEMETERY:
                    return LOCATION_TYPE_GROUP.SURFACE;
            }
            return LOCATION_TYPE_GROUP.NARROW;
        }
    }

    public enum    LOCATION_TYPE_GROUP {
        SURFACE,
        WIDE,
        AVERAGE,
        NARROW,
        NATURAL,
        NATURAL_SURFACE,
    }

    public enum MAP_BACKGROUND {
        SHEOTH("big","death combat","sheoth.jpg"),
        PIRATE_BAY("big","Pirate Bay3.jpg"),
        DARK_FOREST("big","dark forest.jpg"),
        LABYRINTH("big","dungeons","labyrinth.jpg"),
        UNDERCITY("big","dungeons","undercity.jpg"),
        FORGOTTEN_CITY("big","dungeons","forgotten city.jpg"),
        MISTY_MOUNTAINS("big","dungeons","Misty Mountains.jpg"),
        DEAD_CITY("big","dungeons","dead city.jpg"),
        MANSION("big","dungeons","MANSION.jpg"),
        RUINS("big","dungeon","ruins.jpg"),
        CASTLE("big","dungeons","castle.jpg"),
        DARK_CASTLE("big","dungeons","dark castle.jpg"),

        // ARENA("big","Arena.jpg")
        // SANCTUARY("big","death combat","forgotten city.jpg"),
        // HIDDEN_CITY("big","death combat","Hidden City.jpg"),
        // PIRATE_COAST("big","death combat","Pirate Coast.jpg"),
        // SECRET_ENCLAVE("big","death combat","secret enclave.jpg"),
        // KYNTHOS("big","new","kynthos.jpg"),
        // FELMARSH("big","new","felmarsh.jpg"),
        // RAVENWOOD("big","new","ravenwood.jpg"),
        // NORDHEIM("big","new","nordheim.jpg"),
        // MISTY_MOUNTAINS("big","death combat","Misty Mountains Onyx Spire.jpg"),

        BASTION(PathFinder.getBgPicsPath(),"bastion.jpg"),
        BASTION_DARK(PathFinder.getBgPicsPath(),"bastion_1.1.png"),
        CAVE(PathFinder.getBgPicsPath(),"dungeon.png"),
        SHIP(PathFinder.getBgPicsPath(),"ship flip.jpg"),
        ELVEN_RUINS(PathFinder.getBgPicsPath(), "RUINS.jpg"),
        RAVENWOOD(PathFinder.getBgPicsPath(), "RAVENWOOD.png"),
        RAVENWOOD_EVENING(PathFinder.getBgPicsPath(), "RAVENWOOD evening.png"),
        TUNNEL(PathFinder.getBgPicsPath(), "Ironhelm Tunnel.png"),
        CEMETERY(PathFinder.getBgPicsPath(), "OLD CEMETERY.png"),
        TOWER(PathFinder.getBgPicsPath(), "moon valley.jpg"),

        SPIDER_GROVE(PathFinder.getBgPicsPath(), "spider grove.png"),
        ERSIDRIS(PathFinder.getBgPicsPath(), "ERSIDRIS.png")

        ;
        private final String backgroundFilePath;

        MAP_BACKGROUND(String... backgroundPathSegments) {
            this.backgroundFilePath = StrPathBuilder.build(backgroundPathSegments);
        }

        public String getBackgroundFilePath() {
            return backgroundFilePath;
        }

    }
//brush?
    public enum MAP_FILL_TEMPLATE {
        FOREST("Tree Sapling(2);Oak(2);Shrub(3);", "Forest Crags(1);Mossy Rocks(1);Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        SWAMP("Dead Tree(3);Forest Crags(1);Mossy Rocks(1);Tree Sapling(1); Shrub(1);", "Forest Crags(1);Mossy Rocks(1);Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        DARK_FOREST("Dead Tree(2);Tree Sapling(1);Oak(2);Shrub(3);", "Forest Crags(2);Mossy Rocks(1);Fallen Tree(2);Mossy Boulder(1);Sleek Rock(2);"),
        DEAD_FOREST("Dead Tree(3);Tree Sapling(1);Oak(1);Shrub(1);", "Forest Crags(1);Mossy Rocks(1);Tree Stump(2);Fallen Tree(2);Mossy Boulder(1);Sleek Rock(3);"),
        // RUINS("Tree Sapling(2);Oak(2);Shrub(3);",
        // "Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        // ROCKS("Tree Sapling(2);Oak(2);Shrub(3);",
        // "Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        ;
        private final String centerObjects;
        private final String peripheryObjects;

        MAP_FILL_TEMPLATE(String centerObjects, String peripheryObjects) {
            this.centerObjects = centerObjects;
            this.peripheryObjects = peripheryObjects;
        }

        public String getCenterObjects() {
            return centerObjects;
        }

        public String getPeripheryObjects() {
            return peripheryObjects;
        }
    }

    public enum SUBLEVEL_TYPE {
        COMMON, PRE_BOSS, BOSS, SECRET, TRANSIT, FALSE_LEVEL
    }

}
