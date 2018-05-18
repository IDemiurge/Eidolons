package eidolons.system.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;
//a folder tree per music theme!
//standard path structure

public class MusicMaster {
    public static final int PERIOD = 3500;
    public static final String MASTER_PATH = PathFinder.getMusicPath() + "\\main\\";
    public static final boolean MASTER_MODE = true;

    public static final String MOMENT_PATH = "\\music\\moments\\";
    public static final String ATMO_PATH = "\\music\\atmo\\";
    public static final String MUSIC_PATH = "\\music\\";
    static Map<MUSIC_SCOPE, Integer> indexMap;
    static Map<MUSIC_SCOPE, List<Integer>> indexListMap;
    private static MusicMaster instance;
    private static boolean on = true;
    Stack<String> playList;
    Stack<String> cachedPlayList;
    MUSIC_SCOPE scope = MUSIC_SCOPE.MENU;
    MUSIC_VARIANT variant;
    MUSIC_THEME theme;
    AMBIENCE ambience = AMBIENCE.FOREST_NIGHT;
    private boolean shuffle = true;
    private boolean autoplay = true;
    private boolean loopPlaylist = false;
    private Map<String, Music> musicCache = new XLinkedMap<>();
    private Music playedMusic;
    private boolean stopped;
    private boolean running;

    private Map<MUSIC_SCOPE, Music> trackCache = new XLinkedMap<>();

    private Thread thread;
    private boolean interruptOnSet;
    private boolean mainThemePlayed;
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
                try {
                    checkNewMusicToPlay();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
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

    public static void resetSwitcher() {
        on = !(OptionsMaster.getSoundOptions().
         getBooleanValue(SOUND_OPTION.MUSIC_OFF));

        if (!on) {
            getInstance().pause();
        } else {
            getInstance().resume();
        }

    }

    public static void resetVolume() {
        if (getInstance().getPlayedMusic() != null)
            getInstance().getPlayedMusic().setVolume(getInstance().getVolume());
    }

    public void scopeChanged(MUSIC_SCOPE scope) {
        if (!isRunning())
            return;
        if (!isOn())
            return;
        trackCache.put(this.scope, playedMusic);
        playList.clear();
        setScope(scope);
        pause();
        playedMusic = trackCache.get(scope);
        if (playedMusic == null) {
            checkNewMusicToPlay();
        }
        resume();
        //fade? :)
        //
    }

    public Stack<String> getPlayList() {
        return playList;
    }

    public Music getPlayedMusic() {
        return playedMusic;
    }

    public void init() {
        scope = MUSIC_SCOPE.ATMO;
        variant =
         new EnumMaster<MUSIC_VARIANT>().retrieveEnumConst(MUSIC_VARIANT.class,
          OptionsMaster.getSoundOptions().getValue(SOUND_OPTION.MUSIC_VARIANT));
        if (variant == null) {
            variant = MUSIC_VARIANT.EIDOLONS_SCORE;
        }
        if (RandomWizard.random()) {
            theme = RandomWizard.random() ? MUSIC_THEME.GOODLY : MUSIC_THEME.DARK;
        }
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
        if (scope == MUSIC_SCOPE.ATMO) {
            scope = MUSIC_SCOPE.BATTLE;
        } else {
            scope = MUSIC_SCOPE.ATMO;
            if (theme == null) {
                theme = MUSIC_THEME.DARK;
            } else {
                theme = MUSIC_THEME.GOODLY;
            }
        }

    }

    private void checkNewMusicToPlay() {
        if (!interruptOnSet)
            if (playedMusic != null)
                if (playedMusic.isPlaying())
                    return;
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
        String format = StringMaster.getFormat(sub).toLowerCase();
        // white list is a better idea probably
        // Drive on macos spam 'icon' files all over the place
        return format.contains("mp3") || format.contains("wav");
    }

    private String getMusicFolder() {
        if (MASTER_MODE) {
            if (Eidolons.game != null && Eidolons.game.isStarted())
                if (!ExplorationMaster.isExplorationOn())
//        if (scope==MUSIC_SCOPE.BATTLE)
                    return MASTER_PATH + "battle";
            if (!mainThemePlayed || scope == MUSIC_SCOPE.MENU) {
                mainThemePlayed = true;
                return MASTER_PATH + "menu";
            }
            return MASTER_PATH;
        }
        StrPathBuilder builder = new StrPathBuilder(PathFinder.getMusicPath());
        if (scope != null)
            builder.append(StringMaster.getWellFormattedString(
             scope.toString()));
        if (variant != null) {
            if (variant == MUSIC_VARIANT.RANDOM) {
                builder.append(
                 StringMaster.getWellFormattedString(
                  RandomWizard.getRandomListObject(
                   Arrays.asList(MUSIC_VARIANT.values())
                  ).toString()));
            } else
                builder.append(StringMaster.getWellFormattedString(variant.toString()));
        }
        if (theme != null)
            builder.append(StringMaster.getWellFormattedString(
             theme.toString()));
        return builder.toString();
    }

    public void startLoop() {
        if (thread != null) {
            resume();
            checkNewMusicToPlay();
            return;
        }
        thread =
         new Thread(() -> {
             while (true) {
                 try {
                     WaitMaster.WAIT(PERIOD);
                 } catch (Exception e) {
                     main.system.ExceptionMaster.printStackTrace(e);
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
//        MusicMaster.this.running=false;
         }, "Music Thread");
        thread.start();
        running = true;
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
            if (isMusicPreloadOn()) {
                playedMusic = new PreloadedMusic(path);
            } else {
                FileHandle file = Gdx.files.getFileHandle(path, FileType.Absolute);
                playedMusic = Gdx.audio.newMusic(file);
//                Gdx.audio.newAudioDevice()
            }
            musicCache.put(path, playedMusic);
        }
        Float volume =
         getVolume();
        playedMusic.setVolume(volume);
        try {
            playedMusic.play();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private boolean isMusicPreloadOn() {
        return !mainThemePlayed;
    }

    private Float getVolume() {
        return new Float(OptionsMaster.getSoundOptions().
         getIntValue(SOUND_OPTION.MUSIC_VOLUME)) / 100 * SoundMaster.getMasterVolume() / 100;
    }

    public void setScope(MUSIC_SCOPE scope) {
        if (scope != this.scope) {
            this.scope = scope;
            if (interruptOnSet)
                musicReset();
        }
    }

    public void setVariant(MUSIC_VARIANT variant) {
        if (variant != this.variant) {
            this.variant = variant;
            if (interruptOnSet)
                musicReset();
        }
    }

    public void setTheme(MUSIC_THEME theme) {
        this.theme = theme;
        if (interruptOnSet)
            musicReset();
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
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public static boolean isOn() {
        return !CoreEngine.isjUnit();
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
        RPG,
        RANDOM,
    }

    // stopScope()

    public class MusicData {

    }

}
