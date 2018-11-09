package eidolons.system.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
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

import static main.system.auxiliary.log.LogMaster.log;
//a folder tree per music theme!
//standard path structure

public class MusicMaster {
    public static final int PERIOD = 100; //millis
    public static final String MASTER_PATH = PathFinder.getMusicPath() + "/main/";
    public static final boolean MASTER_MODE = true;
    public static final String AMBIENT_FOLDER = "atmo";
    private static final int ALT_AMBIENCE_TOGGLE_CHANCE_BASE = 5;
    static Map<MUSIC_SCOPE, Integer> indexMap; //what was the idea?..
    static Map<MUSIC_SCOPE, List<Integer>> indexListMap;
    private static int ALT_AMBIENCE_TOGGLE_CHANCE = ALT_AMBIENCE_TOGGLE_CHANCE_BASE;
    private static boolean ALT_AMBIENCE = RandomWizard.random();
    private static MusicMaster instance;
    private static Boolean on;
    Stack<String> playList;
    Stack<String> cachedPlayList;
    MUSIC_SCOPE scope = MUSIC_SCOPE.MENU;
    MUSIC_VARIANT variant;
    MUSIC_THEME theme;
    AMBIENCE ambience = AMBIENCE.MIST;
    private boolean shuffle = true;
    private boolean autoplay = true;
    private boolean loopPlaylist = false;
    private Map<String, Music> musicCache = new XLinkedMap<>();

    private Music playedMusic;
    private Music playedAmbient;
    private boolean stopped;
    private boolean running;

    private Map<MUSIC_SCOPE, Music> trackCache = new XLinkedMap<>();


    private Map<MUSIC_SCOPE, Playlist> playlists = new XLinkedMap<>();

    private Thread thread;
    private boolean interruptOnSet;
    private boolean mainThemePlayed;

    private Float musicVolume;
    private Float ambientVolume;
    private float PAUSE;
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

