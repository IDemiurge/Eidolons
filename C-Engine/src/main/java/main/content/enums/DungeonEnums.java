package main.content.enums;

import main.content.enums.entity.OBJ_TYPE_ENUM;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 2/14/2017.
 */
public class DungeonEnums {
    public enum DUNGEONS_OBJ_TYPES implements OBJ_TYPE_ENUM {
        CAVERN, RAVENWOOD,
        RUINED_FORT, ARCANE_TOWER;
    }

    public enum DUNGEON_CLASS {
        CAVE,
        DUNGEON,
        BUILDING,
        SURFACE,
        FOREST,
    }

    public enum DUNGEON_DIFFICULTY {

    }

    public enum DUNGEON_GROUP {
        UNDERWORLD, ARCANE, UNDEAD, HUMAN, MISC
    }

    public enum DUNGEON_LEVEL {
        ONE, TWO, THREE, FOUR, FIVE
    }

    public enum DUNGEON_MAP_MODIFIER {
        CULTIST_HIDEOUT("Altar(2)", MAP_BACKGROUND.CAVE),
        HALL("Marble Column(3);", MAP_BACKGROUND.DARK_CASTLE),
        GRAVEYARD("Gravestone(4);"
         // "Desecrated Grave(2);Fresh Grave(2);Gravestone(2);Statue(2);"
         , MAP_BACKGROUND.CEMETERY),
        RUINS("Ruined Structure(2);Ruined Column(2);Ruined Gateway(2)", MAP_BACKGROUND.RUINS),
        DEEP_WOOD("Shrub(2);Ancient Oak(3);", MAP_BACKGROUND.RUINS),
        ROCKY("Mossy Boulder(2);Sleek Rock(2);", MAP_BACKGROUND.SHEOTH),
        CAVERN("Stalactite(2);Stalagmite(3);", MAP_BACKGROUND.CAVE),;
        private String objects;
        private MAP_BACKGROUND bg;
        private String presetObjects;

        DUNGEON_MAP_MODIFIER(String objects, MAP_BACKGROUND bg, String presetObjects) {
            this.objects = objects;
            this.bg = bg;
            this.presetObjects = presetObjects;
        }

        DUNGEON_MAP_MODIFIER(String objects, MAP_BACKGROUND bg) {
            this(objects, bg, null);
        }

        public String getObjects() {
            return objects;
        }

        public String getBackground() {
            return bg.getBackgroundFilePath();
        }

        public MAP_BACKGROUND getBg() {
            return bg;
        }

        public String getPresetObjects() {
            return presetObjects;
        }
    }

    public enum DUNGEON_MAP_TEMPLATE {
        COAST("Water(4);", MAP_BACKGROUND.PIRATE_BAY), // one side cut off water
        // halfway thru?
        PLAINS("Shrub(1);Mossy Boulder(2);Oak(2);", MAP_BACKGROUND.CAVE),
        SWAMP("Fallen Tree(1);Sleek Rock(2);Dead Tree(3);", MAP_BACKGROUND.CAVE),

        FOREST("Tree Sapling(2);Oak(2);Fallen Tree(1);Shrub(3);Mossy Boulder(1);Sleek Rock(1);", MAP_BACKGROUND.CAVE),
        DEAD_FOREST("Fallen Tree(1);Shrub(2);Mossy Boulder(1);Sleek Rock(1);Dead Tree(4);", MAP_BACKGROUND.DARK_FOREST),
        DARK_FOREST("Fallen Tree(1);Shrub(2);Mossy Boulder(1);Sleek Rock(1);Oak(2);Dead Tree(3);", MAP_BACKGROUND.DARK_FOREST),
        CEMETARY("Gravestone(4);", MAP_BACKGROUND.CEMETERY),
        CEMETARY_WOODS("Gravestone(2);Sleek Rock(1);Mossy Boulder(1);Dead Tree(2);", MAP_BACKGROUND.CEMETERY),
        DUNGEON_HALL("Marble Column(4);", MAP_BACKGROUND.FORGOTTEN_CITY),

        DUNGEON("Stalactite(3);Stalagmite(4);", MAP_BACKGROUND.CAVE),

        HALL_MAGE("Marble Column(6);", MAP_BACKGROUND.FORGOTTEN_CITY),
        HALL_DARK("Ruined Column(3);Ruined Gateway(2);Ruined Structure(2);", MAP_BACKGROUND.DARK_CASTLE),
        CAMP("Fallen Tree(2);Barricade(3);", MAP_BACKGROUND.PIRATE_BAY),;
        private String objects;
        private MAP_BACKGROUND bg;

        DUNGEON_MAP_TEMPLATE(String objects, MAP_BACKGROUND bg) {
            this.objects = objects; // (x) - minimum x/2, max x.
            this.bg = bg;
        }

        public String getObjects() {
            return objects;
        }

        public String getBackground() {
            return bg.getBackgroundFilePath();
        }

        public MAP_BACKGROUND getBg() {
            return bg;
        }

    }


    public enum DUNGEON_POPULATION {
        Cult,
    }

    /**
     * Encapsulates Ambience, decor, illumination
     */
    public enum DUNGEON_STYLE {
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

    public enum DUNGEON_SUBFOLDER {
        // ARCADE,
        BATTLE,
        DEMO,
        CAMPAIGN,
        CRAWL,
        SKIRMISH,
        SUBLEVELS,
        TEST,
    }

    public enum DUNGEON_TAGS {
        INTERIOR, UNDERGROUND, SURFACE, NIGHT, PERMA_DUSK,
    }

    public enum DUNGEON_TYPE {

        GLORY, TREASURE_HOARD, SUBLEVEL, BOSS, MAIN,
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

    public enum
    LOCATION_TYPE_GROUP {
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
        CAVE(PathFinder.getBgPicsPath(),"ship flip.jpg"),
        SHIP(PathFinder.getBgPicsPath(),"dungeon.png"),
        ELVEN_RUINS(PathFinder.getBgPicsPath(), "RUINS.jpg"),
        RAVENWOOD(PathFinder.getBgPicsPath(), "RAVENWOOD.png"),
        RAVENWOOD_EVENING(PathFinder.getBgPicsPath(), "RAVENWOOD evening.png"),
        TUNNEL(PathFinder.getBgPicsPath(), "Ironhelm Tunnel.png"),
        CEMETERY(PathFinder.getBgPicsPath(), "OLD CEMETERY.png"),
        TOWER(PathFinder.getBgPicsPath(), "moon valley.jpg"),

        SPIDER_GROVE(PathFinder.getBgPicsPath(), "spider grove.png"),
        ERSIDRIS(PathFinder.getBgPicsPath(), "ERSIDRIS.png")

        ;
        private String backgroundFilePath;

        MAP_BACKGROUND(String... backgroundPathSegments) {
            this.backgroundFilePath = StrPathBuilder.build(backgroundPathSegments);
        }

        public String getBackgroundFilePath() {
            return backgroundFilePath;
        }

    }

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
        private String centerObjects;
        private String peripheryObjects;

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
