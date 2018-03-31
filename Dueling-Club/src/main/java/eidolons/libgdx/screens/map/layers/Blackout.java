package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.libgdx.anims.ActorMaster;
import main.data.filesys.PathFinder;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.ImageContainer;

/**
 * Created by JustMe on 3/1/2018.
 */
public class Blackout extends Group {
    public static final String path = PathFinder.getMacroUiPath() + "blackout.jpg";
    private final ImageContainer image;

    public Blackout() {
        image = new ImageContainer(path);

    }

    public void fadeOut(Float dur) {
        if (dur == null)
            dur = 1.5f;
        image.getContent().getColor().a = 0;
        image.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(image);
        ActorMaster.addFadeInAction(image.getContent(), dur);
    }

    public void fadeIn(Float dur) {
        if (dur == null)
            dur = 1.5f;
        image.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(image);
        if (image.getContent().getColor().a != 1) {
            ActorMaster.addFadeInAndOutAction(image.getContent(), dur, false);
        } else
            ActorMaster.addFadeOutAction(image.getContent(), dur);
    }

    public void fadeOutAndBack(Float dur) {
        if (dur == null)
            dur = 3f;
        image.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(image);
        ActorMaster.addFadeInAndOutAction(image.getContent(), dur, false);
        //controller?
        //vignette?
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (image.getContent().getColor().a == 0) {
            setZIndex(0);
            image.setTouchable(Touchable.disabled);
        } else {
            setZIndex(Integer.MAX_VALUE);
            image.setTouchable(Touchable.enabled);
        }
    }
}