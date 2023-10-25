package system.consts;

/**
 * Created by Alexander on 8/22/2023
 */
public class StringConsts {
    public static final String CONTAINER_SEPARATOR = ";";
    public static final String MIN_BASE_MAX_SEPARATOR = "/";
    public static final String CONTAINER_PROP_SEPARATOR = ",";

    public static String checkValueNameReplacement(String key) {
        return switch (key) {
            case "ATK" -> "Attack";
            case "DEF" -> "Defense";
            case "RES" -> "Resistance";
            default -> key;
        };
    }
}
