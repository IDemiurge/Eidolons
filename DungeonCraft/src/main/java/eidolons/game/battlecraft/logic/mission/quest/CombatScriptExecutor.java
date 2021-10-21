package eidolons.game.battlecraft.logic.mission.quest;

import eidolons.system.libgdx.wrapper.VectorGdx;
import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner.SPAWN_MODE;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitGroupMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData.PARTY_VALUE;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptGenerator;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.game.battlecraft.logic.mission.universal.ScriptManager;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.text.Texts;
import eidolons.system.text.tips.TipMessageMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.elements.triggers.Trigger;
import main.entity.DataModel;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

/**
 * Created by JustMe on 5/8/2017.
 */
public class CombatScriptExecutor extends ScriptManager<QuestMission, COMBAT_SCRIPT_FUNCTION> {

    List<Trigger> scriptTriggers = new ArrayList<>();

    public CombatScriptExecutor(MissionMaster master) {
        super(master);

    }

    private ScriptExecutor<COMBAT_SCRIPT_FUNCTION> getScriptExecutor() {
        return master.getGame().getAiManager().getScriptExecutor();
    }

    public void checkTriggers(Event e) {
        scriptTriggers.forEach(trigger -> trigger.check(e));

        //getVar ref from where? {vars} - global?
        //GlobalRef - {party}, {main_hero}, {party_group}, ...
    }

    public void init() {
        createMissionTriggers();
    }

