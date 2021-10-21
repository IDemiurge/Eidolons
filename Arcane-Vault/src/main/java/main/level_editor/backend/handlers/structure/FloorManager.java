package main.level_editor.backend.handlers.structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.content.consts.VisualEnums;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import libgdx.gui.utils.FileChooserX;
import eidolons.system.libgdx.datasource.ScreenData;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.io.LE_DataHandler;
import main.level_editor.backend.sim.LE_GameSim;
import main.level_editor.backend.sim.LE_MetaMaster;
import main.level_editor.backend.struct.boss.BossDungeon;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.LE_Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.sound.AudioEnums;
import main.system.sound.SoundMaster;

import java.util.ArrayList;
import java.util.List;

public class FloorManager {
    public static LE_Floor current;
    public static Campaign campaign;
    public static BossDungeon dungeon;
    private static final List<LE_Floor> floors = new ArrayList<>();
    private static LE_Floor cached;
    /*
    TEMPLATE
    zones and blocks obviously
    with some bits being VAR
     */

    public static void newFloor() {
        loadFloor(PathFinder.getDungeonLevelFolder() + LE_DataHandler.PREFIX_TEMPLATE);
    }

    public static void addFloor() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            newFloor();
        } else
            loadFloor(PathFinder.getDungeonLevelFolder() + LE_DataHandler.PREFIX_CRAWL);
    }

    public static void loadFloor(String path) {
        String templatePath = FileChooserX.chooseFile(path, "xml",
                LE_Screen.getInstance().getGuiStage());
        //from modules too
        if (StringMaster.isEmpty(templatePath)) {
            templatePath = LevelEditor.readLast();
        }

        newFloorSelected(templatePath, false);

        LevelEditor.saveLastLvlPath(templatePath);
    }

    public static void cloneFloor() {
        String path = current.getWrapper().getData().getValue(LevelStructure.FLOOR_VALUES.filepath);
        newFloorSelected(path, false);
        current.getManager().getDataHandler().saveVersion();
        //        current.getWrapper().getData().setValue( "");
    }

    public static void newFloorSelected(String name, boolean campaignMode) {
        String path = !campaignMode ? name : getRootPath() + name;
        if (campaignMode) {
            //            dungeon.getFloorPath(name);
        } else {
        }
        LE_MetaMaster meta = new LE_MetaMaster(path);
        FloorManager.initFloor(meta);
    }

    public static void initFloor(LE_MetaMaster meta) {
        LE_GameSim game = meta.init();

        String name = meta.getMetaDataManager().getSoloDungeonPath();
        name = StringMaster.cropFormat(
                PathUtils.getLastPathSegment(name));

        ObjType type = DataManager.getType(name, DC_TYPE.FLOORS);
        if (type == null) {
            type = new ObjType(name, DC_TYPE.FLOORS);// use template?
            //will we save this type with main xml?
        }
        LE_Floor floor = new LE_Floor(type, game);
        floorSelected(floor);
        game.initAndStart();
        floor.getManager().load();
        meta.gameStarted();
        floor.getManager().afterLoaded();

        floors.add(floor);
    }

    public static void floorSelected(LE_Floor floor) {
        current = floor;
        Core.game = floor.getGame();
        DC_Game.game = floor.getGame();
        EidolonsGame.lvlPath = FileManager.formatPath(
                DC_Game. game.getMetaMaster().getMetaDataManager().getSoloDungeonPath(),true,true).
                replace(PathFinder.getDungeonLevelFolder().toLowerCase(), "");
        SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLICK_ACTIVATE);
        main.system.auxiliary.log.LogMaster.log(1, "floorSelected: " + floor.getName());
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(VisualEnums.SCREEN_TYPE.EDITOR, floor));
        GuiEventManager.trigger(GuiEventType.LE_FLOORS_TABS, floors);
    }


    private static String getDefaultFloorTemplate() {
        return null;
    }

    private static String getRootPath() {
        return new StrPathBuilder().build(PathFinder.getDungeonLevelFolder(),
                campaign.getName(), dungeon.getName());
    }

    public static List<LE_Floor> getFloors() {
        return floors;
    }

    public static void removed(LE_Floor floor) {
        floors.remove(floor);
        LE_Screen.getCache().remove(floor);
    }

    public static void selectNextFloor() {
        int i = floors.indexOf(LevelEditor.getCurrent());
        i++;
        if (i >= floors.size()) {
            i = 0;
        }
        floorSelected(floors.get(i));
    }

    public static void selectPreviousFloor() {
        int i = floors.indexOf(LevelEditor.getCurrent());
        i--;
        if (i < 0) {
            i = floors.size() - 1;
        }
        floorSelected(floors.get(i));
    }

    public static void setTempFloor(LE_Floor floor) {
        cached = current;
        FloorManager.current = floor;
    }

    public static void resetTempFloor() {
        current = cached;
    }
}
