package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.game.core.game.DC_Game;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.shaders.GrayscaleShader;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.log.LogMaster;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;

import java.util.concurrent.atomic.AtomicInteger;

public class UnitView extends BaseView {
    protected static AtomicInteger lastId = new AtomicInteger(1);
    protected final int curId;
    protected int initiativeIntVal;
    protected TextureRegion clockTexture;
    protected Label initiativeLabel;
    protected Image clockImage;
    protected boolean mobilityState = true;//mobility state, temporary.
    protected boolean flickering;
    protected Image emblemImage;
    protected Image emblemBorder;
    protected Image modeImage;
    protected boolean greyedOut;
    private boolean emblemBorderOn;
    private Texture outline;
    private boolean initialized;


    public UnitView(UnitViewOptions o) {
        this(o, lastId.getAndIncrement());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.curId = curId;
        setTeamColor(o.getTeamColor());
        init(o.getClockTexture(), o.getClockValue());
    }

    public void setToolTip(ToolTip toolTip) {
        addListener(toolTip.getController());
    }

    public void reset() {


        if (isEmblemBorderOn()) {
            emblemBorder = new Image(TextureCache.getOrCreateR(
             CellBorderManager.teamcolorPath));
            emblemBorder.setBounds(emblemImage.getX() - 6, emblemImage.getY() - 6
             , this.emblemImage.getWidth() + 10, this.emblemImage.getHeight() + 10);
            addActor(emblemBorder);
        }
    }

    private void init(TextureRegion clockTexture, int clockVal) {
        this.initiativeIntVal = clockVal;
            if (!(this instanceof GridUnitView))
                if (clockTexture != null) {
                    this.clockTexture = clockTexture;
                    initiativeLabel = new Label(
                     String.valueOf(clockVal),
                     StyleHolder.getSizedColoredLabelStyle(StyleHolder.ALT_FONT, 16,
                      getTeamColor()
//                  GdxColorMaster.CYAN
//              GdxColorMaster.getColor(SmartTextManager.getValueCase())
                     ));
                    clockImage = new Image(clockTexture);
                    addActor(clockImage);
                    addActor(initiativeLabel);
                }
                setInitialized(true);
        }

    protected void updateModeImage(String pathToImage) {
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (initiativeLabel != null) {
            clockImage.setPosition(0, 0);
            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth()),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        }
        if (isInitialized())
            reset();
    }

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            initiativeIntVal = val;
            initiativeLabel.setText(String.valueOf(val));
            initiativeLabel.setStyle(StyleHolder.getSizedColoredLabelStyle(StyleHolder.ALT_FONT, 16,
             getTeamColor()));
            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth() / 2),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (flickering) {

        }
//        alphaFluctuation(emblemBorder, delta);

    }

    protected boolean checkIgnored() {
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OPTIMIZATION_ON))
            if (!DungeonScreen.getInstance().
             getController().isWithinCamera(getX(), getY(), getWidth(), getHeight())) {
                return false;
            }
        return false;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (greyedOut) {
            batch.setShader(GrayscaleShader.getGrayscaleShader());
            ActorMaster.addFadeInOrOutIfNoActions(this, 1);
        }
        super.draw(batch, parentAlpha);
        if (DC_Game.game.isDebugMode())
        if (outline != null)
            batch.draw(outline, 0, 0);

        batch.setShader(null);
    }

    public void setFlickering(boolean flickering) {
        this.flickering = flickering;
    }

    public void setGreyedOut(boolean greyedOut) {
        this.greyedOut = greyedOut;
    }

    public void toggleGreyedOut() {
        this.greyedOut = !greyedOut;
    }

    public int getCurId() {
        return curId;
    }

    public int getInitiativeIntVal() {
        return initiativeIntVal;
    }


    public boolean getMobilityState() {
        return mobilityState;
    }

    public void setMobilityState(boolean mobilityState) {
        this.mobilityState = mobilityState;
    }

    public boolean isEmblemBorderOn() {
        return emblemBorderOn;
    }

    public void setEmblemBorderOn(boolean emblemBorderOn) {
        this.emblemBorderOn = emblemBorderOn;
    }

    public Texture getOutline() {
        return outline;
    }

    public void setOutline(Texture outline) {
        this.outline = outline;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