        if (!isOn()) {
            getInstance().pause();
        } else {
            getInstance().resume();
        }

    }

    public static void preload(MUSIC_SCOPE scope) {
        if (isOn())
            if (isMusicPreloadOn()) {
                String path = MASTER_PATH;
                if (scope != null)
                    path = StrPathBuilder.build(path, scope.name());

                for (File sub : FileManager.getFilesFromDirectory(
                 path, false, true)) {
                    if (isMusic(sub.getName())) {
                        getInstance().getMusic(sub.getPath());

                    }
                }
            }
    }

    public static boolean isMusic(String sub) {
        String format = StringMaster.getFormat(sub).toLowerCase();
        // white list is a better idea probably
        // Drive on macos spam 'icon' files all over the place
        return format.contains("mp3") || format.contains("wav");
    }

    public static boolean isMusicPreloadOn() {
        return true;//!mainThemePlayed;
    }

    public static boolean isOn() {
        if (CoreEngine.isjUnit()) return false;
        if (on == null)
            on = !(OptionsMaster.getSoundOptions().
             getBooleanValue(SOUND_OPTION.MUSIC_OFF));
        return on;
    }

    public void resetVolume() {
        resetVolume(getVolume());

    }

    public void resetAmbientVolume() {
        resetAmbientVolume(getAmbientVolume());

    }

    private void resetVolume(Float volume) {
        if (getPlayedMusic() != null) {
            getPlayedMusic().setVolume(volume);
        }
    }

    private void resetAmbientVolume(Float volume) {
        if (getPlayedAmbient() != null) {
            getPlayedAmbient().setVolume(volume);
        }
    }

    public Music getPlayedAmbient() {
        return playedAmbient;
    }

    public void scopeChanged(MUSIC_SCOPE scope) {
        if (this.scope == scope) {
            return;
        }
        setScope(scope);
        if (!isRunning()) {
            startLoop();
        }
        if (!isOn())
            return;
        stopAmbience();
        if (ListMaster.isNotEmpty(playList))
            playList.clear();
        pause();
        if (isStreaming())
            playedMusic = trackCache.get(scope);
        else
            playedMusic = null;
        if (playedMusic == null) {
            checkNewMusicToPlay();
        }
        resume();
        //fade? :)
        //
    }

    private boolean isStreaming() {
        return false;
    }

    public Stack<String> getPlayList() {
        return playList;
    }

    public Music getPlayedMusic() {
        return playedMusic;
    }

    public void init() {
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
        if (ListMaster.isNotEmpty(playList))
            playList.clear();
        checkNewMusicToPlay();
        resume();
    }


    //???
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

        if (!isOn())
            return;
        if (!interruptOnSet)
            if (playedMusic != null)
                if (playedMusic.isPlaying())
                    return;
        if (ListMaster.isNotEmpty(playList)) {
            if (playedMusic != null)
                if (checkMakePause())
                    return;
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
                if (folder.isEmpty())
                    break;
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
        //       TODO what was the idea? checkUpdateTypes();
    }

    private boolean checkMakePause() {
        int chance = 0;
        int length = 0;
        if (scope == MUSIC_SCOPE.BATTLE) {
            chance = 10;
            length = 100 * 1000;
        }
        if (scope == MUSIC_SCOPE.MENU) {
            chance = 100;
            length = 30 * 1000;
        }
        if (scope == MUSIC_SCOPE.MAP) {
            chance = 6;
            length = 15 * 1000;
        }
        if (RandomWizard.chance(chance)) {
            PAUSE = length;
            ALT_AMBIENCE = !ALT_AMBIENCE;
            return true;
        }
        return false;
    }

    public void stopAmbience() {
        if (playedAmbient != null)
            if (playedAmbient.isPlaying())
            {
                playedAmbient.stop();
                playedAmbient = null;
            }
    }

    private void checkAmbience() {
        if (!isAmbientOn()) {
            if (playedAmbient != null)
                if (playedAmbient.isPlaying())
                    playedAmbient.stop();
            return;
        }

        if (playedAmbient == null || !playedAmbient.isPlaying()) {

            boolean global = true; //TODO
            AMBIENCE newAmbience = null;
            try {
                newAmbience = AmbientMaster.getCurrentAmbience(ALT_AMBIENCE, global);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (newAmbience == null) {
                if (playedAmbient != null)
                    playedAmbient.stop();
                return;
            }

            if (ambience != newAmbience) {
                ambience = newAmbience;
                if (playedAmbient != null) {
                    playedAmbient.stop();
//                    playedAmbient = null;
                    return;
                }
            }
            if (playedAmbient == null) {
                playedAmbient = new PreloadedMusic(ambience.getPath());
                log(1, "loaded Ambient: " + ambience.getPath());
                Float volume = getAmbientVolume();
                playedAmbient.setVolume(volume);
            }
            if (playedAmbient.isPlaying()) {
                log(1, "WTF  " + ambience.getPath());
            } else {
                log(1, "playing Ambient: " + ambience.getPath());
                playedAmbient.play();
            }


            if (RandomWizard.chance(ALT_AMBIENCE_TOGGLE_CHANCE)) {
                ALT_AMBIENCE = !ALT_AMBIENCE;
                ALT_AMBIENCE_TOGGLE_CHANCE = ALT_AMBIENCE_TOGGLE_CHANCE_BASE;
            } else ALT_AMBIENCE_TOGGLE_CHANCE++;
        }
    }

    private boolean isAmbientOn() {
        if (scope == MUSIC_SCOPE.MENU) {
            return false;
        }
        if (scope == MUSIC_SCOPE.BATTLE)
        {
            if (PAUSE>0)
                return true;
            return ALT_AMBIENCE;
        }
        return true;
    }

    private String getMusicFolder() {
        if (MASTER_MODE) {
            if (scope == MUSIC_SCOPE.MAP)
                return MASTER_PATH + "map";
            if (Eidolons.game != null && Eidolons.game.isStarted())
                if (scope == MUSIC_SCOPE.BATTLE)
                    return MASTER_PATH + "battle";
            if (scope == MUSIC_SCOPE.MENU) {
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
        ALT_AMBIENCE = RandomWizard.chance(28);

        thread = new Thread(() -> {
            while (true) {
                try {
                    runLoop();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }, "Music Thread");
        thread.start();
        running = true;
    }

    private void runLoop() {
        checkAmbience();
        if (!stopped) {
            if (PAUSE > 0) {
                PAUSE -= PERIOD;
            } else {
                if (playedMusic != null) {
                    if (!playedMusic.isPlaying()) {
                        checkNewMusicToPlay();
                    }
                } else {
                    checkNewMusicToPlay();
                }
            }
        }
        WaitMaster.WAIT(PERIOD);
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
        path = PathUtils.addMissingPathSegments(path, PathFinder.getMusicPath());
        //TODO save!
        if (playedMusic != null)
            playedMusic.stop();

        playedMusic = getMusic(path);
        Float volume =
         getVolume();
        playedMusic.setVolume(volume);
        playedMusic.play();
        trackCache.put(this.scope, playedMusic);
        log(1, "Music playing: " + path);
    }

    private Music getMusic(String path) {
        path = path.toLowerCase();
        Music playedMusic = musicCache.get(path);
        if (playedMusic == null) {
            if (isMusicPreloadOn()) {
                playedMusic = new PreloadedMusic(path);
            } else {
                FileHandle file = Gdx.files.getFileHandle(path, FileType.Absolute);
                playedMusic = Gdx.audio.newMusic(file);
            }
            musicCache.put(path, playedMusic);
            log(1, "Music loaded " + path);
        }
        //what if music was disposed?
        return playedMusic;
    }

    public Float getAmbientVolume() {
        ambientVolume = OptionsMaster.getSoundOptions().
         getFloatValue(SOUND_OPTION.AMBIENCE_VOLUME) / 100;
        return ambientVolume * SoundMaster.getMasterVolume() / 100;
    }

    private Float getVolume() {
        musicVolume = OptionsMaster.getSoundOptions().
         getFloatValue(SOUND_OPTION.MUSIC_VOLUME) / 100;
        return musicVolume * SoundMaster.getMasterVolume() / 100;
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

    public void autoScope() {
        if (DC_Game.game == null)
            scopeChanged(MUSIC_SCOPE.MENU);
        else {
            //            if (MacroGame)
            scopeChanged(ExplorationMaster.isExplorationOn() ? MUSIC_SCOPE.ATMO : MUSIC_SCOPE.BATTLE);
        }
    }


    // SOUNDS VS THEME-FILES?!
    public enum AMBIENCE {
        MINE,
        SHIP,
        INTERIOR,
        MIST,
        HAUNTED,
        CAVE,
        EVIL,

        //        FOREST_NIGHT(),
        //        FOREST_DAY,
        //        TAVERN,
        //        TEMPLE,
        //        CASTLE,
        //        DUNGEON,
        //        MOUNTAINS,
        //        NORTH,
        TOWN();

        AMBIENCE(ATMO_SOUND_TYPE... TYPES) {

        }

        public String getPath() {
            return StrPathBuilder.build(PathFinder.getMusicPath(), AMBIENT_FOLDER, name() + AmbientMaster.FORMAT);
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
            return name();
        }
    }

    public enum MUSIC_SCOPE {
        MENU, ATMO, MAP, BATTLE
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
