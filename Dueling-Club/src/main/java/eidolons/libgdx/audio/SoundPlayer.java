package eidolons.libgdx.audio;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import main.system.sound.Player;
import main.system.sound.SoundFx;
import main.system.sound.SoundMaster.SOUNDS;

import java.util.Stack;

/**
 * Created by JustMe on 8/27/2017.
 */
public class SoundPlayer extends Player {
    Stack<SoundFx> playQueue = new Stack();
//    List<SoundFx> playing = new ArrayList<>();

    DungeonScreen dungeonScreen;
    private Vector2 position;

    public SoundPlayer(DungeonScreen dungeonScreen) {
        this.dungeonScreen = dungeonScreen;

    }
    public enum SOUND_TYPE{
        VOICE,
    }
    protected SOUND_TYPE getSoundType(SOUNDS sound_type) {
        switch (sound_type) {
            case ATTACK:
            case HIT:
            case WHAT:
            case FLEE:
            case TAUNT:
            case THREAT:
            case DEATH:
            case READY:
            case FAIL:
            case CHANNELING:
            case W_CHANNELING:
            case FALL:
                return SOUND_TYPE.VOICE;

        }
        return null ;
    }
        protected boolean checkSoundTypeOff(SOUNDS sound) {
            SOUND_TYPE sound_type = getSoundType(sound);
            if (sound_type!=null )
        switch (sound_type) {
            case VOICE:
                return OptionsMaster.getSoundOptions().
                 getBooleanValue(SOUND_OPTION.VOICE_OFF);

        }
        return false;
    }
    protected int checkAdditionalVolume(SOUNDS sound) {
        SOUND_TYPE sound_type = getSoundType(sound);
        if (sound_type!=null )
            switch (sound_type) {
                case VOICE:
                    return OptionsMaster.getSoundOptions().
                     getIntValue(SOUND_OPTION.VOICE_VOLUME);

            }
        return 100;
    }
    @Override
    public int getVolume() {
//        OptionsMaster.get
        return super.getVolume();
    }

    @Override
    public void play(SoundFx sound) {
        if (position != null) {
            sound.setOrigin(position);
            setPosition(null);
        }
        playQueue.add(sound);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void doPlayback(float delta) {
        if (!playQueue.isEmpty()) {
            SoundFx soundFx = playQueue.pop();
//                playing.add(sound);
//                if (sound.getDelay()!=0)
//                    sound.setDelay( - delta);
            if (dungeonScreen != null)
                if (soundFx.getOrigin() != null) {
                    float x = dungeonScreen.controller.getXCamPos();
                    float y = dungeonScreen.controller.getYCamPos();
                    float distance = soundFx.getOrigin().dst(x, y);
                    distance *= dungeonScreen.controller.getZoom();
                    float volume =
                     Math.max(10, getVolume() / Math.max(1, (distance) / 200)) / 100;
                    soundFx.setVolume(volume);
                    if (volume<=0.1f)
                        return;
                }
            playNow(soundFx);
            setVolume(OptionsMaster.getSoundOptions().getIntValue(SOUND_OPTION.MASTER_VOLUME));
        }
        //fade in or out?
        //pause sounds
    }

    @Override
    public void setVolume(int volume) {
        super.setVolume(volume);
    }
}
