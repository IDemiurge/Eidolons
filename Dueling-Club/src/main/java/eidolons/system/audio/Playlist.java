package eidolons.system.audio;

import com.badlogic.gdx.audio.Music;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import eidolons.system.audio.MusicMaster.MUSIC_THEME;

import java.util.List;

/**
 * Created by JustMe on 9/7/2018.
 */
public class Playlist {
    MUSIC_SCOPE scope;
    MUSIC_THEME theme;

    List<Music> tracks;
    private Music playedMusic;

    private boolean shuffle = true;
    private boolean autoplay = true; //?
    private boolean loop = false;

    private Float volume;

    public Playlist(MUSIC_SCOPE scope) {
        this.scope = scope;
    }
}
