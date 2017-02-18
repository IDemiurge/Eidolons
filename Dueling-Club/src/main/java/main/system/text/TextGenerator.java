package main.system.text;

import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.ValuePageManager;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.List;

public class TextGenerator {

    private static final String PREFIX = "Provides ";

    public static String generatePerkParamBonuses(Entity e) {

        List<List<VALUE>> pages = ValuePageManager.getValuesForHCInfoPages(e
                .getOBJ_TYPE_ENUM());

        String string = PREFIX;
        for (List<VALUE> list : pages) {
            boolean prop = false;
            for (VALUE v : list) {
                if (v instanceof PARAMETER) {
                    if (!ContentManager.isValueForOBJ_TYPE(DC_TYPE.CHARS, v)) {
                        continue;
                    }
                    String amount = e.getValue(v);
                    amount = TextParser.parse(amount, e.getRef());
                    int n = new Formula(amount).getInt(e.getRef());
                    if (n != 0) {
                        string += n + " " + v.getName() + ", ";
                    }
                } else {
                    prop = true;
                    break;
                }
            }
            if (!prop && !list.equals(pages.get(pages.size() - 1))) {
                string += StringMaster.NEW_LINE;
            }
        }
        // StringMaster.replaceLast(" ," "and
        string.substring(0, string.length() - 2);
        return string;

    }

}
