package main.system.sound;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.HERO_SOUNDSET;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Player {
    public static final String ALT = "_ALT";
    private static final String SOUNDSETS = "\\soundsets\\";
    private static final String FORMAT = ".mp3";
    private static final String ALT_FORMAT = ".wav";
    private static final String SOUNDSET_FOLDER_PATH = PathFinder.getSoundPath() + SOUNDSETS;
    private static Stack<String> lastplayed = new Stack<>();
    private static boolean neverRepeat = false;
    private static boolean switcher = true;
    private int volume;
    private int delay = 0;
//    Stack<SoundFx> process =new Stack();

    // mute
    public Player() {
        this(100);
    }

    public Player(int volume) {
        this.setVolume(volume * SoundMaster.getMasterVolume() / 100);

    }

    public static Stack<String> getLastplayed() {
        return lastplayed;
    }

    public static boolean isNeverRepeat() {
        return neverRepeat;
    }

    public static void setNeverRepeat(boolean neverRepeat) {
        Player.neverRepeat = neverRepeat;
    }

    public static boolean isSwitcher() {
        return switcher;
    }

    public static void setSwitcher(boolean switcher) {
        Player.switcher = switcher;
    }

    public void play(File file) {
        if (file != null) {
            play(file.getAbsolutePath());
        }
    }

    public void playStandardSound(STD_SOUNDS sound) {
        if (sound.hasAlt()) {
            if (RandomWizard.random()) {
                play(sound.getAltPath());
                return;
            }
        }
        play(sound.getPath());
    }

    public void playRandomSound(HERO_SOUNDSET hero_SOUNDSET) {
        playRandomSoundFromFolder(hero_SOUNDSET.getPropValue());
    }

    public void playRandomSoundVariant(String basePath, boolean alt) {
        if (!switcher) // TODO to playNow!
        {
            return;
        }
        if (StringMaster.isEmpty(SoundMaster.getPath())) {
            return;
        }
        String format = (alt) ? ALT_FORMAT : FORMAT;
        if (!basePath.contains(SoundMaster.getPath())) {
            basePath = SoundMaster.getPath() + basePath;
        }
        String sound = FileManager.getRandomFilePathVariant(basePath, format, !alt);
        if (sound == null) {
            sound = FileManager.getRandomFilePathVariant(basePath, format, alt);
        }
        if (sound == null) {
            format = (!alt) ? ALT_FORMAT : FORMAT;
            sound = FileManager.getRandomFilePathVariant(basePath, format, !alt);
        }
        if (sound == null) {
            sound = FileManager.getRandomFilePathVariant(basePath, format, alt);
        }
        if (sound == null) {
            // failedSounds.add(basePath);
            return;
        }
        play(sound);
    }

    public boolean playEffectSound(SOUNDS sound_type, SOUNDSET soundset) {
        if (!SoundMaster.checkSoundTypePlayer(sound_type)) {
            return false;
        }
        String sound = getRandomSound(sound_type, soundset);
        if (sound == null) {
            return false;
        }
        play(sound);
        return true;

    }

    public void play(String sound) {
        play(sound, getDelay());
    }

    public void play(final String sound, final int delay) {
        if (sound == null)
            return;
        if (!switcher) {
            return;
        }
        if (lastplayed.size() > 1) {
            if (neverRepeat) {
                if (!lastplayed.isEmpty())
                    if (lastplayed.peek().equals(sound)
                     && !lastplayed.get(lastplayed.size() - 2).equals(sound)) {
                        LogMaster.log(0, "NOT playing twice! - " + sound);
                        return;
                    }
            }
        }
        LogMaster.log(0, "Playing: " + sound);
        if (!FileManager.getFile(sound).isFile()) {
            if (sound.endsWith(FORMAT)) {
                play(sound.replace(FORMAT, "") + ALT_FORMAT);
            } else if (sound.contains(" ")) {
                play(sound.replace(" ", "_"));
            } else {
                LogMaster.log(0, "Sound not found: " + sound);
            }
            return;
        }
        play(new SoundFx(sound, volume, delay));

    }

    public void play(SoundFx sound) {
        new Thread(() -> {
            if (delay > 0) {
                WaitMaster.WAIT(delay);
            }
            playNow(sound);
        }, " sound - " + sound).start();
    }

    public void playNow(String sound) {
        playNow(new SoundFx(sound, 1, 0));
    }

    public void playNow(SoundFx sound) {
        if (SoundMaster.isBlockNextSound()) {
            SoundMaster.setBlockNextSound(false);
            return;
        }
        if (sound.getSound().endsWith(".ini"))
            return;
        try {
            Sound soundFile = Gdx.audio.newSound(Gdx.files.getFileHandle(sound.getSound(),
             FileType.Absolute));

            soundFile.setVolume(0, sound.getVolume());
            soundFile.play();
            if (neverRepeat)
            lastplayed.push(sound.getSound());

        } catch (Exception ex) {
            main.system.ExceptionMaster.printStackTrace(ex);
        }
    }


    private String getRandomSound(SOUNDS sound_type, SOUNDSET soundSet) {
        String corePath = getFullPath(sound_type, soundSet);

        File file = new File(corePath + "_1" + FORMAT);
        if (!file.isFile()) {
            LogMaster.log(1, "no sound file available for " + sound_type + " - " + soundSet);
            return null;
        }
        int i = 1;
        while (file.isFile()) {

            String newPath = corePath + "_" + i + FORMAT;
            file = new File(newPath);
            if (!file.isFile()) {
                break;
            }
            i++;
        }
        // FileManager.getRandomFilePathVariant(corePath, FORMAT)
        return corePath + "_" + (new Random().nextInt(i - 1) + 1) + FORMAT;
    }

    private String getFullPath(SOUNDS sound_type, SOUNDSET soundSet) {
        return SoundMaster.getPath() + sound_type.getPath() + "_" + soundSet.getName();
    }

    public void playEffectSound(final SOUNDS sound_type, final Obj obj) {
        playEffectSound(sound_type, obj, 100);
    }

    public void playEffectSound(final SOUNDS sound_type, final Obj obj, int volumePercentage) {
        try {
            playSoundOnCurrentThread(sound_type, obj);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
//        new Thread(() -> playSoundOnCurrentThread(sound_type, obj), "playing " + sound_type + " sound for " + obj).start();
    }


    public void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj) {

        String prop = obj.getProperty(G_PROPS.CUSTOM_SOUNDSET);
        if (!StringMaster.isEmpty(prop)) {
            if (!StringMaster.isEmpty(prop)) {
                if (playCustomSoundsetSound(prop, sound_type)) {
                    return;
                }
            }
        }
        prop = obj.getProperty(G_PROPS.SOUNDSET);
        SOUNDSET soundSet = new EnumMaster<SOUNDSET>().retrieveEnumConst(SOUNDSET.class, obj
         .getProperty(G_PROPS.SOUNDSET));
        boolean result;
        if (soundSet == null) {
            result = playCustomSoundsetSound(prop, sound_type);
        } else {
            result = playEffectSound(sound_type, soundSet);
        }

        if (!result) {
            if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.SPELLS) {
                String autopath = SOUNDSET_FOLDER_PATH + "\\spells\\"
                 + obj.getAspect().toString().toLowerCase() + "\\"
                 + obj.getProperty(G_PROPS.SPELL_GROUP) + "\\" + obj.getName();

                result = playCustomSoundsetSound(autopath, sound_type);
                if (result) {
                    return;
                }
                // spell group level
                autopath = StringMaster.cropLastPathSegment(autopath);
                result = playCustomSoundsetSound(autopath + obj.getProperty(G_PROPS.SPELL_GROUP),
                 sound_type);
                if (result) {
                    return;
                }
                // aspect level
                autopath = StringMaster.cropLastPathSegment(autopath)
                 + obj.getAspect().toString().toLowerCase();

                result = playCustomSoundsetSound(autopath, sound_type);

            }
        }
    }

    public void playRandomSoundFromFolder(String path) {
        if (!path.contains(PathFinder.getSoundPath())) {
            path = PathFinder.getSoundPath() + path;
//            StringMaster.addMissingPathSegments(
        }
        File file = FileManager.getRandomFile(path);

        play(file);
    }

    private boolean playCustomSoundsetSound(String prop, SOUNDS sound_type) {
        if (checkSoundTypeOff(sound_type))
            return false;
        if (!prop.contains("soundsets")) {
            prop = "soundsets\\" + prop;
        }
        String path = PathFinder.getSoundPath()
         // SOUNDSET_FOLDER_PATH + "//"
         + prop.replace(PathFinder.getSoundPath(), "");
        File folder = new File(path);
        String fileName = "";
        if (!folder.isDirectory()) {
            fileName = StringMaster.cropFormat(StringMaster.getLastPathSegment(prop));
            folder = FileManager.getFile(StringMaster.cropLastPathSegment(path));
        }
        if (!folder.isDirectory()) {
            return false;
        }
        String suffix = fileName + getSoundsetSuffix(sound_type);
        List<File> files = FileManager.findFiles(folder, suffix, true, false);
        if (files.isEmpty()) {
            if (fileName.isEmpty()) {
                suffix = sound_type.toString();
            } else {
                suffix = fileName + "_" + sound_type.toString();
            }
            files = FileManager.findFiles(folder, suffix, true, false);
        }
        if (files.isEmpty()) {
            return false;
        }

        int volume = getVolume()*checkAdditionalVolume(sound_type)/100;
        File file = files.get(RandomWizard.getRandomIndex(files, new Random()));
        try {
            play(file.getAbsolutePath(), volume , getDelay());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return true;
    }

    protected int checkAdditionalVolume(SOUNDS sound) {
        return 100;
    }
    protected boolean checkSoundTypeOff(SOUNDS sound_type) {
        return false;
    }

    private String getSoundsetSuffix(SOUNDS sound_type) {
        switch (sound_type) {
            case ZONE:
                break;
            case ATTACK:
                return "_atk";
            case CAST:
                return "_cast";
            case DEATH:

                return "_death";

            case HIT:
                return "_hit";
            case IMPACT:
                return "_impact";
            case MOVEMENT:
                break;
            case PRECAST:
                return "_precast";
            case READY:
                break;
            case SPEC_ACTION:
                break;
            case FAIL:
                break;
            case TAUNT:
                break;
            case THREAT:
                break;
            case WHAT:
                return "_bat";
        }
        return "";
    }

    public void playCustomEffectSound(SOUNDS sound_type, Obj obj) {
        playRandomSoundVariant(getCustomSoundsetPath(sound_type, obj), false);
    }

    private String getCustomSoundsetPath(SOUNDS sound_type, Obj obj) {
        boolean add_name = sound_type != SOUNDS.READY;

        String string = SoundMaster.getPath()
         + getBasePath(obj.getProperty(G_PROPS.CUSTOM_SOUNDSET), false);
        if (add_name) {
            string += StringMaster.FORMULA_REF_SEPARATOR + sound_type.toString();
        }
        return string;
    }

    private String getBasePath(String property, boolean b) {
        return StringMaster.clipEnding(property, (b) ? StringMaster.FORMULA_REF_SEPARATOR
         : StringMaster.FORMAT_CHAR);
    }

    public void playHitSound(Obj obj) {
        playEffectSound(SOUNDS.HIT, obj);
    }

    public void playSkillAddSound(ObjType type, PARAMETER mastery, String masteryGroup, String rank) {

        masteryGroup = ContentValsManager.getMasteryGroup(mastery, masteryGroup);

        String soundPath = SoundMaster.getPath() + "std\\skills\\" + "NEW_SKILL_" + masteryGroup
         + FORMAT;
        if (!FileManager.getFile(soundPath).isFile()) {
            soundPath = SoundMaster.getPath() + "std\\skills\\" + "NEW_SKILL_GENERIC" + FORMAT;
        }
        play(soundPath);
    }

    // FULL PARAMETER METHODS

    public String getSoundFileForItem(Obj item, SOUNDS soundType) {
        return item.getOBJ_TYPE();

    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void play(File file, int volume_percentage, int delay) {
        play(file);
        setDelay(delay);
        setVolume(volume_percentage);
    }

    public void playStandardSound(STD_SOUNDS sound, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playStandardSound(sound);
    }

    public void playRandomSound(HERO_SOUNDSET hero_SOUNDSET, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playRandomSound(hero_SOUNDSET);
    }

    public void playRandomSoundVariant(String basePath, boolean alt, int volume_percentage,
                                       int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playRandomSoundVariant(basePath, alt);
    }

    public boolean playEffectSound(SOUNDS sound_type, SOUNDSET soundset, int volume_percentage,
                                   int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        return playEffectSound(sound_type, soundset);
    }

    public void play(String sound, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        play(sound);
    }

    public void playNow(String sound, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playNow(sound, volume_percentage, delay);
    }

    public void playEffectSound(SOUNDS sound_type, Obj obj, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playEffectSound(sound_type, obj);
    }

    public void playEffectSound(SOUNDS sound_type, Obj obj, int volumePercentage,
                                int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playEffectSound(sound_type, obj, volumePercentage);
    }

    public void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj, int volume_percentage,
                                         int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playSoundOnCurrentThread(sound_type, obj);
    }

    public void playRandomSoundFromFolder(String path, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playRandomSoundFromFolder(path);
    }

    public void playCustomEffectSound(SOUNDS sound_type, Obj obj, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playCustomEffectSound(sound_type, obj);
    }

    public void playHitSound(Obj obj, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playHitSound(obj);
    }

    public void playSkillAddSound(ObjType type, PARAMETER mastery, String masteryGroup,
                                  String rank, int volume_percentage, int delay) {
        setDelay(delay);
        setVolume(volume_percentage);
        playSkillAddSound(type, mastery, masteryGroup, rank);
    }

    private Clip createSoundClip(String sound) throws UnsupportedAudioFileException, IOException,
     LineUnavailableException {
        /*
         * Once you have a handle to the inputStream, get the audioInputStream and do the rest.

		InputStream is = getClass().getResourceAsStream("......");
		AudioInputStream ais = AudioSystem.getAudioInputStream(is);
		Clip clip = AudioSystem.getClip();
		clip.open(ais);
		 */
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream ulawIn = AudioSystem.getAudioInputStream(new File(sound));

            // define a target AudioFormat that is likely to be supported by
            // your audio hardware,
            // i.e. 44.1kHz sampling rate and 16 bit samples.
            AudioInputStream pcmIn = AudioSystem.getAudioInputStream(new AudioFormat(
             AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 1, 2, 44100f, true), ulawIn);

            clip.open(pcmIn);
            // clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // AudioInputStream audioStream =
        // AudioSystem.getAudioInputStream(FileManager.getFile(sound));
        // AudioFormat baseFormat = audioStream.getFormat();
        // AudioFormat decodedFormat = new
        // AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat
        // .getSampleRate(), 16, baseFormat.getChannels(),
        // baseFormat.getChannels() * 2,
        // baseFormat.getSampleRate(), false);
        // AudioInputStream audioStream2 =
        // AudioSystem.getAudioInputStream(decodedFormat, audioStream);
        //
        // Clip clip = AudioSystem.getClip();
        // clip.open(audioStream2);
        return clip;
    }


    // VOLUME CONTROL TO BE ADDED TODO
    // main.system.auxiliary.LogMaster.log(1, "playing - " + sound +
    // " with " + volumePercentage
    // + " volumePercentage ");
    // float gain = new Float(getVolume()) * volumePercentage / 100 - 100;
    // Clip clip = null;
    // if (gain != 0.0f)
    // try {
    // clip = createSoundClip(sound);
    // } catch (UnsupportedAudioFileException e) {
    // main.system.ExceptionMaster.printStackTrace(e);
    // } catch (IOException e) {
    // main.system.ExceptionMaster.printStackTrace(e);
    // } catch (LineUnavailableException e) {
    // main.system.ExceptionMaster.printStackTrace(e);
    // }
    // if (clip != null) {
    // FloatControl gainControl = (FloatControl) clip
    // .getControl(FloatControl.Type.MASTER_GAIN);
    // gainControl.setValue(gain);
    // }


    public enum SOUND_LEVEL {
        TINY, SMALL, MODERATE, LARGE, HUGE
    }
}
