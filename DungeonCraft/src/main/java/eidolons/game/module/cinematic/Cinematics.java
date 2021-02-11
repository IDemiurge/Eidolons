package eidolons.game.module.cinematic;

import com.badlogic.gdx.math.Interpolation;
import libgdx.anims.fullscreen.Screenshake;
import libgdx.stage.camera.MotionData;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class Cinematics {
    public static boolean ON;
    public static float VOLUME_MUSIC;
    public static float VOLUME_AMBIENCE;
    public static float ANIM_SPEED=1f;

    public static void doShake(Screenshake.ScreenShakeTemplate temp, float dur, Boolean vert) {
        GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(dur, vert, temp));
    }

    public static void whiteout(boolean in, float dur) {
        GuiEventManager.trigger(in ? GuiEventType.WHITEOUT_IN : GuiEventType.WHITEOUT_OUT, dur);
    }

    public static void doBlackout(boolean in, float dur) {
        GuiEventManager.trigger(in ? GuiEventType.BLACKOUT_IN : GuiEventType.BLACKOUT_OUT, dur);
    }

    public static void doZoom(float zoom, float dur) {
        doZoom(zoom, dur, Interpolation.fade);
    }
    public static void doZoom(float zoom, float dur, Interpolation interpolation) {
        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, new MotionData(zoom, dur, interpolation));
    }

    public static void set(String field, Object val) {
        try {
            Cinematics.class.getField(field.toUpperCase()).set(null, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
