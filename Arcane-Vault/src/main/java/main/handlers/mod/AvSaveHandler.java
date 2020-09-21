package main.handlers.mod;

import main.ability.AE_Manager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.sound.AudioEnums;
import main.system.sound.SoundMaster;
import main.system.threading.TimerTaskMaster;
import main.system.threading.WaitMaster;
import main.system.threading.Weaver;

import javax.swing.*;

public class AvSaveHandler {
    private static final long BACK_UP_PERIOD = 300000;
    static final long RELOAD_PERIOD = 20000;
    private static final int WAIT_PERIOD = 2000;
    static boolean auto;
    private static boolean saving;
    static boolean autoSaveOff;
    private static boolean backupOnLaunch;

    public AvSaveHandler() {
        backupOnLaunch= StringMaster.countChar(ArcaneVault.getTypes(), ";")>3;
        //TODO this is a hack..
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

            AvVersionHandler.xmlSaved();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            saving = false;
        }
    }

    public static void startSaving() {
        if (backupOnLaunch) {
            Weaver.inNewThread(true, new Runnable() {
                @Override
                public void run() {
                    AvModelHandler.backUp();
                }
            });
        }

        TimerTaskMaster.newTimer(new AvModelHandler(), "saveAllIfDirty", null, null, BACK_UP_PERIOD);
    }

    public static void startBackingUp() {
        TimerTaskMaster.newTimer(new AvModelHandler(), "backUp", null, null, BACK_UP_PERIOD);

    }

    public static boolean isAutoSaveOff() {
        return autoSaveOff;
    }

    public static void setAutoSaveOff(boolean autoSaveOff) {
        AvSaveHandler.autoSaveOff = autoSaveOff;
    }

    public static void fullBackUp() {
        //entire freaking directory? :)
        String dir = PathFinder.getTYPES_PATH();
        String newPath= PathFinder.getXML_PATH()+"/"+PathFinder.MICRO_MODULE_NAME+"/"+"types backup";
        FileManager.copyDir(dir, newPath);
    }

    public static void save(OBJ_TYPE obj_type) {

        if (obj_type.isTreeEditType() || obj_type == DC_TYPE.ABILS) {
            AE_Manager.saveTreesIntoXML();
        }
        if (!auto) {
            AvModelHandler.checkTypeModifications(obj_type);
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
}
