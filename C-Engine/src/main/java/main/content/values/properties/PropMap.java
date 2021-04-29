package main.content.values.properties;

import main.content.ContentValsManager;
import main.content.ValueMap;

public class PropMap  extends ValueMap<PROPERTY> {

    @Override
    public String get(String valueName) {
        PROPERTY property = ContentValsManager.getPROP(valueName);
        return get(property);
    }

    @Override
    public String put(String valueName, String value) {
        PROPERTY p = ContentValsManager.getPROP(valueName);
        return map.put(p, value);
    }
    @Override
    protected boolean isEmpty(String value) {
        return value==null || value.isEmpty();
    }
    }