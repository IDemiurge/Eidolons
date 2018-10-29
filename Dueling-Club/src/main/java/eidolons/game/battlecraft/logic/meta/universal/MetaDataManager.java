package eidolons.game.battlecraft.logic.meta.universal;

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
        return missionPath;
    }

    public void setMissionPath(String missionPath) {
        this.missionPath = missionPath;
    }
}
