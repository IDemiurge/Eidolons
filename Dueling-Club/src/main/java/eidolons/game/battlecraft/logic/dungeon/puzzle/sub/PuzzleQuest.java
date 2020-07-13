package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.module.dungeoncrawl.quest.QuestReward;
import eidolons.game.module.dungeoncrawl.quest.advanced.ChainQuest;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

public class PuzzleQuest extends ChainQuest {
    private int counter;
    private final int originalCounter;
    Puzzle puzzle;
    PuzzleData data;
    private QuestReward reward;

    public PuzzleQuest(Puzzle puzzle, PuzzleData data) {
        this.puzzle = puzzle;
        this.data = data;
        originalCounter = puzzle.getCountersMax();
        counter = puzzle.getCountersMax();
        GuiEventManager.trigger(GuiEventType.QUEST_STARTED, this);
        //from type?  sync with rules / resolution
    }

    @Override
    public String getDescription()
    {
        return  "";
    }

    private String getFriendlyTooltip(PuzzleEnums.PUZZLE_ACTION_BASE value) {
        switch (value) {
            case MOVE:
            case MOVE_AFTER:
                return "Moves";
            case ACTION:
                break;
            case FACING:
                return "Turn Actions";
        }
        return "Actions";
    }

    @Override
    public String getProgressText() {
        if (counter==0 && originalCounter==0) {
            return puzzle.getQuestText();
        }
        return  getFriendlyTooltip(puzzle.getData().getCounterActionBase()) + " left: " +
                StringMaster.wrapInBrackets(
                counter + " / " +
                        originalCounter);
    }

    @Override
    public String getTitle() {
        return puzzle.getTitle();
    }


    @Override
    public QuestReward getReward() {
        if (reward == null) {
            reward = createReward(puzzle);
        }
        return reward;
    }

    private QuestReward createReward(Puzzle puzzle) {
//        puzzle.getData().getValue()
        reward= new QuestReward("soulforce=" +puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE. SOULFORCE_REWARD));
        return reward;
    }

    public String getVictoryText() {
        return  "";
//                "Soulforce gained: " +         getReward().getValue(QuestReward.REWARD_VALUE.soulforceFormula);
    }
    public boolean decrementCounter() {
        counter--;

        GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, this);
        return counter > 0;
    }

    public void complete() {
        GuiEventManager.trigger(GuiEventType.QUEST_COMPLETED, this);
        GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, this);
//        getReward().award(Eidolons.getMainHero(), false, false);
    }
    @Override
    public boolean isComplete() {
        return puzzle.isSolved();
    }
    public void failed() {
        GuiEventManager.trigger(GuiEventType.QUEST_CANCELLED, this);

//        getReward().award(Eidolons.getMainHero(), false, true);
    }

    @Override
    public boolean isStarted() {
        return puzzle.isActive();
    }

    @Override
    public boolean isRewardTaken() {
        //TODO
        return super.isRewardTaken();
    }
}
