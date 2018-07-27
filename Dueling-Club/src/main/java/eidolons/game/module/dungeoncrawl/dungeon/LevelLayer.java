package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/20/2018.
 */
public abstract class LevelLayer<T> {
    List<T> subParts=    new ArrayList<>() ;

    AMBIENCE ambience;
    COLOR_THEME colorTheme;
    int globalIllumination;

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

    public COLOR_THEME getColorTheme() {
        return colorTheme;
    }

    public int getGlobalIllumination() {
        return globalIllumination;
    }

    public void setAmbience(AMBIENCE ambience) {
        this.ambience = ambience;
    }

    public void setColorTheme(COLOR_THEME colorTheme) {
        this.colorTheme = colorTheme;
    }

    public void setGlobalIllumination(int globalIllumination) {
        this.globalIllumination = globalIllumination;
    }
}
