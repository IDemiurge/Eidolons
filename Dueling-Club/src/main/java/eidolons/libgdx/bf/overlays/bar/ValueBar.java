package eidolons.libgdx.bf.overlays.bar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster;

public abstract class ValueBar extends SuperActor {
    protected  int innerWidth;
    protected  Image barBg;
    protected  Image barBg2;
    protected  Label label2;
    protected  Label label1;
    protected float fullLengthPerc = 1f;
    protected TextureRegion barBgRegion; //TODO can it be static?
    protected float height;
    protected boolean dirty;
    protected TextureRegion overBarRegion;
    protected TextureRegion underBarRegion;
    protected Color secondaryColor;
    protected Color primaryColor;
    protected  float displayedPrimaryPerc = 1f;
    protected  float displayedSecondaryPerc = 1f;
    protected  FloatActionLimited primaryAction = (FloatActionLimited) ActionMaster.getAction(FloatActionLimited.class);
    protected  FloatActionLimited secondaryAction = (FloatActionLimited) ActionMaster.getAction(FloatActionLimited.class);
    protected  boolean labelsDisplayed = true;
    protected Float primaryPerc = 1f;
    protected Float secondaryPerc = 1f;
    protected Float previousPrimaryPerc = 1f;
    protected Float previousSecondaryPerc = 1f;
    protected Float lastOfferedPrimary;
    protected Float lastOfferedSecondary;
    protected float offsetX;

    public ValueBar() {
        initRegions();
        label2 = new Label("", StyleHolder.
                getSizedLabelStyle(FontMaster.FONT.AVQ, getFontSize(false)));
        label1 = new Label("", StyleHolder.
                getSizedLabelStyle(FontMaster.FONT.AVQ, getFontSize(false)));
        addActor(label1);
        addActor(label2);

        initSizes();

    }

    protected void initRegions() {
        overBarRegion = TextureCache.getOrCreateR(getBarImagePath(true));
        underBarRegion = TextureCache.getOrCreateR(getBarImagePath(false));
        barBgRegion = TextureCache.getOrCreateR(getBarBgPath());
        barBg = new Image(barBgRegion);
        addActor(barBg);

        barBg2 = new Image(barBgRegion);
        addActor(barBg2);
        barBg2.setY(-barBg.getHeight() - 1);
    }
    protected void initSizes() {
        height = barBgRegion.getRegionHeight();
        innerWidth = underBarRegion.getRegionWidth();
        offsetX = barBgRegion.getRegionWidth() - overBarRegion.getRegionWidth();
    }

    protected abstract String getBarBgPath();

    protected Integer getFontSize(boolean prime) {
        return 18;
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
    }

    //make sure this isn't called all the time!
    public void reset() {
        //TODO queue animation?
//        main.system.auxiliary.log.LogMaster.log(1, this + ">>>  tries to reset " +
//         dataSource + " previousPrimaryPerc=" + previousPrimaryPerc + " previousSecondaryPerc=" + previousSecondaryPerc + " primaryPerc=" + primaryPerc + " secondaryPerc=" + secondaryPerc);

        setValues();


        if (getPrimaryPerc().equals(lastOfferedPrimary) &&
                getSecondaryPerc().equals(lastOfferedSecondary))
            return;

        dirty = true;
//        main.system.auxiliary.log.LogMaster.log(1, this + ">>>   reset " +
//         dataSource + " previousPrimaryPerc=" + previousPrimaryPerc + " previousSecondaryPerc=" + previousSecondaryPerc + " primaryPerc=" + primaryPerc + " secondaryPerc=" + secondaryPerc);

//        if (!getPrimaryPerc().equals(lastOfferedPrimary))
        setPreviousPrimaryPerc(lastOfferedPrimary);
//        if (!getSecondaryPerc().equals(lastOfferedSecondary))
        setPreviousSecondaryPerc(lastOfferedSecondary);

        lastOfferedPrimary = getPrimaryPerc();
        lastOfferedSecondary = getSecondaryPerc();


        //TODO quick fix
//        if (ExplorationMaster.isExplorationOn())
        animateChange(isAnimated());
        resetLabel();
    }

