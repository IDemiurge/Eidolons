package main.level_editor;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.level_editor.functions.model.LE_DataModel;
import main.level_editor.gui.palette.PaletteHolder;
import main.level_editor.metadata.settings.LE_OptionsMaster;
import main.level_editor.sim.LE_GameSim;
import main.level_editor.sim.LE_MetaMaster;
import main.level_editor.struct.boss.BossDungeon;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.launch.TypeBuilder;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;

public class LevelEditor {
    public static Window.WindowStyle windowStyle;
    private static Floor current;
    private static boolean campaignMode;
    private static Campaign campaign;
    private static BossDungeon dungeon;
    private static boolean saveTest = true;
  public  static final boolean TEST_MODE = true;

    public static void main(String[] args) {
        CoreEngine.setLevelEditor(true);
        TypeBuilder.typeBuildOverride.addAll(PaletteHolder.tabTypes);
        Assets.setON(false);
//        DC_Engine.systemInit(false);
        new EditorApp(args).start();
        LE_OptionsMaster.init();

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

    public static void newFloorSelected(String name) {
        String path = !campaignMode ? name : getRootPath() + name;
        if (campaignMode) {
//            dungeon.getFloorPath(name);
        } else {
        }
        LE_MetaMaster meta = new LE_MetaMaster(path);
        initFloor(meta);
    }

    private static String getRootPath() {
        return new StrPathBuilder().build(PathFinder.getDungeonLevelFolder(),
                campaign.getName(), dungeon.getName());
    }

    public static void floorSelected(Floor floor) {
        current = floor;
        Eidolons.game = floor.getGame();
        DC_Game.game = floor.getGame();
        SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.CLICK_ACTIVATE);
        main.system.auxiliary.log.LogMaster.log(1, "floorSelected: " + floor.getName());
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.EDITOR, floor));
    }

    public static Integer getId(BattleFieldObject bfObj) {
        return getCurrent().getGame().getId(bfObj);
    }

    public static LE_DataModel getModel() {
        return getCurrent().getManager().getModelManager().getModel();
    }

    public static Window.WindowStyle getWindowStyle() {
        if (windowStyle == null) {
            windowStyle = new Window.WindowStyle(StyleHolder.getHqLabelStyle(
                    GdxMaster.adjustFontSize(20)).font
                    , StyleHolder.getDefaultLabelStyle().fontColor,
                    new NinePatchDrawable(NinePatchFactory.getLightDecorPanelFilledDrawable()));
        }
        return windowStyle;
    }

    public static Floor getCurrent() {
        return current;
    }


    public static void welcome(
            String toOpen) {
        //welcome screen?..
//        editorSettings = FileManager.readFile(getSettingsPath());
//        toOpen = getOpenDefault();
        if (toOpen == null) {
            toOpen = promptOpen();
        }
        LE_MetaMaster meta;
        if (toOpen.contains("campaigns")) {
            campaignMode = true;
            ObjType type = DataManager.getType(toOpen, MACRO_OBJ_TYPES.CAMPAIGN);
            campaign = new Campaign(type);
            dungeon = campaign.getCurrentDungeon();
            meta = new LE_MetaMaster(campaign);
            //use data soruce / update() paradigm?     VS cached LE_Screens, eh?
        } else {
            meta = new LE_MetaMaster(toOpen);
        }
        initFloor(meta);
    }

    private static String promptOpen() {
        GuiEventManager.trigger(GuiEventType.LE_CHOOSE_FILE);
        return (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.SELECTION);
    }
}
