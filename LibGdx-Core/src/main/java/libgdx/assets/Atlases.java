package libgdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.battlecraft.rules.combat.attack.AttackTypes;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import libgdx.GdxImageMaster;
import libgdx.GdxMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.sprite.SpriteX;
import libgdx.assets.utils.AtlasGen;
import libgdx.gui.panels.dc.actionpanel.bar.SpriteParamBar;
import libgdx.texture.SmartTextureAtlas;
import libgdx.texture.TextureCache;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static main.system.ExceptionMaster.printStackTrace;
import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;

public class Atlases {

    public static final List<AssetEnums.ATLAS> all = new LinkedList<>();
    private static final Map<String, Array<TextureAtlas.AtlasRegion>> atlasRegionsCache = new HashMap<>();
    // private static final Map<String, TextureAtlas> atlasMap = new HashMap<>();
    private static final List<DC_WeaponObj> broken = new ArrayList<>();
    static final Map<String, TextureAtlas> atlasMap = new ConcurrentHashMap<>();
    static ObjectMap<String, String> substituteMap;

    static {
        init();
    }

    public static void preloadAtlas(AssetEnums.ATLAS atlas) {
        preloadAtlas(atlas.path);
    }

    public static void loaded(String fileName, SmartTextureAtlas atlas) {
        AssetEnums.ATLAS e = getAtlasByName(fileName);
        if (e != null) {
            e.file = atlas;            // we want this field because we will do lookup in these atlases!
            if (e != AssetEnums.ATLAS.SPRITES_ONEFRAME) {
                all.add(e);
            }
            atlas.setType(e);
        }
    }

    private static AssetEnums.ATLAS getAtlasByName(String fileName) {
        for (AssetEnums.ATLAS value : AssetEnums.ATLAS.values()) {
            if (value.path.equalsIgnoreCase(fileName)) {
                return value;
            }
        }
        return null;
    }

    public static Array<TextureAtlas.AtlasRegion> getAtlasRegions(String texturePath) {
        boolean oneFrame = false;
        if (isUseOneFrameVersion(texturePath)) {
            oneFrame = true;
            texturePath = StringMaster.getAppendedFile(GdxStringUtils.getOneFramePath(texturePath), " img");
        }
        AssetEnums.ATLAS atlas = AtlasGen.getAtlasForPath(texturePath);
        return getAtlasRegions(texturePath,
                oneFrame ? AssetEnums.ATLAS.SPRITES_ONEFRAME :atlas);
                        // ui ?   AssetEnums.ATLAS.SPRITES_UI :    AssetEnums.ATLAS.SPRITES_GRID);
    }

    public static Array<TextureAtlas.AtlasRegion> getAtlasRegions(String texturePath, AssetEnums.ATLAS atlas) {
        texturePath =
                GdxStringUtils.cropImagePath(StringMaster.cropFormat(texturePath));

        Array<TextureAtlas.AtlasRegion> regions = atlasRegionsCache.get(texturePath);
        if (regions != null) {
            return regions;
        }
        if (atlas != null && atlas.file != null) regions = atlas.file.findRegions(texturePath);
        if (regions == null)
            for (AssetEnums.ATLAS a : all) {
                if (a == atlas) continue;
                regions = a.file.findRegions(texturePath);
                if (regions != null && regions.size > 0) {
                    break;
                }
            }
        if (regions.size > 0) {
            atlasRegionsCache.put(texturePath, regions);
            important(regions.size + " Atlas regions found: " + texturePath);
        } else {
            important("No atlas regions found: " + texturePath);
        }
        return regions;
    }


    public static void init() {
        substituteMap = new ObjectMap<>();
        for (String[] sub : AssetEnums.substitutesWeapons) {
            substituteMap.put(sub[0], sub[1]);
        }
        for (String[] sub : AssetEnums.substitutesActions) {
            substituteMap.put(sub[0], sub[1]);
        }
    }

    public static String getAtlasFileKeyForAction(String projection,
                                                  String weaponName, String actionName,
                                                  Boolean offhand) {
        StringBuilder s = new StringBuilder();
        s.append(
                ContainerUtils.join(AssetEnums.SEPARATOR,
                        weaponName,
                        actionName, AssetEnums.ANIM, projection
                ));

        //       TODO  if (BooleanMaster.isTrue(offhand))
        //            s.append(SEPARATOR + "l");
        //        if (BooleanMaster.isFalse(offhand))
        if (offhand != null)
            s.append(AssetEnums.SEPARATOR + "r");
        String string = s.toString();
        string = string.toLowerCase().replace("offhand ", "").replace("off hand ", "").replace(" ", AssetEnums.SEPARATOR);
        return string;
    }

