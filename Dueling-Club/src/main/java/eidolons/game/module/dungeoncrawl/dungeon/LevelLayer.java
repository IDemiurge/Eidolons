package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.system.auxiliary.EnumMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/20/2018.
 */
public abstract class LevelLayer<T> {
    protected String name;
    protected List<T> subParts = new ArrayList<>();
    protected AMBIENCE ambience;
    protected COLOR_THEME colorTheme;
    protected int globalIllumination;
    protected COLOR_THEME altColorTheme;

    protected  LevelStructure.StructureData data;

    public LevelLayer() {
    }

    public LevelLayer(List<T> subParts, AMBIENCE ambience, COLOR_THEME colorTheme, int globalIllumination) {
        this.subParts = subParts;
        this.ambience = ambience;
        this.colorTheme = colorTheme;
        this.globalIllumination = globalIllumination;
    }

    public int getGlobalIllumination() {
        return globalIllumination;
    }

    public void setGlobalIllumination(int globalIllumination) {
        this.globalIllumination = globalIllumination;
    }

    public abstract String toXml();

    public List<T> getSubParts() {
        return subParts;
    }

    public AMBIENCE getAmbience() {
//        zone.setStyle( new EnumMaster<DungeonEnums.DUNGEON_STYLE>().retrieveEnumConst(DungeonEnums.DUNGEON_STYLE.class,
//                getValue(ZoneData.ZONE_VALUE.style)));
        return  new EnumMaster<AMBIENCE>().retrieveEnumConst(AMBIENCE.class,
                data.getValue("AMBIENCE"));
    }

    public COLOR_THEME getColorTheme() {
        return new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                data.getValue("COLOR_THEME"));
    }

    public COLOR_THEME getAltColorTheme() {

       return new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
               data.getValue("ALT_COLOR_THEME"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LevelStructure.StructureData getData() {
        return data;
    }

    public void setData(LevelStructure.StructureData data) {
        this.data = data;
    }

    public void setAltColorTheme(COLOR_THEME c) {
        getData().setValue("alt_color_theme", c.toString());
    }
    public void setColorTheme(COLOR_THEME c) {
        getData().setValue("color_theme", c.toString());
    }
}
