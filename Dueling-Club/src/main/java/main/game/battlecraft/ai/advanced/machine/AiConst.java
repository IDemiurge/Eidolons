package main.game.battlecraft.ai.advanced.machine;

import main.game.battlecraft.ai.advanced.machine.AiPriorityConstantMaster.AiConstant;

/**
 * Created by JustMe on 7/31/2017.
 * <p>
 * what kind of params will allow to alter behavior significantly?
 */

//IDEA - generate consts dynamically?
public enum AiConst implements AiConstant {
    //technical
    DEFAULT_PRUNE_SIZE(5),
    DEFAULT_PRIORITY(100),
    DEFAULT_ATTACK_PRIORITY(450),
    DEFAULT_SPELL_ATTACK_PRIORITY(350),

    TURN_,
    SEQUENCE_LENGTH_PENALTY_POW15(3),
    SEQUENCE_LENGTH_PENALTY(12),
    //ATOMIC

    //general
    COST_SQUARE(6),
    COST_DIVIDER(20),
    CONVERGING_FACTOR(0.5f),
    SUMMON_PRIORITY_MOD(1000),
    RETREAT_PRIORITY_FACTOR(0.33f),
    CAPACITY,
    DAMAGE_PRIORITY_MOD(1.5f),
    //BEHAVIORS
    BERSERK_ATTACK_PRIORITY, //SEPARATE PROFILE?
    DANGER_RANGED_BASE(125),
    DANGER_MELEE_BASE(125),
    //STD_ACTION
    DEFAULT_RESTORATION_PRIORITY_MOD(10),
    WAIT_PRIORITY_FACTOR(10),
    ACTION_PRIORITY_DEFEND,
    ACTION_PRIORITY_ALERT,
    ACTION_PRIORITY_WATCH,

    //RULES AND ,S
    COUNTER_FACTOR(0.25f),
    ITEM_USE_PRIORITY_MOD,
    THROW_PENALTY_PER_ITEM_COST,
    EXTRA_ATTACK,
    RULE_ATTACK_OF_OPPORTUNITY,
    RULE_INSTANT_ATTACK,
//    RULE_ATTACK_OF_OPPORTUNITY,

    //RPG
    SELF_VALUE,
    ALLY_PRIORITY_HEALTH,
    ALLY_PRIORITY_POWER,
    ALLY_PRIORITY_THREAT,

    ENEMY_PRIORITY_HEALTH,
    ENEMY_PRIORITY_POWER,
    ENEMY_PRIORITY_THREAT,
    //PER GOAL_TYPE
    GOAL_STEALTH,
    GOAL_ATTACK, // ALL HOSTILE GOALS
    GOAL_APPROACH,
    GOAL_BUFF, // ALL ALLIES
    GOAL_SELF(), // ALL non-std SELFIES
    GOAL_DEBUFF,
    GOAL_RESTORE(0.5f),
    GOAL_DEBILITATE,

    GOAL_SUMMONING,
    GOAL_MOVE, // STD AND CUSTOM MOVE ACTIONS
    GOAL_WAIT, // on allies or enemies!
    GOAL_PREPARE(0.5f), // BUFFS, MODES
    GOAL_DEFEND(0.5f), // ALERT OR DEFEND
    GOAL_PROTECT,
    GOAL_RETREAT, // USUALLY FORCED
    GOAL_SEARCH, // IF NO ENEMIES DETECTED, LOOK AROUND

    GOAL_ZONE_SPECIAL,
    GOAL_AUTO_DAMAGE,
    GOAL_AUTO_BUFF,
    GOAL_AUTO_DEBUFF,
    GOAL_ZONE_DAMAGE,
    GOAL_CUSTOM_HOSTILE,
    GOAL_CUSTOM_SUPPORT,
    GOAL_COATING,
    //PER SITUATION


