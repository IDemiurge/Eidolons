package eidolons.system.audio;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
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
import main.system.sound.SoundMaster.SOUNDS;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;

import static eidolons.system.audio.MusicMaster.MUSIC_TRACK.*;
import static main.content.CONTENT_CONSTS.SOUNDSET.*;
import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;
//a folder tree per music theme!
//standard path structure

public class MusicMaster {
    public static final int PERIOD = 100; //millis
    public static final String MASTER_PATH = PathFinder.getMusicPath() + "/main/";
    public static final boolean MASTER_MODE = true;

    public static final String AMBIENT_FOLDER = "atmo";
    private static final int ALT_AMBIENCE_TOGGLE_CHANCE_BASE = 5;
    private static int ALT_AMBIENCE_TOGGLE_CHANCE = ALT_AMBIENCE_TOGGLE_CHANCE_BASE;
    private static boolean ALT_AMBIENCE = RandomWizard.random();

    private static MusicMaster instance;
    private static Boolean on;
    Stack<String> playList;
    Stack<String> cachedPlayList;
    MUSIC_SCOPE scope = MUSIC_SCOPE.MENU;
    MUSIC_THEME theme;
    AMBIENCE ambience = AMBIENCE.MIST;
    private boolean shuffle = true;
    private boolean autoplay = true;
    private boolean loopPlaylist = false;
    private Map<String, Music> musicCache = new XLinkedMap<>();

    Soundscape soundscape = new Soundscape();

    private Music playedMusic;
    private Music mutedMusic;
    private Music playedAmbient;
    private boolean stopped;
    private boolean running;

    private Map<MUSIC_SCOPE, Music> trackCache = new XLinkedMap<>();
    List<PreloadedMusic> looping = new ArrayList<>();

//    static Map<MUSIC_SCOPE, Integer> indexMap; //what was the idea?..
//    static Map<MUSIC_SCOPE, List<Integer>> indexListMap;
//    private Map<MUSIC_SCOPE, Playlist> playlists = new XLinkedMap<>();
//    private boolean interruptOnSet;
//    private boolean mainThemePlayed;

