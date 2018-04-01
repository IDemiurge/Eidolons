package eidolons.game.battlecraft.ai.advanced.machine.train;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.machine.PriorityProfile;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingCriteria.CRITERIA_TYPE_NUMERIC;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager.STAT;
import eidolons.game.battlecraft.logic.battle.universal.stats.PlayerStats;
import eidolons.game.battlecraft.logic.battle.universal.stats.UnitStats;
import eidolons.game.core.game.DC_Game;
import main.entity.Ref;
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

    public AiTrainingResult(PriorityProfile profile, AiTrainingParameters parameters, AiTrainingCriteria criteria) {
        this.profile = profile;
        this.parameters = parameters;
        this.criteria = criteria;
    }

    public void construct() {
        DC_Game game = DC_Game.game;
        Unit unit = game.getMaster().getUnitByName(parameters.getTraineeType().getName(), new Ref());
        unitStats = game.getBattleMaster().getStatManager().getStats().getUnitStatMap().get(unit);
        allyStats = game.getBattleMaster().getStatManager().getStats().
         getPlayerStats(unit.getOwner());
        allyStats = game.getBattleMaster().getStatManager().getStats().
         getPlayerStats(game.getPlayer(!unit.getOwner().isMe()));
    }

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
                try {
                    stat = AiTrainingCriteria.getStatForCriteria(key);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
        }
        try {
            return unitStats.getStatMap().get(stat) * value;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return 0;
    }


    public Float getValue() {
        if (value == null) {
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
    public int compare(AiTrainingResult o1, AiTrainingResult o) {
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
