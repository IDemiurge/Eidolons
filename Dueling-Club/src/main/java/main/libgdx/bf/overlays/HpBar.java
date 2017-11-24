package main.libgdx.bf.overlays;

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
import main.content.PARAMS;
import main.game.battlecraft.rules.round.UnconsciousRule;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.GdxColorMaster;
import main.libgdx.StyleHolder;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.SuperActor;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

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

    private final Image barImage;
    private final Label label;
    private final Label label_t;
    private final int innerWidth;
    ResourceSourceImpl dataSource;
    Float toughnessPerc = 1f;
    Float endurancePerc = 1f;
    Float previousToughnessPerc;
    Float toughnessDeathBarrier;
    Float previousEndurancePerc;
    float displayedToughnessPerc;
    float displayedEndurancePerc;
    FloatAction toughnessAction = new FloatAction();
    FloatAction enduranceAction = new FloatAction();
    boolean labelsDisplayed = true;
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

    public HpBar(ResourceSourceImpl dataSource) {
        this.dataSource = dataSource;
        barRegion = TextureCache.getOrCreateR(StrPathBuilder.build("ui", "components",
         "2017", "grid", "unit", "hp bar empty.png"));
        height = barRegion.getRegionHeight();

        toughnessBarRegion = TextureCache.getOrCreateR(StrPathBuilder.build("ui", "components",
         "2017", "grid", "unit", "hp bar.png"));
        enduranceBarRegion = TextureCache.getOrCreateR(StrPathBuilder.build("ui", "components",
         "2017", "grid", "unit", "hp bar.png"));
        innerWidth = enduranceBarRegion.getRegionWidth();
        barImage = new Image(barRegion);
        addActor(barImage);
        label = new Label("", StyleHolder.
         getSizedLabelStyle(FONT.AVQ, 18));
        label_t = new Label("", StyleHolder.
         getSizedLabelStyle(FONT.AVQ, 18));
        addActor(label_t);
        addActor(label);
        offsetX = barRegion.getRegionWidth() - toughnessBarRegion.getRegionWidth();
    }

    //make sure this isn't called all the time!
    public void reset(ResourceSourceImpl resourceSource) {
        //TODO queue animation?
//        main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar tries to reset " +
//         dataSource +" previousToughnessPerc=" + previousToughnessPerc +" previousEndurancePerc=" + previousEndurancePerc+ " toughnessPerc=" + toughnessPerc +" endurancePerc=" + endurancePerc);

        dataSource = resourceSource;
        toughnessPerc = new Float(dataSource.getIntParam(PARAMS.C_TOUGHNESS))
         /  (dataSource.getIntParam(PARAMS.ENDURANCE));
        toughnessPerc = ((int) (toughnessPerc / 0.01f) / new Float(100));//  toughnessPerc % 0.01f;
        endurancePerc = new Float(dataSource.getIntParam(PARAMS.C_ENDURANCE)) /  (dataSource.getIntParam(PARAMS.ENDURANCE));
        endurancePerc = ((int) (endurancePerc / 0.01f) / new Float(100));// endurancePerc % 0.01f;
        if (toughnessPerc.equals(previousToughnessPerc) &&
         endurancePerc.equals(previousEndurancePerc))
            return;
        dirty = true;
//        main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar reset " +
//         dataSource +" previousToughnessPerc=" + previousToughnessPerc +" previousEndurancePerc=" + previousEndurancePerc+ " toughnessPerc=" + toughnessPerc +" endurancePerc=" + endurancePerc);
        if (!toughnessPerc.equals(previousToughnessPerc))
            previousToughnessPerc = toughnessPerc;
        if (!endurancePerc.equals(previousEndurancePerc))
            previousEndurancePerc = endurancePerc;
        toughnessDeathBarrier = new Float(UnconsciousRule.DEFAULT_DEATH_BARRIER) / 100;
        Integer mod = StringMaster.getInteger(dataSource.getParam(PARAMS.TOUGHNESS_DEATH_BARRIER_MOD));
        if (mod != 0) {
            toughnessDeathBarrier = toughnessDeathBarrier * mod / 100;
        }
        //TODO quick fix
        if (ExplorationMaster.isExplorationOn())
            animateChange(false);
    }

    public void animateChange() {
        animateChange(isAnimated());
    }
        public void animateChange(boolean smooth) {
        if (!dataSource.isHpBarVisible()) {
            main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar not visible " +
             dataSource);
            return;
        }
        getActions().clear();
