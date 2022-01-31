package eidolons.netherflame.feature.reflectItem;

import eidolons.content.PARAMS;
import eidolons.entity.item.handlers.ItemMaster;
import eidolons.netherflame.feature.reflectItem.data.WeaponMasteryData;
import eidolons.netherflame.feature.reflectItem.data.MaterialData;
import main.content.DC_TYPE;
import main.content.enums.entity.Item2Enums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.SkillEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;

import java.util.List;

import static eidolons.netherflame.feature.reflectItem.data.Inv2Enums.*;
import static main.content.enums.entity.ItemEnums.*;

/**
 * We'd want player to do some ALCHEMY with items - but simple enough
 * <p>
 * On Match UI - what can we swap between eidolons and what can we combine within a single eidolon? ONLY during loot!
 * <p>
 * If we have Prism-Slot: 2 sets, armor, 3 trinket types - combine sigils vs prisms...
 * <p>
 * Artifact effect - magicMod (additional imbue %?), rarity? , synergy for weapon class / size /... ? from weapon set...
 * for: > same weapon type, group, class and size - all add 10 and +5 for each next synergy
 */
public class ItemReflector {

    public static class ArtifactSet {
        WEAPON_GROUP[] groups;
        WEAPON_SIZE[] sizes;
        WEAPON_TYPE[] types;
        WEAPON_CLASS[] classes;

        public ArtifactSet(ObjType... objTypes) {
            groups = new WEAPON_GROUP[types.length];
            classes = new WEAPON_CLASS[types.length];
            types = new WEAPON_TYPE[types.length];
            sizes = new WEAPON_SIZE[types.length];
            int i = 0;
            for (ObjType type : objTypes) {
                classes[i] = new EnumMaster<WEAPON_CLASS>().retrieveEnumConst(WEAPON_CLASS.class, type.getProperty(G_PROPS.WEAPON_CLASS));
                sizes[i] = new EnumMaster<WEAPON_SIZE>().retrieveEnumConst(WEAPON_SIZE.class, type.getProperty(G_PROPS.WEAPON_SIZE));
                classes[i] = new EnumMaster<WEAPON_CLASS>().retrieveEnumConst(WEAPON_CLASS.class, type.getProperty(G_PROPS.WEAPON_CLASS));
                groups[i++] = new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(WEAPON_GROUP.class, type.getProperty(G_PROPS.WEAPON_GROUP));
            }
        }
    }

    //support lite unit-version
    public static ObjType createReflectedWeaponType(MaterialData materialData, int qualityMod,
                                                    int magicMod, int rarity, SigilType sigilType,
                                                    EnergyGrade energy, WeaponMasteryData weaponMasteryData,
                                                    ArtifactSet artifactSet) {


        WEAPON_GROUP group = getWeaponGroup(sigilType, weaponMasteryData);
        ObjType baseReflectedWeapon = getBaseWeapon(group, rarity);
        Item2Enums.IMaterial material = getMaterial(baseReflectedWeapon, materialData);
        ObjType type = new ObjType(baseReflectedWeapon);
        // if (artifactSet != null)
        //     float synergyMod = calculateSynergy(type, artifactSet);

        // type.setValue(PARAMS.QUALITY_MOD, qualityMod);
        // type.setValue(, energy);

        String itemName = generateName(type);
        type.setName(itemName);
        return type;
    }

    private static Item2Enums.IMaterial getMaterial(ObjType base, MaterialData materialData) {
        ITEM_MATERIAL_GROUP group = ItemMaster.getMaterialGroup(base);
        int tier = materialData.getTier();
        Boolean lite_heavy_balanced = materialData.getLiteHeavyBalanced();
        return getMaterial(group, tier, lite_heavy_balanced);
    }

    private static Item2Enums.IMaterial getMaterial(ITEM_MATERIAL_GROUP group, int tier, Boolean lite_heavy_balanced) {
        Item2Enums.IMaterial[] materialConsts = getMaterialConsts(group);

        int offset = 2;
        if (Boolean.TRUE == lite_heavy_balanced) offset = 0;
        if (Boolean.FALSE == lite_heavy_balanced) offset = 1;
        int index = tier * 3 + offset;
        return materialConsts[index];
    }

