package gdx.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import gdx.dto.EntityDto;
import gdx.general.view.ADtoView;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import main.content.enums.GenericEnums;

public class FieldView<T extends EntityDto> extends ADtoView<T> {

    private final FadeImageContainer portrait = new FadeImageContainer(); //empty mirror?
    private final boolean side;

    public FieldView(boolean side) {
        this.side = side;
        addActor(portrait);
    }

    @Override
    public void setScale(float scaleXY) {
        portrait.setScale(scaleXY);
        super.setScale(scaleXY);
    }

    @Override
    protected void update() {
        //TODO hierarchy of DTOs - good idea?
        portrait.setImage(dto.getEntity().getImagePath());
        if (!side) {
            portrait.setFlipX(true);
        }
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
