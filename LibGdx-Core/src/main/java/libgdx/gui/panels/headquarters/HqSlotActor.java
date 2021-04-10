package libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.GroupX;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.gui.generic.GroupX;
import libgdx.gui.tooltips.SmartClickListener;
import main.entity.DataModel;

/**
 * Created by JustMe on 4/18/2018.
 */
public abstract class HqSlotActor<T extends DataModel> extends GroupX {
    protected String overlayPath;
    protected T model;
    protected FadeImageContainer border;
    protected FadeImageContainer overlay;
    protected FadeImageContainer image;
    protected FadeImageContainer backgroundOverlay;
    protected FadeImageContainer background;
    protected boolean dirty;
    protected ClickListener listener;

    public HqSlotActor(T model) {
        background = createBackground();
        if (background != null) {
            addActor(background);
        }
        backgroundOverlay = createBackgroundOverlay(model);
        if (backgroundOverlay != null) {
            addActor(backgroundOverlay);
        }
        if (model == null) {
            addActor(image = new FadeImageContainer(getEmptyImage()));
        } else {
            this.model = model;
            addActor(image = new FadeImageContainer(getImagePath(model)));
            addActor(border = new FadeImageContainer());
            if (isOverlayOn()) {
                addActor(overlay = new FadeImageContainer());
                overlayPath = getOverlay(model);
                if (overlayPath != null) {
                    overlay.setImage(overlayPath);
                }
            }
        }
        if (isListenerRequired())
            addListener(listener = createListener());
        if (model != null) {
            initSize();
        }
    }

    protected void initSize() {
        image.setSize(64, 64);
        setSize(64, 64);
    }

    protected FadeImageContainer createBackgroundOverlay(T model) {
        return null;
    }

    protected FadeImageContainer createBackground() {
        return null;
    }

    protected String getImagePath(T model) {
        return model.getImagePath();
    }

    protected boolean isListenerRequired() {
        return model != null;
    }

    public boolean isOverlayOn() {
        return true;
    }

    public void setOverlayPath(String overlayPath) {
        if (overlayPath == this.overlayPath)
            return;
        this.overlayPath = overlayPath;
        dirty = true;
    }

    protected abstract String getOverlay(T model);

    protected abstract String getEmptyImage();

    public ClickListener getListener() {
        return listener;
    }

    protected ClickListener createListener() {
        return new SmartClickListener(this) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                if (event.getButton() == 1) {
                    //                     rightClick();
                }

            }


        };
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (dirty) {
            if (overlayPath != null)
                overlay.setImage(overlayPath);
            dirty = false;
        }
    }
}
