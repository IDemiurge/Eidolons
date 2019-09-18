package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.CinematicGridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.LinkedGridObject;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SPEECH_ACTION;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.herocreator.logic.spells.SpellMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.SimpleAnim;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.anims.fullscreen.Screenshake;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.audio.SoundPlayer;
import main.content.enums.GenericEnums.BLENDING;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.datasource.SpriteData;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.particles.ParticlesSprite.PARTICLES_SPRITE;
import eidolons.libgdx.particles.spell.SpellVfxMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.post.PostFxUpdater.POST_FX_TEMPLATE;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.system.audio.DC_SoundMaster;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.SOUND_CUE;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.Soundscape.SOUNDSCAPE;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.Texts;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.threading.TimerTaskMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SPEECH_ACTION.*;
import static eidolons.libgdx.bf.GridMaster.*;
import static main.system.auxiliary.log.LogMaster.important;

public class SpeechExecutor {

    private static final boolean ABORT_ON_EXCEPTION = false;
    private final DialogueManager dialogueManager;
    DialogueHandler handler;
    DialogueContainer container;
    MetaGameMaster master;
    private int waitOnEachLine;
    private boolean waiting;
    private boolean running;
    private boolean paused;
    private AbstractCoordinates offset;
    private boolean skipRun;
    private boolean finalScript;
    private SpeechScript lastScript;
    private SpeechScript lt;


    public SpeechExecutor(MetaGameMaster master, DialogueManager dialogueManager) {
        this.master = master;
        this.dialogueManager = dialogueManager;
    }

