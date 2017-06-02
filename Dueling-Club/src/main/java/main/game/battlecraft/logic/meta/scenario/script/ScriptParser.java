package main.game.battlecraft.logic.meta.scenario.script;

import main.ability.Ability;
import main.ability.AbilityImpl;
import main.data.ability.construct.AbilityConstructor;
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

    public static STANDARD_EVENT_TYPE getEventByShortcut(String text) {
        SCRIPT_EVENT_SHORTCUT shortcut =
         new EnumMaster<SCRIPT_EVENT_SHORTCUT>().
          retrieveEnumConst(SCRIPT_EVENT_SHORTCUT.class, text);
        return shortcut.event_type;
    }

    public static EVENT_TYPE parseEvent(String eventPart) {
        EVENT_TYPE eventType = getEventByShortcut(eventPart);
        if (eventType != null) return eventType;
         eventType = new EnumMaster<STANDARD_EVENT_TYPE>().retrieveEnumConst(STANDARD_EVENT_TYPE.class, eventPart);
        if (eventType != null) return eventType;

        return STANDARD_EVENT_TYPE.GAME_STARTED;
    }

    public static ScriptTrigger parseScript(String script, DC_Game game,
                                            ScriptExecutor executor

    ) {
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
            //TODO for multiple scripts, need another SEPARATOR!
            String separator = executor.getSeparator(func);
            List<String> strings =
             StringMaster.openContainer(VariableManager.getVars(script),
              separator);
            String[] args = strings.toArray(new String[strings.size()]);
            abilities = new AbilityImpl() {
                @Override
                public boolean activatedOn(Ref ref) {
                    executor.execute(func, ref, args);
                    //reset after? not like normal action certainly...
                    return true;
                }
            };
        } else {
            abilities = AbilityConstructor.getAbilities(script, ref);
            if (abilities.getEffects().getEffects().isEmpty()) {
                main.system.auxiliary.log.LogMaster.log(1, "SCRIPT NOT FOUND: " + funcPart);
                return null;
            }
        }
        abilities.setRef(ref);
        ScriptTrigger trigger = new ScriptTrigger(originalText, event_type, condition, abilities);
        trigger.setRemoveAfterTriggers(isRemove);
        return trigger;
    }

    public enum SCRIPT_EVENT_SHORTCUT {
        ROUND(STANDARD_EVENT_TYPE.NEW_ROUND), DIES(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED),
        ENTERS(STANDARD_EVENT_TYPE.UNIT_HAS_ENTERED), ENGAGED(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ENGAGED),;
        STANDARD_EVENT_TYPE event_type;

        SCRIPT_EVENT_SHORTCUT(STANDARD_EVENT_TYPE event_type) {
            this.event_type = event_type;
        }
    }
}
