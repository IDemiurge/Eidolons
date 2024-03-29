package libgdx.assets;

import com.badlogic.gdx.math.Vector2;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import libgdx.anims.Animation;
import libgdx.anims.CompositeAnim;
import libgdx.anims.anim3d.Ready3dAnim;
import libgdx.anims.construct.AnimConstructor;
import libgdx.anims.main.AnimMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Ref;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.math.PositionMaster;

import static libgdx.anims.sprite.SpriteAnimationFactory.fps30;
import static main.system.ExceptionMaster.printStackTrace;
import static main.system.auxiliary.log.LogMaster.log;

public class AnimMaster3d {

    protected static Boolean off;
    protected static float fps = fps30;

    public static void preloadAtlases(Unit unit) {
        if (isOff())
            return;
        WeaponItem weapon = unit.getWeapon(false);
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
        for (QuickItem sub : unit.getQuickItems()) {
            if (sub.isAmmo()) {
                Atlases.preloadAtlas(sub.getWrappedWeapon());
            } else {
                if (sub.getWrappedWeapon() == null) {
                    String path;
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


    public static Vector2 getOffset(ActiveObj activeObj) {
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


    public static int getWeaponActionSpeed(ActiveObj active) {
        if (active.isRanged())
            return 400;
        if (active.getActiveWeapon().isTwoHanded())
            return 30;
        return 50;
    }

    public static void hoverOff(UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Animation anim = getReadyAnim(entity);
        anim.setDone(true);
    }

    public static void initHover(UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Animation anim = getReadyAnim(entity);
        if (!anim.isDone())
            return;
        anim.setDone(false);
        AnimMaster.getInstance().addAttached(anim);

        //counter?
    }

    protected static boolean isReadyAnimSupported(UnitAction entity) {
        return false;
        //        return is3dAnim(entity);
        //        return entity.getActiveWeapon().getName().contains("Short Sword");
    }

    protected static Animation getReadyAnim(UnitAction entity) {
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

    public static VisualEnums.PROJECTION getProjectionByFacing(FACING_DIRECTION facing) {
        if (!facing.isVertical())
            return VisualEnums.PROJECTION.HOR;
        return facing == main.game.bf.directions.FACING_DIRECTION.NORTH ? VisualEnums.PROJECTION.TO : VisualEnums.PROJECTION.FROM;
    }

    public static VisualEnums.PROJECTION getProjection(Ref ref, ActiveObj active) {
        // if (ref.getTargetObj() == null || ref == null)
        //     return getProjectionByFacing(active.getOwnerUnit().getFacing());
        Boolean b =
                PositionMaster.isAboveOr(ref.getSourceObj(), ref.getTargetObj());
        if (active.getOwnerUnit().getCoordinates().equals(ref.getTargetObj().getCoordinates()))
            b = active.getOwnerUnit().isMine();
        VisualEnums.PROJECTION projection = VisualEnums.PROJECTION.HOR;
        if (b != null)
            projection = b ? VisualEnums.PROJECTION.FROM : VisualEnums.PROJECTION.TO;
        return projection;
    }


}
