package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.GdxMaster;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.sprite.SpriteAnimationFactory;
import main.libgdx.anims.weapons.Ready3dAnim;
import main.libgdx.texture.SmartTextureAtlas;
import main.libgdx.texture.TexturePackerLaunch;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.secondary.BooleanMaster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 9/6/2017.
 */
public class AnimMaster3d {

    private static final String SEPARATOR = "_";
    private static final String ANIM = "anim";
    private static final int frames = 30;
    //  TINY, SMALL, MEDIUM, LARGE, HUGE
    private static final int[] height_for_size = {
     64, 128, 196, 256, 320,
    };
    private static final int[] width_for_size = {
     256, 320, 384, 448, 512,
    };
    private static final String[][] substitutesWeapons = {
//     {"halbert", "axe"},

     {"insect claws", "pincers"},
     {"broad sword", "long sword"},
     {"falchion", "scimitar"},
     {"orcish arrows", "arrows"},
     {"elven arrows", "arrows"},

     {"heavy bolts", "bolts"},
     {"heavy crossbow", "crossbow"},
     {"hand crossbow", "crossbow"},
     {"longbow", "short bow"},
     {"kriss", "stiletto"},



    };
    private static final String[][] substitutesActions = {
     {"fist swing", "punch"},
     {"aimed shot", "quick shot"}
    };
    private static Map<String, TextureAtlas> atlasMap = new HashMap<>();
    private static List<DC_WeaponObj> broken = new ArrayList<>();
    private static Map<String, String> substituteMap;

    public static void init() {
        substituteMap = new HashMap<>();
        for (String[] sub : substitutesWeapons) {
            substituteMap.put(sub[0], sub[1]);
        }
        for (String[] sub : substitutesActions) {
            substituteMap.put(sub[0], sub[1]);
        }
    }

    public static boolean is3dAnim(DC_ActiveObj active) {
        if (!active.isAttackAny()) return false;
        DC_WeaponObj weapon = active.getActiveWeapon();
        if (!is3dSupported(weapon))
            return false;
        if (getOrCreateAtlas(weapon) == null)
        {
            if (!Assets.isOn()) {
                return false;
            }
            preloadAtlas(weapon);
            return true;
        }
        if (weapon.getAmmo() != null) {
            if (getOrCreateAtlas(weapon.getAmmo().getWrappedWeapon()) == null)
                return false;
        }
        return true;
    }

    private static boolean is3dSupported(DC_WeaponObj weapon) {
//        if (weapon.getWeaponType() == WEAPON_TYPE.POLE_ARM)
//            return false;

//        if (weapon.getWeaponType() == WEAPON_TYPE.NATURAL)
//            if (weapon.getWeaponGroup() != WEAPON_GROUP.FISTS)
//                return false;

        return true;
    }


    public static void preloadAtlases(Unit unit) {
        DC_WeaponObj weapon = unit.getWeapon(false);
        if (weapon != null)
            preloadAtlas(weapon);
        weapon = unit.getWeapon(true);
        if (weapon != null)
            preloadAtlas(weapon);
        weapon = unit.getNaturalWeapon(false);
        if (weapon != null)
            preloadAtlas(weapon);
        weapon = unit.getNaturalWeapon(true);
        if (weapon != null)
            preloadAtlas(weapon);
        for (DC_QuickItemObj sub : unit.getQuickItems()) {
            if (sub.isAmmo()) {
                preloadAtlas(sub.getWrappedWeapon());
            }
        }
    }

    public static Vector2 getOffset(DC_ActiveObj activeObj) {
        return null;
    }

    public static String getAtlasFileKeyForAction(String projection,
                                                  String weaponName, String actionName,
                                                  Boolean offhand) {
        StringBuilder s = new StringBuilder();
        s.append(
         StringMaster.join(SEPARATOR,
          weaponName,
          actionName, ANIM, projection
         ));

        if (BooleanMaster.isTrue(offhand))
            s.append(SEPARATOR + "l");
        if (BooleanMaster.isFalse(offhand))
            s.append(SEPARATOR + "r");
        String string = s.toString();
        string = string.toLowerCase().replace("offhand ", "").replace("off hand ", "").replace(" ", SEPARATOR);
        return string;
    }

