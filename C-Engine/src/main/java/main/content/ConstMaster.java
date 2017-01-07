package main.content;

import java.util.LinkedHashMap;

public class ConstMaster {
    /*
     * ref format: [const_name] => *find*
	 */

    private static LinkedHashMap<String, String> constMap;

    public static void init() {
        constMap = new LinkedHashMap<>();
    }

    public static String findConst(String s) {
        return s;

    }

}
