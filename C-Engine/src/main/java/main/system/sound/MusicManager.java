package main.system.sound;

import main.system.auxiliary.data.FileManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Map;

public class MusicManager {

    private Map<String, Clip> musMap;

    public void resume(Music mus) {

    }

    public void play(Music mus, boolean fromStart) {
        String path = mus.getFullPath();
        Clip clip = musMap.get(mus.getFullPath());
        try {
            if (fromStart) {
                stopPlay(mus, true);
            }

            if (clip == null || fromStart) {
                AudioInputStream inputStream = AudioSystem
                        .getAudioInputStream(FileManager.getFile(path));
                clip = AudioSystem.getClip();
                clip.open(inputStream);
            }
            clip.start();
            musMap.put(path, clip);
        } catch (Exception e) {
            stopPlay(mus, true);
            System.err.println(e.getMessage());
        }
    }

    public void stopPlay(Music mus, boolean close) {
        Clip clip = musMap.get(mus.getFullPath());
        if (clip != null) {
            clip.stop();
            if (close) {
                clip.close();
                clip = null;
            }
        }
    }

}
