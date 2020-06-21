package eidolons.game.module.herocreator.logic.skills;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.HeroManager;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.enums.entity.SkillEnums.SKILL_GROUP;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.content.enums.entity.SkillEnums.MASTERY.*;

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
    public static final String DUMMY_SKILL = "Skill Slot";
    private static DC_FeatObj dummy;

    public static final DC_FeatObj getEmptySkill() {
        if (dummy == null) {
            dummy = new DC_FeatObj(new ObjType(SkillMaster.DUMMY_SKILL), new Ref(Eidolons.getMainHero()));
        }
        return dummy;
    }

    public static void masteryIncreased(Unit hero, PARAMETER mastery) {
        //do we assume that it is increased by 1 only? we can perhaps
        int newValue = hero.getIntParam(mastery);
        if (newValue % 5 != 0)
            return;
        int tier = (newValue - 1) / 10;
        addMasteryRank(hero, mastery, tier);

    }

    private static void addMasteryRank(Unit hero, PARAMETER mastery, int tier) {
        PROPERTY prop = getMasteryRankProp(tier);
        if (ContainerUtils.openContainer(hero.getProperty(prop)).size() >= getSlotsForTier(tier))
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
        List<DC_FeatObj> list = new ArrayList<>();
        String prop = hero.getProperty(getTierProp(PROPS.SKILLS, tier));
//        if (isSkillSlotsMixed()){
        prop = hero.getProperty(PROPS.SKILLS);
//        }
        int i = 0;
        ListMaster.fillWithNullElements(list, SkillMaster.getSlotsForTier(tier));
        for (String substring : ContainerUtils.openContainer(prop)) {
            String[] parts = substring.split("=");
            String name = parts.length > 1 ? parts[1] : substring;
            DC_FeatObj skill = hero.getFeat(DataManager.getType(name, DC_TYPE.SKILLS));
            if (skill == null)
                continue;
            if (skill.getIntParam("circle") != tier)
                continue;
            int slot = parts.length > 1 ? Integer.valueOf(parts[0]) : i++;
            list.add(slot, skill);
        }
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
                StringMaster.format(mastery1.toString())));
        list.addAll(DataManager.getTypesSubGroup(DC_TYPE.SKILLS,
                StringMaster.format(mastery2.toString())));

        list.removeIf(type -> type.getIntParam(PARAMS.CIRCLE) != tier);
        list.removeIf(type -> isSkillProhibited(type, hero));

        return list;
    }

    private static boolean isSkillProhibited(ObjType type, Unit hero) {
        for (DC_FeatObj skill : hero.getSkills()) {
            if (skill.getType().equals(type)) {
                return true;
            }

        }
        return false;
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

    private static PROPERTY getTierProp(PROPERTY prop, int tier) {
        tier++;
        return ContentValsManager.getPROP(prop.name() + "_TIER_" + tier);
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

    public static void newSkill(Unit hero, ObjType arg, int tier, int slot) {
        newFeat(PROPS.SKILLS, hero, arg, tier, slot);
    }

    public static void newPerk(Unit hero, ObjType arg, int tier, int slot) {
        newFeat(PROPS.PERKS, hero, arg, tier, slot);
    }

    public static void newClass(Unit hero, ObjType arg, int tier, int slot) {
        newFeat(PROPS.CLASSES, hero, arg, tier, slot);
    }

    public static void newFeat(PROPERTY prop, Unit hero, ObjType arg, int tier, int slot) {
        DC_FeatObj featObj = createFeatObj(arg, hero.getRef());

        //max rank req? Well, it is kind of determined by slot number ... but then, how does the rank scale?
        //        featObj.getRank()
        //        arg.getVar(PARAMS.CIRCLE);
        PROPERTY tierProp = getTierProp(prop, tier);

        hero.addProperty(tierProp, getSlotString(slot, arg.getName()), false);
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

    private static String getSlotString(int slot, String name) {
        return slot + "=" + name;
    }


    public static List<MASTERY> getUnlockedMasteries_(Entity entity) {
        List<PARAMETER> list = getUnlockedMasteries(entity);
        return list.stream().map(parameter -> new EnumMaster<MASTERY>()
                .retrieveEnumConst(MASTERY.class, parameter.getName())).collect(Collectors.toList());
    }

    public static List<PARAMETER> getUnlockedMasteries(Entity entity) {
        List<PARAMETER> list = new ArrayList<>();
        for (PARAMS p : DC_ContentValsManager.getMasteryParams()) {
            if (isMasteryUnlocked(entity, p)) {
                list.add(p);
            }
        }
        return list;
    }

    public static boolean isMasteryUnlocked(Entity entity, PARAMETER p) {
        return entity.getIntParam(p, true) > 0;
    }

    public static MASTERY[] getMasteriesFromSkillGroup(SKILL_GROUP group) {
        switch (group) {
            case BODY_MIND:
                return new MASTERY[]{
                        ATHLETICS_MASTERY,
                        MOBILITY_MASTERY,
                        MEDITATION_MASTERY,
                        DISCIPLINE_MASTERY,

                };
            case SPELLCASTING:
                return new MASTERY[]{
                        SPELLCRAFT_MASTERY,
                        WIZARDRY_MASTERY,
                        DIVINATION_MASTERY,

                };
            case WEAPONS:
                return new MASTERY[]{
                        BLADE_MASTERY,
                        AXE_MASTERY,
                        BLUNT_MASTERY,
                        POLEARM_MASTERY,
                };
            case OFFENSE:
                return new MASTERY[]{
                        TWO_HANDED_MASTERY,
                        DUAL_WIELDING_MASTERY,
                        MARKSMANSHIP_MASTERY,
                        UNARMED_MASTERY,
                };
            case DEFENSE:
                return new MASTERY[]{
                        DEFENSE_MASTERY,
                        SHIELD_MASTERY,
                        STEALTH_MASTERY,
                        DETECTION_MASTERY,

                };
            case COMMAND:
                return new MASTERY[]{
                        LEADERSHIP_MASTERY,
                        TACTICS_MASTERY,
                        WARCRY_MASTERY
                };
            case CRAFT:
                return new MASTERY[]{
                        ARMORER_MASTERY,
                        ITEM_MASTERY,

                };
            case PRIME_ARTS:
                return new MASTERY[]{
                        FIRE_MASTERY,
                        AIR_MASTERY,
                        WATER_MASTERY,
                };
            case ARCANE_ARTS:
                return new MASTERY[]{
                        CONJURATION_MASTERY,
                        SORCERY_MASTERY,
                        ENCHANTMENT_MASTERY,

                };
            case LIFE_ARTS:
                return new MASTERY[]{
                        EARTH_MASTERY,
                        SAVAGE_MASTERY,
                        SYLVAN_MASTERY,
                };
            case DARK_ARTS:
                return new MASTERY[]{
                        PSYCHIC_MASTERY,
                        SHADOW_MASTERY,
                        WITCHERY_MASTERY,

                };
            case CHAOS_ARTS:
                return new MASTERY[]{
                        WARP_MASTERY,
                        DESTRUCTION_MASTERY,
                        DEMONOLOGY_MASTERY,

                };
            case HOLY_ARTS:
                return new MASTERY[]{
                        REDEMPTION_MASTERY,
                        BENEDICTION_MASTERY,
                        CELESTIAL_MASTERY,
                };
            case DEATH_ARTS:
                return new MASTERY[]{
                        AFFLICTION_MASTERY,
                        BLOOD_MAGIC_MASTERY,
                        NECROMANCY_MASTERY,
                };
        }
        return null;
    }

    public static boolean isMasteryAvailable(PARAMETER p, Unit hero) {
        //        ValuePageManager.getGenericValuesForInfoPages()


        return true;
    }

    public static String getSkillImgPath(Entity left) {
        String path = "main/skills/gen/64/" + left.getName() + ".png";
        if (TextureCache.isImage(path))
            return path;
        GdxImageMaster.size(left.getImagePath(), 64, true);
        return GdxImageMaster.getSizedImagePath(left.getImagePath(), 64);
    }

    public static boolean isDataAnOpenSlot(Triple<DC_FeatObj, MASTERY, MASTERY> data) {
        if (data == null) {
            return false;
        }
        return data.getLeft().getName().equalsIgnoreCase(SkillMaster.DUMMY_SKILL);
    }
}
