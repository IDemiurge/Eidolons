package eidolons.game.module.dungeoncrawl.quest.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.quest.QuestReward;
import eidolons.macro.entity.town.Town;
import main.entity.obj.Obj;
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

    void setTown(Town town);

    Coordinates getCoordinate();
}
