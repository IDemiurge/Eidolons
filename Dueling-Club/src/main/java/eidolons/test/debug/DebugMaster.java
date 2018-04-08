package eidolons.test.debug;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.oneshot.unit.CreateObjectEffect;
import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.battle.arena.ArenaBattleMaster;
import eidolons.game.battlecraft.logic.battle.arena.Wave;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.state.*;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.game.module.herocreator.logic.spells.LibraryManager;
import eidolons.libgdx.anims.controls.EmitterController;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.DC_Formulas;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.TestMasterContent;
import eidolons.test.PresetMaster;
import eidolons.test.auto.AutoTestMaster;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.common.OwnershipChangeEffect;
import main.content.C_OBJ_TYPE;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.elements.conditions.Conditions;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.logic.battle.player.Player;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;
import main.system.auxiliary.log.SpecialLogger;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.Stack;

import static eidolons.test.debug.DebugMaster.DEBUG_FUNCTIONS.*;

/**
 * @author JustMe
 */

public class DebugMaster {
    public static final char HOTKEY_CHAR = 'd';
    public static final char FUNCTION_HOTKEY_CHAR = 'f';

    public static final DEBUG_FUNCTIONS[] group_add_bf_obj = {
     DEBUG_FUNCTIONS.ADD_UNIT,
     DEBUG_FUNCTIONS.ADD_ENEMY_UNIT,
     DEBUG_FUNCTIONS.ADD_GROUP,
     DEBUG_FUNCTIONS.ADD_CHAR,
     DEBUG_FUNCTIONS.ADD_OBJ,
     DEBUG_FUNCTIONS.SPAWN_PARTY,
     DEBUG_FUNCTIONS.SPAWN_WAVE,
     DEBUG_FUNCTIONS.SPAWN_CUSTOM_WAVE,

    };
    public static final DEBUG_FUNCTIONS[] group_bf = {
     DEBUG_FUNCTIONS.CLEAR,
     DEBUG_FUNCTIONS.RESTART,
     DEBUG_FUNCTIONS.KILL_ALL_UNITS,
     DEBUG_FUNCTIONS.END_TURN,
//            DEBUG_FUNCTIONS.CLEAR_WAVES,
//            DEBUG_FUNCTIONS.LOAD_DUNGEON,
    };
    public static final DEBUG_FUNCTIONS[] group_basic = {
     DEBUG_FUNCTIONS.END_TURN,
     DEBUG_FUNCTIONS.PAUSE,
     DEBUG_FUNCTIONS.SAVE,
     DEBUG_FUNCTIONS.LOAD,
     GUI_EVENT
    };
    public static final DEBUG_FUNCTIONS[] group_toggle = {
     TOGGLE_DUMMY,
     TOGGLE_DUMMY_PLUS,
     TOGGLE_FREE_ACTIONS,
     TOGGLE_OMNIVISION,
//     TOGGLE_LIGHTING,
//     TOGGLE_FOG,
    };
    public static final DEBUG_FUNCTIONS[] group_add = {
     DEBUG_FUNCTIONS.ADD_SKILL,
     DEBUG_FUNCTIONS.ADD_ACTIVE,
     DEBUG_FUNCTIONS.ADD_SPELL,
     DEBUG_FUNCTIONS.ADD_ITEM,

    };
    public static final DEBUG_FUNCTIONS[] group_sfx = {

     DEBUG_FUNCTIONS.SFX_ADD,
     DEBUG_FUNCTIONS.SFX_SET,
     DEBUG_FUNCTIONS.SFX_PLAY_LAST,
     DEBUG_FUNCTIONS.SFX_ADD_RANDOM,
     DEBUG_FUNCTIONS.SFX_MODIFY,
     DEBUG_FUNCTIONS.SFX_SAVE,
    };
    public static final DEBUG_FUNCTIONS[] group_graphics = {
     DEBUG_FUNCTIONS.TOGGLE_LIGHTING,
     DEBUG_FUNCTIONS.TOGGLE_FOG,
    };
    public static final HIDDEN_DEBUG_FUNCTIONS[] group_display = {
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_EFFECTS,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_TRIGGERS,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_EFFECTS,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_EVENT_LOG,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_LOG,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_STATE,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_OBJECTS,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_UNITS,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_REF,
     HIDDEN_DEBUG_FUNCTIONS.DISPLAY_UNIT_INFO,
    };
    public static final Object[][] groups = {
     group_add_bf_obj,
     group_bf,
     group_display,
     group_add,
     group_toggle,
    };
    public static boolean ALT_AI_PLAYER;
    private static boolean omnivision;
    private static boolean mapDebugOn = true;
    private static boolean altMode;
    private static DC_Obj target;
    public DEBUG_FUNCTIONS[] onStartFunctions = {DEBUG_FUNCTIONS.GOD_MODE,
     SPAWN_WAVE};
    Unit selectedTarget = null;
    private String lastFunction;
    private Stack<String> executedFunctions = new Stack<>();
    // public void editAi(DC_HeroObj unit) {
    // UnitAI ai = unit.getUnitAI();
    // }
    private DC_Game game;
    private DC_GameState state;
    private DebugPanel debugPanel;
    private ObjType selectedType;
    private DC_Player altAiPlayer;
    private String lastType;
    private Obj arg;
    private String type;
    private boolean quiet;
    private boolean debugFunctionRunning;


    public DebugMaster(DC_GameState state) {
        this.state = state;
        this.game = state.getGame();
    }

    public static boolean isOmnivisionOn() {
        return omnivision;
    }

    public static void setOmnivisionOn(boolean HACK) {
        omnivision = HACK;
    }

