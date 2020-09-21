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

import static main.system.sound.AudioEnums.STD_SOUNDS.*;

public class SoundMaster {

    public static final String ALT = "_ALT";
    public static final String STD_FORMATS = "mp3;flac;ogg;m4a;wav";
    public static final String STD_SOUND_PATH = PathFinder.getSoundPath() + "STD/";
    public static final String FORMAT = ".mp3";
    public static final String ALT_FORMAT = ".wav";
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

    public static void playRandomStandardSound(AudioEnums.STD_SOUNDS... sounds) {
        playStandardSound(new RandomWizard<AudioEnums.STD_SOUNDS>().getRandomListItem(Arrays.asList(sounds)));

    }

    public static void playStandardSound(AudioEnums.STD_SOUNDS sound) {
        if (isOn()) getPlayer().playStandardSound(sound);
    }

    private static Player getPlayer() {
        return new Player();
    }

    public static void playRandomSound(HERO_SOUNDSET hero_SOUNDSET) {
        if (isOn()) getPlayer().playRandomSound(hero_SOUNDSET);
    }

    public static void playRandomSoundVariant(String basePath, boolean alt) {
        if (isOn())
            getPlayer().playRandomSoundVariant(basePath, alt);
    }

    public static boolean playEffectSound(AudioEnums.SOUNDS sound_type, SOUNDSET soundset) {
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

    public static void playEffectSound(AudioEnums.SOUNDS sound_type, Obj obj) {
        if (isOn()) getPlayer().playEffectSound(sound_type, obj);
    }

    public static void playSoundOnCurrentThread(AudioEnums.SOUNDS sound_type, Obj obj) {
        if (isOn()) getPlayer().playSoundOnCurrentThread(sound_type, obj);
    }

    public static void playRandomSoundFromFolder(String path) {
        if (isOn()) getPlayer().playRandomSoundFromFolder(path);
    }

    public static void playCustomEffectSound(AudioEnums.SOUNDS sound_type, Obj obj) {
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

    public static void playStandardSound(AudioEnums.STD_SOUNDS sound, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playStandardSound(sound, volume_percentage, delay);
    }

    public static void playRandomSound(HERO_SOUNDSET hero_SOUNDSET, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playRandomSound(hero_SOUNDSET, volume_percentage, delay);
    }

    public static void playRandomSoundVariant(String basePath, boolean alt, int volume_percentage,
                                              int delay) {
        if (isOn()) getPlayer().playRandomSoundVariant(basePath, alt, volume_percentage, delay);
    }

    public static boolean playEffectSound(AudioEnums.SOUNDS sound_type, SOUNDSET soundset,
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

    public static void playEffectSound(AudioEnums.SOUNDS sound_type, Obj obj, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playEffectSound(sound_type, obj, volume_percentage, delay);
    }

    public static void playEffectSound(AudioEnums.SOUNDS sound_type, Obj obj, int volumePercentage,
                                       int volume_percentage, int delay) {
        if (isOn()) getPlayer().playEffectSound(sound_type, obj, volumePercentage, volume_percentage, delay);
    }

    public static void playSoundOnCurrentThread(AudioEnums.SOUNDS sound_type, Obj obj, int volume_percentage,
                                                int delay) {
        if (isOn()) getPlayer().playSoundOnCurrentThread(sound_type, obj, volume_percentage, delay);
    }

    public static void playRandomSoundFromFolder(String path, int volume_percentage, int delay) {
        if (isOn()) getPlayer().playRandomSoundFromFolder(path, volume_percentage, delay);
    }

    public static void playCustomEffectSound(AudioEnums.SOUNDS sound_type, Obj obj, int volume_percentage,
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

    public static boolean checkSoundTypePlayer(AudioEnums.SOUNDS sound_type) {
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

    public static SOUNDSET getSoundset(Obj obj) {
        return null;
    }

}
