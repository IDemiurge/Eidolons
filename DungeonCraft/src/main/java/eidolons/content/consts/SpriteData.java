package eidolons.content.consts;

import main.system.data.DataUnit;

import java.util.regex.Pattern;

public class SpriteData extends DataUnit<SpriteData.SPRITE_VALUE> {
    public SpriteData(String text) {
        super(text);
    }

    @Override
    protected String getSeparator() {
        return Pattern.quote("|");
    }

    @Override
    protected String getPairSeparator() {
        return Pattern.quote("::");
    }

    @Override
    public Class<? extends SPRITE_VALUE> getEnumClazz() {
        return SPRITE_VALUE.class;
    }

    public enum SPRITE_VALUE{
        fps, playMode, backAndForth, loops,
        x, y, scale, rotation, flipX, flipY, color, alpha,
        blending
    }
}
