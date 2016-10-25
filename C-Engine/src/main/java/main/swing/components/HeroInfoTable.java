package main.swing.components;

import main.content.VALUE;
import main.content.parameters.MACRO_PARAMS;
import main.content.properties.G_PROPS;
import main.entity.obj.Obj;

public class HeroInfoTable extends InfoTable {

    public HeroInfoTable(Obj obj) {
        super(obj);
    }

    @Override
    protected void initValues() {
        valueTable = new VALUE[][]{
                {G_PROPS.NAME},
                // loyalty
                // ++ health %
                {MACRO_PARAMS.CONSUMPTION, MACRO_PARAMS.FATIGUE},
                {MACRO_PARAMS.TRAVEL_SPEED, MACRO_PARAMS.EXPLORE_SPEED},

        };

    }

    @Override
    public void initSize() {
        size = 13;
        rows = 2;
        compWidth = 64;
        compHeight = 32;
    }

}
