package main.game.battlecraft.ai.advanced.machine.train;

import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.advanced.machine.train.AiTrainingCriteria.CRITERIA_TYPE_NUMERIC;
import main.game.battlecraft.logic.battle.universal.stats.BattleStatManager.STAT;
import main.game.battlecraft.logic.battle.universal.stats.PlayerStats;
import main.game.battlecraft.logic.battle.universal.stats.UnitStats;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;

/**
 * Created by JustMe on 7/31/2017.
 */
public class AiTrainingResult implements Comparator<AiTrainingResult>, Comparable<AiTrainingResult> {
    PriorityProfile profile;
    AiTrainingParameters parameters;
    AiTrainingCriteria criteria;

    UnitStats unitStats;
    PlayerStats allyStats;
    PlayerStats enemyStats;

    Float value;

    public float evaluate() {
        float result = 0;
        for (Pair<CRITERIA_TYPE_NUMERIC, Float> sub : criteria.numericCriteria) {
            result += evalNumeric(sub.getKey(), sub.getValue());
        }
        return result;
    }

    private float evalNumeric(CRITERIA_TYPE_NUMERIC key, Float value) {
        STAT stat = null;
        switch (key) {
            default:
                stat = AiTrainingCriteria.getStatForCriteria(key);
        }
        return unitStats.getStatMap().get(stat) * value;
    }





    public Float getValue() {
        if (value==null ){
            value = evaluate();
        }
        return value;
    }


    @Override
    public int compareTo(AiTrainingResult o) {

        if (this.getValue() > o.getValue()) {
            return 1;
        } else if (this.getValue() < o.getValue()) {
            return -1;
        }
        return 0;
    }

    @Override
    public int compare(AiTrainingResult o1, AiTrainingResult o ) {
        if (o1.getValue() > o.getValue()) {
            return 1;
        } else if (o1.getValue() < o.getValue()) {
            return -1;
        }
        return 0;
    }
    public PriorityProfile getProfile() {
        return profile;
    }

    public AiTrainingParameters getParameters() {
        return parameters;
    }

    public AiTrainingCriteria getCriteria() {
        return criteria;
    }

    public UnitStats getUnitStats() {
        return unitStats;
    }

    public PlayerStats getAllyStats() {
        return allyStats;
    }

    public PlayerStats getEnemyStats() {
        return enemyStats;
    }


}
