package main.libgdx.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.generic.ImageContainer;
import main.system.images.ImageManager.STD_IMAGES;

/**
 * Created by JustMe on 2/26/2018.
 */
public class RollDecorator {
    public static RollableGroup decorate(Actor actor) {
        RollableGroup group = new RollableGroup(actor);
        return group;
    }

    public static class RollableGroup extends Group {
        ImageContainer arrow;
        Actor contents;

        public RollableGroup(Actor contents) {
            this.contents = contents;
            this.arrow = initArrow();
            addActor(arrow);
            addActor(contents);
        }

        private ImageContainer initArrow() {
            ImageContainer arrow = new ImageContainer(STD_IMAGES.DIRECTION_POINTER.getPath());
            arrow.setRotation(270);
            arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
            arrow.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (getActions().size > 0)
                        return true;
                    toggle();
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            return arrow;
        }

        private void open() {
            //wait for non-moving
            toggle(true);
        }

        private void toggle() {
            toggle(!isOpen());
        }

        private void toggle(boolean open) {
            int toX =0;// open ? 0 : -getMainWidth();

            ActorMaster.addMoveToAction(
             this, toX, getY(), getDuration());

            ActorMaster.addRotateByAction(
             arrow.getContent(), 180);
        }

        private boolean isOpen() {
            return getX() >= 0;
        }

        private float getDuration() {
            return 0.5f;
        }
    }


}
