package eidolons.game.module.dungeoncrawl.quest.advanced;

import eidolons.game.battlecraft.logic.meta.scenario.Objective;
import eidolons.game.module.dungeoncrawl.quest.QuestReward;
import main.entity.type.ObjType;

import java.util.List;
import java.util.Map;

public class ChainQuest implements Quest{

    //support failure? branching?

    Map< Quest, Interim> chain;

    Quest current;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getProgressText() {
        return null;
    }

    @Override
    public QuestReward getReward() {
        return null;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void setStarted(boolean started) {

    }

    @Override
    public boolean isRewardTaken() {
        return false;
    }

    @Override
    public void setRewardTaken(boolean rewardTaken) {

    }

    private class Interim extends QuestReward {
        public Interim(ObjType objType) {
            super(objType);
        }

        String message;
        //conditional?

    }
}
