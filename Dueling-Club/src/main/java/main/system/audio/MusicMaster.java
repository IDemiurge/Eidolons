package main.system.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.options.OptionsMaster;
import main.system.options.SoundOptions.SOUND_OPTION;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
//a folder tree per music theme!
//standard path structure

public class MusicMaster {
    public static final int PERIOD = 3500;
    public static final String MOMENT_PATH = "\\music\\moments\\";
    public static final String ATMO_PATH = "\\music\\atmo\\";
    public static final String MUSIC_PATH = "\\music\\";
    static Map<MUSIC_SCOPE, Integer> indexMap;
    static Map<MUSIC_SCOPE, List<Integer>> indexListMap;
    private static MusicMaster instance;
    Stack<String> playList;
    Stack<String> cachedPlayList;
    MUSIC_SCOPE scope;
    MUSIC_VARIANT variant;
    MUSIC_THEME theme;
    AMBIENCE ambience = AMBIENCE.FOREST_NIGHT;
    private boolean shuffle = false;
    private boolean autoplay = true;
    private boolean loopPlaylist = false;
    private Map<String, Music> musicCache = new XLinkedMap<>();
    private Music playedMusic;
    private boolean stopped;
    // IDEA: map music per scope to resume()
// TODO AMBIENT SOUNDS -

    private MusicMaster() {
        init();
        GuiEventManager.bind(GuiEventType.MUSIC_STOP, p -> {
            stop();
        });
        GuiEventManager.bind(GuiEventType.MUSIC_RESUME, p -> {
            resume();
        });
        GuiEventManager.bind(GuiEventType.MUSIC_PAUSE, p -> {
            pause();
        });
        GuiEventManager.bind(GuiEventType.MUSIC_START, p -> {
            Object data = p.get();
            if (data == null) {
                checkNewMusicToPlay();
                return;
            }
            if (data instanceof String) {
                playMusic(data.toString());
                return;
            }
            if (data instanceof MUSIC_SCOPE) {
                scope = (MUSIC_SCOPE) data;
                musicReset();
            }
            if (data instanceof MUSIC_THEME) {
                theme = (MUSIC_THEME) data;
                musicReset();
            }
            if (data instanceof MUSIC_VARIANT) {
                variant = (MUSIC_VARIANT) data;
                musicReset();
            }
        });
        startLoop();
    }

    public static MusicMaster getInstance() {
        if (instance == null) {
            instance = new MusicMaster();
        }
        return instance;
    }

    public static void playMoment(MUSIC_MOMENT moment) {
        String corePath = moment.getCorePath();
        DC_SoundMaster.playRandomSoundVariant(corePath, false);
    }

    public static void resetVolume() {
        Float volume =
         getInstance().getVolume();
        getInstance().getPlayedMusic().setVolume(volume);
    }

    public Stack<String> getPlayList() {
        return playList;
    }

    public Music getPlayedMusic() {
        return playedMusic;
    }

    public void init() {
        scope = MUSIC_SCOPE.ATMO;
        variant = MUSIC_VARIANT.EIDOLONS_SCORE;
        autoplay = true;
    }

    private void musicReset() {
        //cache to scope
        stop();
        playList.clear();
        checkNewMusicToPlay();
        resume();
    }

    private void checkUpdateTypes() {
        if (scope==MUSIC_SCOPE.ATMO){
            scope = MUSIC_SCOPE.BATTLE;
        } else {
            scope = MUSIC_SCOPE.ATMO;
            if (theme==null ){
                theme = MUSIC_THEME.DARK;
            } else {
                theme = MUSIC_THEME.GOODLY;
            }
        }

    }

    private void checkNewMusicToPlay() {
        if (ListMaster.isNotEmpty(playList)) {
            playMusic(playList.pop());
            return;
        }
        if (!autoplay)
            return;
        String folder = getMusicFolder();
        while (true) {
            List<File> files = FileManager.getFilesFromDirectory(folder, false);

            if (files.isEmpty()) {
                //change?
                folder = StringMaster.cropLastPathSegment(folder);
                continue;
            }

            playList = new Stack<>();
            for (String sub : FileManager.getFilePaths(files)) {
                if (isMusic(sub)) {
                    playList.add(sub);
                }
            } //bind to scope?
            if (shuffle) {
                Collections.shuffle(playList);
            }
            cachedPlayList = new Stack<>();
            cachedPlayList.addAll(playList);
            break;
        }
        checkUpdateTypes();
    }

