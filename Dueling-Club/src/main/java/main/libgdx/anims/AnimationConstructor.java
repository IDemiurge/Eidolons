package main.libgdx.anims;

import main.ability.effects.Effect;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.oneshot.DealDamageEffect;
import main.content.*;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_QuickItemAction;
import main.entity.active.DC_SpellObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.particles.EmitterPools;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.sprite.SpriteAnimationFactory;
import main.libgdx.anims.std.*;
import main.libgdx.anims.std.SpellAnim.SPELL_ANIMS;
import main.libgdx.anims.weapons.Missile3dAnim;
import main.libgdx.anims.weapons.Ranged3dAnim;
import main.libgdx.anims.weapons.Reload3dAnim;
import main.libgdx.anims.weapons.Weapon3dAnim;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.OptionsMaster;

import java.util.*;

/**
 * Created by JustMe on 1/11/2017.
 */
public class AnimationConstructor {
    private final boolean preconstructOn = false; //TODO
    Map<DC_ActiveObj, CompositeAnim> map = new HashMap<>();
    private boolean autoconstruct = false;
    private VALUE[] anim_vals = {
//     PROPS.ANIM_MODS,
//
     PROPS.ANIM_SPRITE_PRECAST,
     PROPS.ANIM_SPRITE_CAST,
     PROPS.ANIM_SPRITE_RESOLVE,
     PROPS.ANIM_SPRITE_MAIN,
     PROPS.ANIM_SPRITE_IMPACT,
     PROPS.ANIM_SPRITE_AFTEREFFECT,
     PROPS.ANIM_MISSILE_SPRITE,
     PROPS.ANIM_MODS_SPRITE,
     PROPS.ANIM_MISSILE_SFX,
//
     PROPS.ANIM_SFX_PRECAST,
     PROPS.ANIM_SFX_CAST,
     PROPS.ANIM_SFX_RESOLVE,
     PROPS.ANIM_SFX_MAIN,
     PROPS.ANIM_SFX_IMPACT,
     PROPS.ANIM_SFX_AFTEREFFECT,
     PROPS.ANIM_MODS_SFX,
//
//
//     PROPS.ANIM_SPRITE_COLOR,
//     PROPS.ANIM_SFX_COLOR,
//     PROPS.ANIM_LIGHT_COLOR,
//
//
//     PROPS.ANIM_LIGHT_CAST,
//     PROPS.ANIM_LIGHT_RESOLVE,
//     PROPS.ANIM_LIGHT_MAIN,
//     PROPS.ANIM_LIGHT_IMPACT,
//     PROPS.ANIM_LIGHT_AFTEREFFECT,
//
//     PARAMS.ANIM_LIGHT_MISSILE,
//     PARAMS.ANIM_LIGHT_CASTER,
//     PARAMS.ANIM_LIGHT_TARGET,
//
//     PARAMS.ANIM_MAGNITUDE,
     PARAMS.ANIM_SPEED,
     PARAMS.ANIM_FRAME_DURATION,
//     PARAMS.ANIM_SIZE,
    };
    private boolean reconstruct = false;
    private boolean findClosestResource;