    public static String getAtlasFileKeyForAction(Boolean projection,
                                                  DC_ActiveObj activeObj, WEAPON_ANIM_CASE aCase) {
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
        String actionName = null;
        String projectionString = "to";

//        if (aCase != WEAPON_ANIM_CASE.RELOAD) {
            projectionString = (projection == null ? "hor" :
             (projection ? "from" : "to"));
//        }
        if (aCase.isMissile()) {
            if (weapon.getLastAmmo() == null)
                return null;
            weapon = weapon.getLastAmmo().getWrappedWeapon();

        }
        if (aCase.isMiss() && isMissSupported()) {
            actionName = "miss";
        } else
            switch (aCase) {
                case RELOAD:
//                    weapon
                    actionName = "reload";
                    break;
                case MISSILE:
                    actionName = null;
                    break;
                case READY:
                    actionName = "awaiting";
                    break;
                case PARRY:
                    actionName = "parry";
                    break;
                case BLOCKED:
                    actionName = "blocked";
                    break;
                default:
                    actionName = getActionAtlasKey(activeObj);
            }
        Boolean offhand = null;
        if (projection!=null )
            if (isAssymetric(weapon.getProperty(G_PROPS.BASE_TYPE)))
            offhand = (activeObj.isOffhand());

        return getAtlasFileKeyForAction(projectionString,
         getWeaponAtlasKey(weapon),
         actionName, offhand);
    }

    private static boolean isMissSupported() {
        return false;
    }

    private static String getActionAtlasKey(DC_ActiveObj activeObj) {
        String name = activeObj
         .getName().replace("Off Hand ", "");
        String substitute = substituteMap.get(name.toLowerCase());
        if (substitute != null) {
            return substitute;
        }
        return name;
    }

    private static String getWeaponAtlasKey(DC_WeaponObj weapon) {
        String name = weapon.getProperty(G_PROPS.BASE_TYPE);
        String substitute = substituteMap.get(name.toLowerCase());
        if (substitute != null) {
            return substitute;
        }
        return name;
    }

    public static String getAtlasPath(DC_WeaponObj weapon, String name) {
String groupName = weapon.getWeaponGroup().toString().replace("_", " ");

        StrPathBuilder s = new StrPathBuilder(
         PathFinder.getWeaponAnimPath(), "atlas",
         weapon.getWeaponType().toString().replace("_", " ")
         , groupName, name
         + TexturePackerLaunch.ATLAS_EXTENSION);
        return s.toString();
    }

    private static boolean isAssymetric(String activeWeapon) {
        switch (activeWeapon) {
            case "Fist":
                return true;

        }
        return false;
    }

