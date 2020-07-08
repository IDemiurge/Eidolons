package eidolons.game.module.cinematic;

import com.badlogic.gdx.math.Interpolation;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.ScriptLib;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.screens.ScreenMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import static eidolons.game.module.cinematic.CinematicLib.StdCinematic.*;
import static eidolons.game.module.cinematic.Cinematics.doShake;
import static eidolons.game.module.cinematic.Cinematics.doZoom;

public class CinematicLib {

    public static final boolean TEST_ON = true;

    public static void doTest(int i) {
        StdCinematic func = getTestFunc(i);
        Eidolons.onNonGdxThread(() -> run(func));
    }

    private static StdCinematic getTestFunc(int i) {
        switch (i) {
            case 1:
                return VOID_MAZE_FIRST_MOVE;
            case 2:
                return VOID_MAZE_FAIL;
            case 3:
                return VOID_MAZE_AFTER;
            case 4:
                return VOID_MAZE_WIN;
        }
        return null;
    }

    public enum StdCinematic{
        VOID_MAZE_FIRST_MOVE, VOID_MAZE_FAIL, VOID_MAZE_AFTER, VOID_MAZE_WIN,

    }
    public static void run(StdCinematic cinematic) {
        switch (cinematic) {
            case VOID_MAZE_AFTER:

                GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero());
                WaitMaster.WAIT(1500);
                doZoom(1.25f, 3.5f, Interpolation.fade);
                WaitMaster.WAIT(2500);
                ScriptLib.execute(ScriptLib.STD_SCRIPT.mini_explosion);
                break;
            case VOID_MAZE_WIN:
                ScriptLib.execute(ScriptLib.STD_SCRIPT.gate_flash);
                break;
            case VOID_MAZE_FIRST_MOVE:
                doZoom(1f, 2.25f, Interpolation.pow2In);
                break;
            case VOID_MAZE_FAIL:
                doShake(Screenshake.ScreenShakeTemplate.HARD, 2, null);
                doZoom(0.1f, 2.25f, Interpolation.pow2In);
                ScreenMaster.getGrid().getGridManager().getAnimHandler().doFall(Eidolons.getMainHero());
                break;
        }
    }
}
