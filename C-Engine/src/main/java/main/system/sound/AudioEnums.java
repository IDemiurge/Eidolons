package main.system.sound;

import main.content.CONTENT_CONSTS;
import main.system.datatypes.WeightMap;

import static main.system.sound.AudioEnums.SOUNDSCAPE_SOUND.*;
import static main.system.sound.AudioEnums.STD_SOUNDS.*;

public class AudioEnums {

    static {
        AudioEnums.SOUNDSCAPE.NETHER.weightMapCustom
                .chain(thunder, 20)
                .chain(whispers, 4)
                .chain(rocks, 21)
                .chain(crack, 15)
                .chain(growl, 1)
                .chain(chant_dark, 2)
                .chain(chant_evil, 2);
    }

    public enum SOUNDSCAPE_SOUND {
        rocks,
        crack,
        growl,
        chant_dark,
        chant_evil,
        whispers,
        thunder,
        woosh,
        splash,
        howl,
        scream,
        stones,
        mythic_beast,

    }

    public enum SOUNDSCAPE {
        NETHER,
        WATERS,
        DEEP,
        HELL,
        HORROR,
        ASTRAL,
        ;
        public int customChance = 75;
        public WeightMap<String> weightMapCustom = new WeightMap<>();
        public WeightMap<CONTENT_CONSTS.SOUNDSET> weightMapUnit = new WeightMap<>(CONTENT_CONSTS.SOUNDSET.class);
        public WeightMap<SOUNDS> weightMapSoundType = new WeightMap<>(SOUNDS.class);
        public int minPause = 20000;
        public float chancePerSecond = 0.003f;
    }

    public enum BUTTON_SOUND_MAP {
        //drag? scroll?
        MENU(null, null, NEW__CLICK_UP, NEW__CLICK_DISABLED),
        SELECTION_SHARP(NEW__CLICK, null, NEW__CLICK_UP2, NEW__CLICK_DISABLED),

        SELECTION_SCROLL(NEW__TAB, null, NEW__OPEN_MENU, NEW__CLICK_DISABLED),
        HELP(NEW__TAB, null, NEW__OPEN_MENU, NEW__CLICK_DISABLED),

        SELECTION(NEW__TAB, null, null, NEW__CLICK_DISABLED),
        EYE(NEW__TAB, null, null, NEW__CLICK_DISABLED),

        TAB(NEW__TAB, null, null, null),
        STAT(NEW__PLUS, null, null, CLICK_ERROR),
        ENTER(null, NEW__TAB, NEW__ENTER, NEW__CLICK_DISABLED),
        OK(NEW__OK, NEW__TAB, null, NEW__CLICK_DISABLED),
        CANCEL(NEW__CLICK_DISABLED, NEW__TAB, NEW__ENTER, NEW__CLICK_DISABLED),

        //        REPAIR(NEW__OPEN_MENU , NEW__HOVER ,NEW__CONTAINER  , NEW__CLICK_DISABLED),
        CHEST(NEW__OPEN_MENU, NEW__HOVER, NEW__CONTAINER, NEW__CLICK_DISABLED),
        ;

        public STD_SOUNDS down;
        public STD_SOUNDS hover;
        public STD_SOUNDS up;
        public STD_SOUNDS disabled;

        BUTTON_SOUND_MAP(STD_SOUNDS down, STD_SOUNDS hover, STD_SOUNDS up, STD_SOUNDS disabled) {
            this.down = down;
            this.hover = hover;
            this.up = up;
            this.disabled = disabled;
        }
    }

    public enum SCREEN_SOUND_MAP {

    }

    public enum SOUNDS {
        ATTACK {
            @Override
            public String getAltName() {
                return "atk";
            }
        },
        HIT {
            @Override
            public String getAltName() {
                return "hurt";
            }

            @Override
            public String getAltName2() {
                return "pain";
            }
        }, DEATH,
        SPOT, IDLE, ALERT,

