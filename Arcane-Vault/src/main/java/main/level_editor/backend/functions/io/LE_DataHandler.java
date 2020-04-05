package main.level_editor.backend.functions.io;

import eidolons.game.core.EUtils;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.utils.FileChooserX;
import main.data.filesys.PathFinder;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LE_DataHandler extends LE_Handler {
    private Campaign campaign;
    private Timer timer;
    private boolean dirty;

    public LE_DataHandler(LE_Manager manager) {
        super(manager);

    }

    public void afterLoaded() {
        backup();
        timer = new Timer("autosaver");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isDirty())
                    if (isAutosave()) {
                        autosave();
                    }
            }
        };
        timer.schedule(task, Calendar.getInstance().getTime(), getAutosavePeriod());

    }

    private long getAutosavePeriod() {
        return 30000;
    }

    public void backup() {
        String path = getBackupPath(getFloor());
        String contents = FileManager.readFile(getDefaultSavePath(getFloor()));
        FileManager.write(contents, path);
        EUtils.showInfoText("Backed up as " + PathUtils.getLastPathSegment(path));
    }


    public void autosave() {
//        LE_OptionsMaster.getOptions_().getBooleanValue(AUTOSAVE)
        if (isBackupAutosave()) {
            backup();
        } else {
            saveFloor();
        }
    }

    private boolean isBackupAutosave() {
        return false;
    }

    private boolean isAutosave() {
        return true;
    }


    public void saveFloor() {
        String path = getDefaultSavePath(getFloor());
        saveAs(path);
        setDirty(false);
    }

    public void saveAs(String path) {
        String contents = LE_XmlMaster.toXml(getFloor());
        FileManager.write(contents, path);
        EUtils.showInfoText("Saved as " + PathUtils.getLastPathSegment(path));
    }

    private String getBackupPath(Floor floor) {
        return getDefaultSavePath(floor, "backup");
    }

    private String getDefaultSavePath(Floor floor, String prefix) {
        return PathFinder.getDungeonLevelFolder() + prefix + "/" + floor.getName() + ".xml";
    }

    private String getDefaultSavePath(Floor floor) {
        String prefix = "";
        if (campaign == null) {
            if (LevelEditor.TEST_MODE) {
                prefix = "test/";
            } else {
                prefix = "crawl/";
//                prefix += floor.getGame().getDungeon().getGroup() + "/";
            }
        } else {
//            TODO campaign
        }
        return getDefaultSavePath(floor, prefix);
    }

    public void open(String floorName) {
        Campaign campaign = getDataHandler().getCampaign();
//        campaign.getFloorPath(floorName);
//        Floor floor = getDataHandler().loadFloor(floorName);
//        LevelEditor.floorSelected(floor);

//        ScreenData data = new ScreenData(SCREEN_TYPE.EDITOR, floorName);
//        data.setParam(new EventCallbackParam(floor));
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);

    }

    public void activateFloorTab(Floor floor) {

    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void openFloor() {
        String file = FileChooserX.chooseFile(PathFinder.getDungeonLevelFolder(),
                "xml", LE_Screen.getInstance().getGuiStage());

        FloorManager.addFloor();
    }


    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        if (dirty) {
            GdxMaster.setWindowName("*" + LevelEditor.getWindowName());
        } else {
            GdxMaster.setWindowName( LevelEditor.getWindowName());
        }
    }
}
