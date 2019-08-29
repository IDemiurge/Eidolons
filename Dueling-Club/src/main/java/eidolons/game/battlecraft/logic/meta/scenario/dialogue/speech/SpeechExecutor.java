package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.sun.org.apache.xpath.internal.operations.Bool;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.CinematicGridObject;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SPEECH_ACTION;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.SimpleAnim;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.audio.SoundPlayer;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor.BLENDING;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.particles.ParticlesSprite.PARTICLES_SPRITE;
import eidolons.libgdx.shaders.post.PostFxUpdater.POST_FX_TEMPLATE;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums.SOUND_CUE;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.Soundscape.SOUNDSCAPE;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.Texts;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;
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
    private boolean paused;


    public SpeechExecutor(MetaGameMaster master, DialogueManager dialogueManager) {
        this.master = master;
        this.dialogueManager = dialogueManager;
    }

    public void execute(String speechAction, String value) {
        execute(new EnumMaster<SPEECH_ACTION>().retrieveEnumConst(SPEECH_ACTION.class, speechAction), value);
    }

    public void execute(SPEECH_ACTION speechAction, String value) {
        try {
            execute(speechAction, value, false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void execute(SPEECH_ACTION speechAction, String value, boolean wait) {
        container = dialogueManager.getContainer();
        handler = container.getHandler();
        value = value.trim().toLowerCase();
        List<String> vars = VariableManager.getVarList(value);
        value = VariableManager.removeVarPart(value);
        boolean ui = false;
        boolean inverse = false;
        boolean random = false;
        Vector2 v = null;
        CUSTOM_OBJECT obj = null;
        Coordinates c = null;
        BattleFieldObject unit = Eidolons.getMainHero();


        if (waitOnEachLine != 0) {
            WAIT(waitOnEachLine);
        }
        switch (speechAction) {
            case PORTAL_OPEN:
            case PORTAL_CLOSE:
                c = getCoordinate(value);
                if (speechAction == PORTAL_OPEN) {
                    GuiEventManager.trigger(GuiEventType.PORTAL_OPEN, c);
                } else {
                    GuiEventManager.trigger(GuiEventType.PORTAL_CLOSE, c);
                }
                break;
            case WAIT_FOR:
                pause();
                Integer max = null;
                if (vars.size() > 0) {
                    max = Integer.valueOf(vars.get(0));
                }
                WaitMaster.waitForInput(WaitMaster.getOperation(value), max);
                resume();
                break;
            case ATTACHED:
            case COLOR:
            case SCREEN:
            case DISPLACE:
                GuiEventManager.trigger(("GRID_" + speechAction.name()), getUnit(value), vars.get(0));
                break;
            case SPRITE:
                String script = null;
                Boolean sequential = null;
                Coordinates dest = null;
                boolean flipX;
                for (String var : vars) {
                    //spriteData
                    if (Texts.getScriptsMap().get(var) != null) {
                        script = var;
                        continue;
                    }
                    sequential = Bools.getBool(var);
                    if (sequential != null) {
                        continue;
                    }
                    if (c == null) {
                        c = getCoordinate(var);
                    } else
                        dest = getCoordinate(var);

                }
                Runnable onDone = null;
                if (script != null) {
                    String finalScript = script;
                    onDone = () -> {
                        if (finalScript != null) {
                            Eidolons.onThisOrNonGdxThread(() ->
                                    execute(SCRIPT, finalScript));
                        }
                    };
                }
                SimpleAnim simpleAnim = new SimpleAnim(value, onDone);
                v = GridMaster.getCenteredPos(c);
                simpleAnim.setOrigin(v);
//                simpleAnim.setBlending(b);
//                simpleAnim.setFps(f);
                if (dest != null) {
                    Vector2 v2 = GridMaster.getCenteredPos(dest);
                    simpleAnim.setDest(v2);
                }
                if (sequential == null) {
                    simpleAnim.setParallel(true);
                }
                AnimMaster.onCustomAnim(simpleAnim);
                break;
            case FULLSCREEN:
                FULLSCREEN_ANIM anim = new EnumMaster<FULLSCREEN_ANIM>().retrieveEnumConst(FULLSCREEN_ANIM.class, value);
                FullscreenAnimDataSource data = new FullscreenAnimDataSource(anim, 1f,
                        FACING_DIRECTION.NORTH, BLENDING.SCREEN);

                //invert?
                //flip?
                for (String var : vars) {
                    if (NumberUtils.isNumber(var, false)) {
                        data.setIntensity(Float.valueOf(var));
                    } else if (var.equalsIgnoreCase("random")) {
                        data.flipX = RandomWizard.random();
                        data.flipY = RandomWizard.random();
                    } else if (var.equalsIgnoreCase("randomx")) {
                        data.flipX = RandomWizard.random();
                    } else if (var.equalsIgnoreCase("randomy")) {
                        data.flipY = RandomWizard.random();
                    } else if (var.equalsIgnoreCase("x")) {
                        data.flipX = true;
                    } else if (var.equalsIgnoreCase("y")) {
                        data.flipY = true;
                    } else
                        data.setBlending(new EnumMaster<BLENDING>().retrieveEnumConst(BLENDING.class, var, true));
                }
                GuiEventManager.trigger(GuiEventType.SHOW_FULLSCREEN_ANIM, data);
                break;


            case POSTFX:
                POST_FX_TEMPLATE template = new EnumMaster<POST_FX_TEMPLATE>().retrieveEnumConst(POST_FX_TEMPLATE.class, value);
                if (template != null) {
                    GuiEventManager.trigger(GuiEventType.POST_PROCESSING, template);
                } else GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
                break;
            case MOVE:
                getUnit(value).setCoordinates((getCoordinate(vars.get(0))));
                break;
            case TURN_AUTO:
                getUnit(value).setFacing(
                        FacingMaster.getFacing(vars.get(0)));
                break;
            case ACTION:
                if (vars.size() > 0) {
                    unit = getUnit(vars.get(0));
                }
                if (unit == null) {
                    unit = Eidolons.getMainHero();
                }

                Object arg = null;
                if (vars.size() > 1) {
                    arg = getUnit(vars.get(1));
                    if (arg == null) {
                        arg = getCoordinate(vars.get(1));
                    }
                }
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.ACTION,
                        unit.getRef(), new Object[]{
                                value, arg
                        });
                break;

            case TURN:
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TURN_TO,
                        unit.getRef(), new Object[]{
                                vars.get(0), value
                        });
                break;
            case SCRIPT:
                String d = Texts.getScriptsMap().get(value);
                if (d != null) {
                    if (!vars.isEmpty()) {
                        Eidolons.onThisOrNonGdxThread(() ->
                                new SpeechScript(d, master).execute());
                    } else
                        new SpeechScript(d, master).execute();
                    return;
                }
                COMBAT_SCRIPT_FUNCTION func = new EnumMaster<COMBAT_SCRIPT_FUNCTION>().
                        retrieveEnumConst(COMBAT_SCRIPT_FUNCTION.class, VariableManager.removeVarPart(value));

                master.getBattleMaster().getScriptManager().execute(func, Eidolons.getMainHero().getRef(),
                        vars.toArray(new String[vars.size()]));
                break;
            case MOMENT:
                MusicMaster.playMoment(value);
                break;
            case STOP_LOOP:
                inverse = true;
            case LOOP_TRACK:
                String path = "";
                float volume = 0.1f;
                switch (value) {
                    default:
                        if (vars.size() > 0) {
                            //TODO
                            volume = Float.valueOf(vars.get(0));
                        }
                    case "cue":
                        SOUND_CUE cue = new EnumMaster<SOUND_CUE>().retrieveEnumConst(SOUND_CUE.class, value);
                        if (cue == null) {
                            path = PathFinder.getSoundCuesPath() +
                                    value.replace("_", " ").toLowerCase()
                                    + ".mp3";
                        } else
                            path = cue.getPath();
                        break;
                    case "ambi":
                    case "atmo":
                    case "music":
                        MusicMaster.MUSIC_TRACK track = MusicMaster.getTrackByName(vars.get(0));
                        path = PathFinder.getMusicPath() + track.getPath();
                        break;
                }
                GuiEventManager.trigger(inverse ?
                        GuiEventType.STOP_LOOPING_TRACK : GuiEventType.ADD_LOOPING_TRACK, path, volume);
                break;
            case PARALLEL_MUSIC:
                MusicMaster.getInstance().playParallel(value);
                break;
            case SOUNDSCAPE:
                SOUNDSCAPE soundscape = new EnumMaster<SOUNDSCAPE>().retrieveEnumConst(SOUNDSCAPE.class, value);
                Float vol = Float.valueOf(vars.get(0));
                GuiEventManager.trigger(GuiEventType.SET_SOUNDSCAPE_VOLUME,
                        soundscape, vol);
                break;
            case MUSIC:
                MusicMaster.getInstance().overrideWithTrack(value);
                break;
            case SOUND_VARIANT:
                random = true;
            case SOUND:
                SoundPlayer.cinematicSoundOverride = true;
                volume = 1f;
                if (vars.size() > 0) {
                    volume = Float.valueOf(vars.get(0));
                }
                try {
                    DC_SoundMaster.playKeySound(value, volume, random);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                } finally {
                    SoundPlayer.cinematicSoundOverride = false;
                }
                break;
            case VAR_INTEGER:
                Cinematics.set(value, Integer.valueOf(vars.get(0)));
                break;
            case VAR_FLOAT:
                Cinematics.set(value, Float.valueOf(vars.get(0)));
                break;
            case VAR:
                Cinematics.set(value, Boolean.valueOf(vars.get(0)));
                break;
            case GLOBAL:
                EidolonsGame.set(value, Boolean.valueOf(vars.get(0)));
                break;
            case OPTION_SOUND:
                OptionsMaster.getSoundOptions().setValue(value, vars.get(0));
                OptionsMaster.applyOptions();
                break;
            case OPTION_GRAPHICS:
                OptionsMaster.getGraphicsOptions().setValue(value, vars.get(0));
                OptionsMaster.applyOptions();
                break;
            case OPTION_GAMEPLAY:
                OptionsMaster.getGameplayOptions().setValue(value, vars.get(0));
                OptionsMaster.applyOptions();
                break;
            case OPTION_ANIM:
                OptionsMaster.getAnimOptions().setValue(value, vars.get(0));
                OptionsMaster.applyOptions();
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
                if (vars.size() > 0) {
                    unit = getUnit(vars.get(0));
                }
                Vector2 offset = null;
                if (vars.size() > 1) {
                    offset = GridMaster.getCenteredPos(getCoordinate(vars.get(1)));
                }
                CombatScriptExecutor.doComment((Unit) unit, value, offset);
                break;
            case PARTICLES:
                float alpha = 0;
                switch (vars.get(0)) {
                    case "in":
                        alpha = 1f;
                        break;
                    case "out":
                        alpha = 0f;
                        break;
                    default:
                        alpha = Float.valueOf(vars.get(0));
                }
                PARTICLES_SPRITE type = new EnumMaster<PARTICLES_SPRITE>().retrieveEnumConst(PARTICLES_SPRITE.class, value);
                GuiEventManager.trigger(GuiEventType.SET_PARTICLES_ALPHA, type, alpha);
                break;
            case AUTOCAMERA:
                handler.setAutoCamera(!value.equalsIgnoreCase("off"));
                break;
            case ZOOM:
//                if (vars.size() < 2) {
//                    vars.add("swing");
//                }
                CameraMan.MotionData motion =
                        getMotionData(value, vars, true);
                GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, motion);
                break;

            case DIALOGUE_AFTER:
                String finalValue = value;
                WaitMaster.doAfterWait(getWaitTime(Integer.valueOf(vars.get(0)), vars),
                        () ->
                                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.DIALOGUE,
                                        new Ref(), finalValue)
//                                execute(SCRIPT, "dialogue=" + finalValue)

                );
                //TODO
                break;

            case CAMERA_OFFSET:
                c = Coordinates.get(value);
                GuiEventManager.trigger(GuiEventType.CAMERA_OFFSET, c);
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
            case GRID_OBJ_ANIM:
            case REMOVE_GRID_OBJ:
                if (!vars.isEmpty()) {
                    c = getCoordinate(vars.get(0));
                }
            case GRID_OBJ:
                //wards and awakening!
                if (speechAction == GRID_OBJ_ANIM) {
                    GuiEventManager.trigger(GuiEventType.GRID_OBJ_ANIM, value, c, new GraphicData(vars.get(1)));
                } else if (speechAction == REMOVE_GRID_OBJ) {
                    GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, value, c);
                } else
                    GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, new CinematicGridObject(c, obj));

                break;
            case COORDINATE:
                c = getCoordinate(vars.get(0));
                for (BattleFieldObject object : Eidolons.getGame().getObjectsOnCoordinate(c)) {
                    doUnit(object, value);
                }

            case UNIT:
                doUnit(value, vars);
                break;
            case SPEED:
                if (Float.valueOf(value) > 5) {
                    handler.setSpeed(Float.valueOf(value) / 100);
                } else
                    handler.setSpeed(Float.valueOf(value));
