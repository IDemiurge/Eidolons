package main.handlers.mod;

import eidolons.system.text.NameMaster;
import main.ability.AE_Manager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.launch.ArcaneVault;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.sound.AudioEnums;
import main.system.sound.SoundMaster;
import main.system.threading.TimerTaskMaster;
import main.system.threading.WaitMaster;
import main.system.threading.Weaver;
import main.v2_0.AV2;

import javax.swing.*;

public class AvSaveHandler  extends AvHandler {
    private static final int WAIT_PERIOD = 2000;
    private static final long AUTO_SAVE_PERIOD = 30000;
    static boolean auto;
    private static boolean saving;
    static boolean autoSaveOff;
    private static boolean backupOnLaunch;

    public AvSaveHandler(AvManager manager) {
        super(manager);
        backupOnLaunch= StringMaster.countChar(ArcaneVault.getTypes(), ";")>3;
    //     //TODO this is a hack..
    }

    public static void save(ObjType type, String valName) {
        AvModelHandler.getAV_Manager().save(type);
    }

    public static void save() {
        if (ArcaneVault.getSelectedOBJ_TYPE() == DC_TYPE.ABILS) {
            VariableManager.setVariableInputRequesting(JOptionPane.showConfirmDialog(null,
                    "Do you want to set variables manually?") == JOptionPane.YES_OPTION);
        }
        Weaver.inNewThread(new Runnable() {
            @Override
            public void run() {
                save(ArcaneVault.getSelectedOBJ_TYPE());
            }
        });
        VariableManager.setVariableInputRequesting(false);
        return;

    }

    public static void saveAllIfDirty() {
        if (!isAutoSaveOff()) {
            if (ArcaneVault.isDirty()) {
                auto = true;
                try {
                    saveAll();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                } finally {
                    auto = false;
                }
            }
        }

    }

    public static void saveAll() {
        ArcaneVault.setDirty(true);
        SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.DONE);
        Weaver.inNewThread(new Runnable() {
            public void run() {
                saveAllTypes();
            }
        });

    }

    private static void saveAllTypes() {
        ArcaneVault.getWorkspaceManager().save();
        if (saving) {
            return;
        }
        saving = true;
        try {
            if (ArcaneVault.isMacroMode()) {

                for (String type : XML_Reader.getTypeMaps().keySet()) {
                    save(ContentValsManager.getOBJ_TYPE(type));

                }
            } else {
                for (String type : XML_Reader.getTypeMaps().keySet()) {
                    OBJ_TYPE objType = ContentValsManager.getOBJ_TYPE(type);
                    if (auto) {
                        if (objType == DC_TYPE.PARTY) {
                            continue;
                        }
                    }
                    save(objType);
                    if (auto) {
                        WaitMaster.WAIT(WAIT_PERIOD);
                    }
                }
            }

            SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CHECK);
            ArcaneVault.setDirty(false);

            AV2.getVersionHandler().xmlSaved();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            saving = false;
        }
    }

    @Override
    public void loaded() {
        startSaving();
    }

    public void startSaving() {
        if (backupOnLaunch) {
            Weaver.inNewThread(true, () -> fullBackUp(null));
        }
        TimerTaskMaster.newTimer(this, "saveAllIfDirty", null, null, AUTO_SAVE_PERIOD);
    }

    public void backupNewVersion(String xmlBuild) {
        fullBackUp(xmlBuild);
        //write changelog to returned path
    }
    public static void fullBackUp(String version) {
        //entire freaking directory? :)
        String dir = PathFinder.getTYPES_PATH();
        String newPath= PathFinder.getXML_PATH()+"/"+PathFinder.MICRO_MODULE_NAME+"/"+(version==null? "types backup" :
                NameMaster.version(version));
        FileManager.copyDir(dir, newPath);
    }

    public static void save(OBJ_TYPE obj_type) {

        if (obj_type.isTreeEditType() || obj_type == DC_TYPE.ABILS) {
            AE_Manager.saveTreesIntoXML();
        }
        if (!auto) {
            AvAdjuster.checkTypeModifications(obj_type);
        } else {
            if (obj_type == DC_TYPE.CHARS) {
                XML_Reader.checkHeroesAdded();
            }
        }
        try {
            XML_Writer.writeXML_ForTypeGroup(obj_type);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.FAIL);
            return;
        }

    }
    public static boolean isAutoSaveOff() {
        return autoSaveOff;
    }

    public static void setAutoSaveOff(boolean autoSaveOff) {
        AvSaveHandler.autoSaveOff = autoSaveOff;
    }

}
