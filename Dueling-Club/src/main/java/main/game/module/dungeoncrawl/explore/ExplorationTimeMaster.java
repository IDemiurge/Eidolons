package main.game.module.dungeoncrawl.explore;

import main.content.PARAMS;
import main.entity.active.DC_ActiveObj;
import main.game.battlecraft.ai.UnitAI;
import main.system.auxiliary.TimeMaster;
import main.system.datatypes.DequeImpl;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationTimeMaster extends ExplorationHandler {
    public static final float secondsPerAP = 2f;
    private float time=0;

    public ExplorationTimeMaster(ExplorationMaster master) {
        super(master);
    }

    public void aiActionActivated(UnitAI ai, DC_ActiveObj activeObj) {
//        int time = getTimeForAction(activeObj);
//        ai.setExplorationTimePassed(ai.getExplorationTimePassed() + time);
        ai.setExplorationTimeOfLastAction(time);
    }
        public void playerActionActivated(DC_ActiveObj activeObj) {
        int time = getTimeForAction(activeObj);
        DequeImpl<UnitAI> aiList = master.getAiMaster().getActiveUnitAIs();
        aiList.forEach(ai -> ai.setExplorationTimePassed(ai.getExplorationTimePassed() - time));
        master.getAiMaster().checkAiActs();
    }

    public String getDisplayedTime( ) {
        return TimeMaster.getMinutes((long) time) + ":" + TimeMaster.getSeconds((long) time);
//        return TimeMaster.getFormattedTime((long) time, true, false);
    }
        public int getTimeForAction(DC_ActiveObj activeObj) {
        //speed factor of the unit?
        return (int) Math.round(secondsPerAP * activeObj.getParamDouble(PARAMS.AP_COST));
    }

    public int getTimePassedSinceAiActions(UnitAI ai) {
        return (int)(time - ai.getExplorationTimeOfLastAction());
//        return ai.getExplorationTimePassed();
    }

    public void act(float delta) {
        time+=delta;
    }

    public void checkTimedEvents() {
        master.getAiMaster().checkAiActs();



    }

    public float getTime() {
        return time;
    }
}
