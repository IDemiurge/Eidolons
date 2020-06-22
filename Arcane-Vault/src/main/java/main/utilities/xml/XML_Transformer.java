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
import main.data.filesys.PathFinder;
import main.data.xml.*;
import main.elements.conditions.*;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.handlers.mod.AvSaveHandler;
import main.launch.ArcaneVault;
import main.swing.generic.components.editors.lists.ListChooser;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

public class XML_Transformer {

    private static final int PROGRESSION_WEIGHT_CONST = 3;
    private static boolean main;

    public enum SPECIAL_ROUTINES {
        SPEACIAL_ROUTINE, CLEAN_UP, FILTER_HEROES, REMOVE_VALUE, RENAME_VALUE, RENAME_TYPE,
        REPAIR, RELEASE_CLEANUP // remove dev values
    }

    public static void main(String[] args) {
        //TODO planned routines via ARGS!
        // for (String arg : args) {
        //     SPECIAL_ROUTINES special_routines = SPECIAL_ROUTINES.valueOf(arg);
        //     // doRoutine(special_routines, argList);
        // }
        main = true;
        GuiManager.init();
        if (DialogMaster.confirm("Special routine?")) {
            routine();
            return;
        }
        if (DialogMaster.confirm("Overwrite backup?")) {
            backUp();
        }
        showTransformDialog();
    }

