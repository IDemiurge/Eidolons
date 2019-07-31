package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.BaseView;
import main.content.enums.GenericEnums.VFX;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

import static main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT.GATE_PILLAR;
import static main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT.LIGHT;

public class LinkedGridObject extends GridObject {
    static {
        CUSTOM_OBJECT.BLACKNESS.vfxOver  = VFX.darkness.path + "(-132, -32);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver += VFX.darkness.path + "(-132, -32);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder  = VFX.soul_bleed.path + "(-132, -64);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder += VFX.soul_bleed.path + "(-132, -64);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver += VFX.darkness.path + "(-132, -82);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver += VFX.darkness.path + "(-132, -82);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder += VFX.soul_bleed.path + "(-132, -124);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder += VFX.soul_bleed.path + "(-132, -124);";

        CUSTOM_OBJECT.GATE_PILLAR.vfxOver  = VFX.MIST_ARCANE.path + "(32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.MIST_ARCANE.path + "(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.MIST_ARCANE.path + "(32, 0);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.MIST_ARCANE.path + "(-32, 0);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.MIST_ARCANE.path + "(32, -128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.MIST_ARCANE.path + "(-32, -128);";

        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.MIST_WIND.path + "(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.ASH.path + "(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.THUNDER_CLOUDS_CRACKS.path + "(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.SNOWFALL_THICK.path + "(-32, 128);";
        CUSTOM_OBJECT.GATE_PILLAR.vfxOver += VFX.WISPS.path + "(-32, 128);";

        LIGHT.screen = true;
        GATE_PILLAR.screen = true;
//        CUSTOM_OBJECT.black_waters.screen = true;
        CUSTOM_OBJECT.BLACKNESS.vfxUnderMirrorX = true;


        CUSTOM_OBJECT.crematory.vfxOver  =   "advanced/ambi/waters/fire(0, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "advanced/ambi/waters/fire(-45, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "advanced/ambi/waters/fire(45, 0);";

        CUSTOM_OBJECT.crematory.vfxOver  +=   "ambient/sprite/fire small(-33, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "ambient/sprite/fire small(0, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "ambient/sprite/fire small(33, 0);";

        CUSTOM_OBJECT.black_waters.vfxFolderOver  =   "advanced/ambi/waters;";
        CUSTOM_OBJECT.black_waters.vfxChance = 0.1f;
//        CUSTOM_OBJECT.black_waters.vfxOver += VFX.MIST_BLACK.path + "(32, 32);";
//        CUSTOM_OBJECT.black_waters.vfxUnder += VFX.BLACK_MIST_white_mist_wind.path + "(32, 64);";
//        CUSTOM_OBJECT.black_waters.vfxUnder += VFX.BLACK_MIST_clouds_wind.path + "(32, 64);";
//        CUSTOM_OBJECT.black_waters.vfxOver += VFX.BLACK_MIST_clouds_gravity.path + "(-32, -52);";
//        CUSTOM_OBJECT.black_waters.vfxUnder += VFX.BLACK_MIST_clouds_antigravity.path + "(-32, -24);";
//        CUSTOM_OBJECT.black_waters.vfxOver += VFX.darkness.path + "(32, 32);";
//        CUSTOM_OBJECT.black_waters.vfxUnder += VFX.soul_bleed.path + "(32, 64);";
//        CUSTOM_OBJECT.black_waters.vfxOver += VFX.darkness.path + "(-32, -52);";
//        CUSTOM_OBJECT.black_waters.vfxUnder += VFX.soul_bleed.path + "(-32, -24);";
    }

    private final BaseView linked;
    CUSTOM_OBJECT object;

    public LinkedGridObject(BaseView view, CUSTOM_OBJECT object, Coordinates c) {
        super(c, object.spritePath);
        if (object== LIGHT) {
            object = LIGHT;
        }
        linked = view;
        this.object = object;
        visionRange = getDefaultVisionRange();
        int i = 1;
        for (String additionalObject : object.additionalObjects) {
            object = new EnumMaster<CUSTOM_OBJECT>().retrieveEnumConst(CUSTOM_OBJECT.class, VariableManager.removeVarPart(additionalObject));
            LinkedGridObject obj = new LinkedGridObject(view, object, c);

            Coordinates offset = AbstractCoordinates.createFromVars(additionalObject);
            obj.setPosition(offset.x, offset.y);
            if (i++ % 2 == 0) {
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
        createEmittersFromString(object.vfxUnder, object.vfxUnderMirrorX, object.vfxUnderMirrorY, object.vfxChance);
        createEmittersFromFolder(object.vfxUnder,  object.vfxChance);
    }


    @Override
    protected void createEmittersOver() {
        createEmittersFromString(object.vfxOver, object.vfxOverMirrorX, object.vfxOverMirrorY, object.vfxChance);
        createEmittersFromFolder(object.vfxFolderOver,  object.vfxChance); }

    @Override
    public void act(float delta) {
        if (object== CUSTOM_OBJECT.black_waters) {
            delta = delta / 2;
        }
        super.act(delta);
    }

    @Override
    public boolean checkVisible() {
        sprite.setFps(getFps());
//        if (object== CUSTOM_OBJECT.black_waters) {
//            sprite.setFps(15);
//        }
        if (object== LIGHT) {
            return true;
        }
        if (!linked.isVisible()) {
            return false;
        }
        return super.checkVisible();
    }


}
