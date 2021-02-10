package eidolons.content.consts;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.consts.libgdx.GdxColorMaster;
import main.content.enums.GenericEnums;
import main.content.mode.MODE;
import main.content.mode.STD_MODES;
import main.data.filesys.PathFinder;
import main.system.EventType;
import main.system.GuiEventType;

import static main.content.enums.GenericEnums.VFX.*;

public class VisualEnums {
    public enum SHARD_OVERLAY {
        MIST,
        DARKNESS,
        NETHER,

    }

    public enum SHARD_SIZE {
        SMALL,
        NORMAL {
            @Override
            public String toString() {
                return "";
            }
        },
        LARGE,
        ;

        public String toString() {
            return name() + " ";
        }
    }

    public enum SHARD_TYPE {
        ROCKS,
        ROOTS,
        CHAINS,

    }

    public enum VFX_TEMPLATE {

        CAVE,
        COLD,
        POISON,
        DUNGEON,
        CRYPT,
        HELL,
        HALL,
        FOREST,
        DEEP_MIST,
        ;

        static {
            COLD.setDaily(
                    SNOW,
                    MIST_ARCANE,
                    MIST_WIND,
                    SNOWFALL,
                    //             MIST_CYAN,
                    //             DARK_MIST_LITE,
                    MIST_CYAN,
                    STARS,
                    SNOWFALL_THICK
            );
            COLD.setNightly(
                    //             MIST_CYAN,
                    MIST_WIND,
                    STARS,
                    MIST_ARCANE,
                    SNOW,
                    SNOWFALL,
                    MIST_WHITE2,
                    MIST_ARCANE,
                    //             DARK_MIST_LITE,
                    DARK_MIST_LITE
            );

            POISON.setDaily(
                    POISON_MIST,
                    POISON_MIST2,
                    ASH,
                    MIST_BLACK
            );
            POISON.setNightly(
                    POISON_MIST,
                    POISON_MIST2,
                    MIST_BLACK

            );


            CRYPT.setDaily(
                    MIST_WIND,
                    MIST_WHITE2,
                    MIST_ARCANE,
                    MIST_TRUE,
                    MIST_TRUE2
            );
            CRYPT.setNightly(
                    MIST_ARCANE,
                    MIST_WIND,
                    MIST_ARCANE,
                    DARK_MIST_LITE,
                    MIST_CYAN,
                    MIST_WIND
            );

            HELL.setDaily(
                    MIST_WIND,
                    POISON_MIST2,
                    ASH,
                    MIST_SAND_WIND,
                    CINDERS,
                    ASH
            );
            HELL.setNightly(
                    POISON_MIST,
                    POISON_MIST2,
                    ASH,
                    CINDERS,
                    ASH,
                    MIST_SAND_WIND
            );

            FOREST.setDaily(
                    FALLING_LEAVES
            );
            FOREST.setNightly(
                    FALLING_LEAVES_WINDY,
                    WISPS,
                    STARS
            );

            DEEP_MIST.setDaily(
                    MIST_WIND,
                    MIST_WHITE3,
                    MIST_BLACK,
                    DARK_MIST,
                    MIST_TRUE2
            );

            DEEP_MIST.setNightly(
                    MIST_WIND,
                    MIST_WHITE3,
                    MIST_ARCANE,
                    MIST_TRUE,
                    DARK_MIST,
                    MIST_TRUE2
            );
            DUNGEON.setDaily(
                    MIST_BLACK,
                    MIST_CYAN,
                    MIST_ARCANE
            );
            DUNGEON.setNightly(
                    MIST_BLACK,
                    WISPS,
                    STARS,
                    MOTHS_TIGHT2
            );
            HALL.setDaily(
                    MIST_WIND
            );
            HALL.setNightly(
                    MIST_WIND,
                    MIST_ARCANE,
                    MOTHS,
                    MOTHS_TIGHT2
            );

            CAVE.setDaily(
                    MIST_BLACK,
                    MIST_WIND,
                    MIST_TRUE2,
                    MIST_WHITE
            );
            CAVE.setNightly(
                    MIST_WIND,
                    MIST_WHITE,
                    MIST_ARCANE,
                    DARK_MIST_LITE,
                    MIST_TRUE2,
                    WISPS,
                    STARS

            );
        }

        public GenericEnums.VFX[] daily;
        public GenericEnums.VFX[] nightly;

        public void setDaily(GenericEnums.VFX... daily) {
            this.daily = daily;
        }
/*
        what will change with day-time?
        > chance
        > hue
        > some emitters will be exclusive or dependent

        0-100 for midnight - noon
        true/false for more/less

         */

        public void setNightly(GenericEnums.VFX... nightly) {
            this.nightly = nightly;
        }
    }

    public enum CELL_PATTERN {
        CROSS,
        CROSS_DIAG,
        CENTERPIECE,
        CHESS,

        GRID,
        SPIRAL,
        CONCENTRIC,
        OUTER_BORDER,
        //        DIAMOND,
    }

    public enum CELL_UNDERLAY {
        CRACKS,
        ROCKS,
        VINES,
        DARK,
        cobwebs,
        ruins,

    }

    /**
     * Created by JustMe on 8/31/2017.
     */
    public enum RESOLUTION {
    //    _1366x768,
        _1600x900,
        _1680x1050,
        _1920x1200,
        _1920x1080,
        _2560x1080,
        _2560x1440,
        _2560x1600,
    //    _2560x1200,
        _3440x1440,
        _3140x2160,
    //    _3840x2160,
    //    _5120x2880,
        ;
    }

