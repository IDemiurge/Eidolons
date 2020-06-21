package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.OverlayView;
import eidolons.libgdx.particles.EmitterActor;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class LinkedGridObject extends CinematicGridObject {

    protected final BaseView linked;
    private List<LinkedGridObject> additional;
    private Fluctuating fluctuating;


    public LinkedGridObject(BaseView view, CUSTOM_OBJECT object, Coordinates c) {
        super(c, object);
        linked = view;
        this.object = object;
        visionRange = getDefaultVisionRange();
        if (object.additionalObjects.length > 0) {
            createAdditionalObjects(object.additionalObjects);
        }

        setKey(StringMaster.format(object.toString()));
    }

    protected void createAdditionalObjects(String[] additionalObjects) {
        if (linked == null) {
            return;
        }
        additional = new ArrayList<>();
        int i = 1;
        for (String additionalObject : additionalObjects) {
            object = new EnumMaster<CUSTOM_OBJECT>().retrieveEnumConst(CUSTOM_OBJECT.class, VariableManager.removeVarPart(additionalObject));
            LinkedGridObject obj = new LinkedGridObject(linked, object, c) {
                @Override
                public boolean isVisible() {
                    return LinkedGridObject.this.isVisible();
                }

                @Override
                public boolean checkVisible() {
                    return LinkedGridObject.this.checkVisible();
                }
            };

            Coordinates offset = AbstractCoordinates.createFromVars(additionalObject);
            obj.setPosition(offset.x, offset.y);
            if (i++ % 2 == 0) {
                obj.setFlipX(true);
            }
//            if (i>1) {
//
//            }
            addActor(obj);
            additional.add(obj);
        }
    }

    public BaseView getLinked() {
        return linked;
    }

    @Override
    protected void init() {
        super.init();

        origX = linked.localToStageCoordinates(new Vector2(0, 0)).x;
        origY = linked.localToStageCoordinates(new Vector2(0, 0)).y;

        if (object.alpha_template != null) {
            addActor(fluctuating = new Fluctuating(object.alpha_template));
        }
        if (sprite != null) {
            if (object.spriteColor != null) {
                sprite.setColor(object.spriteColor);
            }
            if (object.backAndForth) {
                sprite.getSprite().setBackAndForth(true);
            }
            sprite.setBlending(object.blending);
        }
        if (object.invert_screen_vfx) {
            setInvertScreen(true);
        }
        if (additional != null)
            for (LinkedGridObject linkedGridObject : additional) {
                linkedGridObject.act(RandomWizard.getRandomFloat());

            }
    }

    @Override
    protected void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        super.initEmitter(emitter, offsetX, offsetY);
        emitter.setSpeed(object.getVfxSpeed());
        emitter.act(RandomWizard.getRandomFloat());
    }

    protected boolean isIgnored() {
//    TODO     return !Eidolons.getScreen().controller.isWithinCamera(linked.getX(), linked.getY(),
//                2*linked.getWidth(),2* linked.getHeight());
        return false;
    }

    @Override
    protected boolean isClearshotRequired() {
        return true;
    }

    @Override
    protected double getDefaultVisionRange() {
        if (object == null) {
            return 0;
        }
        return object.range;
    }

    @Override
    protected int getFps() {
        return object.fps;
    }

    @Override
    protected void createEmittersUnder() {
        createEmittersFromString(object.vfxUnder, object.vfxUnderMirrorX, object.vfxUnderMirrorY, object.vfxChance);
        createEmittersFromFolder(object.vfxFolderUnder, object.vfxChance, object.maxEmitters);
    }


    @Override
    protected void createEmittersOver() {
        createEmittersFromString(object.vfxOver, object.vfxOverMirrorX, object.vfxOverMirrorY, object.vfxChance);
        createEmittersFromFolder(object.vfxFolderOver, object.vfxChance, object.maxEmitters);
    }

    @Override
    public void act(float delta) {
//        if (object== CUSTOM_OBJECT.black_waters) {
//            delta = delta / 2;
//        }
//        if (object== CUSTOM_OBJECT.soul_net) {
//            delta = delta / 10;
//        }
        super.act(delta);

//        for (Action action : getLinked().getActionsOfClass(MoveByAction.class)) {
//            if (action instanceof MoveByAction) {
//                ((MoveByAction) action).getAmountX();
//                ((MoveByAction) action).getAmountY();
//                ((MoveByAction) action).getDuration()
//            }
//        }

        if (object.movable) {
            for (EmitterActor emitterActor : emitters.keySet()) {
                emitterActor.setX(-origX + linked.localToStageCoordinates(new Vector2(0, 0)).x);
                emitterActor.setY(-origY + linked.localToStageCoordinates(new Vector2(0, 0)).y);
            }
        }
        if (sprite != null) {
            if (object.movable) {
                sprite.setX(-origX + linked.localToStageCoordinates(new Vector2(0, 0)).x);
                sprite.setY(-origY + linked.localToStageCoordinates(new Vector2(0, 0)).y);

            }
            if (getLinked() instanceof OverlayView) {
                sprite.getSprite().setOffsetX(((OverlayView) getLinked()).getOffsetX());
                sprite.getSprite().setOffsetY(((OverlayView) getLinked()).getOffsetY());
            }

            if (fluctuating != null) {
                fluctuating.fluctuate(delta);
                sprite.getColor().a = fluctuating.getColor().a;
            }
        }
        for (EmitterActor emitterActor : emitters.keySet()) {
            emitterActor.setSpeed(object.getVfxSpeed());

        }
    }

    @Override
    public boolean checkVisible() {
        if (linked != null)
            if (linked.getParent() == null) {
                return false;
            }
        if (object.always_visible || object.attach) {
            return true;
        }
        if (!object.ignore_linked_visible)
            if (!linked.isVisible()) {
                return false;
            }
//        if (isOverlapHiding()) TODO
        {
//            if (linked.getUserObject().getGame().getObjectsOnCoordinate(linked.getUserObject().getCoordinates()).size()>1){
//                linked.setZIndex(0);
//                return false;
//            }
        }

        return super.checkVisible();
    }


}
