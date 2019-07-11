package eidolons.game.module.dungeoncrawl.quest.advanced;

import eidolons.game.module.dungeoncrawl.quest.QuestReward;

public interface Quest {


    String getTitle();

    String getDescription();

    String getProgressText();

    QuestReward getReward();

    String getImage();

    boolean isStarted();

    void setStarted(boolean started);

    boolean isRewardTaken();

    void setRewardTaken(boolean rewardTaken);
}
