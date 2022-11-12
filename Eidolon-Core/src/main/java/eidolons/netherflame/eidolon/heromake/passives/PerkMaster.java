package eidolons.netherflame.eidolon.heromake.passives;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.unit.attach.Perk;
import eidolons.entity.unit.Unit;
import main.content.DC_TYPE;
import main.content.enums.entity.ClassEnums.CLASS_PERK_GROUP;
import main.content.enums.entity.PerkEnums.PERK_PARAM;
import main.content.enums.entity.NewRpgEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/1/2018.
 */
public class PerkMaster {

    public static class PerkData extends DataUnit<NewRpgEnums.PerkValue> {
        public PerkData(String text) {
            super(text);
        }

        public String getPerk(int tier) {
            return values.get("perks_" + tier);
        }

        public String getQuirk(int tier) {
            return values.get("quirks_" + tier);
        }

        public String getAdditionalPerk(int tier) {
            return values.get("add_perks_" + tier);
        }
    }

    public static boolean isAdditionalPerkSlotAvailable(Unit hero, int tier, int slot) {
        return getPerkData(hero).getQuirk(tier).isEmpty();
    }

    private static PerkData getPerkData(Unit hero) {
        return new PerkData(hero.getProperty(PROPS.PERKS));
    }

    public static List<ObjType> getAvailablePerks(Unit hero, int tier,
                                                  Entity c1, Entity c2) {
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
            if (!type.getProperty(G_PROPS.GROUP).equalsIgnoreCase(VisualEnums.PERK_TYPE.PARAMETER.toString())) {
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

    private static List<ObjType> getCustomPerks(Entity c1) {
        return DataManager.getTypes(DC_TYPE.PERKS).stream().filter(
                t -> t.getProperty(PROPS.PERK_FOR_CLASSES)
                        .toLowerCase().contains(c1.getName().toLowerCase())
        ).collect(Collectors.toList());
    }

    private static boolean isPerkProhibited(ObjType type, Unit hero) {
        for (Perk perk : hero.getPerks()) {
            if (perk.getType().equals(type)) {
                return true;
            }

        }
        return false;
    }

    private static boolean checkCustomPerkReqs(ObjType type, Unit hero, Entity c1, Entity c2) {
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

    private static boolean checkAliasForClass(String substring, Entity heroClass) {
        substring = substring.toLowerCase();
        if (substring.contains(heroClass.getName().toLowerCase())) return true;
        CLASS_PERK_GROUP group =
                new EnumMaster<CLASS_PERK_GROUP>().retrieveEnumConst(CLASS_PERK_GROUP.class,
                        heroClass.getProperty(PROPS.CLASS_PERK_GROUP));
        return substring.contains(StringMaster.format(
                group.toString()).toLowerCase());
    }

    public static List<Perk> getPerks(Unit hero, int tier) {
        ArrayList<Perk> list = new ArrayList<>(hero.getPerks());
        list.removeIf(c -> c.getTier() != tier);
        return list;
    }
}
