package main.libgdx.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;

/**
 * Created by JustMe on 1/30/2017.
 */
public class AudioMaster {
    Audio audio = Gdx.audio;
    public AudioMaster() {
        Sound sound = audio.newSound(new FileHandle(new File("")));
//        sound.setVolume(id, volume);
    }
}
