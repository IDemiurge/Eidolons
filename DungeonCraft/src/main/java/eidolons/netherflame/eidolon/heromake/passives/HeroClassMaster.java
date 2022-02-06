package eidolons.netherflame.eidolon.heromake.passives;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.attach.ClassRank;
import eidolons.entity.unit.Unit;
import eidolons.entity.unit.netherflame.HeroUnit;
import eidolons.game.core.Core;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.ClassEnums.CLASS_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.datatypes.WeightMap;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 * <p>
 * Class Ranks Class reqs
 */
public class HeroClassMaster {
    public static final String CLASSES_TIER_ = "CLASSES_TIER_";
    private static ObjType openSlotType;
    private static ClassRank openSlot;

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

    public static List<DC_PassiveObj> getClasses(Unit hero, int tier) {
        ArrayList<DC_PassiveObj> list = new ArrayList<>(hero.getClassRanks());
        list.removeIf(c -> c.getTier() != tier);
        if (list.size() < getMaxClassSlots(tier)) {
            list.add(getOpenSlot());
        }
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

    public static List<ObjType> getPotentiallyAvailableClasses(Unit hero, int tier) {
        List<ObjType> list = new ArrayList<>(DataManager.getTypes(DC_TYPE.CLASSES));

        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier
                || (tier > 0 && !hasRootTreeClass(hero, type)) //TODO hasEmptyClassSlot(hero) ||
        );
        //        ContentFilter
        //         if (tier>0) {
        //         HqMaster.filterContent(list);
        //         }
        //just check same root tree!

        return list;
    }

    private static boolean hasRootTreeClass(Unit hero, ObjType type) {
        String property = type.getProperty(G_PROPS.CLASS_GROUP);
        if (hero.getProperty(PROPS.FIRST_CLASS).equalsIgnoreCase(property)) {
            return true;
        }
        if (hero.getProperty(PROPS.SECOND_CLASS).equalsIgnoreCase(property)) {
            return true;
        }
        return hero.getProperty(PROPS.THIRD_CLASS).equalsIgnoreCase(property);
    }

    public static List<ObjType> getClassesToChooseFrom(Unit hero, int tier) {
        if (Flags.isIggDemoRunning()) {
            return getPotentiallyAvailableClasses(hero, tier);
        }
        return getAllClasses(hero, tier);
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
            List<DC_PassiveObj> classes = getClasses(entity, i);
            for (DC_PassiveObj sub : classes) {
                CLASS_GROUP group = new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class, sub.getProperty(G_PROPS.CLASS_GROUP));
                if (!list.contains(group))
                    list.add(group);
            }
        }
        return list;
    }

    public static String getPerkInfo(Entity classType) {
        List<ObjType> perks = PerkMaster.getAvailablePerks(Core.getMainHero(), classType.getIntParam("circle"), classType, classType);
        StringBuilder sBuilder = new StringBuilder("Available class perks: \n");
        for (ObjType type : perks) {
            sBuilder.append(type.getName()).append(", ");
        }
        String s = sBuilder.toString();
        return s.substring(0, s.length() - 2);
    }

    public static String getNextClassInfo(Entity classType) {
        List<ObjType> classes = DataManager.getTypes(DC_TYPE.CLASSES);
        StringBuilder s = new StringBuilder("Unlocked Classes: \n");
        for (ObjType aClass : classes) {
            if (aClass.getProperty(G_PROPS.BASE_TYPE).equalsIgnoreCase(classType.getName())) {
                s.append("   ").append(aClass.getName()).append("\n");
            }
        }
        return s.toString();
    }

    public static String getImgPath(Entity data) {
        String path = "gen/class/64/" + data.getName() + ".png";
        if (GdxStringUtils.isImage(path))
            return path;
        return (data.getImagePath());
    }

    public static String getImgPathRadial(Entity data) {
        String path = "gen/class/96/" + data.getName() + ".png";
        if (GdxStringUtils.isImage(path))
            return path;
        return (data.getImagePath());
        //        String path =GdxImageMaster.getRoundedPathNew(data.getName() + ".png");
        //        if (TextureCache.isImage(path))
        //            return path;
        //
        //        path = "main/skills/gen/" + data.getName() + ".png";
        //        if (TextureCache.isImage(path))
        //            return path;
        //        return GdxImageMaster.getRoundedPath(data.getImagePath());
    }

    public static boolean isDataAnOpenSlot(Object lastData) {
        return lastData == getOpenSlot();
    }

    public static ClassRank getOpenSlot() {
        if (openSlot == null) {
            openSlot = new ClassRank(getOpenSlotType(), (Core.getMainHero()));
        }
        return openSlot;
    }

    public static ObjType getOpenSlotType() {
        if (openSlotType == null) {
            openSlotType = new ObjType("Dummy Class", DC_TYPE.CLASSES);
        }
        return openSlotType;
    }


    public static WeightMap<ClassEnums.CLASS_RANK> getRankWeightMap(HeroUnit hero) {
        for (ClassRank classRank : hero.getClassRanks()) {

        }
        return null;
    }
}
