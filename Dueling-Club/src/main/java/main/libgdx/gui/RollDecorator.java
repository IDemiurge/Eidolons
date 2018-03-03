package main.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.images.ImageManager.STD_IMAGES;

/**
 * Created by JustMe on 2/26/2018.
 */
public class RollDecorator {
    public static RollableGroup decorate(Actor actor) {
        return decorate(actor, FACING_DIRECTION.WEST);
    }

    public static RollableGroup decorate(Actor actor, FACING_DIRECTION direction) {
        RollableGroup group = new RollableGroup(actor, direction);
        return group;
    }

    public static class RollableGroup extends Group {
        private TablePanel table;
        private ImageContainer arrow;
        private Actor contents;
        private FACING_DIRECTION direction;
        private float origX;
        private float origY;

        public RollableGroup(Actor contents, FACING_DIRECTION direction) {
            this.contents = contents;
            this.direction = direction;
            this.contents = contents;
            this.arrow = initArrow();
            table = new TablePanel();

            switch (direction) {
                case NORTH:
                    table.add(contents);
                    table.row();
                    table.add(arrow).top();
                    break;
                case WEST:
                    table.add(contents);
                    table.add(arrow).left();
                    break;
                case EAST:
                    table.add(arrow).right();
                    table.add(contents);
                    break;
                case SOUTH:
                    table.add(arrow).bottom();
                    table.row();
                    table.add(contents);
                    break;
            }
            addActor(table);
            table.setSize( getWidth(),
             getHeight());
        }

        @Override
        public void setPosition(float x, float y) {
            origX = x;
            origY = y;
            super.setPosition(x, y);
        }

        @Override
        public void act(float delta) {
            super.act(delta);

//            setSize(contents.getWidth() + STD_IMAGES.DIRECTION_POINTER.getWidth(),
//             contents.getHeight());
            table.setSize( getWidth(),
              getHeight());
        }

        @Override
        public float getHeight() {
            switch (direction) {
                case WEST:
                case EAST:
                    return contents.getHeight();
                case SOUTH:
                case NORTH:
                    return contents.getHeight() + STD_IMAGES.DIRECTION_POINTER.getHeight();
                case NONE:
                    break;
            }
            return super.getHeight();
        }

        @Override
        public float getWidth() {
            switch (direction) {
                case WEST:
                case EAST:
                    return contents.getWidth() + STD_IMAGES.DIRECTION_POINTER.getWidth();
                case SOUTH:
                case NORTH:
                    return contents.getWidth();
                case NONE:
                    break;
            }
            return super.getWidth();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            setDebug(false, true);
        }

        private ImageContainer initArrow() {
            ImageContainer arrow = new ImageContainer(STD_IMAGES.DIRECTION_POINTER.getPath());

            arrow.setRotation(direction.getDirection().getDegrees() + 90);
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
            toggle(isOpen());
        }

        private void toggle(boolean open) {
            float toY = getY();
            float toX = getX();
//            int toX = open ? 0 : (int) -contents.getWidth();

            switch (direction) {
                case NORTH:
                    toY = open ? (int) origY + contents.getHeight() : origY;
                    break;
                case WEST:
                    toX = open ? (int) origX - contents.getWidth() : origX;
                    break;
                case EAST:
                    toX =  open ? (int) origX + contents.getWidth() : origX;
                    break;
                case SOUTH:
                    toY = open ? (int) origY - contents.getHeight() : origY;
                    break;
            }

            ActorMaster.addMoveToAction(
             this, toX, toY, getDuration());

            ActorMaster.addRotateByAction(
             arrow.getContent(), 180);
        }

        private boolean isOpen() {
            switch (direction) {
                case NORTH:
                case SOUTH:
                    return getY() == origY;
                case WEST:
                case EAST:
                    return getX() == origX;
                case NONE:
                    break;
            }
            return false;
        }

        private float getDuration() {
            return 0.5f;
        }
    }


}
