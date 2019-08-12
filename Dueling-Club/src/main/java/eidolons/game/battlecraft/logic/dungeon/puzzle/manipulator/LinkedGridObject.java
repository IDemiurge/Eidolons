package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.particles.EmitterActor;
import main.content.enums.GenericEnums.VFX;
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

        CUSTOM_OBJECT.soul_net.vfxOver += VFX.soulflux_continuous.path + "(-42, 32);";
        CUSTOM_OBJECT.soul_net.vfxOver += VFX.soulflux_continuous.path + "(42, 32);";
        CUSTOM_OBJECT.soul_net.setVfxSpeed(0.1f);

        CUSTOM_OBJECT.crematory.vfxOver  =   "advanced/ambi/waters/fire(0, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "advanced/ambi/waters/fire(-45, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "advanced/ambi/waters/fire(45, 0);";


        CUSTOM_OBJECT.wisp_floating.vfxOver  +=   "ambient/sprite/willowisps(0, 0);";
        CUSTOM_OBJECT.wisp_floating.screen = true;

        CUSTOM_OBJECT.flames.vfxOver  +=   "ambient/sprite/fires/real fire2(0, 0);";
        CUSTOM_OBJECT.flames.screen = true;

        CUSTOM_OBJECT.nether_flames.vfxUnder  +=   "ambient/sprite/fires/nether flame(0, 0);";
        CUSTOM_OBJECT.nether_flames.setVfxSpeed(0.62f);
        CUSTOM_OBJECT.nether_flames.screen = true;
        CUSTOM_OBJECT.nether_flames.movable = true;


        CUSTOM_OBJECT.crematory.vfxOver  +=   "ambient/sprite/fire small(-33, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "ambient/sprite/fire small(0, 0);";
        CUSTOM_OBJECT.crematory.vfxOver  +=   "ambient/sprite/fire small(33, 0);";
        CUSTOM_OBJECT.wisp_floating.invert_screen_vfx = true;
        CUSTOM_OBJECT.black_waters.vfxOver  +=   "advanced/ambi/black water square small slow(-21, -21);";
//        CUSTOM_OBJECT.black_waters.vfxFolderOver  =   "advanced/ambi/waters;";
//        CUSTOM_OBJECT.black_waters.vfxChance = 0.1f;


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

    float origX;
    float origY;

    public LinkedGridObject(BaseView view, CUSTOM_OBJECT object, Coordinates c) {
        super(c, object.spritePath);
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

    public BaseView getLinked() {
        return linked;
    }

    @Override
    protected void init() {
        super.init();

        origX = linked.localToStageCoordinates(new Vector2(0, 0)).x;
        origY = linked.localToStageCoordinates(new Vector2(0, 0)).y;

        if (object.screen) {
            if (sprite != null)
                sprite.setBlending(SuperActor.BLENDING.SCREEN);
        } else
        if (object.invert_screen_vfx) {
            setInvertScreen(true);
        }
    }

    @Override
    protected void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        super.initEmitter(emitter, offsetX, offsetY);
        emitter.setSpeed(object.getVfxSpeed());
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

        if (object.movable){
            for (EmitterActor emitterActor : emitters.keySet()) {
                emitterActor.setX(-origX + linked.localToStageCoordinates(new Vector2(0, 0)).x);
                emitterActor.setY(-origY + linked.localToStageCoordinates(new Vector2(0, 0)).y);
            }
        }
        if (sprite != null) {
            if (object.movable){
                sprite.setX(-origX + linked.localToStageCoordinates(new Vector2(0, 0)).x);
                sprite.setY(-origY+linked.localToStageCoordinates(new Vector2(0, 0)).y);

            }
            if (getLinked() instanceof OverlayView) {
                sprite.getSprite().setOffsetX(((OverlayView) getLinked()).getOffsetX());
                sprite.getSprite().setOffsetY(((OverlayView) getLinked()).getOffsetY());
            }
        }
    }

    @Override
    public boolean checkVisible() {
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
