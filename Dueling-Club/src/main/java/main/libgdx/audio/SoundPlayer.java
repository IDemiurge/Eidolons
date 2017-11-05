package main.libgdx.audio;

import com.badlogic.gdx.math.Vector2;
import main.libgdx.screens.DungeonScreen;
import main.system.sound.Player;
import main.system.sound.SoundFx;

import java.util.Stack;

/**
 * Created by JustMe on 8/27/2017.
 */
public class SoundPlayer extends Player {
    Stack<SoundFx> playQueue = new Stack();
//    List<SoundFx> playing = new LinkedList<>();

    DungeonScreen dungeonScreen;
    private Vector2 position;

    public SoundPlayer(DungeonScreen dungeonScreen) {
        this.dungeonScreen = dungeonScreen;

    }

    @Override
    public int getVolume() {
//        OptionsMaster.get
        return super.getVolume();
    }

    @Override
    public void play(SoundFx sound) {
        if (position!=null )
        {
            sound.setOrigin(position);
            setPosition(null );
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
                        float x = dungeonScreen .getController().getXCamPos();
                        float y = dungeonScreen.getController().getYCamPos();
                        float distance = soundFx.getOrigin().dst(x, y);
                        distance *= dungeonScreen.getController().getZoom();
                        float volume =
                         Math.max(10, getVolume() / Math.max(1, (distance) / 200 ))/100;
                        soundFx.setVolume(volume);
                    }
                playNow(soundFx);
            }
            //fade in or out?
            //pause sounds
    }

}
