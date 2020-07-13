package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.texture.Sprites;
import main.content.values.parameters.PARAMETER;

import java.util.function.Supplier;

public class SoulParamsBar extends SpriteParamBar {

    public SoulParamsBar(Supplier<BattleFieldObject> supplier) {
        super(supplier);
    }

    @Override
    protected Color getColor(boolean over) {
        if (!isColored())
            return GdxColorMaster.WHITE;
        return over ? GdxColorMaster.CYAN : GdxColorMaster.PURPLE;
    }

    @Override
    protected String getTooltipText() {
        // BattleFieldObject unit = supplier.get();
        return "Focus: "+label1.getText()+"\nEssence: "+label2.getText();
    }
    private boolean isColored() {
        return false;
    }

    @Override
    protected String getBarImagePath(boolean over) {
        if (!isColored())
            return over ? Sprites.SOULFORCE_BAR : Sprites.SOULFORCE_BAR_BG;
        return over ? Sprites.SOULFORCE_BAR_WHITE : Sprites.SOULFORCE_BAR_BG_WHITE;
    }

    protected int getLabelY() {
        return 110;
    }

    @Override
    protected PARAMETER getOverParam(boolean current) {
        return current ? PARAMS.C_FOCUS : PARAMS.FOCUS;
    }

    @Override
    protected PARAMETER getUnderParam(boolean current) {
        return current ? PARAMS.C_ESSENCE : PARAMS.ESSENCE;
    }
}
