package main.libgdx.anims;

import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS2.SFX;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.specific.BuffObj;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.particles.ParticleEmitter;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.std.*;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.StringMaster;

import java.util.*;

/**
 * Created by JustMe on 1/11/2017.
 */
public class AnimationConstructor {
    VALUE[] anim_vals = {
//     PROPS.ANIM_MODS,
//
            PROPS.ANIM_SPRITE_CAST,
            PROPS.ANIM_SPRITE_RESOLVE,
            PROPS.ANIM_SPRITE_MAIN,
            PROPS.ANIM_SPRITE_IMPACT,
            PROPS.ANIM_SPRITE_AFTEREFFECT,
            PROPS.ANIM_MISSILE_SPRITE,
            PROPS.ANIM_MODS_SPRITE,
            PROPS.ANIM_MISSILE_SFX,
//
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
    Map<DC_ActiveObj, CompositeAnim> map = new HashMap<>();
    boolean reconstruct = false;
    private boolean findClosestResource;

    public CompositeAnim getOrCreate(ActiveObj active) {
        if (active == null) return null;
        CompositeAnim anim = map.get(active);
        if (!isReconstruct())
            if (anim != null) {

                return anim;
            }
        return construct((DC_ActiveObj) active);
    }

    CompositeAnim construct(DC_ActiveObj active) {
        return construct(active.getRef(), null, active);
    }

    private CompositeAnim construct(Ref ref, AnimData data, DC_ActiveObj active) {

        //re-construct sometimes?
        CompositeAnim anim = new CompositeAnim(active);
//        active.getActionGroup()


        Arrays.stream(ANIM_PART.values()).forEach(part -> {
            Anim animPart = getPartAnim(active, part);
            if (animPart != null)
                anim.add(part, animPart);
        });

        map.put(active, anim);
        return anim;
    }


    private Anim getPartAnim(DC_ActiveObj active, ANIM_PART part) {
//        active.getProperty(sfx);
        AnimData data = new AnimData();
        for (VALUE val : anim_vals) {
            if (val instanceof PARAMETER || //TODO add filtering
                    StringMaster.contains(val.getName(), part.toString()))
                data.add(val, active.getValue(val));
        }
        return getPartAnim(data, active, part);
    }

    private Anim getPartAnim(AnimData data, DC_ActiveObj active, ANIM_PART part) {

        Anim anim = createAnim(active, data, part);

        if (!initAnim(data, active, part, anim)) {
            if (!(active instanceof DC_SpellObj)) {
                return null;
            }

            data = getStandardData((DC_SpellObj) active, part);
            if (!initAnim(data, active, part, anim))
                return null;
        }
        return anim;
    }

    private Anim createAnim(DC_ActiveObj active, AnimData data, ANIM_PART part) {
        if (active.isMove())
            return new MoveAnimation(active, data);
        if (active.isAttackAny()) {
            if (part == ANIM_PART.MAIN) {
                if (active.isRanged())
                    return new RangedAttackAnim(active);
                return new AttackAnim(active);
            }
            if (part == ANIM_PART.IMPACT)
                return new HitAnim(active, data);
//            if (part == ANIM_PART.AFTEREFFECT)
//                if (lethal)
//                return new DeathAnim(active, data);
        }
        if (active.isSpell())
            if (active.isMissile()) {
                if (part == ANIM_PART.IMPACT)
                    return new HitAnim(active, data);

            }
        return new ActionAnim(active, data);
    }

    private boolean initAnim(AnimData data,
                             DC_ActiveObj active, ANIM_PART part, Anim anim) {
        boolean exists = false;
        List<SpriteAnimation> sprites = new LinkedList<>();
        for (String path :
                StringMaster.openContainer(data.getValue(ANIM_VALUES.SPRITES))) {
            sprites.add(new SpriteAnimation(path));
            exists = true;
        }
        List<ParticleEmitter> list = new LinkedList<>();
        for (String path :
                StringMaster.openContainer(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS))) {
            ParticleEmitter emitter = null;
            SFX sfx = new EnumMaster<SFX>().
                    retrieveEnumConst(SFX.class, path);
            if (sfx == null)
                emitter = new ParticleEmitter(path);
            else
                emitter = new ParticleEmitter(
                        sfx);
            if (emitter != null)
                list.add(emitter
                );
            exists = true;
        }

        if (!exists)
            if (active != null)
                exists = checkForcedAnimation(active, part);
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
                if (active.isAttackAny())
                    if (!active.isFailedLast())
                        return true;
                break;
//            case AFTEREFFECT:
//                if (active.isAttackAny()) {
//                    if (active.getAttack()!=null )
//
//                        return true;
//                }
//

            case CAST:
            case RESOLVE:
                break;
        }
        return false;
    }

    public CompositeAnim getEffectAnim(Effect e) {
//        map
        if (!isAnimated(e)) return null;
        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, "EFFECT ANIM CONSTRUCTED FOR " + e + e.getRef());
        Anim effectAnim = EffectAnimCreator.getEffectAnim(e);
        initAnim(effectAnim.getData(), (DC_ActiveObj) effectAnim.getActive(),
                effectAnim.getPart(),
                effectAnim);
        if (!isValid(effectAnim)) return null;
        CompositeAnim a = new CompositeAnim();
        a.add(
                effectAnim.getPart()
                , effectAnim);

        return a;
    }

    private boolean isAnimated(Effect e) {
        if (e.getActiveObj() == null) return false;

        if (e instanceof DealDamageEffect) return true;
        if (e instanceof ModifyValueEffect) {
            if (e.isContinuousWrapped())
                return false;
            return true;
        }

        return false;
    }


    AnimData getStandardData(DC_SpellObj spell, ANIM_PART part) {
        AnimData data = new AnimData();

        String partPath = part.toString();
        if (part == ANIM_PART.MAIN) partPath = "missile";
        String size = "";
        if (spell.getCircle() > 4)
            size = " huge";
        if (spell.getCircle() >= 3)
            size = " large";
        if (spell.getCircle() < 2)
            size = " small";

        ANIM_VALUES[] values = {
                ANIM_VALUES.SPRITES,
                ANIM_VALUES.PARTICLE_EFFECTS,
        };
//         getValuesForPart(part);
        PROPERTY[] props = {
                G_PROPS.NAME,
                G_PROPS.ASPECT,
                G_PROPS.SPELL_TYPE,
                G_PROPS.SPELL_GROUP,
                PROPS.DAMAGE_TYPE,
        };
        for (ANIM_VALUES s : values) {

            String pathRoot = getPath(s);
            String file = findResourceForSpell(spell, partPath, size, props, pathRoot, false);

            if (file == null) {
                if (!isFindClosestResource(part, s))
                    continue;
                file = findResourceForSpell(spell, partPath, size, props, pathRoot, true);
                if (file == null)
                    continue;
            }
            String val = StringMaster.buildPath(
                    partPath, StringMaster.removePreviousPathSegments(file, pathRoot));
            main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,
                    "AUTO ANIM CONSTRUCTION FOR " + spell + "-" + part + ": " + s + " is set automatically to " + val);
            data.setValue(s, val);
        }
