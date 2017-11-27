package main.client.cc.logic;

import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeMaster {

    public static List<String> getAttributeBonusInfoStrings(ATTRIBUTE attr, Unit hero) {
        List<String> list = new ArrayList<>();
        Object key;
        key = StringMaster.getWellFormattedString(attr.name());
        for (PARAMS p : attr.getParams()) {
            Map<String, Double> map = hero.getModifierMaps().get(p);
            if (map == null) {
                continue;
            }
            Double amount = map.get(key);
            if (amount == null) {
                continue;
            }
            String string = "+";
            if (amount < 0) {
                string = "-";
            }
            string += StringMaster.wrapInBraces("" + amount) + " " + p.getName();

            list.add(string);
        }
        return list;
    }

}
