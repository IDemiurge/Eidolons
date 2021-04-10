package eidolons.game.module.cinematic;

import com.badlogic.gdx.math.Interpolation;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.ScriptLib;
import eidolons.game.core.Eidolons;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import static eidolons.game.module.cinematic.CinematicLib.StdCinematic.*;
import static eidolons.game.module.cinematic.Cinematics.*;

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

    public enum StdCinematic {
        VOID_MAZE_FIRST_MOVE, VOID_MAZE_FAIL, VOID_MAZE_AFTER, VOID_MAZE_WIN,
        UNCONSCIOUS_BEFORE, UNCONSCIOUS_AFTER,

        SUMMONING, // PORTAL?
COMBAT_STARTS, //ZOOM, PAN TO LEADER
COMBAT_ENDS,

        ENTER_MODULE,

        //what are the limits and possibilities here?
        //use of Runnables
        // parameter signatures? sometimes it's just better to have a method!
        EXIT_PORTAL, ENTER_PORTAL

    }

    public static void run(StdCinematic cinematic, Object... args) {
        switch (cinematic) {
            case EXIT_PORTAL:
                GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, args[0]);
                break;
            case ENTER_PORTAL:
                GuiEventManager.trigger(GuiEventType.UNIT_FADE_OUT_AND_BACK, args[0]);
                break;
            case COMBAT_ENDS:
            case COMBAT_STARTS:
                Unit leader = (Unit) args[0];
                break;
            case ENTER_MODULE:
                doBlackout(true, 0.04f);
                WaitMaster.WAIT(500);
                doZoom(1.25f, 0.05f, Interpolation.fade);
                doBlackout(false, 4f);
                doZoom(1f, 3.25f, Interpolation.pow2In);
                break;
            case UNCONSCIOUS_BEFORE:
                MusicMaster.playMoment(MusicEnums.MUSIC_MOMENT.FALL);
                doZoom(1.21f, 3.15f, Interpolation.pow2In);
                doBlackout(true, 1f);
                WaitMaster.WAIT(1500);
                doBlackout(false, 4f);
                break;
            case UNCONSCIOUS_AFTER:
                GuiEventManager.trigger(GuiEventType.UNIT_MOVED, args[0]);
                GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, args[0]);
                doZoom(1f, 3.25f, Interpolation.pow2In);
                break;
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
                doShake( VisualEnums.ScreenShakeTemplate.HARD, 2, null);
                doZoom(0.21f, 2.25f, Interpolation.pow2In);
                // ScreenMaster.getGrid().getGridManager().getAnimHandler().doFall(Eidolons.getMainHero());
                break;
        }
    }
}
