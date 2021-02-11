package eidolons.content.consts.libgdx;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.DC_WeaponObj;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;

public class GdxUtils {


    public static String getSizedImagePath(String path, int size) {
        return getSizedImagePath(path, size, null);
    }

    public static String getSizedImagePath(String path, int size, String cropSuffix) {
        path = FileManager.formatPath(path);
        if (cropSuffix != null) {
            path = StringMaster.cropSuffix(path, cropSuffix);
        }
        return StringMaster.cropFormat(path) + " sized " + size + StringMaster.getFormat(path);
    }

    public static String appendImagePath(String s) {
        s = cropImagePath(s);
        return PathFinder.getImagePath().toLowerCase() + "/" + s;
    }

    public static String cropImagePath(String s) {
        s = FileManager.formatPath(s, true, true);
        return s.replace(PathFinder.getImagePath().toLowerCase(), "");
    }

    public static String getAttackActionPath(DC_ActiveObj obj) {
        return getAttackActionPath(obj, obj.getActiveWeapon());
    }

    public static String getAttackActionPath(DC_ActiveObj obj, DC_WeaponObj weapon) {
        return (!obj.isStandardAttack() || obj.isThrow()) ? getWeaponIconPath(weapon)
                : getStandardAttackIcon(obj);
        //            if (obj.isOffhand()){
        //                Texture texture = GdxImageMaster.flip(path, true, false, true);
        //                return new TextureRegion(texture);
        //            }
    }

    public static String getArmorIconPath(Entity entity) {
        return GdxUtils.getItemIconPath(entity);
    }

    public static String getWeaponIconPath(Entity entity) {
        return GdxUtils.getItemIconPath(entity);
    }
    private static String getStandardAttackIcon(DC_ActiveObj obj) {
        DC_WeaponObj weapon = obj.getActiveWeapon();
        return getStandardAttackIcon(obj.getType(), weapon.getType());
    }

    private static String getStandardAttackIcon(String baseType, String weaponGroup,
                                                ObjType action) {
        return StrPathBuilder.build("main", "actions", "standard attack",
                weaponGroup,
                baseType,
                action.getName().replace(ActionEnums.OFFHAND, "").replace(" ", "_") + ".png");
    }

    private static String getStandardAttackIcon(ObjType action, ObjType weapon) {
        String baseType = weapon.getProperty(G_PROPS.BASE_TYPE);
        String weaponGroup = weapon.getProperty(G_PROPS.WEAPON_GROUP);
        String path = getStandardAttackIcon(baseType, weaponGroup, action);


        if (!ImageManager.isImage(path)) {
            path = path.replace("_", "");
            if (!ImageManager.isImage(path))
                path = findClosestIcon(action, weapon).replace("_", "");
        }
        return path;
    }

    private static String findClosestIcon(ObjType action, ObjType weapon) {
        String path;
        String subgroup = weapon.getSubGroupingKey();
        String baseType;
        String weaponGroup = weapon.getProperty(G_PROPS.WEAPON_GROUP);
        for (ObjType sub : DataManager.getTypesSubGroup(DC_TYPE.WEAPONS, subgroup)) {
            baseType = sub.getName();
            path = getStandardAttackIcon(baseType, weaponGroup, action);
            if (ImageManager.isImage(path)) {
                return path;
            }
        }
        return weapon.getImagePath();
    }

    public static boolean isRoundedPath(String name) {
        return name.contains(" rounded");
    }

    public static boolean isSizedPath(String name) {
        return name.contains(" sized ");
    }

    public static boolean isGeneratedFile(String path) {
        if (path.contains("sized")) {
            return true;
        }
        if (path.contains("rounded")) {
            return true;
        }
        return path.contains(" Copy");
    }

    public static String getItemIconPath(Entity entity) {
        if (entity == null) {
            return "";
        }
        DC_TYPE TYPE = (DC_TYPE) entity.getOBJ_TYPE_ENUM();
        String basePath = "";
        switch (TYPE) {
            case ARMOR:
                basePath = PathFinder.getArmorIconPath();
                break;
            case WEAPONS:
                basePath = PathFinder.getWeaponIconPath();
                break;
            case JEWELRY:
                basePath = PathFinder.getJewelryIconPath();
                break;
        }
        String typeName = entity.getName();


        if (entity instanceof DC_HeroSlotItem) {
            DC_HeroSlotItem item = ((DC_HeroSlotItem) entity);
            typeName = item.getBaseTypeName();
            boolean versioned=false;
            if (TYPE.getName().contains("Crossbow")){
                typeName = "Crossbow";
            }
            if (TYPE == DC_TYPE.ARMOR ) {
                versioned =true;
            }
            if (item instanceof DC_WeaponObj) {
                versioned=((DC_WeaponObj) item).isAmmo();
            }
            if (versioned){
                int durability = DataManager.getType(item.getBaseTypeName(),
                 item.getOBJ_TYPE_ENUM()).getIntParam(PARAMS.DURABILITY);
                float perc = (float) durability / item.getIntParam(PARAMS.C_DURABILITY);

                if (perc < 0.75f) {
                    typeName += " 2";
                }
                if (perc < 1.25f) {
                    typeName += " 1";
                }
        }
        }

        String path = StrPathBuilder.build(basePath,
         typeName + ".png");
        if (!ImageManager.isImage(path))
            path = entity.getImagePath();
        return path;
    }

    public static String getOneFrameImagePath(String path) {
        return PathFinder.getImagePath() + "gen/one_frame/" +
                StringMaster.cropFormat(path) + " img.png";
    }

    public static String getOneFramePath(String path) {
        return PathFinder.getImagePath() + "gen/one_frame/" +
                cropImagePath(path);
    }
}