    protected abstract void setValues();

    public void animateChange() {
        animateChange(isAnimated());
    }

    public void animateChange(boolean smooth) {

        if (!getPrimaryPerc().equals(getPreviousPrimaryPerc())) {
            if (getPreviousPrimaryPerc() == null) {
                setPreviousPrimaryPerc(1f);
            }
            initChangeActions(primaryAction, getPreviousPrimaryPerc(), getPrimaryPerc());
            setPreviousPrimaryPerc(getPrimaryPerc());
        }
        if (!getSecondaryPerc().equals(getPreviousSecondaryPerc())) {
            if (getPreviousSecondaryPerc() == null) {
                setPreviousSecondaryPerc(1f);
            }
            initChangeActions(secondaryAction, getPreviousSecondaryPerc(), getSecondaryPerc());
            setPreviousSecondaryPerc(getSecondaryPerc());
        }

    }

    protected boolean isAnimated() {
        return true;
    }

    public void initChangeActions(FloatAction floatAction, Float previousPerc, float perc) {
//        main.system.auxiliary.log.LogMaster.log(0, ">>> hp bar animated " +
//         dataSource +
//         " previousPerc=" + previousPerc +
//         " perc=" + perc);
        floatAction.reset();
        floatAction.setStart(previousPerc);
        floatAction.setEnd(perc);
        addAction(floatAction);
        floatAction.setTarget(this);
        floatAction.setDuration(1.5f + (Math.abs(previousPerc - perc)));
    }

    protected abstract void resetLabel();

    protected void drawBar(TextureRegion region, Batch batch, Color color,
                           float y ) {
        if (color == null) {
            return;
        }
        batch.setColor(color);
        float x = getX() + offsetX;

        batch.draw(region, x, y, getScaleX() * region.getRegionWidth(),
                getScaleY() * region.getRegionHeight());
    }

    protected void initColors() {
        secondaryColor =
                getColor(false);
        primaryColor =
                getColor(true);
        label2.setColor(secondaryColor);
        label1.setColor(primaryColor);
    }

    protected abstract Color getColor(boolean over);

    @Override
    protected boolean isIgnored() {
        if (isDisplayedAlways())
            return false;
        if (displayedSecondaryPerc == 0)
            return true;
        if (fullLengthPerc == 0) {
            if (isAnimated()) {
//                return true; TODO why?
            }
        }
        return false;
    }

    protected boolean isDisplayedAlways() {
        return false;

    }

    protected String getBarImagePath(boolean over) {
        return StrPathBuilder.build("ui", "components",
                "dc", "unit", "hp bar.png");
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
    }

    @Override
    public float getWidth() {
        return GridMaster.CELL_W;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setLabelsDisplayed(boolean labelsDisplayed) {
        this.labelsDisplayed = labelsDisplayed;
    }

    public Float getPrimaryPerc() {
        return primaryPerc;
    }

    public void setPrimaryPerc(Float primaryPerc) {
        this.primaryPerc = primaryPerc;
    }

    public Float getSecondaryPerc() {
        return secondaryPerc;
    }

    public void setSecondaryPerc(Float secondaryPerc) {
        this.secondaryPerc = secondaryPerc;
    }

    public Float getPreviousPrimaryPerc() {
        return previousPrimaryPerc;
    }

    public void setPreviousPrimaryPerc(Float previousPrimaryPerc) {

//        main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>>>>>>>>> TOUGHNESS " +
//         " " + this.previousSecondaryPerc + " t0 " + previousSecondaryPerc);
        this.previousPrimaryPerc = previousPrimaryPerc;
    }

    public Float getPreviousSecondaryPerc() {
        return previousSecondaryPerc;
    }

    public void setPreviousSecondaryPerc(Float previousSecondaryPerc) {
//        if (previousSecondaryPerc!=0 )
//            if (previousSecondaryPerc!=1 )
//        main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>>>>>>>>> ENDURANCE " +
//         " " + this.previousSecondaryPerc + " t0 " + previousSecondaryPerc);
        this.previousSecondaryPerc = previousSecondaryPerc;
    }
}
