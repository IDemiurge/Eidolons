package libgdx.stage.camera.generic;

import eidolons.system.options.ControlOptions;

import static eidolons.system.options.ControlOptions.CONTROL_OPTION;

public class CameraOptions {

    public static CameraOptions options;
    public final boolean CAMERA_ON_ACTIVE;
    public final boolean AUTO_CAMERA_OFF;
    public final boolean CAMERA_ON_HERO;
    public final boolean CENTER_CAMERA_ON_COMMENTS;
    public final boolean CENTER_CAMERA_ON_SPEAKER;
    public final float CENTER_CAMERA_AFTER_TIME;
    public final float CENTER_CAMERA_DISTANCE_MOD;

    public static void update(ControlOptions base) {
        options = new CameraOptions(base);
    }

    private CameraOptions(ControlOptions base) {
        CAMERA_ON_ACTIVE = base.getBooleanValue(CONTROL_OPTION.CAMERA_ON_ACTIVE);
        AUTO_CAMERA_OFF = base.getBooleanValue(CONTROL_OPTION.AUTO_CAMERA_OFF);
        CAMERA_ON_HERO = base.getBooleanValue(CONTROL_OPTION.CAMERA_ON_HERO);
        CENTER_CAMERA_AFTER_TIME = base.getFloatValue(CONTROL_OPTION.CENTER_CAMERA_AFTER_TIME);
        CENTER_CAMERA_DISTANCE_MOD = (float) base.getIntValue(CONTROL_OPTION.CENTER_CAMERA_DISTANCE_MOD) / 100;
        CENTER_CAMERA_ON_COMMENTS = base.getBooleanValue(CONTROL_OPTION.CENTER_CAMERA_ON_COMMENTS);
        CENTER_CAMERA_ON_SPEAKER = base.getBooleanValue(CONTROL_OPTION.CENTER_CAMERA_ON_SPEAKER);
    }
}
