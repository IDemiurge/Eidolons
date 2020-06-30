package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid;

import com.badlogic.gdx.scenes.scene2d.Action;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.CinematicGridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.std.HitAnim;
import eidolons.libgdx.anims.std.sprite.CustomSpriteAnim;
import eidolons.libgdx.bf.grid.cell.GridCell;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.sound.SoundMaster;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static main.system.auxiliary.log.LogMaster.log;

public class VoidAnimator {
    VoidHandler handler;
    private final Set<GridObject> gridObjects = new LinkedHashSet<>();

    public VoidAnimator(VoidHandler handler) {
        this.handler = handler;
    }

    protected float getDelayBetweenAnims() {
        return 0.2f;
    }

    protected void animate(float waitPeriod, boolean raiseOrCollapse, GridCell cell, float speed, DIRECTION from) {
        if (isDisableGhostsAfterAnim())
            cell.setVoidAnimHappened(true);
        Coordinates c = cell.getUserObject().getCoordinates();

        //TODO fade void shadecell overlay!
        float dur = 1 * speed;
        float x = cell.getGridX() * 128;
        float y =
                handler.gridPanel.getGdxY_ForModule(cell.getGridY()) * 128;
        // float x1 = x - (scale - actor.getScaleX()) * actor.getWidth() / 2;
        // float y1 = y - (scale - actor.getScaleY()) * actor.getHeight() / 2;
        //TODO IDEA: Maybe scaling from SLICE could look nice? basically, scaling only on one axis. Depending on facing...
        float offsetX = 0;
        float offsetY = 0;
        if (!raiseOrCollapse) {
            from = from.flip();
            if (handler.collapseDown) if (from.growX == null) from = DIRECTION.DOWN;
            else from = from.growX ? DIRECTION.DOWN_RIGHT : DIRECTION.DOWN_LEFT;
        }
        //cell will move in a way to 'arrive' at where we are raising 'from'
        if (from.growY != null) offsetY = from.growY ? -64 : 64;
        if (from.growX != null) offsetX = from.growX ? -64 : 64;

        float scale, scaleX, scaleY;

        if (isScaleOn()) {
            scale = raiseOrCollapse ? 0.01f : 1;
            scaleX = isVertScale() ? 0.01f : scale;
            scaleY = scale;
            cell.setScale(scaleX, scaleY);
        }

        if (raiseOrCollapse) {
            cell.setPosition(x + offsetX, y + offsetY);
        } else
            cell.setPosition(x, y);
        cell.getCellImage().setVisible(true);
        if (raiseOrCollapse) {
            cell.getColor().a = 0;
        }

        if (!raiseOrCollapse) {
            SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(HitAnim.getSpritePath(HitAnim.SPRITE_TYPE.DUST,
                    HitAnim.HIT.SPLASH));
            sprite.setFps(40);
            CustomSpriteAnim anim = new CustomSpriteAnim(null,
                    sprite);
            sprite.setFlipX(RandomWizard.random());
            sprite.setFlipY(RandomWizard.random());

            AtomicInteger counter = new AtomicInteger(0);
            float finalOffsetX = offsetX;
            float finalOffsetY = offsetY;

            anim.setOrigin(c);

            anim.setOnDone(b -> {
                int times = 3;
                if (counter.getAndIncrement() < times) {
                    sprite.setAlpha(RandomWizard.getRandomFloat());
                    anim.setOffsetX(finalOffsetX / times * counter.get() * 2);
                    anim.setOffsetY(finalOffsetY / times * counter.get() * 2);
                    AnimMaster.getInstance().customAnimation(anim);
                } else {
                    CinematicGridObject flames;
                    gridObjects.add(flames = new CinematicGridObject(c, BfObjEnums.CUSTOM_OBJECT.flames));
                    flames.fadeIn();
                    flames.addToGrid();
                }
            });
            AnimMaster.getInstance().customAnimation(anim);

        }

        ActionMaster.addWaitAction(cell, waitPeriod);
        ActionMaster.addCustomAction(cell, () -> playAnimSound(raiseOrCollapse));
        ActionMaster.addAlphaAction(cell, dur, !raiseOrCollapse);

        if (isScaleOn()) {
            scale = raiseOrCollapse ? 1f : 0.01f;
            scaleX = isVertScale() ? 100 * scale : scale;
            scaleY = scale;
            ActionMaster.addScaleActionCentered(cell.getCellImage(), scaleX, scaleY, dur + waitPeriod);
        }
        if (raiseOrCollapse) {
            ActionMaster.addMoveToAction(cell, x, y, dur);
        } else
            ActionMaster.addMoveToAction(cell, x + offsetX, y + offsetY, dur);

        float delay =raiseOrCollapse?  dur / 5 : 0.01f;
        ActionMaster.addDelayedAction(cell, delay, new Action() {
            @Override
            public boolean act(float delta) {
                cell.getUserObject().setVOID(!raiseOrCollapse);
                updatePillar(c);
                if (handler.isLogged())
                    log(1, cell + " toggled void to " + cell.getUserObject().isVOID());
                return true;
            }
        });

        DIRECTION direction = from;

        ActionMaster.addAfter(cell, () -> {

            if (raiseOrCollapse) {
                handler.raised.put(cell, direction);
                handler.collapsed.remove(cell);
                if (handler.isLogged())
                    log(1, cell.getUserObject().getNameAndCoordinate() +
                            cell.getColor().a + " was raised; raised cells: " + handler.raised.size());
            } else {
                handler.collapsed.put(cell, direction);
                handler.raised.remove(cell);
                cell.setPosition(x, y);

                if (handler.isLogged())
                    log(1, cell.getUserObject().getNameAndCoordinate() +
                            cell.getColor().a + " had collapsed and been reset; collapsed cells: " + handler.collapsed.size());
            }


        });
    }

    private void updatePillar(Coordinates  c) {
        handler.gridPanel.getGridManager().getPillarManager().updateDynamicPillars(c, false);
    }


    protected boolean isVertScale() {
        return false;
    }

    protected boolean isScaleOn() {
        return false;
    }

    protected void playAnimSound(boolean raiseOrCollapse) {
        DC_SoundMaster.playStandardSound(raiseOrCollapse ?
                SoundMaster.STD_SOUNDS.NEW__TAB
                : SoundMaster.STD_SOUNDS.CHAIN);
    }

    protected boolean isDisableGhostsAfterAnim() {
        return true;
    }

    public void cleanUp() {
        for (GridObject gridObject : gridObjects) {
            gridObject.removeFromGrid();
        }
        gridObjects.clear();
    }
}
