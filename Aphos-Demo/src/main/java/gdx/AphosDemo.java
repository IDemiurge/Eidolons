package gdx;

import main.system.data.DataUnit;

import java.util.HashMap;
import java.util.Map;

public class AphosDemo {
    public static final String VERSION = "0.01x";

    public static void main(String[] args) {
        //value map from args? would be easier...
        Map<String, String> values = new HashMap<>();
        if (args.length > 0) {
            values = parseLaunchVals(args[0]);
        }
//        values.put("fullscreen", false);
        new DemoApp(values).start();
    }

    private static Map<String, String> parseLaunchVals(String args) {
        return new DataUnit<>(args).getValues();
    }
}