    public static SpriteAnimation getFxSpriteForAction(DC_ActiveObj activeObj) {
        return null;
    }


    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     Obj targetObj, WEAPON_ANIM_CASE aCase, PROJECTION projection) {
        return getSpriteForAction(duration, activeObj, targetObj, aCase, projection.bool);
    }

    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     Obj targetObj, WEAPON_ANIM_CASE aCase, Boolean projection) {
        // loops,

        //TODO who is displayed above on the cell?
//modify texture? coloring, sizing,
//        float angle = PositionMaster.getAngle(activeObj.getOwnerObj(), targetObj);
//float baseAngle =
//        float rotation = angle * 2 / 3;
        String name = getAtlasFileKeyForAction(projection, activeObj, aCase);

        TextureAtlas atlas = getAtlas(activeObj, aCase );
        Array<AtlasRegion> regions = atlas.findRegions(name);
        if (regions.size == 0) {
            if (isSearchAtlasRegions(activeObj))
                regions = findAtlasRegions(atlas, projection, activeObj, true);
        }
        if (regions.size == 0) {
            if (activeObj.getParentAction() != null)
                if (isSearchAtlasRegions(activeObj))
                regions = findAtlasRegions(atlas, projection, activeObj, false);
        }
        if (regions.size == 0)
            main.system.auxiliary.log.LogMaster.log(
             1, activeObj + ": " + aCase + " no 3d sprites: " + name + atlas);
//        if (TexturePackerLaunch.TRIM) {
//            regions.forEach(region -> {
//                region.setRegionHeight(getHeight(activeObj));
//                region.setRegionWidth(getWidth(activeObj));
//                region.setRegionWidth(region.originalWidth);
//                region.setRegionHeight(region.originalHeight);
//            });
//        }
        float frameDuration = duration / regions.size;
        int loops = 0;
        if (aCase.isMissile()) {
//            loops = Math.max(0,PositionMaster.getDistance(activeObj.getOwnerObj(), targetObj) - 1);
        }
        if (loops != 0)
            frameDuration /= loops;

        SpriteAnimation sprite = SpriteAnimationFactory.
         getSpriteAnimation(regions, frameDuration, loops);
//        sprite.setRotation(rotation);
        return sprite;
    }

    private static boolean isSearchAtlasRegions(DC_ActiveObj activeObj) {
        return false;
    }

    private static int getWidth(DC_ActiveObj activeObj) {
        WEAPON_SIZE size = activeObj.getActiveWeapon().getWeaponSize();
        int i = EnumMaster.getEnumConstIndex(WEAPON_SIZE.class, size);
        return width_for_size[i];
    }

    private static int getHeight(DC_ActiveObj activeObj) {
        WEAPON_SIZE size = activeObj.getActiveWeapon().getWeaponSize();
        int i = EnumMaster.getEnumConstIndex(WEAPON_SIZE.class, size);
        return height_for_size[i];
    }

    private static Array<AtlasRegion> findAtlasRegions(TextureAtlas atlas,
                                                       Boolean projection,
                                                       DC_ActiveObj activeObj,
                                                       boolean searchOtherWeaponOrAction) {
        String name = getAtlasFileKeyForAction(projection, activeObj, WEAPON_ANIM_CASE.NORMAL);
        List<Entity> types = null;
        if (searchOtherWeaponOrAction) {
            types = Arrays.stream(DataManager.getBaseWeaponTypes()) .
             filter(type -> type.getProperty(G_PROPS.WEAPON_GROUP).equals(
              activeObj.getActiveWeapon().getProperty(G_PROPS.WEAPON_GROUP))).collect(Collectors.toList());
        } else {
            types = new ArrayList<>(
             activeObj.getParentAction().getSubActions());
        }
        Array<AtlasRegion> regions = null;
        for (Entity sub : types) {
            name = sub.getName() + name.substring(name.indexOf(SEPARATOR));
            regions = atlas.findRegions(name.toLowerCase());

            main.system.auxiliary.log.LogMaster.log(
             1, activeObj + " searching " + name);
            if (regions.size > 0)
                break;
        }
        return regions;

    }

    private static TextureAtlas getAtlas(DC_ActiveObj activeObj, WEAPON_ANIM_CASE aCase
      ) {
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
        if (aCase.isMissile()) {
            if (weapon.getLastAmmo() == null)
                return null;
            weapon = weapon.getLastAmmo().getWrappedWeapon();
        }
        return getOrCreateAtlas(weapon );
    }

    public static void preloadAtlas(DC_WeaponObj weapon) {
        if (GdxMaster.isLwjglThread())
            loadAtlasForWeapon(weapon);
        else
            Gdx.app.postRunnable(() -> {
                    loadAtlasForWeapon(weapon);
            });
    }

    private static void loadAtlasForWeapon(DC_WeaponObj weapon) {
        if (!Assets.isOn()) {
            getOrCreateAtlas(weapon);
            return;
        }
        String path =
         getFullAtlasPath(weapon);
        Assets.get().getManager().load(path, TextureAtlas.class);
    }

    public static TextureAtlas getOrCreateAtlas(DC_WeaponObj weapon) {
        if (broken.contains(weapon))
            return null;
        try {
            String path =
             getFullAtlasPath(weapon);

            if (!FileManager.isFile(path))
                return null;

            if ( Assets.isOn()) {
                Assets.get().getManager().update();
                Assets.get().getManager().finishLoadingAsset(path);
               return Assets.get().getManager().get(path, TextureAtlas.class);
//
            }
            TextureAtlas atlas = atlasMap.get(path);
            if (atlas == null) {
                atlas = new SmartTextureAtlas(path);
            }
            atlasMap.put(path, atlas);
            return atlas;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            broken.add(weapon);
            return null;
        }

    }

    private static String getFullAtlasPath(DC_WeaponObj weapon) {
        return PathFinder.getImagePath()+ getAtlasPath(weapon, getWeaponAtlasKey(weapon));
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
        Anim anim = getReadyAnim(entity);
        anim.setDone(true);
    }

    public static void initHover(DC_UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Anim anim = getReadyAnim(entity);
        if (!anim.isDone())
            return;
        anim.setDone(false);
        AnimMaster.getInstance().addAttached(anim);

        //counter?
    }

    private static boolean isReadyAnimSupported(DC_UnitAction entity) {
        return false;
//        return is3dAnim(entity);
//        return entity.getActiveWeapon().getName().contains("Short Sword");
    }

    private static Anim getReadyAnim(DC_UnitAction entity) {
        CompositeAnim composite = AnimMaster.getInstance().getConstructor().getOrCreate(entity);
        Anim anim = composite.getContinuous();
        if (anim == null) {
            anim = new Ready3dAnim(entity);
            composite.setContinuous(anim);
        }
        anim.start(entity.getRef());
        return anim;
    }

    public enum PROJECTION {
        FROM(true), TO(false), HOR(null),;
        public Boolean bool;

        PROJECTION(Boolean bool) {
            this.bool = bool;
        }
    }

    public enum WEAPON_ANIM_CASE {
        NORMAL,
        MISSILE_MISS,
        MISSILE,
        MISS,
        READY,
        PARRY,
        BLOCKED,
        RELOAD,;

        public boolean isMissile() {
            return this == WEAPON_ANIM_CASE.MISSILE || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }

        public boolean isMiss() {
            return this == WEAPON_ANIM_CASE.MISS || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }
    }
}
