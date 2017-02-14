package main.game.ai.logic.types.brute;

import main.game.ai.AI_Logic;
import main.game.ai.logic.ActionTypeManager;
import main.game.ai.logic.Analyzer.ACTION_CASES;

import java.util.HashMap;
import java.util.Map;

public class BruteActionMaster extends ActionTypeManager {

    private static final ACTION_TYPES DEFAULT_CASE = ACTION_TYPES.APPROACH;
    ACTION_CASES[] DEFAULT_ACTION_CASE_PRIORITIES = {
            // ACTION_CASES.FREE_KILL,
            // ACTION_CASES.HIT_TO_KILL,
            // ACTION_CASES.MOVE_TO_KILL,
            // ACTION_CASES.MOVE_TO_HIT,
            ACTION_CASES.HIT};
    Map<ACTION_CASES, ACTION_TYPES> caseActionMap = new HashMap<>();

    public BruteActionMaster(AI_Logic ai) {
        super(ai);
        initCaseActionMap();

    }

    @Override
    public ACTION_TYPES getAction() {
        ACTION_TYPES action = checkCases();
        return action;
    }

    private ACTION_TYPES checkCases() {
        for (ACTION_CASES CASE : DEFAULT_ACTION_CASE_PRIORITIES) {
            if (getAnalyzer().checkActionCase(CASE)) {
                return caseActionMap.get(CASE);
            }
        }

        return DEFAULT_CASE;
    }

    private void initCaseActionMap() {
        for (ACTION_CASES CASE : ACTION_CASES.values()) {

            ACTION_TYPES ACTION = null;
            switch (CASE) {
                case MOVE_TO_CLAIM:
                    ACTION = ACTION_TYPES.CLAIM;
                    break;

                case MOVE_TO_DECLAIM:
                    ACTION = ACTION_TYPES.DECLAIM;
                    break;
                case HIT_TO_KILL:
                    ACTION = ACTION_TYPES.ATTACK;
                    break;
                case MOVE_TO_HIT:
                    ACTION = ACTION_TYPES.CLOSE_IN;
                    break;
                case MOVE_TO_ESCAPE:
                    ACTION = ACTION_TYPES.ESCAPE;
                    break;
                case MOVE_TO_KILL:
                    ACTION = ACTION_TYPES.CLOSE_IN;
                    break;
                case HIT:
                    ACTION = ACTION_TYPES.ATTACK;
                    break;

                default:
                    break;

            }

            caseActionMap.put(CASE, ACTION);

        }
    }
}
