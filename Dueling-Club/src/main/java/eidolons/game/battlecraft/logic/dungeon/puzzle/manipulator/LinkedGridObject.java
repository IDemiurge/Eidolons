package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.particles.EmitterActor;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.VFX;
import main.content.enums.entity.BfObjEnums.CUSTOM_OBJECT;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

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
        CUSTOM_OBJECT.SMALL_LEVIATHAN.spriteColor = new Color(1, 1, 1, 0.7f);
        CUSTOM_OBJECT.SMALL_CLAW.spriteColor = new Color(1, 1, 1, 0.7f);

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

//        CUSTOM_OBJECT.black_waters.screen = true;
        CUSTOM_OBJECT.BLACKNESS.vfxUnderMirrorX = true;

        CUSTOM_OBJECT.soul_net.vfxOver += VFX.soulflux_continuous.path + "(-42, 32);";
        CUSTOM_OBJECT.soul_net.vfxOver += VFX.soulflux_continuous.path + "(42, 32);";
        CUSTOM_OBJECT.soul_net.setVfxSpeed(0.1f);


        CUSTOM_OBJECT.wisp_floating.vfxOver += "ambient/sprite/willowisps(0, 0);";

        CUSTOM_OBJECT.flames.vfxOver += "ambient/sprite/fires/real fire2(0, 0);";


//        CUSTOM_OBJECT.nether_flames.vfxUnder += "ambient/sprite/fires/nether flame green(0, 0);";
//        CUSTOM_OBJECT.nether_flames.vfxFolderOver = "ambient/sprite/fires/nether flames";


//        CUSTOM_OBJECT.nether_flames.vfxFolderUnder = "ambient/sprite/fires/hypno";
//        CUSTOM_OBJECT.nether_flames.maxEmitters = 1;

//        CUSTOM_OBJECT.nether_flames.setVfxSpeed(0.32f);

//        CUSTOM_OBJECT.nether_flames.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire mass slow up narrow2(0, 0);";
//        CUSTOM_OBJECT.burning_rubble.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire mass slow up narrow2(0, 0);";

        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.vfxOver +=
                "ambient/sprite/fires/hypno/best/hypnofire mass slow up narrow2(0, 0);";

        CUSTOM_OBJECT.smoke.vfxOver +=
                "invert/smoke large(0, 0);";

        CUSTOM_OBJECT.power_field.setVfxSpeed(0.34f);
        CUSTOM_OBJECT.nether_flames.setVfxSpeed(0.4f);
        CUSTOM_OBJECT.burning_rubble.setVfxSpeed(0.4f);
        CUSTOM_OBJECT.smoke.setVfxSpeed(0.64f);
        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.setVfxSpeed(0.74f);
        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.spriteColor = new Color(1, 1, 1, 0.34f);
//        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.invert_screen_vfx = true;

        CUSTOM_OBJECT.anti_flame.copy(CUSTOM_OBJECT.hypnotic_flames_mass_narrow);
        CUSTOM_OBJECT.anti_flame.invert_screen_vfx = true;
        CUSTOM_OBJECT.anti_flame.spritePath = null ;
        CUSTOM_OBJECT.smoke.invert_screen_vfx = true;


//        CUSTOM_OBJECT.hypnotic_flames_slow_up.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire slow up fine(0, 0);";
        CUSTOM_OBJECT.hypnotic_flames_slow_up.vfxOver += "ambient/sprite/fires/hypno/mass/hypnofire green slow up fine(0, 0);";

        CUSTOM_OBJECT.hypnotic_flames.vfxOver +=
                "ambient/sprite/fires/hypno/mass/hypnofire mass supernarrow(0, 0);";

        CUSTOM_OBJECT.hypnotic_flames_green.vfxOver +=
                "ambient/sprite/fires/hypno/mass/hypnofire mass supernarrow green(0, 0);";
        CUSTOM_OBJECT.hypnotic_flames_red.vfxOver +=
                "ambient/sprite/fires/hypno/mass/hypnofire mass supernarrow red(0, 0);";


        CUSTOM_OBJECT.hypnotic_flames_red.always_visible = true;
        CUSTOM_OBJECT.hypnotic_flames.always_visible = true;
        CUSTOM_OBJECT.hypnotic_flames_green.always_visible = true;
        CUSTOM_OBJECT.hypnotic_flames_mass.always_visible = true;
        CUSTOM_OBJECT.hypnotic_flames_mass.spriteColor = new Color(1, 1, 1, 0.57f);
        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.spriteColor = new Color(1, 1, 1, 0.57f);
        CUSTOM_OBJECT.hypnotic_flames_slow_up.always_visible = true;
        CUSTOM_OBJECT.hypnotic_flames_pale.always_visible = true;
        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.always_visible = true;

        CUSTOM_OBJECT.smoke.always_visible = true;

//        CUSTOM_OBJECT.hypnotic_flames_green.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire supernarrow green(0, 0);";
//        CUSTOM_OBJECT.hypnotic_flames_pale.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire pale2(0, 0);";
//        CUSTOM_OBJECT.hypnotic_flames.vfxFolderUnder = "ambient/sprite/fires/hypno/best";

        //portal.  ambient/sprite/swarm of light