//        if (getActions().size != 0)
//        {
//            main.system.auxiliary.log.LogMaster.log(1, ">>> hp bar already being animated " +
//             dataSource);
//            return;
//        }
        setVisible(true);
        if (!smooth) {
            displayedEndurancePerc = endurancePerc;
            displayedToughnessPerc = toughnessPerc;
            return;
        }
        if (previousToughnessPerc != toughnessPerc)
            initChangeActions(toughnessAction, previousToughnessPerc, toughnessPerc);
        if (previousEndurancePerc != endurancePerc)
            initChangeActions(enduranceAction, previousEndurancePerc, endurancePerc);

    }

    private boolean isAnimated() {
        return true;
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
        floatAction.setDuration(1.5f + (Math.abs(this.previousToughnessPerc - this.toughnessPerc)));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!queue && Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            label_t.setVisible(true);
            label.setVisible(true);
        } else {
            label_t.setVisible(labelsDisplayed);
            label.setVisible(labelsDisplayed);
        }
        if (isAnimated()) {
            if (getActions().size == 0)
                return;
            displayedEndurancePerc = enduranceAction.getValue();
            displayedToughnessPerc = toughnessAction.getValue();
        } else if (!dirty) return;
        String text = "" + Math.round(dataSource.getIntParam(PARAMS.ENDURANCE) * displayedEndurancePerc)
         + "/" + dataSource.getIntParam(PARAMS.ENDURANCE);
        label.setText(text);

        text = "" + (dataSource.getIntParam(PARAMS.C_TOUGHNESS) //* displayedToughnessPerc
        ) + "/" + dataSource.getIntParam(PARAMS.TOUGHNESS);
        label_t.setText(text);
        fullLengthPerc = displayedEndurancePerc;
        if (toughnessPerc <= 0) {
            toughnessPerc = toughnessDeathBarrier;
            toughnessDeath = true; //diff color?
        }

//        float offset = innerWidth * displayedToughnessPerc / 2;
        label_t.setPosition(0,
//         offset - label_t.getWidth() / 2,
         height * 3 / 2);
        float offset = innerWidth * displayedEndurancePerc / 2;

        label.setPosition(offset,
//         offset - label.getWidth() / 2,
         -height / 2);
        dirty = false;
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
        batch.draw(region, x, getY());
    }

    @Override
    protected boolean isIgnored() {
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

    public void drawAt(Batch batch, float x, float y) {
        setPosition(x, y);
        draw(batch, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isIgnored())
            return;
        super.draw(batch, parentAlpha);
        batch.flush();
        if (fluctuatingAlpha != 1)
            batch.setColor(new Color(1, 1, 1, 1));
//TODO ownership change? team colors reset...

        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(getX(), getY(), innerWidth * fullLengthPerc, height);
        getStage().calculateScissors(clipBounds, scissors);
        ScissorStack.pushScissors(scissors);

        Color color = enduranceColor;
        TextureRegion region = enduranceBarRegion;
        drawBar(region, batch, fullLengthPerc, color, false);

        if (toughnessDeath) {
            //setShader
        }
        color = toughnessColor;
        region = toughnessBarRegion;
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
        return GridConst.CELL_W;
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
         (queue ? "queue hp bar" : label.getText()+" bar ") +
         dataSource.getName();
    }
}
