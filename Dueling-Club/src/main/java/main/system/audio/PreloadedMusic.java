package main.system.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by JustMe on 11/15/2017.
 */
public class PreloadedMusic implements Music {
    Sound sound;
    private boolean playing;

    public PreloadedMusic(String path) {
        FileHandle file = Gdx.files.getFileHandle(path, FileType.Absolute);
        this.sound = Gdx.audio.newSound(file);
    }

    public long play(float volume) {
        playing = true;
        return sound.play(volume);
    }

    public long play(float volume, float pitch, float pan) {
        playing = true;
        return sound.play(volume, pitch, pan);
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

    public void play() {
        playing = true;
        sound.play();
    }

    public void stop() {
        playing = false;
        sound.stop();
    }

    @Override
    public boolean isPlaying() {
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
        return 0;
    }

    @Override
    public void setVolume(float volume) {

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
        return;
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
