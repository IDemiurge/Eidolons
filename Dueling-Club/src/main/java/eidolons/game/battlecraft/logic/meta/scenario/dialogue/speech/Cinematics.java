package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

public class Cinematics {
    /**
     * what does it work with?
     *
     * - events
     * its own fullscreen layer?
     *
     *
     *
     */

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
