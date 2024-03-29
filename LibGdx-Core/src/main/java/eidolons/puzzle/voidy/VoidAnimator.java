package eidolons.puzzle.voidy;

import com.badlogic.gdx.scenes.scene2d.Action;
import eidolons.puzzle.gridobj.CinematicGridObject;
import eidolons.puzzle.gridobj.GridObject;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.std.HitAnim;
import libgdx.anims.std.sprite.CustomSpriteAnim;
import libgdx.bf.grid.cell.GridCell;
import eidolons.system.audio.DC_SoundMaster;
import libgdx.screens.handlers.ScreenMaster;
import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.sound.AudioEnums;

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
                ScreenMaster.getGrid().getGdxY_ForModule(cell.getGridY()) * 128;
        // float x1 = x - (scale - actor.getScaleX()) * actor.getWidth() / 2;
        // float y1 = y - (scale - actor.getScaleY()) * actor.getHeight() / 2;
        //TODO IDEA: Maybe scaling from SLICE could look nice? basically, scaling only on one axis. Depending on facing...
        float offsetX = 0;
        float offsetY = 0;
        if (!raiseOrCollapse) {
            from = from.flip();
            if (handler.isCollapseDown()) if (from.growX == null) from = DIRECTION.DOWN;
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

        if (!raiseOrCollapse) {
            SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(
                    RandomWizard.random()?
                            HitAnim.getSpritePath(
                                    HitAnim.SPRITE_TYPE.STONE,
                                    HitAnim.HIT.SMASH) :
                    HitAnim.getSpritePath(
                    HitAnim.SPRITE_TYPE.DUST,
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
                    if (!handler.isCollapsing())
                        return;
                    CinematicGridObject flames;
                    gridObjects.add(flames = new CinematicGridObject(c, BfObjEnums.CUSTOM_OBJECT.hypnotic_flames_red));
                    flames.setRange(11d);
                    flames.fadeIn();
                    flames.addToGrid();
                }
            });
            AnimMaster.getInstance().customAnimation(anim);

        }
        //TODO wait mechanism for cellImage!
        if (waitPeriod > 0)
            ActionMasterGdx.addWaitAction(cell, waitPeriod);
        ActionMasterGdx.addCustomAction(cell, () -> playAnimSound(raiseOrCollapse));
        cell.getCellImgContainer().setVisible(true);
        ActionMasterGdx.addAlphaAction(cell.getCellImgContainer(), dur, !raiseOrCollapse);

        if (isScaleOn()) {
            scale = raiseOrCollapse ? 1f : 0.01f;
            scaleX = isVertScale() ? 100 * scale : scale;
            scaleY = scale;
            ActionMasterGdx.addScaleActionCentered(cell.getCellImage(), scaleX, scaleY, dur + waitPeriod);
        }
        if (raiseOrCollapse) {
            ActionMasterGdx.addMoveToAction(cell, x, y, dur);
        } else
            ActionMasterGdx.addMoveToAction(cell, x + offsetX, y + offsetY, dur);

        float delay = raiseOrCollapse ? dur / 5 : 0.01f;
        ActionMasterGdx.addDelayedAction(cell, delay, new Action() {
            @Override
            public boolean act(float delta) {
                cell.getUserObject().setVOID(!raiseOrCollapse);
                updatePillar(c);
                if (handler.isLogged())
                    log(1, cell + " toggled void to " + cell.getUserObject().isVOID());
                // checkUnitFalls(c);
                return true;
            }
        });

        DIRECTION direction = from;

        ActionMasterGdx.addAfter(cell, () -> {

            if (raiseOrCollapse) {
                handler.getRaised().put(cell, direction);
                handler.getCollapsed().remove(cell);
                if (handler.isLogged())
                    log(1, cell.getUserObject().getNameAndCoordinate() +
                            cell.getColor().a + " was raised; raised cells: " + handler.raised.size());
            } else {
                handler.getCollapsed().put(cell, direction);
                handler.getRaised().remove(cell);
                cell.setPosition(x, y);

                if (handler.isLogged())
                    log(1, cell.getUserObject().getNameAndCoordinate() +
                            cell.getColor().a + " had collapsed and been reset; collapsed cells: " + handler.getCollapsed().size());
            }


        });
    }

    private void checkUnitFalls(Coordinates c) {
        handler.gridPanel.getGridManager().getAnimHandler().doFall(c);
    }

    private void updatePillar(Coordinates c) {
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
                AudioEnums.STD_SOUNDS.NEW__TAB
                : AudioEnums.STD_SOUNDS.CHAIN);
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
