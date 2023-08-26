package system.log;

import system.ListMaster;
import system.MapMaster;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 */
public class SysLog {
    public static final void printOut(Object... toLog){

        StringBuilder builder = new StringBuilder("Data: ");
        for (Object o : toLog) {
            if (o instanceof Map)
                builder.append(MapMaster.represent((Map) o));
            else
            if (o instanceof Collection)
                builder.append(ListMaster.represent((Collection) o));
            else
                builder.append(o);

            builder.append(", ");
        }
       System.out.println(builder.toString());
    }
    ////////////////////region STD LOG SHORTCUTS

    //endregion
}
