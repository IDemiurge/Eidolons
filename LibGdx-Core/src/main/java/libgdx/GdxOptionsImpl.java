package libgdx;

import eidolons.game.core.Eidolons;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.libgdx.GdxOptions;
import eidolons.system.options.AnimationOptions;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.GraphicsOptions;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.std.HitAnim;
import libgdx.assets.AnimMaster3d;
import libgdx.bf.Fluctuating;
import libgdx.bf.decor.shard.ShardVisuals;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.light.ShadowMap;
import libgdx.bf.mouse.BattleClickListener;
import libgdx.bf.mouse.InputController;
import libgdx.gui.panels.ScrollPanel;
import libgdx.gui.panels.dc.logpanel.LogPanel;
import libgdx.particles.ParticleEffectX;
import libgdx.particles.ambi.EmitterMap;
import libgdx.particles.ambi.ParticleManager;
import libgdx.screens.ScreenMaster;
import libgdx.screens.map.layers.LightLayer;
import libgdx.stage.GuiVisualEffects;
import libgdx.stage.camera.CameraOptions;
import main.system.auxiliary.EnumMaster;

public class GdxOptionsImpl implements GdxOptions {
    @Override
    public void setAnimationSpeedFactor(float floatValue) {

    }

    @Override
    public void setFloatingTextLayerDurationMod(float floatValue) {

    }

    @Override
    public void applyAnimOption(AnimationOptions.ANIMATION_OPTION key, float floatValue, Integer intValue, boolean booleanValue) {
        switch (key) {
            case SPEED:
                GdxAdapter.getOptions().setAnimationSpeedFactor(
                        floatValue);
                break;
            case FLOAT_TEXT_DURATION_MOD:
                GdxAdapter.getOptions().setFloatingTextLayerDurationMod(floatValue);
                break;

            case WEAPON_3D_ANIMS_OFF:
                AnimMaster3d.setOff(booleanValue);
                break;
            case BLOOD_ANIMS_OFF:
                HitAnim.setBloodOff(booleanValue);
                break;
            case MAX_ANIM_WAIT_TIME:
            case AFTER_EFFECTS_ANIMATIONS:
            case CAST_ANIMATIONS:

            case PRECAST_ANIMATIONS:
                break;
            case PARALLEL_ANIMATIONS:
                AnimMaster.getInstance().setParallelDrawing(booleanValue);
                break;
            case HIT_ANIM_DISPLACEMENT:
                HitAnim.setDisplacementOn(booleanValue);
                break;
        }

    }

    @Override
    public void applyControlOption(ControlOptions options) {
        CameraOptions.update(options);

        GdxAdapter.getOptions().applyControlOption(options);

        for (Object sub : options.getValues().keySet()) {
            new EnumMaster<ControlOptions.CONTROL_OPTION>().
                    retrieveEnumConst(ControlOptions.CONTROL_OPTION.class,
                            options.getValues().get(sub));
            ControlOptions.CONTROL_OPTION key = options.getKey((sub.toString()));
            if (key == null)
                continue;
            String value = options.getValue(key);
            int intValue = options.getIntValue(key);
            boolean booleanValue = options.getBooleanValue(key);
            switch (key) {
                case SCROLL_SPEED:
                    ScrollPanel.setScrollAmount(intValue);
                    break;
                case ZOOM_STEP:
                    InputController.setZoomStep(Integer.parseInt(value) / 100f);
                    break;
                case UNLIMITED_ZOOM:
                    InputController.setUnlimitedZoom(booleanValue);
                    break;
                case ALT_MODE_ON:
                    BattleClickListener.setAltDefault(booleanValue);
                    break;
            }


        }
    }

    @Override
    public void applyGraphics(GraphicsOptions.GRAPHIC_OPTION key, String value, boolean bool) {
        switch (key) {
            //            case ALT_ASSET_LOAD:
            //                Assets.setON(!bool);
            //                break;
            case AMBIENCE_DENSITY:
                EmitterMap.setGlobalShowChanceCoef(Integer.valueOf(value));
                break;
            case ADDITIVE_LIGHT:
                LightLayer.setAdditive(bool);
                break;
            case PERFORMANCE_BOOST:
                Fluctuating.fluctuatingAlphaPeriodGlobal = (Integer.parseInt(value)) / 10;
                break;
            case GRID_VFX:
                GridPanel.setShowGridEmitters(bool);
                break;
            case UI_VFX:
                GuiVisualEffects.setOff(!bool);
                break;
            case BRIGHTNESS:
                GdxMaster.setBrightness((float) (Integer.parseInt(value) / 100));
                break;
            case AMBIENCE_VFX:
                ParticleManager.setAmbienceOn(bool);
                break;

            case FULLSCREEN:
                if (Eidolons.getScope() == Eidolons.SCOPE.MENU)
                    ScreenMaster.setFullscreen(bool);
                break;

            case VIDEO:
            case BACKGROUND_SPRITES_OFF:
            case SHARD_VFX:
            case FULL_ATLAS:
            case UI_ATLAS:
            case LIGHT_OVERLAYS_OFF:
            case SPRITE_CACHE_ON:
            case VSYNC:

                break;
            case AMBIENCE_MOVE_SUPPORTED:
                ParticleManager.setAmbienceMoveOn(
                        bool);
                break;
            case RESOLUTION:
                ScreenMaster.setResolution(value);
                break;
            case SHARDS_OFF:
                ShardVisuals.setOn(!bool);
                break;
            case SHADOW_MAP_OFF:
                ShadowMap.setOn(!bool);
                break;
            case SPECIAL_EFFECTS:
                ParticleEffectX.setGlobalAlpha(Float.parseFloat(value) / 100);
                break;
            case FONT_SIZE:
                GdxMaster.setUserFontScale(Float.parseFloat(value) / 100);
                break;
            case UI_SCALE:
                GdxMaster.setUserUiScale(Float.parseFloat(value) / 100);
                break;
            case COLOR_TEXT_LOG:
                LogPanel.setColorText(bool);
                break;
        }
    }
    /*
     case WEAPON_3D_ANIMS_OFF:
                    AnimMaster3d.setOff(booleanValue);
                    break;
                case BLOOD_ANIMS_OFF:
                    HitAnim.setBloodOff(booleanValue);
                    break;
                case MAX_ANIM_WAIT_TIME:
                case AFTER_EFFECTS_ANIMATIONS:
                case CAST_ANIMATIONS:

                case PRECAST_ANIMATIONS:
                    break;
                case PARALLEL_ANIMATIONS:
                    AnimMaster.getInstance().setParallelDrawing(Boolean.valueOf(value));
                    break;
                case HIT_ANIM_DISPLACEMENT:
                    HitAnim.setDisplacementOn(booleanValue);
                    break;

                     switch (key) {
                case SCROLL_SPEED:
                    ScrollPanel.setScrollAmount(intValue);
                    break;
                case ZOOM_STEP:
                    InputController.setZoomStep(Integer.parseInt(value) / 100f);
                    break;
                case UNLIMITED_ZOOM:
                    InputController.setUnlimitedZoom(booleanValue);
                    break;
                case ALT_MODE_ON:
                    BattleClickListener.setAltDefault(booleanValue);
                    break;
            }
     */
}