//        for (String substring : StringMaster.openContainer(
//         spell.getProperty(G_PROPS.SPELL_TAGS))) {
//            SPELL_TAGS tag = new EnumMaster<SPELL_TAGS>().retrieveEnumConst(SPELL_TAGS.class, substring);
//            if (tag == SPELL_TAGS.MISSILE) {
//                data.setValue(ANIM_VALUES.MISSILE_SPEED, "");
//            }
//        }
        return data;
    }

    private String findResourceForSpell(DC_SpellObj spell,
                                        String partPath, String size,
                                        PROPERTY[] props, String pathRoot,
                                        boolean closest) {
        String path = StringMaster.buildPath(
                pathRoot, partPath);
//        spell.getTargeting();
        String file = null;
        for (PROPERTY p : props) {
            String name = spell.getProperty(p) + " " + partPath + size;
            file = FileManager.findFirstFile(path, name, closest);
            if (file != null)
                break;
        }
        return file;
    }


    private String getPath(ANIM_VALUES s) {
        switch (s) {
            case PARTICLE_EFFECTS:
                return PathFinder.getSfxPath();
            case SPRITES:
                return PathFinder.getSpritesPath();

        }
        return null;
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
        if (buff.getActive() instanceof DC_ActiveObj)
            active = (DC_ActiveObj) buff.getActive();
        initAnim(anim.getData(), active, anim.getPart(), anim);
        if (!isValid(anim)) return null;
        return anim;
    }

    private boolean isValid(Anim anim) {
        if (!anim.getSprites().isEmpty()) return true;
        if (!anim.getEmitterList().isEmpty()) return true;
        if (anim.getLightEmission() > 0) return true;
        if (anim instanceof HitAnim) return true;
        return false;
    }

    public boolean isFindClosestResource(ANIM_PART part, ANIM_VALUES val) {

        switch (part) {
            case MAIN:
                if (val==ANIM_VALUES.PARTICLE_EFFECTS)
                return true;
        }
        return findClosestResource;
    }

    public void setFindClosestResource(boolean findClosestResource) {
        this.findClosestResource = findClosestResource;
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
