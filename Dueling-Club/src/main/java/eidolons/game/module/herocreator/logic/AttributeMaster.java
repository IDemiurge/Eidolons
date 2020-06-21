package eidolons.game.module.herocreator.logic;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.DC_ContentValsManager.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class AttributeMaster {

    public static List<String> getAttributeBonusInfoStrings(ATTRIBUTE attr, Unit hero) {
        List<String> list = new ArrayList<>();
        Object key  = StringMaster.format(attr.name());
        for (PARAMS p : attr.getParams()) {
            ObjectMap<String, Double> map = hero.getModifierMaps().get(p);
            if (map == null) {
                continue;
            }
            Double amount = map.get((String) key);
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
