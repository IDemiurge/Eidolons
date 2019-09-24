package eidolons.game.battlecraft.logic.meta.scenario.script;

import eidolons.game.core.game.DC_Game;
import eidolons.system.DC_ConditionMaster;
import main.ability.Ability;
import main.ability.AbilityImpl;
import main.data.ability.construct.AbilityConstructor;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Converter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster.CONDITION_TEMPLATES;
import main.system.util.Refactor;

import java.util.List;

/**
 * Created by JustMe on 5/19/2017.
 */
public class ScriptParser {
    public static final boolean TEST_MODE = false;//CoreEngine.isLiteLaunch() ;

    public static Conditions parseConditions(String conditionPart) {
        Conditions c = DC_ConditionMaster.toConditions(conditionPart);
        if (c != null)
            return c;
        return null;
    }

    public static STANDARD_EVENT_TYPE getEventByShortcut(String text) {
        SCRIPT_EVENT_SHORTCUT shortcut =
                new EnumMaster<SCRIPT_EVENT_SHORTCUT>().
                        retrieveEnumConst(SCRIPT_EVENT_SHORTCUT.class, text);
        if (shortcut == null) {
            return null;
        }
        return shortcut.event_type;
    }

    public static STANDARD_EVENT_TYPE parseEvent(String eventPart) {
        STANDARD_EVENT_TYPE eventType = getEventByShortcut(eventPart);
        if (eventType != null) return eventType;
        eventType = new EnumMaster<STANDARD_EVENT_TYPE>().retrieveEnumConst(STANDARD_EVENT_TYPE.class, eventPart);
        if (eventType != null) return eventType;

        return STANDARD_EVENT_TYPE.GAME_STARTED;
    }

    public static CONDITION_TEMPLATES getDefaultConditionForEvent(STANDARD_EVENT_TYPE event_type) {
        switch (event_type) {
            case UNIT_HAS_BEEN_DEALT_PURE_DAMAGE:
            case UNIT_HAS_BEEN_HIT:
//                return CONDITION_TEMPLATES.HIT;
            return CONDITION_TEMPLATES.NAME_TARGET;

            case UNIT_HAS_CHANGED_FACING:
                return CONDITION_TEMPLATES.FACING;

            case UNIT_ACTION_COMPLETE:
            case ACTION_BEING_ACTIVATED:
            case ACTION_ACTIVATED:
            case RESET_DONE:
            case CHANNELING_DONE:
            case CHANNELING_STARTED:
            case CHANNELING_FAIL:
            case SPELL_RESISTED:
                return CONDITION_TEMPLATES.NAME_ACTIVE;
            case COMBAT_STARTS:
            case COMBAT_ENDS:
                break;
            case UNIT_TURN_READY:
            case UNIT_IS_BEING_KILLED:
            case UNDYING_RULE:
                return CONDITION_TEMPLATES.NAME_SOURCE;
            case UNIT_BEING_SUMMONED:
            case UNIT_SUMMONED:
                return CONDITION_TEMPLATES.NAME_SUMMONED;
            case UNIT_HAS_BEEN_KILLED:
                return CONDITION_TEMPLATES.NAME_TARGET;

            case DOOR_OPENS:
            case INTERACTIVE_OBJ_USED:
                return CONDITION_TEMPLATES.NAME_TARGET; //TODO could do cord and name with 2 args..
//                return CONDITION_TEMPLATES.COORDINATES; //TODO could do cord and name with 2 args..
            case UNIT_FINISHED_MOVING: //for POS(10-10), will check that source is in 2 or less dist
                return CONDITION_TEMPLATES.DISTANCE;

            case DIALOGUE_FINISHED:
                return CONDITION_TEMPLATES.STRING_STRICT;
            case DIALOGUE_LINE_SPOKEN:
            case ROUND_ENDS:
            case NEW_ROUND:
                return CONDITION_TEMPLATES.NUMERIC_EQUAL;
        }
        return null;
    }

    public static Condition getDefaultCondition(STANDARD_EVENT_TYPE event_type, String vars) {
        CONDITION_TEMPLATES template = getDefaultConditionForEvent(event_type);
        if (template == null) {
            return null;
        }
        String var1 = getVarOne(event_type, vars);
        String var2 = getVarTwo(event_type, vars);
        if (var1.isEmpty())
            if (var2.isEmpty()) {
                return null;
            }
        return DC_ConditionMaster.getInstance().getConditionFromTemplate(
                template, var1, var2);
    }

    private static String getVarTwo(STANDARD_EVENT_TYPE event_type, String vars) {
        switch (event_type) {

            case UNIT_FINISHED_MOVING:
                return
                        //VariableManager.AUTOVAR.COORDINATE+StringMaster.wrapInBraces
                        (vars);
            case DIALOGUE_FINISHED:
                return "{event_string}";
            case DIALOGUE_LINE_SPOKEN:
            case NEW_ROUND:
                return "{event_amount}";
        }
        if (vars.contains(",")) {
            return vars.split(",")[1];
        }
        return vars;
    }

    private static String getVarOne(STANDARD_EVENT_TYPE event_type, String vars) {
        switch (event_type) {
            case UNIT_FINISHED_MOVING:
                return "{event_source}";
        }
        if (vars.contains(",")) {
            return vars.split(",")[0];
        }
        return vars;
    }

