package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxColorMaster;
import main.content.values.parameters.PARAMETER;

import java.util.function.Supplier;

public class SoulParamsBar extends SpriteParamBar {

    public SoulParamsBar(Supplier<BattleFieldObject> supplier) {
        super(supplier);
    }

    @Override
    protected String getBarImagePath(boolean b) {
        return super.getBarImagePath(b);
    }

    ////TODO we will need different for over/under!

    @Override
    protected Color getColor(boolean over) {
        return over? GdxColorMaster.CYAN : GdxColorMaster.PURPLE;
    }
    @Override
    protected PARAMETER getOverParam(boolean current) {
        return current? PARAMS.C_FOCUS : PARAMS.FOCUS;
    }

    @Override
    protected PARAMETER getUnderParam(boolean current) {
        return current? PARAMS.C_ESSENCE : PARAMS.ESSENCE;
    }
}
