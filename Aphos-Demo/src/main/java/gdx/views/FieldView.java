package gdx.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxColorMaster;
import gdx.dto.EntityDto;
import gdx.general.view.ADtoView;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import logic.content.AUnitEnums;
import main.content.enums.GenericEnums;

public class FieldView<T extends EntityDto> extends ADtoView<T> {

    protected final FadeImageContainer portrait = new FadeImageContainer(); //empty mirror?
    protected SpriteX highlight;
    protected final boolean side;
    protected boolean active;
    protected LabelX atb;

    public FieldView(boolean side) {
        this.side = side;

        addActor("highlight", highlight = new SpriteX("sprite\\view\\gate.txt"));
        highlight.setBlending(GenericEnums.BLENDING.SCREEN);
        highlight.setColor(1,1,1,0);
//        , VisualEnums.SPRITE_TEMPLATE.THUNDER2,
//                GenericEnums.ALPHA_TEMPLATE.SUN, GenericEnums.BLENDING.SCREEN
        addActor(portrait);
        initAtbLabel();
    }

    protected void initAtbLabel() {
        addActor(atb = new LabelX());
        atb.setStyle(StyleHolder.getAVQLabelStyle(18));
    }

    public void setAtb(int val) {
        atb.setText("ATB: " + val);
        atb.setColor(GdxColorMaster.getColorForAtbValue(val));
        atb.pack();
        atb.setX(GdxMaster.centerWidth(atb));
    }

    @Override
    public void setScale(float scaleXY) {
        portrait.setScale(scaleXY);
        super.setScale(scaleXY);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isEmpty()) {
            if (dto.getEntity().isOnAtb()) {
                setAtb(dto.getEntity().getInt(AUnitEnums.ATB));
            }
        }
    }

    protected boolean isEmpty() {
        if (dto == null)
            return true;
        if (dto.getEntity() == null)
            return true;
        return false;
    }

    @Override
    protected void update() {
        //TODO hierarchy of DTOs - good idea?
        if (dto.getEntity() == null) {
            portrait.setImage("");
        } else {
            portrait.setImage(dto.getEntity().getImagePath());
            setSize(portrait.getWidth(), portrait.getHeight());
            highlight.setPosition(portrait.getWidth()/2 , portrait.getHeight()/2);
            if (!side) {
                portrait.setFlipX(true);
            }
        }
    }

    public void setActive(boolean active) {
        if (this.active==active)
            return;
        this.active = active;
        if (active){
            highlight.fadeIn();
        }else
            highlight.fadeOut();

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
