package main.system.sound;

import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.enums.entity.HeroEnums.HERO_SOUNDSET;
import main.content.values.parameters.PARAMETER;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;

import java.io.File;
import java.util.Arrays;

import static main.system.sound.SoundMaster.STD_SOUNDS.*;

public class SoundMaster {

    public static final String ALT = "_ALT";
    public static final String STD_FORMATS = "mp3;flac;ogg;m4a;wav";
    private static final String STD_SOUND_PATH = PathFinder.getSoundPath() + "STD/";
    private static final String FORMAT = ".mp3";
    private static final String ALT_FORMAT = ".wav";
    public static int masterVolume = 100;
    private static String path;
    private static boolean blockNextSound;
    private static boolean on = true;

    public static void initialize() {
        path = PathFinder.getSoundPath();

    }

    public static void play(File file) {
        if (isOn()) getPlayer().play(file);
    }

    public static void playRandomStandardSound(STD_SOUNDS... sounds) {
        playStandardSound(new RandomWizard<STD_SOUNDS>().getRandomListItem(Arrays.asList(sounds)));

    }

    public static void playStandardSound(STD_SOUNDS sound) {
        if (isOn()) getPlayer().playStandardSound(sound);
    }

    private static Player getPlayer() {
        return new Player();
    }

    public static void playRandomSound(HERO_SOUNDSET hero_SOUNDSET) {
        if (isOn()) getPlayer().playRandomSound(hero_SOUNDSET);
    }

    public static void playRandomSoundVariant(String basePath, boolean alt) {
        if (isOn()) if (isOn()) getPlayer().playRandomSoundVariant(basePath, alt);
    }

    public static boolean playEffectSound(SOUNDS sound_type, SOUNDSET soundset) {
        if (isOn()) return getPlayer().playEffectSound(sound_type, soundset);
        return false;
    }

    public static void play(String sound) {
        if (isOn()) getPlayer().play(sound);
    }

    public static void play(String sound, int delay) {
        if (isOn()) getPlayer().play(sound, delay);
    }

    public static void playNow(String sound) {
        if (isOn()) getPlayer().playNow(sound);
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj) {
        if (isOn()) getPlayer().playEffectSound(sound_type, obj);
    }

