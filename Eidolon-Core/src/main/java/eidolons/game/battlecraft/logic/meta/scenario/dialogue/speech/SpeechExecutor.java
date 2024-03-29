package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import com.badlogic.gdx.math.Interpolation;
import eidolons.system.libgdx.GdxStatic;
import eidolons.system.libgdx.datasource.FullscreenAnimDataSource;
import eidolons.system.libgdx.wrapper.VectorGdx;
import eidolons.content.consts.VisualEnums.FULLSCREEN_ANIM;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.game.exploration.dungeon.generator.model.AbstractCoordinates;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.content.consts.GraphicData;
import eidolons.content.consts.SpriteData;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.Texts;
import main.content.C_OBJ_TYPE;
import main.content.ContentValsManager;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.BLENDING;
import main.content.enums.GenericEnums.SOUND_CUE;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.sound.AudioEnums.SOUNDSCAPE;
import main.system.threading.TimerTaskMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static eidolons.content.consts.VisualEnums.*;
import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SCRIPT.*;
import static main.system.auxiliary.log.LogMaster.important;

public class SpeechExecutor {

    protected static final boolean ABORT_ON_EXCEPTION = false;
    protected final DialogueManager dialogueManager;
    protected DialogueHandler handler; //gdx Review - don't want to depend..
    protected DialogueContainerAdapter container;
    protected MetaGameMaster master;
    protected int waitOnEachLine;
    protected boolean waiting;
    protected boolean running;
    protected boolean paused;
    protected AbstractCoordinates offset;
    protected boolean skipRun;
    protected boolean finalScript;
    protected SpeechScript lastScript;
    protected SpeechScript lt;

    GdxSpeechActions gdxActions;

    public SpeechExecutor(MetaGameMaster master, DialogueManager dialogueManager) {
        this.master = master;
        this.dialogueManager = dialogueManager;
        gdxActions = master.getGdxBeans().createGdxActions();
    }