    public static void run(String s) {
        try {
            Eidolons.getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(s);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void execute(String speechAction, String value) {
        execute(new EnumMaster<SPEECH_ACTION>().retrieveEnumConst(SPEECH_ACTION.class, speechAction), value);
    }

    public boolean execute(SPEECH_ACTION speechAction, String value) {
        try {
            return execute(speechAction, value, true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return !ABORT_ON_EXCEPTION;
    }

    public boolean execute(SPEECH_ACTION speechAction, String value, boolean wait) {
        container = dialogueManager.getContainer();
        handler = container.getHandler();
        value = value.trim().toLowerCase();
        String full = value;
        List<String> vars = VariableManager.getVarList(value);
        value = VariableManager.removeVarPart(value);
        boolean ui = false;
        boolean bool = false;
        boolean random = false;
        Vector2 v = null;
        CUSTOM_OBJECT obj = null;
        Coordinates c = null;
        int max = 0;
        Coordinates c1 = null;
        String string = null;
        BattleFieldObject unit = Eidolons.getMainHero();
        List<Coordinates> coordinatesList = null;

        if (wait && !skipRun) {
            if (!Cinematics.ON) {
//            TODO fix it!
//             if (Eidolons.getGame().isPaused())
//                    WaitMaster.waitForCondition(delta -> !Eidolons.getGame().isPaused(), 0);
            }
            if (waitOnEachLine != 0) {
                WAIT(waitOnEachLine);
            }
        }
        if (skipRun)
            if (checkSkip(speechAction)) {
                return true;
            }
        switch (speechAction) {
            case RESET:
                master.getGame().getManager().reset();
                break;
            case END:
                finalScript = true;
                break;
            case HIGHLIGHT_ACTION:
                DC_ActiveObj a = Eidolons.getMainHero().getActionOrSpell(value);
                if (vars.size() > 0) {
                    GuiEventManager.trigger(GuiEventType.HIGHLIGHT_ACTION_OFF, a);
                } else
                    GuiEventManager.trigger(GuiEventType.HIGHLIGHT_ACTION, a);
            case NO_SKIP:
                setSkipRun(false);
                break;
            case SKIP:
                setSkipRun(true);
                break;
            case CHECK_SKIP:
                checkSkipRun(value);
                break;
            case DEV:
                /*

                 */
                CoreEngine.setFlag(value, Boolean.valueOf(vars.get(0)));
                break;
            case CHEAT:

                break;
            case DEBUG:
                master.getGame().setDebugMode(Boolean.valueOf(value));

                break;
            case PORTAL_OPEN:
            case PORTAL_CLOSE:
                c = getCoordinate(value);
                if (speechAction == PORTAL_OPEN) {
                    GuiEventManager.trigger(GuiEventType.PORTAL_OPEN, c);
                } else {
                    GuiEventManager.trigger(GuiEventType.PORTAL_CLOSE, c);
                }
                break;
            case BLOCK_ACTION:
                unit = getUnit(value);
                if (unit instanceof Unit) {
                    DC_ActiveObj active = ((Unit) unit).getActionOrSpell(vars.get(0));
                    active.setDisabled(true);
                }
                break;
            case ADD_SPELL:
            case REMOVE_SPELL:
                unit = getUnit(value);
                ObjType t = DataManager.getType(vars.get(0), DC_TYPE.SPELLS);
                if (t == null) {
                    SpellMaster.removeSpells((Unit) unit);
                } else if (speechAction == ADD_SPELL) {
                    SpellMaster.addVerbatimSpell((Unit) unit, t);
                } else {
                    SpellMaster.removeSpell((Unit) unit, t);

                }

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
                if (isVisionRefreshRequired())
                    master.getGame().getVisionMaster().refresh();
                break;
            case ORDER:
                unit = getUnit(value);
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.ORDER,
                        unit.getRef(), new Object[]{
                                unit.getName(), vars.get(0)
                        });
                break;
            case MOVE:
                unit = getUnit(value);
                main.system.auxiliary.log.LogMaster.dev("MOVING: " + unit.getNameAndCoordinate());
                unit.setCoordinates((getCoordinate(vars.get(0))));
                main.system.auxiliary.log.LogMaster.dev("MOVED: " + unit.getNameAndCoordinate());

                GuiEventManager.trigger(GuiEventType.UNIT_MOVED, unit);
                if (isVisionRefreshRequired()) {
                    GuiEventManager.trigger(GuiEventType.UNIT_FADE_OUT_AND_BACK, unit);
                    master.getGame().getVisionMaster().refresh();
                }
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
                                unit, value, arg
                        });
                break;
            case LAST_TUTORIAL:
                if (!value.isEmpty()) {
                    execute(lt);
                } else
                    lt = lastScript;
                break;
            case END_ROUND:
                master.getGame().getManager().endRound();
                break;
            case ADD_PARAM:
                getUnit(value).addParam(vars.get(0), Integer.valueOf(vars.get(1)));
                reset();
                break;
            case PARAM:
                getUnit(value).setParam(vars.get(0), Integer.valueOf(vars.get(1)));
                reset();
                break;
            case PROP:
                getUnit(value).setProperty(vars.get(0), (vars.get(1)));
                reset();
                break;

            case REPLACE:
                string = "replace";
            case CLEAR:
                if (speechAction == CLEAR) {
                    string = "remove";
                }
//            case DECIMATE:
            case ALL:
//                if (vars.size() > 0) {
//                    c1 = getCoordinate(vars.get(1), true);
//                    coordinatesList = CoordinatesMaster.getCoordinatesBetween(c, c1);
//                }
                if (string.isEmpty())
                    if (vars.size() > 0) {
                        string = vars.get(0);
                    }
                for (BattleFieldObject bfObject : master.getGame().getBfObjects()) {
                    if (bfObject.getName().equalsIgnoreCase(value)) {
                        if (coordinatesList == null) {
                            doUnit(bfObject, string, vars);
                        } else if (coordinatesList.contains(bfObject.getCoordinates())) {
                            doUnit(bfObject, string, vars);
                        }
                    }

                }

                break;
            case BUFF_REMOVE:
                getUnit(value).removeBuff(vars.get(0));
                break;
            case BUFF_ADD:
                unit = getUnit(value);
                master.getGame().getManager().getBuffMaster().createCustomBuff(vars.get(0), unit);
                break;
            case FILL:
                c1 = getCoordinate(vars.get(1), true);
                coordinatesList = CoordinatesMaster.getCoordinatesBetween(c, c1);

            case ADD:
            case BF_OBJ:
                c = getCoordinate(vars.get(0));
            case NAMED_COORDINATES_ADD:
                if (speechAction == NAMED_COORDINATES_ADD) {
                    coordinatesList = new ArrayList<>();
                    for (String s : master.getGame().getDungeon().getCustomDataMap().keySet()) {
                        if (master.getGame().getDungeon().getCustomDataMap().get(s).equalsIgnoreCase(value)) {
                            coordinatesList.add(Coordinates.get(s));
                        }
                    }
                }
                ObjType bfType = DataManager.getType(value,
                        C_OBJ_TYPE.BF_OBJ);
                if (coordinatesList != null) {
                    int chance = 100;
                    if (vars.size() > 2) {
                        chance = Integer.valueOf(vars.get(2));
                    }
                    for (Coordinates coordinates : coordinatesList) {
                        if (!RandomWizard.chance(chance)) {
                            continue;
                        }
                        master.getGame().createUnit(bfType, coordinates,
                                speechAction == ADD ? master.getGame().getPlayer(false)
                                        : DC_Player.NEUTRAL);
                    }
                    break;
                }

                master.getGame().createUnit(bfType, c,
                        speechAction == ADD ? master.getGame().getPlayer(false)
                                : DC_Player.NEUTRAL);
                break;

            case TIP:
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TIP, new Ref(), value);
                //TODO after?
                break;

            case TURN:
                unit = getUnit(vars.get(0));
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TURN_TO,
                        unit.getRef(), new Object[]{
                                unit.getName(), value
                        });
                //TODO doing it twice because bug..
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TURN_TO,
                        unit.getRef(), new Object[]{
                                unit.getName(), value
                        });
                break;

