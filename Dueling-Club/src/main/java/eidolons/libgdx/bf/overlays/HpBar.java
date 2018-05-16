package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.round.UnconsciousRule;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.actions.FloatActionLimited;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 11/21/2017.
 * <p>
 * What if Endurance% is less than toughness?
 * <p>
 * 100/150
 * 50/300
 * <p>
 * #
 * <p>
 * perhaps remaining toughness is 'ignored', so the total length is min(e,t)
 * <p>
 * >max
 * <p>
 * negative toughness death visuals
 * > when toughness is <=0, make the full length min() condition different?
 * suppose there is 20% endurance remaining, which == 150
 * and toughness was 300 so -100 will be death also
 */
public class HpBar extends SuperActor {

    private static final Integer FONT_SIZE = 18;
    private static Boolean hpAlwaysVisible;
    private final Image barImage;
    private final Label label;
    private final Label label_t;
    private final int innerWidth;
    ResourceSourceImpl dataSource;
    Float toughnessDeathBarrier;
    float displayedToughnessPerc;
    float displayedEndurancePerc;
    FloatActionLimited toughnessAction = (FloatActionLimited) ActorMaster.getAction(FloatActionLimited.class);
    FloatActionLimited enduranceAction = (FloatActionLimited) ActorMaster.getAction(FloatActionLimited.class);
    boolean labelsDisplayed = true;
    private Float toughnessPerc = 1f;
    private Float endurancePerc = 1f;
    private Float previousToughnessPerc = 1f;
    private Float previousEndurancePerc = 1f;
    private float fullLengthPerc;
    private boolean toughnessDeath;
    private TextureRegion barRegion; //TODO can it be static?
    private TextureRegion toughnessBarRegion;
    private TextureRegion enduranceBarRegion;
    private float height;
    private Color enduranceColor;
    private Color toughnessColor;
    private float offsetX;
    private boolean queue;
    private boolean dirty;
    private Float lastOfferedToughness;
    private Float lastOfferedEndurance;