    private void checkAmbience() {
        if (ambience != null) {
//            getAmbienceFolder();

        }
    }

    private boolean isMusic(String sub) {
        if (StringMaster.getFormat(sub).contains("ini"))
            return false;
        return true;
    }

    private String getMusicFolder() {
        StrPathBuilder builder = new StrPathBuilder(PathFinder.getMusicPath());
        if (scope != null)
            builder.append(StringMaster.getWellFormattedString(
             scope.toString()));
        if (variant != null)
            builder.append(StringMaster.getWellFormattedString(
             variant.toString()));
        if (theme != null)
            builder.append(StringMaster.getWellFormattedString(
             theme.toString()));
        return builder.toString();
    }

    private void startLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    WaitMaster.WAIT(PERIOD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkAmbience();
                if (stopped)
                    continue;
                if (playedMusic != null) {
                    if (!playedMusic.isPlaying()) {
                        checkNewMusicToPlay();
                    }
                } else {
                    checkNewMusicToPlay();
                }
            }
        }, "Music Thread").start();

    }

    public void resume() {
        if (playedMusic != null)
            playedMusic.play();
        stopped = false;
    }

    public void pause() {
        if (playedMusic != null)
            playedMusic.pause();
        stopped = true;
    }

    public void stop() {
        if (playedMusic != null)
            playedMusic.stop();
        stopped = true;
    }

    public void playMusic(String path) {
        path = StringMaster.addMissingPathSegments(path, PathFinder.getMusicPath());
        //TODO save!
        if (playedMusic != null)
            playedMusic.stop();

        playedMusic = musicCache.get(path);
        if (playedMusic == null) {
            FileHandle file = Gdx.files.getFileHandle(path, FileType.Absolute);
            playedMusic = Gdx.audio.newMusic(file);
            musicCache.put(path, playedMusic);
        }
        Float volume =
         getVolume();
        playedMusic.setVolume(volume);
        playedMusic.play();
    }

    private Float getVolume() {
        return new Float(OptionsMaster.getSoundOptions().
         getIntValue(SOUND_OPTION.MUSIC_VOLUME)) / 100 * SoundMaster.getMasterVolume() / 100;
    }

    public void setScope(MUSIC_SCOPE scope) {
        this.scope = scope;
    }

    public void setVariant(MUSIC_VARIANT variant) {
        this.variant = variant;
    }

    public void setTheme(MUSIC_THEME theme) {
        this.theme = theme;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    public void setLoopPlaylist(boolean loopPlaylist) {
        this.loopPlaylist = loopPlaylist;
    }

    public void autoPlaylist() {
        try {
            Robot robot = new Robot();

            // Simulate a mouse click
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            // Simulate a key press
            robot.keyPress(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_A);

        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    // SOUNDS VS THEME-FILES?!
    public enum AMBIENCE {
        FOREST_NIGHT(),
        FOREST_DAY,
        TAVERN,
        TEMPLE,
        CASTLE,
        EVIL,
        DUNGEON,
        CAVE,
        MINE,
        MOUNTAINS,
        NORTH,
        SEA,;

        AMBIENCE(ATMO_SOUND_TYPE... TYPES) {

        }
    }

    public enum ATMO_SCOPE {
        MENU, HC, BATTLE
    }

    public enum ATMO_SOUND_TYPE {
        CREEK,
        WHISPER,
        WAVES,
        WIND_HOWL,
        WIND_BLOW,
        ROCK_FALL,
        BIRD_CHIRP,
        RIVER,
        WOLF_HOWL,
        DOG_BARK,
        HUMAN_CHATTER,
        HUMAN_WHISPER,
        PICK_AXE,

    }

    public enum MUSIC_MOMENT {
        DEFEAT, VICTORY, AFTER_DEFEAT, AFTER_VICTORY, WAVE_CLEARED, WAVE_SPAWN, // regular/elite/boss?
        WELCOME; // VARIANTS?

        public String getCorePath() {
            return MOMENT_PATH + name();
        }
    }

    public enum MUSIC_SCOPE {
        MENU, ATMO, BATTLE
    }

    public enum MUSIC_THEME {
        DARK,
        BOSS,
        GOODLY,
    }

    public enum MUSIC_TYPE {
        ATMO, MUSIC, BONUS
    }

    public enum MUSIC_VARIANT {
        EIDOLONS_SCORE,
        OLD_SCHOOL,
        DARK,
        FANTASY,
        EPIC,
        SCI_FI,
    }

    // stopScope()

    public class MusicData {

    }

}
