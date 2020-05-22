package main.utilities.xml;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.*;
import main.elements.conditions.*;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.gui.components.controls.ModelManager;
import main.system.ExceptionMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.graphics.GuiManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.auxiliary.log.LogMaster.log;

public class XML_Transformer {

    private static final int PROGRESSION_WEIGHT_CONST = 3;
    private static boolean main;

    public enum SPECIAL_ROUTINES {
        SPEACIAL_ROUTINE, CLEAN_UP, FILTER_HEROES, REMOVE_VALUE, RENAME_VALUE, RENAME_TYPE,
        RELEASE_CLEANUP // remove dev values
    }

    public static void main(String[] args) {
        //TODO planned routines via ARGS!
        for (String arg : args) {
            SPECIAL_ROUTINES special_routines = SPECIAL_ROUTINES.valueOf(arg);
            // doRoutine(special_routines, argList);
        }
        main = true;
        GuiManager.init();
        if (DialogMaster.confirm("Overwrite backup?")) {
            backUp();
        }
        showTransformDialog();
    }

    public static void showTransformDialog() {
        int i = JOptionPane.showOptionDialog(null, "", "", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, SPECIAL_ROUTINES.values(),
                SPECIAL_ROUTINES.CLEAN_UP);
        switch (SPECIAL_ROUTINES.values()[i]) {
            case CLEAN_UP:
                cleanUp();
                break;
            case RENAME_VALUE:
                renameValue();
                break;

        }
    }

    private static void backUp() {
        ModelManager.fullBackUp(); // new reserve?
    }

    private static void transformStringsToNormal() {
        // TODO Auto-generated method stub

    }

    public static void renameValue() {
        String valueName = JOptionPane.showInputDialog(null, "Enter value to be renamed ", null);
        if (valueName == null) {
            return;
        }
        String newName = JOptionPane.showInputDialog(null, "Enter new name for " +
                valueName, null);
        renameValue(valueName, newName);
    }

    public static void renameValue(String name, String newName) {
        name = StringMaster.getWellFormattedString(name);
        newName = StringMaster.getWellFormattedString(newName);
        log(1, "Renaming value " +
                name + " to " + newName);
        for (XML_File xmlFile : getXmlFiles()) {
            String contents = xmlFile.getContents();
            if (!contents.contains(XML_Writer.closeXML((name)))) {
                continue;
            }
            log(1, xmlFile + "- Current length: " + xmlFile.getContents().length());
            String newContents = contents;

            newContents = newContents.replace(XML_Writer.closeXML((name)),
                    XML_Writer.closeXML((newName)));
            newContents = newContents.replace(XML_Writer.openXML((name)),
                    XML_Writer.openXML((newName)));

            //try with abilities? formulas?
            if (xmlFile.getType() == DC_TYPE.ABILS) {
                newContents = newContents.replace(name, newName);
                newContents = newContents.replace(name.toLowerCase(), newName);
                newContents = newContents.replace(name.toUpperCase(), newName);
                newContents = newContents.replace(StringMaster.getWellFormattedString(name), newName);
            }
            xmlFile.setContents(newContents);
            log(1, xmlFile + "-New length: " + xmlFile.getContents().length());
            FileManager.write(newContents, xmlFile.getFile().getPath());

        }
    }

    public static void transformBuffTypes() {
        for (ObjType t : DataManager.getTypes(DC_TYPE.BUFFS)) {
            if (StringMaster.contains(t.getProperty(G_PROPS.BUFF_TYPE), "buff")) {
                t.setProperty(G_PROPS.BUFF_TYPE, StringMaster
                        .getWellFormattedString(GenericEnums.BUFF_TYPE.SPELL.name()));
            }
        }
    }


    public static void filterHeroes() {
        Conditions c = new Conditions();
        c.add(new NotCondition(new NumericCondition(StringMaster.getValueRef(KEYS.SOURCE,
                PARAMS.HERO_LEVEL), "1", true)));
        // c.add(new StringComparison(StringMaster
        // .getValueRef(KEYS.SOURCE, G_PROPS.RACE), "Human", true));
        c.add(new NotCondition(new StringComparison(StringMaster.getValueRef(KEYS.SOURCE,
                G_PROPS.GROUP), "Background", true)));
        filterTypes(c, DC_TYPE.CHARS);
    }

    public static void filterTypes(Condition c, OBJ_TYPE... TYPES) {
        for (OBJ_TYPE TYPE : TYPES) {
            for (ObjType type : DataManager.getTypes(TYPE)) {
                if (c.preCheck(type.getRef())) {
                    DataManager.removeType(type.getName(), TYPE.getName());
                }
            }
        }
    }