    public static boolean isMapDebugOn() {
        return mapDebugOn;
    }

    public static boolean isAltMode() {
        return altMode;
    }

    public static void setAltMode(boolean altMode2) {
        altMode = altMode2;
    }

    public static void setTarget(DC_Obj obj) {
        target = obj;
    }

    public void promptFunctionToExecute() {
        DC_SoundMaster.playStandardSound(RandomWizard.random() ? STD_SOUNDS.DIS__OPEN_MENU
         : STD_SOUNDS.SLING);
        String message = "Input function name";
        String funcName = JOptionPane.showInputDialog(null, message, lastFunction);
        if (funcName == null) {
            if (AutoTestMaster.isRunning()) {
                funcName = DEBUG_FUNCTIONS.AUTO_TEST_INPUT.name();
            } else {
                reset();
                return;
            }
        }
        if (AutoTestMaster.isRunning()) {
            if (funcName.equalsIgnoreCase("re")) {
                new Thread(new Runnable() {
                    public void run() {
                        AutoTestMaster.runTests();
                    }
                }, " thread").start();
                return;
            }
        }
        if (funcName.contains(" ")) {
            if (funcName.trim().equals("")) {
                int length = funcName.length();
                if (executedFunctions.size() >= length) {
                    for (int i = 0; i < length; i++) {
                        funcName = executedFunctions.pop();
                    }
                }
            }

        }
        if (StringMaster.isInteger(funcName)) {
            try {
                Integer integer = StringMaster.getInteger(funcName);
                if (integer >= DEBUG_FUNCTIONS.values().length) {
                    executeDebugFunctionNewThread(HIDDEN_DEBUG_FUNCTIONS.values()[integer
                     - DEBUG_FUNCTIONS.values().length]);
                    playFuncExecuteSound();
                    return;
                }
                {
                    executeDebugFunctionNewThread(DEBUG_FUNCTIONS.values()[integer]);
                    playFuncExecuteSound();
                    return;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        DEBUG_FUNCTIONS function = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(
         DEBUG_FUNCTIONS.class, funcName);
        if (function != null) {
            executeDebugFunctionNewThread(function);
            playFuncExecuteSound();
            return;
        }

        HIDDEN_DEBUG_FUNCTIONS function2 = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>()
         .retrieveEnumConst(HIDDEN_DEBUG_FUNCTIONS.class, funcName);
        if (function2 != null) {
            executeDebugFunctionNewThread(function2);
        } else {
            function = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(DEBUG_FUNCTIONS.class,
             funcName, true);

            function2 = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>().retrieveEnumConst(
             HIDDEN_DEBUG_FUNCTIONS.class, funcName, true);
            if (StringMaster.compareSimilar(funcName, function.toString()) > StringMaster
             .compareSimilar(funcName, function2.toString())) {
                executeDebugFunctionNewThread(function);
            } else {
                executeDebugFunctionNewThread(function2);
            }

        }
        playFuncExecuteSound();

    }

    private void playFuncExecuteSound() {
        DC_SoundMaster.playStandardSound(RandomWizard.random() ? STD_SOUNDS.SKILL_LEARNED
         : STD_SOUNDS.SPELL_UPGRADE_LEARNED);
    }

    public void editAi(Unit unit) {
        UnitAI ai = unit.getUnitAI();
        GroupAI group = ai.getGroup();
        DialogMaster.confirm("What to do with " + group);

        String TRUE = "Info";
        String FALSE = "Set Behavior";
        String NULL = "Set Parameter";
        String string = "What to do with " + group + "?";
        Boolean result = DialogMaster.askAndWait(string, TRUE, FALSE, NULL);
        LogMaster.log(1, " ");
        DIRECTION info = group.getWanderDirection();

        // TODO GLOBAL AI LOG LEVEL
        if (result == null) {
            AI_PARAM param = new EnumMaster<AI_PARAM>().retrieveEnumConst(AI_PARAM.class,
             ListChooser.chooseEnum(AI_PARAM.class));
            if (param != null) {
                switch (param) {
                    case LOG_LEVEL:
                        ai.setLogLevel(DialogMaster.inputInt(ai.getLogLevel()));
                        break;
                }
            }

        }

		/*
         * display on BF: >> Direction >> Target coordinate for each unit or
		 * patrol >> Maybe even path... >>
		 *
		 *
		 */

        group.getPatrol();

    }

    public void showDebugWindow() {
        if (getDebugPanel() == null) {
            initDebugPanel();
        }
        // if (!getDebugPanel().isVisible())
        // initDebugPanel();
        getDebugPanel().getFrame().setVisible(true);
        toggleDebugPanel();
        if (game.isSimulation()) {
            getDebugPanel().refresh();
        }
    }

    private void toggleDebugPanel() {

        this.getDebugPanel().getFrame().setAlwaysOnTop(!getDebugPanel().getFrame().isAlwaysOnTop());

        if (getDebugPanel().getFrame().isAlwaysOnTop()) {
            getDebugPanel().getFrame().requestFocus();
        }

    }

    private void initDebugPanel() {
        this.setDebugPanel(new DebugPanel(this));

    }

    public boolean isDebugFunctionRunning() {
        return debugFunctionRunning;
    }

    public Object executeDebugFunction(DEBUG_FUNCTIONS func) {
        executedFunctions.push(func.toString());
        boolean transmitted = false;
        if (game.isOnline()) {
            if (func.transmitted) {
                transmitted = true;

            }
        }
        if (target != null) {
            arg = target;
        }

        Unit infoObj = target instanceof Unit ? (Unit) target : null;
        Ref ref = null;
        if (infoObj == null) {
            try {
                infoObj = (Unit) getObj();
            } catch (Exception e) {
            }
        }
        if (infoObj == null) {
            infoObj = game.getManager().getActiveObj();
        }
        if (game.getManager().getActiveObj() != null) {
            ref = new Ref(game, game.getManager().getActiveObj().getId());
        } else {
            ref = new Ref(game);
        }

        ref.setDebug(true);
        Coordinates coordinate = null;
        String data = null;
        DC_TYPE TYPE;

        debugFunctionRunning = true;
        try {
            switch (func) {
                case SET_GLOBAL_ILLUMINATION:
                    game.getVisionMaster().getIlluminationMaster().setGlobalIllumination(
                     DialogMaster.inputInt("SET_GLOBAL_ILLUMINATION",
                      game.getVisionMaster().getIlluminationMaster().getGlobalIllumination()));
                    break;

                case SET_GLOBAL_CONCEALMENT:
                    game.getVisionMaster().getIlluminationMaster().setGlobalConcealment(
                     DialogMaster.inputInt("SET_GLOBAL_CONCEALMENT",
                      game.getVisionMaster().getIlluminationMaster().getGlobalConcealment()));

                    break;
                case TEST_CLONE_STATE:
                    StateCloner.test();
                    break;
                case TEST_LOAD_STATE:
                    StatesKeeper.testLoad();
                    return null;

                case RUN_AUTO_TESTS:
                    AutoTestMaster.runTests();
                    break;
                case AUTO_TEST_INPUT:
                    WaitMaster.receiveInput(WAIT_OPERATIONS.AUTO_TEST_INPUT, true);
                    break;
                case SET_OPTION:
                    OptionsMaster.promptSetOption();
                    break;
                case ADD_GROUP:
                    File groupFile = ListChooser.chooseFile(PathFinder.getUnitGroupPath());
                    if (groupFile == null) {
                        break;
                    }
                    if (arg instanceof DC_Cell) {
                        coordinate = arg.getCoordinates();
                    } else {
                        coordinate = getGame().getBattleFieldManager().pickCoordinate();
                    }
                    if (coordinate == null) {
                        break;
                    }
                    data = FileManager.readFile(groupFile);

                    UnitGroupMaster.setCurrentGroupHeight(MathMaster.getMaxY(data));
                    UnitGroupMaster.setCurrentGroupWidth(MathMaster.getMaxX(data));
                    UnitGroupMaster.setMirror(isAltMode());
                    // String flip = ListChooser.chooseEnum(FLIP.class);
                    // if (flip != null)
                    // UnitGroupMaster.setFlip(new
                    // EnumMaster<FLIP>().retrieveEnumConst(FLIP.class,
                    // flip));
                    // else
                    // UnitGroupMaster.setFlip(null);
                    try {
                        DC_ObjInitializer.createUnits(game.getPlayer(isAltMode()), data, coordinate);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    } finally {
                        UnitGroupMaster.setMirror(false);
                    }

                    break;
                case TOGGLE_DUMMY:
                    game.setDummyMode(!game.isDummyMode());
                    TestMasterContent.setForceFree(game.isDummyMode());
                    break;
                case TOGGLE_DUMMY_PLUS:
                    game.setDummyPlus(!game.isDummyPlus());
                    TestMasterContent.setForceFree(game.isDummyMode());
                    break;

                case PRESET:
                    PresetMaster.handlePreset(isAltMode());

                    break;

                case TOGGLE_DUNGEON_DEBUG: {
                    mapDebugOn = !mapDebugOn;
                    break;
                }


                case HIDDEN_FUNCTION: {
                    int i = DialogMaster.optionChoice(HIDDEN_DEBUG_FUNCTIONS.values(), "...");
                    if (i != -1) {
                        executeHiddenDebugFunction(HIDDEN_DEBUG_FUNCTIONS.values()[i]);
                    }
                    break;
                }
                case TOGGLE_AUTO_UNIT:
                    if (!infoObj.isOwnedBy(game.getPlayer(true))) {
                        infoObj.setOriginalOwner(game.getPlayer(true));
                        infoObj.setOwner(game.getPlayer(true));
                    } else {
                        infoObj.setAiControlled(!infoObj.isAiControlled());
                    }
                    WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
                    break;


                case EDIT_AI:
                    break;
                case SAVE:
                    Saver.save("test");
                    break;
                case LOAD:
                    Loader.loadGame("test.xml");
                    break;
                case PAUSE:
                    DC_Game.game.getLoop().setPaused(!DC_Game.game.getLoop().isPaused());
                    break;
                case TOGGLE_OMNIVISION:
                    omnivision = !omnivision;
                    break;
                case AUTO_COMBAT:
                    game.getPlayer(true).setAi(!game.getPlayer(true).isAi());
                    WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
                    break;
                case ADD_TEST_SPELLS:
                    TestMasterContent.addTestActives(false, infoObj.getType(), true);
                    break;
                case ADD_ALL_SPELLS:
                    TestMasterContent.addTestActives(true, infoObj.getType(), true);
                    break;
                case TOGGLE_LOG: {
                    String e = ListChooser.chooseEnum(LOG_CHANNEL.class);
                    LogMaster.toggle(e);
                    break;
                }

                case TOGGLE_QUIET:
                    quiet = !quiet;
                    break;
                case TOGGLE_FREE_ACTIONS:
                    TestMasterContent.toggleFree();
                    break;
                case GOD_MODE:
                    TestMasterContent.toggleImmortal();
                    // game.getManager().getActiveObj().setGodMode(
                    // !game.getManager().getActiveObj().isGodMode());
                    // DebugUtilities.initGodMode(game.getManager().getActiveObj(),
                    // game.getManager()
                    // .getActiveObj().isGodMode());
                    // game.getManager().refreshAll();
                    break;

                case RESTART:
//                    if (!altMode) {
//                        if (DialogMaster.confirm("Select anew?")) {
//                            FAST_DC.getLauncher().selectiveInit();
//                        }
//                    }

                    game.getManager().getDeathMaster().killAllUnits(true, false, quiet);
                    game.getBattleMaster().getSpawner().spawnCustomParty(true);
                    game.getBattleMaster().getSpawner().spawnCustomParty(false);
                    game.getManager().refreshAll();
                    WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
                    return func;
                case CLEAR:
                    boolean respawn = isAltMode();
                    game.getManager().getDeathMaster().killAllUnits(!isAltMode());
                    if (respawn) {
                        // /respawn!
                        game.getBattleMaster().getSpawner().spawnCustomParty(true);
                        game.getBattleMaster().getSpawner().spawnCustomParty(false);
                    }
                    game.getManager().refreshAll();

                    break;
                case KILL_ALL_UNITS:
                    game.getManager().getDeathMaster().killAll(isAltMode());
                    break;

                case ACTIVATE_UNIT:
                    if (isAltMode()) {
                        getObj().modifyParameter(PARAMS.C_N_OF_ACTIONS, 100);
                    }
                    if (getObj().isMine()) {
                        game.getManager().setActivatingAction(null);
                        game.getManager().activeSelect(getObj());
                    } else {
                        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_COMPLETE, true);
                        WaitMaster.WAIT(1234);
                        getObj().modifyParameter(PARAMS.C_N_OF_ACTIONS, 100);
                    }

                    game.getVisionMaster().refresh();
                    break;

                case ADD_ITEM:
                    if (isAltMode()) {
                        TYPE = DC_TYPE.WEAPONS;
                    } else {
                        TYPE = (DC_TYPE) DialogMaster.getChosenOption("Choose item type...",
                         DC_TYPE.WEAPONS, DC_TYPE.ARMOR, DC_TYPE.ITEMS, DC_TYPE.JEWELRY);
                    }
                    if (isAltMode()) {
                        if (!selectWeaponType()) {
                            break;
                        }
                    } else if (!selectType(TYPE)) {
                        break;
                    }

                    if (!selectTarget(ref)) {
                        selectedTarget = infoObj;
                    }
                    if (selectedTarget == null) {
                        break;
                    }
                    boolean quick = false;
                    if (isAltMode()) {
                        quick = false;
                    } else if (TYPE == DC_TYPE.ITEMS) {
                        quick = true;
                    } else if (TYPE == DC_TYPE.WEAPONS) {
                        quick = DialogMaster.confirm("quick slot item?");
                    }
                    DC_HeroItemObj item = ItemFactory.createItemObj(selectedType, selectedTarget.getOwner(),
                     game, ref, quick);
                    if (!quick) {
                        if (TYPE != DC_TYPE.JEWELRY) {
                            selectedTarget.equip(item, TYPE == DC_TYPE.ARMOR ? ItemEnums.ITEM_SLOT.ARMOR
                             : ItemEnums.ITEM_SLOT.MAIN_HAND);
                        }
                    } else {
                        selectedTarget.addQuickItem((DC_QuickItemObj) item);
                    }

                    // selectedTarget.addItemToInventory(item);

                    break;
                case ADD_SPELL:
                    if (!selectType(DC_TYPE.SPELLS)) {
                        break;
                    }
                    if (!selectTarget(ref)) {
                        selectedTarget = infoObj;
                    }
                    if (selectedTarget == null) {
                        break;
                    }
                    TestMasterContent.setTEST_LIST(TestMasterContent.getTEST_LIST()
                     + selectedType.getName() + ";");

                    selectedTarget.getSpells().add(
                     new DC_SpellObj(selectedType, selectedTarget.getOwner(), game, selectedTarget.getRef()));
                    break;
                case ADD_SKILL:
                case ADD_ACTIVE:
                    PROPERTY prop = G_PROPS.ACTIVES;
                    DC_TYPE T = DC_TYPE.ACTIONS;
                    if (func == DEBUG_FUNCTIONS.ADD_SKILL) {
                        prop = PROPS.SKILLS;
                        T = DC_TYPE.SKILLS;
                    }
                    String type = ListChooser.chooseType(T);
                    if (type == null) {
                        break;
                    }

                    if (!new SelectiveTargeting(new Conditions(ConditionMaster
                     .getTYPECondition(C_OBJ_TYPE.BF_OBJ))).select(ref)) {
                        break;
                    }
                    lastType = type;
                    new AddBuffEffect(type + " hack", new ModifyPropertyEffect(prop, MOD_PROP_TYPE.ADD,
                     type), new Formula("1")).apply(ref);
                    if (func == DEBUG_FUNCTIONS.ADD_ACTIVE) {
                        infoObj.getActives().add(game.getActionManager().getAction(type, infoObj));
                        game.getActionManager().constructActionMaps(infoObj);
                    }

                    // game.getManager().reset();
                    // instead of toBase()
                    break;
                case ADD_PASSIVE:
                    // same method
                    infoObj.getPassives().add(
                     AbilityConstructor.getPassive(ListChooser.chooseType(DC_TYPE.ABILS),
                      infoObj));
                    infoObj.activatePassives();
                    break;
                case CHANGE_OWNER:
                    // if already has, make permanent
                    new AddBuffEffect("ownership hack", new OwnershipChangeEffect(), new Formula("1"))
                     .apply(ref);

                    break;

                case END_TURN:
                    game.getManager().setActivatingAction(null);
                    WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT, null);
                    return func;
                case KILL_UNIT:
                    if (arg != null) {
                        arg.kill(infoObj, !isAltMode(), isAltMode());
                    } else {
                        infoObj.kill(infoObj, !isAltMode(), isAltMode());
                    }
                    // game.getManager().killUnitQuietly((DC_UnitObj)
                    // game.getManager()
                    // .getInfoObj());
                    break;

                case ADD_CHAR:
                    summon(true, DC_TYPE.CHARS, ref);
                    break;
                case ADD_OBJ:
                    summon(null, DC_TYPE.BF_OBJ, new Ref(game));
                    break;
                case ADD_UNIT:
                    summon(true, DC_TYPE.UNITS, ref);
                    break;
                case SET_WAVE_POWER:
                    Integer forcedPower;
                    forcedPower = DialogMaster.inputInt();
                    if (forcedPower < 0) {
                        forcedPower = null;
                    }
                    ArenaBattleMaster a = (ArenaBattleMaster) game.getBattleMaster();
                    a.getWaveAssembler().setForcedPower(forcedPower);
                    break;
                case SPAWN_CUSTOM_WAVE:
                    coordinate = getGame().getBattleFieldManager().pickCoordinate();
                    ObjType waveType = ListChooser.chooseType_(DC_TYPE.ENCOUNTERS);
                    Wave wave = new Wave(coordinate, waveType, game, ref, game.getPlayer(!isAltMode()));

                    String value = new ListChooser(SELECTION_MODE.MULTIPLE, StringMaster
                     .openContainer(wave.getProperty(PROPS.UNIT_TYPES)), DC_TYPE.UNITS)
                     .choose();
                    wave.setProperty(PROPS.UNIT_TYPES, value);
                    // PROPS.EXTENDED_PRESET_GROUP
                    break;
                case SPAWN_PARTY:

                    coordinate = getGame().getBattleFieldManager().pickCoordinate();
                    ObjType party = ListChooser.chooseType_(DC_TYPE.PARTY);
                    game.getBattleMaster().getSpawner().spawnCustomParty(coordinate, null, party);

                    break;
                case SPAWN_WAVE:
                    if (!isAltMode()) {
                        coordinate = getGame().getBattleFieldManager().pickCoordinate();
                    } else {
//                        FACING_DIRECTION side = new EnumChooser<FACING_DIRECTION>()
//                                .choose(FACING_DIRECTION.class);
                        // if (side== FACING_DIRECTION.NONE)
//                        game.getBattleMaster().getSpawner().getPositioner().setForcedSide(side);
                    }
                    String typeName = ListChooser.chooseType(DC_TYPE.ENCOUNTERS);
                    if (typeName == null) {
                        return func;
                    }
                    try {
                        game.getBattleMaster().getSpawner().spawnWave(typeName,
                         game.getPlayer(ALT_AI_PLAYER), coordinate);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    } finally {
//                        game.getBattleMaster().getSpawner().getPositioner().setForcedSide(null);
                    }
                    game.getManager().refreshAll();
                    break;
                case ADD_ENEMY_UNIT:
                    summon(false, DC_TYPE.UNITS, new Ref(game));
                    // ref = new Ref(game
                    // // , game.getManager().getActiveObj().getId()
                    // );
                    // ref.setPlayer(game.getPlayer(false));
                    // typeName = ListChooser.chooseType(OBJ_TYPES.UNITS);
                    // if (StringMaster.isEmpty(typeName))
                    // break;
                    // new SelectiveTargeting(new Conditions(
                    // ConditionMaster.getTYPECondition(OBJ_TYPES.TERRAIN)))
                    // .select(ref);
                    // effect = new SummonEffect(typeName);
                    // effect.apply(ref);
                    // effect.getUnit().setOwner(game.getPlayer(false));
                    // game.getManager().refreshAll();
                    break;

                case TOGGLE_ALT_AI: {
                    game.getPlayer(true).setAi(!game.getPlayer(true).isAi());
                    ALT_AI_PLAYER = !ALT_AI_PLAYER;
                    break;
                }
                case TOGGLE_DEBUG: {
                    game.setDebugMode(!game.isDebugMode());
                    break;
                }
                case WAITER_INPUT: {
                    String input = DialogMaster.inputText("operation");
                    WAIT_OPERATIONS operation = new EnumMaster<WAIT_OPERATIONS>().retrieveEnumConst(
                     WAIT_OPERATIONS.class, input);
                    if (operation == null) {
                        operation = new EnumMaster<WAIT_OPERATIONS>().retrieveEnumConst(
                         WAIT_OPERATIONS.class, input, true);
                    }
                    if (operation == null) {
                        DialogMaster.error("no such operation");
                        return func;
                    }
                    input = DialogMaster.inputText("input");
                    WaitMaster.receiveInput(operation, input);
                }
                case REMOVE_HACKS:
                    break;
//                case CLEAR_WAVES:
//                    game.getBattleMaster().getSpawner().clear();
//                    game.getBattleMaster().getBattleConstructor().setIndex(0);
//                    break;
//                case SCHEDULE_WAVES:
//                    game.getBattleMaster().getBattleConstructor().setIndex(0);
//                    game.getBattleMaster().getBattleConstructor().construct();
//                    break;
                case TOGGLE_LIGHTING:
                    break;
                case TOGGLE_FOG:
                    break;
                case GUI_EVENT:
                    EmitterController.getInstance();
                    String string = ListChooser.chooseEnum(GuiEventType.class);
                    GuiEventManager.trigger(
                     new EnumMaster<GuiEventType>().
                      retrieveEnumConst(GuiEventType.class,
                       string), null);
                    break;
                case SFX_PLAY_LAST:
                    EmitterController.getInstance();
                    GuiEventManager.trigger(GuiEventType.SFX_PLAY_LAST, null);
                    break;
                case SFX_ADD:
                    EmitterController.getInstance();
                    GuiEventManager.trigger(GuiEventType.CREATE_EMITTER, null);
                    break;
                case SFX_ADD_RANDOM:
                    EmitterController.getInstance().getInstance();
                    GuiEventManager.trigger(GuiEventType.CREATE_EMITTER, true);
                    break;
                case SFX_MODIFY:
                    EmitterController.getInstance().modify();
                    break;
                case SFX_SET:
                    EmitterController.getInstance().setForActive();
                    break;
                case SFX_SAVE:
                    EmitterController.getInstance().save();
                    break;
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            debugFunctionRunning = false;
        }
        if (isResetRequired(func))
            reset();

        if (transmitted) {
//            String transmittedData = lastType + StringMaster.NET_DATA_SEPARATOR + infoObj
//                    + StringMaster.NET_DATA_SEPARATOR + data + StringMaster.NET_DATA_SEPARATOR
//                    + ref;
//            game.getCommunicator().transmitDebugFunction(func, transmittedData);
        }
        return func;

    }

    private boolean isResetRequired(DEBUG_FUNCTIONS func) {
        switch (func) {
            case PAUSE:
                return false;
        }
        return true;
    }

    private boolean selectWeaponType() {
        MATERIAL material = ItemEnums.MATERIAL.STEEL;
        // if (DialogMaster.confirm("Select material?"))
        QUALITY_LEVEL quality = ItemEnums.QUALITY_LEVEL.NORMAL;
        // if (DialogMaster.confirm("Select material?"))
        selectedType = DataManager.getItem(quality, material, ListChooser
         .chooseType(ItemGenerator.getBaseTypes(DC_TYPE.WEAPONS)));
        return selectedType != null;
    }

    private void summon(Boolean me, DC_TYPE units, Ref ref) {

        Player player = Player.NEUTRAL;
        if (me != null) {
            player = game.getPlayer(me);
            if (!me) {
                if (ALT_AI_PLAYER) {
                    if (altAiPlayer == null) {
                        altAiPlayer = new DC_Player("", null, false);
                    }
                    player = altAiPlayer;
                }
            }
        }
        /*
         * alt mode: >> random >> preset >> last
		 */
        ref.setPlayer(player);
        String typeName;
        if (arg instanceof Unit) {
            Obj obj = arg;
            typeName = (obj.getType().getName());
        }

        // new ListChooser(mode, listData, TYPE)

        if (altMode) {
            typeName = lastType;
            // RandomWizard.getRandomType(units).getName();
        } else {
            typeName = ListChooser.chooseType(units);
        }
        if (!DataManager.isTypeName(typeName)) {
            typeName = DialogMaster.inputText("Then enter it yourself...");
        }
        if (typeName == null) {
            return;
        }
        if (!DataManager.isTypeName(typeName)) {
            ObjType foundType = DataManager.findType(typeName, units);
            if (foundType == null) {
                return;
            }
            typeName = foundType.getName();

        }
        if (arg instanceof Obj) {

            Obj obj = arg;
            ref.setTarget(game.getCellByCoordinate(obj.getCoordinates()).getId());
        } else if (!new SelectiveTargeting(new Conditions(ConditionMaster
         .getTYPECondition(DC_TYPE.TERRAIN))).select(ref)) {
            return;
        }
        lastType = typeName;
        SummonEffect effect = (me == null) ? new CreateObjectEffect(typeName, true)
         : new SummonEffect(typeName);
        if (units == DC_TYPE.UNITS)

        {
            if (checkAddXp()) {
                Formula xp = new Formula(""
                 + (DC_Formulas.getTotalXpForLevel(DataManager.getType(typeName,
                 DC_TYPE.UNITS).getIntParam(PARAMS.LEVEL)
                 + DialogMaster.inputInt()) - DC_Formulas
                 .getTotalXpForLevel(DataManager.getType(typeName,
                  DC_TYPE.UNITS).getIntParam(PARAMS.LEVEL))));
                effect = new SummonEffect(typeName, xp);
            }
        }

        effect.setOwner(player);
        effect.apply(ref);

        if (player.isAi()) {
            game.getAiManager().getCustomUnitGroup((Unit) effect.getUnit()).add(effect.getUnit());
        }
        game.getManager().refreshAll();
    }

    private boolean checkAddXp() {
        return false;
        // if (alt)
        // return DialogMaster.confirm("Leveled?");
    }

    private void reset() {
        game.getManager().reset();
        game.getManager().refreshAll();
    }

    private boolean selectTarget(Ref ref) {
        if (!new SelectiveTargeting(new Conditions(ConditionMaster
         .getTYPECondition(C_OBJ_TYPE.BF_OBJ))).select(ref)) {
            return false;
        }

        selectedTarget = (Unit) ref.getTargetObj();
        return true;
    }

    private boolean selectType(OBJ_TYPE TYPE) {
        if (isAltMode()) {
            String name = DialogMaster.inputText("Type name...");
            if (name == null) {
                return false;
            }
            selectedType = DataManager.getType(name, TYPE);
            if (selectedType != null) {
                return true;
            }
            selectedType = DataManager.findType(name, TYPE);
            if (selectedType != null) {
                return true;
            }
        }
        String type = ListChooser.chooseType(TYPE);
        if (type == null)

        {
            return false;
        }

        selectedType = DataManager.getType(type, TYPE);
        return true;
    }

    private Obj getObj() {
        if (game.isSimulation()) {
            return CharacterCreator.getHero();
        }
        return game.getManager().getActiveObj();
    }

    public void executeDebugFunctionNewThread(final HIDDEN_DEBUG_FUNCTIONS func) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeHiddenDebugFunction(func);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                } finally {
                    cleanUp();
                }
            }

        }).start();
    }

    public void cleanUp() {
        DebugMaster.setAltMode(false);
        setArg(null);
    }

    public void executeDebugFunctionNewThread(final DEBUG_FUNCTIONS func) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeDebugFunction(func);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                } finally {
                    cleanUp(); // TODO refactor alt click!!!
                }
            }
        }).start();

    }

    public Object executeHiddenDebugFunction(HIDDEN_DEBUG_FUNCTIONS func) {
        executedFunctions.push(func.toString());

        Unit infoObj;
        try {
            infoObj = (Unit) getObj();
        } catch (Exception e) {
            infoObj = game.getManager().getActiveObj();
        }
        switch (func) {
            case WRITE_GROUP:
                Entity entity = DC_Game.game.getValueHelper().getEntity();
                if (entity == null) {
                    break;
                }
                XML_Writer
                 .writeXML_ForTypeGroup(entity.getOBJ_TYPE_ENUM(), entity.getGroupingKey());

            case WRITE:
                if (!(DC_Game.game.getValueHelper().getEntity() instanceof ObjType)) {
                    break;
                }
                XML_Writer.writeXML_ForTypeGroup(DC_Game.game.getValueHelper().getEntity()
                 .getOBJ_TYPE_ENUM());
                break;
            case WRITE_TYPE:
                if (!(DC_Game.game.getValueHelper().getEntity() instanceof ObjType)) {
                    break;
                }
                XML_Writer.writeXML_ForType((ObjType) DC_Game.game.getValueHelper().getEntity());
                break;
            case TOGGLE_AV_MODE:
                CoreEngine.setArcaneVault(!CoreEngine.isArcaneVault());
                CoreEngine.setArcaneVaultMode(true);
                break;

            case RECONSTRUCT: {
                for (ActiveObj obj : infoObj.getPassives()) {
                    obj.setConstructed(false);
                }
                for (ActiveObj obj : infoObj.getActives()) {
                    obj.setConstructed(false);
                }
                break;
            }
            case RELOAD_TYPES: {
                XML_Reader.readTypes(false, true);
                game.initObjTypes();
                break;
            }
            case RESTART_GAME:
//            TODO     executeDebugFunction(KILL_ALL_UNITS);
//                DC_ObjInitializer.processUnitDataString(game.getPlayer(true),
//                        game.getPlayerParty(), game);
                game.getManager().unitActionCompleted(null, false);
                break;
            case BF_RESURRECT_ALL:
                break;
            case DISPLAY_EVENT_LOG:
                break;
            case DISPLAY_REF:
                display(game.getManager().getActiveObj().getName() + "'s REF:", game.getManager()
                 .getActiveObj().getRef()
                 + "");
                break;
            case DISPLAY_TRIGGERS:
                // display("Triggers: ", game.getState().getAttachedTriggers());
                displayList("Triggers: ", game.getState().getTriggers(), 1);
                break;
            case DISPLAY_STATE:
                display("State: ", game.getState());
                break;
            case DISPLAY_EFFECTS:
                displayList("Effects ", game.getState().getEffects(), 1);
                break;
            case DISPLAY_OBJECTS:
                for (OBJ_TYPE sub : game.getState().getGame().getState().getObjMaps().keySet()) {
                    displayList(sub + ": ", game.getState().getGame().getState().getObjMaps().get(
                     sub).keySet(), 1);
                }

            case DISPLAY_UNITS:
                displayList("Units ", game.getState().getGame().getUnits(), 1);

                break;
            case DISPLAY_UNIT_INFO:
                display("INFO OBJ: ", getUnitInfo(game.getManager().getActiveObj()));

                break;
            case HERO_ADD_ALL_SPELLS:

                for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
                    Unit hero = (Unit) game.getManager().getMainHero();
                    if (LibraryManager.checkHeroHasSpell(hero, type)) {
                        continue;
                    }
                    LibraryManager.addVerbatimSpell(hero, type);
                    DC_SpellObj spell = new DC_SpellObj(type, hero.getOriginalOwner(), hero
                     .getGame(), hero.getRef());
                    hero.getSpells().add(spell);
                }
                break;
            default:
                break;

        }
        return null;
    }

    private void displayList(String string, Collection<?> list, int chunkSize) {
        LogMaster.log(1, string + " list (" + list.size() + ")");

        int i = 0;
        String chunk = ">> ";
        for (Object o : list) {
            if (i >= chunkSize) {
                LogMaster.log(1, chunk);
                i = 0;
                chunk = ">> ";

            }
            chunk += o.toString() + " <|> ";
            i++;

        }
        LogMaster.log(1, chunk);

        LogMaster.log(1, string + " list (" + list.size() + ")");
    }

    private String getUnitInfo(DC_Obj infoObj) {
        String str = "Unit info: \n";
        for (PARAMETER param : PARAMS.values()) {
            if (!param.isDynamic()) {
                continue;
            }
            str += param.toString();
            str += " = ";
            str += infoObj.getValue(param);
            str += "\n";
        }
        for (PARAMETER param : PARAMS.values()) {
            if (!param.isAttribute()) {
                continue;
            }
            str += param.toString();
            str += " = ";
            str += infoObj.getValue(param);
            str += "\n";
        }
        for (PARAMETER param : PARAMS.values()) {
            if (param.isDynamic() || param.isAttribute()) {
                continue;
            }
            str += param.toString();
            str += " = ";
            str += infoObj.getValue(param);
            str += "\n";
        }
        for (PROPERTY p : ContentManager.getPropList()) {
            if (!(ContentManager.isValueForOBJ_TYPE(DC_TYPE.CHARS, p) || ContentManager
             .isValueForOBJ_TYPE(DC_TYPE.UNITS, p))) {
                continue;
            }
            str += p.toString();
            str += " = ";
            str += infoObj.getValue(p);
            str += "\n";
        }
        return str;
    }

    private void display(String str, Object obj) {
        LogMaster.log(1, "" + str + obj.toString());

    }

    public Object typeInFunction() {
        String funcName = JOptionPane.showInputDialog("Type in function to execute");
        HIDDEN_DEBUG_FUNCTIONS func = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>().retrieveEnumConst(
         HIDDEN_DEBUG_FUNCTIONS.class, funcName);
        if (func != null) {
            return executeHiddenDebugFunction(func);
        } else {
            DEBUG_FUNCTIONS func1 = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(
             DEBUG_FUNCTIONS.class, funcName);
            if (func1 != null) {
                return executeDebugFunction(func1);
            }
        }
        return null;
    }


    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public DC_GameState getState() {
        return state;
    }

    public void setState(DC_GameState state) {
        this.state = state;
    }

    public DebugPanel getDebugPanel() {
        return debugPanel;
    }

    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
    }


    public Obj getArg() {
        return arg;
    }

    public void setArg(Obj arg) {
        this.arg = arg;
    }

    public void debugModeToggled(boolean debugMode) {
        if (debugMode) {
//            main.system.auxiliary.log.LogMaster.log(1," " +
//             game.getLogManager().getCombatActionLogBuilder().toString());
            SpecialLogger.getInstance().logCombatLog();
        }
        VisionManager.setVisionHacked(debugMode);
    }

    public enum AI_PARAM {
        LOG_LEVEL
    }

    public enum DEBUG_FUNCTIONS {
        // GAME
        SAVE,
        LOAD,
        //
        SET_GLOBAL_ILLUMINATION,
        SET_GLOBAL_CONCEALMENT,
        TEST_CLONE_STATE,
        TEST_LOAD_STATE,
        END_TURN(true),
        // UNIT
        ADD_UNIT(true),
        ADD_ENEMY_UNIT(true),
        ADD_GROUP(true),
        KILL_UNIT(true),
        RESTART,
        CLEAR(true),
        SPAWN_WAVE,

        PAUSE,
        ADD_SKILL(true),
        ADD_ACTIVE(true),
        ADD_SPELL(true),
        ADD_CHAR(true),
        ADD_OBJ(true),
        ADD_ITEM(true),
        // MISC

        ACTIVATE_UNIT(true),
        TOGGLE_DUMMY(true),
        TOGGLE_DUMMY_PLUS(true),
        TOGGLE_OMNIVISION(true),
        TOGGLE_DEBUG,

        PRESET,
        KILL_ALL_UNITS,
        SPAWN_PARTY(true),
        SPAWN_CUSTOM_WAVE(true),
        CLEAR_WAVES,

        SET_OPTION,
        EDIT_AI,

        SET_WAVE_POWER,
        WAITER_INPUT,
        SCHEDULE_WAVES,

        AUTO_COMBAT,
        TOGGLE_ALT_AI,
        TOGGLE_AUTO_UNIT,
        TOGGLE_GRAPHICS_TEST,
        TOGGLE_FREE_ACTIONS,
        TOGGLE_QUIET,
        HIDDEN_FUNCTION,
        ADD_PASSIVE(true),
        GOD_MODE(true),
        CHANGE_OWNER(true),
        ADD_TEST_SPELLS(true),
        ADD_ALL_SPELLS(true),
        TOGGLE_LOG,
        REMOVE_HACKS,
        TOGGLE_DUNGEON_DEBUG,

        AUTO_TEST_INPUT,
        RUN_AUTO_TESTS,


        TOGGLE_LIGHTING,
        TOGGLE_FOG, SFX_ADD,
        SFX_ADD_RANDOM,
        SFX_MODIFY,
        SFX_SAVE,
        SFX_PLAY_LAST, SFX_SET, GUI_EVENT;

        boolean transmitted;

        DEBUG_FUNCTIONS() {

        }

        DEBUG_FUNCTIONS(boolean transmitted) {
            this.transmitted = transmitted;
        }
    }

    public enum HIDDEN_DEBUG_FUNCTIONS {
        HERO_ADD_ALL_SPELLS,
        BF_RESURRECT_ALL,
        DISPLAY_TRIGGERS,
        DISPLAY_EFFECTS,
        DISPLAY_EVENT_LOG,
        DISPLAY_LOG,
        DISPLAY_STATE,
        DISPLAY_OBJECTS,
        DISPLAY_UNITS,
        DISPLAY_REF,
        DISPLAY_UNIT_INFO,
        RECONSTRUCT,
        RELOAD_TYPES,
        RESTART_GAME,

        TOGGLE_AV_MODE,
        AV_MODE_WRITE,
        WRITE_TYPE,
        WRITE_GROUP,
        WRITE,

    }

    public enum SIMULATION_FUNCTIONS {
        REMAP,

    }

}
