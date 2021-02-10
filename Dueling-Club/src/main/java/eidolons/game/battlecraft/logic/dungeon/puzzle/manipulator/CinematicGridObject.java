package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.libgdx.particles.EmitterActor;
import main.content.enums.entity.BfObjEnums;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.List;

public class CinematicGridObject extends GridObject {

    protected BfObjEnums.CUSTOM_OBJECT object;
    protected List<CinematicGridObject> additional;
    protected float origX;
    protected float origY;
    private Double range;

    public CinematicGridObject(Coordinates c,   BfObjEnums.CUSTOM_OBJECT object) {
        super(c, object.spritePath);
        this.object = object;
        if (object.additionalObjects.length > 0) {
            createAdditionalObjects(object.additionalObjects);
        }
    }


    protected void createAdditionalObjects(String[] additionalObjects) {
        additional = new ArrayList<>();
        int i = 1;
        for (String additionalObject : additionalObjects) {
            object = new EnumMaster<BfObjEnums.CUSTOM_OBJECT>().retrieveEnumConst(BfObjEnums.CUSTOM_OBJECT.class, VariableManager.removeVarPart(additionalObject));
            CinematicGridObject obj = new CinematicGridObject(c, object) {
                @Override
                public boolean isVisible() {
                    return CinematicGridObject.this.isVisible();
                }

                @Override
                public boolean checkVisible() {
                    return CinematicGridObject.this.checkVisible();
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

    @Override
    protected void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        super.initEmitter(emitter, offsetX, offsetY);
        emitter.setSpeed(object.getVfxSpeed());
        emitter.act(RandomWizard.getRandomFloat());
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
    protected boolean isClearshotRequired() {
        return false;
    }

    @Override
    protected double getDefaultVisionRange() {
        return 0;
    }

    @Override
    public double getVisionRange() {
        if (range!=null) {
            return range;
        }
        return visionRange;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    @Override
    protected int getFps() {
        return 20;
    }
}
