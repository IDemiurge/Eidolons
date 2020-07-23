package main.handlers.mod;

import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.system.auxiliary.TimeMaster;
import main.system.launch.CoreEngine;

public class AvVersionHandler {
    public static String getVersion(ObjType t) {
        return CoreEngine.VERSION;
    }
    public static String getTimeStamp() {
        return TimeMaster.getTimeStamp();
    }

    public static void xmlSaved() {
        CoreEngine.incrementXmlBuild();
        ArcaneVault.resetTitle();
    }

    public void newVersion(){
        //saves all and stamps a new version into meta.xml

        //report - types changed, added/removed, symbols

    }
}
