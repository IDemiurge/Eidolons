package eidolons.system.libgdx;

import eidolons.system.options.AnimationOptions;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.GraphicsOptions;

public interface GdxOptions {
    void setAnimationSpeedFactor(float floatValue);
    // AnimMaster.getInstance()
    void setFloatingTextLayerDurationMod(float floatValue);

    void applyGraphics(GraphicsOptions.GRAPHIC_OPTION key, String value, boolean bool);

    void applyAnimOption(AnimationOptions.ANIMATION_OPTION key, float floatValue, Integer intValue, boolean booleanValue);

    void applyControlOption(ControlOptions options);
}
