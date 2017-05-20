package main.game.battlecraft.logic.meta.scenario.script;

import main.ability.Ability;
import main.ability.AbilityImpl;
import main.ability.AbilityObj;
import main.ability.AbilityType;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.game.battlecraft.logic.battle.mission.MissionScriptManager.MISSION_SCRIPT_FUNCTION;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.util.Refactor;

import java.util.List;

/**
 * Created by JustMe on 5/19/2017.
 */
public class ScriptParser {
    public static Condition parseConditions(String conditionPart) {
        Condition c = DC_ConditionMaster.toConditions(conditionPart);
        if (c != null)
            return c;
        return null;
    }

    public static EVENT_TYPE parseEvent(String eventPart) {

        EVENT_TYPE eventType = new EnumMaster<STANDARD_EVENT_TYPE>().retrieveEnumConst(STANDARD_EVENT_TYPE.class, eventPart);
        if (eventType != null) return eventType;
        return STANDARD_EVENT_TYPE.GAME_STARTED;
    }

    public static ScriptTrigger parseScript(String script, DC_Game game,
                                            ScriptExecutor executor) {
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
        Ref ref = new Ref(game); // TODO Global
        script = StringMaster.getLastPart(script, ScriptSyntax.PART_SEPARATOR);
        String funcPart = VariableManager.removeVarPart(script);
       @Refactor
        //TODO this won't work in generic way!!!!
        MISSION_SCRIPT_FUNCTION func = new EnumMaster<MISSION_SCRIPT_FUNCTION>().retrieveEnumConst
         (MISSION_SCRIPT_FUNCTION.class, funcPart);
        if (func != null) {
            List<String> strings = StringMaster.openContainer(VariableManager.getVars(script), ScriptSyntax.SCRIPT_ARGS_SEPARATOR);
            String[] args = strings.toArray(new String[strings.size()]);
//DataUnit?
            abilities = new AbilityImpl() {
                @Override
                public boolean activatedOn(Ref ref) {
                    executor.execute(func, ref, args);
                    //reset after? not like normal action certainly...
                    return true;
                }
            };
        } else {
            AbilityType type =
             VariableManager.getVarType(script, false, ref);
//             (AbilityType) DataManager.getType(funcPart, DC_TYPE.ABILS);
            if (type == null) {
                main.system.auxiliary.log.LogMaster.log(1, "SCRIPT NOT FOUND: " + funcPart);
                return null;
            }
            AbilityObj abilObj = new AbilityObj(type, ref);

            abilities = abilObj.getAbilities();
        }
        abilities.setRef(ref);
        ScriptTrigger trigger = new ScriptTrigger(originalText, event_type, condition, abilities);
        trigger.setRemoveAfterTriggers(isRemove);
        return trigger;
    }
}
