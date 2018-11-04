package eidolons.game.module.herocreator.logic;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.texture.TextureCache;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 * <p>
 * Class Ranks
 * Class reqs
 */
public class HeroClassMaster {
    public static final String CLASSES_TIER_ = "CLASSES_TIER_";

    public static boolean isMulticlass(Entity type) {
        return false; //TODO
    }

    public static void classRankAcquired(Unit hero, int tier) {

//        String notChosenPerks = hero.getProperty(PROPS.PENDING_PERKS);

        String chosenPerks = hero.getProperty(PROPS.PERKS);
        for (String sub : ContainerUtils.openContainer(
         hero.getProperty(ContentValsManager.getPROP("CLASSES_TIER_" + tier)))) {

        }

    }

    public static List<DC_FeatObj> getClasses(Unit hero, int tier) {
        ArrayList<DC_FeatObj> list = new ArrayList<>(hero.getClasses());
        list.removeIf(c -> c.getTier() != tier);
        return list;
    }

    public static int getMaxClassSlots(int tier) {
        return 5 - tier;
    }

    public static List<ObjType> getAvailableClasses(List<ObjType> list, Unit hero, int tier) {
        list.removeIf(type -> hero.getGame().getRequirementsManager().check(hero, type) != null);
        return list;
    }

    public static List<ObjType> getAvailableClasses(Unit hero, int tier) {
        List<ObjType> list = new ArrayList<>(DataManager.getTypes(DC_TYPE.CLASSES));
        //check if branching is OK
        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier
         || hero.getGame().getRequirementsManager().check(hero, type) != null
        );

        return list;
    }

    public static List<ObjType> getAllClasses(Unit hero, int tier) {
        List<ObjType> list = new ArrayList<>(DataManager.getTypes(DC_TYPE.CLASSES));
        //check if branching is OK
        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier);

        return list;
    }

    public static List<CLASS_GROUP> getClassGroups(HeroDataModel entity) {
        List<CLASS_GROUP> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<DC_FeatObj> classes = getClasses(entity, i);
            for (DC_FeatObj sub : classes) {
                CLASS_GROUP group = new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class, sub.getProperty(G_PROPS.CLASS_GROUP));
                if (!list.contains(group))
                    list.add(group);
            }
        }
        return list;
    }
    public static String getImgPath(Entity data) {
        String path = "main/skills/gen/" + data.getName() + ".png";
        if (TextureCache.isImage(path))
            return path;

        return GdxImageMaster.getRoundedPath(data.getImagePath());
    }
}
