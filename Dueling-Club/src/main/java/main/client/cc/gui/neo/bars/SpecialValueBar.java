package main.client.cc.gui.neo.bars;

import main.content.ContentManager;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.rules.rpg.IntegrityRule;
import main.swing.components.panels.ValueComp;
import main.system.DC_Formulas;
import main.system.math.MathMaster;

import java.awt.*;

public class SpecialValueBar extends ValueComp {

    public SpecialValueBar(boolean upsideDown, PARAMS p) {
        super(upsideDown ? VISUALS.VALUE_BAR_UPSIDEDOWN : VISUALS.VALUE_BAR, p);

        setPanelSize(getVisuals().getSize());
    }

    // xp_next_level, max_integrity (?)
    protected boolean isVertical() {
        return false;
    }

    public int getOffsetX() {
        return 42;
    }

    @Override
    protected String getCompPath() {
        return "UI\\components\\new\\bars\\bar comp ";
    }

    protected int getTextOffsetY() {
        return 4;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    protected Integer initPercentage() {
        if (param == PARAMS.XP) {
            c_val = getObj().getIntParam(PARAMS.TOTAL_XP);
            max_val = DC_Formulas.getTotalXpForLevel(getObj().getIntParam(PARAMS.LEVEL) + 1);
            return MathMaster.MULTIPLIER * c_val / max_val;

        } else if (param == PARAMS.INTEGRITY) {
            c_val = getObj().getIntParam(PARAMS.INTEGRITY);
            max_val = IntegrityRule.getMaxIntegrityValue();
            return MathMaster.MULTIPLIER * c_val / max_val;
        }

        return super.initPercentage();
    }

    protected PARAMETER getPercentageParam() {
        return null;
    }

    protected PARAMETER getCurrentParam() {
        return ContentManager.getCurrentParam(param);
    }

}
