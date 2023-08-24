package apps;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/1/2023
 */
public class JOption {

    public static Object pick(Object... options) {
        return options[pickIndex(options)];
    }
    public static int pickIndex(Object... options) {
       return  JOptionPane.showOptionDialog(null, "Pick an option", "Choice Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
    }

    public static Map<String, Object> multiPick(Map<String, PickType> picks) {
        Map<String, Object> map= new LinkedHashMap<>();
        for (String s : picks.keySet()) {
            Object result = pickType(picks.get(s), s);
            map.put(s, result);
        }
        return map;
    }
    public static Object pickType(PickType type, String comment) {
        switch (type) {
            case Bool:  return JOptionPane.showConfirmDialog(null, comment);
        }
        if (type== PickType.Bool){
            return JOptionPane.showConfirmDialog(null, comment);
        }
        if (type== PickType.String){
            return JOptionPane.showInputDialog(null, comment);
        }
        if (type== PickType.UnitType){
            return pick(Consts.UNITS);
        }
        if (type== PickType.Factors){
            return multiPick(Consts.attackFactors);
        }
        return null;
    }
    public enum PickType {
        Bool,
        String,
        UnitType, Factors,

    }
}
