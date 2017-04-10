package main.game.ai.tools;

import main.content.PARAMS;
import main.content.enums.system.AiEnums;
import main.entity.Entity;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.tools.future.FutureBuilder;
import main.game.ai.tools.priority.ParamPriorityAnalyzer;
import main.game.logic.generic.DC_ActionManager;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;
import main.system.math.PositionMaster;

import java.util.List;

/**
 * Created by JustMe on 4/9/2017.
 */
public class SituationAnalyzer extends AiHandler{

    public SituationAnalyzer(AiHandler master) {
        super(master);
    }

    public int getMeleeDangerFactor(Unit unit) {
        return getMeleeDangerFactor(unit, true, true);
    }

    public int getMeleeDangerFactor(Unit unit, boolean adjacentOnly, boolean now) {
        List<? extends Entity> units = (!adjacentOnly) ? Analyzer.getAdjacentEnemies(unit, false)
         : Analyzer.getMeleeEnemies(unit);
        int factor = 0;
        for (Entity e : units) {
            Unit enemy = (Unit) e;
            int meleeThreat = getMeleeThreat(enemy, now);
            factor += meleeThreat;
            LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Melee threat " + meleeThreat + " from " + enemy.getName());
        }

        int mod = 125 - ParamPriorityAnalyzer.getUnitLifeFactor(unit);
        LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Melee threat mod " + mod + " for " + unit.getName());

        if (mod != 0) {
            factor = factor * mod / 100;
        }
        LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Melee threat factor " + factor + " for " + unit.getName());

        return factor;
    }

    
    public int getMeleeThreat(Unit enemy) {
        return getMeleeThreat(enemy, true);
    }

    
    public int getMeleeThreat(Unit enemy, boolean now) {
        if (now) {
            if (!enemy.canActNow() || !enemy.canAttack()) {
                return 0;
            }
        }
        int threat = 0;
        int factor = 1;
        DC_UnitAction attack = enemy.getAction(DC_ActionManager.ATTACK);
        if (attack == null) {
            return 0;
        }
        if (now) {
            attack.initCosts();
            try {
                int ap_factor = enemy.getIntParam(PARAMS.C_N_OF_ACTIONS)
                 / attack.getCosts().getCost(PARAMS.C_N_OF_ACTIONS).getPayment()
                 .getAmountFormula().getInt(enemy.getRef());
                int sta_factor = enemy.getIntParam(PARAMS.C_STAMINA)
                 / attack.getCosts().getCost(PARAMS.C_STAMINA).getPayment()
                 .getAmountFormula().getInt(enemy.getRef());
                int foc_factor = enemy.getIntParam(PARAMS.C_FOCUS)
                 / attack.getCosts().getCost(PARAMS.C_FOCUS).getPayment().getAmountFormula()
                 .getInt(enemy.getRef());
                factor = Math.min(sta_factor, ap_factor);
                factor = Math.min(factor, foc_factor);// extract to
                // getTimesActivate()
                // TODO
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        threat = FutureBuilder.precalculateDamage(attack, unit, true) * factor;

        // special attacks? dual wielding?

        int distance = 1 + PositionMaster.getDistance(unit, enemy);
        threat /= distance;

        return threat;
    }

    
    public int getCastingPriority(Unit unit) {
        UnitAI unit_ai = unit.getAI();
        if (!Analyzer.hasSpells(unit)) {
            return 0;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.CASTER) {
            return 200;
        }
        // per spell? TODO

        if (unit_ai.getType() == AiEnums.AI_TYPE.BRUTE) {
            return 25;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.ARCHER) {
            return 35;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.TANK) {
            return 50;
        }
        return 100;
    }

}