    public static String getAtlasFileKeyForAction(Boolean projection,
                                                  DC_ActiveObj activeObj, VisualEnums.WEAPON_ANIM_CASE aCase) {
        if (aCase == VisualEnums.WEAPON_ANIM_CASE.POTION)
            return getPotionKey(activeObj);
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
        String actionName;
        String projectionString;

        //        if (aCase != WEAPON_ANIM_CASE.RELOAD) {
        projectionString = (projection == null ? "hor" :
                (projection ? "from" : "to"));
        //        }
        if (aCase.isMissile()) {
            if (weapon.getLastAmmo() == null)
                return null;
            weapon = weapon.getLastAmmo().getWrappedWeapon();

        }
        if (aCase.isMiss() && AnimMaster3d.isMissSupported()) {
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
        if (AnimMaster3d.isAssymetric(weapon.getBaseTypeName()))
            offhand = (activeObj.isOffhand());

        return getAtlasFileKeyForAction(projectionString,
                getWeaponAtlasKey(weapon),
                actionName, offhand);
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
                        ItemEnums.ARMOR_TYPE.HEAVY)
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
                + GdxStringUtils.ATLAS_EXTENSION);
        return s.toString();
    }

    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     VisualEnums.WEAPON_ANIM_CASE aCase, VisualEnums.PROJECTION projection) {
        return getSpriteForAction(duration, activeObj,
                aCase, projection.bool);
    }

    private static String getPotionKey(DC_ActiveObj activeObj) {
        DC_QuickItemAction action = (DC_QuickItemAction) activeObj;
        boolean full = action.getItem().getIntParam(PARAMS.CHARGES) != 0;
        String suffix = full ? "_full" : "_half";

        return action.getItem().getName().replace(" ", "_") + suffix;
    }

    static String getPotionAtlasPath(DC_ActiveObj activeObj) {
        DC_QuickItemAction action = (DC_QuickItemAction) activeObj;
        String name = action.getItem().getName();
        String level = name.split(" ")[0];
        return StrPathBuilder.build(PathFinder.getImagePath(), PathFinder.getSpritesPath(),
                "potions", "atlas", level, name + ".txt").replace(" ", "_");

    }

    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     VisualEnums.WEAPON_ANIM_CASE aCase,
                                                     Boolean projection) {
        // loops,

        //TODO who is displayed above on the cell?
        //modify texture? coloring, sizing,
        //        float angle = PositionMaster.getAngle(activeObj.getOwnerUnit(), targetObj);
        //float baseAngle =
        //        float rotation = angle * 2 / 3;

        Array<TextureAtlas.AtlasRegion> regions = getRegions(aCase, activeObj, projection);


        float frameDuration = duration / regions.size;
        int loops = 0;
        if (aCase.isMissile()) {
            //            loops = Math.max(0,PositionMaster.getDistance(activeObj.getOwnerUnit(), targetObj) - 1);
        }
        if (loops != 0)
            frameDuration /= loops;

        //        sprite.setRotation(rotation);
        return SpriteAnimationFactory.
                getSpriteAnimation(regions, frameDuration, loops);
    }

    public static Array<TextureAtlas.AtlasRegion> getRegions(VisualEnums.WEAPON_ANIM_CASE aCase, DC_ActiveObj activeObj, Boolean projection) {
        String name = getAtlasFileKeyForAction(projection, activeObj, aCase);

        TextureAtlas atlas = getAtlas(activeObj, aCase);
        if (atlas == null) {
            log(1, activeObj + " has invalid atlas: " + name);
            return SpriteAnimationFactory.dummySpriteRegions;
        }

        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(name.toLowerCase());
        if (regions.size == 0) {
            regions = atlas instanceof SmartTextureAtlas
                    ? ((SmartTextureAtlas) atlas).findRegionsClosest(name.toLowerCase())
                    : null;
        }
        if (regions.size == 0) {
            regions = atlas.findRegions(name.toLowerCase()
                    .replace(AssetEnums.ANIM + AssetEnums.SEPARATOR, ""));
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

    private static Array<TextureAtlas.AtlasRegion> findAtlasRegions(TextureAtlas atlas,
                                                                    Boolean projection,
                                                                    DC_ActiveObj activeObj,
                                                                    boolean searchOtherWeaponOrAction) {
        String name = getAtlasFileKeyForAction(projection, activeObj, VisualEnums.WEAPON_ANIM_CASE.NORMAL);
        List<Entity> types;
        if (searchOtherWeaponOrAction) {
            types = Arrays.stream(DataManager.getBaseWeaponTypes()).
                    filter(type -> type.getProperty(G_PROPS.WEAPON_GROUP).equals(
                            activeObj.getActiveWeapon().getProperty(G_PROPS.WEAPON_GROUP))).collect(Collectors.toList());
        } else {
            // types = new ArrayList<>(
            //         activeObj.getParentAction().getSubActions());
            types= AttackTypes.getAttackTypes(activeObj);
        }
        Array<TextureAtlas.AtlasRegion> regions = null;
        for (Entity sub : types) {
            name = sub.getName() + name.substring(name.indexOf(AssetEnums.SEPARATOR));
            regions = atlas.findRegions(name.toLowerCase());

            log(
                    1, activeObj + " searching " + name);
            if (regions.size > 0)
                break;
        }
        return regions;

    }

    private static TextureAtlas getAtlas(DC_ActiveObj activeObj, VisualEnums.WEAPON_ANIM_CASE aCase
    ) {
        if (aCase == VisualEnums.WEAPON_ANIM_CASE.POTION) {
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
        String path;

        try {
            path = getFullAtlasPath(weapon);
        } catch (Exception e) {
            log(1, "FAILED TO LOAD ATLAS FOR WEAPON: " + weapon);
            printStackTrace(e);
            return;
        }
        preloadAtlas(path);
    }

    static void preloadAtlas(String path) {
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
            // if (Flags.isIDE() || Flags.isJarlike())
            if (isUseOneFrameVersion(path)) {
                String p = GdxStringUtils.getOneFramePath(path);
                if (new FileHandle(p).exists()) {
                    // log(1, "One-frame atlas used:\n" + p);
                    return getOrCreateAtlas(p, true);
                }
            }
            TextureAtlas atlas = getOrCreateAtlas(path, true);
            if (!isUseOneFrameVersion(path) || !Flags.isIDE()) {
                return atlas;
            }
            if (atlas == null) {
                return null;
            }
            //MEGA-HACK - ONE FRAME GENERATION
            //TODO we could also do something like halving frames, in the future...
            String p = GdxStringUtils.getOneFramePath(path);
            TextureAtlas.AtlasRegion region = atlas.getRegions().get(atlas.getRegions().size / 2);//use mid frame
            String imgName =
                    StringMaster.cropFormat(PathUtils.getLastPathSegment(path)) + " img.png";
            String imgPath = PathUtils.cropLastPathSegment(p) + imgName;
            GdxImageMaster.writeImage(new FileHandle(imgPath), region);
            int w = (int) region.getRotatedPackedWidth();
            int h = (int) region.getRotatedPackedHeight();
            String size = w + ", " + h;
            // region.getTexture().getTextureData()
            String contents = getDefaultAtlasTxtContents(imgName,
                    StringMaster.cropFormat(imgName), size, size);
            FileManager.write(contents, p);
            log(1, "One-frame atlas saved:\n" +
                    contents + "\n\n at" + "\n" + p);
            return getOrCreateAtlas(p, true);
        } catch (Exception e) {
            printStackTrace(e);
        }
        return null;
    }

    public static boolean isUseOneFrameVersion(String path) {
        if (CoreEngine.isLevelEditor()) {
            return true;
        }
        if (path != null) {
        if (!path.contains("soulforce"))
            if (path.contains("fly"))
                if (SpriteX.TEST_MODE) {
                    return false;
                }
        if (SpriteParamBar.TEST)
            if (path.contains("soulforce")) {
                return false;
            }

        if (path.contains("dust")) {
            return false;
        }
        }
        return Flags.isLiteLaunch();
        // return !CellDecorLayer.spriteTest && !MaskTest.spriteMaskTest && CoreEngine.TEST_LAUNCH;
    }

    public static String getDefaultAtlasTxtContents(String fileName, String textureName, String atlasSize, String size) {
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
            path = GdxStringUtils.appendImagePath(path);
            if (!new FileHandle(path).exists()) {
                important("CRITICAL: No atlas for path - " + path);
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
                    return null;
                }
                try {
                    Assets.get().getManager().finishLoadingAsset(path);
                    atlas = Assets.get().getManager().get(path, TextureAtlas.class);
                } catch (Exception e) {
                    printStackTrace(e);
                    FileLogManager.streamMain("CRITICAL: asset not loaded - " + path);

                    important("ALL assets: \n"
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
            atlasMap.put(path, atlas);
        }
        return atlas;


    }

    public static Map<String, TextureAtlas> getAtlasMap() {
        return atlasMap;
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
}
