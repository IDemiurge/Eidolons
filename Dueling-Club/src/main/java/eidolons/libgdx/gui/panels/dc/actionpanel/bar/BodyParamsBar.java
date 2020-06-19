package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxColorMaster;
import main.content.values.parameters.PARAMETER;

import java.util.function.Supplier;

public class BodyParamsBar extends SpriteParamBar {


    public BodyParamsBar(Supplier<BattleFieldObject> supplier) {
        super(supplier);
    }

    @Override
    protected PARAMETER getOverParam(boolean current) {
        return current? PARAMS.C_TOUGHNESS : PARAMS.TOUGHNESS;
    }

    @Override
    protected PARAMETER getUnderParam(boolean current) {
        return current? PARAMS.C_ENDURANCE : PARAMS.ENDURANCE;
    }

    @Override
    protected Color getColor(boolean over) {
        return over? GdxColorMaster.GREEN : GdxColorMaster.CRIMSON;
    }


    @Override
    public Color getTeamColor() {
        return super.getTeamColor();
    }
}
