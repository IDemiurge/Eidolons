package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.EidolonsGame;

public class Cinematics {
    public static boolean ON;
    public static float VOLUME_MUSIC;
    public static float VOLUME_AMBIENCE;
    public static float ANIM_SPEED=1f;




    /**
     * what does it work with?
     *
     * - events
     * its own fullscreen layer?
     *
     *
     *
     */
    public static final void set(String field, Object val) {
        try {
            Cinematics.class.getField(field.toUpperCase()).set(null, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
    public enum CINEMATIC_FULLSCREEN_PRESET {

        gate_front(false, 15),
        ;

        CINEMATIC_FULLSCREEN_PRESET( boolean screen, int fps) {
            this.path = "sprites/fullscreen/cinematic/" +
                    "" +
                    ".txt";
            this.screen = screen;
            this.fps = fps;
        }

        String path;
        boolean screen;
        int fps;

    }
    public enum CINEMATIC_ACTION_PRESET {

    }

}