    public static <T> ScriptTrigger parseScript(String script, DC_Game game,
                                                ScriptExecutor<T> executor, Class<T> funcClass

    ) {
        script = ScriptMaster.getScriptByName(script); //TODO
//non-trigger scripts?
        String originalText = script;
        String processedPart = StringMaster.getFirstItem(script, ScriptSyntax.PART_SEPARATOR);
        STANDARD_EVENT_TYPE event_type = parseEvent(processedPart);
        script = StringMaster.cropFirstSegment(script, ScriptSyntax.PART_SEPARATOR);
        Conditions conditions = new Conditions();
        Condition defaultCondition = null;
        defaultCondition = getDefaultCondition(event_type, VariableManager.getVars(processedPart));
        if (defaultCondition != null) {
            defaultCondition.setXml(XML_Converter.wrap("ScriptedCondition",
                    XML_Converter.wrap("STANDARD_EVENT_TYPE", event_type.toString())
                            + XML_Converter.wrap("STRING", VariableManager.getVars(processedPart))
            ));
            conditions.add(defaultCondition);
        }
        String conditionPart = StringMaster.getFirstItem(script,
                ScriptSyntax.PART_SEPARATOR).trim();
        if (!conditionPart.isEmpty()) {
            Conditions customCondition = parseConditions(conditionPart);

            {
                String var1 = getVarOne(event_type, VariableManager.getVars(conditionPart));
                String var2 = getVarTwo(event_type, VariableManager.getVars(conditionPart));
                CONDITION_TEMPLATES template = new EnumMaster<CONDITION_TEMPLATES>().retrieveEnumConst(CONDITION_TEMPLATES.class,
                        VariableManager.removeVarPart(conditionPart));
                if (template != null) {
                    customCondition.add(DC_ConditionMaster.getInstance().getConditionFromTemplate(template, var1, var2));
                }

            }
            if (customCondition != null) {
                customCondition.setXml(conditionPart);
                conditions.add(customCondition);
            }
        }


        boolean isRemove = true;
//        if (contains("cyclic"))remove = false;
        Ability abilities = null;
        Ref ref = new Ref(game); // TODO Global
        script = StringMaster.getLastPart(script, ScriptSyntax.PART_SEPARATOR);
        String funcPart = VariableManager.removeVarPart(script);
        @Refactor
        //TODO this won't work in generic way!!!!
                T func =
                new EnumMaster<T>().retrieveEnumConst
                        (funcClass, funcPart);
        if (func != null) {
            //TODO for multiple scripts, need another SEPARATOR!
            String separator = executor.getSeparator(func);
            List<String> strings =
                    ContainerUtils.openContainer(VariableManager.getVars(script),
                            separator);
            String[] args = strings.toArray(new String[strings.size()]);
            abilities = new AbilityImpl() {
                @Override
                public boolean activatedOn(Ref ref) {
                    return executor.execute(func, ref, args);
                    //reset after? not like normal action certainly...
                }
            };
        } else {
            main.system.auxiliary.log.LogMaster.log(1, "Ability function in script: " + funcPart);
            abilities = AbilityConstructor.getAbilities(script, ref);
            if (abilities.getEffects().getEffects().isEmpty()) {
                main.system.auxiliary.log.LogMaster.log(1, "SCRIPT NOT FOUND: " + funcPart);
                return null;
            }
        }
        abilities.setRef(ref);
        ScriptTrigger trigger = new ScriptTrigger(originalText, event_type, conditions, abilities);
        if (TEST_MODE)
            isRemove = false;
        trigger.setRemoveAfterTriggers(isRemove);
        return trigger;
    }


    public enum SCRIPT_EVENT_SHORTCUT {
        HIT(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT),
        ACTION(STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE),
        AFTER_ACTION(STANDARD_EVENT_TYPE.RESET_DONE),
        AFTER_CHANNELING(STANDARD_EVENT_TYPE.CHANNELING_DONE),
        BEFORE_ACTION(STANDARD_EVENT_TYPE.ACTION_BEING_ACTIVATED),
        MID_ACTION(STANDARD_EVENT_TYPE.ACTION_ACTIVATED),
        FACING(STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING),
        HAS_TURN(STANDARD_EVENT_TYPE.UNIT_TURN_READY),
        DYING(STANDARD_EVENT_TYPE.UNIT_IS_BEING_KILLED),
        UNDYING(STANDARD_EVENT_TYPE.UNDYING_RULE),
        MOVE(STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING),
        COMBAT(STANDARD_EVENT_TYPE.COMBAT_STARTS),
        COMBAT_ENDS(STANDARD_EVENT_TYPE.COMBAT_ENDS),

        BEFORE_SUMMONED(STANDARD_EVENT_TYPE.UNIT_BEING_SUMMONED),
        SUMMONED(STANDARD_EVENT_TYPE.UNIT_SUMMONED),

        KILL(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED),
        INTERACT(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_USED),
        INTERACT_NAME(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_USED),
        DOOR(STANDARD_EVENT_TYPE.DOOR_OPENS),
        POS(STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING),
        LINE(STANDARD_EVENT_TYPE.DIALOGUE_LINE_SPOKEN),
        DIALOGUE(STANDARD_EVENT_TYPE.DIALOGUE_FINISHED),
        CLEARED(STANDARD_EVENT_TYPE.ENEMIES_CLEARED),
        ROUND(STANDARD_EVENT_TYPE.NEW_ROUND),
        DIES(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED),
        ENTERS(STANDARD_EVENT_TYPE.UNIT_HAS_ENTERED_COMBAT),
        ENGAGED(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ENGAGED),
        ;
        STANDARD_EVENT_TYPE event_type;

        SCRIPT_EVENT_SHORTCUT(STANDARD_EVENT_TYPE event_type) {
            this.event_type = event_type;
        }
    }
}
