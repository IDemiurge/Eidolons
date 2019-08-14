package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.CinematicGridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.LinkedGridObject;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.SimpleAnim;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.SuperActor.BLENDING;
import eidolons.libgdx.shaders.post.PostFxUpdater.POST_FX_TEMPLATE;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SPEECH_ACTION.*;
import static main.system.auxiliary.log.LogMaster.important;

public class SpeechExecutor {

    private final DialogueManager dialogueManager;
    DialogueHandler handler;
    DialogueContainer container;
    MetaGameMaster master;
    private int waitOnEachLine;
    private boolean waiting;
    private boolean running;


    public SpeechExecutor(MetaGameMaster master, DialogueManager dialogueManager) {
        this.master = master;
        this.dialogueManager = dialogueManager;
    }

    public void execute(SpeechScript.SPEECH_ACTION speechAction, String value) {
        try {
            execute(speechAction, value, false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void execute(SpeechScript.SPEECH_ACTION speechAction, String value, boolean wait) {
        container = dialogueManager.getContainer();
        handler = container.getHandler();
        value = value.trim().toLowerCase();
        List<String> vars = VariableManager.getVarList(value);
        value = VariableManager.removeVarPart(value);
        boolean ui = false;
        Vector2 v = null;
        CUSTOM_OBJECT obj = null;
        Coordinates c = null;

        if (waitOnEachLine != 0) {
            WAIT(waitOnEachLine);
        }
        switch (speechAction) {
            case WAIT_OFF:
            case WAIT_EACH:
//                dialogueManager.getd
                if (value.isEmpty() || !NumberUtils.isInteger(value)) {
                    waitOnEachLine = 0;
                } else
                    waitOnEachLine = Integer.valueOf(value);
                break;
            case TIME:
                container.getCurrent().setTime(new Float(Integer.valueOf(value)));
                break;
            case WAIT:
                WAIT(Integer.valueOf(value));
                break;
            case SPRITE:
                SimpleAnim simpleAnim = new SimpleAnim(value, () -> {
                });
                v = GridMaster.getCenteredPos(getCoordinate(vars.get(0)));
                simpleAnim.setOrigin(v);
//                simpleAnim.setBlending(b);
//                simpleAnim.setFps(f);
                AnimMaster.onCustomAnim(simpleAnim);
                break;
            case FULLSCREEN:
                FULLSCREEN_ANIM anim = new EnumMaster<FULLSCREEN_ANIM>().retrieveEnumConst(FULLSCREEN_ANIM.class, value);
                FullscreenAnimDataSource data = new FullscreenAnimDataSource(anim, 1,
                        FACING_DIRECTION.NORTH, BLENDING.SCREEN);
                //invert?
                //flip?
                if (vars.size() > 0) {
                    data.setBlending(new EnumMaster<BLENDING>().retrieveEnumConst(BLENDING.class, vars.get(0), true));
                }
                GuiEventManager.trigger(GuiEventType.SHOW_FULLSCREEN_ANIM, data);
                break;


            case POSTFX:
                POST_FX_TEMPLATE template = new EnumMaster<POST_FX_TEMPLATE>().retrieveEnumConst(POST_FX_TEMPLATE.class, value);
                if (template != null) {
                    GuiEventManager.trigger(GuiEventType.POST_PROCESSING, template);
                } else GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
                break;
            case ACTION:
                vars.add(value);
                Unit unit = Eidolons.getMainHero();
                if (vars.size() > 0) {
                    unit = getUnit(vars.get(0));
                }
                if (unit == null) {

                }
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.ACTION,
                        unit.getRef(), vars.toArray(new String[vars.size()]));
                break;
            case SCRIPT:
                COMBAT_SCRIPT_FUNCTION func = new EnumMaster<COMBAT_SCRIPT_FUNCTION>().
                        retrieveEnumConst(COMBAT_SCRIPT_FUNCTION.class, VariableManager.removeVarPart(value));

                master.getBattleMaster().getScriptManager().execute(func, Eidolons.getMainHero().getRef(),
                        vars.toArray(new String[vars.size()]));
                break;
            case MOMENT:
                MusicMaster.playMoment(value);
                break;
            case MUSIC:
                MusicMaster.getInstance().overrideWithTrack(value);
                break;
            case SOUND:
                DC_SoundMaster.playKeySound(value);
                break;
            case PORTRAIT_ANIM:
                //animate the portait displayed in UI?
                AnimMaster.onCustomAnim(value, true, 1, () -> {
//                    handler.continues();
                });
                break;
            case SHAKE:
                doShake(value, vars);
                break;
            case REVEAL_AREA:
                //set some flag?
                Coordinates center = getCoordinate(value);
                Integer radius = Integer.valueOf(vars.get(0));

                for (int i = center.x - radius; i < center.x + radius; i++) {
                    for (int j = center.y - radius; j < center.y + radius; j++) {
                        //use sightMaster for circle?
                        c = Coordinates.get(i, j);
                        if (c != null) {
                            for (BattleFieldObject object : master.getGame().getObjectsOnCoordinate(c)) {
                                object.setRevealed(true);
                            }
                        }
                    }
                }
                master.getGame().getVisionMaster().refresh();
                break;
            case PORTAL:
                switch (value) {
                    case "loop":
                    case "open":
                    case "close":
//                        PortalMaster
                        break;
                }
            case COMMENT:
                if (vars.size()==0) {
                    vars.add(value); //self
                }
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.COMMENT,
                        Eidolons.getMainHero().getRef(), vars.toArray(new String[vars.size()]));
                break;

            case ZOOM:
                CameraMan.MotionData motion = new CameraMan.MotionData(Float.valueOf(value) / 100, Float.valueOf(vars.get(0)) / 1000, Interpolation.swing);
                GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, motion);
                break;
            case AUTOCAMERA:
                handler.setAutoCamera(!value.equalsIgnoreCase("off"));
                break;
            case CAMERA_SET:
            case CAMERA:
                doCamera(value, vars, speechAction);
                break;

            case WHITEOUT:
            case BLACKOUT:
                doOut(value, vars, speechAction);
                break;
            case ANIM:
            case UI_ANIM:
                ui = true;
            case BG_ANIM:
                doAnim(ui, value, vars);
                break;
            case REMOVE_GRID_OBJ:
                obj = CUSTOM_OBJECT.valueOf(value);
                c = getCoordinate(vars.get(0));
            case GRID_OBJ:
                //wards and awakening!
                if (speechAction == REMOVE_GRID_OBJ) {
                    GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, c, obj);
                } else
                    GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, new CinematicGridObject(c, obj));
                break;

