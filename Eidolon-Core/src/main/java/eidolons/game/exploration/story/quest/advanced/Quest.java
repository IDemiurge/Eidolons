package eidolons.game.exploration.story.quest.advanced;

import eidolons.game.exploration.story.quest.QuestReward;
import main.game.bf.Coordinates;

public interface Quest {


    String getTitle();

    String getDescription();

    String getProgressText();

    Integer getNumberRequired();

    Integer getNumberAchieved();

    QuestReward getReward();

    String getImage();

    boolean isStarted();

    void setStarted(boolean started);

    boolean isRewardTaken();

    void setRewardTaken(boolean rewardTaken);

    boolean isComplete();

    Object getArg();

    void setArg( Object obj);

    Coordinates getCoordinate();

    void increment();
}
