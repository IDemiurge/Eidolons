package main.handlers.types;

import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;

public class AvCheckHandler extends AvHandler {
    public AvCheckHandler(AvManager manager) {
        super(manager);
    }

    public boolean subgroup(ObjType type, String subgroup) {
        try {
            return type.getSubGroupingKey().equalsIgnoreCase(subgroup);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }
    /*
    for filter/sort
     */
}
