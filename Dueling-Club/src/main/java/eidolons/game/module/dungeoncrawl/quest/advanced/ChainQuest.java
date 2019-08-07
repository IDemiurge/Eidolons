package eidolons.game.module.dungeoncrawl.quest.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.meta.scenario.Objective;
import eidolons.game.module.dungeoncrawl.quest.QuestReward;
import eidolons.macro.entity.town.Town;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

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
    public Integer getNumberRequired() {
        return null;
    }

    @Override
    public Integer getNumberAchieved() {
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

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Object getArg() {
        return null;
    }

    @Override
    public void setArg( Object obj) {

    }

    @Override
    public void setTown(Town town) {

    }

    @Override
    public Coordinates getCoordinate() {
        return null;
    }

    private class Interim extends QuestReward {
        public Interim(ObjType objType) {
            super(objType);
        }

        String message;
        //conditional?

    }
}
