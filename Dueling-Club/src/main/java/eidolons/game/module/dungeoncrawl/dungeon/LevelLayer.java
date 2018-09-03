package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/20/2018.
 */
public abstract class LevelLayer<T> {
    protected List<T> subParts = new ArrayList<>();
    protected AMBIENCE ambience;
    protected COLOR_THEME colorTheme;
    protected int globalIllumination;
    protected COLOR_THEME altColorTheme;

    public LevelLayer() {
    }

    public LevelLayer(List<T> subParts, AMBIENCE ambience, COLOR_THEME colorTheme, int globalIllumination) {
        this.subParts = subParts;
        this.ambience = ambience;
        this.colorTheme = colorTheme;
        this.globalIllumination = globalIllumination;
    }

    public abstract String toXml();

    public List<T> getSubParts() {
        return subParts;
    }

    public AMBIENCE getAmbience() {
        return ambience;
    }

    public void setAmbience(AMBIENCE ambience) {
        this.ambience = ambience;
    }

    public COLOR_THEME getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(COLOR_THEME colorTheme) {
        this.colorTheme = colorTheme;
    }

    public int getGlobalIllumination() {
        return globalIllumination;
    }

    public void setGlobalIllumination(int globalIllumination) {
        this.globalIllumination = globalIllumination;
    }

    public COLOR_THEME getAltColorTheme() {
        return altColorTheme;
    }

    public void setAltColorTheme(COLOR_THEME altColorTheme) {
        this.altColorTheme = altColorTheme;
    }
}
