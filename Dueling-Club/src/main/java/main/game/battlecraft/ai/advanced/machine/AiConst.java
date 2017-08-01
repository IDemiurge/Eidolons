package main.game.battlecraft.ai.advanced.machine;

/**
 * Created by JustMe on 7/31/2017.
 */
public enum AiConst {
    //technical
    DEFAULT_PRUNE_SIZE(5),
    DEFAULT_PRIORITY(100),
    DEFAULT_ATTACK_PRIORITY(100),

    //general
    CONVERGING_FACTOR(0.5f),
    SUMMON_PRIORITY_MOD(1000),
    RETREAT_PRIORITY_FACTOR(0.33f),

    //STD_ACTION
    DEFAULT_RESTORATION_PRIORITY_MOD(40),
    WAIT_PRIORITY_FACTOR(10),
    //RULES AND ,S
    COUNTER_FACTOR(4),
    ITEM,
    THROW,
    EXTRA_ATTACK,

    //RPG
    COST_SQUARE(5),
    COST_DIVIDER(20),

    //PARAMS
    ATTRIBUTE(50), C_ENDURANCE(2), ENDURANCE(3), C_TOUGHNESS(3), TOUGHNESS(5),
    C_STAMINA(8), C_FOCUS(6), C_MORALE(3), C_ESSENCE(4), C_N_OF_ACTIONS(50),
    C_N_OF_COUNTERS(30), SPIRIT(30), CONCEALMENT_DARKVISION(2), CONCEALMENT(3),
    C_INITIATIVE_BONUS(2), ARMOR(5), RESISTANCE(4), BASE_DAMAGE(2.5f),
    DAMAGE_BONUS(2.5f), DEFENSE(3), ATTACK(3), STAMINA(12), FOCUS(9), MORALE(5),
    ESSENCE(6), N_OF_ACTIONS(80), N_OF_COUNTERS(40),
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
     */;

    private float defValue;

    AiConst() {
    }

    AiConst(float f) {
        this.defValue = f;
    }

    public float getDefValue() {
        return defValue;
    }
}
