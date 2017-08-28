package main.system.sound;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by JustMe on 8/27/2017.
 */
public class SoundFx {
    String sound;
    float volume;
    float delay;
    Vector2 origin;

    public SoundFx(String sound, float volume, float delay, Vector2 origin) {
        this.sound = sound;
        this.volume = volume;
        this.delay = delay;
        this.origin = origin;
    }

    public SoundFx(String sound, int volume, int delay) {
        this.sound = sound;
        this.volume = volume;
        this.delay = delay;
    }

    public void setOrigin(Vector2 origin) {
        this.origin = origin;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public String getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }
}
