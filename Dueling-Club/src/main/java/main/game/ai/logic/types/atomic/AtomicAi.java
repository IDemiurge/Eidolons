package main.game.ai.logic.types.atomic;

import main.content.enums.system.AiEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.ai.tools.Analyzer;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DC_MovementManager;
import main.game.battlefield.FacingMaster;
import main.system.math.PositionMaster;

import java.util.List;

public class AtomicAi {
    public static Action getAtomicAction(UnitAI ai) {
        ATOMIC_LOGIC logic = getAtomicLogic(ai);
        List<Unit> enemies = Analyzer.getVisibleEnemies(ai);
        List<Unit> allies = Analyzer.getAllies(ai);
        int greatest = 0;
        Coordinates pick = null;
        for (Coordinates c : ai.getUnit().getCoordinates().getAdjacentCoordinates()) {
            int i = 0;
            i += getCellPriority(c, ai);
            for (Unit a : allies) {
                i += getAllyPriority(c, a, ai, logic);
            }
            for (Unit e : enemies) {
                i += getEnemyPriority(c, e, ai, logic);
            }
            i = i * getCellPriorityMod(c, ai) / 100;
            if (i > greatest) {
                greatest = i;
                pick = c;
            }
        }
        Action action = null;
        try {
            action = DC_MovementManager.getFirstAction(ai.getUnit(), pick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return action;
    }

    private static int getCellPriority(Coordinates c, UnitAI ai) {
        return 0;
    }

    private static int getCellPriorityMod(Coordinates c, UnitAI ai) {
        int mod = 100;
        if (FacingMaster
                .getSingleFacing(ai.getUnit().getFacing(), ai.getUnit().getCoordinates(), c) == UnitEnums.FACING_SINGLE.IN_FRONT) {
            mod += 35;
        }
        if (FacingMaster
                .getSingleFacing(ai.getUnit().getFacing(), ai.getUnit().getCoordinates(), c) == UnitEnums.FACING_SINGLE.BEHIND) {
            mod += -50;
        }
        if (PositionMaster.inLine(ai.getUnit().getCoordinates(), c)) {
            if (ai.getType() == AiEnums.AI_TYPE.BRUTE) {
                mod += 25;
            }
            mod += 35;
        }
        return mod;
    }

    private static int getEnemyPriority(Coordinates c, Unit e, UnitAI ai, ATOMIC_LOGIC logic) {
        if (logic == ATOMIC_LOGIC.GEN_AGGRO || logic == ATOMIC_LOGIC.GROUP_AGGRO) {
            return DC_PriorityManager.getUnitPriority(ai, e, null)
                    / (1 + PositionMaster.getDistance(e.getCoordinates(), c));
        }
        return 0;
    }

    private static int getAllyPriority(Coordinates c, Unit a, UnitAI ai, ATOMIC_LOGIC logic) {
        if (logic == ATOMIC_LOGIC.PROTECT || logic == ATOMIC_LOGIC.GROUP_AGGRO) {

        }
        return 0;
    }

    private static ATOMIC_LOGIC getAtomicLogic(UnitAI ai) {
        return ATOMIC_LOGIC.GEN_AGGRO;
    }

    public enum ATOMIC_LOGIC {
        GEN_AGGRO, GROUP_AGGRO, RETREAT, PROTECT, FORMATION,
    }
}