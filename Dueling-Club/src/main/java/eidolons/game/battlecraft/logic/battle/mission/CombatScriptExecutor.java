package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battle.universal.ScriptManager;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner.SPAWN_MODE;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueView;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.Scene;
import eidolons.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptGenerator;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.elements.triggers.Trigger;
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
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 5/8/2017.
 */
public class CombatScriptExecutor extends ScriptManager<MissionBattle, COMBAT_SCRIPT_FUNCTION> {

    List<Trigger> scriptTriggers = new ArrayList<>();

    public CombatScriptExecutor(BattleMaster master) {
        super(master);

    }

    private ScriptExecutor<COMBAT_SCRIPT_FUNCTION> getScriptExecutor() {
        return master.getGame().getAiManager().getScriptExecutor();
    }

    public void checkTriggers(Event e) {
        scriptTriggers.forEach(trigger -> trigger.check(e));

        //get ref from where? {vars} - global?
        //GlobalRef - {party}, {main_hero}, {party_group}, ...
    }

    public void init() {
        createMissionTriggers();
    }

    public void createMissionTriggers() {
        String scripts = getBattle().getMission().getProperty(PROPS.MISSION_SCRIPTS);
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
        return PathUtils.buildPath(
                PathFinder.getTextPath(), "scripts", "demo"
                , getBattle().getMission().getName() + " " + ScriptGenerator.SCRIPTS_FILE_NAME);
    }
    //getMaster().getMetaMaster().getMetaGame().getScenario()

    @Override
    public MissionBattleMaster getMaster() {
        return (MissionBattleMaster) super.getMaster();
    }


    protected Class<COMBAT_SCRIPT_FUNCTION> getFunctionClass() {
        return COMBAT_SCRIPT_FUNCTION.class;
    }


    @Override
    public boolean execute(COMBAT_SCRIPT_FUNCTION function, Ref ref, String... args) {
        switch (function) {
            case SPAWN:
                return doSpawn(ref, args);
            case DIALOGUE:
                return doDialogue(ref, args);

            case SCRIPT:
                return doScript(ref, args);

            case REPOSITION:
                return doReposition(ref, args);

            case MOVE_TO:
            case TURN_TO:
            case ACTION:
            case ATTACK:
            case FREEZE:
            case UNFREEZE:
            case ORDER:
                return getScriptExecutor().execute(function, ref, args);
        }

        return doUnitOperation(function, ref, args);
    }


    //moves all party members to new positions around given origin
    private boolean doReposition(Ref ref, String[] args) {
//        GuiEventManager.trigger(GuiEventType.SHADOW_MAP_FADE_IN, 100);
        int i = 0;
//        String group = args[i];
//        i++;
        List<Unit> members = getMaster().getMetaMaster().getPartyManager().getParty().
         getMembers();
        List<Coordinates> coordinates =
         getCoordinatesListForUnits(args[i], getPlayerManager().getPlayer(true),
          members.stream().map(m -> m.getName()).collect(Collectors.toList()), ref);
        i = 0;
        for (Unit unit : members) {
            unit.setCoordinates(coordinates.get(i));
            i++;
        }
        for (Unit unit : members) {
            GuiEventManager.trigger(GuiEventType.UNIT_MOVED, unit);
        }
//        GuiEventManager.trigger(GuiEventType.SHADOW_MAP_FADE_OUT,  0);
        return true;
    }

    private boolean doComment(Unit unit, String text) {
        FloatingTextMaster.getInstance().createFloatingText
         (TEXT_CASES.BATTLE_COMMENT, text, unit);
        return true;
    }

    private boolean doDialogue(Ref ref, String[] args) {
        GameDialogue dialogue = getGame().getMetaMaster().getDialogueFactory().getDialogue(
         args[0]);
        List<Scene> list = SceneFactory.getScenes(dialogue);

        GuiEventManager.trigger(GuiEventType.DIALOG_SHOW, new DialogueHandler(dialogue, getGame(), list));
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
        int i = 0;
        Unit unit = (Unit) ref.getObj(args[0]);
        if (unit == null) {
            String name = args[i];
            if (DataManager.isTypeName(name))
                i++;
            else name = null;

            if (unit == null) {
                Boolean power = null;
                Boolean distance = null;
                Boolean ownership = null;
                unit = ((DC_Game) ref.getGame()).getMaster().getUnitByName(name, ref,
                 ownership, distance, power);
            }
        }
        //options - annihilate, ...
        switch (function) {
            case COMMENT:
                return doComment(unit, args[i]);
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
        int level = NumberUtils.getInteger(VariableManager.getVars(unitString));
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
         DataUnitFactory.getKeyValueString(UnitData.FORMAT,
          PARTY_VALUE.COORDINATES, ContainerUtils.joinList(coordinates, DataUnitFactory.getContainerSeparator(UnitData.FORMAT)));
        data +=
         DataUnitFactory.getKeyValueString(UnitData.FORMAT,
          PARTY_VALUE.MEMBERS, ContainerUtils.joinStringList(units, DataUnitFactory.getContainerSeparator(UnitData.FORMAT)));

        UnitData unitData = new UnitData(data);

        SPAWN_MODE mode = SPAWN_MODE.SCRIPT;

        List<Unit> unitsList = getSpawner().spawn(unitData, player, mode);
        getSpawner().getFacingAdjuster().adjustFacing(unitsList);
        return true;
    }

    private List<Coordinates> getCoordinatesListForUnits(String arg, DC_Player player, List<String> units, Ref ref) {
        List<Coordinates> list = new ArrayList<>();
        Coordinates origin = getCoordinates(arg, ref);
        // formation as arg? ;)
        list = getPositioner().getPartyCoordinates(origin, player.isMe(), units);

        return list;
    }

    private Coordinates getCoordinates(String arg, Ref ref) {
//TODO have an arg for N of Units
        Coordinates origin = null;
        if (arg.contains(ScriptSyntax.SPAWN_POINT) || NumberUtils.isInteger(arg)) {
            arg = arg.replace(ScriptSyntax.SPAWN_POINT, "");
            Integer i = NumberUtils.getInteger(arg) - 1;
            List<String> spawnPoints = ContainerUtils.openContainer(
             getMaster().getDungeon().getProperty(PROPS.ENEMY_SPAWN_COORDINATES));
            origin = Coordinates.get(spawnPoints.get(i));
            origin = getMaster().getDungeon().getPoint(arg);
//            getUnit(arg).getCoordinates()
            //another units' coordinates
            //closest point
        } else {
            try {
                origin = Coordinates.get(arg);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return origin;
    }

    public enum COMBAT_SCRIPT_FUNCTION {

        SCRIPT,
        SPAWN,
        MOVE,
        REPOSITION,
        REMOVE,
        KILL,
        ABILITY,
        DIALOGUE,
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
    }

}
