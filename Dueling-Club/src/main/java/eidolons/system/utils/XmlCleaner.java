package eidolons.system.utils;

import eidolons.content.PROPS;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.SpellEnums;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlCleaner {

    private static List<DC_TYPE> cleaned = new ArrayList<>();

    public static final void cleanTypesXml(DC_TYPE TYPE) {
//        DC_TYPE.values()

        List<ObjType> types = new ArrayList<>(DataManager.getTypes(TYPE));

        for (ObjType type : types) {
            if (isRemoveType(type, TYPE))
                DataManager.removeType(type);

        }
        //save separately? and load selectively from cleaned 
    }

    private static boolean isRemoveType(ObjType type, OBJ_TYPE TYPE) {
        if (!cleaned.contains(TYPE)) {
            return false;
        }
        switch (((DC_TYPE) TYPE)) {
            case UNITS:
                return isRemoveUnit(type);
            case SPELLS:
                return isRemoveSPELLS(type);
            case CHARS:
                return isRemoveCHARS(type);
            case BF_OBJ:
                return isRemoveBF_OBJ(type);
            case SKILLS:
                return isRemoveSKILLS(type);
            case FLOORS:
                return isRemoveDUNGEONS(type);
            case PARTY:
                return isRemovePARTY(type);
            case ENCOUNTERS:
                return isRemoveEncounter(type);
            case PLACES:
            case DIALOGUE:
                return true;
            case ACTORS:
            case ALL:
            case META:
                break;
        }
        /**
         * circle
         * groups
         * art
         * ALL chars! 
         *
         */
        return false;
    }

    private static boolean isRemoveSPELLS(ObjType type) {
        if (type.getIntParam("circle") >= 3) {
            return true;
        }
        SpellEnums.SPELL_GROUP group =
                new EnumMaster<SpellEnums.SPELL_GROUP>().retrieveEnumConst(SpellEnums.SPELL_GROUP.class,
                        type.getProperty(G_PROPS.SPELL_GROUP));
        switch (group) {
            case EARTH:
            case ENCHANTMENT:
            case TRANSMUTATION:
            case VOID:
            case BLOOD_MAGIC:
            case WARP:
            case DEMONOLOGY:
            case DESTRUCTION:
            case REDEMPTION:
            case SYLVAN:
            case ELEMENTAL:
                return true;
        }
        return false;
    }

    private static boolean isRemoveCHARS(ObjType type) {
        if ("Count Ledwraith;Grimbart;Eidas Adeilar v3;   Elinda Carrow;Gnauri Viragost;Milthir;Ilureth Atalun-Ree;Raina Ardren;".contains(
                type.getName())) {
            return false;
        }
        return !type.getGroup().equalsIgnoreCase("igg");
    }

    private static boolean isRemoveUnit(ObjType type) {
        return type.getWorkspaceGroup() == MetaEnums.WORKSPACE_GROUP.IGG_CONTENT;
    }

    public static final PROPERTY[] encounterProperties = {
            PROPS.PRESET_GROUP,
            PROPS.EXTENDED_PRESET_GROUP,
            PROPS.SHRUNK_PRESET_GROUP,
            PROPS.FILLER_TYPES};

    private static boolean isRemoveEncounter(ObjType type) {
        for (PROPERTY encounterProperty : encounterProperties) {
        for (String substring : ContainerUtils.openContainer(
                type.getProperty(encounterProperty).trim())) {
            if (!DataManager.isTypeName(substring)) {
                return true;
            }
        }
        }

        return false;
    }

    private static boolean isRemoveBF_OBJ(ObjType type) {
        return false;
    }

    private static boolean isRemoveSKILLS(ObjType type) {
        if (type.getIntParam("circle") >= 2) {
            return true;
        }
        SkillEnums.SKILL_GROUP group =
                new EnumMaster<SkillEnums.SKILL_GROUP>().retrieveEnumConst(SkillEnums.SKILL_GROUP.class, type.getProperty(G_PROPS.SKILL_GROUP));
        if (group == null) {
            return true;
        }
        switch (group) {
            case SPELLCASTING:
            case CHAOS_ARTS:
            case ARCANE_ARTS:
            case COMMAND:
            case CRAFT:
                return true;

        }
        return false;
    }

    private static boolean isRemoveDUNGEONS(ObjType type) {
        return true;
    }


    private static boolean isRemovePARTY(ObjType type) {
        return !type.getName().equalsIgnoreCase("chained");
    }

    public static void setCleanReadTypes(DC_TYPE... types) {
        cleaned.addAll(Arrays.asList(types));
//        clean = true;
        XML_Reader.setTypeChecker(t -> !isRemoveType(t, t.getOBJ_TYPE_ENUM()));
    }
}
