package main.handlers.mod;

import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.TimeMaster;
import main.system.launch.CoreEngine;

public class AvVersionHandler {
    public static String getVersion(ObjType t) {
        return CoreEngine.VERSION;
    }
    public static String getTimeStamp() {
        return TimeMaster.getTimeStamp();
    }

    // List<AvOperation> operations;

    public static void xmlSaved() {
        //changes appended

    }

    public static void typeModified() {
        // operations.add(new AvOperation(modified, type, value, prev, newVal));
    }
    public static void commitVersion() {
        DialogMaster.confirm("Commit data for version " + CoreEngine.VERSION + " ?");

        CoreEngine.incrementXmlBuild();
        ArcaneVault.resetTitle();
    }

    public void newVersion(){
        //saves all and stamps a new version into meta.xml

        //report - types changed, added/removed, symbols

    }
}
