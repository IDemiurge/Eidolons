package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.screens.CustomSpriteBatch;

import java.util.Collection;
import java.util.Collections;

public abstract class GroupWithEmitters<T extends EmitterActor> extends GroupX {
    private boolean invertScreen;

    public abstract Collection<T> getEmitters();

    @Override
    public void draw(Batch batch, float parentAlpha) {
        draw(batch, parentAlpha, false);
    }

    public void draw(Batch batch, float parentAlpha, boolean drawEmitters) {
        for (Actor child : getChildren()) {
            if (child instanceof EmitterActor) {
                if (((EmitterActor) child).isWithinCamera()) {
                    child.setVisible(false);
                } else
                    child.setVisible(drawEmitters);
            } else {
                child.setVisible(!drawEmitters);
            }
//         for (EmitterActor e : getEmitters()) {
//            e.setVisible(true);
//         }
//      }else {
//         for (EmitterActor e : getEmitters()) {
//            e.setVisible(false);
//         }
        }
        setTransform(false);
        if (invertScreen)
        if (drawEmitters) {
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).setBlending(SuperActor.BLENDING.INVERT_SCREEN);
            }
        }
        super.draw(batch, parentAlpha);
    }

    public void setInvertScreen(boolean invertScreen) {
        this.invertScreen = invertScreen;
    }
}
