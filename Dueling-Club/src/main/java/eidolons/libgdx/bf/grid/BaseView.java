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
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;

import java.util.List;

import static main.system.GuiEventType.TARGET_SELECTION;

public class BaseView extends SuperActor {
    protected TextureRegion originalTexture;
    protected TextureRegion originalTextureAlt;
    protected FadeImageContainer portrait;
    private Image altPortrait;
    protected  List<SpriteX> overlaySprites;
    protected List<SpriteX> underlaySprites;
    protected boolean forceTransform;
    protected GroupX spritesContainers;
    protected FadeImageContainer highlight;

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
        addActor( spritesContainers = new GroupX());
        overlaySprites = SpriteMaster.getSpriteForUnit(o.getObj(), true);
        if (overlaySprites != null) {
            for (SpriteX spriteX : overlaySprites) {
                spritesContainers.addActor(spriteX);
                forceTransform = true;
            }
        }

        underlaySprites = SpriteMaster.getSpriteForUnit(o.getObj(), false);
        if (underlaySprites != null) {
            for (SpriteX spriteX : underlaySprites) {
              spritesContainers.addActor(spriteX);
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

        addActor(highlight = new FadeImageContainer(Images.COLORLESS_BORDER));
        highlight.setVisible(false);
        highlight.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT_SPEAKER);

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

    public List<SpriteX> getOverlaySprites() {
        return overlaySprites;
    }

    public List<SpriteX> getUnderlaySprites() {
        return underlaySprites;
    }

    public void highlight() {
        highlight.fadeIn();
        highlight.setColor(getTeamColor().r, getTeamColor().g, getTeamColor().b, 0);
//        firelight.fadeIn();

        //screen anim

        main.system.auxiliary.log.LogMaster.dev("highlight " );
    }
    public void highlightOff() {
        highlight.fadeOut();
    }
}