    //PARAMS
    ATTRIBUTE(50),
    PARAM_STRENGTH,
    PARAM_VITALITY,
    PARAM_AGILITY,
    PARAM_DEXTERITY,
    PARAM_WILLPOWER,
    PARAM_INTELLIGENCE,
    PARAM_WISDOM,
    PARAM_KNOWLEDGE,
    PARAM_SPELLPOWER,
    PARAM_CHARISMA,
    PARAM_DETECTION(1.5f),
    PARAM_C_ENDURANCE(2), PARAM_ENDURANCE(3), PARAM_C_TOUGHNESS(3), PARAM_TOUGHNESS(5),
    PARAM_C_STAMINA(8), PARAM_C_FOCUS(6), PARAM_C_MORALE(3), PARAM_C_ESSENCE(4), PARAM_C_N_OF_ACTIONS(50),
    PARAM_C_N_OF_COUNTERS(30), PARAM_SPIRIT(30), PARAM_CONCEALMENT_DARKVISION(2), PARAM_CONCEALMENT(3),
    PARAM_C_INITIATIVE(2), PARAM_C_INITIATIVE_BONUS(2), PARAM_ARMOR(5), PARAM_RESISTANCE(4), PARAM_BASE_DAMAGE(2.5f),
    PARAM_DAMAGE_BONUS(2.5f), PARAM_DEFENSE(3), PARAM_ATTACK(3), PARAM_STAMINA(12), PARAM_FOCUS(9), PARAM_MORALE(5),
    PARAM_ESSENCE(6), PARAM_N_OF_ACTIONS(80), PARAM_N_OF_COUNTERS(40),
    /*
                , DEFENSE_MOD,
                    (target.getIntParam(PARAMS.DEFENSE) * 4 / 100)
                , DAMAGE_MOD,
                    (target.getIntParam(PARAMS.DAMAGE) * 3 / 100)
                      , ATTACK_MOD,
                    (new Float(Math.sqrt(target
                            .getIntParam(PARAMS.ATTACK))
                            * target.getIntParam(PARAMS.DAMAGE)
                            / 100
                            + Math.sqrt(target
                            .getIntParam(PARAMS.OFF_HAND_ATTACK))
                            * target.getIntParam(PARAMS.OFF_HAND_DAMAGE) / 100)

                            )
 if (param.isAttribute()) {
        (50)
    }
    , ATTACK,
     , DEFENSE,
     , ATTACK_MOD,
     , DEFENSE_MOD,
     (75)
            , C_STAMINA,
     , C_FOCUS,
     , C_MORALE,
     (25)
            , C_ESSENCE,
     (25)

            , C_ENDURANCE,
     (65)
            , C_TOUGHNESS,
     (100)


      if (unit_ai.getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
            basePriority = 100)
        } else if (targetObj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            if (!Analyzer.isBlockingMovement(unit_ai.getUnit(), (Unit) targetObj)) {
                (0)
            }
            basePriority = 20)
        }

         Integer healthMod = 100)
        if (less_or_more_for_health != null) {
            healthMod = getHealthFactor(targetObj, less_or_more_for_health))
        } else {
            if (target_unit.isUnconscious()) {
                (basePriority / 4) // [QUICK FIX] - more subtle?
            }
        }


         private int getRestorationPriorityMod(Unit unit) {
        int mod = getConstInt(AiConst.DEFAULT_RESTORATION_PRIORITY_MOD))
        if (unit.getAI().getType() == AI_TYPE.BRUTE) {
            mod -= 15)
        }
        if (unit.getAI().checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
            mod -= 15)
        }
        if (unit.isMine()) {
            mod += 15)
        }
        (mod;
    }
     */
    GEN_SPELL_DURATION_MULTIPLIER(50),
    GEN_SPELL_DURATION_SQRT_MULTIPLIER(1000),
    LETHAL_DAMAGE_MOD(200),
    LETHAL_DAMAGE_MOD_VS_UNCONSCIOUS(150),
    UNCONSCIOUS_DAMAGE_MOD(160), DAMAGE_PERCENTAGE_MOD_MINIMUM(10),
    UNCONSCIOUS_UNIT_PRIORITY_MOD(0.25f),
    ATOMIC_APPROACH_DEFAULT_DISTANCE(3);

    private float defValue;

    AiConst() {
        this(1f);
    }

    AiConst(float f) {
        this.defValue = f;
    }

    public float getDefValue() {
        return defValue;
    }
}
