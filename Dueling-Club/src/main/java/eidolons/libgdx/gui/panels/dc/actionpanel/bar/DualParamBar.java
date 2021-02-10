package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.overlays.bar.ValueBar;
import eidolons.libgdx.gui.ScissorMaster;
import main.content.values.parameters.PARAMETER;
import main.system.math.MathMaster;

import java.util.function.Supplier;

public abstract class DualParamBar extends ValueBar {

    protected Supplier<BattleFieldObject> supplier;
    protected BattleFieldObject dataSource;
    protected boolean underIsGreater;

    public DualParamBar(BattleFieldObject dataSource) {
        this.dataSource = dataSource;
    }

    public DualParamBar(Supplier<BattleFieldObject> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected void initRegions() {
        super.initRegions();
        barBg2.setVisible(false);
    }

    protected boolean isDisplayedAlways() {
        return true;
    }

    @Override
    public void animateChange(boolean smooth) {
        setVisible(true);
        if (!smooth) {
            displayedSecondaryPerc = getSecondaryPerc();

            float realPerc = (float) (100 * getDataSource().getIntParam(getUnderParam(true)) /
                    getDataSource().getIntParam(getUnderParam(false))) / 100f;
            {
                displayedSecondaryPerc = realPerc;
            }
            displayedPrimaryPerc = getPrimaryPerc();

            realPerc = (float) (100 * getDataSource().getIntParam(getOverParam(true)) /
                    getDataSource().getIntParam(getOverParam(false))) / 100f;
            {
                displayedPrimaryPerc = realPerc;
            }

            return;
        }
        if (getDataSource().getIntParam(getUnderParam(true)) > getDataSource().getIntParam(getOverParam(true))) {
            underIsGreater = true;
        }
        super.animateChange(smooth);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isIgnored())
            return;
        if (fluctuatingAlpha != 1)
            batch.setColor(new Color(1, 1, 1, 1));
        if (label2.isVisible()) {
            label2.getWidth();
        }
        super.draw(batch, parentAlpha);
        float p = MathMaster.minMax(displayedSecondaryPerc, 0, 1);
        float y = getY();
        // if (isGradientMask()) {
        //     if (p < 1) {
        //         float x = getX() - (innerWidth * (1 - p));
        //         ScissorMaster.drawWithAlphaMask(this, batch, x, y, innerWidth, height,
        //                 () -> drawBar(underBarRegion, batch, secondaryColor, y),
        //                 Textures.HOR_GRADIENT_72);
        //     } else {
        //         drawBar(underBarRegion, batch, secondaryColor, y);
        //     }
        //     p = Math.min(p, displayedPrimaryPerc);
        //     if (p < 1) {
        //         float x = getX() - (innerWidth * (1 - p));
        //         ScissorMaster.drawWithAlphaMask(this, batch, x, y, innerWidth, height,
        //                 () -> drawBar(overBarRegion, batch, primaryColor, y),
        //                 Textures.HOR_GRADIENT_72);
        //     } else {
        //         drawBar(overBarRegion, batch, primaryColor, y);
        //     }
        //     return;
        // }
        //UNDER
        if (p > 0.01f) {
            ScissorMaster.drawInRectangle(this, batch, getX(),
                    y,
                    innerWidth * p,
                    height, () ->
                            drawBar(underBarRegion, batch, secondaryColor, y));
        } else {
            //draw something dramatic to show it's ALMOST ZERO
        }
        //OVER
        float min = Math.min(p, displayedPrimaryPerc);

        if (min > 0.01f)
            ScissorMaster.drawInRectangle(this, batch, getX(),
                    y, innerWidth * min,
                    height, () ->
                            drawBar(overBarRegion, batch, primaryColor, y));

    }

    private boolean isGradientMask() {
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isLabelsDisplayed()) {
            if (!label2.isVisible()) {
                label1.setVisible(true);
                label2.setVisible(true);
                label1.setZIndex(Integer.MAX_VALUE);
                label2.setZIndex(Integer.MAX_VALUE);
            }
        } else {
            label1.setVisible(false);
            label2.setVisible(false);
        }
        if (isAnimated() && getActions().size > 0) {
            displayedSecondaryPerc =
                    secondaryAction.getValue();
            displayedPrimaryPerc =
                    primaryAction.getValue();

            if (!getDataSource().isPlayerCharacter())
                fullLengthPerc = displayedSecondaryPerc;
            else {
                fullLengthPerc = displayedPrimaryPerc;
            }
        } else if (!dirty)
            return;
        dirty = false;

    }

    protected boolean isLabelsDisplayed() {
        return Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);
    }

    @Override
    protected void setValues() {
        setPrimaryPerc(new Float(getDataSource().getIntParam(getOverParam(true)))
                / (getDataSource().getLastValidParamValue(getUnderParam(false)))); //not a bug - we want to display this way!
        setPrimaryPerc(MathMaster.getFloatWithDigitsAfterPeriod(2, getPrimaryPerc()));//  primaryPerc % 0.01f;
        setSecondaryPerc(new Float(getDataSource().getIntParam(getUnderParam(true))) / (getDataSource().getLastValidParamValue(getUnderParam(false))));
        setSecondaryPerc(MathMaster.getFloatWithDigitsAfterPeriod(2, getSecondaryPerc()));
    }

    protected abstract PARAMETER getOverParam(boolean current);

    protected abstract PARAMETER getUnderParam(boolean current);

    @Override
    protected void resetLabel() {
        String text = "" + Math.round(getDataSource().getIntParam(getUnderParam(true)))
                + "/" + getDataSource().getLastValidParamValue(getUnderParam(false));
        label1.setText(text);

        text = "" + (getDataSource().getIntParam(getOverParam(true)) //* displayedPrimaryPerc
        ) + "/" + getDataSource().getLastValidParamValue(getOverParam(false));
        label2.setText(text);
        //EA check - ?
        if (!getDataSource().isPlayerCharacter())
            fullLengthPerc = displayedSecondaryPerc;
        else {
            fullLengthPerc = displayedPrimaryPerc;
        }
        resetLabelPos();
    }

    protected void resetLabelPos() {
        label2.setPosition(0, height * 3 / 2);
        float offset = innerWidth * displayedSecondaryPerc / 2;
        label1.setPosition(offset, -height / 2);

        label1.setWidth(innerWidth);
        label2.setWidth(innerWidth);
        label1.setHeight(18);
        label2.setHeight(18);
    }

    public BattleFieldObject getDataSource() {
        if (supplier != null) {
            return supplier.get();
        }
        return dataSource;
    }
}
