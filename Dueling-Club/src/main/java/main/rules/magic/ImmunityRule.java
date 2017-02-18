package main.rules.magic;

import main.content.values.properties.G_PROPS;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;

public class ImmunityRule {

    public static boolean checkImmune(Obj obj, String key) {
        key = formatKey(key);
        boolean result = obj.checkProperty(G_PROPS.IMMUNITIES, key);
        if (result) {
            logImmune(obj, key);
        }
        // TODO remove counters if immune?
        return result;

    }

    private static void logImmune(Obj obj, String key) {
        // I probably don't want to log always, right?
        // E.g. with Buff Rules - I only want to log once

        String s = obj.getName() + " is immune to " + key + " effects!";
        obj.getGame().getLogManager().log(s);
    }

    private static String formatKey(String key) {
        return key.replace(StringMaster.COUNTER, "");
    }

}
