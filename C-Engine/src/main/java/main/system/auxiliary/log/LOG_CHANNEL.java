package main.system.auxiliary.log;

import main.system.auxiliary.log.LogMaster.LOG;

/**
 * Created by JustMe on 5/23/2018.
 */
public enum LOG_CHANNEL {
    EFFECT_ACTIVE_DEBUG(LogMaster.EFFECT_SPECIFIC_DEBUG_PREFIX, LogMaster.EFFECT_SPECIFIC_DEBUG_ON, LogMaster.EFFECT_SPECIFIC_DEBUG),

    EFFECT_PASSIVE_DEBUG(LogMaster.EFFECT_PASSIVE_DEBUG_PREFIX, LogMaster.EFFECT_PASSIVE_DEBUG_ON, LogMaster.EFFECT_PASSIVE_DEBUG),
    WAVE_ASSEMBLING(LogMaster.WAVE_ASSEMBLING_DEBUG_PREFIX, LogMaster.WAVE_ASSEMBLING_DEBUG_ON, LogMaster.WAVE_ASSEMBLING_DEBUG),
    CONDITION_DEBUG(LogMaster.CONDITION_DEBUG_PREFIX, LogMaster.CONDITION_DEBUG_ON, LogMaster.CONDITION_DEBUG),

    CORE_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    EVENT_DEBUG(LogMaster.EVENT_DEBUG_PREFIX, LogMaster.EVENT_DEBUG_ON, LogMaster.EVENT_DEBUG),
    TRIGGER_DEBUG(LogMaster.TRIGGER_DEBUG_PREFIX, LogMaster.TRIGGER_DEBUG_ON, LogMaster.TRIGGER_DEBUG),
    EFFECT_DEBUG(LogMaster.EFFECT_DEBUG_PREFIX, LogMaster.EFFECT_DEBUG_ON, LogMaster.EFFECT_DEBUG),
    PERFORMANCE_DEBUG(LogMaster.PERFORMANCE_DEBUG_PREFIX, LogMaster.PERFORMANCE_DEBUG_ON, LogMaster.PERFORMANCE_DEBUG),
    WAIT_DEBUG(LogMaster.WAIT_DEBUG_PREFIX, LogMaster.WAIT_DEBUG_ON, LogMaster.WAIT_DEBUG),
    RULES_DEBUG(LogMaster.RULES_DEBUG_PREFIX, LogMaster.RULES_DEBUG_ON, LogMaster.RULES_DEBUG),
    BUFF_DEBUG(LogMaster.BUFF_DEBUG_PREFIX, LogMaster.BUFF_DEBUG_ON, LogMaster.BUFF_DEBUG),
    LOGIC_DEBUG(LogMaster.LOGIC_DEBUG_PREFIX, LogMaster.LOGIC_DEBUG_ON, LogMaster.LOGIC_DEBUG),
    VISIBILITY_DEBUG(LogMaster.VISIBILITY_DEBUG_PREFIX, LogMaster.VISIBILITY_DEBUG_ON, LogMaster.VISIBILITY_DEBUG),
    PATHING_DEBUG(LogMaster.PATHING_DEBUG_PREFIX, LogMaster.PATHING_DEBUG_ON, LogMaster.PATHING_DEBUG),

    CORE_DEBUG_1(LogMaster.CORE_DEBUG_1_PREFIX, LogMaster.CORE_DEBUG_1_ON, LogMaster.CORE_DEBUG_1),
    CONSTRUCTION_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    AI_DEBUG2(LogMaster.AI_DEBUG_PREFIX, LogMaster.AI_DEBUG_ON2, LogMaster.AI_DEBUG2),
    AI_DEBUG(LogMaster.AI_DEBUG_PREFIX, LogMaster.AI_DEBUG_ON, LogMaster.AI_DEBUG),
    AI_TRAINING(LogMaster.AI_TRAINING_PREFIX, LogMaster.AI_TRAINING_ON,
     LogMaster.AI_TRAINING),


    MOVEMENT_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    GUI_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),

    ANIM_DEBUG(LogMaster.ANIM_DEBUG_PREFIX, LogMaster.ANIM_DEBUG_ON, LogMaster.ANIM_DEBUG),
    PUZZLE_DEBUG(LogMaster.PUZZLE_DEBUG_PREFIX, LogMaster.PUZZLE_DEBUG_ON, LogMaster.PUZZLE_DEBUG),

    VFX_DEBUG(LogMaster.VFX_DEBUG_PREFIX, LogMaster.VFX_DEBUG_ON, LogMaster.VFX_DEBUG),

    COMBAT_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    MAP_GENERATION_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    MATH_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    VERBOSE_CHECK(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    TRAVEL_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    WAITING_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    ATTACKING_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    VALUE_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
    DATA_DEBUG(LogMaster.DATA_DEBUG_PREFIX, LogMaster.DATA_DEBUG_ON, LogMaster.DATA_DEBUG),
    GAME_INFO(LOG.GAME_INFO, LogMaster.GAME_INFO_PREFIX, LogMaster.GAME_INFO_ON, LogMaster.GAME_INFO),

    MACRO_DYNAMICS(LOG.GAME_INFO, LogMaster.MACRO_DYNAMICS_PREFIX, LogMaster.MACRO_DYNAMICS_ON, LogMaster.MACRO_DYNAMICS),
    GENERATION(LOG.SYSTEM_INFO, LogMaster.GENERATION_PREFIX, LogMaster.GENERATION_ON, LogMaster.GENERATION),
    ERROR_CRITICAL(LOG.SYSTEM_INFO, LogMaster.ERROR_CRITICAL_PREFIX, LogMaster.ERROR_CRITICAL_ON, LogMaster.GENERATION),

    BUILDING(LogMaster.BUILDING_PREFIX, LogMaster.BUILDING_ON, LogMaster.BUILDING ),
    SAVE(LogMaster.SAVE_PREFIX, LogMaster.SAVE_ON, LogMaster.SAVE );
    private boolean on;
    private String prefix;
    private int code;
    private LOG log;

    LOG_CHANNEL(LOG log, String prefix, boolean on, int code) {
        this(prefix, on, code);
        this.setLog(log);
    }

    LOG_CHANNEL(String prefix, boolean on, int code) {
        this.setCode(code);
        this.setOn(on);
        this.setPrefix(prefix);
    }

    public static LOG_CHANNEL getByCode(int priority) {

        for (LOG_CHANNEL c : LOG_CHANNEL.values()) {
            if (c.getCode() == priority) {
                return c;
            }
        }
        return null;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LOG getLog() {
        return log;
    }

    public void setLog(LOG log) {
        this.log = log;
    }
}
