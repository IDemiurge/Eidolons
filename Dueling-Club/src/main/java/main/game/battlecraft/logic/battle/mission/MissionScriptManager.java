package main.game.battlecraft.logic.battle.mission;

import main.ability.Ability;
import main.ability.AbilityImpl;
import main.ability.AbilityObj;
import main.ability.AbilityType;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.BattleHandler;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.game.battlecraft.logic.dungeon.universal.Spawner.SPAWN_MODE;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import main.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.game.battlecraft.logic.meta.scenario.script.ScriptTrigger;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnitFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionScriptManager extends BattleHandler<MissionBattle> {

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
            parseScript(script);

        }
    }


    private Condition parseConditions(String conditionPart) {
        Condition c = DC_ConditionMaster.toConditions(conditionPart);
        if (c!=null )
            return c;
        return null;
    }

    private EVENT_TYPE parseEvent(String eventPart) {

        EVENT_TYPE eventType = new EnumMaster<STANDARD_EVENT_TYPE>().retrieveEnumConst(STANDARD_EVENT_TYPE.class, eventPart);
        if (eventType != null) return eventType;
        return STANDARD_EVENT_TYPE.GAME_STARTED;
    }

    private void parseScript(String script) {
//non-trigger scripts?
        String originalText = script;
        String eventPart = StringMaster.getFirstItem(script, ScriptSyntax.PART_SEPARATOR);
        EVENT_TYPE event_type = parseEvent(eventPart);
        script = StringMaster.cropFirstSegment(script, ScriptSyntax.PART_SEPARATOR);

        String conditionPart = StringMaster.getFirstItem(script, ScriptSyntax.PART_SEPARATOR);
        Condition condition = parseConditions(conditionPart);

        boolean isRemove = true;
//        if (contains("cyclic"))remove = false;
        Ability abilities = null;
        Ref ref = new Ref(getMaster().getGame()); // TODO Global
        script = StringMaster.getLastPart(script, ScriptSyntax.PART_SEPARATOR);
        String funcPart = VariableManager.removeVarPart(script);
        MISSION_SCRIPT_FUNCTION func = new EnumMaster<MISSION_SCRIPT_FUNCTION>().retrieveEnumConst
         (MISSION_SCRIPT_FUNCTION.class, funcPart);
        if (func != null) {
            List<String> strings = StringMaster.openContainer(VariableManager.getVars(script), ScriptSyntax.SCRIPT_ARGS_SEPARATOR);
            String[] args = strings.toArray(new  String[strings.size()]);
//DataUnit?
            abilities = new AbilityImpl() {
                @Override
                public boolean activatedOn(Ref ref) {
                    execute(func, ref, args);
                    //reset after? not like normal action certainly...
                    return true;
                }
            };
        } else {
            AbilityType type = (AbilityType) DataManager.getType(funcPart, DC_TYPE.ABILS);
            if (func == null) {
                main.system.auxiliary.log.LogMaster.log(1, "SCRIPT NOT FOUND: " + funcPart);
                return;
            }
            AbilityObj abilObj = new AbilityObj(type, ref);
            abilities = abilObj.getAbilities();
        }
        abilities.setRef(ref);
        ScriptTrigger trigger = new ScriptTrigger(originalText, event_type, condition, abilities);
        trigger.setRemoveAfterTriggers(isRemove);
        addTrigger(trigger);
    }

    private void addTrigger(Trigger trigger) {
        getMaster().getGame().getManager().addTrigger(trigger);
//        scriptTriggers.add(trigger);
    }

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
