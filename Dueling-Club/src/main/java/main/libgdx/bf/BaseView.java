package main.libgdx.bf;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.active.DefaultActionHandler;
import main.libgdx.bf.mouse.BattleClickListener;
import main.system.GuiEventManager;

import static main.system.GuiEventType.CALL_BLUE_BORDER_ACTION;

public class BaseView extends SuperActor {
    protected final TextureRegion originalTexture;
    protected   TextureRegion originalTextureAlt;
    protected Image portrait;
    private Image altPortrait;

    public BaseView(UnitViewOptions o) {
        this(o.getPortrateTexture());

    }


    public BaseView(TextureRegion portraitTexture) {
        portrait = new Image(portraitTexture);
        originalTexture = portraitTexture;
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


                    if (isAlt())
                        try {
                            if (DefaultActionHandler.leftClickActor(event, getX(), getY()))
                                return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    GuiEventManager.trigger(CALL_BLUE_BORDER_ACTION, BaseView.this);
                }
            }
        });
    }

    public Image getPortrait() {
        return portrait;
    }

    public void setPortrait(Image portrait) {
        this.portrait = portrait;
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
    public void setVisible(boolean visible) {
        if (!isVisible())
            if (visible){
                super.setVisible(visible);
            }
        super.setVisible(visible);
    }

    public void setAltPortrait(Image altPortrait) {
        this.altPortrait = altPortrait;
    }

    public Image getAltPortrait() {
        return altPortrait;
    }
}
