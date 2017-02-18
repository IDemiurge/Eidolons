package main.system.audio;

import main.system.sound.SoundMaster;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
//a folder tree per music theme!
//standard path structure

public class MusicMaster {
    public static final int PAUSE = 1500;
    public static final String MOMENT_PATH = "\\music\\moments\\";
    public static final String ATMO_PATH = "\\music\\atmo\\";
    public static final String MUSIC_PATH = "\\music\\";
    static Map<MUSIC_SCOPE, Integer> indexMap;
    static Map<MUSIC_SCOPE, List<Integer>> indexListMap;
    boolean shuffle = false;

    // BONUS?

    public static void startScope(MUSIC_SCOPE scope) {
        // monitor track?

    }

    public static void playMoment(MUSIC_MOMENT moment) {
        String corePath = moment.getCorePath();
        SoundMaster.playRandomSoundVariant(corePath, false);
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

    public enum MUSIC_TYPE {
        ATMO, MUSIC, BONUS
    }

    public enum ATMO_SCOPE {
        MENU, HC, BATTLE
    }

    public enum MUSIC_SCOPE {
        MENU, HC, FIGHT
    }

    // stopScope()

    public enum MUSIC_MOMENT {
        DEFEAT, VICTORY, AFTER_DEFEAT, AFTER_VICTORY, WAVE_CLEARED, WAVE_SPAWN, // regular/elite/boss?
        WELCOME; // VARIANTS?

        public String getCorePath() {
            return MOMENT_PATH + name();
        }
    }

}
