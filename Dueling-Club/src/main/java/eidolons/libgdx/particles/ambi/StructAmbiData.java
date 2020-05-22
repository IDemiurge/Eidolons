package eidolons.libgdx.particles.ambi;

import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.Soundscape;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;

public class StructAmbiData {
    //for each block?
    /*
    new way of managing VFX
    can we afford to init on entire module?
    are we discarding STYLE?
    it's what we're using for placeholders... and as general container of this all
     */

    public StructAmbiData(LevelStruct struct) {
        this.struct = struct;
    }

    LevelStruct struct;

    MusicMaster.MUSIC_THEME theme;
    MusicMaster.AMBIENCE ambience;
    AmbienceDataSource.VFX_TEMPLATE template;
    CONTENT_CONSTS.COLOR_THEME colorTheme;
    Soundscape.SOUNDSCAPE soundscape;
    DungeonEnums.DUNGEON_STYLE style;
    int illumination;

    public LevelStruct getStruct() {
        return struct;
    }

    public MusicMaster.MUSIC_THEME getTheme() {
        if (theme == null) {
            theme = struct.getMusicTheme();
        }
        return theme;
    }

    public MusicMaster.AMBIENCE getAmbience() {
        if (ambience == null) {
            ambience = struct.getAmbience();
        }

        return ambience;
    }

    public DungeonEnums.DUNGEON_STYLE getStyle() {

        if (style == null) {
            style = struct.getStyle();
        }
        return style;
    }


    public AmbienceDataSource.VFX_TEMPLATE getTemplate() {
        if (template == null) {
            template = struct.getVfx();
        }
        return template;
    }

    public CONTENT_CONSTS.COLOR_THEME getColorTheme() {

        if (colorTheme == null) {
            colorTheme = struct.getColorTheme();
        }
        return colorTheme;
    }

    public Soundscape.SOUNDSCAPE getSoundscape() {
        if (soundscape == null) {
            soundscape = struct.getSoundscape();
        }
        return soundscape;
    }

    public int getIllumination() {
        return struct.getData().getIntValue("illumination");
    }
}
