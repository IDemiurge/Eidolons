package eidolons.game.battlecraft.logic.meta.universal;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MetaDataManager<E extends MetaGame> extends MetaGameHandler<E> {

    public MetaDataManager(MetaGameMaster master) {
        super(master);
    }

    public String getDataPath() {
        return null;
    }

    public String getMissionName() {
        return null;
    }
}
