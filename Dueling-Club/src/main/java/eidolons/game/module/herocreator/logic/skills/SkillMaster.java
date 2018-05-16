package eidolons.game.module.herocreator.logic.skills;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.HeroManager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/6/2018.
 * <p>
 * :: Mastery ranks
 * :: Slots
 * :: “Geometry”
 * :: RequirementMaster
 */
public class SkillMaster {
    public static final String MASTERY_RANKS = "MASTERY_RANKS_";

    public static void masteryIncreased(Unit hero, PARAMETER mastery) {
        //do we assume that it is increased by 1 only? we can perhaps
        int newValue = hero.getIntParam(mastery);
        if (newValue % 5 != 0)
            return;
        int tier = (newValue-1) / 10;
        addMasteryRank(hero, mastery, tier);

    }

    private static void addMasteryRank(Unit hero, PARAMETER mastery, int tier) {
        PROPERTY prop = getMasteryRankProp(tier);
        if (StringMaster.openContainer(hero.getProperty(prop)).size() >= getSlotsForTier(tier))
            return;
        hero.addProperty(prop, mastery.getName(), false);
        hero.getType().addProperty(prop, mastery.getName(), false);
    }

    public static void initMasteryRanks(Unit hero) {
        if (hero.checkProperty(PROPS.MASTERY_RANKS_1))
            return;
        Map<PARAMS, Integer> map = new XLinkedMap<>();
        for (PARAMS sub : DC_ContentValsManager.getMasteryParams()) {
            Integer val = hero.getIntParam(sub);
            if (val > 0)
                map.put(sub, val);
        }
        Map<PARAMS, Integer> sortedMap = map;
        while (true) {
            sortedMap = new MapMaster<PARAMS, Integer>().getSortedMap(sortedMap,
             param -> map.get(param));
            PARAMS top = sortedMap.keySet().iterator().next();
            if (map.get(top) < 5)
                break;
            int tier = map.get(top) / 10;
            MapMaster.addToIntegerMap(map, top, -5);
            addMasteryRank(hero, top, tier);

        }

    }


    public static List<DC_FeatObj> getSkillsOfTier(Unit hero, int tier) {
        List<DC_FeatObj> list = new ArrayList<>(hero.getSkills());
        list.removeIf(skill -> skill.getTier() != tier);
        return list;
    }

    public static List<ObjType> getAvailableSkills(List<ObjType> list, Unit hero, int tier) {
        list.removeIf(type -> hero.getGame().getRequirementsManager().check(hero, type) != null);
        return list;
    }

    public static List<ObjType> getAvailableSkills(Unit hero, int tier) {
        List<ObjType> list = new ArrayList<>(DataManager.getTypes(DC_TYPE.SKILLS));
        //check if branching is OK
        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier
         || hero.getGame().getRequirementsManager().check(hero, type) != null
        );

        return list;
    }
    public static List<ObjType> getAllSkills(Unit hero, int tier,
                                             MASTERY mastery1, MASTERY mastery2) {
        List<ObjType> list = new ArrayList<>();
        list.addAll(DataManager.getTypesSubGroup(DC_TYPE.SKILLS,
         StringMaster.getWellFormattedString(mastery1.toString())));
        list.addAll(DataManager.getTypesSubGroup(DC_TYPE.SKILLS,
         StringMaster.getWellFormattedString(mastery2.toString())));

        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier);

        return list;
    }

    public static int getSlotsForTier(int tier) {
        return 7 - tier;
    }

    public static int getLinkSlotsPerTier(int tier) {
        return 6 - tier;
    }

    public static String getReqReasonForSkill(Unit hero, ObjType type) {
        String reason = hero.getGame().getRequirementsManager().check(hero, type);
        return reason;
    }

    public static PROPERTY getMasteryRankProp(int tier) {
        tier++;
        return ContentValsManager.getPROP(MASTERY_RANKS + tier);
    }

    public static DC_FeatObj createFeatObj(ObjType featType, Ref ref) {
        switch ((DC_TYPE) featType.getOBJ_TYPE_ENUM()) {
            case PERKS:
                return new Perk(featType, (Unit) ref.getSourceObj());
            case CLASSES:
                return new HeroClass(featType, (Unit) ref.getSourceObj());
        }
        return new DC_FeatObj(featType, ref);
    }

    public static void newSkill(Unit hero, ObjType arg) {
        newFeat(PROPS.SKILLS, hero, arg);
    }
    public static void newPerk(Unit hero, ObjType arg) {
        newFeat(PROPS.PERKS, hero, arg);
    }
    public static void newClass(Unit hero, ObjType arg) {
        newFeat(PROPS.CLASSES, hero, arg);
    }

    public static void newFeat(PROPERTY prop, Unit hero, ObjType arg) {
        DC_FeatObj featObj = createFeatObj(arg, hero.getRef());
        hero.addProperty(prop, arg.getName(), false);
        hero.getType().addProperty(prop, arg.getName(), false);
        hero.modifyParameter(PARAMS.XP,
         -HeroManager.getIntCost(arg, hero));
        DequeImpl<? extends DC_FeatObj> container = hero.getSkills();

        if (arg.getOBJ_TYPE_ENUM() == DC_TYPE.CLASSES) {
            container = hero.getClasses();
        } else if (arg.getOBJ_TYPE_ENUM() == DC_TYPE.PERKS) {
            container = hero.getPerks();
        }
        container.addCast(featObj);
    }
}
