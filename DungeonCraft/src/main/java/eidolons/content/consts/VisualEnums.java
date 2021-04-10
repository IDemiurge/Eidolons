package eidolons.content.consts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import eidolons.ability.effects.common.ModifyStatusEffect;
import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.libgdx.GdxColorMaster;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.master.EffectMaster;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.mode.MODE;
import main.content.mode.STD_MODES;
import main.data.filesys.PathFinder;
import main.elements.costs.Cost;
import main.game.logic.event.Event;
import main.system.EventType;
import main.system.GuiEventType;
import main.system.Producer;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.FontMaster;

import java.util.List;

import static main.content.enums.GenericEnums.VFX.*;

public class VisualEnums {
    public static final VALUE[] anim_vals = {
            PROPS.ANIM_SPRITE_PRECAST,
            PROPS.ANIM_SPRITE_CAST,
            PROPS.ANIM_SPRITE_RESOLVE,
            PROPS.ANIM_SPRITE_MAIN,
            PROPS.ANIM_SPRITE_IMPACT,
            PROPS.ANIM_SPRITE_AFTEREFFECT,
            PROPS.ANIM_MISSILE_SPRITE,
            PROPS.ANIM_MODS_SPRITE,

            PROPS.ANIM_MISSILE_VFX,
            PROPS.ANIM_VFX_PRECAST,
            PROPS.ANIM_VFX_CAST,
            PROPS.ANIM_VFX_RESOLVE,
            PROPS.ANIM_VFX_MAIN,
            PROPS.ANIM_VFX_IMPACT,
            PROPS.ANIM_VFX_AFTEREFFECT,
            PROPS.ANIM_MODS_VFX,
            PARAMS.ANIM_SPEED,
            PARAMS.ANIM_FRAME_DURATION,
    };

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
        public GenericEnums.BLENDING blending;
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
                        return CHANNELING;
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
                        return DEFEND;
                    case WAITING:
                        return WAIT;
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

    public static enum CONTAINER {
        INVENTORY,
        STASH,
        SHOP,
        CONTAINER, QUICK_SLOTS,
        EQUIPPED,
        UNASSIGNED,
    }

    public static enum CELL_TYPE {
        WEAPON_MAIN(Images.EMPTY_WEAPON_MAIN),
        WEAPON_OFFHAND(Images.EMPTY_WEAPON_OFFHAND),
        ARMOR(Images.EMPTY_ARMOR),
        AMULET(Images.EMPTY_AMULET),
        RING(Images.EMPTY_RING),
        QUICK_SLOT(Images.EMPTY_QUICK_ITEM),
        INVENTORY(Images.EMPTY_ITEM),
        CONTAINER(Images.EMPTY_LIST_ITEM),
        STASH(Images.EMPTY_LIST_ITEM), WEAPON_MAIN_RESERVE(Images.EMPTY_WEAPON_MAIN), WEAPON_OFFHAND_RESERVE(Images.EMPTY_WEAPON_OFFHAND);


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

        public Color color;

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
        EFFORTLESS(GdxColorMaster.WHITE, 25),
        ;
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

    public enum SCREEN_TYPE {
        EDITOR, DUNGEON, PRE_BATTLE, MAIN_MENU, WEAVE, BRIEFING, CINEMATIC, MAP, EDITOR_WELCOME
    }

    public enum LABEL_STYLE {
        AVQ_SMALL(17, FontMaster.FONT.AVQ),
        AVQ_MED(20, FontMaster.FONT.AVQ),
        AVQ_LARGE(24, FontMaster.FONT.AVQ),

        MORPH_SMALL(14, FontMaster.FONT.METAMORPH),
        MORPH_MED(16, FontMaster.FONT.METAMORPH),
        MORPH_LARGE(20, FontMaster.FONT.METAMORPH),


        ;

        public int size;
        public FontMaster.FONT font;
        public Color color;

        LABEL_STYLE(int size, FontMaster.FONT font) {
            this(size, font, GdxColorMaster.getDefaultTextColor());
        }

        LABEL_STYLE(int size, FontMaster.FONT font, Color color) {
            this.size = size;
            this.font = font;
            this.color = color;
        }
    }