    private static Item2Enums.IMaterial[] getMaterialConsts(ITEM_MATERIAL_GROUP group) {
        switch (group) {
            case METAL:
                return Item2Enums.Metal.values();
            case WOOD:
                return Item2Enums.Wood.values();
            case BONE:
                return Item2Enums.Bone.values();
            // case LEATHER:
            //     return Item2Enums.Metal.values();
            // case CLOTH:
            //     return Item2Enums.Metal.values();
            // case STONE:
            //     return Item2Enums.Metal.values();
        }
        return new Item2Enums.IMaterial[0];
    }

    private static ObjType getBaseWeapon(WEAPON_GROUP group, int rarity) {
        List<ObjType> typesGroup = DataManager.getTypesGroup(DC_TYPE.WEAPONS, group.name());
        typesGroup.removeIf(type -> type.isGenerated());
        SortMaster.getEntitySorterByExpression(e -> Math.abs(rarity - e.getIntParam(PARAMS.ITEM_RARITY)));
        return typesGroup.get(0);
    }

    //blunt - axe - blade - pollarm - ranged - other
    public static final WEAPON_GROUP[][][] sigilGroups = {
            {{WEAPON_GROUP.CLUBS, WEAPON_GROUP.MACES, WEAPON_GROUP.HAMMERS}, {WEAPON_GROUP.POLLAXES, WEAPON_GROUP.AXES},}, //Warrior
            //TODO
            // { WEAPON_GROUP.CLUBS , WEAPON_GROUP.MACES , WEAPON_GROUP.HAMMERS , WEAPON_GROUP.POLLAXES , WEAPON_GROUP.AXES , }, //Assassin
            // { WEAPON_GROUP.CLUBS , WEAPON_GROUP.MACES , WEAPON_GROUP.HAMMERS , WEAPON_GROUP.POLLAXES , WEAPON_GROUP.AXES , }, //Knight
            // { WEAPON_GROUP.CLUBS , WEAPON_GROUP.MACES , WEAPON_GROUP.HAMMERS , WEAPON_GROUP.POLLAXES , WEAPON_GROUP.AXES , }, //Archer
            // { WEAPON_GROUP.CLUBS , WEAPON_GROUP.MACES , WEAPON_GROUP.HAMMERS , WEAPON_GROUP.POLLAXES , WEAPON_GROUP.AXES , }, //Battle_Mage
            // { WEAPON_GROUP.CLUBS , WEAPON_GROUP.MACES , WEAPON_GROUP.HAMMERS , WEAPON_GROUP.POLLAXES , WEAPON_GROUP.AXES , }, //Trickster
    };

    private static WEAPON_GROUP getWeaponGroup(SigilType sigilType, WeaponMasteryData weaponMasteryData) {
        int sigilIndex = EnumMaster.getEnumConstIndex(SigilType.class, sigilType);
        WEAPON_GROUP[] options = getSubgroup(sigilGroups[sigilIndex], weaponMasteryData);
        // weaponMasteryData.getSpecializations();
        // return chooseGroup(options, metadata); //TODO based on ... Artifact? Preference? Rarity? Specialization!
        return null;
    }

    private static WEAPON_GROUP[] getSubgroup(WEAPON_GROUP[][] sigilGroup, WeaponMasteryData weaponMasteryData) {
        int n = 0;
        while (n < 5) {
            SkillEnums.MASTERY mastery = weaponMasteryData.getGreatest(n++);
            int index = getIndex(mastery);
            WEAPON_GROUP[] subgroup = sigilGroup[index];
            if (subgroup.length != 0) {
                return subgroup;
            }
        }
        return new WEAPON_GROUP[0];
    }

    private static int getIndex(SkillEnums.MASTERY mastery) {
        switch (mastery) {
            case BLUNT_MASTERY:
                return 0;
            case AXE_MASTERY:
                return 1;
            case BLADE_MASTERY:
                return 2;
            case POLEARM_MASTERY:
                return 3;
            case MARKSMANSHIP_MASTERY:
                return 4;
            case UNARMED_MASTERY:
                return 5;
        }
        return -1;
    }

    private static String generateName(ObjType type) {
        return null;
    }

    private static float calculateSynergy(ObjType type, ArtifactSet artifactSet) {
        int synergies =
                compare(artifactSet, new ArtifactSet(type));
        int mod = 10;
        int total = 0;
        for (int i = 0; i < synergies; i++) {
            total += mod;
            mod += 5;
        }
        return total;
    }

    private static int compare(ArtifactSet artifactSet, ArtifactSet artifactSet1) {
        // artifactSet.sizes
        int total = 0;
        return total;
    }
}
