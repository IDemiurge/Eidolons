package main.libgdx.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import main.data.filesys.PathFinder;

import java.io.File;

/**
 * Created by JustMe on 1/30/2017.
 */
public class AudioMaster {
    private static AudioMaster instance; // мне не нравится синглтон
    Audio audio = Gdx.audio;
    ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();

    private AudioMaster() {
        Gdx.app.log("AudioMaster::AudioMaster()", "-- START!");
        instance = this;

        Gdx.app.log("AudioMaster::AudioMaster()", "-- sounds.size:" + sounds.size);
        FileHandle resSoundDir = Gdx.files.internal(PathFinder.getSoundPath());
        foundSoundInDir(resSoundDir);
        Gdx.app.log("AudioMaster::AudioMaster()", "-- sounds.size:" + sounds.size);
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
        if(dir.isDirectory()) {
            for (FileHandle fileHandle : dir.list()) {
                if(fileHandle.isDirectory()) {
                    foundSoundInDir(fileHandle);
                } else {
                    try {
//                        String ext = fileHandle.extension();
//                        if (ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("wav")) {
//                        if(!fileHandle.name().contains("SUMMON_NIGHTMARE")) {
                            sounds.put(fileHandle.path(), audio.newSound(fileHandle));
                            Gdx.app.log("AudioMaster::foundSoundInDir()", "-- Load sound:" + fileHandle.name());
//                        }
//                        }
                    } catch (Exception exp) {
                        Gdx.app.error("AudioMaster::foundSoundInDir()", "-- Exp:" + exp);
                    }
                }
            }
        }
    }

    public long playSound(int index) {
        if(index < sounds.size) {
            String soundPath = sounds.keys().toArray().get(index);
            Gdx.app.log("AudioMaster::playSound()", "-- Play sound:" + soundPath);
            return sounds.values().toArray().get(index).play();
        }
        return -1;
    }
}
