package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.rules.round.UnconsciousRule;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.gui.ScissorMaster;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.NumberUtils;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 11/21/2017.
 * <p>
 * What if Secondary% is less than primary?
 * <p>
 * 100/150
 * 50/300
 * <p>
 * #
 * <p>
 * perhaps remaining primary is 'ignored', so the total length is min(e,t)
 * <p>
 * >max
 * <p>
 * negative primary death visuals
 * > when primary is <=0, make the full length min() condition different?
 * suppose there is 20% secondary remaining, which == 150
 * and primary was 300 so -100 will be death also
 */
public class HpBar extends ValueBar {

    private static Boolean hpAlwaysVisible;
    private boolean queue;
    BattleFieldObject dataSource;
    Float primaryDeathBarrier;
    
    private boolean primaryDeath;
    private float offsetX;
    private boolean keserim;

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
            if (displayedSecondaryPerc != realPerc) {
                displayedSecondaryPerc = realPerc;
            }
            displayedPrimaryPerc = getPrimaryPerc();

            realPerc = new Float(100 * dataSource.getIntParam(PARAMS.C_TOUGHNESS) /
                    dataSource.getIntParam(PARAMS.TOUGHNESS)) / 100f;
            if (displayedPrimaryPerc != realPerc) {
                displayedPrimaryPerc = realPerc;
            }
            keserim=false;
            if (EidolonsGame.BRIDGE)
                if (dataSource.isPlayerCharacter()){
                    if (!EidolonsGame.getVar("secondary")){
                        keserim=true;
//                    displayedPrimaryPerc=Math.min( displayedPrimaryPerc;
                        displayedSecondaryPerc =displayedPrimaryPerc;
//                setSecondaryPerc(MathMaster.getFloatWithDigitsAfterPeriod(2, getSecondaryPerc()));
                    }
                }
            return;
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
        if (!EidolonsGame.BRIDGE||!dataSource.isPlayerCharacter() || EidolonsGame.getVar("secondary"))
            fullLengthPerc = displayedSecondaryPerc;
        else {
            fullLengthPerc = displayedPrimaryPerc;
        }
        if (getPrimaryPerc() <= 0) {
            setPrimaryPerc(primaryDeathBarrier);
            primaryDeath = true; //diff color?
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

        primaryDeathBarrier = new Float(UnconsciousRule.DEFAULT_DEATH_BARRIER) / 100;
        Integer mod = NumberUtils.getInteger(dataSource.getParam(PARAMS.TOUGHNESS_DEATH_BARRIER_MOD));
        if (mod != 0) {
            primaryDeathBarrier = primaryDeathBarrier * mod / 100;
        }
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

            if (!EidolonsGame.BRIDGE||!dataSource.isPlayerCharacter() || EidolonsGame.getVar("secondary"))
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
    public void drawAt(Batch batch, float x, float y) {
        setPosition(x, y);
        resetLabel();
        draw(batch, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (isIgnored())
//            return;
//        if (label.isVisible()) {
//            label.getWidth();
//        }
//        super.draw(batch, parentAlpha);
////        batch.flush();
//        if (fluctuatingAlpha != 1)
//            batch.setColor(new Color(1, 1, 1, 1));
//TODO ownership change? team colors reset...

//        if (GridMaster.isHpBarsOnTop() && !queue)
//        {
//            Vector2 v = //localToStageCoordinates
//             (new Vector2(getX(), getY()));
//            clipBounds =   new Rectangle(v.x , v.y , innerWidth * fullLengthPerc, height);
//        } else
//        Rectangle scissors = new Rectangle();
//        Rectangle clipBounds = null;
//        clipBounds = new Rectangle(getX(), getY(), innerWidth * Math.min(1, fullLengthPerc)*getScaleX(), height);
//        getStage().calculateScissors(clipBounds, scissors);
//        ScissorStack.pushScissors(scissors);

//        Color color = secondaryColor;
//        TextureRegion region = secondaryBarRegion;
//        drawBar(region, batch,Math.min(1,  displayedSecondaryPerc), color, false);
//
//        if (primaryDeath) {
//            //setShader
//        }
//        color = primaryColor;
//        region = primaryBarRegion;
//        drawBar(region, batch,Math.min(displayedSecondaryPerc, displayedPrimaryPerc), color, true);

//        batch.flush();
//        try {
//            ScissorStack.popScissors();
//        } catch (Exception e) {
//        }
//        batch.flush();


        if (parentAlpha != ShaderDrawer.SUPER_DRAW) {
            if (isIgnored())
                return;
            if (fluctuatingAlpha != 1)
                batch.setColor(new Color(1, 1, 1, 1));
            if (label2.isVisible()) {
                label2.getWidth();
            }
//            super.draw(batch, parentAlpha);
            if (dataSource instanceof BossUnit) {
                ScissorMaster.drawInRectangle(this, batch, getX()-128, getY()-128,3* innerWidth *  MathMaster.minMax( fullLengthPerc, 0, 1)  * getScaleX(), height);
            } else
                ScissorMaster.drawInRectangle(this, batch, getX(), getY(), innerWidth * MathMaster.minMax( fullLengthPerc, 0.03f, 1) * getScaleX(), height);
        } else {
            Color color = secondaryColor;
            TextureRegion region = secondaryBarRegion;
            float p = MathMaster.minMax(displayedSecondaryPerc, 0, 1);
            drawBar(region, batch, p, color, false);
            color = primaryColor;
            region = primaryBarRegion;
            drawBar(region, batch, Math.min(p, displayedPrimaryPerc), color, true);
            batch.flush();
        }

//        clipBounds =        new Rectangle(getX(), getY(), innerWidth * displayedPrimaryPerc, height);
//        getStage().calculateScissors(clipBounds, scissors);
//        ScissorStack.pushScissors(scissors);

    }

    @Override
    public void setTeamColor(Color teamColor) {
        if (teamColor == GdxColorMaster.NEUTRAL)
            teamColor = GdxColorMaster.RED;
        super.setTeamColor(teamColor);
        secondaryColor = GdxColorMaster.darker(getTeamColor(), 0.55f);
        primaryColor = GdxColorMaster.lighter(getTeamColor(), 0.55f);
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

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
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