    public enum INTENT_ICON {
        ATTACK,
        MOVE("attack"),
        OTHER("wheel"),
        SPELL("eye"),
        DEBUFF("eye"),
        BUFF("eye"),
        HOSTILE_SPELL("eye"),
        CHANNELING(GenericEnums.BLENDING.SCREEN),
        UNKNOWN,

        PREPARE(GenericEnums.BLENDING.SCREEN),
        WAIT,
        DEFEND,
        SEARCH,

        WHEEL,

        ;
        GenericEnums.BLENDING blending;
        protected String path;

        INTENT_ICON() {
            path = toString().toLowerCase();
        }

        INTENT_ICON(String path) {
            this.path = path;
        }

        INTENT_ICON(GenericEnums.BLENDING blending) {
            this();
            this.blending = blending;
        }

        public static INTENT_ICON getModeIcon(MODE mode) {
            if (mode instanceof STD_MODES) {
                switch (((STD_MODES) mode)) {
                    case CHANNELING:
                        return  CHANNELING;
                    case STEALTH:
                        break;
                    case ALERT:
                    case SEARCH:
                        return SEARCH;
                    case CONCENTRATION:
                    case RESTING:
                    case MEDITATION:
                        return PREPARE;
                    case DEFENDING:
                        return  DEFEND;
                    case WAITING:
                        return  WAIT;
                        default:
                            return WHEEL;
                }
            }
            return null;
        }

        public String getPath() {
            return "ui/content/intent icons/" + path + ".txt";

        }
    }

    public static   enum CONTAINER {
        INVENTORY,
        STASH,
        SHOP,
        CONTAINER, QUICK_SLOTS,
        EQUIPPED,
        UNASSIGNED,
    }

    public static   enum CELL_TYPE {
        WEAPON_MAIN(Images.EMPTY_WEAPON_MAIN),
        WEAPON_OFFHAND(Images.EMPTY_WEAPON_OFFHAND),
        ARMOR(Images.EMPTY_ARMOR),
        AMULET(Images.EMPTY_AMULET),
        RING(Images.EMPTY_RING),
        QUICK_SLOT(Images.EMPTY_QUICK_ITEM),
        INVENTORY(Images.EMPTY_ITEM),
        CONTAINER(Images.EMPTY_LIST_ITEM),
        STASH(Images.EMPTY_LIST_ITEM)
        , WEAPON_MAIN_RESERVE(Images.EMPTY_WEAPON_MAIN)
        , WEAPON_OFFHAND_RESERVE(Images.EMPTY_WEAPON_OFFHAND)
        ;


        private String slotImagePath;

        CELL_TYPE(String slotImagePath) {
            this.slotImagePath = slotImagePath;
        }

        public String getSlotImagePath() {
            return slotImagePath;
        }
    }

    public enum FULLSCREEN_ANIM {
        //        BLACK,
        BLOOD,
        BLOOD_SCREEN,
        HELLFIRE,
        GREEN_HELLFIRE(new Color(0.69f, 0.9f, 0.6f, 1f)) {
            public String getSpritePath() {
                return HELLFIRE.getSpritePath();
            }
        },
        POISON,

        EXPLOSION {
            public String getSpritePath() {
                return PathFinder.getSpritesPath() + "fullscreen/explode bright.txt";
            }
        },
        WAVE,
        TUNNEL,

        FLAMES,
        DARKNESS,
        THUNDER,
        MIST,

        GATE_FLASH,
        GATES {
            public String getSpritePath() {
                return PathFinder.getSpritesPath() + "fullscreen/short2.txt";
            }
        };

        FULLSCREEN_ANIM() {
        }

        FULLSCREEN_ANIM(Color color) {
            this.color = color;
        }

        Color color;

        public String getSpritePath() {
            return PathFinder.getSpritesPath() + "fullscreen/" + (toString().replace("_", " ")) + ".txt";
        }
    }

    public enum VIEW_ANIM {
        displace(GuiEventType.GRID_DISPLACE),
        screen(GuiEventType.GRID_SCREEN),
        //        scale,
        //        alpha,
        color(GuiEventType.GRID_COLOR),
        fall(GuiEventType.GRID_FALL),
        //        shader,
        //        postfx,
        //        sprite,
        //        vfx,
        attached(GuiEventType.GRID_ATTACHED);

        VIEW_ANIM(EventType event) {
            this.event = event;
        }

        public EventType event;
    }

    public enum THREAT_LEVEL {
        DEADLY(GdxColorMaster.PURPLE, 250),
        OVERPOWERING(GdxColorMaster.RED, 175),
        CHALLENGING(GdxColorMaster.ORANGE, 125),
        EVEN(GdxColorMaster.YELLOW, 100),
        MODERATE(GdxColorMaster.BLUE, 75),
        EASY(GdxColorMaster.GREEN, 50),
        EFFORTLESS(GdxColorMaster.WHITE, 25),;
        public final Color color;
        public final int powerPercentage;

        THREAT_LEVEL(Color color, int powerPercentage) {
            this.color = color;
            this.powerPercentage = powerPercentage;
        }

    }

    public enum PILLAR {
        HOR, VERT,
        RIGHT, UP,
        LEFT, DOWN,
        SINGLE {
            @Override
            public String toString() {
                return "bare";
            }
        },
        CORNER,
        SKEWED_CORNER {
            @Override
            public String toString() {
                return "skewed_corner";
            }
        },
        SKEWED_CORNER_UP {
            @Override
            public String toString() {
                return "skewed_corner_up";
            }
        }, SKEWED_CORNER_LEFT {
            @Override
            public String toString() {
                return "skewed_corner_left";
            }
        };

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

    }
}
