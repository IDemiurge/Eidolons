package main.game.battlecraft.logic.meta.tutorial;

import main.data.filesys.PathFinder;
import main.game.battlecraft.logic.meta.universal.MetaDataManager;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TutorialMetaDataManager extends MetaDataManager<TutorialMeta> {
    public static final String TUTORIAL_DATA_PATH ="tutorial" ;

    public TutorialMetaDataManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    public String getDataPath() {
        return PathFinder.getTextPath()+ TUTORIAL_DATA_PATH;
    }

}
