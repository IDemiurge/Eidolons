package main.game.battlecraft.logic.battle.mission;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.elements.triggers.Trigger;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.mission.MissionScriptManager.MISSION_SCRIPT_FUNCTION;
import main.game.battlecraft.logic.battle.universal.BattleHandler;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.game.battlecraft.logic.dungeon.universal.Spawner.SPAWN_MODE;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import main.game.battlecraft.logic.meta.scenario.dialogue.GameDialogue;
import main.game.battlecraft.logic.meta.scenario.scene.SceneFactory;
import main.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import main.game.battlecraft.logic.meta.scenario.script.ScriptGenerator;
import main.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import main.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.libgdx.DialogScenario;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionScriptManager extends BattleHandler<MissionBattle> implements ScriptExecutor<MISSION_SCRIPT_FUNCTION> {


    List<Trigger> scriptTriggers = new LinkedList<>();

    public MissionScriptManager(BattleMaster<MissionBattle> master) {
        super(master);
    }

    public void checkTriggers(Event e) {
        scriptTriggers.forEach(trigger -> trigger.check(e));

        //get ref from where? {vars} - global?
        //GlobalRef - {party}, {main_hero}, {party_group}, ...
    }

    public void createMissionTriggers() {
        String scripts = getBattle().getMission().getProperty(PROPS.MISSION_SCRIPTS);
        try {
            scripts +=
             ScriptSyntax.SCRIPTS_SEPARATOR+ readScriptsFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        parseScripts((scripts));
    }

    private String readScriptsFile() {
        String text = FileManager.readFile(
         StringMaster.buildPath(
         getMaster().getMissionResourceFolderPath()
         , ScriptGenerator.SCRIPTS_FILE_NAME));
        text = StringMaster.getLastPart(text, ScriptSyntax.COMMENT_CLOSE);
        return text;
    }

    @Override
    public MissionBattleMaster getMaster() {
        return (MissionBattleMaster) super.getMaster();
    }

    public void parseScripts(String scripts) {

        //syntax: new_round->equals({amount}, 2)->spawn(Vampires,5-5);
        for (String script : StringMaster.openContainer(scripts, ScriptSyntax.SCRIPTS_SEPARATOR)) {
            addTrigger(ScriptParser.parseScript(script, getMaster().getGame(), this));
        }
    }


    private void addTrigger(Trigger trigger) {
        if (trigger == null)
            return;
        getMaster().getGame().getManager().addTrigger(trigger);
//        scriptTriggers.add(trigger);
    }

    @Override
    public boolean execute(MISSION_SCRIPT_FUNCTION function, Ref ref, String... args) {
        switch (function) {
            case SPAWN:
                return doSpawn(ref, args);
            case DIALOGUE:
                return doDialogue(ref, args);

            case SCRIPT:
                return doScript(ref, args);
        }

        return doUnitOperation(function, ref, args);
    }

    private boolean doComment(Unit unit, String text) {
        FloatingTextMaster.getInstance().createFloatingText
         (TEXT_CASES.BATTLE_COMMENT, text, unit);
        return true;
    }

    private boolean doDialogue(Ref ref, String[] args) {
        GameDialogue dialogue = getGame().getMetaMaster().getDialogueFactory().getDialogue(
         args[0]);
        List<DialogScenario> list = SceneFactory.getScenes(dialogue);
        GuiEventManager.trigger(GuiEventType.DIALOG_SHOW, list);
        return true;
    }


    @Override
    public String getSeparator(MISSION_SCRIPT_FUNCTION func) {
        if (func == MISSION_SCRIPT_FUNCTION.SCRIPT) {
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

    private boolean doUnitOperation(MISSION_SCRIPT_FUNCTION function, Ref ref, String[] args) {
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
        List<String> units = new LinkedList<>();
//        if (args[i].contains(ScriptSyntax.SPAWN_ARG_UNITS_WAVE))
        {
            ObjType wave = DataManager.getType(args[i], DC_TYPE.ENCOUNTERS);
            if (wave != null)
                units.addAll(StringMaster.openContainer(wave.getProperty(PROPS.PRESET_GROUP)));
            //TODO adjust wave? difficulty => level
        }
        if (units.isEmpty()) {
            units.addAll(StringMaster.openContainer(UnitGroupMaster.getUnitGroupData(args[i], 0)));
        }
        if (units.isEmpty()) { //DataManager.gettypes
            units.addAll(StringMaster.openContainer(args[i]));
        }
        if (units.isEmpty())
            return false;
        i++;

//        CoordinatesFactory.createCoordinates(args[i]);
//        if (origin==null )
//            origin = ref.getObj(args[i]).getCoordinates();

        List<Coordinates> coordinates =
         getCoordinates(args[i], player, units);
        String data = "";
        data +=
         DataUnitFactory.getKeyValueString(UnitData.FORMAT,
          PARTY_VALUE.COORDINATES, StringMaster.joinList(coordinates, DataUnitFactory.getContainerSeparator(UnitData.FORMAT)));
        data +=
         DataUnitFactory.getKeyValueString(UnitData.FORMAT,
          PARTY_VALUE.MEMBERS, StringMaster.joinStringList(units, DataUnitFactory.getContainerSeparator(UnitData.FORMAT)));

        UnitData unitData = new UnitData(data);

        SPAWN_MODE mode = SPAWN_MODE.SCRIPT;

        getSpawner().spawn(unitData, player, mode);

        return true;
    }

    private List<Coordinates> getCoordinates(String arg, DC_Player player, List<String> units) {
        List<Coordinates> list = new LinkedList<>();
        Coordinates origin = null;
//TODO have an arg for N of Units
        if (arg.contains(ScriptSyntax.SPAWN_POINT) || StringMaster.isInteger(arg)) {
            arg = arg.replace(ScriptSyntax.SPAWN_POINT, "");
            Integer i = Integer.valueOf(arg);
            List<String> spawnPoints = StringMaster.openContainer(
             getBattle().getMission().getProperty(PROPS.ENEMY_SPAWN_COORDINATES));
            origin = new Coordinates(spawnPoints.get(i));
//            getUnit(arg).getCoordinates()
            //another units' coordinates
            //closest point
        }else {
            try {
                origin = new Coordinates(arg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // formation as arg? ;)
        list = getPositioner().getPartyCoordinates(origin, player.isMe(), units);

        return list;
    }

    public enum MISSION_SCRIPT_FUNCTION {
        AI,
        SPAWN,
        REMOVE,
        KILL,
        ABILITY,
        ACTION,
        DIALOGUE,
        SCRIPT, COMMENT, //on event, create trigger script on another event...
    }

}