    private static void routine() {
        renameValue("STA_COST", PARAMS.TOU_COST.getName());
        renameValue("STAMINA_PENALTY", PARAMS.TOUGHNESS_PENALTY.getName());
        renameValue("AOO_STAMINA_PENALTY", PARAMS.AOO_TOUGH_PENALTY.getName());
        renameValue("COUNTER_STAMINA_PENALTY", PARAMS.COUNTER_TOUGH_PENALTY.getName());
        renameValue("INSTANT_STAMINA_PENALTY", PARAMS.INSTANT_TOUGH_PENALTY.getName());
        renameValue("MOVE_STA_PENALTY", PARAMS.MOVE_TOU_PENALTY.getName());


        renameValue("MORALE_RESTORATION", PARAMS.ESSENCE_RESTORATION.getName());
        renameValue("MORALE_RETAINMENT", PARAMS.ESSENCE_RETAINMENT.getName());
        // renameValue("FOCUS_RESTORATION", PARAMS.FOCUS_RESTORATION.getName());
        // renameValue("FOCUS_RETAINMENT", PARAMS.FOCUS_RETAINMENT.getName());


        // renameValue("", PARAMS.FOCUS_REGEN.getName());
        // removeValue(  PARAMS.STA_COST.getName());
        // renameValue("", PARAMS.FOCUS_RECOVER_REQ.getName());

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
            case REPAIR:
                repair(DC_TYPE.SPELLS);
                break;

        }
    }


    private static void backUp() {
        AvSaveHandler.fullBackUp(); // new reserve?
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
        name = StringMaster.format(name);
        newName = StringMaster.format(newName);
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
                newContents = newContents.replace(StringMaster.format(name), newName);
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
                        .format(GenericEnums.BUFF_TYPE.SPELL.name()));
            }
        }
    }

    private static void repair(OBJ_TYPE TYPE  ) {
/*
restore values from backup file
run comparison based on current valueTypePairs
- list values that are not present...
 */
        Map<String, ObjType> map = new LinkedHashMap<>();
         DataManager.getTypes(TYPE).stream().forEach(type-> map.put(type.getName(), type));
        // XML_Reader.setCustomTypesPath(PathFinder.getBACKUP_TYPES_PATH());
        XML_Reader.readTypeFile(PathFinder.getBACKUP_TYPES_PATH()+TYPE.getName()
                +".xml", TYPE);
        Set<VALUE> missing=new LinkedHashSet<>();
        Set<ObjType> repaired = new LinkedHashSet<>();
        Document doc = XML_Converter.getDoc(XML_Reader.getFile((DC_TYPE) TYPE).getContents());
        for (Node group : XmlNodeMaster.getNodeListFromFirstChild(doc,true))
        for (Node typeNode : XmlNodeMaster.getNodeList(group))
        {
            ObjType type = map.get(XML_Formatter.restoreXmlNodeName(typeNode.getNodeName()));
            for (Node node : XmlNodeMaster.getNodeList(typeNode))
        {
            if (node.getNodeName().equalsIgnoreCase("params")) {
                repair(true,type, missing ,repaired, node, TYPE);
            }
            if (node.getNodeName().equalsIgnoreCase("props")) {
                repair(false,type, missing ,repaired, node, TYPE);
            }
        }
        }
        // XML_Converter.getTypeListFromXML(backupXml , false).for

        log(missing.size()+ " Missing vals: " + ContainerUtils.constructStringContainer(missing, "\n"));
        log(repaired.size()+ " Repaired types: " + ContainerUtils.constructStringContainer(repaired, "\n"));

        if (DialogMaster.confirm("Save?")) {
            XML_Writer.writeXML_ForTypeGroup(TYPE);
        }

    }

    private static void repair(boolean parameter, ObjType type, Set<VALUE> missing,
                               Set<ObjType> repaired, Node node, OBJ_TYPE TYPE) {
        for (Node valNode : XmlNodeMaster.getNodeList(node)) {
            VALUE val = ContentValsManager.getValue(valNode.getNodeName());
            String value = valNode.getTextContent();
            if (DC_ContentValsManager.isValueForOBJ_TYPE(TYPE, val ))
                if (!type.getValueMap(parameter).containsKey(val)) {
                    type.setValue(val, value);
                    repaired.add(type);
                    missing.add(val);
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
                    // Core REvamp - maybe if we used >name< that would be more reliable, otherwise too fuzzy
                    value = value.replaceAll(type.getName(), newName);
                    objType.setProperty(prop, value);
                }
                XML_Writer.writeXML_ForTypeGroup(key);
            }
        }
        type.setName(newName);
        XML_Writer.writeXML_ForType(type, type.getOBJ_TYPE_ENUM());

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
    }

    private void cleanUp_() {

        String string = "What do I clean up now?..";
        String TRUE = "Group";
        String FALSE = "Subgroup";
        String NULL = "XML";
        Boolean result = DialogMaster.askAndWait(string, TRUE, FALSE, NULL);
        if (result == null) {
            XML_Transformer.cleanUp();
            return;
        }
        OBJ_TYPE TYPE = ArcaneVault.getSelectedOBJ_TYPE();
        String subgroup = (result) ? ArcaneVault.getSelectedType().getGroupingKey() : ArcaneVault
                .getSelectedType().getSubGroupingKey();
        List<String> types = (result) ? DataManager.getTypesGroupNames(TYPE, subgroup)
                : DataManager.getTypesSubGroupNames(TYPE, subgroup);
        List<String> retained = ContainerUtils.openContainer(new ListChooser(ListChooser.SELECTION_MODE.MULTIPLE,
                types, TYPE).choose());
        for (String t : types) {
            if (retained.contains(t)) {
                continue;
            }
            DataManager.removeType(t, TYPE.getName());
        }
        ArcaneVault.getMainBuilder().getTreeBuilder().reload();

        int n = ArcaneVault.getMainBuilder().getTree().getRowCount();
        ArcaneVault.getMainBuilder().getTree().setSelectionRow(Math.min(1, n));
        ArcaneVault.getMainBuilder().getTree().getListeners(TreeSelectionListener.class)[0]
                .valueChanged(new TreeSelectionEvent(ArcaneVault.getMainBuilder().getTree(), null,
                        null, null, null));
        ArcaneVault.getMainBuilder().getEditViewPanel().refresh();

        // reset tree

    }

    private void renameType(ObjType type) {
        if (type == null) {
            return;
        }
        String input = ListChooser.chooseEnum(PROPERTY.class, ListChooser.SELECTION_MODE.MULTIPLE);

        if (StringMaster.isEmpty(input)) {
            return;
        }

        List<PROPERTY> propList = new ListMaster<>(PROPERTY.class).toList(input);

        String newName = JOptionPane.showInputDialog("Enter new name");
        if (StringMaster.isEmpty(newName)) {
            return;
        }
        XML_Transformer.renameType(type, newName, propList.toArray(new PROPERTY[propList.size()]));
    }


}
