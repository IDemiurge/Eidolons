package main.libgdx.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 1/30/2017.
 */
public class AudioMaster {
    private static AudioMaster instance; // мне не нравится синглтон
    Audio audio = Gdx.audio;
    ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
    ObjectMap<String, FileHandle> files = new ObjectMap<String, FileHandle>();
    Array<String> badFiles = new Array<String>();
    String soundPath = PathFinder.getSoundPath().replaceAll("\\\\", "/");
    String badSoundsPath = soundPath + "/badSounds";

    private AudioMaster() {

        Gdx.app.log("AudioMaster::AudioMaster()", "-- START!");
        instance = this;

        Gdx.app.log("AudioMaster::AudioMaster()", "-- sounds.size:" + sounds.size);
        FileHandle resSoundDir = Gdx.files.internal(PathFinder.getSoundPath());
        foundSoundInDir(resSoundDir);
        Gdx.app.log("AudioMaster::AudioMaster()", "-- sounds.size:" + sounds.size);
        Gdx.app.log("AudioMaster::AudioMaster()", "-- files.size:" + files.size);
        Gdx.app.log("AudioMaster::AudioMaster()", "-- badFiles.size:" + badFiles.size);
        FileHandle logWithBadFiles = new FileHandle(badSoundsPath + "Log.txt");
        logWithBadFiles.writeString(badFiles.toString().replaceAll(", ", "\n"), false);
//        Sound sound = audio.newSound(new FileHandle(new File("")));
//        sound.setVolume(id, volume);
//        String soundPath = sounds.keys().toArray().get(1);
//        Sound firstSound = sounds.get(soundPath);
//        Gdx.app.log("AudioMaster::AudioMaster()", "-- Play sound:" + soundPath);
//        long soundid = firstSound.play(1f);
//        firstSound.setLooping;
//        firstSound.setLooping(firstSound.play(), true);
//        for (String soundPath: sounds.keys()) {
//            Sound sound = sounds.get(soundPath);
//            sound.play();
//            Gdx.app.log("AudioMaster::AudioMaster()", "-- Play sound:" + soundPath);
//        }
        Gdx.app.log("AudioMaster::AudioMaster()", "-- END!");
    }

    public static AudioMaster getInstance() {
        if (instance == null) {
            instance = new AudioMaster();
        }
        return instance;
    }

    private void foundSoundInDir(FileHandle dir) {
        if (dir.isDirectory()) {
            for (FileHandle fileHandle : dir.list()) {
                if (fileHandle.isDirectory()) {
                    foundSoundInDir(fileHandle);
                } else {
                    if (!fileHandle.name().contains("desktop.ini")) {
                        try {
                            files.put(fileHandle.path(), fileHandle);
//                        String ext = fileHandle.extension();
//                        if (ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("wav")) {
//                        if(!fileHandle.name().contains("SUMMON_NIGHTMARE")) {
                            sounds.put(fileHandle.path(), audio.newSound(fileHandle));
//                            Gdx.app.log("AudioMaster::foundSoundInDir()", "-- Load sound:" + fileHandle.name());
//                        }
//                        }
                        } catch (Exception exp) {
//                            Gdx.app.error("AudioMaster::foundSoundInDir()", "-- Exp:" + exp);
//                            String filePath = fileHandle.path();
//                            String shortFilePath = filePath.replace(soundPath, "");
//                            FileHandle newFile = new FileHandle(badSoundsPath + "/" + shortFilePath);
//                            fileHandle.copyTo(newFile);
//                            badFiles.add(shortFilePath);
                        }
                    }
                }
            }
        }
    }

    public long playSound(int index) {
        if (index < sounds.size) {
            String soundPath = sounds.keys().toArray().get(index);
            Gdx.app.log("AudioMaster::playSound()", "-- Play sound:" + soundPath);
            return sounds.get(soundPath).play();
        }
        return -1;
    }

    public long playRandomSound() {
        String soundPath = sounds.keys().toArray().random();
        Gdx.app.log("AudioMaster::playRandomSound()", "-- Play sound:" + soundPath);
        return sounds.get(soundPath).play();
    }
}
