package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.DC_Obj;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import main.system.GuiEventManager;

import static main.system.GuiEventType.TARGET_SELECTION;

public class BaseView extends SuperActor {
    protected TextureRegion originalTexture;
    protected TextureRegion originalTextureAlt;
    protected FadeImageContainer portrait;
    private Image altPortrait;

    public BaseView(UnitViewOptions o) {
        init(o);
    }

    public BaseView(TextureRegion portraitTexture, String path) {
        init(portraitTexture, path);
    }

    public void init(UnitViewOptions o) {
        init(o.getPortraitTexture(), o.getPortraitPath());
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

    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        originalTexture = processPortraitTexture(portraitTexture, path);
        return new FadeImageContainer(new Image(originalTexture));
    }

    protected TextureRegion processPortraitTexture(TextureRegion texture, String path) {
        return texture;
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
