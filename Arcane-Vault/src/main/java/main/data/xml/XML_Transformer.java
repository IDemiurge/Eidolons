package main.data.xml;

import main.content.CONTENT_CONSTS.BUFF_TYPE;
import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.*;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.elements.conditions.*;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.Game;
import main.gui.components.controls.ModelManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.util.ValueHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XML_Transformer {

    private static final int PROGRESSION_WEIGHT_CONST = 3;

    public static void showTransformDialog() {
        int i = JOptionPane.showOptionDialog(null, "", "", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, SPECIAL_ROUTINES.values(),
                SPECIAL_ROUTINES.CLEAN_UP);
        switch (SPECIAL_ROUTINES.values()[i]) {
            case SPEACIAL_ROUTINE:
                transformStringsToNormal();
                break;
            case CLEAN_UP:
                cleanUp();
                break;
            case FILTER_HEROES:
                cleanUp();
                break;
            case REMOVE_VALUE:
                cleanUp();
                break;
            case RENAME_TYPE:
                cleanUp();
                break;
            case RENAME_VALUE:
                cleanUp();
                break;

        }
    }

    private static void transformStringsToNormal() {
        // TODO Auto-generated method stub

    }

    public static void renameValue() {
        String valueName = JOptionPane.showInputDialog(null, "Enter value to be renamed ", null);
        VALUE val = new ValueHelper(Game.game).getValue(valueName);
        String newName = JOptionPane.showInputDialog(null, "Enter value to be renamed ", null);
        renameValue(val, newName);
    }

    public static void renameValue(VALUE val, String newName) {

    }

    public static void transformBuffTypes() {
        for (ObjType t : DataManager.getTypes(OBJ_TYPES.BUFFS)) {
            if (StringMaster.contains(t.getProperty(G_PROPS.BUFF_TYPE), "buff")) {
                t.setProperty(G_PROPS.BUFF_TYPE, StringMaster
                        .getWellFormattedString(BUFF_TYPE.SPELL.name()));
            }
        }
    }

    public static void transformResistances() {
        for (ObjType t : DataManager.getTypes()) {
            DAMAGE_TYPE dmg_type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(
                    DAMAGE_TYPE.class, t.getProperty(PROPS.DAMAGE_TYPE));
            if (dmg_type != null) {
                switch (dmg_type) {
                    // case BLUDGEONING
                }
            }

            int earth = t.getIntParam(PARAMS.EARTH_RESISTANCE);
            int water = t.getIntParam(PARAMS.WATER_RESISTANCE);
            int air = t.getIntParam(PARAMS.AIR_RESISTANCE);
            int arcane = t.getIntParam(PARAMS.ARCANE_RESISTANCE);
            int dark = t.getIntParam(PARAMS.SHADOW_RESISTANCE);
            int chaos = t.getIntParam(PARAMS.CHAOS_RESISTANCE);
            int holy = t.getIntParam(PARAMS.HOLY_RESISTANCE);
            int acid = earth / 2 + water / 3;
            int sonic = air / 3 + earth / 2;
            int light = air / 3 + holy * 2 / 3; // ++ %5
            int lightning = air * 2 / 3;
            int cold = water * 2 / 3;
            int psionic = chaos / 2 + dark / 2 + arcane / 2;
            int death = dark / 2 + earth / 2;

            if (t.checkProperty(G_PROPS.CLASSIFICATIONS, CLASSIFICATIONS.CONSTRUCT.toString())) {
                psionic = Math.min(100, psionic + 50);
                death = Math.min(100, death + 50);
                cold = Math.min(100, cold + 25);
                lightning = Math.max(0, lightning - 25);
                acid = Math.max(0, acid - 25);
            }

            t.setParam(PARAMS.ACID_RESISTANCE, acid);
            t.setParam(PARAMS.SONIC_RESISTANCE, sonic);
            t.setParam(PARAMS.LIGHT_RESISTANCE, light);
            t.setParam(PARAMS.LIGHTNING_RESISTANCE, lightning);
            t.setParam(PARAMS.COLD_RESISTANCE, cold);
            t.setParam(PARAMS.PSIONIC_RESISTANCE, psionic);
            t.setParam(PARAMS.DEATH_RESISTANCE, death);
        }
    }

    public static void renameValue(String oldString, String newString) {

    }

    public static void filterHeroes() {
        Conditions c = new Conditions();
        c.add(new NotCondition(new NumericCondition(StringMaster.getValueRef(KEYS.SOURCE,
                PARAMS.HERO_LEVEL), "1", true)));
        // c.add(new StringComparison(StringMaster
        // .getValueRef(KEYS.SOURCE, G_PROPS.RACE), "Human", true));
        c.add(new NotCondition(new StringComparison(StringMaster.getValueRef(KEYS.SOURCE,
                G_PROPS.GROUP), "Background", true)));
        filterTypes(c, OBJ_TYPES.CHARS);
    }

    public static void filterTypes(Condition c, OBJ_TYPE... TYPES) {
        for (OBJ_TYPE TYPE : TYPES) {
            for (ObjType type : DataManager.getTypes(TYPE)) {
                if (c.check(type.getRef())) {
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

        List<String> container = StringMaster.openContainer(attrString);
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
        DequeImpl<XML_File> xmlFiles = getXmlFiles();
        for (XML_File file : xmlFiles) {
            for (VALUE v : ContentManager.getValueList()) {
                if (!ContentManager.isValueForOBJ_TYPE(file.getType(), v)) {
                    removeValue(v, file, false, false);
                }
            }

            removeEmptyNodes(file);
            XML_Writer.write(file);

        }
    }

    private static void removeEmptyNodes(XML_File file) {
        Document doc = XML_Converter.getDoc(file.getContents());
        if (doc == null) {
            return;
        }
        for (Node groupNode : XML_Converter.getNodeList(doc.getFirstChild())) {
            for (Node typeNode : XML_Converter.getNodeList(groupNode)) {
                for (Node valueGroupNode : XML_Converter.getNodeList(typeNode)) {
                    for (Node valueNode : XML_Converter.getNodeList(valueGroupNode)) {
                        if (StringMaster.isEmpty(valueNode.getTextContent())) {
                            valueGroupNode.removeChild(valueNode);
                        }
                    }
                }
            }
        }
        String contents = XML_Converter.getStringFromXML(doc);
        file.setContents(contents);

    }

    public static void removeValue(VALUE val, XML_File file, boolean write, boolean ifEmpty) {
        String newContents = file.getContents();
        String divider = XML_Converter.openXmlFormatted(G_PROPS.NAME.getName());
        List<String> list = new LinkedList<>();
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
                            + subString.substring(closeIndex, subString.length());
                    changed = true;
                }
            } finally {
                list.add(subString);
            }
        }
        if (!changed) {
            return;
        }
        newContents = StringMaster.joinStringList(list, divider);
        file.setContents(newContents);
        if (write) {
            XML_Writer.write(file);
        }
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
            for (OBJ_TYPE key : ContentManager.getOBJ_TYPEsForValue(prop)) {
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
        List<XML_File> list = new LinkedList<>();
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

    private static void backUp() {
        ModelManager.backUp(); // new reserve?
    }

    public enum SPECIAL_ROUTINES {
        SPEACIAL_ROUTINE, CLEAN_UP, FILTER_HEROES, REMOVE_VALUE, RENAME_VALUE, RENAME_TYPE,

    }

}
