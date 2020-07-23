package eidolons.libgdx.bf.decor;

import eidolons.libgdx.bf.datasource.GraphicData;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;

import java.util.ArrayList;
import java.util.List;

public class DecorData extends DataUnit<DecorData.DECOR_LEVEL> {

    public DecorData(String data) {
        super(data);
    }

    @Override
    public Class<? extends DECOR_LEVEL> getEnumClazz() {
        return DECOR_LEVEL.class;
    }

    @Override
    protected String getSeparator() {
        return super.getSeparator();
    }

    @Override
    protected String getPairSeparator() {
        return super.getPairSeparator();
    }

    public List<GraphicData> getGraphicData(DECOR_LEVEL level) {
        List<GraphicData> list = new ArrayList<>();
        for (String substring : ContainerUtils.openContainer(getValue(level))) {
            list.add(new GraphicData(substring, false));
        }
        return list;
    }


    public enum DECOR_LEVEL {
        LOWEST,
        BOTTOM,
        OVER_CELLS,
        OVER_MAPS,
        TOP,

    }
}
