package eidolons.system.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;
import com.badlogic.gdx.files.FileHandle;
import main.system.auxiliary.TimeMaster;

/**
 * Created by JustMe on 11/15/2017.
 */
public class PreloadedMusic implements Music {
    private final String path;
    Sound sound;
    private boolean playing;
    private long id;
    long timeStarted;
    float duration;
    private boolean done;
    private float volume;

    public PreloadedMusic(String path) {
        this.path = path;
        FileHandle file = Gdx.files.getFileHandle(path, FileType.Absolute);
        this.sound = Gdx.audio.newSound(file);
        if (sound instanceof OpenALSound) {
            duration = ((OpenALSound) sound).duration() * 1000;
        }
    }

    @Override
    public String toString() {
        return path + ":  " + super.toString();
    }

    public String getPath() {
        return path;
    }

    public void play() {
        if (playing)
            return;
        id = sound.play(volume);
//        main.system.auxiliary.log.LogMaster.dev(path+" music Playing, sound id: " +id + " volume: " +  volume);
//        sound.setVolume(id, volume);
        done = false;
        playing = true;
        timeStarted = TimeMaster.getTime();
    }


    public long play(float volume) {
        setVolume(volume);
        play( );
        return id;
    }

    public long play(float volume, float pitch, float pan) {
        setVolume(volume);
        play();
        return id;
    }

    public long loop() {
        return sound.loop();
    }

    public long loop(float volume) {
        return sound.loop(volume);
    }

    public long loop(float volume, float pitch, float pan) {
        return sound.loop(volume, pitch, pan);
    }

    public void stop(long soundId) {
        playing = false;
        sound.stop(soundId);
    }

    public void pause(long soundId) {
        playing = false;
        sound.pause(soundId);
    }

    public void resume(long soundId) {
        playing = true;
        sound.resume(soundId);
    }

    public void setLooping(long soundId, boolean looping) {
        sound.setLooping(soundId, looping);
    }

    public void setPitch(long soundId, float pitch) {
        sound.setPitch(soundId, pitch);
    }

    public void setPan(long soundId, float pan, float volume) {
        sound.setPan(soundId, pan, volume);
    }


    public void stop() {
        sound.stop();
        playing = false;
    }

    @Override
    public boolean isPlaying() {
        done = main.system.auxiliary.TimeMaster.getTime() - timeStarted > duration;
        if (done) {
            stop();
        }
//        playing = AL10.alGetSourcei((int) id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
        return playing;
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public void setLooping(boolean isLooping) {

    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        sound.setVolume(id, volume);
    }

    @Override
    public void setPan(float pan, float volume) {

    }

    @Override
    public float getPosition() {
        return 0;
    }

    @Override
    public void setPosition(float position) {
    }

    public void pause() {
        playing = false;
        sound.pause();
    }

    public void resume() {
        playing = true;
        sound.resume();
    }

    public void dispose() {
        sound.dispose();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {

    }

    public void setVolume(long soundId, float volume) {
        sound.setVolume(soundId, volume);
    }
}