        MOVEMENT, CRITICAL, FLEE, TAUNT, THREAT,
        SPEC_ACTION, WHAT, READY,
        // spell
        IMPACT,
        CAST,
        EFFECT, // ON CUSTOM EFFECTS, PLAY SPELL'S 'EFFECT' SOUND
        ZONE,
        PRECAST,
        FAIL,
        CHANNELING,
        W_CHANNELING,
        RESOLVE {
            public String toString() {
                return "";
            }
        },
        FALL, LAUGH, DARK, EVIL;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public String getAltName() {
            return super.toString();
        }

        public String getAltName2() {
            return super.toString();
        }

        public String getPath() {
            return name();
//            return "effects/" + name().toLowerCase() + "/" + name();
        }
    }

    public enum STD_SOUNDS {
        POTION,
        POTION2,
        LAMP,

        HERO,
        SCROLL,
        SCROLL2,
        SCRIBBLE(true),
        SCRIBBLE2(true),
        SCRIBBLE3(true),
        CLOSE,
        OPEN,
        BUY,
        DONE2,

        HEY,
        FIGHT,
        FAIL,
        FALL(true),
        LEVEL_UP,
        CHECK,
        ADD,
        ERASE,
        MOVE,
        NOTE,
        OK,
        BACK,
        DEATH,
        PAGE_TURNED,
        PAGE_TURNED_ALT,
        SPELL_RESISTED,

        CLICK_BLOCKED,
        CLICK_ERROR,
        CLICK_TARGET_SELECTED,
        CLICK,
        CLOCK,
        SLING,
        SLOT,
        WEAPON,
        NO,
        MODE,
        OK_STONE,
        ON_OFF,
        CHAIN,

        CLICK_ACTIVATE,
        SPELL_LEARNED,
        SKILL_LEARNED,
        SPELL_UPGRADE_LEARNED,
        SPELL_UPGRADE_UNLEARNED,
        SPELL_ACTIVATE,
        ACTION_CANCELLED,
        SPELL_CANCELLED,

        MISSED_MELEE,
        MISSED,
        TURN,
        DONE,

        CLICK1,
        ButtonUp,
        ButtonDown, VICTORY,

        NEW__CLICK, NEW__CLICK_DISABLED, NEW__CLICK_UP2,
        NEW__CLICK_UP, NEW__ENTER, NEW__TAB,
        NEW__BATTLE_START, NEW__BATTLE_START2,

        NEW__CONTAINER,
        NEW__PLUS,
        NEW__PAUSE,
        NEW__RESUME,

        NEW__OPEN_MENU, NEW__GOLD, NEW__OK,
        NEW__DEFEAT, NEW__VICTORY, NEW__BATTLE_END, NEW__BATTLE_END2,
        NEW__QUEST_TAKEN, NEW__QUEST_CANCELLED, NEW__QUEST_COMPLETED,

        NEW__HOVER, NEW__HOVER_OFF,

        NEW__BONES, NEW__CHEST, NEW__GATE,

        NEW__UNLOCK, NEW__TOWN_PORTAL_DONE, NEW__TOWN_PORTAL_START, NEW__DREAD, NEW__SHADOW_FALL, NEW__SHADOW_SUMMON, NEW__SHADOW_PRE_SUMMON;
        String path;

        STD_SOUNDS() {
            this(false);
        }

        STD_SOUNDS(boolean alt) {
            path =SoundMaster. STD_SOUND_PATH
                    + (alt ? toString() + SoundMaster.ALT_FORMAT
                    : toString() + SoundMaster.FORMAT).replace("__", "/").replace("_", " ");
        }

        public String getPath() {
            return path;
        }

        public String getAltPath() {
            return SoundMaster.STD_SOUND_PATH + toString() + SoundMaster.ALT + SoundMaster.FORMAT;
        }

        public boolean hasAlt() {
            return false;
        }
    }
}
