package main.content.values.parameters;

import main.content.ContentValsManager;
import main.content.ValueMap;

public class ParamMap extends ValueMap<PARAMETER> {

    @Override
    public String get(String valueName) {
        PARAMETER param = ContentValsManager.getPARAM(valueName);
        return get(param);
    }

    @Override
    public String put(String valueName, String value) {
        PARAMETER p = ContentValsManager.getPARAM(valueName);
        return map.put(p, value);
    }

    @Override
    protected boolean isEmpty(String value) {
        if (value==null ||value.isEmpty() || value.equals("0")) {
            return true;
        }
        return false;
    }
}