    public static void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj) {
        if (isOn()) getPlayer().playSoundOnCurrentThread(sound_type, obj);
    }

    public static void playRandomSoundFromFolder(String path) {
        if (isOn()) getPlayer().playRandomSoundFromFolder(path);
    }

    public static void playCustomEffectSound(SOUNDS sound_type, Obj obj) {
        if (isOn()) getPlayer().playCustomEffectSound(sound_type, obj);
    }

    public static void playHitSound(Obj obj) {
        if (isOn()) getPlayer().playHitSound(obj);
    }

    public static void playSkillAddSound(ObjType type, PARAMETER mastery, String masteryGroup,
                                         String rank) {
        if (isOn()) getPlayer().playSkillAddSound(type, mastery, masteryGroup, rank);
    }

    // FULL PARAMETER METHODS
    // TODO [REFACTOR] - use (volume_percentage, delay) constructor instead!
    public static void play(File file, int volume_percentage, int delay) {
        if (isOn()) getPlayer().play(file, volume_percentage, delay);
    }

    public static void playStandardSound(STD_SOUNDS sound, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playStandardSound(sound, volume_percentage, delay);
    }

    public static void playRandomSound(HERO_SOUNDSET hero_SOUNDSET, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playRandomSound(hero_SOUNDSET, volume_percentage, delay);
    }

    public static void playRandomSoundVariant(String basePath, boolean alt, int volume_percentage,
                                              int delay) {
        if (isOn()) getPlayer().playRandomSoundVariant(basePath, alt, volume_percentage, delay);
    }

    public static boolean playEffectSound(SOUNDS sound_type, SOUNDSET soundset,
                                          int volume_percentage, int delay) {
        if (isOn()) return getPlayer().playEffectSound(sound_type, soundset, volume_percentage, delay);
        return false;
    }

    public static void play(String sound, int volume_percentage, int delay) {
        if (isOn()) getPlayer().play(sound, volume_percentage, delay);
    }

    public static void playNow(String sound, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playNow(sound, volume_percentage, delay);
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playEffectSound(sound_type, obj, volume_percentage, delay);
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj, int volumePercentage,
                                       int volume_percentage, int delay) {
        if (isOn()) getPlayer().playEffectSound(sound_type, obj, volumePercentage, volume_percentage, delay);
    }

    public static void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj, int volume_percentage,
                                                int delay) {
        if (isOn()) getPlayer().playSoundOnCurrentThread(sound_type, obj, volume_percentage, delay);
    }

    public static void playRandomSoundFromFolder(String path, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playRandomSoundFromFolder(path, volume_percentage, delay);
    }

    public static void playCustomEffectSound(SOUNDS sound_type, Obj obj, int volume_percentage,
                                             int delay) {
        if (isOn()) getPlayer().playCustomEffectSound(sound_type, obj, volume_percentage, delay);
    }

    public static void playHitSound(Obj obj, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playHitSound(obj, volume_percentage, delay);
    }

    public static void playSkillAddSound(ObjType type, PARAMETER mastery, String masteryGroup,
                                         String rank, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playSkillAddSound(type, mastery, masteryGroup, rank, volume_percentage, delay);
    }

    public static boolean checkSoundTypePlayer(SOUNDS sound_type) {
        return true;
    }

    public static String getPath() {
        return path;
    }

    public static int getMasterVolume() {
        return masterVolume;
    }

    public static void setMasterVolume(int masterVolume) {
        SoundMaster.masterVolume = masterVolume;
    }

    public static void blockNextSound() {
        blockNextSound = true;
    }

    public static boolean isBlockNextSound() {
        return blockNextSound;
    }

    public static void setBlockNextSound(boolean blockNextSound) {
        SoundMaster.blockNextSound = blockNextSound;
    }

    public static void playScribble() {
        playRandomStandardSound(SCRIBBLE, SCRIBBLE2, SCRIBBLE3);
    }

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        SoundMaster.on = on;
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
        CHEST(NEW__OPEN_MENU, NEW__HOVER, NEW__CONTAINER, NEW__CLICK_DISABLED),;

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
        ATTACK, HIT, WHAT, MOVEMENT, CRITICAL, FLEE, TAUNT, THREAT, DEATH, READY, SPEC_ACTION,
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
        FALL;

        public String getPath() {
            return "effects/" + name().toLowerCase() + "/" + name();
        }
    }

    public enum STD_SOUNDS {
        POTION,
        POTION2,


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

        DIS__OPEN_MENU(true),
        DIS__BLOCKED(true),
        DIS__COINS(true),
        DIS__FADE(true),
        DIS__KNIFE(true),
        DIS__PAGE_TURN(true),
        DIS__BOON_SMALL(true),
        DIS__BOON_LARGE(true),
        DIS__BOOK_CLOSE(true),
        DIS__BOOK_OPEN(true),
        DIS__REWARD(true),
        DIS__REWARD2(true),
        DIS__REWARD3(true),
        DIS__BLESS(true),
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

        NEW__BONES, NEW__CHEST, NEW__GATE

        ;
        String path;
        private boolean alt;

        STD_SOUNDS() {
            this(false);
        }

        STD_SOUNDS(boolean alt) {
            this.alt = alt || toString().contains("__");
            path = STD_SOUND_PATH
             + (alt ? toString() + ALT_FORMAT
             : toString() + FORMAT).replace("__", "/").replace("_", " ");

        }

        public String getPath() {
            return path;
        }

        public String getAltPath() {
            return STD_SOUND_PATH + toString() + ALT + FORMAT;
        }

        public boolean hasAlt() {
            return false;
        }
    }

}