            case TRIGGER_REMOVE:
                if (value.equalsIgnoreCase("last")) {
                    master.getBattleMaster().getScriptManager().removeLast();
                } else {
//                    TODO master.getBattleMaster().getScriptManager().remove(vars.getVar(0));
                }
                break;
            case TRIGGER:
                master.getBattleMaster().getScriptManager().parseScripts(full);
                break;
            case WAIT_FOR_NO_COMMENTS:
                max = Integer.valueOf(value);
                WaitMaster.waitForCondition(delta ->
                        DungeonScreen.getInstance().getGridPanel().getActiveCommentSprites().isEmpty(), max);
                break;
            case WAIT_ANIMS:
                Predicate<Float> p = delta -> {
                    if (!AnimMaster.getInstance().isDrawing()) {
                        main.system.auxiliary.log.LogMaster.dev("Anims waiting unlocked! ");
                        return true;
                    }
                    main.system.auxiliary.log.LogMaster.dev("Anims waiting... ");
                    return false;
                };
                main.system.auxiliary.log.LogMaster.dev("Anims wait locked! ");
                if (NumberUtils.isInteger(value)) {
                    WaitMaster.waitForCondition(p, Integer.valueOf(value));
                } else
                    WaitMaster.waitForCondition(p, 0);

//                if (NumberUtils.isInteger(value)) {
//                    execute(WAIT_FOR, WaitMaster.WAIT_OPERATIONS.ANIMATION_FINISHED.toString()
//                            + StringMaster.wrapInParenthesis(value));
//                } else
//                    execute(WAIT_FOR, WaitMaster.WAIT_OPERATIONS.ANIMATION_FINISHED.toString());
                break;
            case WAIT_PASS:
                bool = true;
                if (!vars.isEmpty()) {
                    max = Integer.valueOf(vars.get(0));
                }
            case WAIT_INPUT:
//                DungeonScreen.getInstance().getController().onInput(() -> {
//                    WaitMaster.WAIT_OPERATIONS operation = WaitMaster.WAIT_OPERATIONS.INPUT;
//                    WaitMaster.receiveInput(operation, true, true);
//                });
//                //set max from value ?
//                value = "INPUT";
                if (NumberUtils.isInteger(value)) {
                    vars.add(value);
                }
                if (vars.size() > 0) {
                    execute(WAIT, vars.get(0));
                }
                Lock lock = new ReentrantLock();
                Condition waiting = lock.newCondition();
                GdxMaster.onInput(() -> {
                            main.system.auxiliary.log.LogMaster.dev("Scripts Unlocking..");
                            lock.lock();
                            waiting.signal();
                            lock.unlock();
                        },
                        bool ? null : false, bool);