    public static void adjustProgressionToWeightForm(ObjType type, boolean attr) {
        String finalString = "";
        String attrString = attr ? type.getProperty(PROPS.ATTRIBUTE_PROGRESSION) : type
                .getProperty(PROPS.MASTERY_PROGRESSION);
        if (attrString.isEmpty()) {
            return;
        }
        if (attrString.contains("(")) {
            return;
        }

        List<String> container = ContainerUtils.openContainer(attrString);
        int i = 0;
        Map<String, Integer> map = new HashMap<>();
        for (String s : container) {
            int weight = container.size() - i + PROGRESSION_WEIGHT_CONST;
            map.get(s);
            if (map.get(s) != null) {
                weight += map.get(s);
            }
            map.put(s, weight);
            i++;
        }

        for (String s : map.keySet()) {
            finalString += s + StringMaster.wrapInParenthesis("" + map.get(s))
                    + StringMaster.CONTAINER_SEPARATOR;
        }
        if (attr) {
            type.setProperty(PROPS.ATTRIBUTE_PROGRESSION, finalString);
        } else {
            type.setProperty(PROPS.MASTERY_PROGRESSION, finalString);
        }
    }

    public static void cleanUp() { // ++ remove *empty* nodes
        // backUp();
        // // filterHeroes();
        // transformBuffTypes();
        // transformResistances();

        // int ok = JOptionPane
        // .showConfirmDialog(null, "Create a backup data reserve?");
        // if (ok == JOptionPane.YES_OPTION)
        // XML_Writer.createBackUpReserve();

        new DC_ContentValsManager().init();

        log(1, "Cleaning up XML files... ");
        DequeImpl<XML_File> xmlFiles = getXmlFiles();
        for (XML_File file : xmlFiles) {
            // log(1, file + " current length: " + file.getContents().length());
            // log(1, "Removing alien nodes from " + file);
            // for (VALUE v : ContentValsManager.getValueList()) {
            //     if (!ContentValsManager.isValueForOBJ_TYPE(file.getType(), v)) {
            //         removeValue(v, file, false, false);
            //     }
            // }

            removeEmptyNodes(file);
            // log(1, "New length: " + file.getContents().length());
            try {
                XML_Writer.write(file);
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }

        }
        log(1, "Cleaning up XML files done. ");
    }

    private static void removeEmptyNodes(XML_File file) {
        Document doc = XML_Converter.getDoc(file.getContents());
        if (doc == null) {
            return;
        }
        if (file.getType().isTreeEditType()) {
            return ; //TODO different cleanup here
        }
        log(1, "Removing empty nodes from " + file);
        log(1, file + " current length: " + file.getContents().length());
        for (Node groupNode : XmlNodeMaster.getNodeList(doc.getFirstChild())) {
            for (Node typeNode : XmlNodeMaster.getNodeList(groupNode)) {
                for (Node valueGroupNode : XmlNodeMaster.getNodeList(typeNode)) {
                    for (Node valueNode : XmlNodeMaster.getNodeList(valueGroupNode)) {
                        //TODO remove default values too? btw, we could do even better than default value -
                        // we could have default type with all those vals set already.
                        VALUE value = DC_ContentValsManager.getValue(valueNode.getNodeName());
                        if (value == null) {
                            valueGroupNode.removeChild(valueNode);
                            continue;
                        }
                        String defaultValue = value.getDefaultValue();
                        if (valueNode.getTextContent().trim().equalsIgnoreCase(defaultValue)) {
                            valueGroupNode.removeChild(valueNode);
                        } else if (StringMaster.isEmpty(valueNode.getTextContent().trim())) {
                            valueGroupNode.removeChild(valueNode);
                        }
                    }
                }
            }
        }
        String contents = XML_Converter.getStringFromXML(doc);
        file.setContents(contents);
        log(1, "New length: " + file.getContents().length());

    }

    public static void removeValue(VALUE val, XML_File file, boolean write, boolean ifEmpty) {
        log(1, "Remove Value " +
                val + " from " + file);
        log(1, "Current length: " + file.getContents().length());
        String newContents = file.getContents();
        String divider = XML_Converter.openXmlFormatted(G_PROPS.NAME.getName());
        List<String> list = new ArrayList<>();
        boolean changed = false;
        for (String subString : newContents.split(divider)) {
            try {
                int openIndex = subString.indexOf(XML_Converter.openXmlFormatted(XML_Converter
                        .getXmlNodeName(val)));
                int closeIndex = subString.lastIndexOf(XML_Converter
                        .closeXmlFormatted(XML_Converter.getXmlNodeName(val)));
                if (openIndex != -1 && closeIndex != -1) {
                    closeIndex += XML_Converter
                            .closeXmlFormatted(XML_Converter.getXmlNodeName(val)).length();
                    subString = subString.substring(0, openIndex)
                            + subString.substring(closeIndex);
                    changed = true;
                }
            } finally {
                list.add(subString);
            }
        }
        if (!changed) {
            return;
        }
        newContents = ContainerUtils.joinStringList(list, divider);
        file.setContents(newContents);
        if (write) {
            XML_Writer.write(file);
        }
        log(1, "New length: " + file.getContents().length());
    }

