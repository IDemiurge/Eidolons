package main.game.battlecraft.logic.meta.scenario;

import main.ability.Abilities;
import main.ability.Ability;
import main.ability.ActiveAbility;
import main.ability.effects.special.meta.EndGameEffect;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.FixedTargeting;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.module.dungeoncrawl.dungeon.Location;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class ObjectiveMaster {
    private static final String KEY = KEYS.OBJECTIVE.toString();
    private static String objectiveData;

    public static String getObjectiveDescription(OBJECTIVE_TYPE type) {
        String data = null;
        return data;

    }

    public static String getObjectiveData() {
        return objectiveData;

    }

    public static void initCustomTriggers() {

    }

    private static void addTrigger(EVENT_TEMPLATE ET, String eventData, SCRIPT_CONDITION_TEMPLATE CT,
                                   String conditionData, ACTION_TEMPLATE AT, String actionData) {
        STANDARD_EVENT_TYPE eventType = null;
        Conditions conditions = null;
        Abilities abilities = null;
        addTrigger(eventType, conditions, abilities);
    }

    public static void initSpecialDefeatTrigger(OBJECTIVE_TYPE type, String data, Location mission) {

    }

    public static void initObjectives(String types, String data, Location location) {
        for (String s : StringMaster.openContainer(types)) {
            // main objective - victory?
            OBJECTIVE_TYPE type = new EnumMaster<OBJECTIVE_TYPE>().retrieveEnumConst(
                    OBJECTIVE_TYPE.class, s);
            // String data = scenario.getProperty(MACRO_PROPS.OBJECTIVE_DATA);
            ObjectiveMaster.initObjectiveTrigger(type, data, location);

        }

    }

    public static void initObjectiveTrigger(OBJECTIVE_TYPE type, String data, Location mission) {
        Dungeon dungeon = mission.getBossLevel();
        objectiveData = data;
        STANDARD_EVENT_TYPE eventType = null;
        Conditions conditions = new Conditions();
        Abilities abilities = new Abilities();
        abilities.add(new ActiveAbility(new FixedTargeting(), new EndGameEffect(true)));
        Ref ref = new Ref();
        Integer id = null;
        String name;
        switch (type) {
            case BOSS:
                eventType = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;
                Coordinates c = DC_ObjInitializer.getCoordinatesFromObjString(objectiveData);
                // new Coordinates(s);
                name = DC_ObjInitializer.getNameFromObjString(objectiveData);
//                for (Unit u : DC_Game.game.getObjectsOnCoordinate(dungeon.getZ(), c, false,
//                        false, false)) {
//                    if (u.getName().equals(name)) {
//                        id = u.getId();
//                    }
//                }
//                ref.setID(KEY, id);
//                conditions.add(new ObjComparison(KEY, KEYS.TARGET.toString()));
                break;
            case CAPTURE_HOLD:
                // holdWaveSequence
                // captureRule - claim counters or simpler? Position-based could
                // do too!
                // "As long as
                break;
            case SURVIVE_TIME:

                break;
            case ESCAPE:
//                eventType = STANDARD_EVENT_TYPE.UNIT_GONE_THRU_ENTRANCE;
                break;
            case ITEM:
                break;
        }
        // some objectives will be initialized on a condition, e.g. capture-hold

        addTrigger(eventType, conditions, abilities);
    }

    private static void addTrigger(EVENT_TYPE eventType, Condition conditions, Ability abilities) {
        Trigger trigger = new Trigger(eventType, conditions, abilities);
        DC_Game.game.getState().addTrigger(trigger);
    }

    /*
     * to be used also for constructing standalone missions
     *
     *
     *
     */
    public enum EVENT_TEMPLATE {
        ENTER, KILL, TIME, PICK_UP,
    }

    // should accept parameters!
    public enum ACTION_TEMPLATE {
        SUBOBJECTIVE_EFFECT, OBJECTIVE_EFFECT, INIT_DIALOG,
    }

    public enum SCRIPT_CONDITION_TEMPLATE {
        OBJ_REF, NUMERIC, STRING,
    }

    public enum SUB_OBJECTIVE_TYPE {
        COLLECT_ITEMS, SLAY_UNITS, RESCUE_UNITS,

    }

    public enum OBJECTIVE_TYPE {
        ITEM,
        BOSS,
        CAPTURE_HOLD,
        CAPTURE_HOLD_TIME,
        ESCAPE,
        RESCUE_ESCAPE,
        ENTER_AREA,
        SURVIVE_TIME
        // time-based
        ,
        ITEM_ESCAPE;
    }

    public enum OBJECTIVE_VALUES {
        GLOBAL_AGGRO_ON_ENTER_BLOCK,
    }

}