    public HpBar(ResourceSourceImpl dataSource) {
        this.dataSource = dataSource;
        barRegion = TextureCache.getOrCreateR(StrPathBuilder.build("ui", "components",
         "dc",  "unit", "hp bar empty.png"));
        height = barRegion.getRegionHeight();

        toughnessBarRegion = TextureCache.getOrCreateR(StrPathBuilder.build("ui", "components",
         "dc",  "unit", "hp bar.png"));
        enduranceBarRegion = TextureCache.getOrCreateR(StrPathBuilder.build("ui", "components",
         "dc",  "unit", "hp bar.png"));
        innerWidth = enduranceBarRegion.getRegionWidth();
        barImage = new Image(barRegion);
        addActor(barImage);
        label = new Label("", StyleHolder.
         getSizedLabelStyle(FONT.AVQ, FONT_SIZE));
        label_t = new Label("", StyleHolder.
         getSizedLabelStyle(FONT.AVQ, FONT_SIZE));
        addActor(label_t);
        addActor(label);
        offsetX = barRegion.getRegionWidth() - toughnessBarRegion.getRegionWidth();
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
    public void reset(ResourceSourceImpl resourceSource) {
        //TODO queue animation?
//        main.system.auxiliary.log.LogMaster.log(1, this + ">>>  tries to reset " +
//         dataSource + " previousToughnessPerc=" + previousToughnessPerc + " previousEndurancePerc=" + previousEndurancePerc + " toughnessPerc=" + toughnessPerc + " endurancePerc=" + endurancePerc);

        dataSource = resourceSource;
        setToughnessPerc(new Float(dataSource.getIntParam(PARAMS.C_TOUGHNESS))
         / (dataSource.getIntParam(PARAMS.ENDURANCE)));
        setToughnessPerc(((int) (getToughnessPerc() / 0.01f) / new Float(100)));//  toughnessPerc % 0.01f;
        setEndurancePerc(new Float(dataSource.getIntParam(PARAMS.C_ENDURANCE)) / (dataSource.getIntParam(PARAMS.ENDURANCE)));
        setEndurancePerc(((int) (getEndurancePerc() / 0.01f) / new Float(100)));// endurancePerc % 0.01f;
        if (getToughnessPerc().equals(lastOfferedToughness) &&
         getEndurancePerc().equals(lastOfferedEndurance))
            return;

        dirty = true;
//        main.system.auxiliary.log.LogMaster.log(1, this + ">>>   reset " +
//         dataSource + " previousToughnessPerc=" + previousToughnessPerc + " previousEndurancePerc=" + previousEndurancePerc + " toughnessPerc=" + toughnessPerc + " endurancePerc=" + endurancePerc);

//        if (!getToughnessPerc().equals(lastOfferedToughness))
        setPreviousToughnessPerc(lastOfferedToughness);
//        if (!getEndurancePerc().equals(lastOfferedEndurance))
        setPreviousEndurancePerc(lastOfferedEndurance);

        lastOfferedToughness = getToughnessPerc();
        lastOfferedEndurance = getEndurancePerc();

        toughnessDeathBarrier = new Float(UnconsciousRule.DEFAULT_DEATH_BARRIER) / 100;
        Integer mod = StringMaster.getInteger(dataSource.getParam(PARAMS.TOUGHNESS_DEATH_BARRIER_MOD));
        if (mod != 0) {
            toughnessDeathBarrier = toughnessDeathBarrier * mod / 100;
        }
        //TODO quick fix
//        if (ExplorationMaster.isExplorationOn())
            animateChange(false);
    }

    public void animateChange() {
        animateChange(isAnimated());
    }

    public void animateChange(boolean smooth) {
        if (!dataSource.isHpBarVisible()) {
//            main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar not visible " +
//             dataSource);
            return;
        }
        if (getActions().size != 0)
//        getActions().clear();
            return;
//        if (getActions().size != 0)
//        {
//            main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar already being animated " +
//             dataSource);
//            return;
//        }
        setVisible(true);
        if (!smooth) {
            displayedEndurancePerc = getEndurancePerc();

            float realPerc = new Float(100*dataSource.getIntParam(PARAMS.C_ENDURANCE) /
             dataSource.getIntParam(PARAMS.ENDURANCE))/100f ;
            if (displayedEndurancePerc!=realPerc){
                main.system.auxiliary.log.LogMaster.log(1,"HP BAR ENDURANCE PERCENTAGE MISMATCH: " +
                 displayedEndurancePerc+
                 " VS REAL == " +
                 realPerc );
                displayedEndurancePerc=realPerc;
            }

            displayedToughnessPerc = getToughnessPerc();

            realPerc = new Float(100*dataSource.getIntParam(PARAMS.C_TOUGHNESS) /
             dataSource.getIntParam(PARAMS.TOUGHNESS))/100f ;
            if (displayedToughnessPerc!=realPerc){
                main.system.auxiliary.log.LogMaster.log(1,"HP BAR TOUGHNESS PERCENTAGE MISMATCH: " +
                 displayedToughnessPerc+
                 " VS REAL == " +
                 realPerc );
                displayedToughnessPerc=realPerc;
            }
            return;
        }
        if (!getToughnessPerc().equals(getPreviousToughnessPerc())) {
            if (getPreviousToughnessPerc() == null) {
                setPreviousToughnessPerc(1f);
            }
            initChangeActions(toughnessAction, getPreviousToughnessPerc(), getToughnessPerc());
            setPreviousToughnessPerc(getToughnessPerc());
        }
        if (!getEndurancePerc().equals(getPreviousEndurancePerc())) {
            if (getPreviousEndurancePerc() == null) {
                setPreviousEndurancePerc(1f);
            }
            initChangeActions(enduranceAction, getPreviousEndurancePerc(), getEndurancePerc());
            setPreviousEndurancePerc(getEndurancePerc());
        }

    }

    private boolean isAnimated() {
        return false;
    }

    public void initChangeActions(FloatAction floatAction, Float previousPerc, float perc) {
        main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar animated " +
         dataSource +
         " previousPerc=" + previousPerc +
         " perc=" + perc);
        floatAction.reset();
        floatAction.setStart(previousPerc);
        floatAction.setEnd(perc);
        addAction(floatAction);
        floatAction.setTarget(this);
        floatAction.setDuration(1.5f + (Math.abs(previousPerc - perc)));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!queue && Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            if (!label.isVisible()) {
                label_t.setVisible(true);
                label.setVisible(true);
                label_t.setZIndex(Integer.MAX_VALUE);
                label.setZIndex(Integer.MAX_VALUE);
            }
        } else {
            label_t.setVisible(false);
            label.setVisible(false);
        }
        if (isAnimated() && getActions().size > 0) {
//            main.system.auxiliary.log.LogMaster.log(1,displayedToughnessPerc+ " WAS " +displayedEndurancePerc);
            displayedEndurancePerc = MathMaster.minMax(enduranceAction.getValue(),
             Math.min(previousEndurancePerc, endurancePerc), Math.max(previousEndurancePerc, endurancePerc));
            displayedToughnessPerc = MathMaster.minMax(toughnessAction.getValue(),
             Math.min(previousToughnessPerc, toughnessPerc), Math.max(previousToughnessPerc, toughnessPerc));
//            main.system.auxiliary.log.LogMaster.log(1,displayedToughnessPerc+ " BECAME " +displayedEndurancePerc);
        } else if (!dirty)
            return;
        String text = "" + Math.round(dataSource.getIntParam(PARAMS.C_ENDURANCE) )
         + "/" + dataSource.getIntParam(PARAMS.ENDURANCE);
        label.setText(text);

