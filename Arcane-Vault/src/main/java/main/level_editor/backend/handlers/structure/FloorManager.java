package main.level_editor.backend.handlers.structure;

import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.utils.FileChooserX;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.data.filesys.PathFinder;
import main.level_editor.backend.sim.LE_GameSim;
import main.level_editor.backend.sim.LE_MetaMaster;
import main.level_editor.backend.struct.boss.BossDungeon;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;

public class FloorManager {
    public static Floor current;
    private static boolean saveTest = true;
    public static Campaign campaign;
    public static BossDungeon dungeon;
    /*
    TEMPLATE
    zones and blocks obviously
    with some bits being VAR
     */

    public static void addFloor(){
        String templatePath = FileChooserX.chooseFile(PathFinder.getDungeonLevelFolder(), "xml",
                LE_Screen.getInstance().getGuiStage());

        //from modules too
        if (StringMaster.isEmpty(templatePath)) {
            templatePath = getDefaultFloorTemplate();
        }

        newFloorSelected(templatePath, false);
    }


    public static void newFloorSelected(String name, boolean campaignMode ) {
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

        String name = meta.getMetaDataManager().getMissionPath();
        name = StringMaster.cropFormat(
                PathUtils.getLastPathSegment(name));
        Floor floor = new Floor(name, game);
        floorSelected(floor);
        WaitMaster.WAIT(500);
        game.initAndStart();

        meta.gameStarted();
        if (saveTest){
            floor.getManager().getDataHandler().saveFloor();
        }
    }

    public static void floorSelected(Floor floor) {
        current = floor;
        Eidolons.game = floor.getGame();
        DC_Game.game = floor.getGame();
        SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.CLICK_ACTIVATE);
        main.system.auxiliary.log.LogMaster.log(1, "floorSelected: " + floor.getName());
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.EDITOR, floor));
    }

    private static String getDefaultFloorTemplate() {
        return null;
    }

    private static String getRootPath() {
        return new StrPathBuilder().build(PathFinder.getDungeonLevelFolder(),
                campaign.getName(), dungeon.getName());
    }


}
