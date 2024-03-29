package libgdx.audio;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.exploration.story.cinematic.Cinematics;
import libgdx.bf.GridMaster;
import libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import main.content.CONTENT_CONSTS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.sound.AudioEnums.SOUNDS;
import main.system.sound.Player;
import main.system.sound.SoundFx;

import java.util.Stack;

/**
 * Created by JustMe on 8/27/2017.
 */
public class SoundPlayer extends Player {
    Stack<SoundFx> playQueue = new Stack();
    //    List<SoundFx> playing = new ArrayList<>();

    DungeonScreen dungeonScreen;
    private Vector2 position;
    private float waitTime = 0;
    private boolean cinematicSoundOverride; //TODO refactor

    public SoundPlayer(DungeonScreen dungeonScreen) {
        this.dungeonScreen = dungeonScreen;

    }



    public enum SOUND_TYPE {
        VOICE,
    }

    public void playEffectSound(final SOUNDS sound_type, final Obj obj) {
        if (Cinematics.ON) {
            if (!cinematicSoundOverride)
                return;
        }
        super.playEffectSound(sound_type, obj);
    }

    public void playEffectSound(final SOUNDS sound_type, final Obj obj, int volumePercentage) {
        if (Cinematics.ON) {
            if (!cinematicSoundOverride)
                return;
        }
        super.playEffectSound(sound_type, obj, volumePercentage);
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
        return null;
    }

    protected boolean checkSoundTypeOff(SOUNDS sound) {
        SOUND_TYPE sound_type = getSoundType(sound);
        if (sound_type != null)
            switch (sound_type) {
                case VOICE:
                    return OptionsMaster.getSoundOptions().
                            getBooleanValue(SOUND_OPTION.VOICE_OFF);

            }
        return false;
    }

    protected int checkAdditionalVolume(SOUNDS sound) {
        SOUND_TYPE sound_type = getSoundType(sound);
        if (sound_type != null)
            switch (sound_type) {
                case VOICE:
                    return OptionsMaster.getSoundOptions().
                            getIntValue(SOUND_OPTION.VOICE_VOLUME);

            }
        return 100;
    }

    @Override
    public int getVolume() {
        //        OptionsMaster.getVar
        return super.getVolume();
    }

    public void playSoundOnCurrentThread(SOUNDS sound_type, Obj obj) {
        CONTENT_CONSTS.SOUNDSET soundSet = DC_SoundMaster.getSoundset(obj);
        playEffectSound(sound_type, soundSet);
    }

    @Override
    public void play(SoundFx sound) {
        if (position != null) {
            sound.setOrigin(position);
            setPosition(null);
        }
        playQueue.add(sound);
    }

    @Override
    public void play(String path, Coordinates c, float vol) {
        Vector2 v = GridMaster.getCenteredPos(c);
        playQueue.add(new SoundFx(path, vol, 0, v));
    }
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void doPlayback(float delta) {
        if (playQueue.isEmpty()) {
        } else {
            if (isWaitBetweenSounds())
                if (waitTime >= 0) {
                    waitTime -= delta;
                    return;
                }

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
                    if (volume <= 0.1f)
                        return;
                }
            playNow(soundFx);
            waitTime = getWaitTime(soundFx);
            setVolume(OptionsMaster.getSoundOptions().getIntValue(SOUND_OPTION.MASTER_VOLUME));
        }
        //fade in or out?
        //pause sounds
    }

    public void setCinematicSoundOverride(boolean cinematicSoundOverride) {
        this.cinematicSoundOverride = cinematicSoundOverride;
    }
    private boolean isWaitBetweenSounds() {
        return false;
    }

    private float getWaitTime(SoundFx soundFx) {
        return 0.51f;
    }

    @Override
    public void setVolume(int volume) {
        super.setVolume(volume);
    }

    @Override
    public void setPositionFor(Coordinates c) {
        setPosition(GridMaster.getCenteredPos(c));
    }
}