    public AnimationConstructor() {
        if (preconstructOn)
            GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED, p -> {
                if (isPreconstructOn((Unit) p.get()))
                    preconstructSpells((Unit) p.get());
            });
    }

    public static void preconstructAllForAV() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            DC_SpellObj active = new DC_SpellObj(type, Player.NEUTRAL, DC_Game.game, new Ref());
            AnimData data = null;
            try {
                int i = 0;
                for (ANIM_PART part : ANIM_PART.values()) {
                    data = new AnimationConstructor().getStandardData(active, part, i);
                    for (ANIM_VALUES val : ANIM_VALUES.values()) {
                        String identifier;
                        switch (val) {
                            case PARTICLE_EFFECTS:
                                identifier = "SFX";
                                break;
                            case SPRITES:
                                identifier = "SPRITE";
                                break;
                            default:
                                continue;
                        }
                        String value = data.getValue(val);
                        if (StringMaster.isEmpty(value))
                            continue;
                        value = value.replace(PathFinder.getImagePath().toLowerCase(), "");
                        i++;
                        PROPERTY prop = ContentManager.findPROP("anim" +
                         "_" + identifier + "_" + part);
                        if (prop == null)
                            continue;
                        type.setProperty(prop, value);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String findResourceForSpell(DC_SpellObj spell,
                                              String partPath, String size,
                                              PROPERTY[] props, String pathRoot,
                                              boolean closest) {
        String path = StringMaster.buildPath(
         pathRoot, partPath);
//        spell.getTargeting();
        String file = null;
        for (PROPERTY p : props) {
            String name = spell.getProperty(p);
            if (name.isEmpty()) continue;
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null) {
                break;
            }
            name = spell.getProperty(p);
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null) {
                break;
            }
            name = spell.getProperty(p) + " " + partPath + size;
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null) {
                break;
            }
        }
//        if (file != null || closest || isPartIgnored(partPath))

        if (CoreEngine.isJar())
            System.out.println(pathRoot + " root; file found " + file);
        return file;
//        return findResourceForSpell(spell, partPath, size, props, pathRoot, true);
    }

    private boolean isPreconstructOn(Unit unit) {
        if (unit.getSpells().size() > 3)
            return false;

        return true;
    }

    public CompositeAnim getOrCreate(ActiveObj active) {
        if (active == null) {
            return null;
        }
        CompositeAnim anim = map.get(active);
        if (!isReconstruct()) {
            if (anim != null) {
                anim.reset();
                return anim;
            }
        }
        try {
            anim = construct((DC_ActiveObj) active);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return anim;
    }

    public void preconstructSpells(Unit unit) {
        if (ListMaster.isNotEmpty(unit.getSpells())) {
            new Thread(() -> {
                unit.getSpells().forEach(spell -> getOrCreate(spell));
            }, unit + " Spell anim preconstruct thread").start();
        }
    }

    public void preconstructAll(Unit unit) {
        new Thread(() -> {
            unit.getActives().forEach(spell -> getOrCreate(spell));
        }, unit + "   anim preconstruct thread").start();
    }

    public void preconstruct(DC_ActiveObj active) {
        new Thread(() -> {
            getOrCreate(active);
        }, active + " anim preconstruct thread").start();
    }

    private CompositeAnim construct(DC_ActiveObj active) {

        //re-construct sometimes?
        CompositeAnim anim = new CompositeAnim(active);
//        active.getActionGroup()

        Arrays.stream(ANIM_PART.values()).forEach(part -> {
            if (!isPartIgnored(part)) {
                Anim animPart = getPartAnim(active, part, anim);
                if (animPart != null) {
                    anim.add(part, animPart);
                }
            }
        });

        map.put(active, anim);
        return anim;
    }

    public Anim getPartAnim(DC_ActiveObj active, ANIM_PART part, CompositeAnim anim) {
//        active.getProperty(sfx);
        AnimData data = new AnimData();
        for (VALUE val : anim_vals) {
            if (val instanceof PARAMETER || //TODO add filtering
             StringMaster.contains(val.getName(), part.toString())) {
                data.add(val, active.getValue(val));
            }
        }
        return getPartAnim(data, active, part, anim);
    }

    private Anim getPartAnim(AnimData data, DC_ActiveObj active,
                             ANIM_PART part, CompositeAnim composite) {
//TODO if (!checkAnimValid())
//    return null ;
        Anim anim = createAnim(active, data, part);
        if (anim == null) {
            return null;
        }
        if (!initAnim(data, active, part, anim)) {
            if (!(active instanceof DC_SpellObj)) {
                return null;
            }

            if (!autoconstruct)
                return null;
            data = getStandardData((DC_SpellObj) active, part, composite);
            if (!initAnim(data, active, part, anim)) {
                return null;
            }
        }
        anim.setMaster(AnimMaster.getInstance());
        return anim;
    }

    private Anim createAnim(DC_ActiveObj active, AnimData data, ANIM_PART part) {
        if (active.isMove()) {
            return MoveAnimation.isOn() ? new MoveAnimation(active, data) : null;
        }
        if (active instanceof DC_QuickItemAction) {
            if (((DC_QuickItemAction) active).getItem().isAmmo()) {
                return new Reload3dAnim(active);
            } else
                return new QuickItemAnim(((DC_QuickItemAction) active).getItem());

        }

        if (active.isAttackAny()) {
            if (AnimMaster3d.is3dAnim(active)) {
                if (active.isRanged()) {
                    if (part == ANIM_PART.CAST)
                        return new Ranged3dAnim(active);
                    if (part == ANIM_PART.MAIN)
                        return new Missile3dAnim(active);
                }
                if (part == ANIM_PART.MAIN)
                    return new Weapon3dAnim(active);

            } else if (part == ANIM_PART.MAIN) {

                if (active.isThrow()) {
                    return new ThrowAnim(active);
                }
                if (active.isRanged()) {
                    return new RangedAttackAnim(active);
                }
                return new AttackAnim(active);
            }
            if (part == ANIM_PART.IMPACT) {
                return new HitAnim(active, data);
            }
        }

        if (active.isSpell()) {
            if (active.isMissile()) {
                if (part == ANIM_PART.IMPACT) {
                    return new HitAnim(active, data);
                }
            } else if (part == ANIM_PART.MAIN) {
//                if (active.getTargetingMode().isSingle())
                if (active.getChecker().isTopDown())
                    return null;
            }
            SPELL_ANIMS template = getTemplateFromTargetMode(active.getTargetingMode());
            return new SpellAnim(active, data, template);
        }
        return new ActionAnim(active, data);
    }

    private SPELL_ANIMS getTemplateFromTargetMode(TARGETING_MODE targetingMode) {
        switch (targetingMode) {
            case NOVA:
            case RAY:
            case WAVE:
            case BLAST:
            case SPRAY:
                return new EnumMaster<SPELL_ANIMS>().retrieveEnumConst(SPELL_ANIMS.class, targetingMode.name());
        }
        return null;

    }

    private boolean initAnim(AnimData data,
                             DC_ActiveObj active, ANIM_PART part, Anim anim) {
        boolean exists = false;
        List<SpriteAnimation> sprites = new LinkedList<>();
        for (String path :
         StringMaster.openContainer(data.getValue(ANIM_VALUES.SPRITES))) {
            if (path.isEmpty()) {
                continue;
            }
            Chronos.mark("sprite " + path);
            sprites.add(SpriteAnimationFactory.getSpriteAnimation(path));
            Chronos.logTimeElapsedForMark("sprite " + path);
            exists = true;
        }
        List<EmitterActor> list = new LinkedList<>();
//     if (!Thread.currentThread().getName().contains("LWJGL")){
//
//     }
//       Eidolons.gdxApplication.postRunnable(()->{
//        list = EmitterPools.getEmitters(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS));
//        });

        try {
            list = EmitterPools.getEmitters(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS));
        } catch (Exception e) {
            e.printStackTrace();
            if (CoreEngine.isJar())
                System.out.println("getEffect failed" + data.getValue(ANIM_VALUES.PARTICLE_EFFECTS));

            list = EmitterPools.getEmitters(
             getPath(ANIM_VALUES.PARTICLE_EFFECTS) +
              data.getValue(ANIM_VALUES.PARTICLE_EFFECTS));
        }
        if (!list.isEmpty()) {
            exists = true;
        }

        if (!exists) {
            if (active != null) {
                exists = checkForcedAnimation(active, part);
            }
        }
//        if (!exists) return true;

        anim.setPart(part);
        anim.setSprites(sprites);
        anim.setEmitterList(list);
        return exists;
    }

    private boolean checkForcedAnimation(DC_ActiveObj active, ANIM_PART part) {
        switch (part) {
            case MAIN:
                return (active.isMove() || active.isAttackAny() || active.isTurn());
            case IMPACT:
                if (active.isAttackAny()) {
                    if (!active.isFailedLast()) {
                        return true;
                    }
                }
                break;
//            case AFTEREFFECT:
//                if (active.isAttackAny()) {
//                    if (active.getAttack()!=null )
//
//                        return true;
//                }
//

            case CAST:
                if (active instanceof DC_QuickItemAction)
                    return true;
                if (active.isRanged())
                    return true;
            case RESOLVE:
                break;
        }
        return false;
    }

    public Animation getEffectAnim(Effect e) {
//        map
        if (!isAnimated(e)) {
            return null;
        }
        LogMaster.log(LogMaster.ANIM_DEBUG, "EFFECT ANIM CONSTRUCTED FOR " + e + e.getRef().getInfoString());
        Anim effectAnim = EffectAnimCreator.getOrCreateEffectAnim(e);
        try {
            initAnim(effectAnim.getData(), (DC_ActiveObj) effectAnim.getActive(),
             effectAnim.getPart(),
             effectAnim);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (!isValid(effectAnim)) {
            return null;
        }

        return effectAnim;
//        CompositeAnim a = new CompositeAnim();
//        a.add(
//                effectAnim.getPart()
//                , effectAnim);
//        return a;
    }

    private boolean isAnimated(Effect e) {
        if (e.getActiveObj() == null) {
            return false;
        }
        if (!isCellAnimated(e)) {
            if (e.getRef().getTargetObj() instanceof DC_Cell) {

                return false;
            }
        }
        if (e instanceof DealDamageEffect) {
            return true;
        }
        if (e instanceof ModifyValueEffect) {
            if (e.isContinuousWrapped()) {
                return false;
            }
            return true;
        }

        return false;
    }

    private boolean isCellAnimated(Effect e) {
        return false;
    }

    AnimData getStandardData(DC_SpellObj spell, ANIM_PART part, CompositeAnim compositeAnim) {
        return getStandardData(spell, part, compositeAnim.getMap().size());
    }

    AnimData getStandardData(DC_SpellObj spell, ANIM_PART part, int partsCount) {
        AnimData data = new AnimData();

        String partPath = part.toString();
        if (part == ANIM_PART.MAIN) {
            partPath = "missile";
        }
        String size = "";
        if (spell.getCircle() > 4) {
            size = " huge";
        }
        if (spell.getCircle() >= 3) {
            size = " large";
        }
        if (spell.getCircle() < 2) {
            size = " small";
        }

        PROPERTY[] propsExact = {
         G_PROPS.NAME, G_PROPS.SPELL_SUBGROUP,
         G_PROPS.SPELL_GROUP, G_PROPS.ASPECT,
        };
        PROPERTY[] props = {
         PROPS.DAMAGE_TYPE,
         G_PROPS.SPELL_TYPE,
        };
         /*
        first, check for exact fit...
        if either has one, then don't search for other (unless also exact fit)
         */
        String pathRoot = getPath(ANIM_VALUES.SPRITES);
        String sprite = findResourceForSpell(spell, partPath, size, propsExact, pathRoot, false);
        pathRoot = getPath(ANIM_VALUES.PARTICLE_EFFECTS);
        String emitter = findResourceForSpell(spell, partPath, size, propsExact, pathRoot, false);
        if (sprite == null)
            if (emitter == null) {
                emitter = findResourceForSpell(spell, partPath, size, props, pathRoot, false);
                if (isFindClosestResource(part, ANIM_VALUES.PARTICLE_EFFECTS, partsCount)) {
                    if (emitter == null)
                        emitter = findResourceForSpell(spell, partPath, size, propsExact, pathRoot, true);
                    if (emitter == null)
                        emitter = findResourceForSpell(spell, partPath, size, props, pathRoot, true);

                }
                if (isFindClosestResource(part, ANIM_VALUES.SPRITES, partsCount))
                    if (emitter == null) {
                        pathRoot = getPath(ANIM_VALUES.SPRITES);
                        sprite = findResourceForSpell(spell, partPath, size, props, pathRoot, false);
                        if (sprite == null)
                            sprite = findResourceForSpell(spell, partPath, size, propsExact, pathRoot, true);
                        if (sprite == null)
                            sprite = findResourceForSpell(spell, partPath, size, props, pathRoot, true);


                    }
            }
        if (sprite != null) {
            String val = StringMaster.buildPath(
             partPath, StringMaster.removePreviousPathSegments(sprite, pathRoot));
            LogMaster.log(LogMaster.ANIM_DEBUG,
             "AUTO ANIM CONSTRUCTION FOR " + spell + "-" + part +
              ": " + ANIM_VALUES.SPRITES + " is set automatically to " + val);
            data.setValue(ANIM_VALUES.SPRITES, val);
        }
        if (emitter != null) {
            String val = StringMaster.buildPath(
             partPath, StringMaster.removePreviousPathSegments(emitter, pathRoot));
            LogMaster.log(LogMaster.ANIM_DEBUG,
             "AUTO ANIM CONSTRUCTION FOR " + spell + "-" + part +
              ": " + ANIM_VALUES.PARTICLE_EFFECTS + " is set automatically to " + val);
            data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, val);
        }


        return data;
    }

    private boolean isPartIgnored(String partPath) {
        partPath = partPath.replace("missile", "main");
        return isPartIgnored(new EnumMaster<ANIM_PART>().retrieveEnumConst(ANIM_PART.class, partPath));
    }

    private boolean isPartIgnored(ANIM_PART part) {
        switch (part) {
            case AFTEREFFECT:
                return !OptionsMaster.getAnimOptions().getBooleanValue(
                 ANIMATION_OPTION.AFTER_EFFECTS_ANIMATIONS);
            case CAST:
                return !OptionsMaster.getAnimOptions().getBooleanValue(
                 ANIMATION_OPTION.CAST_ANIMATIONS);
            case PRECAST:
                return !OptionsMaster.getAnimOptions().getBooleanValue(
                 ANIMATION_OPTION.PRECAST_ANIMATIONS);

//            case MAIN:
//            case IMPACT:
//                return false;
        }
        return false;
    }


    private String getPath(ANIM_VALUES s) {
        String path = null;
        switch (s) {
            case PARTICLE_EFFECTS:
                path = PathFinder.getSfxPath();
                break;
            case SPRITES:
                path = PathFinder.getSpritesPath();
                break;
        }
        if (CoreEngine.isJar())
            System.out.println(s + " path= " + path);
        return path;
    }


    public boolean isReconstruct() {
        return false;
//        return reconstruct;
    }

    public void setReconstruct(boolean reconstruct) {
        this.reconstruct = reconstruct;
    }

    public BuffAnim getBuffAnim(BuffObj buff) {
        BuffAnim anim = new BuffAnim(buff);
        DC_ActiveObj active = null;
        if (buff.getActive() instanceof DC_ActiveObj) {
            active = (DC_ActiveObj) buff.getActive();
        }
        initAnim(anim.getData(), active, anim.getPart(), anim);
        if (!isValid(anim)) {
            return null;
        }
        return anim;
    }

    private boolean isValid(Anim anim) {
        if (!anim.getSprites().isEmpty()) {
            return true;
        }
        if (!anim.getEmitterList().isEmpty()) {
            return true;
        }
        if (anim.getLightEmission() > 0) {
            return true;
        }
        if (anim instanceof HitAnim) {
            return true;
        }
        return false;
    }

    public boolean isFindClosestResource(ANIM_PART part, ANIM_VALUES val,
                                         int partsCount) {

        if (part != ANIM_PART.PRECAST)
            if (part != ANIM_PART.AFTEREFFECT)
                if (partsCount < 2)
                    return true;

        switch (part) {
            case MAIN:
                if (val == ANIM_VALUES.PARTICLE_EFFECTS) {
                    return true;
                }
        }
        return findClosestResource;
    }

    public void setFindClosestResource(boolean findClosestResource) {
        this.findClosestResource = findClosestResource;
    }

    public void preconstruct(Event event) {
        Anim anim = EventAnimCreator.getAnim(event);
        CompositeAnim parentAnim = AnimMaster.getInstance().getParentAnim(event.getRef());
    }

    public enum ANIM_PART {
        PRECAST(2F), //channeling
        CAST(2.5f),
        RESOLVE(2),
        MAIN(3), //flying missile
        IMPACT(1),
        AFTEREFFECT(2.5f);

        private float defaultDuration;

        ANIM_PART(float defaultDuration) {
            this.defaultDuration = defaultDuration;
        }

        public float getDefaultDuration() {
            return defaultDuration;
        }
    }


}
