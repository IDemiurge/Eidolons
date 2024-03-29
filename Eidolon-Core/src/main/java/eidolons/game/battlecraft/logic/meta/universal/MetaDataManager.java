package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.core.launch.TestLaunch;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MetaDataManager<E extends MetaGame> extends MetaGameHandler<E> {

    private String missionPath;

    public MetaDataManager(MetaGameMaster master) {
        super(master);
    }

    public String getDataPath() {
        return null;
    }

    public String getMissionName() {
        return null;
    }

    public String getSoloDungeonPath() {
        TestLaunch testLaunch = EngineLauncher.getInstance().getCustomLaunch();
        if (testLaunch !=null ){
                main.system.auxiliary.log.LogMaster.important("*******Custom Launch xml path: " +
                        testLaunch.getValue(TestLaunch.TestValue.module));
                return testLaunch.getValue(TestLaunch.TestValue.module);
            }
        return missionPath;
    }

    public void setMissionPath(String missionPath) {
        this.missionPath = missionPath;
    }

    public void initData() {
    }

    public String nextMission() {
        return null;
    }
}
