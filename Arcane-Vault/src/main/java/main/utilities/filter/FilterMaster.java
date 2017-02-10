package main.utilities.filter;

import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.data.xml.XML_Converter;
import main.elements.conditions.Conditions;
import main.entity.Ref.KEYS;
import main.launch.ArcaneVault;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.ConditionMaster;
import main.system.ConditionMaster.CONDITION_TEMPLATES;
import main.system.auxiliary.StringMaster;
import org.w3c.dom.Document;

import java.util.LinkedList;
import java.util.List;

public class FilterMaster {
    /*
     * valueName ++ value easier to deal via conditions just getOrCreate a bridge
	 */

    public final static char HOTKEY_CHAR = 't';
    static List<TypeFilter> filters = new LinkedList<>();

    public static void save() {
        for (TypeFilter f : filters) {

            f.getCondition();
            f.getTYPE();
            // PathFinder.getXML_PATH() + "\\filters\\";

        }
    }

    public static void newFilter() {

        Conditions conditions = new Conditions();
        OBJ_TYPE TYPE = ArcaneVault.getSelectedOBJ_TYPE();
        while (true) {
            // CHOOSE TYPE? OR USE SELECTED TAB!
            int i = DialogMaster.optionChoice(CONDITION_TEMPLATES.values(),
                    "choose filter template");
            if (i == -1) {
                break;
            }
            CONDITION_TEMPLATES template = CONDITION_TEMPLATES.values()[i];

            String value = DialogMaster.inputText("Enter filter's value name");
            if (StringMaster.isEmpty(value)) {
                break;
            }
            VALUE val = ContentManager.findValue(value);
            value = DialogMaster.inputText("Enter filtering value");
            if (StringMaster.isEmpty(value)) {
                break;
            }
            conditions.add(new ConditionMaster().getConditionFromTemplate(template, StringMaster
                    .getValueRef(KEYS.MATCH, val), value));

        }
        if (conditions.isEmpty()) {
            return;
        }
        if (TYPE == null) {

        }
        Document node = XML_Converter.getDoc(XML_Converter.openXml("conditions")
                + XML_Converter.closeXml("conditions"));
        String conditionString = XML_Converter.getStringFromXML(node);

        TypeFilter filter = new TypeFilter(conditions, TYPE);
        filters.add(filter); // save!
        ArcaneVault.getMainBuilder().getTabBuilder().addFilter(filter);
    }

}