    public enum FLY_OBJ_TYPE {
        cloud(0.04f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        cloud_large(0.03f, GenericEnums.ALPHA_TEMPLATE.CLOUD_HEAVY, true, false, 0.5f) {
            @Override
            public String toString() {
                return "cloud";
            }
        },
        thunder(0.03f, null, true, true, 0f, true, SPRITE_TEMPLATE.THUNDER),
        thunder2(0.03f, null, false, false, 0f, true, SPRITE_TEMPLATE.THUNDER2),
        thunder3(0.03f, null, false, false, 0f, true, SPRITE_TEMPLATE.THUNDER2),
        //linked with texture?
        isle(0.5f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        stars(2.5f, GenericEnums.ALPHA_TEMPLATE.SUN, true, true, 0f),
        wraith(0.2f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        smoke(0.3f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        black_smoke(0.3f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        // star_field,
        // cloud_field, //this would require some alpha tricks!
        //
        comet_pale(2.5f,
                missile_pale, missile_pale, missile_arcane, missile_nether_nox),
        comet_bright(3f,
                missile_warp, missile_death, missile_chaos, missile_arcane_pink),
        mist(2f, MIST_WHITE3, MIST_WHITE2, MIST_TRUE2, MIST_WIND),
        cinders(3f, CINDERS3, CINDERS2, CINDERS),

        debris(0.4f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f),
        light(0.3f, GenericEnums.ALPHA_TEMPLATE.CLOUD, true, false, 0f), //sprite?
        ;

        public  String directory;
        public  String fileName;
        public FLY_OBJ_TYPE host;
        public float angleRange;
        public String path;
        public  GenericEnums.BLENDING blending;
        public  boolean scaling;
        public  GenericEnums.VFX[] vfx;
        public  Color hue;
        public  float baseAlpha;

        FLY_OBJ_TYPE(float speedFactor, GenericEnums.VFX... vfx) {
            this.vfx = vfx;
            this.speedFactor = speedFactor;
        }

        FLY_OBJ_TYPE(float speedFactor, GenericEnums.ALPHA_TEMPLATE alpha,
                     boolean flipX, boolean flipY, float weightFactor) {
            this(speedFactor, alpha, flipX, flipY, weightFactor, false, null);
        }

        FLY_OBJ_TYPE(float speedFactor, GenericEnums.ALPHA_TEMPLATE alpha,
                     boolean flipX, boolean flipY, float weightFactor, boolean sprite, SPRITE_TEMPLATE template) {
            this.directory = sprite ? PathFinder.getSpritesPath() + "fly objs/" : PathFinder.getFlyObjPath();
            this.fileName = toString();
            this.path = sprite ? directory + fileName + ".txt"
                    : directory + fileName + ".png";

            this.speedFactor = speedFactor;
            this.alpha = alpha;
            this.flipX = flipX;
            this.flipY = flipY;
            this.weightFactor = weightFactor;
            spriteTemplate = template;
        }

        public float speedFactor;
        public GenericEnums.ALPHA_TEMPLATE alpha;
        public SPRITE_TEMPLATE spriteTemplate;
        public boolean flipX;
        public boolean flipY;
        public float weightFactor; //TODO revamp; now its just scale boost

        public String getPathVariant() {
            if (vfx != null) {
                return vfx[RandomWizard.getRandomInt(vfx.length)].getPath();
            }
            if (path.endsWith(".txt")) {
                return path;
            }
            return GdxStringUtils.cropImagePath(FileManager.getRandomFilePathVariantSmart(fileName, directory, ".png"));
        }

        static {
            cloud_large.setBaseAlpha(0.89f);
            cloud_large.angleRange = 13;
            cloud.setBaseAlpha(0.89f);
            cloud.angleRange = 3;

            thunder.setHost(cloud_large);
            thunder2.setHost(cloud_large);
            thunder3.setHost(cloud_large);
        }

        public void setHost(FLY_OBJ_TYPE host) {
            this.host = host;
        }

        public boolean isHued() {
            return baseAlpha != 0f;
        }

        public float getBaseAlpha() {
            return baseAlpha;
        }

        public void setBaseAlpha(float baseAlpha) {
            this.baseAlpha = baseAlpha;
        }
    }

    public enum SPRITE_TEMPLATE {
        //        WITCH_FLAME,
        THUNDER(11f, 0.35f, true, 22),
        THUNDER2(11f, 0.23f, false, 32),
        ;

        SPRITE_TEMPLATE(float pauseAfterCycle) {
            this.pauseAfterCycle = pauseAfterCycle;
        }

        SPRITE_TEMPLATE(float pauseAfterCycle, float scaleRange, boolean reverse, int fps) {
            this.pauseAfterCycle = pauseAfterCycle;
            this.scaleRange = scaleRange;
            this.canBeReverse = reverse;
            this.fps = fps;
        }

        SPRITE_TEMPLATE(float pauseAfterCycle, float speedRandomness, float acceleration, float offsetRangeX, float offsetRangeY, float scaleRange) {
            this.pauseAfterCycle = pauseAfterCycle;
            this.speedRandomness = speedRandomness;
            this.acceleration = acceleration;
            this.offsetRangeX = offsetRangeX;
            this.offsetRangeY = offsetRangeY;
            this.scaleRange = scaleRange;
        }

        public boolean canBeReverse;
        public int fps;
        public float pauseAfterCycle;
        public float speedRandomness;
        public float acceleration;
        public float offsetRangeX;
        public float offsetRangeY;
        public float scaleRange;
    }

    public enum CURSOR {
        DEFAULT(PathFinder.getCursorPath()),
        TARGETING(32, 32, PathFinder.getTargetingCursorPath()),
        LOADING(PathFinder.getLoadingCursorPath()),
        WAITING(PathFinder.getLoadingCursorPath()),
        ATTACK(PathFinder.getAttackCursorPath()),
        ATTACK_SNEAK(2, 60, PathFinder.getSneakAttackCursorPath()),
        SPELL,
        DOOR_OPEN,
        LOOT,
        INTERACT,
        EXAMINE,

        NO, EMPTY,
        ;
       public int x, y;
        private String filePath;

        CURSOR(int x, int y, String filePath) {
            this.x = x;
            this.y = y;
            this.filePath = filePath;
        }

        CURSOR(String filePath) {
            this.filePath = filePath;
        }

        CURSOR() {
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    public enum ANIM_PART {
        PRECAST(2F), //channeling
        CAST(2.5f),
        RESOLVE(2),
        MISSILE(3) {
            @Override
            public String getPartPath() {
                return
                        "missile";
            }
        }, //flying missile
        IMPACT(1),
        AFTEREFFECT(2.5f);

        public String getPartPath() {
            return super.toString();
        }

        private final float defaultDuration;

        ANIM_PART(float defaultDuration) {
            this.defaultDuration = defaultDuration;
        }

        public float getDefaultDuration() {
            return defaultDuration;
        }
    }

    public enum TEXT_CASES {
        DEFAULT,
        REQUIREMENT,
        CANNOT_ACTIVATE,

        BONUS_DAMAGE,
        ATTACK_CRITICAL,
        ATTACK_SNEAK,

        ATTACK_DODGED,
        ATTACK_BLOCKED,
        ATTACK_PARRIED,
        ATTACK_MISSED,

        ATTACK_OF_OPPORTUNITY,
        COUNTER_ATTACK,
        ATTACK_INSTANT,

        ROLL,
        PARAM_MOD,
        COSTS(true, (e) -> {
            DC_ActiveObj a = (DC_ActiveObj) e.getRef().getActive();
            List<Cost> costs = a.getCosts().getCosts();
            costs.removeIf(c -> c.getPayment().getLastPaid() == 0
                    //            getAmountFormula().toString().isEmpty()
            );
            return costs.toArray();
        }),
        STATUS(
                false, (e) -> {
            ModifyStatusEffect ef = (ModifyStatusEffect)
                    EffectMaster.getFirstEffectOfClass((DC_ActiveObj) e.getRef().getActive(), ModifyStatusEffect.class);
            //                if (ef==null )
            return ef.getValue().split(";");
        }),
        MODE(
                false, (e) -> {
            ModeEffect ef = (ModeEffect)
                    EffectMaster.getFirstEffectOfClass((DC_ActiveObj) e.getRef().getActive(), ModifyStatusEffect.class);
            return new Object[]{
                    ef.getMode()
            };
        }),

        BATTLE_COMMENT,

        HIT {
            @Override
            public Object[] getArgs(Event e) {
                return new Object[]{
                        e.getRef().getAmount()
                };
            }
        }, DURABILITY_LOSS, ANNIHILATED,
        XP,
        GOLD,
        LEVEL_UP;
        public boolean atOrigin;
        String name = StringMaster.format(name());
        private Producer<Event, Object[]> argProducer;

        TEXT_CASES() {

        }

        TEXT_CASES(boolean atOrigin, Producer<Event, Object[]> producer) {
            this.atOrigin = atOrigin;
            this.argProducer = producer;
        }

        public String getText() {
            return name;
        }

        public Object[] getArgs(Event e) {
            if (argProducer == null) {
                return new Object[]{
                        StringMaster.format(e.getType().toString())
                };
            }
            return argProducer.produce(e);
        }
    }

    public enum FX_TEST_MODE {
        CRITICAL_ALL,
        HIGH_ALL,
        MIXED_1,
        MIXED_2,
        LOW_ALL,

        STA,
        STA2,
        STA3,
        FOC,
        FOC2,
        FOC3,
        MOR,
        MOR2,
        MOR3,
        WOU,
        WOU2,
    }

    public enum POST_FX_FACTOR {
        DISCOLOR,
        FADE_COLOR,
        BLUR,
        LENS,
        BLOOM,
        DARKEN,
        LIGHTEN,
        BLOODY, //red vignette
        MOTION_BLUR,
        //burn, disease,
        // night sight ,
        DISTORT, SMOOTH, LENS2
    }

    public enum POST_FX_TEMPLATE {
        DIZZY,
        FATIGUE,
        ENERGIZED,
        FEAR,
        INSPIRED, UNCONSCIOUS(),

        PALE_ASPECT, MAZE(), MOSAIC();

        public final POST_FX_FACTOR[] factors;

        POST_FX_TEMPLATE(POST_FX_FACTOR... factors) {
            this.factors = factors;
        }
    }

    public enum PARTICLES_SPRITE {
        ASH(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.INVERT_SCREEN,
                12, 2, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        ASH_THICK(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.INVERT_SCREEN,
                14, 3, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        //        MIST(true, "sprites/particles/mist.txt", GenericEnums.BLENDING.SCREEN,
        //                10, 2, null),
        //        BLACK_MIST(true, "sprites/particles/mist.txt", GenericEnums.BLENDING.INVERT_SCREEN,
        //                10, 2, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        SNOW(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.SCREEN,
                14, 2, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        SNOW_THICK(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.SCREEN,
                15, 3, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        ;


        PARTICLES_SPRITE(boolean flipping, String path, GenericEnums.BLENDING blending, int fps, int overlap, GenericEnums.ALPHA_TEMPLATE fluctuation) {
            this.flipping = flipping;
            this.path = path;
            this.blending = blending;
            this.fps = fps;
            this.overlap = overlap;
            this.fluctuation = fluctuation;
        }

        public boolean reverse;
        public boolean flipping;
        public String path;
        public GenericEnums.BLENDING blending;
        public int fps;
        public int overlap;
        public boolean changeFps;
        public GenericEnums.ALPHA_TEMPLATE fluctuation;
        public int eachNisFlipX = 0;
        public int duration;
    }

    public enum SHADE_CELL {
        GAMMA_SHADOW(0.75f, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "shadow neu.png")),
        GAMMA_LIGHT(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "light.png")),
        LIGHT_EMITTER(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "light emitter.png")),
        CONCEALMENT(0.5f, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "concealment.png")),
        BLACKOUT(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "blackout.png")),
        HIGHLIGHT(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "highlight.png")),
        VOID(0, StrPathBuilder.build(PathFinder.getShadeCellsPath(), "void.png")),
        ;

        public float defaultAlpha;
        private final String texturePath;

        SHADE_CELL(float alpha, String texturePath) {
            defaultAlpha = alpha;
            this.texturePath = texturePath;
        }

        public String getTexturePath() {
            return texturePath;
        }

    }

    public enum FLIGHT_ENVIRON {
        astral("objs_over:mist(20),comet_bright(10),comet_pale(20),mist(10),;" +
                "objs_under:stars(15);" +
                "angle:30;"),
        voidmaze("objs_under:cloud(500),thunder3(40),thunder(11);hue:cloud;" +
                "angle:30;"),
        ;
        public String data;

        FLIGHT_ENVIRON(String data) {
            this.data = data;
        }
    }

    public enum VFX_ATLAS {
        AMBIENCE,
        SPELL,
        MAP,
        MISC,
        INVERT, UNIT
    }

    public enum PROJECTION {
        FROM(true), TO(false), HOR(null),
        ;
        public Boolean bool;

        PROJECTION(Boolean bool) {
            this.bool = bool;
        }
    }

    public enum WEAPON_ANIM_CASE {
        NORMAL,
        MISSILE_MISS,
        MISSILE,
        MISS,
        READY,
        PARRY,
        BLOCKED,
        RELOAD,
        POTION,
        ;

        public boolean isMissile() {
            return this == WEAPON_ANIM_CASE.MISSILE || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }

        public boolean isMiss() {
            return this == WEAPON_ANIM_CASE.MISS || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }
    }

    public enum ScreenShakeTemplate {
        SLIGHT(25, 15),
        MEDIUM(30, 25),
        HARD(35, 35),
        BRUTAL(40, 45),

        VERTICAL(50, 35),
        HORIZONTAL(50, 35),
        ;
        public int duration = 5; // In seconds, make longer if you want more variation
        public int frequency; // hertz
        public float amplitude; // how much you want to shake
        public boolean falloff = true; // if the shake should decay as it expires

        ScreenShakeTemplate(int frequency, float amplitude) {
            this.frequency = frequency;
            this.amplitude = amplitude;
        }
    }

    public enum ITEM_FILTERS {
        ALL,
        WEAPONS,
        ARMOR,
        USABLE,
        JEWELRY,
        QUEST
    }

    public enum PERK_ABILITY {
        Consume_Magic, Arcane_Fury, Mark_Sorcerer,
    }

    public enum PERK_PARAM_COMBO {
        //        MAGIC_RESISTANCES(5, 15, 30, false, "Relentless"),
        //        PHYSICAL_RESISTANCES(5, 15, 30, false, "Relentless"),

    }

    public enum PERK_TYPE {
        MASTERY, ATTRIBUTE, PARAMETER, ABILITY, PASSIVE,
        //
    }
}
