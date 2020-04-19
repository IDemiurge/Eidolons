package eidolons.system.content;

import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;

public class UtilUnitGrouper {

    public static void groupUnits() {

        for (UnitEnums.UNIT_GROUP value : UnitEnums.UNIT_GROUP.values()) {

            List<ObjType> subGroup = DataManager.getTypesSubGroup(DC_TYPE.UNITS, value.toString());
            for (ObjType objType : subGroup) {

                switch (value) {
                    case CULT_DARK:
                    case CULT_CHAOS:
                    case CULT_DEATH:
                        objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Occult");
                    case CULT_CERBERUS:
                        objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Cerberus");
                        break;
                    case CULT_CONGREGATION :
                        objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Occult");
                        objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Congregation");
                        break;
                    case MISTSPAWN:
                        objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Occult");
                        objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Mistborn");
                        break;

                    case CONSTRUCTS:

                        switch (objType.getProperty("deity")) {
                            case "Witch King":
                            case "Void Lord":
                                break;
                            default:
                                objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Neutral");
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Constructs");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Serpents");
                        }
                        break;
                    case ANIMALS:
                        switch (objType.getProperty("deity")) {
                            case "Night Lord":
                                objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Neutral");
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Animals");
                                break;
                            case "World Serpent":
                                objType.setProperty(G_PROPS.GROUP, "Monsters");
                                objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Monsters");
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Serpents");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Serpents");
                                break;

                        }
                        break;

                    case DUNGEON:

                        objType.setProperty(G_PROPS.GROUP, "Monsters");
                        objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Monsters");
                        objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Dungeon");
                        break;

                    case PRISONERS:

                        objType.setProperty(G_PROPS.GROUP, "Humans");
                        objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Humans");
                        objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Wretched");
                        break;
                    case UNDEAD:
                        switch (objType.getProperty("deity")) {
                            case "Wraith God":
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Wraith");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Undead Wraith");
                                break;
                            case "Crimson Queen":
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Crimson");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Undead Crimson");
                                break;
                            case "Lord of Decay":
                            case "Death Gorger":
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Plague");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Undead Plague");
                                break;
                            default:
                                objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Restless");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Undead Crimson");
                        }

                        break;
                    case TUTORIAL:

                        if (objType.getName().contains("Sleeper")) {
                            objType.setProperty(G_PROPS.GROUP, "Nether");
                            objType.setProperty(G_PROPS.UNIT_GROUP, "Nether");
                            objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Nether");
                            objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Sleepers");
                        } else {
                            if (
                                    objType.getProperty("deity").equalsIgnoreCase("Nether Gods")) {
                                objType.setProperty(G_PROPS.GROUP, "Nether");
                                objType.setProperty(G_PROPS.UNIT_GROUP, "Nether");
                                objType.setProperty(G_PROPS.ENCOUNTER_GROUP, "Nether");
                                if (objType.getName().contains("Hollow")) {
                                    objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Hollows");
                                } else {
                                    objType.setProperty(G_PROPS.ENCOUNTER_SUBGROUP, "Fiends");
                                }
                            }

                        }
                }
            }
        }

    }
}

