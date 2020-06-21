package main.handlers.types;

import main.entity.type.ObjType;

public class AvCheckHandler {
    public boolean subgroup(ObjType type, String subgroup) {
        return type.getProperty(type.getSubGroupingKey()).equalsIgnoreCase(subgroup);
    }
    /*
    for filter/sort
     */
}
