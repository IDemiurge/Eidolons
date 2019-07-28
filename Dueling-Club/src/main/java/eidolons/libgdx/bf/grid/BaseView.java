package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.libgdx.anims.sprite.SpriteMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;

import java.util.List;

import static main.system.GuiEventType.TARGET_SELECTION;

public class BaseView extends SuperActor {
    protected TextureRegion originalTexture;
    protected TextureRegion originalTextureAlt;
    protected FadeImageContainer portrait;
    private Image altPortrait;
    protected  List<SpriteX> overlaySprite;
    protected List<SpriteX> underlaySprite;
    protected boolean forceTransform;

    public BaseView(UnitViewOptions o) {
        init(o);
    }

    public BaseView(TextureRegion portraitTexture, String path) {
        init(portraitTexture, path);
    }

    public void init(UnitViewOptions o) {
        init(o.getPortraitTexture(), o.getPortraitPath());
    }

    protected void initSprite(UnitViewOptions o) {
        overlaySprite = SpriteMaster.getSpriteForUnit(o.getObj(), true);
        if (overlaySprite != null) {
            for (SpriteX spriteX : overlaySprite) {
                addActor(spriteX);
                forceTransform = true;
            }
        }

        underlaySprite = SpriteMaster.getSpriteForUnit(o.getObj(), false);
        if (underlaySprite != null) {
            for (SpriteX spriteX : underlaySprite) {
                addActor(spriteX);
                spriteX.setZIndex(0);
                forceTransform = true;
            }
        }
        //TODO disable hover?
        /**
         * fade?
         * scale?
         */
    }

    public void init(TextureRegion portraitTexture, String path) {
        portrait = initPortrait(portraitTexture, path);
        addActor(portrait);

        addListener(new BattleClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    event.handle();

                    GuiEventManager.trigger(TARGET_SELECTION, BaseView.this);
                }
            }
        });
    }

    @Override
    public int getFluctuatingAlphaPeriod() {
        return 0;
    }

    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        originalTexture = processPortraitTexture(portraitTexture, path);
        return new FadeImageContainer(new Image(originalTexture));
    }

    protected TextureRegion processPortraitTexture(TextureRegion texture, String path) {
        if (TextureCache.isEmptyTexture(texture)) {
            return getPlaceholderPortrait();
        }
        return texture;
    }

    protected TextureRegion getPlaceholderPortrait() {
        if (getUserObject() instanceof Structure) {
            if (((Structure) getUserObject()).isWall()) {
                return TextureCache.getOrCreateR(Images.PLACEHOLDER_WALL);
            }
            return TextureCache.getOrCreateR(Images.PLACEHOLDER_DECOR);
        } else {
            return TextureCache.getOrCreateR(Images.PLACEHOLDER_UNIT);

        }
//        return TextureCache.getOrCreateR(Images.PLACEHOLDER);
    }

    public FadeImageContainer getPortrait() {
        return portrait;
    }

    public void setOriginalTextureAlt(TextureRegion originalTextureAlt) {
        this.originalTextureAlt = originalTextureAlt;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        portrait.setSize(getWidth(), getHeight());

    }

    @Override
    public DC_Obj getUserObject() {
        return (DC_Obj) super.getUserObject();
    }

    @Override
    public void setVisible(boolean visible) {
        if (!isVisible())
            if (visible) {
                super.setVisible(visible);
            }
        super.setVisible(visible);
    }

    public Image getAltPortrait() {
        return altPortrait;
    }

    public void setAltPortrait(Image altPortrait) {
        this.altPortrait = altPortrait;
    }
}