        text = "" + (dataSource.getIntParam(PARAMS.C_TOUGHNESS) //* displayedToughnessPerc
        ) + "/" + dataSource.getIntParam(PARAMS.TOUGHNESS);
        label_t.setText(text);
        fullLengthPerc = displayedEndurancePerc;
        if (getToughnessPerc() <= 0) {
            setToughnessPerc(toughnessDeathBarrier);
            toughnessDeath = true; //diff color?
        }
        resetLabel();
        dirty = false;

    }

    private void resetLabel() {
//        float offset = innerWidth * displayedToughnessPerc / 2;
        label_t.setPosition(0,
//         offset - label_t.getWidth() / 2,
         height * 3 / 2);
        float offset = innerWidth * displayedEndurancePerc / 2;

        label.setPosition(offset,
//         offset - label.getWidth() / 2,
         -height / 2);
        label.setWidth(innerWidth);
        label_t.setWidth(innerWidth);


        label.setHeight(18);
        label_t.setHeight(18);
    }

    private void drawBar(TextureRegion region, Batch batch, float perc, Color color, boolean reverse) {
        if (color == null) {
            return;
        }
        batch.setColor(color);
        float x = getX() + offsetX;
        if (reverse) {
            x = x + region.getRegionWidth() * (fullLengthPerc - perc);
        }
        batch.draw(region, x, getY(), getScaleX()*region.getRegionWidth(),
         getScaleY()*region.getRegionHeight());
    }

    @Override
    protected boolean isIgnored() {
        if (isDisplayedAlways())
            return false;
        if (displayedEndurancePerc == 0)
            return true;
        if (fullLengthPerc == 0) {
            if (isAnimated()) {
                return true;
            }
            fullLengthPerc = displayedEndurancePerc;
        }
        return false;
    }

    private boolean isDisplayedAlways() {
        return getHpAlwaysVisible() == true;

    }
    public static Boolean getHpAlwaysVisible() {
        if (hpAlwaysVisible == null) {
            hpAlwaysVisible = OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.HP_BARS_ALWAYS_VISIBLE);
        }
        return hpAlwaysVisible;
    }
    public void drawAt(Batch batch, float x, float y) {
        setPosition(x, y);
        resetLabel();
        draw(batch, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isIgnored())
            return;
        if (label.isVisible()) {
            label.getWidth();
        }
        super.draw(batch, parentAlpha);
        batch.flush();
        if (fluctuatingAlpha != 1)
            batch.setColor(new Color(1, 1, 1, 1));
//TODO ownership change? team colors reset...

        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = null ;
//        if (GridMaster.isHpBarsOnTop() && !queue)
//        {
//            Vector2 v = //localToStageCoordinates
//             (new Vector2(getX(), getY()));
//            clipBounds =   new Rectangle(v.x , v.y , innerWidth * fullLengthPerc, height);
//        } else
        clipBounds =        new Rectangle(getX(), getY(), innerWidth * fullLengthPerc, height);
        getStage().calculateScissors(clipBounds, scissors);
        ScissorStack.pushScissors(scissors);

        Color color = enduranceColor;
        TextureRegion region = enduranceBarRegion;
        drawBar(region, batch, displayedEndurancePerc, color, false);

        if (toughnessDeath) {
            //setShader
        }
        color = toughnessColor;
        region = toughnessBarRegion;
//        clipBounds =        new Rectangle(getX(), getY(), innerWidth * displayedToughnessPerc, height);
//        getStage().calculateScissors(clipBounds, scissors);
//        ScissorStack.pushScissors(scissors);
        drawBar(region, batch, displayedToughnessPerc, color, true);

        batch.flush();
        try {
            ScissorStack.popScissors();
        } catch (Exception e) {
        }


    }

    @Override
    public void setTeamColor(Color teamColor) {
        if (teamColor == GdxColorMaster.NEUTRAL)
            teamColor = GdxColorMaster.RED;
        super.setTeamColor(teamColor);
        enduranceColor = GdxColorMaster.darker(getTeamColor(), 0.55f);
        toughnessColor = GdxColorMaster.lighter(getTeamColor(), 0.55f);
        label.setColor((getTeamColor()));
        label_t.setColor(toughnessColor);
    }

    @Override
    public Stage getStage() {
        return (queue) ? DungeonScreen.getInstance().getGuiStage()
         : DungeonScreen.getInstance().getGridStage();
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

    @Override
    public boolean isTeamColorBorder() {
        return true;
    }


    public ResourceSourceImpl getDataSource() {
        return dataSource;
    }

    public void setLabelsDisplayed(boolean labelsDisplayed) {
        this.labelsDisplayed = labelsDisplayed;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
        labelsDisplayed = !queue;
    }

    @Override
    public String toString() {
        return
         (queue ? "queue hp bar" : label.getText() + " bar ") +
          dataSource.getName();
//        label.getText()+
    }

    public Float getToughnessPerc() {
        return toughnessPerc;
    }

    public void setToughnessPerc(Float toughnessPerc) {
        this.toughnessPerc = toughnessPerc;
    }

    public Float getEndurancePerc() {
        return endurancePerc;
    }

    public void setEndurancePerc(Float endurancePerc) {
        this.endurancePerc = endurancePerc;
    }

    public Float getPreviousToughnessPerc() {
        return previousToughnessPerc;
    }

    public void setPreviousToughnessPerc(Float previousToughnessPerc) {

//        main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>>>>>>>>> TOUGHNESS " +
//         " " + this.previousEndurancePerc + " t0 " + previousEndurancePerc);
        this.previousToughnessPerc = previousToughnessPerc;
    }

    public Float getPreviousEndurancePerc() {
        return previousEndurancePerc;
    }

    public void setPreviousEndurancePerc(Float previousEndurancePerc) {
//        if (previousEndurancePerc!=0 )
//            if (previousEndurancePerc!=1 )
//        main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>>>>>>>>> ENDURANCE " +
//         " " + this.previousEndurancePerc + " t0 " + previousEndurancePerc);
        this.previousEndurancePerc = previousEndurancePerc;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}