    public static void run(String s) {
        try {
            Core.getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(s);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void execute(String speechAction, String value) {
        execute(new EnumMaster<SpeechScript.SCRIPT>().retrieveEnumConst(SpeechScript.SCRIPT.class, speechAction), value);
    }

    public boolean execute(SpeechScript.SCRIPT speechAction, String value) {
        try {
            return execute(speechAction, value, true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return !ABORT_ON_EXCEPTION;
    }

    public boolean execute(SpeechScript.SCRIPT speechAction, String value, boolean wait) {
        container = dialogueManager.getContainer();
        if (container != null) {
            handler = container.getHandler();
        }
        if (handler == null) {
            handler = new FauxDialogueHandler();
        }
        value = value.trim().toLowerCase();
        String full = value;
        List<String> vars = VariableManager.getVarList(value);
        value = VariableManager.removeVarPart(value);
        boolean ui = false;
        boolean bool = false;
        boolean random = false;
        CUSTOM_OBJECT obj = null;
        Coordinates c = null;
        int max = 0;
        Coordinates c1 = null;
        String string = null;
        BattleFieldObject unit = Core.getMainHero();
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
            case AWAKEN:
            case RAISE:
            case COLLAPSE:
                gdxActions.execute(speechAction, value, vars);
                break;

            case RESET:
                master.getGame().getManager().reset();
                break;
            case END:
                finalScript = true;
                break;
            case HIGHLIGHT_ACTION:
                ActiveObj a = Core.getMainHero().getActionOrSpell(value);
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
                Flags.setFlag(value, Boolean.valueOf(vars.get(0)));
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
                    ActiveObj active = ((Unit) unit).getActionOrSpell(vars.get(0));
                    active.setDisabled(true);
                }
                break;

            case REVEAL:
            case SWITCH:
                bool = true;
            case CONCEAL:
                getUnit(value).setRevealed(bool);
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
                            for (BattleFieldObject object : master.getGame().getObjectsOnCoordinateNoOverlaying(c)) {
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
                master.getMissionMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.ORDER,
                        unit.getRef(), unit.getName(), vars.get(0));
                break;
            case MOVE:
                unit = getUnit(value);
                LogMaster.devLog("MOVING: " + unit.getNameAndCoordinate());
                unit.setCoordinates((getCoordinate(vars.get(0))));
                LogMaster.devLog("MOVED: " + unit.getNameAndCoordinate());

                GuiEventManager.trigger(GuiEventType.UNIT_MOVED, unit);
                if (isVisionRefreshRequired()) {
                    GuiEventManager.trigger(GuiEventType.UNIT_FADE_OUT_AND_BACK, unit);
                    master.getGame().getVisionMaster().refresh();
                }
                break;
            case ACTION:
                if (vars.size() > 0) {
                    unit = getUnit(vars.get(0));
                }
                if (unit == null) {
                    unit = Core.getMainHero();
                }

                Object arg = null;
                if (vars.size() > 1) {
                    arg = getUnit(vars.get(1));
                    if (arg == null) {
                        arg = getCoordinate(vars.get(1));
                    }
                }
                master.getMissionMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.ACTION,
                        unit.getRef(), unit, value, arg);
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
                getUnit(value).setParam(ContentValsManager.getPARAM(vars.get(0)), Integer.valueOf(vars.get(1)), true, true);
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
                coordinatesList = CoordinatesMaster.getCoordinatesBetweenInclusive(c, c1);

            case ADD:
            case BF_OBJ:
                c = getCoordinate(vars.get(0));
            case NAMED_COORDINATES_ADD: //TODO
                // if (speechAction == NAMED_COORDINATES_ADD) {
                //     coordinatesList = new ArrayList<>();
                //     Map<String, String> map = master.getGame().getDungeon().getCustomDataMap(
                //             CellScriptData.CELL_SCRIPT_VALUE.dialogue);
                //     for (String s : map.keySet()) {
                //         if (map.get(s).equalsIgnoreCase(value)) {
                //             coordinatesList.add(Coordinates.get(s));
                //         }
                //     }
                // }
                // ObjType bfType = DataManager.getType(value,
                //         C_OBJ_TYPE.BF_OBJ);
                // if (coordinatesList != null) {
                //     int chance = 100;
                //     if (vars.size() > 2) {
                //         chance = Integer.valueOf(vars.get(2));
                //     }
                //     for (Coordinates coordinates : coordinatesList) {
                //         if (!RandomWizard.chance(chance)) {
                //             continue;
                //         }
                //         master.getGame().createObject(bfType, coordinates,
                //                 speechAction == ADD ? master.getGame().getPlayer(false)
                //                         : DC_Player.NEUTRAL);
                //     }
                //     break;
                // }
                //
                // master.getGame().createObject(bfType, c,
                //         speechAction == ADD ? master.getGame().getPlayer(false)
                //                 : DC_Player.NEUTRAL);
                // break;

            case TIP:
                master.getMissionMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TIP, new Ref(), value);
                //TODO after?
                break;

            case TURN:
                unit = getUnit(vars.get(0));
                master.getMissionMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TURN_TO,
                        unit.getRef(), unit.getName(), value);
                //TODO doing it twice because bug..
                master.getMissionMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.TURN_TO,
                        unit.getRef(), unit.getName(), value);
                break;

            case TRIGGER_REMOVE:
                if (value.equalsIgnoreCase("last")) {
                    master.getMissionMaster().getScriptManager().removeLast();
                } else {
                    //                    TODO master.getBattleMaster().getScriptManager().remove(vars.getVar(0));
                }
                break;
            case QUEST_DONE:
                master.getQuestMaster().questComplete(value);
                break;
            case QUEST_ADD:
                if (vars.isEmpty()) {
                    master.getQuestMaster().getQuest(value).increment();
                } //TODO
                break;
            case QUEST:
                master.getQuestMaster().questTaken(value, vars.isEmpty());
                break;

            case LOAD_SCOPE:
                GuiEventManager.trigger(GuiEventType.LOAD_SCOPE, value);
                break;
            case UNLOAD_SCOPE:
                GuiEventManager.trigger(GuiEventType.DISPOSE_SCOPE, value);
                break;
            case TRIGGER:
                master.getMissionMaster().getScriptManager().parseScripts(full);
                break;

