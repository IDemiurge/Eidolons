package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelZone extends LevelLayer<LevelBlock>{

    AMBIENCE ambience;
    COLOR_THEME colorTheme;
    int globalIllumination;

    @Override
    public String toXml() {
        return null;
    }
}
