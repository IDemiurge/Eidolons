package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.gui.ScissorMaster;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 11/21/2017.
 * <p>
 * What if Secondary% is less than primary?
 * <p>
 * 100/150 50/300
 * <p>
 * #
 * <p>
 * perhaps remaining primary is 'ignored', so the total length is min(e,t)
 * <p>
 * >max
 * <p>
 * negative primary death visuals > when primary is <=0, make the full length min() condition different? suppose there
 * is 20% secondary remaining, which == 150 and primary was 300 so -100 will be death also
 */
public class HpBar extends ValueBar {

    private static Boolean hpAlwaysVisible;
    private boolean queue;
    BattleFieldObject dataSource;
    Float primaryDeathBarrier;

    //    private boolean primaryDeath;
    private boolean enduranceGreater;

    public HpBar(BattleFieldObject dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void animateChange(boolean smooth) {
        if (!HpBarManager.isHpBarVisible(dataSource)) {
            return;
        }
        setVisible(true);
        if (!smooth) {
            displayedSecondaryPerc = getSecondaryPerc();

            float realPerc = new Float(100 * dataSource.getIntParam(PARAMS.C_ENDURANCE) /
                    dataSource.getIntParam(PARAMS.ENDURANCE)) / 100f;
            {
                displayedSecondaryPerc = realPerc;
            }
            displayedPrimaryPerc = getPrimaryPerc();

            realPerc = new Float(100 * dataSource.getIntParam(PARAMS.C_TOUGHNESS) /
                    dataSource.getIntParam(PARAMS.TOUGHNESS)) / 100f;
            {
                displayedPrimaryPerc = realPerc;
            }

            return;
        }
        if (dataSource.getIntParam(PARAMS.C_ENDURANCE) > dataSource.getIntParam(PARAMS.C_TOUGHNESS)) {
            enduranceGreater = true;
        }
        super.animateChange(smooth);
    }

    @Override
    protected void resetLabel() {
        String text = "" + Math.round(dataSource.getIntParam(PARAMS.C_ENDURANCE))
                + "/" + dataSource.getLastValidParamValue(PARAMS.ENDURANCE);
        label1.setText(text);

        text = "" + (dataSource.getIntParam(PARAMS.C_TOUGHNESS) //* displayedPrimaryPerc
        ) + "/" + dataSource.getLastValidParamValue(PARAMS.TOUGHNESS);
        label2.setText(text);
        if (!EidolonsGame.BRIDGE || !dataSource.isPlayerCharacter() || EidolonsGame.getVar("secondary"))
            fullLengthPerc = displayedSecondaryPerc;
        else {
            fullLengthPerc = displayedPrimaryPerc;
        }
        if (getPrimaryPerc() <= 0) {
            setPrimaryPerc(primaryDeathBarrier);
            //            primaryDeath = true; TODO //diff color?
        }
        //        float offset = innerWidth * displayedPrimaryPerc / 2;
        label2.setPosition(0,
                //         offset - label2.getWidth() / 2,
                height * 3 / 2);
        float offset = innerWidth * displayedSecondaryPerc / 2;

        label1.setPosition(offset,
                //         offset - label1.getWidth() / 2,
                -height / 2);
        label1.setWidth(innerWidth);
        label2.setWidth(innerWidth);


        label1.setHeight(18);
        label2.setHeight(18);
    }

    @Override
    protected void setValues() {
        setPrimaryPerc(new Float(dataSource.getIntParam(PARAMS.C_TOUGHNESS))
                / (dataSource.getLastValidParamValue(PARAMS.ENDURANCE))); //not a bug - we want to display this way!
        setPrimaryPerc(MathMaster.getFloatWithDigitsAfterPeriod(2, getPrimaryPerc()));//  primaryPerc % 0.01f;
        setSecondaryPerc(new Float(dataSource.getIntParam(PARAMS.C_ENDURANCE)) / (dataSource.getLastValidParamValue(PARAMS.ENDURANCE)));
        setSecondaryPerc(MathMaster.getFloatWithDigitsAfterPeriod(2, getSecondaryPerc()));
    }

    public static Boolean getHpAlwaysVisible() {
        if (hpAlwaysVisible == null) {
            hpAlwaysVisible = OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.HP_BARS_ALWAYS_VISIBLE);
        }
        return hpAlwaysVisible;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // if (!queue) {
            barBg2.setVisible(!enduranceGreater);
        // }
        if (!queue && Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
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

            if (!EidolonsGame.BRIDGE || !dataSource.isPlayerCharacter() || EidolonsGame.getVar("secondary"))
                fullLengthPerc = displayedSecondaryPerc;
            else {
                fullLengthPerc = displayedPrimaryPerc;
            }
        } else if (!dirty)
            return;
        //        if (dataSource.isBeingReset())
        //            return;
        //        resetLabel(); now in logic thread!
        dirty = false;

    }

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
            if (!dataSource.isPlayerCharacter() || EidolonsGame.getVar("endurance"))
                fullLengthPerc = displayedSecondaryPerc;
            else {
                fullLengthPerc = displayedPrimaryPerc;
            }
        }
        return false;
    }

    protected boolean isDisplayedAlways() {
        return HpBar.getHpAlwaysVisible() == true;

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
        //ENDURANCE
        ScissorMaster.drawInRectangle(this, batch, getX(),
                y,
                innerWidth * p,
                height, () ->
                        drawBar(secondaryBarRegion, batch, secondaryColor, y));
        //TOUGHNESS
        ScissorMaster.drawInRectangle(this, batch, getX(),
                y, innerWidth * Math.min(p, displayedPrimaryPerc),
                height, () ->
                        drawBar(primaryBarRegion, batch, primaryColor, getY()));

    }

    @Override
    public void setTeamColor(Color teamColor) {
        if (teamColor == GdxColorMaster.NEUTRAL)
            teamColor = GdxColorMaster.RED;
        super.setTeamColor(teamColor);
        secondaryColor = GdxColorMaster.ENDURANCE;
        //        GdxColorMaster.darker(getTeamColor(), 0.55f);
        primaryColor = GdxColorMaster.TOUGHNESS;
        //        GdxColorMaster.lighter(getTeamColor(), 0.55f);
        label2.setColor((getTeamColor()));
        label1.setColor(primaryColor);
    }

    @Override
    public Stage getStage() {
        return (queue) ? DungeonScreen.getInstance().getGuiStage()
                : DungeonScreen.getInstance().getGridStage();
    }

    @Override
    public boolean isTeamColorBorder() {
        return true;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
        labelsDisplayed = !queue;
    }

    @Override
    public String toString() {
        return
                (queue ? "queue hp bar" : label2.getText() + " bar ") +
                        dataSource.getName();
        //        label.getText()+
    }

    public boolean canHpBarBeVisible() {
        return HpBarManager.canHpBarBeVisible(dataSource);
    }

    public boolean isHpBarVisible() {
        return HpBarManager.isHpBarVisible(dataSource);
    }

    public static boolean isResetOnLogicThread() {
        return true;
    }
}
