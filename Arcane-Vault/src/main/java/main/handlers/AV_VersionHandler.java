package main.handlers;

import main.entity.type.ObjType;
import main.system.launch.CoreEngine;

public class AV_VersionHandler {
    public static String getVersion(ObjType t) {
        return CoreEngine.VERSION;
    }
}
