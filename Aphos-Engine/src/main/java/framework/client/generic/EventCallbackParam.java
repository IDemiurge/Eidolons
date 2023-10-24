package framework.client.generic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class EventCallbackParam {

    private Object object;
    Map<String, Object> namedArgs;
    Object[] args;

    public EventCallbackParam(Object param) {
        object = param;
    }

    public EventCallbackParam(Object... args) {
        this.args = (args);
        if (args.length>0) {
            object = args[0];
        }
    }

    public EventCallbackParam(boolean named, Object... args) {
        namedArgs = new HashMap<>();
        for (int j = 0; j < args.length; j += 2) {
            namedArgs.put(args[j].toString().toLowerCase(), args[j + 1]);
        }
        if (args.length>1) {
            object = args[1];
        }
    }

    //    public EventCallbackParam(Map<String, Object> namedArgs) {
    //        this.namedArgs = namedArgs;
    //    }

    public Object get() {
        return object;
    }

    public Object get(int i) {
        return args[i];
    }

    public <T> T get(String key, Class<T> c) {
        return (T) namedArgs.get(key.toLowerCase());
    }

    public Integer getInt(String action) {
        return (Integer) get(action);
    }
    public Object get(String key) {
        return namedArgs.get(key.toLowerCase());
    }

    public Map<String, Object> getNamedArgs() {
        return namedArgs;
    }

    public Object[] getArgs() {
        return args;
    }

}
