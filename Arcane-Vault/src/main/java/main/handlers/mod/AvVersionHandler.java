package main.handlers.mod;

import eidolons.system.text.NameMaster;
import main.data.SmartBackend;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.launch.ArcaneVault;
import main.swing.generic.services.DialogMaster;
import main.system.PathUtils;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static main.handlers.mod.AvVersionHandler.AV_OPERATION.*;
import static main.v2_0.AV2.getSaveHandler;

public class AvVersionHandler extends AvHandler {

    private final SmartBackend data = new SmartBackend() {
        @Override
        protected XML_Reader getXmlReader() {
            return Av2_Xml.getInstance();
        }
    };

    public AvVersionHandler(AvManager manager) {
        super(manager);
    }

    public static String getVersion(ObjType t) {
        return CoreEngine.VERSION;
    }

    public static String getTimeStamp() {
        return TimeMaster.getTimeStamp();
    }

    List<AvOperation> operations = new LinkedList<>();
    StringBuilder changeLog = new StringBuilder();

    public void newVersion() {
        //saves all and stamps a new version into meta.xml
        //report - types changed, added/removed, symbols
    }

    public void writeChangeLog() {
        String content = changeLog.toString();
        FileManager.write(content, getPreviousVersionPath() +"changelog.txt");
    }

    public void xmlSaved() {
        //changes appended

    }

    // afterini
    public void readPreviousDataVersion() {
        Av2_Xml.loadXml(getPreviousVersionPath());

    }

    private String getPreviousVersionPath() {
        return PathUtils.cropLastPathSegment(PathFinder.getTYPES_PATH()) + "/" + NameMaster.version(CoreEngine.PREV_XML_BUILD)+ "/";
    }

    public void commitVersion() {
        if  (!promptCommit())
            return;

        getSaveHandler().backupNewVersion(CoreEngine.XML_BUILD);
        CoreEngine.incrementXmlBuild();
        writeChangeLog();
        ArcaneVault.resetTitle();
    }

    private boolean promptCommit() {
        // while (promptCommit()) ;
        int optionChoice = DialogMaster.optionChoice("Commit data for version " + CoreEngine.VERSION + " ?",
                "View Changelog", "Commit", "Cancel");
        if (optionChoice==-1 || optionChoice== 2) {
            return false;
        }
            if (optionChoice==0) {
               return DialogMaster.confirm(changeLog.toString());
        }
            return true;
    }


    public void modified(ObjType type, String valName, String newValue) {
        operation(modified, type, valName, type.getValue(valName), newValue);

    }

    private void operation(AV_OPERATION modified, ObjType type, String valName, String value, String newValue) {
        operations.add(new AvOperation(modified, type, valName, value, newValue));
    }

    public void renamed(ObjType type, String newValue) {
        operation(renamed, type, null, type.getName(), newValue);
        //spec operation?
    }

    public ObjType getPreviousVersion(ObjType selectedType) {
        ObjType type = data.getType(selectedType.getName(), selectedType.getOBJ_TYPE_ENUM());
        //handle renames!
        return type;
    }

    public void rollback(ObjType type) {
        for (AvOperation operation : new ArrayList<>(operations)) {
            if (operation.type == type) {
                rollback(operation);
                operations.remove(operation);
            }
        }
        //TODO
    }

    private void rollback(AvOperation operation) {
        switch (operation.operation) {

        }
    }

    public enum AV_OPERATION {
        modified,
        renamed,
        added,
        removed,

    }

    public static class AvOperation {

        private final AV_OPERATION operation;
        private final ObjType type;
        private final String valName;
        private final String prevValue;
        private final String newValue;

        public AvOperation(AV_OPERATION operation, ObjType type, String valName, String prevValue, String newValue) {
            this.operation = operation;
            this.type = type;
            this.valName = valName;
            this.prevValue = prevValue;
            this.newValue = newValue;
        }
    }
}