            case UNIT:
                doUnit(value, vars);
                break;
            case NEXT_OFF:
                handler.getDialogue().setTimeBetweenScripts(0);
                handler.getDialogue().setTimeBetweenScriptsLengthMultiplier(0);
                break;
            case NEXT_ALL:
                handler.getDialogue().setTimeBetweenScripts(Integer.valueOf(value));
                if (vars.size() > 0) {
                    handler.getDialogue().setTimeBetweenScriptsLengthMultiplier(Integer.valueOf(vars.get(0)));
                }
                break;
            case NEXT:
                container.getCurrent().disableTimer();
                WaitMaster.doAfterWait(Integer.valueOf(value), () -> container.getCurrent().tryNext());
                break;
            case DIALOG:
                switch (value) {
                    case "end":
                    case "done":
                        container.done();
                        break;
                    case "continue":
                    case "next":
//                    TODO do we need it?    container.getCurrent().tryNext();
                        break;
                }
//                container.setOnDoneCallback();
                break;
        }
    }

    private void doOut(String value, List<String> vars, SpeechScript.SPEECH_ACTION speechAction) {
        boolean white = speechAction == WHITEOUT;
        Float dur = 4f;
        if (vars.size() > 0) {
            dur = Float.valueOf(vars.get(0));
        }
        switch (value) {
            case "out":
                GuiEventManager.trigger(white ? GuiEventType.WHITEOUT_OUT : GuiEventType.BLACKOUT_OUT, dur);
                break;
            case "in":
                GuiEventManager.trigger(white ? GuiEventType.WHITEOUT_IN : GuiEventType.BLACKOUT_IN, dur);
                break;
            default:
                GuiEventManager.trigger(white ? GuiEventType.WHITEOUT_AND_BACK : GuiEventType.BLACKOUT_AND_BACK, dur);
                break;
        }
    }

    private void doAnim(boolean ui, String value, List<String> vars) {
        Float alpha = null;
        switch (value) {
            case "out":
                alpha = 0f;
                break;
            case "in":
                alpha = 1f;
                break;
        }
        Float dur = 2f;
        if (vars.size() > 0) {
            dur = Float.valueOf(vars.get(0));
        }
        if (alpha != null) {
            ActionMaster.addAlphaAction(ui ? container : container.getBgSprite(), dur, alpha);
            return;
        }
    }

    private void doUnit(String value, List<String> vars) {
        //same find-alg!
        Unit unit = getUnit(vars.get(0));

        if (unit != null) {
            switch (value) {
                case "remove":
                    unit.kill(unit, false, true);
                    break;
                case "show":
                    unit.setHidden(false);
                    unit.getGame().getVisionMaster().refresh();
                    break;
                case "hide":
                    //TODO kind of fade out
                    unit.setHidden(true);
                    unit.getGame().getVisionMaster().refresh();
                    break;
                case "fade":
                    unit.kill(unit, false, false);
                    break;
                case "kill":
                    unit.kill(Eidolons.getMainHero(), true, false);
                    break;
                case "die":
                    unit.kill();
                    break;
            }
        }
    }

    private void doShake(String value, List<String> vars) {
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
            vert = vars.get(1).equalsIgnoreCase("vert");
        }
        GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(dur, vert, temp));

    }

    private void doCamera(String value, List<String> vars, SpeechScript.SPEECH_ACTION speechAction) {
        Vector2 v = null;
        switch (value) {
            case "me":
                v = GridMaster.getCenteredPos(Eidolons.getMainHero().getCoordinates());
                break;
            case "orig":
                v = new Vector2(0, 0);
                break;
        }
        if (v == null) {
            v = GridMaster.getCenteredPos(getCoordinate(value));
        }
        //TODO offset considering the UI...?
        if (speechAction == CAMERA_SET) {
            GuiEventManager.trigger(GuiEventType.CAMERA_SET_TO, v);
        } else {
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO, v);
        }
    }

    private Unit getUnit(String value) {
        switch (value.toLowerCase()) {
            case "me":
            case "self":
            case "source":
                return Eidolons.getMainHero();
        }
        Unit unit = master.getGame().getAiManager().getScriptExecutor().findUnit(
                Eidolons.getMainHero().getRef(), value);
        if (unit == null) {
            DialogueActor actor = dialogueManager.getDialogueActorMaster().getActor(value);
            if (actor != null) {
                if (actor.getLinkedUnit() == null) {
                    actor.setupLinkedUnit();
                }
                return actor.getLinkedUnit();
            }
        }
        return unit;
    }

    private Coordinates getCoordinate(String value) {
        if (!value.contains("-")) {
            if (!value.contains(":")) {
                Unit unit = getUnit(value);
                if (unit != null) {
                    return unit.getCoordinates();
                }
            }
        }
        return Coordinates.get(value);
    }

    private void WAIT(int millis) {
//        if (!CoreEngine.isIDE())
//        GdxMaster.setLoadingCursor();
        waiting = true;
        WaitMaster.WAIT(millis);
//        GdxMaster.setDefaultCursor();
    }

    public void execute(String text) {
        SpeechScript script = new SpeechScript(text, master);
        execute(script);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void execute(SpeechScript speechScript) {
        running = true;
        important("Executing script: " + speechScript.toString());
        for (Pair<SpeechScript.SPEECH_ACTION, String> pair : speechScript.actions) {
            executeAction(pair.getKey(), pair.getValue());
        }
        important("Script executed");
        running = false;
    }

    private void executeAction(SpeechScript.SPEECH_ACTION speechAction, String value) {
        important("Executing action: " + speechAction + " = " + value);
        execute(speechAction, value);
    }
}
//                Sprites.getSprite(value);
//
//                container.setBg(value);
//
//                container.getBg();

//                container.addAnimation();
//finish anim if interrupt?
// force wait?
//

