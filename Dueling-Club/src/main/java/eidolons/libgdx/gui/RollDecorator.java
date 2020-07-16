package eidolons.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.anims.actions.ActionMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.TablePanel;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.images.ImageManager.STD_IMAGES;

/**
 * Created by JustMe on 2/26/2018.
 */
public class RollDecorator {
    public static RollableGroup decorate(Actor actor) {
        return decorate(actor, main.game.bf.directions.FACING_DIRECTION.WEST);
    }

    public static RollableGroup decorate(Actor actor, FACING_DIRECTION direction) {
        return decorate(actor, direction, true);

    }

    public static RollableGroup decorate(Actor actor, FACING_DIRECTION direction, boolean manual) {
        return decorate(actor, direction, manual, null );
    }
    public static RollableGroup decorate(Actor actor, FACING_DIRECTION direction, boolean manual,
                                         ButtonStyled.STD_BUTTON style) {
        RollableGroup group = new RollableGroup(actor, direction, manual, style);
        return group;
    }

    public static class RollableGroup extends GroupX {
        private final TablePanel table;
        private final Actor arrow;
        private final Actor contents;
        private final FACING_DIRECTION direction;
        private float origX;
        private float origY;

        Runnable onOpen;
        Runnable onClose;
        Runnable onEither;
        private float rollPercentage = 1f;
        private boolean rollIsLessWhenOpen;

        public RollableGroup(Actor contents, FACING_DIRECTION direction, boolean manual, ButtonStyled.STD_BUTTON style) {
            super(true);
            this.direction = direction;
            this.contents = contents;
            this.arrow = initArrow( style);
            if (!manual)
                arrow.setVisible(false);
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
            table.setSize(getWidth(),
                    getHeight());
            addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (getTapCount()>1) {
                        toggle();
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
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
            table.setSize(getWidth(),
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
//            setDebug(false, true);
        }


        private SymbolButton initArrow(ButtonStyled.STD_BUTTON style) {
            if (style == null) {
                style = ButtonStyled.STD_BUTTON.ARROW;
            }
            SymbolButton arrow = new SymbolButton(style, () -> {
                if (getActions().size > 0)
                    return;
                toggle();
            }){
                @Override
                public boolean isCheckClickArea() {
                    return false;
                }
            };
            arrow.setRotation(direction.getDirection().getDegrees() + 90);
            arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
            arrow.setTransform(true);
            return arrow;
        }

        private void toggle() {
            toggle(isOpen());
        }

        public void toggle(boolean open) {
            float toY = getY();
            float toX = getX();
//            int toX = open ? 0 : (int) -contents.getWidth();
            if (rollIsLessWhenOpen)
                switch (direction) {
                    case NORTH:
                        toY = open ? (int) origY + contents.getHeight() * rollPercentage : origY;
                        break;
                    case SOUTH:
                        toY = open ? (int) origY - contents.getHeight() * rollPercentage : origY;
                        break;
                    case WEST:
                        toX = open ? (int) origX - contents.getWidth() * rollPercentage : origX
                        ;
                        break;
                    case EAST:
                        toX = open ? (int) origX + contents.getWidth() * rollPercentage : origX
                        ;
                        break;
                }
            else
                switch (direction) {
                    case NORTH:
                        toY = open ? (int) origY + contents.getHeight() : origY
                                - (contents.getHeight() * (rollPercentage));
                        break;
                    case SOUTH:
                        toY = open ? (int) origY - contents.getHeight() * rollPercentage : origY
                                + (contents.getHeight() * (rollPercentage));
                        break;
                    case WEST:
                        toX = open ? (int) origX - contents.getWidth() : origX
                                + (contents.getWidth() * (rollPercentage));
                        break;
                    case EAST:
                        toX = open ? (int) origX + contents.getWidth() : origX
                                - (contents.getWidth() * (rollPercentage));
                        break;
                }

            ActionMaster.addMoveToAction(
                    this, toX, toY, getDuration());

            ActionMaster.addRotateByAction(
                    arrow, 180);

            if (onEither != null) {
                onEither.run();
            }
            if (open) {
                if (onOpen != null) {
                    onOpen.run();
                }
            } else {
                if (onClose != null) {
                    onClose.run();
                }
            }
        }

        public void setRollIsLessWhenOpen(boolean rollIsLessWhenOpen) {
            this.rollIsLessWhenOpen = rollIsLessWhenOpen;
        }

        public boolean isOpen() {
            switch (direction) {
               case NORTH:
                // case SOUTH:
                    return getY() <= origY;
               case SOUTH:
                // case NORTH:
                    return getY() >= origY;
                case WEST:
                    return getX() >= origX;
                case EAST:
                    return getX() <= origX;
                case NONE:
                    break;
            }
            return false;
        }

        private float getDuration() {
            return 0.5f;
        }

        public RollableGroup setOnOpen(Runnable onOpen) {
            this.onOpen = onOpen;
            return this;
        }

        public RollableGroup setOnClose(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }

        public RollableGroup setOnEither(Runnable onEither) {
            this.onEither = onEither;
            return this;
        }

        public void setRollPercentage(float rollPercentage) {
            this.rollPercentage = rollPercentage;
        }

        public float getRollPercentage() {
            return rollPercentage;
        }
    }


}
