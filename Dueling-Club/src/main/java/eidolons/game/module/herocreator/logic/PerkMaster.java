package eidolons.game.module.herocreator.logic;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.utils.PerkGenerator.PERK_TYPE;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.CLASS_PERK_GROUP;
import main.content.enums.entity.HeroEnums.PERK_PARAM;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/1/2018.
 */
public class PerkMaster {


    public static List<ObjType> getAvailablePerks(Unit hero, int tier,
                                                  HeroClass c1, HeroClass c2) {
        List<ObjType> list = new ArrayList<>();

        if (HeroClassMaster.isDataAnOpenSlot(c1))
            return list;
        if (HeroClassMaster.isDataAnOpenSlot(c2))
            return list;

        CLASS_PERK_GROUP group1 =
         new EnumMaster<CLASS_PERK_GROUP>().retrieveEnumConst(CLASS_PERK_GROUP.class,
          c1.getProperty(PROPS.CLASS_PERK_GROUP));
        CLASS_PERK_GROUP group2 =
         new EnumMaster<CLASS_PERK_GROUP>().retrieveEnumConst(CLASS_PERK_GROUP.class,
          c2.getProperty(PROPS.CLASS_PERK_GROUP));

        Set<PERK_PARAM> paramPerks =
         new LinkedHashSet<>(Arrays.asList(group1.getParamPerks()));
        paramPerks.addAll(Arrays.asList(group2.getParamPerks()));

        main:
        for (ObjType type : DataManager.getTypes(DC_TYPE.PERKS)) {
            if (isPerkProhibited(type, hero))
                continue;
            if (!type.getProperty(G_PROPS.GROUP).equalsIgnoreCase(PERK_TYPE.PARAMETER.toString())) {
                if (checkCustomPerkReqs(type, hero, c1, c2))
                    list.add(type);
            } else
                for (PERK_PARAM sub : paramPerks) {
                    if (type.getIntParam(PARAMS.CIRCLE) == tier)
                        if (type.getProperty(PROPS.PERK_PARAM).
                         equalsIgnoreCase(sub.toString())) {
                            list.add(type);
                            continue main;
                        }
                }
        }
        if (c1.getType() == c2.getType()) {
            //addSpecial
            List<ObjType> custom = getCustomPerks(c1);
            list.addAll(custom);
        }

        return list;
    }

    private static List<ObjType> getCustomPerks(HeroClass c1) {
        List<ObjType> list = DataManager.getTypes(DC_TYPE.PERKS).stream().filter(
                t -> t.getProperty(PROPS.PERK_FOR_CLASSES)
                        .toLowerCase(). contains(c1.toString().toLowerCase())
        ).collect(Collectors.toList());
        return list;
    }

    private static boolean isPerkProhibited(ObjType type, Unit hero) {
        for (Perk perk : hero.getPerks()) {
            if (perk.getType().equals(type)) {
                return true;
            }

        }
        return false;
    }

    private static boolean checkCustomPerkReqs(ObjType type, Unit hero, HeroClass c1, HeroClass c2) {
        String string = type.getProperty(G_PROPS.PERK_CLASS_REQUIREMENTS);
        //syntax: OR== class1;class2 AND == class1+class2;class3+class2;...
        for (String substring : ContainerUtils.openContainer(string)) {
            if (substring.contains("+")) {
                if (checkAliasForClass(substring, c1) && checkAliasForClass(substring, c2)) {
                    return true;
                }
            } else {
                if (checkAliasForClass(substring, c1) || checkAliasForClass(substring, c2)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkAliasForClass(String substring, HeroClass heroClass) {
        substring = substring.toLowerCase();
        if (substring.contains(heroClass.getName().toLowerCase())) return true;
        CLASS_PERK_GROUP group =
         new EnumMaster<CLASS_PERK_GROUP>().retrieveEnumConst(CLASS_PERK_GROUP.class,
          heroClass.getProperty(PROPS.CLASS_PERK_GROUP));
        if (substring.contains(StringMaster.getWellFormattedString(
         group.toString()).toLowerCase())) {
            return true;
        }
        return false;
    }

    public static List<Perk> getPerks(Unit hero, int tier) {
        ArrayList<Perk> list = new ArrayList<>(hero.getPerks());
        list.removeIf(c -> c.getTier() != tier);
        return list;
    }
}