//TODO
                break;
            case WAIT_OFF:
            case WAIT_EACH:
//                dialogueManager.getd
                if (value.isEmpty() || !NumberUtils.isInteger(value)) {
                    waitOnEachLine = 0;
                } else
                    waitOnEachLine = getWaitTime(Integer.valueOf(value), vars);
                break;
            case TIME:
                container.getCurrent().setTime(new Float(Integer.valueOf(value)));
                break;
            case ABS:
                WAIT((Integer.valueOf(value)));
                break;
            case WAIT:
                WAIT(getWaitTime(Integer.valueOf(value), vars));
                break;

            case NEXT_OFF:
                handler.getDialogue().setTimeBetweenScripts(0);
                handler.getDialogue().setTimeBetweenScriptsLengthMultiplier(0);
                break;
            case NEXT_ALL:
                handler.getDialogue().setTimeBetweenScripts(getNextTime(Integer.valueOf(value), vars));
                if (vars.size() > 0) {
                    handler.getDialogue().setTimeBetweenScriptsLengthMultiplier(getNextTime(Integer.valueOf(vars.get(0)), vars));
                }
                break;
            case NEXT:
//                container.getCurrent().disableTimer();
//                WaitMaster.doAfterWait(getWaitTime(Integer.valueOf(value), vars), () -> container.getCurrent().tryNext());
                container.getCurrent().disableTimer();
                container.getCurrent().setTime(new Float(getNextTime(Integer.valueOf(value), vars)));
                break;
            case DIALOGUE:
                switch (value) {
                    case "pause":
                        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.DIALOGUE_DONE);
                        break;
                    case "resume":
                        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.DIALOGUE_DONE, true);
                        break;
                    case "end":
                    case "done":

                        container.done();
                        break;
                    case "hide":
                        container.fadeOut();
                        //enable camera contronls?
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

    private void resume() {
        container.getCurrent().resume();
    }

    private void pause() {
        container.getCurrent().pause();
    }


    private void doOut(String value, List<String> vars, SPEECH_ACTION speechAction) {
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
                dur = Float.valueOf(value);
                GuiEventManager.trigger(white ? GuiEventType.WHITEOUT_AND_BACK : GuiEventType.BLACKOUT_AND_BACK, dur);
                break;
        }
    }

    private void doAnim(Boolean ui_bg_both, String value, List<String> vars) {
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
            if (ui_bg_both == null) {
                ActionMaster.addAlphaAction(container, dur, alpha);
            } else
                ActionMaster.addAlphaAction(ui_bg_both ? container.getCurrent() : container.getBgSprite(), dur, alpha);
            return;
        }
    }

    private void doUnit(String value, List<String> vars) {
        BattleFieldObject unit = vars.size() == 0 ? Eidolons.getMainHero() : getUnit(vars.get(0));
        doUnit(unit, value);
    }

    private void doUnit(BattleFieldObject unit, String value) {
        //same find-alg!
        if (unit != null) {
            switch (value) {
                case "remove":
                    unit.kill(unit, false, true);

                    break;
                case "show":
                    unit.setHidden(false);

                    break;
                case "hide":
                    //TODO kind of fade out
                    unit.setHidden(true);

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
        unit.getGame().getVisionMaster().refresh();

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

            //

            vert = vars.get(1).equalsIgnoreCase("vert");
        }
        GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(dur, vert, temp));

    }

    private void doCamera(String value, List<String> vars, SPEECH_ACTION speechAction) {
        CameraMan.MotionData motionData = getMotionData(value, vars, false);
        if (speechAction == CAMERA_SET) {
            GuiEventManager.trigger(GuiEventType.CAMERA_SET_TO, motionData.dest);
            return;
        }
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO, motionData);
    }

    private CameraMan.MotionData getMotionData(String value, List<String> vars, boolean zoom) {
        Vector2 v = null;
        if (!zoom) {
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
//        new CameraMan.MotionData(Float.valueOf(value) / 100, Float.valueOf(vars.get(0)) / 1000, Interpolation.swing);
            if (duration >= 500) {
                duration = duration / 1000;
            }
            return new CameraMan.MotionData(Float.valueOf(value) / 100, duration, interpolation);
        }
        return new CameraMan.MotionData(v, duration, interpolation);
    }

    private BattleFieldObject getUnit(String value) {
        value = value.trim();
        switch (value.toLowerCase()) {
            case "me":
            case "self":
            case "source":
                return Eidolons.getMainHero();
        }
        BattleFieldObject unit = master.getGame().getAiManager().getScriptExecutor().findUnit(
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
        value = value.trim();
        if (!value.contains("-")) {
            if (!value.contains(":")) {
                BattleFieldObject unit = getUnit(value);
                if (unit != null) {
                    return unit.getCoordinates();
                }
            }
        }
        Coordinates c = Coordinates.get(value);
        if (c.isInvalid()) {
            return null;
        }
        return c;
    }

    private int getNextTime(Integer millis, List<String> vars) {
        return getTime(millis, vars, 1f);
    }

    private int getWaitTime(Integer millis, List<String> vars) {
        return getTime(millis, vars, 0.5f);
    }

    private int getTime(Integer millis, List<String> vars, float coef) {
        if (CoreEngine.isSuperLite()) {
            millis = millis / 5;
//            return;
        }
        if (CoreEngine.isMyLiteLaunch()) {
            millis = millis * 2 / 3;
        }
        boolean absolute = false;
        if (!absolute) {
            millis = (int) (millis / (Interpolation.linear.apply(1, handler.getSpeed(), coef)));

        }
        return millis;
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
        for (Pair<SPEECH_ACTION, String> pair : speechScript.actions) {
            executeAction(pair.getKey(), pair.getValue());
        }
        important("Script executed");
        running = false;
    }

    private void executeAction(SPEECH_ACTION speechAction, String value) {
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

