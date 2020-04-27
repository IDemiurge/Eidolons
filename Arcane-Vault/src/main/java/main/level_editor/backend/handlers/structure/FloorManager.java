package main.level_editor.backend.handlers.structure;

import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.utils.FileChooserX;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
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
import main.system.sound.SoundMaster;

public class FloorManager {
    public static LE_Floor current;
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

        String name = meta.getMetaDataManager().getSoloDungeonPath();
        name = StringMaster.cropFormat(
                PathUtils.getLastPathSegment(name));

        ObjType type = DataManager.getType(name, DC_TYPE.FLOORS);
        if (type == null) {
            type = new ObjType(name,DC_TYPE.FLOORS );// use template?
            //will we save this type with main xml?
        }
        LE_Floor floor = new LE_Floor(type, game);
        floorSelected(floor);
        game.initAndStart();
        floor.getManager().load();
        meta.gameStarted();
        floor.getManager().afterLoaded();
    }

    public static void floorSelected(LE_Floor floor) {
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
