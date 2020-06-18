package eidolons.libgdx.anims.anim3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.Animation;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.decor.CellDecorLayer;
import eidolons.libgdx.texture.SmartTextureAtlas;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TexturePackerLaunch;
import eidolons.libgdx.utils.mixer.MaskTest;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.entity.ItemEnums.ARMOR_TYPE;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.math.PositionMaster;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.libgdx.anims.sprite.SpriteAnimationFactory.fps30;
import static main.system.ExceptionMaster.printStackTrace;
import static main.system.auxiliary.log.LogMaster.log;

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

            {"golem fist", "armored fist"},
            {"tail", "insect claws"},
            {"paws", "claws"},
            {"tentacle", "insect claws"},
            {"lance", "spear"},
            {"glaive", "battle spear"},
            //     {"pike", "spear"},
            //     {"staff", "spear"},
            //            {"battle spear", "lance"},
            //     {"scythe", "spear"},
            {"sickle", "hand axe"},


            //     {"broad sword", "long sword"},
            //     {"falchion", "scimitar"},
            {"orcish arrows", "arrows"},
            {"elven arrows", "arrows"},

            {"heavy bolts", "bolts"},
            {"heavy crossbow", "crossbow"},
            {"hand crossbow", "crossbow"},
            {"longbow", "short bow"},
            //     {"war axe", "battle axe"},
            //     {"great axe", "battle axe"},
            //     {"kriss", "stiletto"},


    };
    private static final String[][] substitutesActions = {
            {"fist swing", "punch"},
            {"aimed shot", "quick shot"}
    };
    public static boolean JPG_WEAPONS = false;// EidolonsGame.BRIDGE;
    // private static final Map<String, TextureAtlas> atlasMap = new HashMap<>();
    private static final List<DC_WeaponObj> broken = new ArrayList<>();
    private static ObjectMap<String, String> substituteMap;
    private static Boolean off;
    private static float fps = fps30;

    static {
        init();
    }

    public static void init() {
        substituteMap = new ObjectMap<>();
        for (String[] sub : substitutesWeapons) {
            substituteMap.put(sub[0], sub[1]);
        }
        for (String[] sub : substitutesActions) {
            substituteMap.put(sub[0], sub[1]);
        }
    }

    public static boolean is3dAnim(DC_ActiveObj active) {
        if (isOff())
            return false;
        if (!active.isAttackAny()) return false;
        if (!active.getOwnerUnit().isPlayerCharacter()) {
            if (Flags.isFastMode())
                return false;
        }
        DC_WeaponObj weapon = active.getActiveWeapon();
        if (!is3dSupported(weapon))
            return false;
        if (Assets.isOn()) {
            {
                preloadAtlas(weapon);
                return true;
            }
        }
        if (getOrCreateAtlas(weapon) == null) {
            return true;
        }
        if (weapon.getAmmo() != null) {
            return getOrCreateAtlas(weapon.getAmmo().getWrappedWeapon()) != null;
        }
        return true;
    }

    private static boolean is3dSupported(DC_WeaponObj weapon) {
        //        if (weapon.getWeaponType() == WEAPON_TYPE.POLE_ARM)
        //            return false;

        //        if (weapon.getWeaponType() == WEAPON_TYPE.NATURAL)
        //            if (weapon.getWeaponGroup() != WEAPON_GROUP.FISTS)
        //                return false;

        //        return !CoreEngine.isFastMode();
        return true;
    }


    public static void preloadAtlases(Unit unit) {
        if (!unit.isPlayerCharacter()) {
            if (Flags.isFastMode())
                return;
        }
        if (isOff())
            return;
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
            } else {
                if (sub.getWrappedWeapon() == null) {
                    String path = null;
                    try {
                        path = getPotionAtlasPath(sub.getActive());
                    } catch (Exception e) {
                        log(1, "FAILED TO LOAD A QUICK ITEM ATLAS: " + sub);
                        printStackTrace(e);
                        return;
                    }
                    preloadAtlas(path);
                }
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
                ContainerUtils.join(SEPARATOR,
                        weaponName,
                        actionName, ANIM, projection
                ));

        //       TODO  if (BooleanMaster.isTrue(offhand))
        //            s.append(SEPARATOR + "l");
        //        if (BooleanMaster.isFalse(offhand))
        if (offhand != null)
            s.append(SEPARATOR + "r");
        String string = s.toString();
        string = string.toLowerCase().replace("offhand ", "").replace("off hand ", "").replace(" ", SEPARATOR);
        return string;
    }

    public static String getAtlasFileKeyForAction(Boolean projection,
                                                  DC_ActiveObj activeObj, WEAPON_ANIM_CASE aCase) {
        if (aCase == WEAPON_ANIM_CASE.POTION)
            return getPotionKey(activeObj);
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
        //       TODO !!! if (projection != null)
        if (isAssymetric(weapon.getBaseTypeName()))
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

    public static String getWeaponAtlasKey(DC_WeaponObj weapon) {
        String name = weapon.getBaseTypeName();
        String substitute = substituteMap.get(name.toLowerCase());
        if (substitute != null) {
            return substitute;
        }
        if (name.equalsIgnoreCase("fist")) {
            if (weapon.getOwnerObj().getArmor() != null) {
                if (weapon.getOwnerObj().getArmor().getArmorType() ==
                        ARMOR_TYPE.HEAVY)
                    name = "Armored Fist";
                else if (ItemMaster.isGlovedFistOn())
                    name = "Gloved Fist";
            }
        }
        return name;
    }

    public static String getAtlasPath(DC_WeaponObj weapon, String name) {
        if (weapon.getWeaponGroup() == null) {
            log(1, "Invalid weapon group " + weapon.getProperty(G_PROPS.WEAPON_GROUP));
            return "Invalid weapon group " + weapon.getProperty(G_PROPS.WEAPON_GROUP);
        }
        String groupName = weapon.getWeaponGroup().toString().replace("_", " ");

        StrPathBuilder s = new StrPathBuilder(
                PathFinder.getWeaponAnimPath(), "atlas",
                weapon.getWeaponType().toString().replace("_", " ")
                , groupName, name
                + getAtlasSuffix() + TexturePackerLaunch.ATLAS_EXTENSION);
        return s.toString();
    }

    private static String getAtlasSuffix() {
        return JPG_WEAPONS ? " jpg" : "";
    }

    private static boolean isAssymetric(String activeWeapon) {
        switch (activeWeapon) {
            case "Fist":
            case "Armored Fist":
                return true;

        }
        return false;
    }

    public static SpriteAnimation getFxSpriteForAction(DC_ActiveObj activeObj) {
        return null;
    }


    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     WEAPON_ANIM_CASE aCase, PROJECTION projection) {
        return getSpriteForAction(duration, activeObj,
                aCase, projection.bool);
    }

    private static String getPotionKey(DC_ActiveObj activeObj) {
        DC_QuickItemAction action = (DC_QuickItemAction) activeObj;
        boolean full = action.getItem().getIntParam(PARAMS.CHARGES) != 0;
        String suffix = full ? "_full" : "_half";

        return action.getItem().getName().replace(" ", "_") + suffix;
    }

    private static String getPotionAtlasPath(DC_ActiveObj activeObj) {
        DC_QuickItemAction action = (DC_QuickItemAction) activeObj;
        String name = action.getItem().getName();
        String level = name.split(" ")[0];
        return StrPathBuilder.build(PathFinder.getImagePath(), PathFinder.getSpritesPath(),
                "potions", "atlas", level, name + ".txt").replace(" ", "_");

    }

    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     WEAPON_ANIM_CASE aCase,
                                                     Boolean projection) {
        // loops,

        //TODO who is displayed above on the cell?
        //modify texture? coloring, sizing,
        //        float angle = PositionMaster.getAngle(activeObj.getOwnerUnit(), targetObj);
        //float baseAngle =
        //        float rotation = angle * 2 / 3;

        Array<AtlasRegion> regions = getRegions(aCase, activeObj, projection);


        float frameDuration = duration / regions.size;
        int loops = 0;
        if (aCase.isMissile()) {
            //            loops = Math.max(0,PositionMaster.getDistance(activeObj.getOwnerUnit(), targetObj) - 1);
        }
        if (loops != 0)
            frameDuration /= loops;

        SpriteAnimation sprite = SpriteAnimationFactory.
                getSpriteAnimation(regions, frameDuration, loops);
        //        sprite.setRotation(rotation);
        return sprite;
    }

    public static Array<AtlasRegion> getRegions(WEAPON_ANIM_CASE aCase, DC_ActiveObj activeObj, Boolean projection) {
        String name = getAtlasFileKeyForAction(projection, activeObj, aCase);

        TextureAtlas atlas = getAtlas(activeObj, aCase);
        if (atlas == null) {
            log(1, activeObj + " has invalid atlas: " + name);
            return SpriteAnimationFactory.dummySpriteRegions;
        }

        Array<AtlasRegion> regions = atlas.findRegions(name.toLowerCase());
        if (regions.size == 0) {
            regions = atlas instanceof SmartTextureAtlas
                    ? ((SmartTextureAtlas) atlas).findRegionsClosest(name.toLowerCase())
                    : null;
        }
        if (regions.size == 0) {
            regions = atlas.findRegions(name.toLowerCase()
                    .replace(ANIM + SEPARATOR, ""));
        }
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
            log(
                    1, activeObj + ": " + aCase + " no 3d sprites: " + name + atlas);
        return regions;
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
            types = Arrays.stream(DataManager.getBaseWeaponTypes()).
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

            log(
                    1, activeObj + " searching " + name);
            if (regions.size > 0)
                break;
        }
        return regions;

    }

    private static TextureAtlas getAtlas(DC_ActiveObj activeObj, WEAPON_ANIM_CASE aCase
    ) {
        if (aCase == WEAPON_ANIM_CASE.POTION) {
            return getOrCreateAtlas(getPotionAtlasPath(activeObj));
        }
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
        if (aCase.isMissile()) {
            if (weapon.getLastAmmo() == null)
                return null;
            weapon = weapon.getLastAmmo().getWrappedWeapon();
        }
        return getOrCreateAtlas(weapon);
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
        String path = null;

        try {
            path = getFullAtlasPath(weapon);
        } catch (Exception e) {
            log(1, "FAILED TO LOAD ATLAS FOR WEAPON: " + weapon);
            printStackTrace(e);
            return;
        }
        preloadAtlas(path);
    }

    private static void preloadAtlas(String path) {
        //        if (atlasMap.containsKey(path))
        //            return;
        if (!FileManager.isFile(path)) {
            //            brokenPaths.add(path)
            log(1, path + " needs to preload, but it is not a file!..");
            return;
        }
        if (!Assets.get().getManager().isLoaded(path)) {
            log(1, path + " loading...");
            Assets.get().getManager().load(path, TextureAtlas.class);
        }
        //        atlasMap.put(path, null);
    }

    public static TextureAtlas getOrCreateAtlas(DC_WeaponObj weapon) {
        if (broken.contains(weapon))
            return null;
        String path =
                getFullAtlasPath(weapon);
        try {
            return getOrCreateAtlas(path);
        } catch (Exception e) {
            printStackTrace(e);
            broken.add(weapon);
            return null;
        }
    }

    public static TextureAtlas getOrCreateAtlas(String path) {
        try {
            if (Flags.isIDE())
                if (isUseOneFrameVersions()) {
                    String p = getOneFramePath(path);
                    if (new FileHandle(p).exists()) {
                        // log(1, "One-frame atlas used:\n" + p);
                        return getOrCreateAtlas(p, true);
                    }
                }
            TextureAtlas atlas = getOrCreateAtlas(path, true);
            if (!isUseOneFrameVersions()) {
                return atlas;
            }
            //TODO we could also do something like halving frames, in the future...
            String p = getOneFramePath(path);
            AtlasRegion region = atlas.getRegions().get(atlas.getRegions().size / 2);//use mid frame
            String imgName =
                    StringMaster.cropFormat(PathUtils.getLastPathSegment(path)) + " img.png";
            String imgPath = PathUtils.cropLastPathSegment(p) + imgName;
            GdxImageMaster.writeImage(new FileHandle(imgPath), region);
            int w = (int) region.getRotatedPackedWidth();
            int h = (int) region.getRotatedPackedHeight();
            String size = w + ", " + h;
            // region.getTexture().getTextureData()
            String contents = getString(imgName,
                    StringMaster.cropFormat(imgName), size, size);
            FileManager.write(contents, p);
            log(1, "One-frame atlas saved:\n" +
                    contents + "\n\n at" + "\n" + p);
            return getOrCreateAtlas(p, true);
        } catch (Exception e) {
            printStackTrace(e);
            try {
                return new SmartTextureAtlas(path);
            } catch (Exception e1) {
                printStackTrace(e1);
            }
        }
        return null;
    }

    private static boolean isUseOneFrameVersions() {
        return !CellDecorLayer.spriteTest && !MaskTest.spriteMaskTest && CoreEngine.TEST_LAUNCH;
    }

    public static String getOneFrameImagePath(String path) {
        return PathFinder.getImagePath() + "gen/one_frame/" +
                StringMaster.cropFormat(path) + " img.png";
    }

    private static String getOneFramePath(String path) {
        return PathFinder.getImagePath() + "gen/one_frame/" +
                GdxImageMaster.cropImagePath(path);
    }


    public static String getString(String fileName, String textureName, String atlasSize, String size) {
        return "\n" +
                fileName +
                "\n" +
                "size: " +
                atlasSize +
                "\n" +
                "format: RGBA8888\n" +
                "filter: Nearest,Nearest\n" +
                "repeat: none\n" +
                textureName +
                "\n" +
                "  rotate: false\n" +
                "  xy: 0, 0\n" +
                "  size: " +
                size +
                "\n" +
                "  orig: 0, 0\n" +
                "  offset: 0, 0\n" +
                "  index: 1";
    }

    public static TextureAtlas getOrCreateAtlas(String path, boolean cache) {
        if (!new FileHandle(path).exists()) {
            path = GdxImageMaster.appendImagePath(path);
            if (!new FileHandle(path).exists()) {
                main.system.auxiliary.log.LogMaster.important("CRITICAL: No atlas for path - " + path);
                return null;
            }
        }
        path = TextureCache.formatTexturePath(path);

        TextureAtlas atlas = null;// cache ? atlasMap.get(path) : null;
        if (Assets.get().getManager().isLoaded(path)) {
            atlas = Assets.get().getManager().get(path);
        } else {
            if (!Assets.isOn()) {
                Chronos.mark("loading " + path);
                atlas = new SmartTextureAtlas(path);
                Chronos.logTimeElapsedForMark("loading " + path);
            } else {
                Chronos.mark("loading " + path);
                Assets.get().getManager().load(path, TextureAtlas.class);
                //                while (!Assets.getVar().getManager().isLoaded(path)) {
                //                    if (Assets.getVar().getManager().update())
                //                        break;
                //                }

                while (!Assets.get().getManager().isLoaded(path)) {
                    log(1, "... loading " + path);
                    //                    if (Assets.get().getManager().update(5000))
                    if (Assets.get().getManager().update(10))
                        break;
                }
                //                while (!Assets.get().getManager().update(1000)) {
                //                    if (Assets.get().getManager().isLoaded(path))
                //                        break;
                //                    main.system.auxiliary.log.LogMaster.log(1, "... loading " + path);
                //                }

                if (!Assets.get().getManager().isLoaded(path)) {
                    log(1, "************* Atlas failed to load! " + path);
                    //                    main.system.auxiliary.log.LogMaster.log(1, "************* ALT_ASSET_LOAD set to TRUE " + path);
                    //                    OptionsMaster.getGraphicsOptions().setValue(GraphicsOptions.GRAPHIC_OPTION.ALT_ASSET_LOAD, true);
                    //                    Assets.setON(false);
                    //                    OptionsMaster.saveOptions();
                    return null; //getOrCreateAtlas(path, cache);
                }
                try {
                    Assets.get().getManager().finishLoadingAsset(path);
                    atlas = Assets.get().getManager().get(path, TextureAtlas.class);
                } catch (Exception e) {
                    printStackTrace(e);
                    FileLogManager.streamMain("CRITICAL: asset not loaded - " + path);

                    LogMaster.important("ALL assets: \n"
                            + Assets.get().getManager().getDiagnostics());
                }
                if (atlas == null) {
                    FileLogManager.streamMain("Trying lazy load... - " + path);
                    int time = 0;
                    while (time < 10000) {
                        WaitMaster.WAIT(1000);
                        time += 1000;
                        try {
                            atlas = Assets.get().getManager().get(path, TextureAtlas.class);
                        } catch (Exception e) {
                            //                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                }
                if (atlas == null) {
                    FileLogManager.streamMain("Lazy load failed! - " + path);
                } else {
                    Chronos.logTimeElapsedForMark("loading " + path);
                }
            }
        }
        if (cache) {
            //            atlasMap.put(path, atlas);
        }
        return atlas;


    }

    public static String getFullAtlasPath(DC_WeaponObj weapon) {
        try {
            return TextureCache.formatTexturePath(PathFinder.getImagePath()
                    + getAtlasPath(weapon, getWeaponAtlasKey(weapon)));
        } catch (Exception e) {
            printStackTrace(e);
        }
        return null;
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

    private static boolean isReadyAnimSupported(DC_UnitAction entity) {
        return false;
        //        return is3dAnim(entity);
        //        return entity.getActiveWeapon().getName().contains("Short Sword");
    }

    private static Animation getReadyAnim(DC_UnitAction entity) {
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
        //        if (EidolonsGame.BRIDGE)
        //            return true;
        //        if (CoreEngine.isIDE())
        //            if (!CoreEngine.isGraphicTestMode())
        //                if (CoreEngine.isLiteLaunch()) {
        //                    if (!EidolonsGame.BOSS_FIGHT)
        //                        return true;
        //                }
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

    public static PROJECTION getProjectionByFacing(FACING_DIRECTION facing) {
        if (!facing.isVertical())
            return PROJECTION.HOR;
        return facing == main.game.bf.directions.FACING_DIRECTION.NORTH ? PROJECTION.TO : PROJECTION.FROM;
    }

    public static PROJECTION getProjection(Ref ref, DC_ActiveObj active) {
        if (ref.getTargetObj() == null || ref == null)
            return getProjectionByFacing(active.getOwnerUnit().getFacing());
        Boolean b =
                PositionMaster.isAboveOr(ref.getSourceObj(), ref.getTargetObj());
        if (active.getOwnerUnit().getCoordinates().equals(ref.getTargetObj().getCoordinates()))
            b = active.getOwnerUnit().isMine();
        PROJECTION projection = PROJECTION.HOR;
        if (b != null)
            projection = b ? PROJECTION.FROM : PROJECTION.TO;
        return projection;
    }


    public enum PROJECTION {
        FROM(true), TO(false), HOR(null),
        ;
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
        RELOAD,
        POTION,
        ;

        public boolean isMissile() {
            return this == WEAPON_ANIM_CASE.MISSILE || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }

        public boolean isMiss() {
            return this == WEAPON_ANIM_CASE.MISS || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }
    }
}
