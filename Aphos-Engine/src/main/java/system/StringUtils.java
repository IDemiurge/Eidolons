package system;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Alexander on 10/19/2023
 */
public class StringUtils {
    public static String build(String prefix, String separator, Object... toLog) {

        StringBuilder builder = new StringBuilder(prefix);
        for (Object o : toLog) {
            if (o instanceof Map)
                builder.append(MapMaster.represent((Map) o));
            else
            if (o instanceof Collection)
                builder.append(ListMaster.represent((Collection) o));
            else
                builder.append(o);

            builder.append(separator);
        }
        return builder.toString();
    }
}
