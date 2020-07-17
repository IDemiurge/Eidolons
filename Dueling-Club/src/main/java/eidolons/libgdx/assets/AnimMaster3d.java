package eidolons.libgdx.assets;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.Animation;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.anim3d.Ready3dAnim;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Ref;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.math.PositionMaster;

import static eidolons.libgdx.anims.sprite.SpriteAnimationFactory.fps30;
import static main.system.ExceptionMaster.printStackTrace;
import static main.system.auxiliary.log.LogMaster.log;

public class AnimMaster3d {

    protected static Boolean off;
    protected static float fps = fps30;

    public static void preloadAtlases(Unit unit) {
        if (isOff())
            return;
        DC_WeaponObj weapon = unit.getWeapon(false);
        if (weapon != null)
            Atlases.preloadAtlas(weapon);
        weapon = unit.getWeapon(true);
        if (weapon != null)
            Atlases.preloadAtlas(weapon);
        weapon = unit.getNaturalWeapon(false);
        if (weapon != null)
            Atlases.preloadAtlas(weapon);
        weapon = unit.getNaturalWeapon(true);
        if (weapon != null)
            Atlases.preloadAtlas(weapon);
        for (DC_QuickItemObj sub : unit.getQuickItems()) {
            if (sub.isAmmo()) {
                Atlases.preloadAtlas(sub.getWrappedWeapon());
            } else {
                if (sub.getWrappedWeapon() == null) {
                    String path = null;
                    try {
                        path = Atlases.getPotionAtlasPath(sub.getActive());
                    } catch (Exception e) {
                        log(1, "FAILED TO LOAD A QUICK ITEM ATLAS: " + sub);
                        printStackTrace(e);
                        return;
                    }
                    Atlases.preloadAtlas(path);
                }
            }
        }
    }


    public static Vector2 getOffset(DC_ActiveObj activeObj) {
        return null;
    }

    protected static boolean isMissSupported() {
        return false;
    }

    protected static boolean isAssymetric(String activeWeapon) {
        switch (activeWeapon) {
            case "Fist":
            case "Armored Fist":
                return true;

        }
        return false;
    }


    public static int getWeaponActionSpeed(DC_ActiveObj active) {
        if (active.isRanged())
            return 400;
        if (active.getActiveWeapon().isTwoHanded())
            return 30;
        return 50;
    }

    public static void hoverOff(DC_UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Animation anim = getReadyAnim(entity);
        anim.setDone(true);
    }

    public static void initHover(DC_UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Animation anim = getReadyAnim(entity);
        if (!anim.isDone())
            return;
        anim.setDone(false);
        AnimMaster.getInstance().addAttached(anim);

        //counter?
    }

    protected static boolean isReadyAnimSupported(DC_UnitAction entity) {
        return false;
        //        return is3dAnim(entity);
        //        return entity.getActiveWeapon().getName().contains("Short Sword");
    }

    protected static Animation getReadyAnim(DC_UnitAction entity) {
        CompositeAnim composite = AnimConstructor.getOrCreate(entity);
        Animation anim = composite.getContinuous();
        if (anim == null) {
            anim = new Ready3dAnim(entity);
            composite.setContinuous(anim);
        }
        anim.start(entity.getRef());
        return anim;
    }

    public static Boolean isOff() {
        if (off == null)
            off = OptionsMaster.getAnimOptions().getBooleanValue(ANIMATION_OPTION.WEAPON_3D_ANIMS_OFF);
        return off;
    }

    public static void setOff(Boolean off) {
        AnimMaster3d.off = off;
    }

    public static float getFps() {
        return fps;
    }

    public static void setFps(float fps) {
        AnimMaster3d.fps = fps;
    }

    public static AssetEnums.PROJECTION getProjectionByFacing(FACING_DIRECTION facing) {
        if (!facing.isVertical())
            return AssetEnums.PROJECTION.HOR;
        return facing == main.game.bf.directions.FACING_DIRECTION.NORTH ? AssetEnums.PROJECTION.TO : AssetEnums.PROJECTION.FROM;
    }

    public static AssetEnums.PROJECTION getProjection(Ref ref, DC_ActiveObj active) {
        if (ref.getTargetObj() == null || ref == null)
            return getProjectionByFacing(active.getOwnerUnit().getFacing());
        Boolean b =
                PositionMaster.isAboveOr(ref.getSourceObj(), ref.getTargetObj());
        if (active.getOwnerUnit().getCoordinates().equals(ref.getTargetObj().getCoordinates()))
            b = active.getOwnerUnit().isMine();
        AssetEnums.PROJECTION projection = AssetEnums.PROJECTION.HOR;
        if (b != null)
            projection = b ? AssetEnums.PROJECTION.FROM : AssetEnums.PROJECTION.TO;
        return projection;
    }


}
