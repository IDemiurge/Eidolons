package main.system;

import main.content.ContentManager;
import main.content.enums.system.MetaEnums.CUSTOM_VALUE_TEMPLATE;
import main.content.values.parameters.PARAMETER;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.StringMaster;

public class CustomValueManager {
    public static final String CUSTOM_VALUE = "CUSTOM_VALUE";
    private static final String VARIABLES = "VARIABLES";

    public static String getCustomValueName(CUSTOM_VALUE_TEMPLATE template, Object... array) {
        return StringMaster.getEnumFormat(template.getText(array));
    }

    /**
     *
     */

    public static String getVarEnumCustomValueName(CUSTOM_VALUE_TEMPLATE template) {
        return template.name();
        // template.getEnum()
        // prompt
    }

    public static String getVarEnumCustomValueName(Class<?> ENUM_CLASS) {
        return ENUM_CLASS.getSimpleName() + "_" + VARIABLES + "_"
                + CUSTOM_VALUE;
    }

    public static String getVariablefromCV(String key, int i) {
        //CUSTOM_VALUE_TEMPLATE template = getTemplate(key);

        // break down into variables
        // where is the types of variables defined for the CV to be constructed
        // in AE/AV?

        return null;
    }

    public static void processCustomValues(Unit unit) {
        // TODO tags CV's for costs, Spellpower etc
        // how best to override the actives' toBase()?
        // modMap(valueName, value) per Active?
        // wait, this is BS... all I need is to re-init costs!

        // is this the only way, to apply manually to each item in group?
        for (String key : unit.getCustomParamMap().keySet()) {
            if (!checkCV(key)) {
                continue;
            }
            String prop = getVariablefromCV(key, 0);
            String propValue = getVariablefromCV(key, 1);
            PARAMETER param = ContentManager
                    .getPARAM(getVariablefromCV(key, 2));
            Integer value = unit.getCounter(key);
            //List<DC_ActiveObj> activeList = unit.getActiveList(prop, propValue);
            //for (DC_ActiveObj active : activeList)
            //    applyMod(active, param, value);
        }

    }

    private static boolean checkCV(String key) {
        // TODO isParam()
        return false;
    }

    private static void applyMod(DC_ActiveObj active, PARAMETER param, Integer value) {
        // TODO Auto-generated method stub
        active.modifyParamByPercent(param, 100 - value, false);

    }

}
