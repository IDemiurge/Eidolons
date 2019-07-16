package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.BaseView;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.VFX;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;

public class LinkedGridObject extends GridObject {
    static {
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.darkness.path+"(-132, 32);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.darkness.path+"(-132, 32);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.soul_bleed.path+"(-132, 64);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.soul_bleed.path+"(-132, 64);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.darkness.path+"(-132, 82);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.darkness.path+"(-132, 82);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.soul_bleed.path+"(-132, 124);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver+= VFX.soul_bleed.path+"(-132, 124);";

        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_ARCANE.path+"(32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_ARCANE.path+"(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_ARCANE.path+"(32, 0);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_ARCANE.path+"(-32, 0);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_ARCANE.path+"(32, -128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_ARCANE.path+"(-32, -128);";

        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.MIST_WIND.path+"(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.ASH.path+"(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.THUNDER_CLOUDS_CRACKS.path+"(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.SNOWFALL_THICK.path+"(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver+= VFX.WISPS.path+"(-32, 128);";

        CUSTOM_OBJECT.LIGHT.screen = true;
    }
    private final BaseView linked;
    CUSTOM_OBJECT object;

    public LinkedGridObject(BaseView view, CUSTOM_OBJECT object, Coordinates c ) {
        super(c, object.spritePath);
        linked = view;
        this.object = object;
        visionRange = getDefaultVisionRange();
        int i =1;
        for (String additionalObject : object.additionalObjects) {
            object =  new EnumMaster<CUSTOM_OBJECT>().retrieveEnumConst(CUSTOM_OBJECT.class, VariableManager.removeVarPart(additionalObject));
            LinkedGridObject obj = new LinkedGridObject(view, object, c);

            Coordinates offset =   AbstractCoordinates.createFromVars(additionalObject);
            obj.setPosition(offset.x, offset.y);
            if (i++%2==0) {
                obj.setFlipX(true);
            }
            addActor(obj);
        }
    }

    @Override
    protected void init() {
        super.init();
        if (object.screen) {
            sprite.setBlending(SuperActor.BLENDING.SCREEN);
        }
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
        for (String substring : ContainerUtils.openContainer(object.vfxUnder)) {
            Coordinates c =   AbstractCoordinates.createFromVars(substring);
            createEmitter(VariableManager.removeVarPart(substring), c.x, c.y);
        }
    }

    @Override
    protected void createEmittersOver() {
        for (String substring : ContainerUtils.openContainer(object.vfxOver)) {
            Coordinates c =   AbstractCoordinates.createFromVars(substring);
            createEmitter(VariableManager.removeVarPart(substring), c.x, c.y);
        }
    }

    @Override
    public boolean checkVisible() {
        if (!linked.isVisible()) {
            return false;
        }
        return super.checkVisible();
    }


}
