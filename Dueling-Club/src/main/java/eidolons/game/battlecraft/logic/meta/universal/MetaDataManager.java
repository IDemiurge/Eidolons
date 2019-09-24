package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.battlecraft.logic.meta.igg.CustomLaunch;
import eidolons.libgdx.launch.MainLauncher;

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

    public String getMissionPath() {
            if (MainLauncher.getCustomLaunch()!=null ){
                main.system.auxiliary.log.LogMaster.important("*******Custom Launch xml path: " +
                        MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path));
                return MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path);
            }
        return missionPath;
    }

    public void setMissionPath(String missionPath) {
        this.missionPath = missionPath;
    }

    public void initData() {
    }
}
