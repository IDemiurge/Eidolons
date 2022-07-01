package gdx;

import java.util.HashMap;
import java.util.Map;

public class AphosDemo {
    public static final String VERSION = "0.01x";

    public static void main(String[] args) {
        //value map from args? would be easier...

        Map<String, Object> values = parseArgs(args);
        values.put("fullscreen", false);
        new DemoApp(values).create();
    }

    private static Map<String, Object> parseArgs(String[] args) {
        Map<String, Object> map = new HashMap<>();
        return map;
    }
}
