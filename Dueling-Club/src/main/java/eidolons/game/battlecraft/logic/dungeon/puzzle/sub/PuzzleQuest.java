package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.module.dungeoncrawl.quest.QuestReward;
import eidolons.game.module.dungeoncrawl.quest.advanced.ChainQuest;
import main.system.auxiliary.StringMaster;

public class PuzzleQuest extends ChainQuest {
    private int counter;
    Puzzle puzzle;
    PuzzleData data;


    public PuzzleQuest(Puzzle puzzle, PuzzleData data) {
        this.puzzle = puzzle;
        this.data = data;

        //from type?  sync with rules / resolution
    }

    @Override
    public String getProgressText() {
       int n = puzzle.getCountersMax();

        return super.getProgressText();
    }
//    private String getNumberTooltip() {
//        if (getNumberRequired() == 0) {
//            return null;
//        }
//        return StringMaster.wrapInBraces(
//                numberAchieved + " / " +
//                        numberRequired);
//    }
    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public QuestReward getReward() {
        return super.getReward();
    }

    public boolean decrementCounter() {
        counter--;
        if (counter<=0)
            return false;
        return true;
    }
}
