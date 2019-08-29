package eidolons.libgdx.bf.datasource;

import main.system.data.DataUnit;

import java.util.regex.Pattern;

public class GraphicData extends DataUnit<GraphicData.GRAPHIC_VALUE> {
    public GraphicData(String text) {
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
    public Class<? extends GRAPHIC_VALUE> getEnumClazz() {
        return GRAPHIC_VALUE.class;
    }

    public enum GRAPHIC_VALUE{
        x, y, dur, scale, rotation, flipX, flipY, color, alpha,
        blending,  interpolation
    }
}
