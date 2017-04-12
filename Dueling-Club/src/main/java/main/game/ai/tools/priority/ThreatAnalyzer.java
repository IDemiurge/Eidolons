package main.game.ai.tools.priority;

import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.generic.AiHandler;

import java.util.Map;

/**
 * Created by JustMe on 4/11/2017.
 */
public class ThreatAnalyzer extends AiHandler{

    public enum THREAT_TYPE{
        RANGED,
        MELEE,
        MAGIC,
        GRUDGE,
        PRIORITY,
        POWER
    }
    Map<UnitAI, Map<Unit, Integer>> threatMemoryMap;
    Map<UnitAI, Map<Unit, Integer>> grudgeMemoryMap;

    public ThreatAnalyzer(AiHandler master) {
        super(master);
    }

    public int getThreat(UnitAI ai, Unit enemy) {

        return 0;
    }

    public int getRangedDangerFactor(Unit unit) {

        return 0;
    }
}
