package eidolons.libgdx.bf.datasource;

import main.system.data.DataUnit;

public class GraphicData extends DataUnit<GraphicData.GRAPHIC_VALUE> {
    public GraphicData(String text) {
        super(text);
    }

    @Override
    protected String getSeparator() {
//        if (dialogue) return Pattern.quote("|");
        return super.getSeparator();
    }

    @Override
    protected String getPairSeparator() {
//        return Pattern.quote("::");
        return super.getPairSeparator();
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
