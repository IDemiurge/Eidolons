package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.particles.EmitterActor;

import java.util.Collection;

public abstract class GroupWithEmitters<T extends EmitterActor> extends GroupX {

    public abstract Collection<T> getEmitters();

    @Override
    public void draw(Batch batch, float parentAlpha) {
        draw(batch, parentAlpha,
                !GridPanel.isDrawEmittersOnTop() );
    }

    public void draw(Batch batch, float parentAlpha, boolean drawEmitters) {
        for (Actor child : getChildren()) {
            if (child instanceof EmitterActor) {
                // if (((EmitterActor) child).isWithinCamera()) {
                //     child.setVisible(false);
                // } else
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
//        if (drawEmitters) {
//            if (invertScreen)
//            {
//                ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
//            }
//            else
//            {
//                ((CustomSpriteBatch) batch).resetBlending();
//            }
//        }
        super.draw(batch, parentAlpha);
    }

    public void setInvertScreen(boolean invertScreen) {
        for (Actor child : getChildren()) {
            if (child instanceof EmitterActor) {
                ((EmitterActor) child).setInvert(invertScreen);
            }
        }
    }
}
