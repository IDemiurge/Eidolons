package libgdx.anims.construct;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.containers.customtarget.ShapeEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.Spell;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.EffectMaster;
import libgdx.GdxMaster;
import libgdx.anims.Anim;
import libgdx.anims.AnimData;
import libgdx.anims.AnimData.ANIM_VALUES;
import libgdx.anims.Animation;
import libgdx.anims.CompositeAnim;
import libgdx.anims.anim3d.*;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.main.EffectAnimCreator;
import libgdx.anims.main.EventAnimCreator;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.std.*;
import libgdx.anims.std.SpellAnim.SPELL_ANIMS;
import libgdx.anims.std.custom.ForceAnim;
import libgdx.assets.AnimMaster3d;
import libgdx.particles.ParticleEffectX;
import libgdx.particles.spell.SpellMultiplicator;
import libgdx.particles.spell.SpellVfx;
import libgdx.particles.spell.SpellVfxPool;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.ability.effects.Effect;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.ItemEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eidolons.content.consts.VisualEnums.ANIM_PART;
import static eidolons.content.consts.VisualEnums.anim_vals;

/**
 * Created by JustMe on 1/11/2017.
 */
public class AnimConstructor {
    static ObjectMap<DC_ActiveObj, CompositeAnim> map = new ObjectMap<>(200);
    private static final boolean autoconstruct = false;

    private AnimConstructor() {

    }

    public static void preconstruct(Unit unit) {
        unit.getActives().forEach(AnimConstructor::getOrCreate);
        AnimMaster3d.preloadAtlases(unit);
    }

    public static CompositeAnim getCached(ActiveObj active) {
        CompositeAnim anim = map.get((DC_ActiveObj) active);
        if (anim != null) {
            anim.reset();
            return anim;
        }
        return anim;
    }