    private Thread thread;
    private Float musicVolume;
    private Float ambientVolume;
    private float PAUSE;
    private boolean shouldLoop;
    private boolean interruptOnSet;
    private int loopPlayed;
    private int maxLoopCount;
    private boolean loopingTrack;
    private int waited;


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

        });
        GuiEventManager.bind(GuiEventType.ADD_LOOPING_TRACK, p -> {
            List list = (List) p.get();
            String path = list.get(0).toString();
            PreloadedMusic preloadedMusic = (PreloadedMusic) getMusic(path, true);
            preloadedMusic.setVolume((Float) list.get(1));
            looping.add(preloadedMusic);
        });
        GuiEventManager.bind(GuiEventType.STOP_LOOPING_TRACK, p -> {
            String path = (String) p.get();
            for (PreloadedMusic preloadedMusic : new ArrayList<>(looping)) {
                if (path==null || StringMaster.compare(preloadedMusic.getPath(), path, false)) {
                    looping.remove(preloadedMusic);
                    break;
                }
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

    private boolean isStreaming() {
        return false;// !CoreEngine.isIDE();
    }

    public static boolean isMusicPreloadOn() {
        return true; //!CoreEngine.isIDE();//!mainThemePlayed;
    }

    public static boolean isOn() {
        if (EidolonsGame.DUEL) {
            return false;
        }
        if (getInstance().scope!=MUSIC_SCOPE.MENU && DialogueManager.isRunning()) {
            return false;
        }
        if (CoreEngine.isjUnit()) return false;
        if (CoreEngine.isLiteLaunch()) return false;
        if (on == null)
            on = !(OptionsMaster.getSoundOptions().
                    getBooleanValue(SOUND_OPTION.MUSIC_OFF));
        return on;
    }

    public static void playMoment(String value) {
        MusicMaster.MUSIC_MOMENT moment = new EnumMaster<MUSIC_MOMENT>().retrieveEnumConst(MUSIC_MOMENT.class, value);
        playMoment(moment);
    }

    public void playParallel(String value) {
        MUSIC_TRACK track = getTrackByName(value);
        playMusic(track.getPath(), true, false);
    }

    public void overrideWithTrack(String value) {
        MUSIC_TRACK track = getTrackByName(value);
        playMusic(track.getPath(), false, true);
    }

    public static MUSIC_TRACK getTrackByName(String value) {
        return
                new EnumMaster<MUSIC_TRACK>().retrieveEnumConst(MUSIC_TRACK.class, value);
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
        {
            stop();
            return;
        }
        stopAmbience();
        if (ListMaster.isNotEmpty(playList))
            playList.clear();
        pause();
        if (isStreaming())
            playedMusic = trackCache.get(scope);
        else
            playedMusic = null;
        if (playedMusic == null) {
            try {
                checkNewMusicToPlay();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
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

    private boolean isTrackLooping(String path) {
        if (path.toLowerCase().contains("loop")) {
            return true;
        }
        return false;
    }

    private boolean checkLoop() {
        switch (scope) {
            case BATTLE:
                if (tracksPlayedInScope >= 0) {
                    shouldLoop = true;
                }
        }

        if (loopingTrack) {
            if (loopPlayed >= maxLoopCount) {
                loopingTrack = false;
                shouldLoop = false;
                return false;
            }
            loopPlayed++;
//            playedMusic.play();
            //TODO versions?
//            return true; TODO this one will control N
        }
        return false;
    }

    MUSIC_TRACK lastPlayed;
    int tracksPlayedInScope;

    private boolean checkTrackFits(String sub) {
        MUSIC_TRACK track = getTrackFromPath(sub);

        if (shouldLoop != isTrackLooping(sub))
            return false;
        if (EidolonsGame.BRIDGE) {
            if (scope == MUSIC_SCOPE.MENU) {
                if (tracksPlayedInScope == 0) {
                    return fitTracks(track, 86, THE_END_OR_THE_BEGINNING);
                }
            }
            if (scope == MUSIC_SCOPE.BATTLE) {
                return fitTracks(track, 100,
                        SUFFOCATION_LOOP);
            } else {
                return fitTracks(track, 100, LOOMING_SHADES, DUNGEONS_OF_DOOM);
            }
        }
        if (scope == MUSIC_SCOPE.BATTLE) {
            //intro vs alt intro vs no intro...

            if (EidolonsGame.BOSS_FIGHT) {
                return fitTracks(track, 86, SUFFOCATION_LOOP, NIGHT_OF_DEMON);
            }
            if (EidolonsGame.TUTORIAL_PATH) {
                return fitTracks(track, 70, SUFFOCATION_LOOP, TOWARDS_THE_UNKNOWN_LOOP);
            }
            if (tracksPlayedInScope == 0) {
                return fitTracks(track, 86, BATTLE_INTRO_LOOP);
            }
            if (tracksPlayedInScope == 1) {
                return fitTracks(track, 86, BATTLE_LOOP);
            }

            Boolean intro = null;
            if (lastPlayed == BATTLE_LOOP) {
                intro = false;
            } else
                intro = true;

            int coef = AggroMaster.getBattleDifficulty();
            if (coef > 50) {

            }
        }
        if (scope == MUSIC_SCOPE.MENU) {
            if (tracksPlayedInScope == 0) {
                return fitTracks(track, 86, THE_END_OR_THE_BEGINNING);
            }
        }
        if (scope == MUSIC_SCOPE.MAP) {
//tavern false !
        }
        if (scope == MUSIC_SCOPE.ATMO) {
            if (tracksPlayedInScope > 1) {

            }
        }
        return true;
    }

    private boolean fitTracks(MUSIC_TRACK track, int c, MUSIC_TRACK... tracks) {
        for (MUSIC_TRACK music_track : tracks) {
            if (music_track == track) {
                return true;
            }
        }
        if (RandomWizard.chance(c)) {
            return false;
        }

        return true;
    }

    private MUSIC_TRACK getTrackFromPath(String sub) {
        for (MUSIC_TRACK track : MUSIC_TRACK.values()) {
            if (sub.contains(track.getName())) {
                return track;
            }
        }
        return null;
    }

    private void checkNewMusicToPlay() {

        if (!isOn())
            return;
        if (!interruptOnSet)
            if (playedMusic != null)
                if (playedMusic.isPlaying())
                    return;
        if (checkLoop()) {
            return;
        }
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
                folder = PathUtils.cropLastPathSegment(folder);
                if (folder.isEmpty())
                    break;
                continue;
            }

            playList = new Stack<>();
            ArrayList<String> fitting = new ArrayList<>();
            for (String sub : FileManager.getFilePaths(files)) {
                if (isMusic(sub)) {
                    if (checkTrackFits(sub))
                        fitting.add(sub);
                }
            } //bind to scope?
            if (fitting.isEmpty()) {
                fitting.add(getDefaultTrack(shouldLoop));
            }
            if (shouldLoop) {
                String track = RandomWizard.getRandomListObject(fitting).toString();
                playList.add(track);
                playList.add(track);
                shouldLoop = false;
            } else if (shuffle) {
                playList.addAll(fitting);
            }
            Collections.shuffle(playList);
            cachedPlayList = new Stack<>();
            cachedPlayList.addAll(playList);
            break;
        }
        //       TODO what was the idea? checkUpdateTypes();
    }

    //    private String getDefaultTrackPath(boolean shouldLoop) {
//    }
    private String getDefaultTrack(boolean shouldLoop) {
        switch (scope) {
            case MENU:
                if (shouldLoop) {
                    return THERE_WILL_BE_PAIN_LOOP.getName();
                }
                return FRACTURES.getName();
            case ATMO:
                return LOOMING_SHADES.getName();
            case MAP:
                return ENTHRALLING_WOODS.getName();
            case BATTLE:
                if (shouldLoop) {
                    return SUFFOCATION_LOOP.getName();
                }
                return NIGHT_OF_DEMON.getName();
        }
        return SUFFOCATION_LOOP.getName();
    }


    private boolean checkMakePause() {
        int chance = 0;
        int length = 0;
        if (loopingTrack)
            return false;
        if (scope == MUSIC_SCOPE.BATTLE) {
            chance = 10;
            length =  10000;
        }
        if (scope == MUSIC_SCOPE.MENU) {
            chance = 100;
            length =   10000;
        }
        if (scope == MUSIC_SCOPE.MAP) {
            chance = 6;
            length = 15000;
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
            if (playedAmbient.isPlaying()) {
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
                playedAmbient =getMusic(ambience.getPath(), true);
                log(1, "loaded Ambient: " + ambience.getPath());
                Float volume = getAmbientVolume();
                playedAmbient.setVolume(volume);
            }
            if (playedAmbient.isPlaying()) {
                log(1, "WTF  " + ambience.getPath());
            } else {
//                log(1, "playing Ambient: " + ambience.getPath());
                playedAmbient.play();
            }


            if (RandomWizard.chance(ALT_AMBIENCE_TOGGLE_CHANCE)) {
                ALT_AMBIENCE = !ALT_AMBIENCE;
                ALT_AMBIENCE_TOGGLE_CHANCE = ALT_AMBIENCE_TOGGLE_CHANCE_BASE;
            } else ALT_AMBIENCE_TOGGLE_CHANCE++;
        }
    }

    private boolean isAmbientOn() {
        if (CoreEngine.isActiveTestMode()) {
            return false;
        }
        if (scope == MUSIC_SCOPE.MENU) {
            return false;
        }
        if (scope == MUSIC_SCOPE.BATTLE) {
            if (PAUSE > 0)
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
//                mainThemePlayed = true;
                return MASTER_PATH + "menu";
            }
            return MASTER_PATH;
        }
        StrPathBuilder builder = new StrPathBuilder(PathFinder.getMusicPath());
        if (scope != null)
            builder.append(StringMaster.getWellFormattedString(
                    scope.toString()));
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
        checkLoopedTracks(); //breathing, heartbeat, ..
        soundscape.act(new Float(PERIOD));
        WaitMaster.WAIT(PERIOD);
//        soundPlayback(PERIOD);

    }

    private void checkLoopedTracks() {
        for (PreloadedMusic preloadedMusic : looping) {
            if (!preloadedMusic.isPlaying()) {
                preloadedMusic.play();
            }
            if (preloadedMusic.getVolume()>0) {
             preloadedMusic.setVolume(preloadedMusic.getVolume());
            }
        }
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

    public void restoreMuted() {
        if (mutedMusic == null) {
            return;
        }
        mutedMusic.setVolume(1);
        mutedMusic = null;
    }

    public void playMusic(String path) {
        playMusic(path, false, false);
    }

    public void playMusic(String path, boolean parallel, boolean muteCurrent) {
        path = PathUtils.addMissingPathSegments(path, PathFinder.getMusicPath());
        //TODO save!
        Music music = getMusic(path);
        Float volume =
                getVolume();
        music.setVolume(volume);
        music.play();
        trackCache.put(this.scope, music);
        if (!parallel) {
            if (playedMusic != null) {
                if (muteCurrent) {
                    playedMusic.setVolume(0);
                    mutedMusic = playedMusic;
                } else {
                    playedMusic.stop();
                }
            }
        } else {
            //add to list? otherwise, how to mute it?
        }
        playedMusic = music;
        log(1, "Music playing: " + path);
    }

    public Music getMusic(String path) {
        return getMusic(path, isMusicPreloadOn());
    }

    public Music getMusic(String path, boolean preloaded) {
        path = path.toLowerCase();
        if (!path.contains(".mp3")) {
            path +=".mp3";
        }
        Music playedMusic = musicCache.get(path);
        if (playedMusic == null) {
            if (preloaded) {
                playedMusic = new PreloadedMusic(path);
            } else {
                FileHandle file = Gdx.files.getFileHandle(path, FileType.Absolute);
                playedMusic = Gdx.audio.newMusic(file);
            }
            musicCache.put(path, playedMusic);
            important("Music loaded " + path);
        }
        //what if music was disposed?
        return playedMusic;
    }

    public Float getAmbientVolume() {
        ambientVolume = OptionsMaster.getSoundOptions().
                getFloatValue(SOUND_OPTION.AMBIENCE_VOLUME) / 100;
        if (Cinematics.ON) {
            return Cinematics.VOLUME_AMBIENCE;
        }
        return ambientVolume * SoundMaster.getMasterVolume() / 100;
    }

    private Float getVolume() {
        if (Cinematics.ON) {
            return Cinematics.VOLUME_MUSIC;
        }
        musicVolume = OptionsMaster.getSoundOptions().
                getFloatValue(SOUND_OPTION.MUSIC_VOLUME) / 100;
        return musicVolume * SoundMaster.getMasterVolume() / 100;
    }

    public void setScope(MUSIC_SCOPE scope) {
        if (scope != this.scope) {
            this.scope = scope;
            main.system.auxiliary.log.LogMaster.dev("Music scope: " +scope);
            switch (scope) {
                case BATTLE:
                    tracksPlayedInScope=5;
                    break;
            }
//            if (interruptOnSet)
//                musicReset();
        }
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

        AMBIENCE(AmbientMaster.ATMO_SOUND_TYPE... TYPES) {

        }

        public String getPath() {
            return StrPathBuilder.build(PathFinder.getMusicPath(), AMBIENT_FOLDER, name() + AmbientMaster.FORMAT);
        }
    }


    public enum MUSIC_MOMENT {
        SAD,
        SELENE,
        HARVEST,
        SECRET,
        FALL,
        VICTORY,
        RISE,
        DEATH,
        TOWN,
        GAMEOVER,


        DANGER,
        WELCOME; // VARIANTS?

        public String getCorePath() {
            return PathFinder.getSoundPath() + "moments/" + name() + ".mp3";
        }
    }

    public enum MUSIC_SCOPE {
        MENU, ATMO, MAP, CINEMATIC, BATTLE
    }

    public enum MUSIC_THEME {
        DARK,
        BOSS,
        GOODLY,
    }

    public enum MUSIC_TRACK {
        ATMO(MUSIC_SCOPE.CINEMATIC),
        ATMO_FIRE(MUSIC_SCOPE.CINEMATIC),
        FRACTURES(MUSIC_SCOPE.MENU),
        THERE_WILL_BE_PAIN_LOOP(MUSIC_SCOPE.MENU),
        THE_END_OR_THE_BEGINNING(MUSIC_SCOPE.MENU),
        FAR_BEYOND_FALLEN(MUSIC_SCOPE.MENU),

        OBSCURE_PATHS_LOOP,
        FALLEN_REALMS,
        FROM_DUSK_TILL_DAWN,
        DARK_SECRETS,
        DUNGEONS_OF_DOOM,
        LOOMING_SHADES,

        ENTHRALLING_WOODS(MUSIC_SCOPE.MAP),

        TOWARDS_THE_UNKNOWN_LOOP(MUSIC_SCOPE.BATTLE),
        PREPARE_FOR_WAR(MUSIC_SCOPE.BATTLE),
        NIGHT_OF_DEMON(MUSIC_SCOPE.BATTLE),
        SUFFOCATION_LOOP(MUSIC_SCOPE.BATTLE),
        BATTLE_INTRO_LOOP(MUSIC_SCOPE.BATTLE),
        BATTLE_ALT(MUSIC_SCOPE.BATTLE),
        BATTLE_LOOP(MUSIC_SCOPE.BATTLE),
        ;
        MUSIC_SCOPE scope;

        MUSIC_TRACK() {
        }

        MUSIC_TRACK(MUSIC_SCOPE scope) {
            this.scope = scope;
        }

        public String getPath() {
            if (scope == MUSIC_SCOPE.ATMO) {
                return "main/" + getName() + ".mp3";
            }
            return "main/" + scope.toString().toLowerCase() + "/" + getName() + ".mp3";
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }

}