                main.system.auxiliary.log.LogMaster.dev("Scripts locked!");
                Timer timer = TimerTaskMaster.newTimer(new TimerTask() {
                    @Override
                    public void run() {
                        EUtils.showInfoText("Awaiting input ...");
                    }
                }, 2500);
                lock.lock();
                try {
                    if (max > 0) {
                        waiting.await(max, TimeUnit.MILLISECONDS);
                    } else
                        waiting.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer.cancel();
                main.system.auxiliary.log.LogMaster.dev("Scripts Unlocked!");

                if (vars.size() > 1) {
                    execute(WAIT, vars.get(1));
                } else {
                    if (vars.size() > 0) {
                        execute(WAIT, vars.get(0));
                    }
                }
                break;
            case PASS:
                DungeonScreen.getInstance().getController().inputPass();
                break;

            case WAIT_FOR:
                pause();
                if (vars.size() > 0) {
                    max = Integer.valueOf(vars.get(0));
                }
                WaitMaster.waitForInput(WaitMaster.getOperation(value), max);
                resume();
                break;
            case OFFSET:
                if (value.isEmpty()) {
                    offset = null;
                } else
                    offset = new AbstractCoordinates(value);
                break;
            case ATTACHED:
            case COLOR:
            case SCREEN:
            case DISPLACE:
                GuiEventManager.trigger(("GRID_" + speechAction.name()), getUnit(value), vars.get(0));
                break;
            case VFX:
                bool = true;
                value = SpellVfxMaster.checkAlias(value);
            case SPRITE:
                String script = null;
                Boolean sequential = null;
                Coordinates dest = null;
                boolean flipX;//TODO
                for (String var : vars) {
                    //spriteData
                    if (Texts.getScript(var) != null) {
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
                    if (skipRun) {
                        execute(SCRIPT, script);
                        break;
                    }
                    String finalScript = script;
                    onDone = () -> {
                        if (finalScript != null) {
                            Eidolons.onThisOrNonGdxThread(() ->
                                    execute(SCRIPT, finalScript));
                        }
                    };
                }
                SimpleAnim simpleAnim = new SimpleAnim(
                        bool ? value : "",
                        bool ? "" : value, onDone);
                v = getCenteredPos(c);
                simpleAnim.setOrigin(v);
//                simpleAnim.setBlending(b);
//                simpleAnim.setFps(f);
                if (dest != null) {
                    Vector2 v2 = getCenteredPos(dest);
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
                        FACING_DIRECTION.NORTH, GenericEnums.BLENDING.SCREEN);

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
                    } else if (var.contains("::")) {
                        SpriteData graphicData = new SpriteData(var);
                        data.setSpriteData(graphicData);
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

            case SCRIPT_CHANCE:
                if (!RandomWizard.chance(Integer.valueOf(vars.get(0)))) {
                    break;
                }
            case SCRIPT_PARALLEL: //TODO separate if / parallel
                bool = true;
            case TUT_SCRIPT:
                if (speechAction == TUT_SCRIPT)
                    if (!EidolonsGame.getAny("tutorial")) {
                        break;
                    }
            case SCRIPT_IF:
                if (speechAction == SCRIPT_IF)
                    if (!EidolonsGame.getAny(vars.get(0))) {
                        break;
                    }
            case SCRIPT:
                String d = Texts.getScript(value);
                if (d != null) {
                    main.system.auxiliary.log.LogMaster.important("Nested script: " + d);
                    if (!vars.isEmpty() || bool) {
                        Eidolons.onNonGdxThread(() ->
                                new SpeechScript(d, master).execute());
                    } else
                        new SpeechScript(d, master).execute();

                    break;
                }
                COMBAT_SCRIPT_FUNCTION func = new EnumMaster<COMBAT_SCRIPT_FUNCTION>().
                        retrieveEnumConst(COMBAT_SCRIPT_FUNCTION.class, VariableManager.removeVarPart(value));
                if (func == null) {
                    main.system.auxiliary.log.LogMaster.dev("NO SUCH SCRIPT or function: " + value);
                }
                master.getBattleMaster().getScriptManager().execute(func, Eidolons.getMainHero().getRef(),
                        vars.toArray(new String[vars.size()]));
                break;


            case MOMENT:
                MusicMaster.playMoment(value);
                break;
            case STOP_LOOP:
                if (value.equalsIgnoreCase("all")) {
                    GuiEventManager.trigger(GuiEventType.STOP_LOOPING_TRACK, null);
                    break;
                }
                bool = true;
            case LOOP_TRACK:
                String path = "";
                float volume = 0.5f;
               bla: switch (value) {
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
                        break bla;
                    case "ambi":
                    case "atmo":
                    case "music":
                        MusicMaster.MUSIC_TRACK track = MusicMaster.getTrackByName(vars.get(0));
                        if (vars.size() > 1) {
                            volume = Float.valueOf(vars.get(1));
                        }
                        path = PathFinder.getMusicPath() + track.getPath();
                        break bla;
                }
                GuiEventManager.trigger(bool ?
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
            case PRELOAD_MUSIC:
                MusicMaster.getInstance().getMusic(MusicMaster.MUSIC_TRACK.valueOf(value.toUpperCase()
                        .replace(" ", "_")).getPath(), true);
                break;
            case RANDOM_SOUND:
                DC_SoundMaster.playRandomKeySound(value);
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
            case ACTION_MAP:
                EidolonsGame.setActionSwitch(value, Boolean.valueOf(vars.get(0)));
                reset();
                break;
            case VAR_MAP:
                EidolonsGame.setVar(value, Boolean.valueOf(vars.get(0)));
                break;
            case CINEMATICS:
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
            case PORTAL:
                switch (value) {
                    case "loop":
                    case "open":
                    case "close":
//                        PortalMaster
                        break;
                }
            case COMMENT_CENTERED:
                bool = true;
            case COMMENT:
                if (vars.size() > 2) {
                    execute(WAIT_ANIMS, vars.get(2));
                }
                if (vars.size() > 0) {
                    unit = getUnit(vars.get(0));
                }
                if (unit == null) {
                    ObjType objType = DataManager.getType(vars.get(0), C_OBJ_TYPE.UNITS_CHARS);
                    if (vars.size() > 1) {
                        c1 = getCoordinate(vars.get(1), true);
                    }
                    //TODO test
                    if (bool) {
                        c = getCameraCenter();
                    } else
                        c = Eidolons.getMainHero().getCoordinates().getOffset(c1);
                    if (c1 != null) {
                        c = c.getOffset(c1);
                    }
                    String portrait = ImageManager.getBlotch(objType);
                    CombatScriptExecutor.doComment(objType.getName(), portrait, c, value);
                } else {
                    Vector2 offset = null;
                    if (vars.size() > 1) {
                        offset = getCenteredPos(getCoordinate(vars.get(1), true));
                        if (getCoordinate(vars.get(1), true).dst(unit.getCoordinates()) > 10) {
                            offset = getCenteredPos(getCoordinate(vars.get(1), true)
                                    .getOffset(unit.getCoordinates()));
                        }
                    }
                    CombatScriptExecutor.doComment((Unit) unit, value, offset);
                }
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
            case UNFREEZE:
                execute(BUFF_REMOVE, value + StringMaster.wrapInParenthesis("Disabled"));
                break;
            case FREEZE:
                execute(BUFF_ADD, value + StringMaster.wrapInParenthesis("Disabled"));
                break;
            case RESET_VISION:
                master.getGame().getVisionMaster().overrideVisionOff(getUnit(value));
                break;
            case VISION:
                master.getGame().getVisionMaster().overrideVision(getUnit(value), vars.get(0));
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
            case LINKED_OBJ:
                unit = getUnit(vars.get(0));
            case GRID_OBJ_ANIM:
            case REMOVE_GRID_OBJ:
            case GRID_OBJ:
                if (!vars.isEmpty()) {
                    c = getCoordinate(vars.get(0));
                }
                Boolean under = null;
                if (vars.size() > 1) {
                    under = Boolean.valueOf(vars.get(1));
                }
                //wards and awakening!
                if (speechAction == GRID_OBJ_ANIM) {
                    GuiEventManager.trigger(GuiEventType.GRID_OBJ_ANIM, value, c, new GraphicData(vars.get(1)));
                } else if (speechAction == REMOVE_GRID_OBJ) {
                    GuiEventManager.trigger(GuiEventType.REMOVE_GRID_OBJ, value, c);
                } else {
                    GridObject x;
                    obj = new EnumMaster<CUSTOM_OBJECT>().retrieveEnumConst(CUSTOM_OBJECT.class, value);
                    if (speechAction == LINKED_OBJ) {
                        BaseView view = DungeonScreen.getInstance().getGridPanel().getViewMap().get(unit);
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

                break;
            case AREA:
                c = getCoordinate(vars.get(0), true);
                c1 = getCoordinate(vars.get(1), true);
                for (Coordinates coordinates : CoordinatesMaster.getCoordinatesBetween(c, c1)) {
                    for (BattleFieldObject object : Eidolons.getGame().getObjectsOnCoordinate(coordinates)) {
                        doUnit(object, value, vars);
                    }
                }
                break;
            case COORDINATE:
                c = getCoordinate(vars.get(0));
                for (BattleFieldObject object : Eidolons.getGame().getObjectsOnCoordinate(c)) {
                    doUnit(object, value, vars);
                }
                break;
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
            case VIDEO:
                Runnable r = null;
                if (vars.size() > 0) {
                    r = () -> Eidolons.onNonGdxThread(() -> execute(SCRIPT, vars.get(0)));
                }
                GuiEventManager.trigger(GuiEventType.PLAY_VIDEO, value, r);
                break;
            case BREAK_IF:
                if (EidolonsGame.getAny(value)) {
                    return false;
                }
                return true;
            case CONTINUE_IF:
                if (!EidolonsGame.getAny(value)) {
                    return false;
                }
                return true;

        }
        return true;
    }

    private void reset() {

        master.getGame().getManager().reset();
    }

    private boolean isVisionRefreshRequired() {
        return !container.isOpaque();
    }

    private boolean checkSkip(SPEECH_ACTION speechAction) {
        switch (speechAction) {
            case FULLSCREEN:
            case BLACKOUT:
            case WHITEOUT:
            case COMMENT:
            case SOUND:
            case MUSIC:
            case WAIT:
            case WAIT_FOR:
            case WAIT_ANIMS:
            case WAIT_PASS:
                return true;
        }
        return false;
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
        doUnit(unit, value, vars);
    }

    private void doUnit(BattleFieldObject unit, String value, List<String> vars) {
        //same find-alg!
        if (unit != null) {
            switch (value) {
                case "addAction":
                case "removeAction":
                    break;


                case "remove":
                    unit.kill(unit, false, true);
                    GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, unit);
                    break;
                case "show":
                    unit.setHidden(false);

                    break;
                case "hide":
                    //TODO kind of fade out
                    unit.setHidden(true);

                    break;

                case "replace":

                case "fade":
//                    unit.kill(unit, false, false);
                    unit.kill(unit, false, true);
                    if (unit instanceof Unit) {
                        new DeathAnim(unit).startAsSingleAnim(Ref.getSelfTargetingRefCopy(unit));
                    }
                    if (value.equalsIgnoreCase("replace"))
                        if (vars.size() > 0) {
                            execute(BF_OBJ, vars.get(0) + StringMaster.wrapInParenthesis(unit.getCoordinates().toString()));
                        }
                    break;
                case "kill":
                    unit.kill(Eidolons.getMainHero(), true, false);

                    break;
                case "die":
                    unit.kill();

                    break;
            }
            if (isVisionRefreshRequired())
                unit.getGame().getVisionMaster().refresh();
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
                    v = getCenteredPos(Eidolons.getMainHero().getCoordinates());
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
        return getCoordinate(value, false);
    }

    private Coordinates getCoordinate(String value, boolean abstract_) {
        value = value.trim();
        if (value.contains("+")) {
            Coordinates c = getCoordinate(value.split("[+]")[0], true);
            Coordinates c1 = getCoordinate(value.split(("[+]"))[1], true);
            return c.getOffset(c1);
        }
        Coordinates c = null;
        if (!value.contains("-") && !value.contains(":")) {
            BattleFieldObject unit = getUnit(value);
            if (unit != null) {
                return unit.getCoordinates();
            }
            return master.getGame().getDungeon().getCoordinateByName(value);
        }
        if (c == null)
            try {
                c = abstract_ ? new AbstractCoordinates(value)
                        : Coordinates.get(value);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (c.isInvalid()) {
            return null;
        }
        if (!abstract_)
            if (offset != null) {
                c = c.getOffset(offset);
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
        if (CoreEngine.isSuperLite() && CoreEngine.isIDE()) {
            millis = millis / 2;
        } else if (CoreEngine.isMyLiteLaunch()) {
            millis = millis * 2 / 3;
        }
        boolean absolute = false;
        if (!absolute) {
            millis = (int) (millis / (Interpolation.linear.apply(1, handler.getSpeed(), coef)));

        }
        return millis;
    }

    private void WAIT(int millis) {
        if (skipRun) {
            return;
        }
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
        lastScript = speechScript;
        if (finalScript) {
            return;
        }
        running = true;
        important("Executing script: " + speechScript.toString());
        for (Pair<SPEECH_ACTION, String> pair : speechScript.actions) {
            if (!executeAction(pair.getKey(), pair.getValue())) {
                important("Script aborted");
                return;
            }
        }
        important("Script executed");
        running = false;
    }

    public void checkSkipRun(String var) {
        setSkipRun(false);
        if (EidolonsGame.getAny(var)) {
            setSkipRun(true);
        }

    }

    public void setSkipRun(boolean skipRun) {
        this.skipRun = skipRun;
        handler.setSkipping(skipRun);
    }

    private boolean executeAction(SPEECH_ACTION speechAction, String value) {
        important("Executing action: " + speechAction + " = " + value);
        return execute(speechAction, value);

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

