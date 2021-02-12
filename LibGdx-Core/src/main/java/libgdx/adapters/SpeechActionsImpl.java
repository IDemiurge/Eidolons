package libgdx.adapters;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.GdxSpeechActions;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript;
import eidolons.game.core.Eidolons;
import libgdx.anims.SimpleAnim;
import libgdx.anims.fullscreen.Screenshake;
import libgdx.anims.main.AnimMaster;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.cell.BaseView;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.stage.camera.MotionData;
import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.List;
import java.util.function.Predicate;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SCRIPT.LINKED_OBJ;

public class SpeechActionsImpl implements GdxSpeechActions {
    @Override
    public boolean getNoCommentsCondition() {
        return DungeonScreen.getInstance().getGridPanel().getActiveCommentSprites().isEmpty();
    }

    @Override
    public Predicate<Float> getNoAnimsCondition() {
        return
                delta -> {
                    if (!AnimMaster.getInstance().isDrawing()) {
                        LogMaster.devLog("Anims waiting unlocked! ");
                        return true;
                    }
                    LogMaster.devLog("Anims waiting... ");
                    return false;
                };
    }
    @Override
    public void doShake(String value, List<String> vars) {
            float intensity = Float.valueOf(value);
            float dur = Float.valueOf(vars.get(0));
            Screenshake.ScreenShakeTemplate temp = Screenshake.ScreenShakeTemplate.MEDIUM;
            if (intensity < 30) {
                temp = Screenshake.ScreenShakeTemplate.SLIGHT;
            }
            if (intensity > 60) {
                temp = Screenshake.ScreenShakeTemplate.HARD;
            }
            if (intensity > 110) {
                temp = Screenshake.ScreenShakeTemplate.BRUTAL;
            }
            Boolean vert = null;
            if (vars.size() > 1) {

                //

                vert = vars.get(1).equalsIgnoreCase("vert");
            }
            GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(dur, vert, temp));

        }

    @Override
    public void doCamera(String value, List<String> vars, SpeechScript.SCRIPT speechAction) {
            MotionData motionData = getMotionData(value, vars, false);
            if (speechAction == CAMERA_SET) {
                GuiEventManager.trigger(GuiEventType.CAMERA_SET_TO, motionData.dest);
                return;
            }
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO, motionData);
        }

        protected MotionData getMotionData(String value, List<String> vars, boolean zoom) {
            Vector2 v = null;
            if (!zoom) {
                switch (value) {
                    case "me":
                        v = getCenteredPos(Eidolons.getPlayerCoordinates());
                        break;
                    case "orig":
                        v = new Vector2(0, 0);
                        break;
                }
                if (v == null) {
                    v = getCenteredPos(getCoordinate(value));
                }
            }
            float duration = 0f;
            if (vars.size() > 0) {
                try {
                    duration = Float.valueOf(vars.get(0));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            Interpolation interpolation = null;
            if (vars.size() > 1) {
                interpolation = ActionMaster.getInterpolation(vars.get(1));
            }
            if (zoom) {
                //        new CameraMan.MotionData(Float.valueOf(value) / 100, Float.valueOf(vars.getVar(0)) / 1000, Interpolation.swing);
                if (duration >= 500) {
                    duration = duration / 1000;
                }
                return new MotionData(Float.valueOf(value) / 100, duration, interpolation);
            }
            return new MotionData(v, duration, interpolation);
        }

    @Override
    public void doZoom(String value, List<String> vars) {
        MotionData motion =
                getMotionData(value, vars, true);
        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, motion);
    }

    @Override
    public void doSpriteAnim(boolean bool, String value, Runnable onDone, Coordinates c, Coordinates dest, Boolean sequential) {
        SimpleAnim simpleAnim = new SimpleAnim(
                bool ? value : "",
                bool ? "" : value, onDone);
        Vector2 v = GridMaster. getCenteredPos(c);
        simpleAnim.setOrigin(v);
        //                simpleAnim.setBlending(b);
        //                simpleAnim.setFps(f);
        if (dest != null) {
            Vector2 v2 =GridMaster.getCenteredPos(dest);
            simpleAnim.setDest(v2);
        }
        if (sequential == null) {
            simpleAnim.setParallel(true);
        }
        AnimMaster.onCustomAnim(simpleAnim);
    }

    @Override
    public void doGridObj(SpeechScript.SCRIPT speechAction, BattleFieldObject unit, Coordinates c, Boolean under) {
        GridObject x;
        obj = new EnumMaster<BfObjEnums.CUSTOM_OBJECT>().retrieveEnumConst(BfObjEnums.CUSTOM_OBJECT.class, value);
        if (speechAction == LINKED_OBJ) {
            BaseView view = ScreenMaster.getGrid().getViewMap().get(unit);
            x = new LinkedGridObject(view, obj, c);
            x.setUnder(under);
            GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, x);
        } else {
            x = new CinematicGridObject(c, obj);
            x.setUnder(under);
            GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ,
                    x);
        }
    }
}
