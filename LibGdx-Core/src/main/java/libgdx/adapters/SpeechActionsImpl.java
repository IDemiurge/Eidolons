package libgdx.adapters;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Awakener;
import eidolons.puzzle.gridobj.CinematicGridObject;
import eidolons.puzzle.gridobj.GridObject;
import eidolons.puzzle.gridobj.LinkedGridObject;
import eidolons.puzzle.voidy.VoidHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.GdxSpeechActions;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeons.struct.LevelStruct;
import libgdx.anims.SimpleAnim;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.fullscreen.Screenshake;
import libgdx.anims.main.AnimMaster;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.DC_GridPanel;
import libgdx.bf.grid.cell.BaseView;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.stage.camera.MotionData;
import main.content.enums.EncounterEnums;
import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.LogMaster;

import java.util.List;
import java.util.function.Predicate;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SCRIPT.CAMERA_SET;
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
            VisualEnums.ScreenShakeTemplate temp = VisualEnums.ScreenShakeTemplate.MEDIUM;
            if (intensity < 30) {
                temp = VisualEnums.ScreenShakeTemplate.SLIGHT;
            }
            if (intensity > 60) {
                temp = VisualEnums.ScreenShakeTemplate.HARD;
            }
            if (intensity > 110) {
                temp = VisualEnums.ScreenShakeTemplate.BRUTAL;
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
                        v =GridMaster.getCenteredPos(Core.getPlayerCoordinates());
                        break;
                    case "orig":
                        v = new Vector2(0, 0);
                        break;
                }
                if (v == null) {
                    v = GridMaster.getCenteredPos(
                            DC_Game.game.getMetaMaster().getDialogueManager().
                                    getSpeechExecutor().getCoordinate(value));
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
                interpolation = ActionMasterGdx.getInterpolation(vars.get(1));
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
    public void doGridObj(SpeechScript.SCRIPT speechAction, BattleFieldObject unit, Coordinates c, Boolean under, String value, BfObjEnums.CUSTOM_OBJECT obj) {
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

    @Override
    public void execute(SpeechScript.SCRIPT speechAction, String value, List<String> vars) {
        VoidHandler voidHandler = ((DC_GridPanel) ScreenMaster.getGrid()).getVoidHandler();
            boolean bool = false;
            switch (speechAction) {
                case RAISE:
                    bool = true;
                case COLLAPSE:
                    List<Coordinates> list = CoordinatesMaster.getCoordinatesFromString(value);
                    Coordinates origin = DC_Game.game.getMetaMaster().getDialogueManager().getSpeechExecutor().getCoordinate(vars.get(0));
                    Float speed=1f;
                    if (vars.size()>1) {
                        speed = NumberUtils.getFloat(vars.get(1));
                    }
                    voidHandler.toggle(bool, origin , list, speed);
                    break;

                case AUTO_RAISE_ON:
                    voidHandler.toggleAuto();
                    break;
                case AWAKEN:
                    LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().
                            findBlockByName(vars.get(0));
                    EncounterEnums.UNIT_GROUP_TYPE
                            ai = new EnumMaster<EncounterEnums.UNIT_GROUP_TYPE>().
                            retrieveEnumConst(EncounterEnums.UNIT_GROUP_TYPE.class, (vars.get(1)));

                    Awakener.awaken_type type = new EnumMaster<Awakener.awaken_type>().retrieveEnumConst(
                            Awakener.awaken_type.class, (value));

                    DC_Game.game.getDungeonMaster().getAwakener().awaken(struct, ai, type);
                    break;
            }
        }
}