    public static void renameType(ObjType type, String newName, PROPERTY... props) {
        // backUp();

        // after files are read
        boolean dynamic = false;
        if (props == null) {
            dynamic = true;
        }
        // props = getValuesForType(obj_type);
        for (PROPERTY prop : props) {
            if (dynamic) {

            }
            for (OBJ_TYPE key : ContentValsManager.getOBJ_TYPEsForValue(prop)) {
                for (ObjType objType : DataManager.getTypes(key)) {
                    String value = objType.getProperty(prop);
                    value = value.replaceAll(type.getName(), newName);
                    objType.setProperty(prop, value);
                }
                XML_Writer.writeXML_ForTypeGroup(key);
            }
        }
        type.setName(newName);
        XML_Writer.writeXML_ForType(type, type.getOBJ_TYPE_ENUM());

        // XML_File file = getFile(type.getOBJ_TYPE_ENUM());
        // String newContents = file
        // .getContents()
        // .replaceAll(XML_Converter.openXML(type.getName()), XML_Converter
        // .openXML(newName));
        // newContents =
        // file.getContents().replaceAll(XML_Converter.closeXML(type
        // .getName()), XML_Converter.closeXML(newName));
        // String nameOpen = XML_Converter.openXML(type.getName());
        // String nameClose = XML_Converter.closeXML(type.getName());
        // newContents = file.getContents().replaceFirst(nameOpen +
        // type.getName()
        // + nameClose, nameOpen + newName + nameClose);
        // file.setContents(newContents);
        // XML_Writer.write(file);
    }

    private static List<XML_File> getXmlFiles(OBJ_TYPE key) {
        List<XML_File> list = new ArrayList<>();
        for (XML_File t : XML_Reader.getFiles()) {
            if (key.equals(t.getType())) {
                list.add(t);
            }
        }
        return list;

    }

    // private static XML_File getFile(OBJ_TYPE TYPE) {
    // return getXmlFiles().getOrCreate(TYPE);
    //
    // }

    private static DequeImpl<XML_File> getXmlFiles() {
        if (!ListMaster.isNotEmpty(XML_Reader.getFiles())) {
            XML_Reader.readTypes(true);
        }
        return XML_Reader.getFiles();
        // if (files != null) // ++ refresh by setting to null
        // return files;
        // files = new HashMap<>();
        // Map<String, String> xmlMap = XML_Reader.getXmlMap();
        // if (xmlMap.isEmpty())
        // XML_Reader.readTypes(macro);
        // for (String type : xmlMap.keySet()) {
        // OBJ_TYPES TYPE = OBJ_TYPES.getType(type);
        // XML_File file = new XML_File(TYPE, TYPE.getName(), null, macro,
        // XML_Reader.getXmlMap().getOrCreate(type));
        // files.put(TYPE, file);
        // }
        // return files;
    }


    //    public static void transformResistances() {
    //        for (ObjType t : DataManager.getTypes()) {
    //            DAMAGE_TYPE dmg_type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(
    //                    DAMAGE_TYPE.class, t.getProperty(PROPS.DAMAGE_TYPE));
    //            if (dmg_type != null) {
    //                switch (dmg_type) {
    //                    // case BLUDGEONING
    //                }
    //            }
    //
    //            int earth = t.getIntParam(PARAMS.EARTH_RESISTANCE);
    //            int water = t.getIntParam(PARAMS.WATER_RESISTANCE);
    //            int air = t.getIntParam(PARAMS.AIR_RESISTANCE);
    //            int arcane = t.getIntParam(PARAMS.ARCANE_RESISTANCE);
    //            int dark = t.getIntParam(PARAMS.SHADOW_RESISTANCE);
    //            int chaos = t.getIntParam(PARAMS.CHAOS_RESISTANCE);
    //            int holy = t.getIntParam(PARAMS.HOLY_RESISTANCE);
    //            int acid = earth / 2 + water / 3;
    //            int sonic = air / 3 + earth / 2;
    //            int light = air / 3 + holy * 2 / 3; // ++ %5
    //            int lightning = air * 2 / 3;
    //            int cold = water * 2 / 3;
    //            int psionic = chaos / 2 + dark / 2 + arcane / 2;
    //            int death = dark / 2 + earth / 2;
    //
    //            if (t.checkProperty(G_PROPS.CLASSIFICATIONS, UnitEnums.CLASSIFICATIONS.CONSTRUCT.toString())) {
    //                psionic = Math.min(100, psionic + 50);
    //                death = Math.min(100, death + 50);
    //                cold = Math.min(100, cold + 25);
    //                lightning = Math.max(0, lightning - 25);
    //                acid = Math.max(0, acid - 25);
    //            }
    //
    //            t.setParam(PARAMS.ACID_RESISTANCE, acid);
    //            t.setParam(PARAMS.SONIC_RESISTANCE, sonic);
    //            t.setParam(PARAMS.LIGHT_RESISTANCE, light);
    //            t.setParam(PARAMS.LIGHTNING_RESISTANCE, lightning);
    //            t.setParam(PARAMS.COLD_RESISTANCE, cold);
    //            t.setParam(PARAMS.PSIONIC_RESISTANCE, psionic);
    //            t.setParam(PARAMS.DEATH_RESISTANCE, death);
    //        }
    //    }

}
