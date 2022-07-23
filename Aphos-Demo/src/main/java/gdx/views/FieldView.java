package gdx.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.libgdx.GdxColorMaster;
import gdx.dto.EntityDto;
import gdx.general.view.ADtoView;
import libgdx.StyleHolder;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import logic.content.AUnitEnums;
import main.content.enums.GenericEnums;

public class FieldView<T extends EntityDto> extends ADtoView<T> {

    protected final FadeImageContainer portrait = new FadeImageContainer(); //empty mirror?
    protected final boolean side;
    protected boolean active;
    protected LabelX atb;

    public FieldView(boolean side) {
        this.side = side;
        addActor(portrait);
        initAtbLabel();
    }

    protected void initAtbLabel() {
        addActor(atb = new LabelX());
        atb.setStyle(StyleHolder.getAVQLabelStyle(16));
    }

    public void setAtb(int val) {
        atb.setText("" + val);
        atb.setColor(GdxColorMaster.getColorForAtbValue(val));
    }

    @Override
    public void setScale(float scaleXY) {
        portrait.setScale(scaleXY);
        super.setScale(scaleXY);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dto != null)
            if (dto.getEntity() != null) {
            if (dto.getEntity().isOnAtb()) {
                setAtb(dto.getEntity().getInt(AUnitEnums.ATB));
                if (active){
                    /*
                    some interesting dynamic effect?
                     */
                }
            }
        }
    }

    @Override
    protected void update() {
        //TODO hierarchy of DTOs - good idea?
        if (dto.getEntity() == null) {
            portrait.setImage("");
        } else {
            portrait.setImage(dto.getEntity().getImagePath());
            if (!side) {
                portrait.setFlipX(true);
            }
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        portrait.setAlphaTemplate(alphaTemplate);
    }

    public void setScreenOverlay(float screenOverlay) {
        portrait.setScreenOverlay(screenOverlay);
    }

    public void setScreenEnabled(boolean screenEnabled) {
        portrait.setScreenEnabled(screenEnabled);
    }

    public void setBaseAlpha(float s) {
        portrait.setBaseAlpha(s);
    }

    public FadeImageContainer getPortrait() {
        return portrait;
    }

    public void kill() {
        portrait.setImage("");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (portrait.getScreenOverlay() > 0) {
            if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
                super.draw(batch, 1f);
                return;
            }
            ((CustomSpriteBatch) batch).resetBlending();
            drawScreen(batch, false);
            drawScreen(batch, true);
            ((CustomSpriteBatch) batch).resetBlending();
        } else
            super.draw(batch, 1f);
//        setColor(colorAction);
    }
}