    public void createMissionTriggers() {
        String scripts = getMission().getFloor().getProperty(PROPS.FLOOR_SCRIPTS);
        if (!scripts.isEmpty())
            scripts += ScriptSyntax.SCRIPTS_SEPARATOR;
        try {
            scripts += readScriptsFile();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        parseScripts((scripts));
    }

    @Override
    public String readScriptsFile() {
        String text = FileManager.readFile(
                getScriptsPath());
        //        text = StringMaster.getLastPart(text, ScriptSyntax.COMMENT_CLOSE);
        String[] parts = text.split(ScriptSyntax.COMMENT_CLOSE);
        if (parts.length == 1)
            return "";
        return parts[1];
    }

    private String getScriptsPath() {
        String name = getMission().getFloor().getName();
        // if (EidolonsGame.DUEL) {
        //     name = "duel";
        // }
        return PathUtils.buildPath(PathFinder.getTextPath(),
                "scripts", "demo", name + " " + ScriptGenerator.SCRIPTS_FILE_NAME);
    }
    //getMaster().getMetaMaster().getMetaGame().getScenario()

    @Override
    public QuestMissionMaster getMaster() {
        return (QuestMissionMaster) super.getMaster();
    }


    protected Class<COMBAT_SCRIPT_FUNCTION> getFunctionClass() {
        return COMBAT_SCRIPT_FUNCTION.class;
    }


    @Override
    public boolean execute(COMBAT_SCRIPT_FUNCTION function, Ref ref, Object... objects) {
        String[] args = new String[0];
        if (!isAbstractArgs(function)) {
            args = new String[objects.length];
            int i = 0;
            for (Object object : objects) {
                args[i++] = object.toString();

            }
        }

        switch (function) {
            case AWAKEN:
                getMaster().getDungeonMaster().getAwakener().awaken(ref, objects);
                return true;
            case CINEMATIC:
                return doCinematicScript(ref, args);
            case ESOTERICA:
                return doEsoterica(ref, args);
            case AGGRO:
                return doAggro(ref, args);
            case SPAWN:
                return doSpawn(ref, args);
            case DIALOGUE_TIP:
                doDialogue(ref, args[0]);
                String[] finalArgs = args;
                DialogueManager.afterDialogue(() ->
                        doCustomTip(ref, finalArgs.length == 1 ? finalArgs[0] : finalArgs[1]));
                return true;
            case DIALOGUE:
                return doDialogue(ref, args);
            case TIP_MSG:
                return doTipAndMsg(ref, args);

            case TIP:
                return doCustomTip(ref, args);
            case MESSAGE:
                return doMessage(ref, args);
            case TIP_QUEST:
                return doQuest(true, ref, args);
            case QUEST:
                return doQuest(false, ref, args);
            case SCRIPT_IF:
                args[0] = args[1] + StringMaster.wrapInParenthesis(args[0]);
                getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().
                        execute(SpeechScript.SCRIPT.SCRIPT_IF,
                                args[0]);
                return true;
            case SCRIPT_PARALLEL:
                getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().
                        execute(SpeechScript.SCRIPT.SCRIPT_PARALLEL,
                                args[0]);
                return true;
            case SCRIPT:
                getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().
                        execute(SpeechScript.SCRIPT.SCRIPT,
                                args[0]);
                return true;
            //                return doScript(ref, args);

            case REPOSITION:
                return doReposition(ref, args);

            case COMMENT:
                return doUnitOperation(function, ref, args);
            case MOVE_TO:
            case TURN_TO:
            case ACTION:
            case ATTACK:
            case FREEZE:
            case UNFREEZE:
            case ORDER:
                return getScriptExecutor().execute(function, ref, objects);
        }

        return doUnitOperation(function, ref, args);
    }

    private boolean isAbstractArgs(COMBAT_SCRIPT_FUNCTION function) {
        switch (function) {
            case MOVE_TO:
            case TURN_TO:
            case ACTION:
            case ATTACK:
            case FREEZE:
            case UNFREEZE:
            case ORDER:
                return true;
        }
        return false;
    }

    private boolean doCinematicScript(Ref ref, String[] args) {
        //TODO enter cinematic mode?
        if (args.length > 1) {
            getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(args[0], args[1]);
        } else
            getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(args[0]);
        return true;
    }

    public static final String ESOTERICA_QUEST = "Secret Scripture";
    public static String getEsotericaKey(int req) {
        int n= RandomWizard.getRandomIntBetween(1,3, true);
        //        if (req<=1) {
        //            n=1;
        //        }
        return "Esoterica comment_"+n;
    }
    private boolean doEsoterica(Ref ref, String[] args) {

        int n = getGame().getMetaMaster().getQuestMaster().getQuest(ESOTERICA_QUEST).getNumberAchieved();
        int req = Integer.parseInt(args[1]);
        if (req > n) {
            String text = getEsotericaKey(req);
            doComment(Core.getMainHero(), text);
            return false;
        }
        String key = args[0];
        doCustomTip(ref, key); //multiple?
        //reward?


        return true;
    }

    private boolean doAggro(Ref ref, String[] args) {
        float dst = 6f;
        boolean hasAggro = false;
        for (String arg : args) {
            if (NumberUtils.isNumber(arg, false)) {
                dst = NumberUtils.getFloat(arg);
            }
            BattleFieldObject a = getGame().getObjMaster().getByName(arg, ref);
            if (a != null) {
                AggroMaster.aggro((Unit) a, Core.getMainHero());
                hasAggro = true;
            }
        }
        if (!hasAggro)
            for (Unit unit : Core.getGame().getUnits()) {
                if (Core.getPlayerCoordinates().dst(unit.getCoordinates()) < dst) {
                    AggroMaster.aggro(unit, Core.getMainHero());
                    hasAggro = true;
                }
            }
        if (hasAggro) {
            Core.getGame().getDungeonMaster().getExplorationMaster().switchExplorationMode(false);
        } else {

        }

        return true;
    }

    private boolean doTipAndMsg(Ref ref, String[] args) {
        TipMessageMaster.tip(args);
        String msg = args[0];
        EUtils.onConfirm(msg, false, () -> {
        });
        WaitMaster.waitForInput(MESSAGE_RESPONSE);
        return true;
    }

    private boolean doCustomTip(Ref ref, String... args) {
        TipMessageMaster.tip(args);
        return true;
    }

    private boolean doMessage(Ref ref, String... args) {
        String msg = args[0];
        EUtils.onConfirm(msg, false, () -> {
        });
        return true;
    }

    private boolean doQuest(boolean tip, Ref ref, String[] args) {
        if (!ExplorationMaster.isExplorationOn()) {
            return false;
        }
        if (args.length == 1) {
            getMaster().getMetaMaster().getQuestMaster().questTaken(args[0], true);
            return true;
        }
        if (tip) {
            try {
                doCustomTip(ref, args[0]);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            doQuest(false, ref, new String[]{args[1]});
            return true;
        }

        String msg = args[0];
        String questName = args[1];

        EUtils.onConfirm(msg, false, () -> {
            getMaster().getMetaMaster().getQuestMaster().questTaken(questName, true);
        });

        return true;
    }


    //moves all party members to new positions around given origin
    private boolean doReposition(Ref ref, String[] args) {
        //        GuiEventManager.trigger(GuiEventType.SHADOW_MAP_FADE_IN, 100);
        //        String group = args[i];
        //        i++;
        Set<Unit> members = game.getPlayer(true).collectControlledUnits_();
        List<Coordinates> coordinates =
                getCoordinatesListForUnits(args[0], getPlayerManager().getPlayer(true),
                        members.stream().map(DataModel::getName).collect(Collectors.toList()), ref);
        Iterator<Coordinates> i = coordinates.iterator();
        for (Unit unit : members) {
            if (i.hasNext()) {
                unit.setCoordinates(i.next());
            } else {
                unit.setCoordinates((Coordinates) RandomWizard.getRandomListObject(coordinates));
            }
            game.getMovementManager().moved(unit, false);
        }
        for (Unit unit : members) {
            GuiEventManager.trigger(GuiEventType.UNIT_MOVED, unit);
        }
        //        GuiEventManager.trigger(GuiEventType.SHADOW_MAP_FADE_OUT,  0);
        return true;
    }

    public static boolean doComment(Unit unit, String key) {
        return doComment(unit, key, null);
    }

    public static boolean doComment(String name, String img, Coordinates c, String key) {
        String text = Texts.getComments().get(key);
        if (text == null) {
            text = key;
        }
        //        GuiEventManager.trigger(GuiEventType.SHOW_COMMENT_PORTRAIT, img, text, c);

        text = GdxAdapter.getInstance().getEventsAdapter().comment(img, text, c);
        Core.getGame().getLogManager().log(name + " :" +  (text));
        return true;
    }

    public static boolean doComment(Unit unit, String key, VectorGdx at) {
        //        GuiEventManager.trigger(GuiEventType.SHOW_COMMENT_PORTRAIT, unit, key, at);
         GdxAdapter.getInstance().getEventsAdapter().comment_(unit, key, at);
        return true;
    }

    private boolean doDialogue(Ref ref, String... args) {
        GuiEventManager.trigger(GuiEventType.INIT_DIALOG, args[0]);
        return true;
    }


    @Override
    public String getSeparator(COMBAT_SCRIPT_FUNCTION func) {
        if (func == COMBAT_SCRIPT_FUNCTION.SCRIPT) {
            return ScriptSyntax.SCRIPTS_SEPARATOR_ALT;
        }
        return ScriptSyntax.SCRIPT_ARGS_SEPARATOR;
    }

    private boolean doScript(Ref ref, String[] args) {
        parseScripts(args[0]);
        return true;
    }

    //CREATE TRIGGERS
    // maybe separate from normal ones?

    //event
    //conditions
    //action

    //script entity? with vars? SpawnOnAt(bandits, 2, 5-5)
        /*
        variables in syntax, e.g. spawn 2 cells away from main hero?
        check out wc3 system!
         */

    private boolean doKill(Entity entity) {
        entity.kill(entity, true, false);
        //        entity.kill(killer, !annihilate, quiet);
        return true;
    }

    private boolean doRemove(Entity entity) {
        entity.kill(entity, false, true);
        return true;
    }

    private boolean doUnitOperation(COMBAT_SCRIPT_FUNCTION function, Ref ref, String[] args) {
        //        int i = 0;
        String name = args.length == 0 ? "source" : args[0];
        Unit unit = (Unit) ref.getObj(name);
        if (unit == null) {
            Boolean power = null;
            Boolean distance = true;
            Boolean ownership = null;
            try {
                unit = (Unit) ((DC_Game) ref.getGame()).getObjMaster().getByName(name, ref,
                        ownership, distance, power);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (unit == null) {
            unit = Core.getMainHero();
        }


        switch (function) {
            case COMMENT:
                return doComment(unit, args[0]);
            case REMOVE:
                return doRemove(unit);
            case KILL:
                return doKill(unit);
        }

        return false;
    }


    private boolean doSpawn(Ref ref, String[] args) {
        int i = 0;
        DC_Player player = getPlayerManager().getPlayer(args[i]);
        if (player == null)
            player = getPlayerManager().getPlayer(false);
        else
            i++;
        List<String> units = new ArrayList<>();
        //        if (args[i].contains(ScriptSyntax.SPAWN_ARG_UNITS_WAVE))
        String unitString = args[i];
        int level = NumberUtils.getIntParse(VariableManager.getVars(unitString));
        unitString = VariableManager.removeVarPart(unitString);
        ObjType wave = DataManager.getType(unitString, DC_TYPE.ENCOUNTERS);
        if (wave != null) {
            for (String sub : ContainerUtils.open(
                    wave.getProperty(PROPS.PRESET_GROUP))) {
                if (level > 0)
                    units.add(UnitLevelManager.getLeveledTypeName(level, sub));
                else
                    units.add(sub);
            }
        }

        //TODO adjust wave? difficulty => level

        boolean group = false;
        if (units.isEmpty()) {
            units.addAll(ContainerUtils.openContainer(UnitGroupMaster.
                    getUnitGroupData(unitString, level)));
        }
        if (units.isEmpty()) { //DataManager.gettypes
            units.addAll(ContainerUtils.openContainer(unitString));
        } else group = true;
        if (units.isEmpty())
            return false;
        i++;

        //        CoordinatesFactory.createCoordinates(unitString);
        //        if (origin==null )
        //            origin = ref.getObj(unitString).getCoordinates();

        List<Coordinates> coordinates = null;
        if (group) {
            for (String sub : units) {
                coordinates.add(DC_ObjInitializer.getCoordinatesFromObjString(sub));

            }
        } else coordinates =
                getCoordinatesListForUnits(unitString, player, units, ref);
        String data = "";
        data +=
                DataUnitFactory.getKeyValueString(UnitsData.FORMAT,
                        PARTY_VALUE.COORDINATES, ContainerUtils.joinList(coordinates, DataUnitFactory.getContainerSeparator(UnitsData.FORMAT)));
        data +=
                DataUnitFactory.getKeyValueString(UnitsData.FORMAT,
                        PARTY_VALUE.UNITS, ContainerUtils.joinStringList(units, DataUnitFactory.getContainerSeparator(UnitsData.FORMAT)));

        UnitsData unitData = new UnitsData(data);

        SPAWN_MODE mode = SPAWN_MODE.SCRIPT;

        List<Unit> unitsList = getSpawner().spawn(unitData, player, mode);
        getSpawner().getFacingAdjuster().adjustFacing(unitsList);
        return true;
    }


    private List<Coordinates> getCoordinatesListForUnits(String arg, DC_Player player, List<String> units, Ref ref) {
        List<Coordinates> list;
        Coordinates origin = getCoordinates(arg, ref);
        // formation as arg? ;)
        list = getPositioner().getCoordinates(origin, player.isMe(), units);

        return list;
    }

    private Coordinates getCoordinates(String arg, Ref ref) {
        //TODO have an arg for N of Units
        Coordinates origin = null;
        try {
            origin = Coordinates.get(arg);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return origin;
    }

    public enum COMBAT_SCRIPT_FUNCTION {

        SCRIPT,
        SCRIPT_IF,
        SCRIPT_PARALLEL,
        SPAWN,
        MOVE,
        REPOSITION,
        REMOVE,
        KILL,
        ABILITY,
        COMMENT, //on event, create trigger script on another event...

        //        AI_SCRIPT_FUNCTION
        MOVE_TO,
        TURN_TO,
        ACTION,
        ATTACK,
        FREEZE,
        UNFREEZE,
        ORDER,
        ATOMIC,
        AGGRO,
        DIALOGUE, TIP, MESSAGE, QUEST, TIP_MSG, TIP_QUEST, DIALOGUE_TIP,
        ESOTERICA, CINEMATIC,
        AWAKEN,
    }

}