    public static CompositeAnim getOrCreate(ActiveObj active) {
        if (active == null) {
            return null;
        }
        if (!checkAnimationSupported((DC_ActiveObj) active))
            return null;
        CompositeAnim anim = map.get((DC_ActiveObj) active);
        if (!isReconstruct()) {
            if (anim != null) {
                anim.reset();
                return anim;
            }
        }
        try {
            anim = construct((DC_ActiveObj) active);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return anim;
    }

    private static boolean checkAnimationSupported(DC_ActiveObj active) {
        if (active.getActionGroup() == ACTION_TYPE_GROUPS.HIDDEN)
            return false;
        return active.getActionGroup() != ACTION_TYPE_GROUPS.TURN;
        //hidden?
    }

    public static void preconstructSpells(Unit unit) {
        if (ListMaster.isNotEmpty(unit.getSpells()))
            if (GdxMaster.isLwjglThread()) {
                unit.getSpells().forEach(AnimConstructor::getOrCreate);
            } else
                Gdx.app.postRunnable((() -> {
                    unit.getSpells().forEach(AnimConstructor::getOrCreate);
                }));
    }

    public static void preconstruct(DC_ActiveObj active) {

        if (GdxMaster.isLwjglThread()) {
            getOrCreate(active);
        } else
            Gdx.app.postRunnable((() -> {
                getOrCreate(active);
            }));
    }

    private static CompositeAnim construct(DC_ActiveObj active) {
        if (active instanceof Spell) {
            main.system.auxiliary.log.LogMaster.log(1, "Construct spell anim for: " + active);
        }
        active.construct(); // in case it was omitted
        //re-construct sometimes?
        CompositeAnim anim = new CompositeAnim(active);
        //        active.getActionGroup()

        Arrays.stream(ANIM_PART.values()).forEach(part -> {
            if (!isPartIgnored(part)) {
                Anim animPart = getPartAnim(active, part, anim);
                if (animPart != null) {
                    if (part == ANIM_PART.IMPACT) {
                        if (SpellMultiplicator.isMultiAnimRequired(anim.getActive_())) {
                            //                            anim.getMap().getVar(ANIM_PART.MISSILE).getVar
                            animPart.setDelay(2.2f);
                            anim.attach(animPart, part);
                            return;
                        }
                    }
                    anim.add(part, animPart);
                }
            }
        });

        map.put(active, anim);
        return anim;
    }

    public static Anim getPartAnim(DC_ActiveObj active, ANIM_PART part, CompositeAnim anim) {
        AnimData data = new AnimData();
        for (VALUE val : anim_vals) {
            if (val instanceof PARAMETER ||
                    StringMaster.contains(val.name(), part.toString())
                    || (val == PROPS.ANIM_VFX_MAIN && part == ANIM_PART.MISSILE)) {
                String name = active.getValue(val);
                if (!name.isEmpty())
                    data.add(val, name);
            }
        }
        return getPartAnim(data, active, part, anim);
    }


    private static Anim getPartAnim(AnimData data, DC_ActiveObj active,
                                    ANIM_PART part, CompositeAnim composite) {
        //TODO if (!checkAnimValid())
        //    return null ;
        Anim anim = createAnim(active, data, part);
        if (anim == null) {
            return null;
        }
        if (!initAnim(data, active, part, anim)) {
            if (!(active instanceof Spell)) {
                return null;
            }

            if (!autoconstruct)
                return null;
            data = getStandardData((Spell) active, part, composite);
            if (!initAnim(data, active, part, anim)) {
                return null;
            }
        }
        anim.setMaster(AnimMaster.getInstance());
        return anim;
    }

    private static Anim createAnim(DC_ActiveObj active, AnimData data, ANIM_PART part) {
        if (active.isMove()) {
            return MoveAnimation.isOn() ? new MoveAnimation(active, data) : null;
        }
        if (active instanceof DC_QuickItemAction) {
            if (((DC_QuickItemAction) active).getItem().isAmmo()) {
                return new Reload3dAnim(active);
            } else {
                if (((DC_QuickItemAction) active).getItem().isPotion())
                    return new Potion3dAnim(active);
                return new QuickItemAnim(((DC_QuickItemAction) active).getItem());
            }

        }

        if (active.isAttackAny()) {
            if (part == ANIM_PART.IMPACT) {
                return new HitAnim(active, data);
            }
            if (isForceAnim(active, part)) {
                return new ForceAnim(active, part);
            }

            if (active.isRanged()) {
                if (part == ANIM_PART.CAST)
                    return new Ranged3dAnim(active);
                if (part == ANIM_PART.MISSILE)
                    return new Missile3dAnim(active);
            }
            if (part == ANIM_PART.MISSILE) {
                //                    if (EidolonsGame.DUEL && active.getOwnerUnit())
                //                        return new ForceAnim(active, ANIM_PART.MISSILE);
                return new Weapon3dAnim(active);
            }

        }

        if (active.isSpell()) {
            if (active.isMissile()) {
                if (part == ANIM_PART.IMPACT) {
                    return new HitAnim(active, data);
                }
            } else if (part == ANIM_PART.MISSILE) {
                //                if (active.getTargetingMode().isSingle())
                if (active.getChecker().isTopDown())
                    return null;
            }
            SPELL_ANIMS template = getTemplateFromTargetMode(active);
            return new SpellAnim(active, data, template, part);
        }
        return new ActionAnim(active, data);
    }

    public static SPELL_ANIMS getTemplateForSpell(DC_ActiveObj active) {
        return getTemplateFromTargetMode(active);
    }

    public static SPELL_ANIMS getTemplateFromTargetMode(DC_ActiveObj activeObj) {

        TARGETING_MODE targetingMode = activeObj.getTargetingMode();
        switch (targetingMode) {
            case NOVA:
            case RAY:
            case THREE_RAY_AUTO:
            case RAY_AUTO:

            case WAVE:
            case BLAST:
            case SPRAY:
                return new EnumMaster<SPELL_ANIMS>().retrieveEnumConst(SPELL_ANIMS.class, targetingMode.name());
        }
        Effect effect = EffectMaster.getFirstEffectOfClass(activeObj,
                ShapeEffect.class);
        if (effect != null) {
            PositionMaster.SHAPE shape = ((ShapeEffect) effect).getShape();
            if (shape != null) {
                return new EnumMaster<SPELL_ANIMS>().
                        retrieveEnumConst(SPELL_ANIMS.class, shape.name());
            }
        }

        return null;

    }

    private static boolean isForceAnim(DC_ActiveObj active, ANIM_PART part) {
        if (active.getActiveWeapon().getWeaponGroup() == ItemEnums.WEAPON_GROUP.FORCE) {
            return true;
        }
        switch (part) {
            case MISSILE:
                //           TODO wanna test it? do it right...     if (CoreEngine.isFastMode())
                //                    return true;
        }
        return false;
    }


    private static boolean initAnim(AnimData data,
                                    DC_ActiveObj active, ANIM_PART part, Anim anim) {
        boolean exists = false;
        List<SpriteAnimation> sprites = new ArrayList<>();
        for (String path :
                ContainerUtils.openContainer(data.getValue(ANIM_VALUES.SPRITES))) {
            if (path.isEmpty()) {
                continue;
            }
            path = path.toLowerCase();
            path = PathUtils.addMissingPathSegments(path, PathFinder.getSpellSpritesPath());
            //            Chronos.mark("sprite " + path);
            sprites.add(SpriteAnimationFactory.getSpriteAnimation(path));
            //            Chronos.logTimeElapsedForMark("sprite " + path);
            exists = true;
        }
        List<SpellVfx> list;
        //     if (!Thread.currentThread().getName().contains("LWJGL")){
        //
        //     }
        //       Eidolons.gdxApplication.postRunnable(()->{
        //        list = EmitterPools.getEmitters(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS));
        //        });
        int n = 1;
        if (part == ANIM_PART.MISSILE)
            if (anim instanceof SpellAnim) {
                n = SpellMultiplicator.getPrePoolVfxCount((SpellAnim) anim);
            }
        try {
            list = SpellVfxPool.getEmitters(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS), n);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            list = SpellVfxPool.getEmitters(
                    getPath(ANIM_VALUES.PARTICLE_EFFECTS) +
                            data.getValue(ANIM_VALUES.PARTICLE_EFFECTS), 1);
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

    private static boolean checkForcedAnimation(DC_ActiveObj active, ANIM_PART part) {
        switch (part) {
            case MISSILE:
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

    public static Animation getEffectAnim(Effect e) {
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
        effectAnim.setRef(e.getRef());
        return effectAnim;
    }

    public static boolean isAnimated(Effect e) {
        if (e.getActiveObj() == null) {
            if (e instanceof DealDamageEffect) {
                //                TODO
            } else
                return false;
        }
        if (!isCellAnimated(e)) {
            if (e.getRef().getTargetObj() instanceof GridCell) {

                return false;
            }
        }
        if (e instanceof DealDamageEffect) {
            return true;
        }
        if (e instanceof ModifyValueEffect) {
            return !e.isContinuousWrapped();
        }

        return false;
    }

    private static boolean isCellAnimated(Effect e) {
        return false;
    }

    public static AnimData getStandardData(Spell spell, ANIM_PART part, CompositeAnim compositeAnim) {
        return getStandardData(spell, part, compositeAnim.getMap().size());
    }

    public static AnimData getStandardData(Spell spell, ANIM_PART part, int partsCount) {
        AnimData data = new AnimData();

        String partPath = part.getPartPath();

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

       
         /*
        first, check for exact fit...
        if either has one, then don't search for other (unless also exact fit)
         */
        String pathRoot = getPath(ANIM_VALUES.SPRITES);
        String sprite = AnimResourceFinder.findResourceForSpell(spell, partPath, size, true, pathRoot, false);
        pathRoot = getPath(ANIM_VALUES.PARTICLE_EFFECTS);
        String emitter = AnimResourceFinder.findResourceForSpell(spell, partPath, size, true, pathRoot, false);
        if (sprite == null)
            if (emitter == null) {
                emitter = AnimResourceFinder.findResourceForSpell(spell, partPath, size, false, pathRoot, false);
                if (AnimResourceFinder.isFindClosestResource(part, ANIM_VALUES.PARTICLE_EFFECTS, partsCount)) {
                    if (emitter == null)
                        emitter = AnimResourceFinder.findResourceForSpell(spell, partPath, size, true, pathRoot, true);
                    if (emitter == null)
                        emitter = AnimResourceFinder.findResourceForSpell(spell, partPath, size, false, pathRoot, true);

                }
                if (AnimResourceFinder.isFindClosestResource(part, ANIM_VALUES.SPRITES, partsCount))
                    if (emitter == null) {
                        pathRoot = getPath(ANIM_VALUES.SPRITES);
                        sprite = AnimResourceFinder.findResourceForSpell(spell, partPath, size, false, pathRoot, false);
                        if (sprite == null)
                            sprite = AnimResourceFinder.findResourceForSpell(spell, partPath, size, true, pathRoot, true);
                        if (sprite == null)
                            sprite = AnimResourceFinder.findResourceForSpell(spell, partPath, size, false, pathRoot, true);


                    }
            }
        if (sprite != null) {
            String val = PathUtils.buildPath(
                    partPath, PathUtils.removePreviousPathSegments(sprite, pathRoot));
            LogMaster.log(LogMaster.ANIM_DEBUG,
                    "AUTO ANIM CONSTRUCTION FOR " + spell + "-" + part +
                            ": " + ANIM_VALUES.SPRITES + " is set automatically to " + val);
            data.setValue(ANIM_VALUES.SPRITES, val);
        }
        if (emitter != null) {
            String val = PathUtils.buildPath(
                    partPath, PathUtils.removePreviousPathSegments(emitter, pathRoot));
            LogMaster.log(LogMaster.ANIM_DEBUG,
                    "AUTO ANIM CONSTRUCTION FOR " + spell + "-" + part +
                            ": " + ANIM_VALUES.PARTICLE_EFFECTS + " is set automatically to " + val);
            data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, val);
        }


        return data;
    }

    public static void removeCache(Anim anim) {
        map.remove((DC_ActiveObj) anim.getActive());
    }

    private boolean isPartIgnored(String partPath) {
        partPath = partPath.replace("missile", "main");
        return isPartIgnored(new EnumMaster<ANIM_PART>().retrieveEnumConst(ANIM_PART.class, partPath));
    }

    private static boolean isPartIgnored(ANIM_PART part) {
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

    public static String getPath(ANIM_VALUES s) {
        String path = null;
        switch (s) {
            case PARTICLE_EFFECTS:
                if (ParticleEffectX.isEmitterAtlasesOn())
                    path = PathFinder.getVfxPath() + "atlas/"; // +"spell/";
                else
                    path = PathFinder.getVfxPath(); //  + "spell/";
                break;
            case SPRITES:
                path = PathFinder.getSpritesPathFull();
                break;
        }
        return path;
    }

    public static boolean isReconstruct() {
        //not to forget
        return false;
    }

    public static BuffAnim getBuffAnim(BuffObj buff) {
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

    private static boolean isValid(Anim anim) {
        if (!anim.getSprites().isEmpty()) {
            return true;
        }
        if (!anim.getEmitterList().isEmpty()) {
            return true;
        }
        if (anim.getLightEmission() > 0) {
            return true;
        }
        return anim instanceof HitAnim;
    }

    public static void preconstruct(Event event) {
        Gdx.app.postRunnable(() -> {
            try {
                EventAnimCreator.getAnim(event);
                AnimMaster.getInstance().getParentAnim(event.getRef());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
    }

    public static CompositeAnim getParryAnim(WeaponItem weaponObj, DC_ActiveObj attack) {
        Parry3dAnim parryAnim = new Parry3dAnim(weaponObj, attack);
        return new CompositeAnim(parryAnim);
    }

    //is this still useful?
    public static void preconstructAllForAV() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            Spell active = new Spell(type, Player.NEUTRAL, DC_Game.game, new Ref());
            AnimData data;
            try {
                int i = 0;
                for (ANIM_PART part : ANIM_PART.values()) {
                    data = getStandardData(active, part, i);
                    for (ANIM_VALUES val : ANIM_VALUES.values()) {
                        String identifier;

                        String value = data.getValue(val);
                        if (StringMaster.isEmpty(value))
                            continue;
                        value = value.replace(PathFinder.getImagePath().toLowerCase(), "");
                        i++;
                        switch (val) {
                            case PARTICLE_EFFECTS:
                                identifier = "VFX";
                                main.system.auxiliary.log.LogMaster.log(1, "PARTICLE_EFFECTS =" +
                                        value +
                                        " for " + type);
                                break;
                            case SPRITES:
                                identifier = "SPRITES";
                                break;
                            default:
                                continue;
                        }

                        PROPERTY prop = ContentValsManager.findPROP("anim" +
                                "_" + identifier + "_" + part);
                        if (prop == null)
                            continue;
                        type.setProperty(prop, value);

                    }
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }
}
