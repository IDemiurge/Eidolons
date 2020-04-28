package main.level_editor.backend.functions.io;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.dungeons.model.assembly.ModuleGridMapper;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.utils.FileChooserX;
import eidolons.libgdx.utils.GdxDialogMaster;
import eidolons.system.text.NameMaster;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.LE_Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;

import java.util.Calendar;
import java.util.Set;
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
        String path = getBackupPath(getFloorWrapper());
        String contents = FileManager.readFile(getDefaultSavePath(getFloorWrapper()));
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
        return true;
    }

    private boolean isAutosave() {
        return true;
    }


    public void saveFloor() {
        doPresave();
        String path = getDefaultSavePathFull(getFloorWrapper(), "crawl");
        saveAs(path);
        setDirty(false);
//        try {
//            saveModulesSeparately();
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }
    }

    private void doPresave() {
        if (!isResizingSupported()){
            return ;
        }
        boolean changed = false;
        for (Module module : getModuleHandler().getModules()) {
            ModuleData data = (ModuleData) new ModuleData(module).setData(module.getData().getData());
            changed |= recalcModuleParams(data, false);
        }
        if (changed) {
            getModuleHandler().resetBorders();
            getModuleHandler().resetBufferVoid();
            getStructureHandler().resetWalls(getDungeonLevel());
        }
        if (getModuleHandler().getModuleGrid() != null) {
            ModuleGridMapper.calculateTotalSquareSize(getModuleHandler().getModuleGrid());
            if (ModuleGridMapper.width > 0) {
                getFloor().getWrapper().setWidth(ModuleGridMapper.width);
                getFloor().getWrapper().setHeight(ModuleGridMapper.height);
            }
        }
    }

    private boolean isResizingSupported() {
        return false;
    }

    private boolean recalcModuleParams(ModuleData data, boolean standalone) {
        Module structure = data.getStructure();
        int w = structure.getEffectiveWidth(false);
        int h = structure.getEffectiveHeight(false);
        int originalH = h;
        int originalW = w;
//        structure.getEffectiveHeight()
        Set<Coordinates> coordinatesSet = structure.initCoordinateSet(false);
        Set<Coordinates> coordinatesSetToSearch = structure.initCoordinateSet(true);
        int maxX = CoordinatesMaster.getMaxX(coordinatesSet);
        int maxY = CoordinatesMaster.getMaxY(coordinatesSet);
        int minX = CoordinatesMaster.getMinX(coordinatesSet);
        int minY = CoordinatesMaster.getMinY(coordinatesSet);

        for (Coordinates coordinates : coordinatesSetToSearch) {
            Set<BattleFieldObject> objects = DC_Game.game.getObjectsOnCoordinateNoOverlaying(coordinates);
            if (!objects.isEmpty()) {
                for (BattleFieldObject object : objects) {
                    if (object.isModuleBorder()) {
                        continue;
                    }
                    if (object.getCoordinates().x > maxX) {
                        maxX = object.getCoordinates().x;
                    }
                    if (object.getCoordinates().y > maxY) {
                        maxY = object.getCoordinates().y;
                    }
                    if (object.getCoordinates().x < minX) {
                        minX = object.getCoordinates().x;
                    }
                    if (object.getCoordinates().y < minY) {
                        minY = object.getCoordinates().y;
                    }
                }
            }
        }
        w = maxX - minX + 1;
        h = maxY - minY + 1;
        boolean changed = false;
        if (w != originalW || h != originalH) {
            changed = true;
        }
        if (changed) {
            data.setValue(LevelStructure.MODULE_VALUE.width, w);
            data.setValue(LevelStructure.MODULE_VALUE.origin, Coordinates.get(minX, minY));
            data.setValue(LevelStructure.MODULE_VALUE.height, h);
            data.apply();
        }

        return changed;
    }

    public void saveModulesSeparately() {
        for (Module module : getModuleHandler().getModules()) {
            String contents = getXmlMaster().toXml(getFloorWrapper(), module);
            String path = getStandalonePath(module);
            FileManager.write(contents, path);
            EUtils.showInfoText("Saved " +
                    module.getName() +
                    " as " + PathUtils.getLastPathSegment(path));
        }
    }

    private String getStandalonePath(Module module) {
        return PathFinder.getModuleTemplatesPath() + " " + module.getName() + ".xml";
    }

    public void saveVersion() {
        String path = getDefaultSavePath(getFloorWrapper());
        String name = FileManager.getFileNameAndFormat(path);
        String newName = NameMaster.getUniqueVersionedFileName(name, path);
        saveAs(PathFinder.getDungeonLevelFolder() +PathUtils.cropLastPathSegment(path) + "/" + newName);
    }
    public void saveAs( ) {
        String path = GdxDialogMaster.inputText("Enter save path", getDefaultSavePath(getFloorWrapper()));
        if (path == null) {
            return;
        }
        saveAs(PathFinder.getDungeonLevelFolder() +path);

    }
    public void saveAs(String path) {
        String contents = getXmlMaster().toXml(getFloorWrapper());
        FileManager.write(contents, path);
        EUtils.showInfoText("Saved as " + PathUtils.getLastPathSegment(path));
    }

    private String getBackupPath(Location location) {
        return getDefaultSavePathFull(location, "backup");
    }

    public String getDefaultSavePath(Location location, String prefix) {
        String value = location.getData().getValue(LevelStructure.FLOOR_VALUES.filepath);
        if (value.isEmpty()) {
            return prefix + "/" + location.getName() + ".xml";
        }
        return value;
    }
        public String getDefaultSavePathFull(Location location, String prefix ) {
            return PathFinder.getDungeonLevelFolder() +getDefaultSavePath(location, prefix);
    }

    public String getDefaultSavePath(Location floor) {
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

    public void activateFloorTab(LE_Floor floor) {

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
            GdxMaster.setWindowName(LevelEditor.getWindowName());
        }
    }

}