//        CUSTOM_OBJECT.nether_flames.vfxOver += "spell/nether/soul flame ambi(0, 0);";
//        CUSTOM_OBJECT.burning_rubble.vfxOver += "spell/nether/toxic fumes(0, 0);";

        CUSTOM_OBJECT.crematory.vfxOver += "ambient/sprite/fires/real fire2(0, -40);";
        CUSTOM_OBJECT.crematory.setVfxSpeed(0.14f);

        CUSTOM_OBJECT.black_waters.vfxOver += "advanced/ambi/black water square small slow(-21, -21);";
        CUSTOM_OBJECT.black_waters.setVfxSpeed(0.6f);

//        CUSTOM_OBJECT.force_field.vfxOver += "spell/nether/soul flame ambi(0, 0);";
//        CUSTOM_OBJECT.force_field.vfxOver += "spell/nether/toxic fumes(0, 0);";
        CUSTOM_OBJECT.force_field.setVfxSpeed(0.34f);
        CUSTOM_OBJECT.force_field.always_visible = true;
        CUSTOM_OBJECT.smoke.always_visible = true;
//        CUSTOM_OBJECT.force_field.attach=true;
//        CUSTOM_OBJECT.force_field.maxEmitters=1;
//        CUSTOM_OBJECT.force_field.vfxChance=0.12f;
//        CUSTOM_OBJECT.force_field.vfxFolderOver ="spell/weave";
//        CUSTOM_OBJECT.force_field.screen=true;
        CUSTOM_OBJECT.force_field.blending = GenericEnums.BLENDING.INVERT_SCREEN;

//        CUSTOM_OBJECT.power_field.vfxOver ="spell/weave/nether weave(0, -0)";
        CUSTOM_OBJECT.power_field.vfxOver = "ambient/sprite/swarm of light(0, -0)";
        //portal.  ambient/sprite/swarm of light
        CUSTOM_OBJECT.power_field.always_visible = true;

        CUSTOM_OBJECT.fire_light.attach = true;
        CUSTOM_OBJECT.LEVIATHAN.attach = true;
        CUSTOM_OBJECT.LEVIATHAN.always_visible = true;
        CUSTOM_OBJECT.LEVIATHAN.blending = GenericEnums.BLENDING.INVERT_SCREEN;
        CUSTOM_OBJECT.BIG_CLAW.blending = GenericEnums.BLENDING.INVERT_SCREEN;
        CUSTOM_OBJECT.BIG_CLAW.backAndForth = true;
        CUSTOM_OBJECT.SMALL_CLAW.backAndForth = true;
//        CUSTOM_OBJECT.SMALL_CLAW.blending = GenericEnums.BLENDING.INVERT_SCREEN;

        CUSTOM_OBJECT.BLACK_CHAINS.blending = GenericEnums.BLENDING.INVERT_SCREEN;
        CUSTOM_OBJECT.BLACK_CHAINS.spriteColor = new Color(0.6f, 1, 0.4f, 0.4f);

        CUSTOM_OBJECT.LEVIATHAN.vfxOver += "advanced/ambi/black water square small slow(-21, -21);";

//        CUSTOM_OBJECT.keserim.alpha = 0.6f;
        CUSTOM_OBJECT.keserim.alpha_template = GenericEnums.ALPHA_TEMPLATE.BLOOM;

        CUSTOM_OBJECT.dark_chrysalis.invert_screen_vfx = true;
        CUSTOM_OBJECT.dark_chrysalis.blending = GenericEnums.BLENDING.INVERT_SCREEN;
//        CUSTOM_OBJECT.dark_chrysalis.always_visible = true;
        CUSTOM_OBJECT.dark_chrysalis.vfxOver = "unit/bloody bleed2(-50, 45);";


        CUSTOM_OBJECT.black_tendrils.blending = GenericEnums.BLENDING.INVERT_SCREEN;
        CUSTOM_OBJECT.nether_tendrils.spriteColor = new Color(0.6f, 1, 0.4f, 1);
        CUSTOM_OBJECT.black_wings.blending = GenericEnums.BLENDING.INVERT_SCREEN;
        CUSTOM_OBJECT.black_wing.blending = GenericEnums.BLENDING.INVERT_SCREEN;

//        CUSTOM_OBJECT.black_waters.vfxFolderOver  =   "advanced/ambi/waters;";
//        CUSTOM_OBJECT.black_waters.vfxChance = 0.1f;

//        CUSTOM_OBJECT.nether_flames.movable = true;
//        CUSTOM_OBJECT.nether_flames.spriteColor = new Color(0.57f, 0.99f, 0.78f, 0.78f);
//        CUSTOM_OBJECT.nether_flames.vfxChance = 44;

//        CUSTOM_OBJECT.burning_rubble.movable = true;
//        CUSTOM_OBJECT.burning_rubble.setVfxSpeed(0.22f);
//        CUSTOM_OBJECT.burning_rubble.screen = true;
//        CUSTOM_OBJECT.burning_rubble.spriteColor = new Color(0.57f, 0.99f, 0.78f, 0.78f);
//        CUSTOM_OBJECT.burning_rubble.vfxFolderOver = "ambient/sprite/fires/rubble";
//        CUSTOM_OBJECT.burning_rubble.maxEmitters = 1;
//        CUSTOM_OBJECT.burning_rubble.vfxChance = 44;
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
//        CUSTOM_OBJECT.hypnotic_flames_green.vfxFolderUnder = "ambient/sprite/fires/hypno";
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
//        CUSTOM_OBJECT.hypnotic_flames_green.vfxFolderUnder = "ambient/sprite/fires/hypno";
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
//        CUSTOM_OBJECT.hypnotic_flames_green.vfxFolderUnder = "ambient/sprite/fires/hypno/hypnofire green2";
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
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

        setKey(StringMaster.getWellFormattedString(object.toString()));
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
