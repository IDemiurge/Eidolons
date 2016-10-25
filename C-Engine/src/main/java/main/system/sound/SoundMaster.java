package main.system.sound;

import main.content.CONTENT_CONSTS.HERO_SOUNDSET;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.parameters.PARAMETER;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;

import java.io.File;
import java.util.Arrays;

public class SoundMaster {

    public static final String ALT = "_ALT";
    public static final String STD_FORMATS = "mp3;flac;ogg;m4a;wav";
    private static final String STD_SOUND_PATH = PathFinder.getSoundPath() + "STD\\";
    private static final String FORMAT = ".mp3";
    private static final String ALT_FORMAT = ".wav";
    static Player player = new Player();
    private static String path;
    private static int masterVolume = 100;
    private static boolean blockNextSound;

    public static void initialize() {
        path = PathFinder.getSoundPath();

    }

    public static void play(File file) {
        new Player().play(file);
    }

    public static void playRandomStandardSound(STD_SOUNDS... sounds) {
        playStandardSound(new RandomWizard<STD_SOUNDS>().getRandomListItem(Arrays.asList(sounds)));

    }

    public static void playStandardSound(STD_SOUNDS sound) {
        new Player().playStandardSound(sound);
    }

    public static void playRandomSound(HERO_SOUNDSET hero_SOUNDSET) {
        new Player().playRandomSound(hero_SOUNDSET);
    }

    public static void playRandomSoundVariant(String basePath, boolean alt) {
        new Player().playRandomSoundVariant(basePath, alt);
    }

    public static boolean playEffectSound(SOUNDS sound_type, SOUNDSET soundset) {
        return new Player().playEffectSound(sound_type, soundset);
    }

    public static void play(String sound) {
        new Player().play(sound);
    }

    public static void play(String sound, int delay) {
        new Player().play(sound, delay);
    }

    public static void playNow(String sound) {
        new Player().playNow(sound);
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj) {
        new Player().playEffectSound(sound_type, obj);
    }

    public static void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj) {
        new Player().playSoundOnCurrentThread(sound_type, obj);
    }

    public static void playRandomSoundFromFolder(String path) {
        new Player().playRandomSoundFromFolder(path);
    }

    public static void playCustomEffectSound(SOUNDS sound_type, Obj obj) {
        new Player().playCustomEffectSound(sound_type, obj);
    }

    public static void playHitSound(Obj obj) {
        new Player().playHitSound(obj);
    }

    public static void playSkillAddSound(ObjType type, PARAMETER mastery, String masteryGroup,
                                         String rank) {
        new Player().playSkillAddSound(type, mastery, masteryGroup, rank);
    }

    // FULL PARAMETER METHODS
    // TODO [REFACTOR] - use (volume_percentage, delay) constructor instead!
    public static void play(File file, int volume_percentage, int delay) {
        new Player().play(file, volume_percentage, delay);
    }

    public static void playStandardSound(STD_SOUNDS sound, int volume_percentage, int delay) {
        new Player().playStandardSound(sound, volume_percentage, delay);
    }

    public static void playRandomSound(HERO_SOUNDSET hero_SOUNDSET, int volume_percentage, int delay) {
        new Player().playRandomSound(hero_SOUNDSET, volume_percentage, delay);
    }

    public static void playRandomSoundVariant(String basePath, boolean alt, int volume_percentage,
                                              int delay) {
        new Player().playRandomSoundVariant(basePath, alt, volume_percentage, delay);
    }

    public static boolean playEffectSound(SOUNDS sound_type, SOUNDSET soundset,
                                          int volume_percentage, int delay) {
        return new Player().playEffectSound(sound_type, soundset, volume_percentage, delay);
    }

    public static void play(String sound, int volume_percentage, int delay) {
        new Player().play(sound, volume_percentage, delay);
    }

    public static void playNow(String sound, int volume_percentage, int delay) {
        new Player().playNow(sound, volume_percentage, delay);
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj, int volume_percentage, int delay) {
        new Player().playEffectSound(sound_type, obj, volume_percentage, delay);
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj, int volumePercentage,
                                       int volume_percentage, int delay) {
        new Player().playEffectSound(sound_type, obj, volumePercentage, volume_percentage, delay);
    }

    public static void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj, int volume_percentage,
                                                int delay) {
        new Player().playSoundOnCurrentThread(sound_type, obj, volume_percentage, delay);
    }

    public static void playRandomSoundFromFolder(String path, int volume_percentage, int delay) {
        new Player().playRandomSoundFromFolder(path, volume_percentage, delay);
    }

    public static void playCustomEffectSound(SOUNDS sound_type, Obj obj, int volume_percentage,
                                             int delay) {
        new Player().playCustomEffectSound(sound_type, obj, volume_percentage, delay);
    }

    public static void playHitSound(Obj obj, int volume_percentage, int delay) {
        new Player().playHitSound(obj, volume_percentage, delay);
    }

    public static void playSkillAddSound(ObjType type, PARAMETER mastery, String masteryGroup,
                                         String rank, int volume_percentage, int delay) {
        new Player().playSkillAddSound(type, mastery, masteryGroup, rank, volume_percentage, delay);
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
        playRandomStandardSound(STD_SOUNDS.SCRIBBLE, STD_SOUNDS.SCRIBBLE2, STD_SOUNDS.SCRIBBLE3);
    }

    public enum STD_SOUNDS {
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
        ButtonDown,;
        String path;
        private boolean alt;

        STD_SOUNDS() {
            this(false);
        }

        STD_SOUNDS(boolean alt) {
            this.alt = alt;
            path = STD_SOUND_PATH
                    + (alt ? toString().replace("__", "\\").replace("_", " ") + ALT_FORMAT
                    : toString() + FORMAT);

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
            return "effects\\" + name().toLowerCase() + "\\" + name();
        }
    }

}
