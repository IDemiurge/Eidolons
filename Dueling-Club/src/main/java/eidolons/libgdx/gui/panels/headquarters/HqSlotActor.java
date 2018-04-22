package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import main.entity.DataModel;

/**
 * Created by JustMe on 4/18/2018.
 */
public abstract class HqSlotActor<T extends DataModel> extends GroupX{
    protected   String overlayPath;
    protected   T model;
    protected FadeImageContainer overlay;
    protected FadeImageContainer border;
    protected FadeImageContainer image;
    protected boolean dirty;

    public HqSlotActor(T model) {
        if (model == null) {
            addActor(image = new FadeImageContainer(getEmptyImage()));
        } else {
            this.model = model;
            addActor(image = new FadeImageContainer(model.getImagePath()));
            addActor(border = new FadeImageContainer());
            addActor(overlay = new FadeImageContainer());

            overlayPath =  getOverlay(model);
            if (overlayPath!=null )
                overlay.setImage(overlayPath );
            addListener(getListener());
        }

        image.setSize(64, 64);
        setSize(64, 64);
    }

    public void setOverlayPath(String overlayPath) {
        if (overlayPath == this.overlayPath)
            return;
        this.overlayPath = overlayPath;
        dirty = true;
    }

    protected abstract String getOverlay(T model);

    protected abstract String getEmptyImage() ;

    private EventListener getListener() {
        return new SmartClickListener(this){
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
        image.setSize(64, 64);
        if (dirty) {
            if (overlayPath != null)
                overlay.setImage(overlayPath);
            dirty = false;
        }
    }
}
