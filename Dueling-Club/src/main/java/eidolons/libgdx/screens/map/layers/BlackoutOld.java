package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 3/1/2018.
 */
public class BlackoutOld extends Group {
    public static final String path = PathFinder.getMacroUiPath() + "blackout.jpg";
    private final ImageContainer image;

    public BlackoutOld() {
        image = new ImageContainer(path);

    }

    @Override
    public Color getColor() {
        return image. getColor();
    }

    public void fadeOut(Float dur) {
        main.system.auxiliary.log.LogMaster.important("BLACKOUT  IN" + getColor().a);
        if (dur == null)
            dur = 1.5f;
        image.getContent().getColor().a = 0;
        image.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(image);
        ActionMaster.addFadeInAction(image.getContent(), dur);
    }

    public void fadeIn(Float dur) {
        main.system.auxiliary.log.LogMaster.important("BLACKOUT  OUT" + getColor().a);
        if (dur == null)
            dur = 1.5f;
        image.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(image);
        if (image.getContent().getColor().a != 1) {
            ActionMaster.addFadeInAndOutAction(image.getContent(), dur, false);
        } else
            ActionMaster.addFadeOutAction(image.getContent(), dur);
    }

    public void fadeOutAndBack() {
        fadeOutAndBack(null, null);
    }

    public void fadeOutAndBack(Runnable runnable) {
        fadeOutAndBack(null, runnable);
    }

//    public void fadeOutAndBack() {
//        fadeOutAndBack(null);
//    }

    public void fadeOutAndBack(Number dur, Runnable runnable) {
        main.system.auxiliary.log.LogMaster.important("BLACKOUT  fadeOutAndBack" + getColor().a);
        if (true){
            return;
        }
        if (dur == null)
            dur = 3f;

        if (dur instanceof Integer) {
            dur = Float.valueOf((int) dur);
        }
        image.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(image);
        if (runnable == null) {
            ActionMaster.addFadeInAndOutAction(image.getContent() , (Float) dur, false);
        return;
        }
        AlphaAction in =
                ActionMaster.addFadeInAction(image.getContent(), (Float) dur);
        Action run = new Action() {
            @Override
            public boolean act(float delta) {
                if (runnable != null) {
                    runnable.run();
                }
                return true;
            }
        };
        AlphaAction after = ActionMaster.addFadeOutAction(image.getContent(), (Float) dur);

//        image.getContent().clearActions();
        ActionMaster.addChained(image.getContent() , in, run, after);
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

    public static boolean isOnNewScreen() {
        return false;
    }

    public void fadeOutAndBack(Number v) {
        fadeOutAndBack(v, null);
    }
}
