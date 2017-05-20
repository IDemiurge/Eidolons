package main.game.battlecraft.logic.battle.mission;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.data.ability.AE_ConstrArgs;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.mission.MissionScriptManager.MISSION_SCRIPT_FUNCTION;
import main.game.battlecraft.logic.battle.universal.BattleHandler;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.game.battlecraft.logic.dungeon.universal.Spawner.SPAWN_MODE;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import main.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import main.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import main.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.auxiliary.StringMaster;
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
        parseScripts(getBattle().getMission().getProperty(PROPS.MISSION_SCRIPTS));
    }

    public void parseScripts(String scripts) {

        //syntax: new_round->equals({amount}, 2)->spawn(Vampires,5-5);
        for (String script : StringMaster.openContainer(scripts, ScriptSyntax.SCRIPTS_SEPARATOR)) {
            addTrigger(ScriptParser.parseScript(script, getMaster().getGame(), this));
        }
    }


    private void addTrigger(Trigger trigger) {
        if (trigger==null )
            return ;
        getMaster().getGame().getManager().addTrigger(trigger);
//        scriptTriggers.add(trigger);
    }

    @Override
    public boolean execute(MISSION_SCRIPT_FUNCTION function, Ref ref, String... args) {
        switch (function) {
            case SPAWN:
                return doSpawn(ref, args);
            case REMOVE:
                break;
            case KILL:
                break;
        }

        return false;
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
        Coordinates origin = new Coordinates(args[i]);
//        CoordinatesFactory.createCoordinates(args[i]);
//        if (origin==null )
//            origin = ref.getObj(args[i]).getCoordinates();

        List coordinates = getPositioner().getPartyCoordinates(origin, player.isMe(), units);
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

    public enum MISSION_SCRIPT_FUNCTION {
        @AE_ConstrArgs(argNames = {"", "", ""})
        SPAWN,
        REMOVE,
        KILL,
        ABILITY,
        DIALOGUE,
        SCRIPT, //on event, create trigger script on another event...
    }

}