            case WAIT_PASS:
                bool = true;
                if (!vars.isEmpty()) {
                    max = Integer.valueOf(vars.get(0));
                }
            case WAIT_FOR_NO_COMMENTS:
                max = Integer.valueOf(value);
                WaitMaster.waitForCondition(delta ->
                        gdxActions.getNoCommentsCondition(), max);
                break;
            case WAIT_ANIMS:
                Predicate<Float> p = gdxActions.getNoAnimsCondition();
                LogMaster.devLog("Anims wait locked! ");
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
                GdxAdapter.onInput(() -> {
                            LogMaster.devLog("Scripts Unlocking..");
                            lock.lock();
                            waiting.signal();
                            lock.unlock();
                        },
                        bool ? null : false, bool);

                LogMaster.devLog("Scripts locked!");
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
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                timer.cancel();
                LogMaster.devLog("Scripts Unlocked!");

                if (vars.size() > 1) {
                    execute(WAIT, vars.get(1));
                } else {
                    if (vars.size() > 0) {
                        execute(WAIT, vars.get(0));
                    }
                }
                break;
            case PASS:
                GdxAdapter.inputPass();
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
            case AMBI_VFX:
                GenericEnums.VFX vfx = new EnumMaster<GenericEnums.VFX>().retrieveEnumConst(GenericEnums.VFX.class, value);
                GuiEventManager.triggerWithParams(GuiEventType.ADD_AMBI_VFX, vfx, getCoordinate(vars.get(0)));
                break;
            case VFX:
                bool = true;
                //TODO gdx sync
                // value = SpellVfxMaster.checkAlias(value);
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
                            Core.onThisOrNonGdxThread(() ->
                                    execute(SCRIPT, finalScript));
                        }
                    };
                }
                gdxActions.doSpriteAnim(bool, value, onDone, c, dest, sequential);

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
                    important("Nested script: " + d);
                    if (!vars.isEmpty() || bool) {
                        Core.onNonGdxThread(() ->
                                new SpeechScript(d, master).execute());
                    } else {
                        SpeechScript subscript = new SpeechScript(d, master);
                        subscript.execute();
                        if (subscript.interrupted) {
                            return false;
                        }
                    }

                    break;
                }
                COMBAT_SCRIPT_FUNCTION func = new EnumMaster<COMBAT_SCRIPT_FUNCTION>().
                        retrieveEnumConst(COMBAT_SCRIPT_FUNCTION.class, VariableManager.removeVarPart(value));
                if (func == null) {
                    LogMaster.devLog("NO SUCH SCRIPT or function: " + value);
                }
                master.getMissionMaster().getScriptManager().execute(func, Core.getMainHero().getRef(),
                        vars.toArray(new String[0]));
                break;


            case MOMENT:
                MusicMaster.playMoment(value);
                break;
            case STOP_LOOP_NOW:
                if (value.equalsIgnoreCase("all")) {
                    GuiEventManager.trigger(GuiEventType.STOP_LOOPING_TRACK_NOW, null);
                    break;
                }

            case STOP_LOOP:
                if (value.equalsIgnoreCase("all")) {
                    GuiEventManager.trigger(GuiEventType.STOP_LOOPING_TRACK, null);
                    break;
                }
                bool = true;
            case LOOP_TRACK:
                String path = "";
                float volume = 0.5f;
                bla:
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
                        break bla;
                    case "ambi":
                        //                    case "atmo":
                    case "music":
                        MusicEnums.MUSIC_TRACK track = MusicMaster.getTrackByName(vars.get(0));
                        if (vars.size() > 1) {
                            volume = Float.valueOf(vars.get(1));
                        }
                        path = PathFinder.getMusicPath() + track.getPath();
                        break bla;
                }
                if (bool) {
                    GuiEventManager.trigger(
                            GuiEventType.STOP_LOOPING_TRACK, path);
                } else {
                    GuiEventManager.triggerWithParams(GuiEventType.ADD_LOOPING_TRACK, path, volume);
                }
                break;
            case PARALLEL_MUSIC:
                MusicMaster.getInstance().playParallel(value);
                break;
            case SOUNDSCAPE:
                SOUNDSCAPE soundscape = new EnumMaster<SOUNDSCAPE>().retrieveEnumConst(SOUNDSCAPE.class, value);
                Float vol = Float.valueOf(vars.get(0));
                GuiEventManager.triggerWithParams(GuiEventType.SET_SOUNDSCAPE_VOLUME,
                        soundscape, vol);
                break;
            case MUSIC:
                MusicMaster.getInstance().overrideWithTrack(value);
                break;
            case PRELOAD_MUSIC:
                MusicMaster.getInstance().getMusic(MusicEnums.MUSIC_TRACK.valueOf(value.toUpperCase()
                        .replace(" ", "_")).getPath(), true);
                break;
            case RANDOM_SOUND:
                DC_SoundMaster.playRandomKeySound(value);
                break;
            case SOUND_VARIANT:
                random = true;
            case SOUND:
                DC_SoundMaster.getPlayer().setCinematicSoundOverride(true);
                volume = 1f;
                if (vars.size() > 0) {
                    volume = Float.valueOf(vars.get(0));
                }
                try {
                    DC_SoundMaster.playKeySound(value, volume, random);
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                } finally {
                    DC_SoundMaster.getPlayer().setCinematicSoundOverride(false);
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
                //TODO
                //animate the portait displayed in UI?
                // AnimMaster.onCustomAnim(value, true, 1, () -> {
                //     //                    handler.continues();
                // });
                break;
            case SHAKE:
                gdxActions.doShake(value, vars);
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
                        c = GdxStatic.getCameraCenter();
                    } else
                        c = Core.getPlayerCoordinates().getOffset(c1);
                    if (c1 != null) {
                        c = c.getOffset(c1);
                    }
                    String portrait = ImageManager.getBlotch(objType);
                    CombatScriptExecutor.doComment(objType.getName(), portrait, c, value);
                } else {
                    VectorGdx offset = null;
                    if (vars.size() > 1) {
                        offset = GdxStatic.getCenteredPos(getCoordinate(vars.get(1), true));
                        if (getCoordinate(vars.get(1), true).dst(unit.getCoordinates()) > 10) {
                            offset = GdxStatic.getCenteredPos(getCoordinate(vars.get(1), true)
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
                GuiEventManager.triggerWithParams(GuiEventType.SET_PARTICLES_ALPHA, type, alpha);
                break;
            case ZOOM:
                //                if (vars.size() < 2) {
                //                    vars.add("swing");
                //                }
                gdxActions.doZoom(value, vars);
                break;
            case AUTOCAMERA:
                handler.setAutoCamera(!value.equalsIgnoreCase("off"));
                break;


            case DIALOGUE_AFTER:
                String finalValue = value;
                WaitMaster.doAfterWait(getWaitTime(Integer.valueOf(vars.get(0)), vars),
                        () ->
                                master.getMissionMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.DIALOGUE,
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
                gdxActions.doCamera(value, vars, speechAction);
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
                    GuiEventManager.triggerWithParams(GuiEventType.GRID_OBJ_ANIM, value, c, new GraphicData(vars.get(1)));
                } else if (speechAction == REMOVE_GRID_OBJ) {
                    GuiEventManager.triggerWithParams(GuiEventType.REMOVE_GRID_OBJ, value, c);
                } else {
                    gdxActions.doGridObj(speechAction, unit, c, under, value, obj);
                }

                break;
            case AREA:
                c = getCoordinate(vars.get(0), true);
                c1 = getCoordinate(vars.get(1), true);
                for (Coordinates coordinates : CoordinatesMaster.getCoordinatesBetweenInclusive(c, c1)) {
                    for (BattleFieldObject object : Core.getGame().getObjectsOnCoordinateNoOverlaying(coordinates)) {
                        doUnit(object, value, vars);
                    }
                }
                break;
            case COORDINATE:
                c = getCoordinate(vars.get(0));
                for (BattleFieldObject object : Core.getGame().getObjectsOnCoordinateNoOverlaying(c)) {
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
                container.setTime(new Float(Integer.valueOf(value)));
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
                container.disableTimer();
                container.setTime(new Float(getNextTime(Integer.valueOf(value), vars)));
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
                    r = () -> Core.onNonGdxThread(() -> execute(SCRIPT, vars.get(0)));
                }
                GuiEventManager.triggerWithParams(GuiEventType.PLAY_VIDEO, value, r);
                break;
            case BREAK_IF:
                return !EidolonsGame.getAny(value);
            case CONFIRM:
                if (!EUtils.waitConfirm(value)) {
                    return bool;
                }
                if (!vars.isEmpty()) {
                    EidolonsGame.setVar(vars.get(0), true);
                    execute(SCRIPT, vars.get(0));
                }
                return !bool;
            case CONTINUE_IF:
                return EidolonsGame.getAny(value);
            case GLOBAL_CONTINUE_IF:
                if (!EidolonsGame.getAny(value)) {
                    lastScript.interrupted = true;
                    return false;
                }
                return true;
            case GUI_EVENT:
                GuiEventManager.trigger(value);
                break;
        }
        return true;
    }

    protected void reset() {

        master.getGame().getManager().reset();
    }

    protected boolean isVisionRefreshRequired() {
        return !container.isOpaque();
    }

    protected boolean checkSkip(SpeechScript.SCRIPT speechAction) {
        switch (speechAction) {
            case FULLSCREEN:
            case BLACKOUT:
            case WHITEOUT:
            case COMMENT:
                //            case SOUND:
                //            case MUSIC:
            case WAIT:
            case WAIT_FOR:
            case WAIT_ANIMS:
            case WAIT_PASS:
                return true;
        }
        return false;
    }

    protected void resume() {
        container.resume();
    }

    protected void pause() {
        container.pause();
    }


    protected void doOut(String value, List<String> vars, SpeechScript.SCRIPT speechAction) {
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

    protected void doAnim(Boolean ui_bg_both, String value, List<String> vars) {
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
                container.fade(dur, alpha, ui_bg_both);
            } else
                container.fadeBg(dur, alpha);
        }
    }

    protected void doUnit(String value, List<String> vars) {
        BattleFieldObject unit = vars.size() == 0 ? Core.getMainHero() : getUnit(vars.get(0));
        doUnit(unit, value, vars);
    }

    protected void doUnit(BattleFieldObject unit, String value, List<String> vars) {
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
                        //TODO gdx sync
                        // new DeathAnim(unit).startAsSingleAnim(Ref.getSelfTargetingRefCopy(unit));
                    }
                    if (value.equalsIgnoreCase("replace"))
                        if (vars.size() > 0) {
                            execute(BF_OBJ, vars.get(0) + StringMaster.wrapInParenthesis(unit.getCoordinates().toString()));
                        }
                    break;
                case "kill":
                    unit.kill(Core.getMainHero(), true, false);

                    break;
                case "die":
                    unit.kill();

                    break;
            }
            if (isVisionRefreshRequired())
                unit.getGame().getVisionMaster().refresh();
        }

    }


    protected BattleFieldObject getUnit(String value) {
        value = value.trim();
        switch (value.toLowerCase()) {
            case "me":
            case "self":
            case "source":
                return Core.getMainHero();
        }
        BattleFieldObject unit = master.getGame().getAiManager().getScriptExecutor().findUnit(
                Core.getMainHero().getRef(), value);
        if (unit == null) {
            DialogueActor actor = DialogueActorMaster.getActor(value);
            if (actor != null) {
                if (actor.getLinkedUnit() == null) {
                    actor.setupLinkedUnit();
                }
                return actor.getLinkedUnit();
            }
        }
        return unit;
    }

    public Coordinates getCoordinate(String value) {
        return getCoordinate(value, false);
    }

    protected Coordinates getCoordinate(String value, boolean abstract_) {
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

    protected int getNextTime(Integer millis, List<String> vars) {
        return getTime(millis, vars, 1f);
    }

    protected int getWaitTime(Integer millis, List<String> vars) {
        return getTime(millis, vars, 0.5f);
    }

    protected int getTime(Integer millis, List<String> vars, float coef) {
        if (Flags.isSuperLite() && Flags.isIDE()) {
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

    protected void WAIT(int millis) {
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
        for (Pair<SpeechScript.SCRIPT, String> pair : speechScript.actions) {
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

    protected boolean executeAction(SpeechScript.SCRIPT speechAction, String value) {
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

