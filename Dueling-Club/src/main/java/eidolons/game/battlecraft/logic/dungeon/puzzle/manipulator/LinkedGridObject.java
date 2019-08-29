package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.particles.EmitterActor;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.VFX;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

import static main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT.GATE_PILLAR;
import static main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT.LIGHT;

public class LinkedGridObject extends CinematicGridObject {
    static {
        CUSTOM_OBJECT.BLACKNESS.vfxOver = VFX.darkness.path + "(-132, -32);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver += VFX.darkness.path + "(-132, -32);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder = VFX.soul_bleed.path + "(-132, -64);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder += VFX.soul_bleed.path + "(-132, -64);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver += VFX.darkness.path + "(-132, -82);";
        CUSTOM_OBJECT.BLACKNESS.vfxOver += VFX.darkness.path + "(-132, -82);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder += VFX.soul_bleed.path + "(-132, -124);";
        CUSTOM_OBJECT.BLACKNESS.vfxUnder += VFX.soul_bleed.path + "(-132, -124);";

        CUSTOM_OBJECT.GATE_PILLAR.vfxOver = VFX.MIST_ARCANE.path + "(32, 128);";
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


        CUSTOM_OBJECT.wisp_floating.vfxOver += "ambient/sprite/willowisps(0, 0);";
        CUSTOM_OBJECT.wisp_floating.screen = true;

        CUSTOM_OBJECT.flames.vfxOver += "ambient/sprite/fires/real fire2(0, 0);";
        CUSTOM_OBJECT.flames.screen = true;

        CUSTOM_OBJECT.nether_flames.vfxUnder += "ambient/sprite/fires/nether flame(0, 0);";
        CUSTOM_OBJECT.nether_flames.setVfxSpeed(0.32f);
        CUSTOM_OBJECT.nether_flames.screen = true;
        CUSTOM_OBJECT.nether_flames.movable = true;
        CUSTOM_OBJECT.nether_flames.spriteColor = new Color(0.57f, 0.99f, 0.78f, 0.78f);

        CUSTOM_OBJECT.burning_rubble.movable = true;
        CUSTOM_OBJECT.burning_rubble.setVfxSpeed(0.32f);
        CUSTOM_OBJECT.burning_rubble.screen = true;
        CUSTOM_OBJECT.burning_rubble.spriteColor = new Color(0.57f, 0.99f, 0.78f, 0.78f);
        CUSTOM_OBJECT.burning_rubble.vfxFolderOver = "ambient/sprite/fires/rubble";
        CUSTOM_OBJECT.burning_rubble.maxEmitters = 1;


        CUSTOM_OBJECT.crematory.vfxOver += "ambient/sprite/fires/real fire2(0, -40);";
        CUSTOM_OBJECT.crematory.screen = true;
        CUSTOM_OBJECT.crematory.setVfxSpeed(0.14f);

        CUSTOM_OBJECT.black_waters.vfxOver += "advanced/ambi/black water square small slow(-21, -21);";
        CUSTOM_OBJECT.black_waters.setVfxSpeed(0.6f);


        CUSTOM_OBJECT.keserim.screen = true;
//        CUSTOM_OBJECT.keserim.alpha = 0.6f;
        CUSTOM_OBJECT.keserim.alpha_template = GenericEnums.ALPHA_TEMPLATE.BLOOM;

        CUSTOM_OBJECT.dark_chrysalis.invert_screen_vfx = true;
        CUSTOM_OBJECT.dark_chrysalis.invert_screen = true;
//        CUSTOM_OBJECT.dark_chrysalis.always_visible = true;
        CUSTOM_OBJECT.dark_chrysalis.vfxOver  = "unit/bloody bleed2(-50, 45);";


        CUSTOM_OBJECT.black_tendrils.invert_screen = true;
        CUSTOM_OBJECT.black_wings.invert_screen = true;
        CUSTOM_OBJECT.bone_wings.screen = true;
        CUSTOM_OBJECT.black_wing.invert_screen = true;
        CUSTOM_OBJECT.bone_wing.screen = true;

        CUSTOM_OBJECT.GATE.screen = true;
        LIGHT.screen = true;
//        CUSTOM_OBJECT.black_waters.vfxFolderOver  =   "advanced/ambi/waters;";
//        CUSTOM_OBJECT.black_waters.vfxChance = 0.1f;
    }

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

        setKey( StringMaster.getWellFormattedString(object.toString()));
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
            if (object.screen) {
                sprite.setBlending(SuperActor.BLENDING.SCREEN);
            }
            if (object.invert_screen) {
                sprite.setBlending(SuperActor.BLENDING.INVERT_SCREEN);

            }
        }
        if (object.invert_screen_vfx) {
            setInvertScreen(true);
        }
    }

    @Override
    protected void initEmitter(EmitterActor emitter, int offsetX, int offsetY) {
        super.initEmitter(emitter, offsetX, offsetY);
        emitter.setSpeed(object.getVfxSpeed());
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
    }

    @Override
    public boolean checkVisible() {
        if (object.always_visible) {
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
