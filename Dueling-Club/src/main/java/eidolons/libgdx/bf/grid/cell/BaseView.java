package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.libgdx.anims.actions.ActionMaster;
import eidolons.libgdx.anims.sprite.SpriteMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.bf.Hoverable;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS;
import main.system.GuiEventManager;

import java.util.List;

import static main.system.GuiEventType.TARGET_SELECTION;

public class BaseView extends SuperActor  implements Hoverable, Borderable {
    protected TextureRegion originalTexture;
    protected TextureRegion originalTextureAlt;
    protected TextureRegion borderTexture;
    protected FadeImageContainer portrait;
    private Image altPortrait;
    protected List<SpriteX> overlaySprites; //To-Cleanup - could be higher up or local
    protected List<SpriteX> underlaySprites;
    protected GroupX spritesContainers;
    protected GroupX spritesContainersUnder;
    protected FadeImageContainer highlight;
    private float expandWidth;
    private float expandHeight;
    protected CONTENT_CONSTS.FLIP flip;
    protected boolean forceTransform;

    public BaseView(UnitViewOptions o) {
        init(o);
    }

    public BaseView(TextureRegion portraitTexture, String path) {
        init(path);
    }

    public void init(UnitViewOptions o) {
        init(o.getPortraitPath());

    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!touchable)
            return this;
        return super.hit(x, y, touchable);
    }

    protected void initSprite(UnitViewOptions o) {
        if (isUseSpriteContainer(o.getObj())) {
            addActor(spritesContainersUnder = new GroupX() {
                        @Override
                        public void rotateBy(float amountInDegrees) {
                            super.rotateBy(amountInDegrees);
                            for (Actor child : getChildren()) {
                                child.rotateBy(amountInDegrees);
                            }
                        }
                    }
            );
            spritesContainersUnder.setZIndex(0);
        }

        underlaySprites = SpriteMaster.getSpriteForUnit(o.getObj(), false, this);
        if (underlaySprites != null) {
            for (SpriteX spriteX : underlaySprites) {
                if (isUseSpriteContainer(o.getObj())) {
                    spritesContainersUnder.addActor(spriteX);
                } else {
                    addActor(spriteX);
                    spriteX.setZIndex(0);
                }
                if (expandWidth < spriteX.getWidth()) {
                    expandWidth = spriteX.getWidth();
                }
                if (expandHeight < spriteX.getHeight()) {
                    expandHeight = spriteX.getHeight();
                }
                forceTransform = true;
            }
        }

        if (isUseSpriteContainer(o.getObj()))
            addActor(spritesContainers = new GroupX() {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    for (Actor child : getChildren()) {
                        child.setRotation(getRotation());
                    }
                }
            });
        overlaySprites = SpriteMaster.getSpriteForUnit(o.getObj(), true, this);
        if (overlaySprites != null) {
            for (SpriteX spriteX : overlaySprites) {
                if (isUseSpriteContainer(o.getObj()))
                    spritesContainers.addActor(spriteX);
                else {
                    addActor(spriteX);
                }
                if (expandWidth < spriteX.getWidth()) {
                    expandWidth = spriteX.getWidth();
                }
                if (expandHeight < spriteX.getHeight()) {
                    expandHeight = spriteX.getHeight();
                }
                forceTransform = true;
            }
        }
    }

    public boolean isWithinCameraCheck() {
        return getController().isWithinCamera(getX() + getWidth(), getY() + getHeight(), expandWidth + getWidth(), expandHeight + getHeight());
    }

    protected boolean isUseSpriteContainer(BattleFieldObject obj) {
        switch (obj.getName()) { //To-Cleanup
            case "Ash Vault":
            case "Eldritch Vault":
                return true;
        }
        return false;
    }

    public void init(String path) {
        portrait = initPortrait(path);
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

    protected FadeImageContainer initPortrait(String path) {
        originalTexture = processPortraitTexture(path);
        return new FadeImageContainer(new Image(originalTexture));
    }

    protected TextureRegion processPortraitTexture(String path) {
        TextureRegion  texture = TextureCache.getRegionUV(path);
        if (TextureCache.isEmptyTexture(texture)) {
            return getPlaceholderPortrait();
        }
        return texture;
    }

    protected TextureRegion getPlaceholderPortrait() {
        if (getUserObject() instanceof Structure) {
            if (((Structure) getUserObject()).isWall()) {
                return TextureCache.getRegionUV(Images.PLACEHOLDER_WALL);
            }
            return TextureCache.getRegionUV(Images.PLACEHOLDER_DECOR);
        } else {
            return TextureCache.getRegionUV(Images.PLACEHOLDER_UNIT);

        }
//        return TextureCache.getRegionUV(Images.PLACEHOLDER);
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

    public float getExpandWidth() {
        return expandWidth;
    }

    public float getExpandHeight() {
        return expandHeight;
    }

    public void highlight() {
        highlight.setColor(getTeamColor().r, getTeamColor().g, getTeamColor().b, highlight.getColor().a);
        highlight.fadeIn();
    }

    public void highlightOff() {
        if (highlight != null) {
            if (highlight.isVisible()) {
                highlight.fadeOut();
            }
        }
    }

    @Override
    public TextureRegion getBorder() {
        return borderTexture;
    }

    @Override
    public void setBorder(TextureRegion texture) {

        if (texture == null) {
            ActionMaster.addFadeOutAction(border, 0.65f, true);
            borderTexture = null;
            setTeamColorBorder(false);
        } else {
            if (borderTexture==texture) {
                return ;
            }
            if (border != null) {
                removeActor(border);
            }
            addActor(border = new Image(texture));
            border.getColor().a = 0;
            ActionMaster.addFadeInAction(border, 0.65f);

            borderTexture = texture;
            updateBorderSize();
        }
    }
}







