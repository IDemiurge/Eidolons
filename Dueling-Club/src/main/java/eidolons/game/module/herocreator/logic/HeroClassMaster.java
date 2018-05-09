package eidolons.game.module.herocreator.logic;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.utils.PerkGenerator.PERK_GROUP;
import eidolons.system.utils.PerkGenerator.PERK_PARAM;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 *
 * Class Ranks
 * Class reqs
 *
 *
 */
public class HeroClassMaster {
    public static final String CLASSES_TIER_= "CLASSES_TIER_";

    public static List<ObjType> getAvailablePerks(Unit hero, int tier,
                                                  HeroClass c1, HeroClass c2) {
         List<ObjType> list = new ArrayList<>();
        PERK_GROUP group1 =
         new EnumMaster<PERK_GROUP>().retrieveEnumConst(PERK_GROUP.class,
          c1.getProperty(PROPS.PERK_GROUP));
        PERK_GROUP group2 =
         new EnumMaster<PERK_GROUP>().retrieveEnumConst(PERK_GROUP.class,
          c2.getProperty(PROPS.PERK_GROUP));

        List<PERK_PARAM> paramPerks =
         new ArrayList<>(Arrays.asList( group1.getParamPerks())) ;
        paramPerks.addAll(Arrays.asList(group2.getParamPerks()));

         for (ObjType type: DataManager.getTypes(DC_TYPE.PERKS)){
             for (PERK_PARAM sub: paramPerks){
                 if (type.getProperty(PROPS.PERK_PARAM).
                  equalsIgnoreCase(sub.toString())) {
                     list.add(type);
                     continue;
                 }
             }
         }
         if (c1.getType()==c2.getType()){
             //addSpecial
         }

        return list;
    }
    public static void classRankAcquired(Unit hero, int tier) {

//        String notChosenPerks = hero.getProperty(PROPS.PENDING_PERKS);

        String chosenPerks = hero.getProperty(PROPS.PERKS);
       for (String sub:  StringMaster.openContainer(
        hero.getProperty(ContentValsManager.getPROP("CLASSES_TIER_" + tier)))) {

        }

    }

    public static List<DC_FeatObj> getClasses(Unit hero, int tier) {
        ArrayList<DC_FeatObj> list = new ArrayList<>(hero.getClasses());
        list.removeIf(c -> c.getTier() != tier);
        return list;
    }

    public static List<Perk> getPerks(Unit hero, int tier) {
        ArrayList<Perk> list = new ArrayList<>(hero.getPerks());
        list.removeIf(c -> c.getTier() != tier);
        return list;
    }

    public static int getMaxClassSlots(int tier) {
        return 5-tier;
    }

    public static List<ObjType> getAvailableClasses(Unit hero, int tier) {
        List<ObjType> list = new ArrayList<>(DataManager.getTypes(DC_TYPE.CLASSES));
        //check if branching is OK
        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier);

        return list;
    }
}
